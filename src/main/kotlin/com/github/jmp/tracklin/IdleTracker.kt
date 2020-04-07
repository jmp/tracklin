package com.github.jmp.tracklin

import lc.kra.system.keyboard.GlobalKeyboardHook
import lc.kra.system.keyboard.event.GlobalKeyEvent
import lc.kra.system.keyboard.event.GlobalKeyListener
import lc.kra.system.mouse.GlobalMouseHook
import lc.kra.system.mouse.event.GlobalMouseEvent
import lc.kra.system.mouse.event.GlobalMouseListener

class IdleTracker(
    private val action: (idleTime: Long) -> Unit,
    private val idleTimeThreshold: Long
) {
    private val keyboardHook = tryOrNull { GlobalKeyboardHook() }
    private val mouseHook = tryOrNull { GlobalMouseHook() }
    private var previousActivityTime = System.currentTimeMillis()

    init {
        keyboardHook?.addKeyListener(object : GlobalKeyListener {
            override fun keyPressed(e: GlobalKeyEvent?) = handleIdleTime()
            override fun keyReleased(e: GlobalKeyEvent?) = handleIdleTime()
        })
        mouseHook?.addMouseListener(object : GlobalMouseListener {
            override fun mouseReleased(e: GlobalMouseEvent?) = handleIdleTime()
            override fun mouseWheel(e: GlobalMouseEvent?) = handleIdleTime()
            override fun mouseMoved(e: GlobalMouseEvent?) = handleIdleTime()
            override fun mousePressed(e: GlobalMouseEvent?) = handleIdleTime()
        })
    }

    private fun handleIdleTime() {
        val now = System.currentTimeMillis()
        val idleTime = now - previousActivityTime
        if (idleTime >= idleTimeThreshold) {
            action(idleTime)
        }
        previousActivityTime = now
    }
}

private fun <T> tryOrNull(expression: () -> T?) = try {
    expression()
} catch (_: UnsatisfiedLinkError) {
    null
}
