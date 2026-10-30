/*
 * Copyright 2024 David Takač
 *
 * This file is part of Skycast and is licensed under the GNU General Public License v3.0 or later.
 */

package com.applify.skycast.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.applify.skycast.App
import com.applify.skycast.MainActivity
import com.applify.skycast.R
import com.applify.skycast.forecast.parameters.condition.string
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlin.math.roundToInt

class ForecastWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        val pending = goAsync()
        scope.launch {
            try {
                ids.forEach { update(context, manager, it) }
            } finally {
                pending.finish()
            }
        }
    }

    private suspend fun update(context: Context, manager: AppWidgetManager, id: Int) {
        val views = RemoteViews(context.packageName, R.layout.weather_widget)
        val app = context.applicationContext as App
        val place = app.container.selectedPlaceRepo.getSelectedPlace()
        if (place == null) {
            views.setTextViewText(R.id.widgetPlace, context.getString(R.string.app_name))
            views.setTextViewText(R.id.widgetTemperature, "Choose a place in Skycast")
            views.setTextViewText(R.id.widgetCondition, "Open the app to set up your forecast")
        } else {
            views.setTextViewText(R.id.widgetPlace, "${place.name}${place.countryCode?.let { ", $it" }.orEmpty()}")
            val units = app.container.selectedUnitsRepo.getSelectedUnits()
            val forecast = app.container.forecastRepo.get(place.location, units)
            val now = Instant.now()
            val temperature = forecast?.temperature?.get(now)?.temperature
            val condition = forecast?.condition?.get(now)?.condition
            views.setTextViewText(R.id.widgetTemperature, temperature?.let { "${it.value.roundToInt()}°" } ?: "Weather unavailable")
            views.setTextViewText(R.id.widgetCondition, condition?.let { runCatching { it.string(context) }.getOrNull() } ?: "Open Skycast to refresh")
            val updatedAt = forecast?.timestamp?.atZone(place.location.timeZone)?.format(
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(Locale.getDefault())
            )
            views.setTextViewText(R.id.widgetUpdated, updatedAt?.let { "Updated $it" } ?: "No saved forecast")
        }
        val openApp = PendingIntent.getActivity(
            context, 0, Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widgetRoot, openApp)
        manager.updateAppWidget(id, views)
    }

    companion object {
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        fun refresh(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, ForecastWidgetProvider::class.java))
            if (ids.isNotEmpty()) {
                context.sendBroadcast(Intent(context, ForecastWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                })
            }
        }
    }
}
