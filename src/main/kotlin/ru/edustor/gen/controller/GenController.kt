package ru.edustor.gen.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class GenController {
    @RequestMapping("/")
    fun root(): String {
        return "Hello world"
    }
}
