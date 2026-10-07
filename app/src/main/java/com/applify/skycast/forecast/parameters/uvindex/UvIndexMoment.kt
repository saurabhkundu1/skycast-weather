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

package com.applify.skycast.forecast.parameters.uvindex

import com.applify.skycast.forecast.Moment
import java.time.ZonedDateTime
import java.util.Objects

class UvIndexMoment(
    timeZdt: ZonedDateTime,
    val uvIndex: UvIndex
) : Moment(timeZdt) {
    override fun toString(): String = "$timeZdt: $uvIndex"

    override fun equals(other: Any?): Boolean =
        other is UvIndexMoment
                && other.timeZdt == timeZdt
                && other.uvIndex == uvIndex

    override fun hashCode(): Int =
        Objects.hash(timeZdt, uvIndex)
}