package com.github.jmp.tracklin

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.verify
import javafx.stage.FileChooser
import javafx.stage.Window
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class FilePickerTest {
    @Test
    fun smoke() {
        FilePicker("Test", listOf())
    }

    @Test
    fun `pickFile opens a file chooser with the given arguments`() {
        val chosenFile = File("Test File")

        mockkConstructor(FileChooser::class)
        every {
            anyConstructed<FileChooser>().showSaveDialog(any())
        } returns chosenFile
        every {
            anyConstructed<FileChooser>().extensionFilters.addAll(
                any<Collection<FileChooser.ExtensionFilter>>()
            )
        } returns true

        val extensionFilters = listOf(
            FileChooser.ExtensionFilter("Foo", "*.foo"),
            FileChooser.ExtensionFilter("Bar", "*.bar")
        )
        val title = "Test Title"
        val window = mockk<Window>()
        val filePicker = FilePicker(title, extensionFilters)

        assertEquals(chosenFile, filePicker.pickFile(window))

        verify { anyConstructed<FileChooser>().title = title }
        verify { anyConstructed<FileChooser>().extensionFilters.addAll(extensionFilters) }
        verify { anyConstructed<FileChooser>().showSaveDialog(window) }
    }
}
