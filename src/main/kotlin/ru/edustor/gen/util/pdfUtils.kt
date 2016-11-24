package ru.edustor.gen.util

import com.itextpdf.kernel.geom.Rectangle

fun getGridBorders(pageSize: Rectangle,
                   gridSquareSide: Float,
                   borderTop: Float,
                   borderBottom: Float,
                   borderLR: Float
): Rectangle {
    val (xMin, xMax) = calculateMinMaxPoints(pageSize.width, gridSquareSide, borderLR, borderLR)
    val (yMin, yMax) = calculateMinMaxPoints(pageSize.height, gridSquareSide, borderTop, borderBottom)

    return Rectangle(pageSize.x + xMin, pageSize.y + yMin, pageSize.x + xMax - xMin, pageSize.y + yMax - yMin)
}

private fun calculateMinMaxPoints(maxLength: Float, step: Float,
                                  marginStart: Float, marginEnd: Float): Pair<Float, Float> {
    val allowedLength = maxLength - (marginStart + marginEnd)
    val maxRoundLength = allowedLength - (allowedLength % step)

    val additionalMargin = (allowedLength - maxRoundLength) / 2

    val min = marginStart + additionalMargin
    val max = maxLength - (marginEnd + additionalMargin)

    val e = (max - min) % step

    return min to max
}