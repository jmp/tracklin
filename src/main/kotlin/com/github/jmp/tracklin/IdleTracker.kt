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
    private val inputListener = GlobalInputListener { handleIdleTime() }
    private var keyboardHook: GlobalKeyboardHook? = null
    private var mouseHook: GlobalMouseHook? = null
    private var previousActivityTime = 0L

    fun startTracking() {
        keyboardHook = tryOrNull { GlobalKeyboardHook() }
        mouseHook = tryOrNull { GlobalMouseHook() }
        keyboardHook?.addKeyListener(inputListener)
        mouseHook?.addMouseListener(inputListener)
        previousActivityTime = System.currentTimeMillis()
    }

    fun stopTracking() {
        keyboardHook?.shutdownHook()
        mouseHook?.shutdownHook()
    }

    private fun handleIdleTime() {
        val now = System.currentTimeMillis()
        val idleTime = now - previousActivityTime
        if (idleTime >= idleTimeThreshold) {
            action(idleTime)
        }
        previousActivityTime = now
    }

    private class GlobalInputListener(
        private val onEvent: () -> Unit
    ) : GlobalKeyListener, GlobalMouseListener {
        override fun keyPressed(e: GlobalKeyEvent) = onEvent()
        override fun keyReleased(e: GlobalKeyEvent) = onEvent()
        override fun mouseReleased(e: GlobalMouseEvent?) = onEvent()
        override fun mouseWheel(e: GlobalMouseEvent?) = onEvent()
        override fun mouseMoved(e: GlobalMouseEvent?) = onEvent()
        override fun mousePressed(e: GlobalMouseEvent?) = onEvent()
    }
}

private fun <T> tryOrNull(expression: () -> T?) = try {
    expression()
} catch (_: UnsatisfiedLinkError) {
    null
}
