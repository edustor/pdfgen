package ru.edustor.pdfgen.controller

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import ru.edustor.pdfgen.internal.PdfGenerator
import java.io.ByteArrayOutputStream

@Controller
class RootController(private val pdfGenerator: PdfGenerator) {
    @RequestMapping("/")
    fun root(): String {
        return "index"
    }

    @RequestMapping("/pdf")
    @ResponseBody
    fun pdf(@RequestParam author: String,
            @RequestParam subject: String,
            @RequestParam course: String,
            @RequestParam copyright: String,
            @RequestParam contacts: String,
            @RequestParam pagesCount: Int,
            @RequestParam(defaultValue = "false") cornell: Boolean,
            @RequestParam(defaultValue = "false") generateTitle: Boolean): HttpEntity<ByteArray> {
        val filename = when {
            subject != "" -> "$subject.pdf"
            else -> "edustor-paper.pdf"
        }

        val byteArrayOutputStream = ByteArrayOutputStream()
        pdfGenerator.makePdf(byteArrayOutputStream, filename, pagesCount, author, subject, course, copyright, contacts, cornell, generateTitle && subject != "")

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_PDF
        headers.setContentDispositionFormData("file", filename, Charsets.UTF_8)

        return HttpEntity(byteArrayOutputStream.toByteArray(), headers)

    }
}
