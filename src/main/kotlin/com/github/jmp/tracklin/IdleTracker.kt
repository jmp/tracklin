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
    private var keyboardHook: GlobalKeyboardHook? = null
    private var mouseHook: GlobalMouseHook? = null
    private val inputListener = GlobalInputListener { handleIdleTime() }
    private var previousActivityTime = System.currentTimeMillis()

    fun startTracking() {
        keyboardHook = tryOrNull { GlobalKeyboardHook() }
        mouseHook = tryOrNull { GlobalMouseHook() }
        keyboardHook?.addKeyListener(inputListener)
        mouseHook?.addMouseListener(inputListener)
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
}

class GlobalInputListener(
    private val onEvent: () -> Unit
) : GlobalKeyListener, GlobalMouseListener {
    override fun keyPressed(event: GlobalKeyEvent) = onEvent()
    override fun keyReleased(event: GlobalKeyEvent) = onEvent()
    override fun mouseReleased(event: GlobalMouseEvent?) = onEvent()
    override fun mouseWheel(event: GlobalMouseEvent?) = onEvent()
    override fun mouseMoved(event: GlobalMouseEvent?) = onEvent()
    override fun mousePressed(event: GlobalMouseEvent?) = onEvent()
}

private fun <T> tryOrNull(expression: () -> T?) = try {
    expression()
} catch (_: UnsatisfiedLinkError) {
    null
}
