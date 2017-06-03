package ru.edustor.pdfgen.internal

import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
open class EdustorPageIdGenerator {
    fun getNewPageId(): PageId {

        val now = LocalDateTime.now()
        val pageUid = "00000000"
        val pageId = now.format(DateTimeFormatter.ofPattern("yyMMddHHmmss")) + pageUid

        return PageId(pageId)
    }

    data class PageId(val id: String) {
        val humanReadableId = let {
            val groupsCount = id.length / 4

            var src = id
            val groups = mutableListOf<String>()

            while (src.isNotEmpty()) {
                groups.add(src.take(4))
                src = src.drop(4)
            }

            return@let groups.joinToString(" ")
        }
    }

}