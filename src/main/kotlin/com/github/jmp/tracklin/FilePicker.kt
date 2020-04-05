package com.github.jmp.tracklin

import javafx.stage.FileChooser
import javafx.stage.FileChooser.ExtensionFilter
import javafx.stage.Window
import java.io.File

class FilePicker {
    private val fileChooser = FileChooser().apply {
        title = "Export"
        extensionFilters.addAll(listOf(
            ExtensionFilter("CSV Files (*.csv)", "*.csv"),
            ExtensionFilter("Text Documents (*.txt)", "*.txt"),
            ExtensionFilter("All Files (*.*)", "*.*")
        ))
    }

    private var previousDirectory = File(System.getProperty("user.home") ?: "")

    fun pickFile(ownerWindow: Window): File? = with(fileChooser) {
        initialDirectory = previousDirectory
        showSaveDialog(ownerWindow)?.apply {
            previousDirectory = parentFile ?: File("")
        }
    }
}