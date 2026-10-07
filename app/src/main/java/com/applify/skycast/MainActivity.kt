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

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.applify.skycast.theme.AppTheme
import com.applify.skycast.theme.Theme
import com.applify.skycast.theme.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel = viewModel<ThemeViewModel>(factory = ThemeViewModel.Factory)
            val theme = themeViewModel.state.collectAsState().value
            val useDarkTheme = when (theme) {
                Theme.Dark -> true
                Theme.Light -> false
                Theme.FollowSystem -> isSystemInDarkTheme()
            }

            LaunchedEffect(useDarkTheme) {
                setSystemBarIconColors(useDarkTheme)
            }
            AppTheme(useDarkTheme) {
                AppNavHost(
                    theme = theme,
                    onThemeClick = themeViewModel::setTheme
                )
            }
        }
    }

    private fun setSystemBarIconColors(darkTheme: Boolean) {
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = !darkTheme
        insetsController.isAppearanceLightNavigationBars = !darkTheme
    }
}