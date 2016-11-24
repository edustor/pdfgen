package ru.edustor.gen

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.PropertySource

@SpringBootApplication
@PropertySource("classpath:build.properties")
open class EdustorPdfGenApplication(
        @Value("\${version}") val version: String
)