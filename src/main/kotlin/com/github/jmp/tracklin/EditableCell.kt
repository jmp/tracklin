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
        contentDisplay = ContentDisplay.GRAPHIC_ONLY
        with(textField) {
            text = converter.toString(item)
            selectAll()
            requestFocus()
        }
    }

    override fun commitEdit(newValue: T?) {
        if (!isEditing && tableView != null) {
            Event.fireEvent(
                tableColumn,
                TableColumn.CellEditEvent(
                    tableView,
                    TablePosition(tableView, index, tableColumn),
                    TableColumn.editCommitEvent(),
                    newValue
                )
            )
        }
        super.commitEdit(newValue)
        contentDisplay = ContentDisplay.TEXT_ONLY
    }

    override fun cancelEdit() {
        super.cancelEdit()
        tableView.refresh()
        contentDisplay = ContentDisplay.TEXT_ONLY
    }

    init {
        itemProperty().addListener { _, _, newItem: T? ->
            text = converter.toString(newItem)
        }
        contentDisplay = ContentDisplay.TEXT_ONLY
        with(textField) {
            graphic = this
            onAction = EventHandler { commitEdit(converter.fromString(text)) }
            focusedProperty().addListener { _, _, isNowFocused: Boolean ->
                if (!isNowFocused) {
                    commitEdit(converter.fromString(text))
                }
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
