package ru.edustor.pdfgen

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class EdustorPdfGenApplication

fun main(args: Array<String>) {
    SpringApplication.run(EdustorPdfGenApplication::class.java, *args)
}
