package ru.edustor.pdfgen.internal

import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.kernel.color.Color
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants
import org.springframework.stereotype.Component
import java.io.OutputStream
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId

@Component
open class PdfGenerator {

    private val fontBytes = this.javaClass.getResource("/fonts/Proxima Nova Thin.ttf").readBytes()
    private val pageSize = PageSize.A4
    private val cellSide = 5 / 25.4f * 72
    private val markerSide = cellSide.toDouble()

    fun makePdf(outputStream: OutputStream,
                filename: String,
                pagesCount: Int,
                authorName: String,
                subjectName: String,
                courseName: String,
                copyrightString: String,
                contactsString: String,
                drawCornell: Boolean = true,
                generateTitle: Boolean = true) {
        val pdfWriter = PdfWriter(outputStream)
        val pdfDocument = PdfDocument(pdfWriter)
        pdfDocument.documentInfo.title = filename

        val now = LocalDateTime.now(ZoneId.of("Europe/Moscow")).withNano(0)
        val academicYear = when {
            now.month > Month.JUNE -> "${now.year}-${now.year + 1}"
            else -> "${now.year - 1}-${now.year}"
        }

//        Looks like it's necessary to create new PdfFont instance for each document
        val proximaNovaFont = PdfFontFactory.createFont(fontBytes, PdfEncodings.IDENTITY_H, true, false)

        if (generateTitle) {
            drawTitlePage(pdfDocument, proximaNovaFont, authorName, subjectName, courseName, copyrightString, contactsString, academicYear)
        }

        (1..pagesCount).forEach {
            drawRegularPage(pdfDocument, proximaNovaFont, authorName, subjectName, courseName, copyrightString, contactsString, academicYear, drawCornell)
        }

        pdfDocument.close()
    }

    private fun drawTitlePage(pdfDocument: PdfDocument,
                              proximaNovaFont: PdfFont,
                              authorName: String,
                              subjectName: String,
                              courseName: String,
                              copyrightString: String,
                              contactsString: String,
                              academicYear: String) {
        val page = pdfDocument.addNewPage(pageSize)
        val canvas = PdfCanvas(page)
                .setLineWidth(0.1f)
                .setStrokeColor(Color.GRAY)
                .setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.MITER)

        val edustorStr = "Edustor Digital"
        showText(canvas, calculateCenteredTextX(pageSize.width, edustorStr, proximaNovaFont, 18f),
                pageSize.top - 50.0, edustorStr, proximaNovaFont, 18f)


        showText(canvas, calculateCenteredTextX(pageSize.width, courseName, proximaNovaFont, 20f),
                pageSize.top - 365.0, courseName, proximaNovaFont, 20f)

        showText(canvas, calculateCenteredTextX(pageSize.width, subjectName, proximaNovaFont, 30f),
                pageSize.top - 400.0, subjectName, proximaNovaFont, 30f)

        showText(canvas, calculateCenteredTextX(pageSize.width, authorName, proximaNovaFont, 18f),
                pageSize.bottom + 100.0, authorName, proximaNovaFont, 18f)

        showText(canvas, calculateCenteredTextX(pageSize.width, contactsString, proximaNovaFont, 10f),
                pageSize.bottom + 80.0, contactsString, proximaNovaFont, 10f)

        val finalCopyrightString = "© $copyrightString $academicYear"
        showText(canvas, calculateCenteredTextX(pageSize.width, finalCopyrightString, proximaNovaFont, 10f),
                pageSize.bottom + 20.0, finalCopyrightString, proximaNovaFont, 10f)


    }

    private fun drawRegularPage(pdfDocument: PdfDocument,
                                proximaNovaFont: PdfFont,
                                authorName: String,
                                subjectName: String,
                                courseName: String,
                                copyrightString: String,
                                contactsString: String,
                                academicYear: String,
                                drawCornell: Boolean = true) {

        val page = pdfDocument.addNewPage(pageSize)
        val canvas = PdfCanvas(page)

//            Draw grid
        val gridArea = drawGrid(canvas, pageSize, 40, 56, drawCornell)
        drawMarkers(canvas, gridArea)

        val labelsArea = Rectangle(gridArea.x, gridArea.y - 11, gridArea.width, gridArea.height + 15)
        drawRegularPageLabels(canvas, labelsArea, proximaNovaFont, authorName, subjectName, courseName, copyrightString, contactsString, academicYear)
    }

    private fun drawMarkers(canvas: PdfCanvas, targetArea: Rectangle) {
        canvas.saveState()

        val tLeft = targetArea.left.toDouble()
        val tRight = targetArea.right.toDouble()
        val tTop = targetArea.top.toDouble()
        val tBottom = targetArea.bottom.toDouble()

        canvas.setFillColor(Color.BLACK)
        canvas.rectangle(tLeft, tBottom, markerSide, markerSide)
        canvas.rectangle(tRight - markerSide, tBottom, markerSide, markerSide)
        canvas.rectangle(tLeft, tTop - markerSide, markerSide, markerSide)
        canvas.rectangle(tRight - markerSide, tTop - markerSide, markerSide, markerSide)
        canvas.fillStroke()

        canvas.restoreState()
    }

    private fun drawRegularPageLabels(canvas: PdfCanvas,
                                      targetArea: Rectangle,
                                      proximaNovaFont: PdfFont,
                                      authorName: String,
                                      subjectName: String,
                                      courseName: String,
                                      copyrightString: String,
                                      contactsString: String,
                                      academicYear: String) {
        val TOP_FONT_SIZE = 11f
        val BOTTOM_FONT_SIZE = 8f

//            Print top row
        val edustorStr = when {
            subjectName != "" -> "Edustor Digital"
            else -> "Edustor Paper"
        }

        val titleRow = when {
            authorName != "" -> "$edustorStr: $authorName"
            else -> edustorStr
        }
        val topRowY = targetArea.top.toDouble()
        canvas.beginText()
                .setFontAndSize(proximaNovaFont, TOP_FONT_SIZE)
                .moveText(targetArea.left.toDouble(), topRowY)
                .showText(titleRow)
                .endText()

        val topRightString = when {
            subjectName != "" -> "$subjectName, $courseName"
            else -> courseName
        }

        canvas.beginText()
                .moveText(targetArea.right.toDouble() - proximaNovaFont.getWidth(topRightString, TOP_FONT_SIZE),
                        topRowY)
                .showText(topRightString)
                .endText()

        canvas.setFontAndSize(proximaNovaFont, BOTTOM_FONT_SIZE)

//            Print bottom row
        val bottomRowY = targetArea.bottom.toDouble()
        canvas.beginText()
                .moveText(targetArea.left.toDouble(), bottomRowY)
                .showText("© $copyrightString $academicYear")
                .endText()

        canvas.beginText()
                .moveText(targetArea.right.toDouble() - proximaNovaFont.getWidth(contactsString, BOTTOM_FONT_SIZE),
                        bottomRowY)
                .showText(contactsString)
                .endText()
    }

    /**
     * Draws grid on canvas
     * @param canvas Target canvas
     * @param pageSize Page size used to calculate margins
     * @return Grid borders rectangle
     */
    private fun drawGrid(canvas: PdfCanvas, targetArea: Rectangle,
                         xCells: Int, yCells: Int, drawCornell: Boolean): Rectangle {
        canvas
                .saveState()
                .setLineWidth(0.1f)
                .setStrokeColor(Color.GRAY)
                .setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.MITER)

        val xMargin = (targetArea.width - (xCells * cellSide)) / 2
        val yMargin = (targetArea.height - (yCells * cellSide)) / 2
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

//        Cornell template
        if (drawCornell) {
            canvas.saveState()
                    .setLineWidth(1f)

            val titleLineY = (gridBorders.top - 3 * cellSide).toDouble()
            canvas.moveTo(gridBorders.left.toDouble(), titleLineY)
                    .lineTo(gridBorders.right.toDouble(), titleLineY)
                    .stroke()

            val summaryLineY = (gridBorders.bottom + 5 * cellSide).toDouble()
            canvas.moveTo(gridBorders.left.toDouble(), summaryLineY)
                    .lineTo(gridBorders.right.toDouble(), summaryLineY)
                    .stroke()

            val notesLineX = (gridBorders.left + 8 * cellSide).toDouble()
            canvas.moveTo(notesLineX, gridBorders.top.toDouble())
                    .lineTo(notesLineX, (gridBorders.bottom + 5 * cellSide).toDouble())
                    .stroke()

            canvas.restoreState()
        }
        canvas.restoreState()
        return gridBorders
    }

    private fun calculateCenteredTextX(width: Float, string: String, font: PdfFont, fontSize: Float): Double {
        return (width - font.getWidth(string, fontSize)) / 2.0
    }

    private fun showText(canvas: PdfCanvas, x: Double, y: Double, text: String, font: PdfFont, fontSize: Float) {
        canvas.beginText()
                .setFontAndSize(font, fontSize)
                .moveText(x, y)
                .showText(text)
                .endText()
    }
}
