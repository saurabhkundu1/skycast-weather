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

package com.applify.skycast.graphs.common.drawing

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.toSize
import com.applify.skycast.graphs.common.GraphArgs
import com.applify.skycast.graphs.common.MeasuredTickLabel

fun DrawScope.drawMeasuredTickLabel(
    args: GraphArgs,
    label: MeasuredTickLabel,
) {
    val topLeft = label.topLeft.run { copy(y = y + args.tickLabelTopMargin) }
    drawRoundRect(
        brush = SolidColor(args.tickLabelBackgroundColor),
        topLeft = topLeft.run { copy(x = x - args.tickLabelBackgroundHorizontalPadding) },
        size = label.textLayoutResult.size.toSize().run { copy(width = width + args.tickLabelBackgroundHorizontalPadding * 2) },
        cornerRadius = CornerRadius(
            x = args.tickLabelBackgroundRadius,
            y = args.tickLabelBackgroundRadius
        )
    )
    drawText(
        textLayoutResult = label.textLayoutResult,
        color = args.axisColor,
        topLeft = topLeft,
    )
}
