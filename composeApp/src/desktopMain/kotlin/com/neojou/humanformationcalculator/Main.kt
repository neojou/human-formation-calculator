package com.neojou.humanformationcalculator

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

/**
 * Desktop entry — opens a window hosting [App].
 */
fun main() {
    application {
        val windowState = rememberWindowState(
            size = DpSize(1440.dp, 900.dp),
        )
        Window(
            onCloseRequest = ::exitApplication,
            title = "Human-Formation Calculator",
            state = windowState,
        ) {
            App()
        }
    }
}
