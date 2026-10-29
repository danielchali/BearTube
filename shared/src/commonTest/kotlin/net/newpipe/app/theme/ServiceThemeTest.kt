/*
 * SPDX-FileCopyrightText: 2026 NewPipe e.V. <>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package net.newpipe.app.theme

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals

class ServiceThemeTest {

    @Test
    fun testOnlyServiceIsYouTube() {
        assertEquals(Service.entries.size, 1)
        assertEquals(Service.entries.single(), Service.YOUTUBE)
    }

    @Test
    fun testServiceThemeUsesBlue() {
        assertEquals(youTubeLightScheme.primaryContainer, Color(0xFF1565C0))
        assertEquals(youTubeDarkScheme.primaryContainer, Color(0xFF0D47A1))
    }
}
