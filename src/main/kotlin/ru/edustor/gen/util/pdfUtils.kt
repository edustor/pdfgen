package ru.edustor.gen.util

import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.canvas.PdfCanvas

fun getGridBorders(allowedArea: Rectangle,
                   gridSquareSide: Float
): Rectangle {
    val xm = calculateAdditionalMargin(allowedArea.width, gridSquareSide)
    val ym = calculateAdditionalMargin(allowedArea.height, gridSquareSide)

    val result = allowedArea.clone().applyMargins<Rectangle>(ym, xm, ym, xm, false)
    return result
}

private fun calculateAdditionalMargin(maxLength: Float, step: Float): Float {
    val maxRoundLength = maxLength - (maxLength % step)

    val additionalMargin = (maxLength - maxRoundLength) / 2

    return additionalMargin
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
        this.moveTo(borders.left.toDouble(), y.toDouble())
                .lineTo(borders.right.toDouble(), y.toDouble())
                .stroke()
    }
}