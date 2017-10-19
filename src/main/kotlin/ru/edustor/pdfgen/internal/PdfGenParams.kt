package ru.edustor.pdfgen.internal

import com.itextpdf.kernel.geom.PageSize
import java.time.LocalDateTime
import java.time.Month
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
        val generateTitle: Boolean = true
) {
    val academicYear: String = let {
        val now = LocalDateTime.now(ZoneId.of("Europe/Moscow")).withNano(0)
        val academicYear = when {
            now.month > Month.JUNE -> "${now.year}-${now.year + 1}"
            else -> "${now.year - 1}-${now.year}"
        }
        return@let academicYear
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
        val markersEnabled: Boolean,
        val markerSide: Double
)

object EdustorPdfTypes {
    val PAPER = let {
        val title = "Edustor Paper"
        val pageSize = PageSize.A4
        val gridXCells = 40
        val gridYCells = 55
        val gridCellSide = 5 / 25.4 * 72
        val gridStartX = (pageSize.width - (gridXCells * gridCellSide)) / 2.0
        val gridStartY = 30.0
        return@let EdustorPdfType(title = title, pageSize = pageSize, gridStartX = gridStartX, gridStartY = gridStartY,
                gridCellSide = gridCellSide, gridXCells = gridXCells, gridYCells = gridYCells,
                topFontSize = 11f, bottomFontSize = 8f, markersEnabled = true, markerSide = gridCellSide)
    }
}