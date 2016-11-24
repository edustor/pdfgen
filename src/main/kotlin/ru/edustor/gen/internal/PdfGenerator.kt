package ru.edustor.gen.internal

import com.itextpdf.kernel.color.DeviceRgb
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants
import org.springframework.stereotype.Component
import ru.edustor.gen.util.getGridBorders
import java.io.OutputStream

@Component
open class PdfGenerator {
    fun makePdf(outputStream: OutputStream, pageCount: Int) {
        val pdfWriter = PdfWriter(outputStream)
        val pdfDocument = PdfDocument(pdfWriter)

        val ps = PageSize.A4
        val page = pdfDocument.addNewPage(ps)
        val canvas = PdfCanvas(page)
                .setLineWidth(1f)
                .setColor(DeviceRgb(128, 128, 128), false)
                .setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.ROUND)

        val borders = getGridBorders(ps, 15f, 15f, 15f, 15f)

        val xLines = (borders.left.toInt()..borders.right.toInt() step 15).map { it + borders.left.mod(1) }
        val yLines = (borders.bottom.toInt()..borders.top.toInt() step 15).map { it + borders.top.mod(1) }

        for (x in xLines) {
            canvas.moveTo(x.toDouble(), borders.top.toDouble())
                    .lineTo(x.toDouble(), borders.bottom.toDouble())
                    .stroke()
        }

        for (y in yLines) {
            canvas.moveTo(borders.left.toDouble(),y.toDouble())
                    .lineTo(borders.right.toDouble(), y.toDouble())
                    .stroke()
        }

//        canvas.concatMatrix(1.0, 0.0, 0.0, 1.0, 0.0, ps.height.toDouble())

        pdfDocument.close()
    }
}
