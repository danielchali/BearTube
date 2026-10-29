/*
 * SPDX-FileCopyrightText: 2026 NewPipe e.V. <>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package net.newpipe.app.theme

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class ServiceThemeTest {

    @Test
    fun testOnlyServiceIsYouTube() = runComposeUiTest {
        assertEquals(currentService(), Service.YOUTUBE)
    }

    @Test
    fun testServiceThemeUsesBlue() = runComposeUiTest {
        assertEquals(youTubeLightScheme.primaryContainer, Color(0xFF1565C0))
        assertEquals(youTubeDarkScheme.primaryContainer, Color(0xFF0D47A1))
    }
}
