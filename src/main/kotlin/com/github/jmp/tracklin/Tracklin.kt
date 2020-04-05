package com.github.jmp.tracklin

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

private const val TITLE = "Tracklin"
private const val MAIN_FXML = "main.fxml"
private const val STYLESHEET = "style.css"

class Tracklin : Application() {
    override fun start(stage: Stage) {
        stage.title = TITLE
        stage.scene = createScene()
        stage.show()
    }

    private fun createScene(): Scene {
        val scene = Scene(
            FXMLLoader.load(
                javaClass.getResource(MAIN_FXML)
            )
        )
        scene.stylesheets.add(
            javaClass
                .getResource(STYLESHEET)
                .toExternalForm()
        )
        return scene
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Tracklin::class.java)
        }
    }
}