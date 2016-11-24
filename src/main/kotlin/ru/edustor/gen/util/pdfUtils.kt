package ru.edustor.gen.util

import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.canvas.PdfCanvas

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

fun PdfCanvas.drawGrid(borders: Rectangle, gridSquareSide: Int) {
    val xLines = (borders.left.toInt()..borders.right.toInt() step gridSquareSide).map { it + borders.left.mod(1) }
    val yLines = (borders.bottom.toInt()..borders.top.toInt() step gridSquareSide).map { it + borders.top.mod(1) }

    for (x in xLines) {
        this.moveTo(x.toDouble(), borders.top.toDouble())
                .lineTo(x.toDouble(), borders.bottom.toDouble())
                .stroke()
    }

    for (y in yLines) {
        this.moveTo(borders.left.toDouble(),y.toDouble())
                .lineTo(borders.right.toDouble(), y.toDouble())
                .stroke()
    }
}