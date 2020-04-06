package com.github.jmp.tracklin

import javafx.event.Event
import javafx.event.EventHandler
import javafx.scene.control.ContentDisplay
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.TablePosition
import javafx.scene.control.TextField
import javafx.util.Callback
import javafx.util.StringConverter
import javafx.util.converter.DefaultStringConverter

class EditableCell<S, T>(
    private val converter: StringConverter<T>
) : TableCell<S, T>() {
    private val textField = TextField() // Text field for editing

    override fun startEdit() {
        super.startEdit()
        textField.text = converter.toString(item)
        contentDisplay = ContentDisplay.GRAPHIC_ONLY
        textField.selectAll()
        textField.requestFocus()
    }

    override fun commitEdit(item: T?) {
        if (!isEditing && item != getItem()) {
            tableView?.let {
                val column = tableColumn
                val event = TableColumn.CellEditEvent(
                    it,
                    TablePosition(it, index, column),
                    TableColumn.editCommitEvent(),
                    item
                )
                Event.fireEvent(column, event)
            }
        }
        super.commitEdit(item)
        contentDisplay = ContentDisplay.TEXT_ONLY
    }

    override fun cancelEdit() {
        super.cancelEdit()
        tableView.refresh()
        contentDisplay = ContentDisplay.TEXT_ONLY
    }

    init {
        itemProperty().addListener { _, _, newItem: T? ->
            text = newItem?.let {
                converter.toString(newItem)
            }
        }
        graphic = textField
        contentDisplay = ContentDisplay.TEXT_ONLY
        textField.onAction = EventHandler {
            commitEdit(converter.fromString(textField.text))
        }
        textField.focusedProperty()
            .addListener { _, wasFocused: Boolean, isNowFocused: Boolean ->
                if (!isNowFocused || wasFocused) {
                    commitEdit(converter.fromString(textField.text))
                }
            }
    }

    companion object {
        @JvmStatic
        fun <S> forTableColumn(): Callback<TableColumn<S, String>, TableCell<S, String>> =
            forTableColumn(DefaultStringConverter())

        @JvmStatic
        private fun <S, T> forTableColumn(converter: StringConverter<T>): Callback<TableColumn<S, T>, TableCell<S, T>> =
            Callback { EditableCell(converter) }
    }
}
