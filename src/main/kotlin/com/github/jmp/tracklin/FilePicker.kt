package com.github.jmp.tracklin

import javafx.stage.FileChooser
import javafx.stage.FileChooser.ExtensionFilter
import javafx.stage.Window
import java.io.File

class FilePicker(
    title: String,
    extensionFilters: List<ExtensionFilter>
) {
    private val fileChooser = FileChooser().apply {
        this.title = title
        this.extensionFilters.addAll(extensionFilters)
    }

    private var previousDirectory = File(System.getProperty("user.home") ?: "")

    fun pickFile(ownerWindow: Window): File? = with(fileChooser) {
        initialDirectory = previousDirectory
        showSaveDialog(ownerWindow)?.apply {
            previousDirectory = parentFile ?: File("")
        }
    }
}
