package ru.edustor.gen.controller

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.edustor.gen.internal.PdfGenerator
import javax.servlet.http.HttpServletResponse

@RestController
class GenController(val pdfGenerator: PdfGenerator) {
    @RequestMapping("/", "/{pageCount}")
    fun root(resp: HttpServletResponse, @PathVariable(required = false) pageCount: Int?) {
        resp.setHeader("Content-Type", "application/pdf")
        pdfGenerator.makePdf(resp.outputStream, pageCount ?: 1)
    }
}
