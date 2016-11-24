package ru.edustor.gen.internal

import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.kernel.color.Color
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants
import org.springframework.stereotype.Component
import ru.edustor.gen.util.drawGrid
import ru.edustor.gen.util.getGridBorders
import java.io.OutputStream

@Component
open class PdfGenerator {
    fun makePdf(outputStream: OutputStream, pageCount: Int) {
        val pdfWriter = PdfWriter(outputStream)
        val pdfDocument = PdfDocument(pdfWriter)

        val fontBytes = this.javaClass.getResource("/fonts/Proxima Nova Thin.ttf").readBytes()
        val proximaNovaFont = PdfFontFactory.createFont(fontBytes, PdfEncodings.IDENTITY_H, true, true)

        val LR_MARGIN = 15f
        val TOP_MARGIN = 15f
        val BOTTOM_MARGIN = 15f

        val ps = PageSize.A4

        val effectiveArea = ps.clone().applyMargins<Rectangle>(TOP_MARGIN, LR_MARGIN, BOTTOM_MARGIN, LR_MARGIN, false)

        val gridSquareSide = 14
        val allowedGridArea = effectiveArea.clone().applyMargins<Rectangle>(3f, 0f, 0f, 0f, false)
        val effectiveGridArea = getGridBorders(allowedGridArea, gridSquareSide.toFloat())

        for (i in 1..pageCount) {
            val page = pdfDocument.addNewPage(ps)
            val canvas = PdfCanvas(page)
                    .setLineWidth(0.1f)
                    .setStrokeColor(Color.GRAY)
                    .setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.MITER)

            canvas.drawGrid(effectiveGridArea, gridSquareSide)

            canvas.setFontAndSize(proximaNovaFont, 11f)
                    .beginText()
                    .moveText(effectiveGridArea.left.toDouble(), effectiveGridArea.top.toDouble() + 3)
                    .showText("Edustor Alpha")
                    .endText()
        }
//        canvas.concatMatrix(1.0, 0.0, 0.0, 1.0, 0.0, ps.height.toDouble())
        pdfDocument.close()
    }
}
