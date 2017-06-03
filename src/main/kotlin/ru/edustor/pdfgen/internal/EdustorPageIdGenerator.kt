package ru.edustor.pdfgen.internal

import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
open class EdustorPageIdGenerator {

    private var lastEpochSecond = 0L
    private var lastPageUid = 0

    fun getNewPageId(): PageId {

        val now = LocalDateTime.now()
        val pageUid = getPageUid()
        val pageId = now.format(DateTimeFormatter.ofPattern("yyMMddHHmmss")) + pageUid

        return PageId(pageId)
    }

    private fun getPageUid(): String {
        val epochSecond = Instant.now().epochSecond

        if (epochSecond != lastEpochSecond) {
            lastEpochSecond = epochSecond
            lastPageUid = 0
        }

        val result = lastPageUid++.toString().padStart(8, '0')
        return result
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