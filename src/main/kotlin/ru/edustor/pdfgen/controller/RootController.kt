package ru.edustor.pdfgen.controller

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import ru.edustor.pdfgen.internal.EdustorPdfTypes
import ru.edustor.pdfgen.internal.PdfGenParams
import ru.edustor.pdfgen.internal.PdfGenerator
import java.io.ByteArrayOutputStream

@Controller
class RootController(private val pdfGenerator: PdfGenerator) {

    @GetMapping("/")
    fun root(): String {
        return "index"
    }

    @GetMapping("/pdf")
    @ResponseBody
    fun pdf(@RequestParam author: String,
            @RequestParam subject: String,
            @RequestParam course: String,
            @RequestParam copyright: String,
            @RequestParam contacts: String,
            @RequestParam pagesCount: Int,
            @RequestParam(defaultValue = "false") cornell: Boolean,
            @RequestParam(defaultValue = "false") markers: Boolean,
            @RequestParam(defaultValue = "false") generateTitle: Boolean): HttpEntity<ByteArray> {
        val (filename, type, contentDispositionEnabled) = when {
            subject != "" -> Triple("$subject.pdf", EdustorPdfTypes.DIGITAL, true)
            else -> Triple("edustor-paper.pdf", EdustorPdfTypes.PAPER, false)
        }

        val byteArrayOutputStream = ByteArrayOutputStream()

        val params = PdfGenParams(
                type = type,
                pageCount = pagesCount,
                authorName = author,
                subjectName = subject,
                courseName = course,
                copyrightString = copyright,
                contactsString = contacts,
                drawCornell = cornell,
                generateTitle = generateTitle && subject != "",
                markersEnabled = markers
        )
        pdfGenerator.makePdf(byteArrayOutputStream, filename, params)

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_PDF
        if (contentDispositionEnabled) {
            headers.setContentDispositionFormData("file", filename)
        }

        return HttpEntity(byteArrayOutputStream.toByteArray(), headers)

    }
}
