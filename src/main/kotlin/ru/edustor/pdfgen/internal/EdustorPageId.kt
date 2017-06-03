package ru.edustor.pdfgen.internal

class EdustorPageId(val id: String = "1703225501234567") {
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