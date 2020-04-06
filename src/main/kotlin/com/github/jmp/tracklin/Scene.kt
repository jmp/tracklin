package com.github.jmp.tracklin

import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import java.net.URL

fun createScene(fxmlUrl: URL, stylesheetUrl: URL): Scene {
    val scene = Scene(FXMLLoader.load(fxmlUrl))
    return scene.apply {
        stylesheets.add(stylesheetUrl.toExternalForm())
    }
}
