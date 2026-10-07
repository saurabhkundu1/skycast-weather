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

package com.applify.skycast.places.search.compose

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.applify.skycast.places.picker.PlacePickerViewModel

@Composable
fun SearchedPlaceEditDestination(
    viewModel: PlacePickerViewModel,
    onSearchedPlaceToEditNull: () -> Unit,
) {
    val place = viewModel.state.collectAsStateWithLifecycle().value.searchedPlaceToEdit
    LaunchedEffect(place) {
        if (place == null) {
            onSearchedPlaceToEditNull()
        }
    }
    BackHandler {
        viewModel.cancelSearchedPlaceEdit()
    }
    if (place != null) {
        SearchedPlaceEditScreen(
            searchedPlace = place,
            onEdit = viewModel::selectSearchedPlace,
            onCloseClick = viewModel::cancelSearchedPlaceEdit,
        )
    }
}