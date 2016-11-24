package ru.edustor.gen.internal

import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import org.springframework.stereotype.Component
import java.io.OutputStream

@Component
open class PdfGenerator {
    fun makePdf(outputStream: OutputStream, pageCount: Int) {
        val pdfWriter = PdfWriter(outputStream)
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument)

        val helloWorld = Paragraph("Hello world")
        document.add(helloWorld)

        document.close()
    }
}
