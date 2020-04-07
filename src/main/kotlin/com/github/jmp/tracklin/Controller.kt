package com.github.jmp.tracklin

import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import java.io.FileWriter
import java.io.IOException
import java.util.Date
import java.util.concurrent.TimeUnit

private const val SELECTION_DELAY = 50L
private const val IDLE_DELAY = 1000L * 60L * 5L

/**
 * Controller for the JavaFX components.
 *
 * Most of the application logic happens here.
 */
@Suppress("TooManyFunctions")
class Controller {
    @FXML
    lateinit var startButton: Button

    @FXML
    lateinit var stopButton: Button

    @FXML
    lateinit var exportButton: Button

    @FXML
    lateinit var hoursTable: TableView<Hours>

    @FXML
    lateinit var taskColumn: TableColumn<Hours, String>

    @FXML
    fun initialize() {
        // Create and set a context menu for the hours table
        val removeItem = MenuItem(REMOVE_ITEM_TEXT).apply {
            onAction = EventHandler { removeHours(selectedHours) }
        }
        val contextMenu = ContextMenu(removeItem).apply {
            onShown = EventHandler { removeItem.isDisable = selectedHours.isEmpty() }
        }
        hoursTable.contextMenu = contextMenu

        // Set the multiselection mode for the hours table
        hoursTable.selectionModel.selectionMode = SelectionMode.MULTIPLE
    }

    private var isTracking = false
    private val filePicker = FilePicker()
    private val allHours
        get() = hoursTable.items
    private val selectedHours
        get() = hoursTable.selectionModel.selectedItems

    private var isIdlePromptOpen = false

    private val idleTracker = IdleTracker(
        {
            if (isTracking && !isIdlePromptOpen) {
                isIdlePromptOpen = true
                GlobalScope.launch(Dispatchers.JavaFx) {
                    val alert = Alert(Alert.AlertType.CONFIRMATION)
                    alert.title = "You're back!"
                    alert.headerText =
                        "You were idle for ${TimeUnit.MILLISECONDS.toMinutes(it)} minutes.\n" +
                        "The current task is \"${lastTaskName()}\"."
                    alert.contentText = "How would you like to mark the spent time?"
                    val newTaskButton = ButtonType("Different task")
                    val stopButton = ButtonType("End current at ${TimeUnit.MILLISECONDS.toMinutes(it)} mins ago")
                    val keepTaskButton = ButtonType("Keep current task")
                    alert.buttonTypes.setAll(newTaskButton, stopButton, keepTaskButton)
                    val result = alert.showAndWait()
                    if (result.get() == newTaskButton) {
                        markIdleTimeAsNewTask(it)
                    } else if (result.get() == stopButton) {
                        markIdleTimeAsEmpty(it)
                    }
                    isIdlePromptOpen = false
                }
            }
        },
        IDLE_DELAY
    )

    private fun markIdleTimeAsNewTask(idleTime: Long) {
        val lastTaskEndTime = Date(System.currentTimeMillis() - idleTime)
        val hours = allHours
        hours.last().endTime = getTimeAsString(lastTaskEndTime)
        isTracking = false
        startTracking()
        hours.last().startTime = getTimeAsString(lastTaskEndTime)
        hours[hours.lastIndex - 1].task = NEW_TASK_NAME
        editTask(hours.lastIndex - 1)
    }

    private fun markIdleTimeAsEmpty(idleTime: Long) {
        val lastTaskEndTime = Date(System.currentTimeMillis() - idleTime)
        onStopClick()
        updateLastTaskEndTime(lastTaskEndTime)
    }

    /**
     * Handle start button click event.
     */
    fun onStartClick() {
        startTracking()
        editLastTask()
    }

    /**
     * Start tracking a new task.
     *
     * This inserts a new row to the hours table with the current time and a task name.
     * The task name is by default the same as the previous task name. If no previous
     * tasks exist, then it will be set to a dummy name. The end time of the new task
     * will be empty by default.
     */
    private fun startTracking() {
        updateLastTaskEndTime()
        allHours.add(
            Hours(
                startTime = getTimeAsString(),
                task = lastTaskName()
            )
        )
        startButton.text = SWITCH_BUTTON_TEXT
        stopButton.isDisable = false
        isTracking = true
    }

    /**
     * Stop tracking the current task.
     *
     * This will update the end time of the current task to the current time and stop tracking.
     */
    private fun stopTracking() {
        updateLastTaskEndTime()
        isTracking = false
    }

    /**
     * Returns the name of the last task.
     * @return the last task name as a String.
     */
    private fun lastTaskName(): String =
        if (allHours.isNotEmpty()) {
            allHours.last().task
        } else NEW_TASK_NAME

    /**
     * Sets the end time of the last task row.
     */
    private fun updateLastTaskEndTime(date: Date = Date()) {
        val hours = allHours
        if (hours.isNotEmpty() && isTracking) {
            val lastHours = hours.last()
            lastHours.endTime = getTimeAsString(date)
            hours[hours.lastIndex] = lastHours
        }
    }

    /**
     * Focus the second to last item and and activate its task cell for editing.
     */
    private fun editTask(index: Int) = with(hoursTable) {
        selectionModel.clearSelection()
        selectionModel.select(index)
        selectionModel.focus(index)
        GlobalScope.launch(Dispatchers.JavaFx) {
            delay(SELECTION_DELAY)
            edit(index, taskColumn)
        }
    }

    /**
     * Focus the last item and and activate its task cell for editing.
     */
    private fun editLastTask() = with(hoursTable) {
        selectionModel.clearSelection()
        val index = items.lastIndex
        selectionModel.select(index)
        selectionModel.focus(index)
        GlobalScope.launch(Dispatchers.JavaFx) {
            delay(SELECTION_DELAY)
            edit(index, taskColumn)
        }
    }

    /**
     * Stop button click handler.
     */
    fun onStopClick() {
        stopTracking()
        resetButtonStates()
    }

    private val ActionEvent.window
        get() = (this.target as Node).scene.window!!

    /**
     * Export button click handler.
     * @param event the event triggered when the button was clicked
     */
    fun onExportClick(event: ActionEvent) = try {
        filePicker.pickFile(event.window)?.let {
            export(
                FileWriter(it),
                allHours
            )
        }
    } catch (e: IOException) {
        e.printStackTrace() // Write failed
    }

    /**
     * Handler for start time commit after editing.
     * @param event event for editing the table cell
     */
    fun onStartTimeEditCommit(event: TableColumn.CellEditEvent<Hours, String?>) {
        event.rowValue.startTime = event.newValue ?: ""
    }

    /**
     * Handler for end time commit after editing.
     * @param event event for editing the table cell
     */
    fun onEndTimeEditCommit(event: TableColumn.CellEditEvent<Hours, String?>) {
        event.rowValue.endTime = event.newValue ?: ""
    }

    /**
     * Handler for task name commit after editing.
     * @param event event for editing the table cell
     */
    fun onTaskEditCommit(event: TableColumn.CellEditEvent<Hours, String?>) {
        event.rowValue.task = event.newValue ?: ""
    }

    /**
     * Key press handler.
     * @param keyEvent event for the key press
     */
    fun onKeyPressed(keyEvent: KeyEvent) {
        if (keyEvent.code == KeyCode.DELETE) {
            removeHours(selectedHours)
            keyEvent.consume()
        }
    }

    /**
     * Removes the given hours.
     * @param items hours to remove
     */
    private fun removeHours(items: ObservableList<Hours>) {
        val allItems = allHours
        val isLastDeleted = items.isNotEmpty() && items.contains(allItems.last())
        allItems.removeAll(items)
        if (isLastDeleted || allItems.isEmpty()) {
            resetButtonStates()
            isTracking = false
        }
    }

    /**
     * Returns buttons (texts and disabled status) to their initial states.
     */
    private fun resetButtonStates() {
        startButton.text = START_BUTTON_TEXT
        startButton.isDisable = false
        startButton.requestFocus()
        stopButton.isDisable = true
        exportButton.isDisable = allHours.isEmpty()
    }

    companion object {
        private const val NEW_TASK_NAME = "New task"
        private const val SWITCH_BUTTON_TEXT = "Switch"
        private const val START_BUTTON_TEXT = "Start"
        private const val REMOVE_ITEM_TEXT = "Remove"
    }
}
