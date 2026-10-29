/*
 * SPDX-FileCopyrightText: 2026 NewPipe e.V. <>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package net.newpipe.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import net.newpipe.app.navigation.Destination

/**
 * Fragment that hosts the Compose-based About screen directly inside MainActivity,
 * keeping the bottom navigation bar persistent and visible.
 */
class AboutFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                App(
                    startDestination = Destination.About,
                    onCloseRequest = {
                        parentFragmentManager.popBackStack()
                    }
                )
            }
        }
    }
}

