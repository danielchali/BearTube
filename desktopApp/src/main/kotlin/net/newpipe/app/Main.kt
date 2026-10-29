/*
 * SPDX-FileCopyrightText: 2026 NewPipe e.V. <>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package net.newpipe.app

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import newpipe.shared.generated.resources.Res
import newpipe.shared.generated.resources.ic_foreground
import org.jetbrains.compose.resources.painterResource

/**
 * Entry point for compose-related UI components on Desktop
 */
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "BearTube",
        icon = painterResource(Res.drawable.ic_foreground)
    ) {
        App(onCloseRequest = ::exitApplication)
    }
}
