package ru.edustor.pdfgen.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.edustor.pdfgen.internal.PdfGenerator
import javax.servlet.http.HttpServletResponse

@RestController
class RootController(val pdfGenerator: PdfGenerator) {
    @RequestMapping("/")
    fun root(resp: HttpServletResponse) {
        resp.setHeader("Content-Type", "application/pdf")
        pdfGenerator.makePdf(resp.outputStream)
    }
}
