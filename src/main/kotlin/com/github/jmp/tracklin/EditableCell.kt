package com.github.jmp.tracklin

import javafx.event.Event
import javafx.event.EventHandler
import javafx.scene.control.*
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
        textField.requestFocus()
    }

    override fun commitEdit(item: T?) {
        if (!isEditing && item != getItem()) {
            tableView?.let {
                Event.fireEvent(
                    tableColumn,
                    TableColumn.CellEditEvent(
                        it,
                        TablePosition(it, index, tableColumn),
                        TableColumn.editCommitEvent(),
                        item
                    )
                )
            }
        }
        super.commitEdit(item)
        contentDisplay = ContentDisplay.TEXT_ONLY
    }

    init {
        itemProperty().addListener { _, _, newItem: T? ->
            text = newItem?.let { converter.toString(newItem) }
        }
        graphic = textField
        contentDisplay = ContentDisplay.TEXT_ONLY
        textField.onAction = EventHandler {
            commitEdit(converter.fromString(textField.text))
        }
        textField.focusedProperty()
            .addListener { _, _, isNowFocused: Boolean ->
                if (!isNowFocused) {
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