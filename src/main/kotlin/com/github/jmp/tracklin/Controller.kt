package com.github.jmp.tracklin

import javafx.application.Platform
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import java.io.FileWriter
import java.io.IOException

/**
 * Controller for the JavaFX components.
 *
 * Most of the application logic happens here.
 */
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
        createContextMenu()
        initializeSelectionModel()
    }

    private val filePicker = FilePicker()

    /**
     * Create and set a context menu for the hours table.
     */
    private fun createContextMenu() {
        val selectedItems = { hoursTable.selectionModel.selectedItems }
        val removeItem = MenuItem("Remove").apply {
            onAction = EventHandler {
                removeHours(selectedItems())
            }
        }
        val contextMenu = ContextMenu(removeItem).apply {
            onShown = EventHandler {
                removeItem.isDisable = selectedItems().isEmpty()
            }
        }
        hoursTable.contextMenu = contextMenu
    }

    /**
     * Set the multiselection mode for the hours table.
     */
    private fun initializeSelectionModel() {
        hoursTable.selectionModel.selectionMode = SelectionMode.MULTIPLE
    }

    /**
     * Handle start button click event.
     */
    fun onStartClick() {
        updateLastTaskEndTime()
        startTask()
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
    private fun startTask() {
        val startTime = getTimeAsString()
        hoursTable.items.add(
            Hours(
                startTime,
                "",
                lastTaskName()
            )
        )
        startButton.text = SWITCH_BUTTON_TEXT
        stopButton.isDisable = false
        isTrackingStarted = true
    }

    /**
     * Stop tracking the current task.
     *
     * This will update the end time of the current task to the current time and stop tracking.
     */
    private fun stopTask() {
        updateLastTaskEndTime()
        isTrackingStarted = false
    }

    /**
     * Returns the name of the last task.
     * @return the last task name as a String.
     */
    private fun lastTaskName(): String =
        if (hoursTable.items.isNotEmpty()) {
            hoursTable.items.last().task
        } else NEW_TASK_NAME

    /**
     * Sets the end time of the last task row to the current time.
     */
    private fun updateLastTaskEndTime() {
        val data: MutableList<Hours> = hoursTable.items
        if (data.isNotEmpty() && isTrackingStarted) {
            val lastIndex = data.size - 1
            val lastHours = data[lastIndex]
            lastHours.endTime = getTimeAsString()
            data[lastIndex] = lastHours
        }
    }

    /**
     * Focus the last item and and activate its task cell for editing.
     */
    private fun editLastTask() = Thread(Runnable {
        hoursTable.selectionModel.clearSelection()
        try {
            Thread.sleep(50L)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        Platform.runLater {
            with(hoursTable) {
                val lastIndex = items.lastIndex
                selectionModel.select(lastIndex)
                selectionModel.focus(lastIndex)
                edit(lastIndex, taskColumn)
            }
        }
    }).start()

    /**
     * Stop button click handler.
     */
    fun onStopClick() {
        stopTask()
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
                hoursTable.items
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
        println("onStartTimeEditCommit: ${event.newValue}")
        event.rowValue.startTime = event.newValue!!
    }

    /**
     * Handler for end time commit after editing.
     * @param event event for editing the table cell
     */
    fun onEndTimeEditCommit(event: TableColumn.CellEditEvent<Hours, String?>) {
        println("onEndTimeEditCommit: ${event.newValue}")
        event.rowValue.endTime = event.newValue!!
    }

    /**
     * Handler for task name commit after editing.
     * @param event event for editing the table cell
     */
    fun onTaskEditCommit(event: TableColumn.CellEditEvent<Hours, String?>) {
        println("onTaskEditCommit: ${event.newValue}")
        event.newValue?.let {
            event.rowValue.task = it
        }
    }

    /**
     * Key press handler.
     * @param keyEvent event for the key press
     */
    fun onKeyPressed(keyEvent: KeyEvent) {
        val selectedHours = hoursTable.selectionModel.selectedItems
        val isDeletePressed = keyEvent.code == KeyCode.DELETE
        if (selectedHours.isNotEmpty() && isDeletePressed) {
            removeHours(selectedHours)
        }
    }

    /**
     * Removes the given hours.
     * @param items hours to remove
     */
    private fun removeHours(items: ObservableList<Hours>) {
        val allItems = hoursTable.items
        println("items before:")
        allItems.forEach(::println)
        println("---")
        val isLastDeleted = items.isNotEmpty() && items.contains(allItems.last())
        allItems.removeAll(items)
        println("items after:")
        allItems.forEach(::println)
        println("---")
        if (isLastDeleted || allItems.isEmpty()) {
            resetButtonStates()
            isTrackingStarted = false
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
        exportButton.isDisable = hoursTable.items.isEmpty()
    }

    companion object {
        private const val NEW_TASK_NAME = "New task"
        private const val SWITCH_BUTTON_TEXT = "Switch"
        private const val START_BUTTON_TEXT = "Start"
        private var isTrackingStarted = false
    }
}