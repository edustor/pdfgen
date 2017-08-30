package ru.edustor.pdfgen.internal

import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.color.Color
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants
import org.springframework.stereotype.Component
import ru.edustor.commons.version.EdustorVersionInfoHolder
import java.io.OutputStream
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Component
open class PdfGenerator(val versionInfoHolder: EdustorVersionInfoHolder) {
    fun makePdf(outputStream: OutputStream, pageCount: Int) {
        val pdfWriter = PdfWriter(outputStream)
        val pdfDocument = PdfDocument(pdfWriter)
        pdfDocument.documentInfo.title = "Edustor blank pages"

        val fontBytes = this.javaClass.getResource("/fonts/Proxima Nova Thin.ttf").readBytes()
        val proximaNovaFont = PdfFontFactory.createFont(fontBytes, PdfEncodings.IDENTITY_H, true, true)

        val TOP_FONT_SIZE = 11f
        val BOTTOM_FONT_SIZE = 8f
        val PAGE_SIZE = PageSize.A4
        val CELL_SIDE = 5 / 25.4f * 72

        val now = LocalDateTime.now(ZoneId.of("Europe/Moscow")).withNano(0)
        val nowStr = "${now.format(DateTimeFormatter.ISO_LOCAL_DATE)} ${now.format(DateTimeFormatter.ISO_LOCAL_TIME)} MSK"

        for (i in 1..pageCount) {
            val page = pdfDocument.addNewPage(PAGE_SIZE)
            val canvas = PdfCanvas(page)
                    .setLineWidth(0.1f)
                    .setStrokeColor(Color.GRAY)
                    .setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.MITER)

//            Draw grid
            val gridArea = drawGrid(canvas, PAGE_SIZE, CELL_SIDE, 40, 56)

//            Print top row
            val topIdString = "1 курс МОиАИС ПММ ВГУ"
            val topRowY = gridArea.top.toDouble() + 3
            canvas.beginText()
                    .setFontAndSize(proximaNovaFont, TOP_FONT_SIZE)
                    .moveText(gridArea.left.toDouble(), topRowY)
                    .showText("Edustor Digital")
                    .endText()

            canvas.beginText()
                    .moveText(gridArea.right.toDouble() - proximaNovaFont.getWidth(topIdString, TOP_FONT_SIZE),
                            topRowY)
                    .showText(topIdString)
                    .endText()

            canvas.setFontAndSize(proximaNovaFont, BOTTOM_FONT_SIZE)

//            Print bottom row
            val bottomRowY = gridArea.bottom.toDouble() - 9
            canvas.beginText()
                    .moveText(gridArea.left.toDouble(), bottomRowY)
                    .showText("© VSU University, Dmitry Romanov 2017-2018")
                    .endText()

            val pageIdStr = "wutiarn.ru | t.me/wutiarn | me@wutiarn.ru"
            canvas.beginText()
                    .moveText(gridArea.right.toDouble() - proximaNovaFont.getWidth(pageIdStr, BOTTOM_FONT_SIZE),
                            bottomRowY)
                    .showText(pageIdStr)
                    .endText()
        }
        pdfDocument.close()
    }

    /**
     * Draws grid on canvas
     * @param canvas Target canvas
     * @param pageSize Page size used to calculate margins
     * @param cellSide Cell side length, in pt
     * @return Grid borders rectangle
     */
    private fun drawGrid(canvas: PdfCanvas, pageSize: Rectangle, cellSide: Float, xCells: Int, yCells: Int): Rectangle {
        val xMargin = (pageSize.width - (xCells * cellSide)) / 2
        val yMargin = (pageSize.height - (yCells * cellSide)) / 2
        if (xMargin < 0 || yMargin < 0) {
            throw IllegalArgumentException("Can't fit ${xCells}x$yCells grid to specified page size")
        }

        val gridBorders = Rectangle(xMargin, yMargin, cellSide * xCells, cellSide * yCells)

        (0..xCells)
                .map { (gridBorders.left + it * cellSide).toDouble() }
                .forEach {
                    canvas.moveTo(it, gridBorders.top.toDouble())
                            .lineTo(it, gridBorders.bottom.toDouble())
                            .stroke()
                }

        (0..yCells)
                .map { (gridBorders.bottom + it * cellSide).toDouble() }
                .forEach {
                    canvas.moveTo(gridBorders.left.toDouble(), it)
                            .lineTo(gridBorders.right.toDouble(), it)
                            .stroke()
                }

        return gridBorders
    }
}
