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

package com.applify.skycast.common.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.applify.skycast.R

@Composable
fun FailedToDownloadErrorScreen(onTryAgainClick: () -> Unit, modifier: Modifier = Modifier) {
    ErrorScreen(
        text = {
            Text(stringResource(id = R.string.general_error_failed_to_download))
        },
        solution = {
            Button(onClick = onTryAgainClick) {
                Text(stringResource(id = R.string.general_btn_try_again))
            }
        },
        modifier = modifier
    )
}

@Composable
fun OutdatedErrorScreen(onTryAgainClick: () -> Unit, modifier: Modifier = Modifier) {
    ErrorScreen(
        text = {
            Text(stringResource(id = R.string.general_error_outdated))
        },
        solution = {
            Button(onClick = onTryAgainClick) {
                Text(stringResource(id = R.string.general_btn_try_again))
            }
        },
        modifier = modifier
    )
}

@Composable
fun NoSelectedPlaceErrorScreen(
    onSelectPlaceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ErrorScreen(
        text = {
            Text(stringResource(id = R.string.general_error_no_selected_place))
        },
        solution = {
            Button(onClick = onSelectPlaceClick) {
                Text(text = stringResource(id = R.string.general_btn_select_place))
            }
        },
        modifier = modifier
    )
}

@Composable
fun ErrorScreen(
    text: @Composable () -> Unit,
    solution: (@Composable () -> Unit)?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProvideTextStyle(MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center)) {
            text()
        }
        Spacer(modifier = Modifier.height(8.dp))
        solution?.let { it() }
    }
}