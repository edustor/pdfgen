package ru.edustor.gen.internal

import com.itextpdf.kernel.color.Color
import com.itextpdf.kernel.geom.PageSize
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

        val ps = PageSize.A4

        val gridSquareSide = 14
        val borders = getGridBorders(ps, gridSquareSide.toFloat(), 15f, 15f, 15f)

        for (i in 1..pageCount) {
            val page = pdfDocument.addNewPage(ps)
            val canvas = PdfCanvas(page)
                    .setLineWidth(0.1f)
                    .setColor(Color.GRAY, false)
                    .setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.MITER)

            canvas.drawGrid(borders, gridSquareSide)
        }
//        canvas.concatMatrix(1.0, 0.0, 0.0, 1.0, 0.0, ps.height.toDouble())
        pdfDocument.close()
    }
}
