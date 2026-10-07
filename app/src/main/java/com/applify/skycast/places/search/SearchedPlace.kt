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

package com.applify.skycast.places.search

import com.applify.skycast.places.Coordinates
import com.applify.skycast.places.Location
import com.applify.skycast.places.Place
import java.time.ZoneId

data class SearchedPlace(
    val name: String,
    val admin1: String?,
    val admin2: String?,
    val admin3: String?,
    val admin4: String?,
    val countryCode: String?,
    val countryName: String?,
    val coordinates: Coordinates,
    val timeZoneId: String,
) {
    fun toPlace() = Place(
        name = name,
        admin1 = admin1,
        admin2 = admin2,
        admin3 = admin3,
        admin4 = admin4,
        countryCode = countryCode,
        countryName = countryName,
        location = Location(
            timeZone = ZoneId.of(timeZoneId),
            coordinates = coordinates,
        )
    )
}