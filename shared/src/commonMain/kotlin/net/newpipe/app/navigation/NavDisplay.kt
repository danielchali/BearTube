/*
 * SPDX-FileCopyrightText: 2026 NewPipe e.V. <>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package net.newpipe.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay as Navigation3NavDisplay
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer
import newpipe.shared.generated.resources.Res
import newpipe.shared.generated.resources.ic_info
import newpipe.shared.generated.resources.ic_settings
import newpipe.shared.generated.resources.settings
import newpipe.shared.generated.resources.title_activity_about
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.navigation3.koinEntryProvider
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf

/**
 * Navigation display for compose screens
 * @param startDestination Starting destination for the app
 * @param navigator Navigator to help with navigation, injected by Koin
 * @param onCloseRequest Callback to close the app
 */
@OptIn(KoinExperimentalAPI::class)
@Composable
fun NavDisplay(
    startDestination: Destination,
    onCloseRequest: () -> Unit,
    navigator: Navigator = koinInject {
        parametersOf(startDestination, onCloseRequest)
    }
) {
    val backstack = rememberSerializable(serializer = SnapshotStateListSerializer<Destination>()) {
        navigator.backstack
    }

    val saveableStateDecorator =
        rememberSaveableStateHolderNavEntryDecorator<Destination>()
    val viewModelStoreDecorator =
        rememberViewModelStoreNavEntryDecorator<Destination>()
    val entryDecorators: List<NavEntryDecorator<Destination>> =
        remember(saveableStateDecorator, viewModelStoreDecorator) {
            listOf(saveableStateDecorator, viewModelStoreDecorator)
        }

    val currentDestination = backstack.lastOrNull()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val settingsLabel = stringResource(Res.string.settings)
                NavigationBarItem(
                    selected = currentDestination == Destination.Settings,
                    onClick = { navigator.navigateToTopLevel(Destination.Settings) },
                    icon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_settings),
                            contentDescription = settingsLabel
                        )
                    },
                    label = { Text(settingsLabel) }
                )

                val aboutLabel = stringResource(Res.string.title_activity_about)
                NavigationBarItem(
                    selected = currentDestination == Destination.About,
                    onClick = { navigator.navigateToTopLevel(Destination.About) },
                    icon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_info),
                            contentDescription = aboutLabel
                        )
                    },
                    label = { Text(aboutLabel) }
                )
            }
        }
    ) { contentPadding ->
        Navigation3NavDisplay(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            backStack = backstack,
            onBack = { navigator.navigateUp() },
            entryDecorators = entryDecorators,
            entryProvider = koinEntryProvider()
        )
    }
}
