package ru.edustor.pdfgen.util

import java.util.*

class EdustorPageId(val uuid: String = UUID.randomUUID().toString()) {
    private val shortId: String = uuid.split("-").last()

    val humanReadableId = "#${shortId.substring(0, 4)}-${shortId.substring(4, 8)}-${shortId.substring(8, 12)}"
    val shortHumanReadableId = "#${shortId.substring(8, 12)}"
    val qrURI = "edustor://d/$uuid"
}