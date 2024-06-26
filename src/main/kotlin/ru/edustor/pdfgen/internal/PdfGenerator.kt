package ru.edustor.pdfgen.internal

import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.io.image.ImageData
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants
import org.springframework.stereotype.Component
import ru.edustor.pdfgen.utils.QrUtils
import ru.edustor.pdfgen.utils.QrUtils.getAsByteArray
import java.io.OutputStream
import java.lang.IllegalStateException

@Component
open class PdfGenerator {

    private val fontBytes = this.javaClass.getResource("/fonts/Proxima Nova Thin.ttf").readBytes()

    fun makePdf(
        outputStream: OutputStream,
        filename: String,
        p: PdfGenParams
    ) {
        val pdfWriter = PdfWriter(outputStream)
        val pdfDocument = PdfDocument(pdfWriter)
        pdfDocument.documentInfo.title = filename

//        Looks like it's necessary to create new PdfFont instance for each document
        val proximaNovaFont = PdfFontFactory.createFont(fontBytes, PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED, true)

        val markerImage = ImageDataFactory.create(javaClass.getResource("/images/marker.png"))

        if (p.generateTitle) {
            drawTitlePage(pdfDocument, proximaNovaFont, p)
        }

        (1..p.pageCount).forEach { index ->
            drawRegularPage(index, pdfDocument, proximaNovaFont, markerImage, p)
        }

        pdfDocument.close()
    }

    private fun drawTitlePage(
        pdfDocument: PdfDocument,
        proximaNovaFont: PdfFont,
        p: PdfGenParams
    ) {
        val t = p.type
        val page = pdfDocument.addNewPage(t.pageSize)
        val canvas = PdfCanvas(page)
            .setLineWidth(0.1f)
            .setStrokeColor(ColorConstants.GRAY)
            .setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.MITER)

        val edustorStr = when (p.type) {
            EdustorPdfTypes.DIGITAL -> "Edustor Digital"
            EdustorPdfTypes.PAPER -> "Edustor Paper"
            else -> throw IllegalStateException("Unsupported EdustorPdfType")
        }
        showText(
            canvas, calculateCenteredTextX(t.pageSize.width, edustorStr, proximaNovaFont, 18f),
            t.pageSize.top - 50.0, edustorStr, proximaNovaFont, 18f
        )

        showText(
            canvas, calculateCenteredTextX(t.pageSize.width, p.subjectName, proximaNovaFont, 30f),
            t.pageSize.top - 365.0, p.subjectName, proximaNovaFont, 30f
        )

        showText(
            canvas, calculateCenteredTextX(t.pageSize.width, p.courseName, proximaNovaFont, 20f),
            t.pageSize.top - 400.0, p.courseName, proximaNovaFont, 20f
        )

        showText(
            canvas, calculateCenteredTextX(t.pageSize.width, p.authorName, proximaNovaFont, 18f),
            t.pageSize.bottom + 100.0, p.authorName, proximaNovaFont, 18f
        )

        showText(
            canvas, calculateCenteredTextX(t.pageSize.width, p.contactsString, proximaNovaFont, 10f),
            t.pageSize.bottom + 80.0, p.contactsString, proximaNovaFont, 10f
        )

        val finalStringBuilder = StringBuilder()
        if (p.subjectCode.isNotBlank()) {
            finalStringBuilder.append("Subject code: ${p.subjectCode} ")
        }

        finalStringBuilder.append("Date: ${p.currentDate}")
        val finalString = finalStringBuilder.toString()
        showText(
            canvas, calculateCenteredTextX(t.pageSize.width, finalString, proximaNovaFont, 10f),
            t.pageSize.bottom + 20.0, finalString, proximaNovaFont, 10f
        )
    }

    private fun drawRegularPage(
        index: Int,
        pdfDocument: PdfDocument,
        proximaNovaFont: PdfFont,
        markerImage: ImageData,
        p: PdfGenParams
    ) {

        val page = pdfDocument.addNewPage(p.type.pageSize)
        val canvas = PdfCanvas(page)

        val gridArea = drawGrid(canvas, p)

        var pageId: EdustorId? = null
        if (p.markersEnabled) {
            pageId = EdustorId.new(p.batchId, index)
            drawMarkers(canvas, gridArea, markerImage, p.type)
            drawQR(pageId, canvas, gridArea, p.type)
            drawMetaFieldsMarkup(canvas, gridArea, proximaNovaFont, p)
        }

        val labelsArea = Rectangle(gridArea.x, gridArea.y - 9, gridArea.width, gridArea.height + 15)
        drawRegularPageLabels(canvas, labelsArea, proximaNovaFont, p, pageId)
    }

    private fun drawMarkers(canvas: PdfCanvas, targetArea: Rectangle, markerImage: ImageData, t: EdustorPdfType) {
        val markerSize = t.markerSize.toFloat()

        val position = Rectangle(
            targetArea.right - markerSize,
            targetArea.top + (t.gridCellSide.toFloat() * 0.25f),
            markerSize,
            markerSize
        )
        canvas.addImageFittedIntoRectangle(markerImage, position, false)
    }

    private fun drawQR(pageId: EdustorId, canvas: PdfCanvas, targetArea: Rectangle, t: EdustorPdfType) {
        val url = "https://edustor.wtrn.ru/p/${pageId.compactId}"
        val qr = QrUtils.makeQR(url)
        val qrPdfImage = ImageDataFactory.create(qr.getAsByteArray())
        val qrSide = (2 * t.gridCellSide).toFloat()
        val qrLocation = Rectangle(targetArea.right - qrSide, targetArea.y, qrSide, qrSide)
        canvas.addImageFittedIntoRectangle(qrPdfImage, qrLocation, true)
    }

    private fun drawMetaFieldsMarkup(
        canvas: PdfCanvas,
        targetArea: Rectangle,
        proximaNovaFont: PdfFont,
        p: PdfGenParams
    ) {
        val t = p.type
        val width = t.metaWidth * t.gridCellSide
        val height = t.metaHeight * t.gridCellSide

        canvas.saveState()
            .setLineWidth(0.2f)
            .setStrokeColor(ColorConstants.BLACK)
            .setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.MITER)
            .moveTo(targetArea.right - width, targetArea.top.toDouble())
            .lineTo(targetArea.right - width, targetArea.top - height)
            .lineTo(targetArea.right.toDouble(), targetArea.top - height)
            .lineTo(targetArea.right.toDouble(), targetArea.top.toDouble())
            .lineTo(targetArea.right - width, targetArea.top.toDouble())
            .stroke()
            .restoreState()

        p.subjectCode.forEachIndexed() { i, char ->
            drawMetaFieldChar(0, i, char.toString(), canvas, targetArea, proximaNovaFont, p)
        }
        listOf(2, 5).forEach { i ->
            drawMetaFieldChar(1, i, ".", canvas, targetArea, proximaNovaFont, p)
        }
    }

    private fun drawMetaFieldChar(
        row: Int,
        column: Int,
        char: String,
        canvas: PdfCanvas,
        targetArea: Rectangle,
        font: PdfFont,
        p: PdfGenParams
    ) {
        val t = p.type
        val fontSize = 14f

        val charWidth = font.getWidth(char, fontSize)

        val x = targetArea.right - (t.metaWidth - column - 0.5) * t.gridCellSide - (charWidth / 2.0)
        val y = targetArea.top - (row + 0.8) * t.gridCellSide

        showText(canvas, x, y, char, font, fontSize)
    }


    private fun drawRegularPageLabels(
        canvas: PdfCanvas,
        targetArea: Rectangle,
        proximaNovaFont: PdfFont,
        p: PdfGenParams,
        pageId: EdustorId?
    ) {
//            Print top row

        val t = p.type
        val topRowY = targetArea.top.toDouble() - 3f
        val leftX = targetArea.left.toDouble()
        canvas.beginText()
            .setFontAndSize(proximaNovaFont, t.topFontSize)
            .moveText(leftX, topRowY)
            .showText(t.title)
            .endText()

        val topRightString = when {
            p.subjectName != "" -> "${p.subjectName}, ${p.courseName}"
            else -> p.courseName
        }

        val topRightLabelSize = proximaNovaFont.getWidth(topRightString, t.topFontSize)
        val topRightX = when (p.markersEnabled) {
            true -> targetArea.right - (t.markerSize + 3) - topRightLabelSize
            false -> targetArea.right.toDouble() - topRightLabelSize
        }

        canvas.beginText()
            .moveText(topRightX, topRowY)
            .showText(topRightString)
            .endText()

        canvas.setFontAndSize(proximaNovaFont, t.bottomFontSize)

//            Print bottom row
        val bottomRowY = targetArea.bottom - t.bottomLabelMargin

        if (p.authorName.isNotBlank()) {
            val bottomLeftText = "Author: ${p.authorName}, ${p.contactsString}"
            canvas.beginText()
                .moveText(leftX, bottomRowY)
                .showText(bottomLeftText)
                .endText()
        }

        if (pageId != null) {
            val bottomRightText = "${p.currentDate} ${pageId.humanReadableId}"

            val bottomRightLabelSize = proximaNovaFont.getWidth(bottomRightText, t.bottomFontSize)
            val bottomRightX = targetArea.right.toDouble() - bottomRightLabelSize
            canvas.beginText()
                .moveText(bottomRightX, bottomRowY)
                .showText(bottomRightText)
                .endText()
        }
    }

    /**
     * Draws grid on canvas
     * @param canvas Target canvas
     * @param pageSize Page size used to calculate margins
     * @return Grid borders rectangle
     */
    private fun drawGrid(canvas: PdfCanvas, p: PdfGenParams): Rectangle {
        canvas
            .saveState()
            .setLineWidth(0.1f)
            .setStrokeColor(ColorConstants.GRAY)
            .setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.MITER)

        val t = p.type
        val gridBorders = Rectangle(
            t.gridStartX.toFloat(), t.gridStartY.toFloat(),
            (t.gridCellSide * t.gridXCells).toFloat(), (t.gridCellSide * t.gridYCells).toFloat()
        )

        (0..t.gridXCells)
            .map { (gridBorders.left + it * t.gridCellSide) }
            .forEach {
                canvas.moveTo(it, gridBorders.top.toDouble())
                    .lineTo(it, gridBorders.bottom.toDouble())
                    .stroke()
            }

        (0..t.gridYCells)
            .map { (gridBorders.bottom + it * t.gridCellSide) }
            .forEach {
                canvas.moveTo(gridBorders.left.toDouble(), it)
                    .lineTo(gridBorders.right.toDouble(), it)
                    .stroke()
            }

//        Cornell template
        if (p.drawCornell) {
            canvas.saveState()
                .setLineWidth(1f)

            val titleLineY = (gridBorders.top - 3 * t.gridCellSide)
            canvas.moveTo(gridBorders.left.toDouble(), titleLineY)
                .lineTo(gridBorders.right.toDouble(), titleLineY)
                .stroke()

            val summaryLineY = (gridBorders.bottom + 5 * t.gridCellSide)
            canvas.moveTo(gridBorders.left.toDouble(), summaryLineY)
                .lineTo(gridBorders.right.toDouble(), summaryLineY)
                .stroke()

            val notesLineX = (gridBorders.left + 8 * t.gridCellSide)
            canvas.moveTo(notesLineX, gridBorders.top.toDouble())
                .lineTo(notesLineX, (gridBorders.bottom + 5 * t.gridCellSide))
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
