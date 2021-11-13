package ru.edustor.pdfgen.internal

import com.itextpdf.kernel.geom.PageSize
import java.time.LocalDateTime
import java.time.ZoneId


data class PdfGenParams(
        val type: EdustorPdfType,
        val pageCount: Int,
        val authorName: String,
        val subjectName: String,
        val courseName: String,
        val copyrightString: String,
        val contactsString: String,
        val drawCornell: Boolean = true,
        val generateTitle: Boolean = true,
        val markersEnabled: Boolean = false
) {
    val copyrightYears: String = let {
        val now = LocalDateTime.now(ZoneId.of("Europe/Moscow"))
        return@let "2016-${now.year}"
    }
}

data class EdustorPdfType(
        val title: String,
        val pageSize: PageSize,
        val gridStartX: Double,
        val gridStartY: Double,
        val gridXCells: Int,
        val gridYCells: Int,
        val gridCellSide: Double,
        val topFontSize: Float,
        val bottomFontSize: Float,
        val bottomLabelMargin: Double,
        val markerSize: Double = 0.0,
        val metaWidth: Int = 8,
        val metaHeight: Int = 2
)

object EdustorPdfTypes {
    val PAPER: EdustorPdfType = let {
        val pageSize = PageSize.A4
        val gridXCells = 40
        val gridCellSide = 5 / 25.4 * 72
        val gridStartX = (pageSize.width - (gridXCells * gridCellSide)) / 2.0
        EdustorPdfType(
                title = "Edustor Paper",
                pageSize = pageSize,
                gridStartX = gridStartX,
                gridStartY = 23.0,
                gridCellSide = gridCellSide,
                gridXCells = gridXCells,
                gridYCells = 56,
                topFontSize = 11f,
                bottomFontSize = 8f,
                bottomLabelMargin = 0.0,
                markerSize = gridCellSide * 0.4
        )
    }

    val DIGITAL: EdustorPdfType = let {
        val pageSize = PageSize.A4
        val gridXCells = 40
        val gridCellSide = 5 / 25.4 * 72
        val gridStartX = (pageSize.width - (gridXCells * gridCellSide)) / 2.0
        EdustorPdfType(
                title = "Edustor Digital",
                pageSize = pageSize,
                gridStartX = gridStartX,
                gridStartY = 23.0,
                gridCellSide = gridCellSide,
                gridXCells = gridXCells,
                gridYCells = 56,
                topFontSize = 11f,
                bottomFontSize = 8f,
                bottomLabelMargin = 0.0
        )
    }
}