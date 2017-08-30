package ru.edustor.pdfgen.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import ru.edustor.pdfgen.internal.PdfGenerator
import javax.servlet.http.HttpServletResponse

@Controller
class RootController(private val pdfGenerator: PdfGenerator) {
    @RequestMapping("/")
    fun root(): String {
        return "index"
    }

    @RequestMapping("/pdf")
    fun pdf(resp: HttpServletResponse,
            @RequestParam author: String,
            @RequestParam subject: String,
            @RequestParam course: String,
            @RequestParam copyright: String,
            @RequestParam contacts: String,
            @RequestParam(defaultValue = "false") cornell: Boolean) {
        resp.setHeader("Content-Type", "application/pdf")
        val filename = when {
            subject != "" -> "edustor-$subject.pdf"
            else -> "edustor-unnamed.pdf"
        }
        resp.setHeader("Content-Disposition", """inline; filename="$filename"""")
        pdfGenerator.makePdf(resp.outputStream, author, subject, course, copyright, contacts, cornell)
    }
}
