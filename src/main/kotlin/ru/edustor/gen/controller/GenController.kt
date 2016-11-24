package ru.edustor.gen.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.edustor.gen.internal.PdfGenerator
import java.io.OutputStream

@RestController
class GenController(val pdfGenerator: PdfGenerator) {
    @RequestMapping("/")
    fun root(outStream: OutputStream) {
        pdfGenerator.makePdf(outStream, 10)
    }
}
