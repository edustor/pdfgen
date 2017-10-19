package ru.edustor.pdfgen.internal

import com.itextpdf.kernel.geom.PageSize


data class PdfGenParams(
        val pageCount: Int,
        val authorName: String,
        val subjectName: String,
        val courseName: String,
        val copyrightString: String,
        val contactsString: String,
        val drawCornell: Boolean = true,
        val generateTitle: Boolean = true
)

data class EdustorPdfType(
        val pageSize: PageSize,

)