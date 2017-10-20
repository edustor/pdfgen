package ru.edustor.pdfgen.internal

import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.kernel.color.Color
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants
import org.springframework.stereotype.Component
import java.io.OutputStream

@Component
open class PdfGenerator {

    private val fontBytes = this.javaClass.getResource("/fonts/Proxima Nova Thin.ttf").readBytes()

    fun makePdf(outputStream: OutputStream,
                filename: String,
                p: PdfGenParams) {
        val pdfWriter = PdfWriter(outputStream)
        val pdfDocument = PdfDocument(pdfWriter)
        pdfDocument.documentInfo.title = filename

//        Looks like it's necessary to create new PdfFont instance for each document
        val proximaNovaFont = PdfFontFactory.createFont(fontBytes, PdfEncodings.IDENTITY_H, true, false)

        if (p.generateTitle) {
            drawTitlePage(pdfDocument, proximaNovaFont, p)
        }

        (1..p.pageCount).forEach {
            drawRegularPage(pdfDocument, proximaNovaFont, p)
        }

        pdfDocument.close()
    }

    private fun drawTitlePage(pdfDocument: PdfDocument,
                              proximaNovaFont: PdfFont,
                              p: PdfGenParams) {
        val t = p.type
        val page = pdfDocument.addNewPage(t.pageSize)
        val canvas = PdfCanvas(page)
                .setLineWidth(0.1f)
                .setStrokeColor(Color.GRAY)
                .setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.MITER)

        val edustorStr = "Edustor Digital"
        showText(canvas, calculateCenteredTextX(t.pageSize.width, edustorStr, proximaNovaFont, 18f),
                t.pageSize.top - 50.0, edustorStr, proximaNovaFont, 18f)


        showText(canvas, calculateCenteredTextX(t.pageSize.width, p.courseName, proximaNovaFont, 20f),
                t.pageSize.top - 365.0, p.courseName, proximaNovaFont, 20f)

        showText(canvas, calculateCenteredTextX(t.pageSize.width, p.subjectName, proximaNovaFont, 30f),
                t.pageSize.top - 400.0, p.subjectName, proximaNovaFont, 30f)

        showText(canvas, calculateCenteredTextX(t.pageSize.width, p.authorName, proximaNovaFont, 18f),
                t.pageSize.bottom + 100.0, p.authorName, proximaNovaFont, 18f)

        showText(canvas, calculateCenteredTextX(t.pageSize.width, p.contactsString, proximaNovaFont, 10f),
                t.pageSize.bottom + 80.0, p.contactsString, proximaNovaFont, 10f)

        val finalCopyrightString = "© ${p.copyrightString} ${p.academicYear}"
        showText(canvas, calculateCenteredTextX(t.pageSize.width, finalCopyrightString, proximaNovaFont, 10f),
                t.pageSize.bottom + 20.0, finalCopyrightString, proximaNovaFont, 10f)


    }

    private fun drawRegularPage(pdfDocument: PdfDocument,
                                proximaNovaFont: PdfFont,
                                p: PdfGenParams) {

        val page = pdfDocument.addNewPage(p.type.pageSize)
        val canvas = PdfCanvas(page)

        val gridArea = drawGrid(canvas, p)

        if (p.type.markersEnabled) {
            drawMarkers(canvas, gridArea, p.type)

            drawMetaFields(canvas, gridArea, proximaNovaFont, p.type)
        }

        val labelsArea = Rectangle(gridArea.x, gridArea.y - 9, gridArea.width, gridArea.height + 15)
        drawRegularPageLabels(canvas, labelsArea, proximaNovaFont, p)
    }

    private fun drawMarkers(canvas: PdfCanvas, targetArea: Rectangle, t: EdustorPdfType) {
        canvas.saveState()

        val tLeft = targetArea.left.toDouble()
        val tRight = targetArea.right.toDouble()
        val tTop = targetArea.top.toDouble()
        val tBottom = targetArea.bottom.toDouble()

        canvas.setFillColor(Color.BLACK)
        canvas.rectangle(tLeft, tBottom, t.markerSide, t.markerSide)
        canvas.rectangle(tRight - t.markerSide, tBottom, t.markerSide, t.markerSide)
        canvas.rectangle(tLeft, tTop - t.markerSide, t.markerSide, t.markerSide)
        canvas.rectangle(tRight - t.markerSide, tTop - t.markerSide, t.markerSide, t.markerSide)
        canvas.fillStroke()

        canvas.restoreState()
    }

    private fun drawMetaFields(canvas: PdfCanvas, targetArea: Rectangle, proximaNovaFont: PdfFont, t: EdustorPdfType) {
        val width = t.metaWidth * t.gridCellSide
        val height = t.metaHeight * t.gridCellSide

        canvas.saveState()
                .setLineWidth(0.5f)
                .setStrokeColor(Color.BLACK)
                .setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.MITER)

                .moveTo(targetArea.right - width, targetArea.top - height)
                .lineTo(targetArea.right - width, targetArea.top.toDouble())

                .moveTo(targetArea.right - width, targetArea.top - height)
                .lineTo(targetArea.right.toDouble(), targetArea.top - height)

                .stroke()
                .restoreState()
    }


    private fun drawRegularPageLabels(canvas: PdfCanvas,
                                      targetArea: Rectangle,
                                      proximaNovaFont: PdfFont,
                                      p: PdfGenParams) {
//            Print top row

        val t = p.type
        val titleRow = when {
            p.authorName != "" -> "${t.title}: ${p.authorName}"
            else -> t.title
        }
        val topRowY = targetArea.top.toDouble()
        val leftX = targetArea.left.toDouble()
        canvas.beginText()
                .setFontAndSize(proximaNovaFont, t.topFontSize)
                .moveText(leftX, topRowY)
                .showText(titleRow)
                .endText()

        val topRightString = when {
            p.subjectName != "" -> "${p.subjectName}, ${p.courseName}"
            else -> p.courseName
        }

        val topRightLabelSize = proximaNovaFont.getWidth(topRightString, t.topFontSize)
        val topRightX = targetArea.right.toDouble() - topRightLabelSize

        canvas.beginText()
                .moveText(topRightX, topRowY)
                .showText(topRightString)
                .endText()

        canvas.setFontAndSize(proximaNovaFont, t.bottomFontSize)

//            Print bottom row
        val bottomRowY = targetArea.bottom - t.bottomLabelMargin
        canvas.beginText()
                .moveText(leftX, bottomRowY)
                .showText("© ${p.copyrightString} ${p.academicYear}")
                .endText()

        val bottomRightLabelSize = proximaNovaFont.getWidth(p.contactsString, t.bottomFontSize)
        val bottomRightX = when (t.markersEnabled) {
            true -> targetArea.right - (t.markerSide + 3) - bottomRightLabelSize
            false -> targetArea.right.toDouble() - bottomRightLabelSize
        }
        canvas.beginText()
                .moveText(bottomRightX, bottomRowY)
                .showText(p.contactsString)
                .endText()
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
                .setStrokeColor(Color.GRAY)
                .setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.MITER)

        val t = p.type
        val gridBorders = Rectangle(t.gridStartX.toFloat(), t.gridStartY.toFloat(),
                (t.gridCellSide * t.gridXCells).toFloat(), (t.gridCellSide * t.gridYCells).toFloat())

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
