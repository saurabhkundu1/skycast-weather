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

package com.applify.skycast.common.util

import org.json.JSONArray
import org.json.JSONObject

fun <T : Any> Collection<T>.mapToJSONArray(transform: (T) -> Any = { it }) =
    JSONArray(map(transform))

fun <T> JSONArray.mapJSONObjects(transform: (JSONObject) -> T): List<T> =
    mapInternal(transform, argProducer = ::getJSONObject)

fun <T> JSONArray.mapStrings(transform: (String) -> T): List<T> =
    mapInternal(transform, argProducer = ::getString)

fun <T> JSONArray.mapDoubles(transform: (Double) -> T): List<T> =
    mapInternal(transform, argProducer = ::getDouble)

fun <T> JSONArray.mapInts(transform: (Int) -> T): List<T> =
    mapInternal(transform, argProducer = ::getInt)

fun <T> JSONArray.mapLongs(transform: (Long) -> T): List<T> =
    mapInternal(transform, argProducer = ::getLong)

private fun <T, U> JSONArray.mapInternal(
    transform: (U) -> T,
    argProducer: (Int) -> U,
): List<T> {
    val result = mutableListOf<T>()
    for (i in 0 until length()) {
        result.add(transform(argProducer(i)))
    }
    return result
}

fun JSONObject.getStringOrNull(name: String): String? =
    if (isNull(name)) null else getString(name)

fun JSONObject.getJSONArrayOrNull(name: String): JSONArray? =
    if (isNull(name)) null else getJSONArray(name)
