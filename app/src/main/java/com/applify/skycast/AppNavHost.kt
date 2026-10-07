/*
 * Copyright 2024 David Takač
 *
 * This file is part of Skycast.
 *
 * Skycast is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Skycast is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Skycast. If not, see <https://www.gnu.org/licenses/>.
 */

package com.applify.skycast

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.applify.skycast.graphs.EssentialGraphsDestination
import com.applify.skycast.places.picker.PlacePickerViewModel
import com.applify.skycast.places.search.compose.SearchedPlaceEditDestination
import com.applify.skycast.settings.SettingsDestination
import com.applify.skycast.summary.SummaryDestination
import com.applify.skycast.theme.Theme
import com.applify.skycast.unexpectederror.UnexpectedErrorScreen
import com.applify.skycast.unexpectederror.UnexpectedErrorUiState
import com.applify.skycast.unexpectederror.UnexpectedErrorViewModel
import java.time.LocalDate

@Composable
fun AppNavHost(theme: Theme, onThemeClick: (Theme) -> Unit) {
    val controller = rememberNavController()
    val unexpectedErrorVM = viewModel<UnexpectedErrorViewModel>(factory = UnexpectedErrorViewModel.Factory)
    val unexpectedErrorState = unexpectedErrorVM.state.collectAsStateWithLifecycle().value
    LaunchedEffect(unexpectedErrorState) {
        if (unexpectedErrorState is UnexpectedErrorUiState.Ongoing) {
            controller.navigate("unexpected-error") {
                popUpTo(controller.graph.startDestinationId) {
                    inclusive = true
                }
            }
        }
    }

    NavHost(navController = controller, startDestination = "summary") {
        composable("summary") {
            SummaryDestination(
                onHourlySectionClick = {
                    controller.navigate("essential-graphs")
                },
                onDayClick = {
                    controller.navigate("essential-graphs?initialDay=$it")
                },
                onSettingsButtonClick = {
                    controller.navigate("settings")
                },
                onPrecipitationClick = {
                    controller.navigate("essential-graphs")
                },
                onSearchedPlaceEditRequest = {
                    controller.navigate("searched-place-edit")
                },
            )
        }
        composable("searched-place-edit") { backStackEntry ->
            val viewModel = viewModel<PlacePickerViewModel>(
                viewModelStoreOwner = remember(backStackEntry) {
                    controller.getBackStackEntry("summary")
                },
                factory = PlacePickerViewModel.Factory,
            )
            SearchedPlaceEditDestination(
                viewModel = viewModel,
                onSearchedPlaceToEditNull = controller::navigateUp
            )
        }
        composable(
            route = "essential-graphs?initialDay={initialDay}",
            arguments = listOf(
                navArgument("initialDay") {
                    nullable = true
                    defaultValue = null
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            EssentialGraphsDestination(
                initialDay = backStackEntry.arguments?.getString("initialDay")?.let(LocalDate::parse),
                onSelectPlaceClick = controller::navigateUp,
                onBackClick = controller::navigateUp
            )
        }
        composable("settings") {
            SettingsDestination(
                theme = theme,
                onThemeClick = onThemeClick,
                onBackClick = controller::navigateUp
            )
        }
        composable("unexpected-error") {
            UnexpectedErrorScreen(
                cause = (unexpectedErrorState as UnexpectedErrorUiState.Ongoing).cause,
                onGoHomeClick = {
                    unexpectedErrorVM.consumeError()
                    controller.navigate("summary") {
                        popUpTo(controller.graph.id) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}