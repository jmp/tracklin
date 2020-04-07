package com.github.jmp.tracklin

import javafx.application.Application
import javafx.stage.Stage
import kotlin.system.exitProcess

private const val TITLE = "Tracklin"
private const val MAIN_FXML = "main.fxml"
private const val STYLESHEET = "style.css"

class Tracklin : Application() {
    override fun start(stage: Stage) {
        stage.title = TITLE
        stage.scene = createScene(
            javaClass.getResource(MAIN_FXML),
            javaClass.getResource(STYLESHEET)
        )
        stage.show()
    }

    override fun stop() {
        super.stop()
        exitProcess(0)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Tracklin::class.java)
        }
    }
}
