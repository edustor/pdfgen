package ru.edustor.pdfgen.internal

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

fun makeQR(content: String): BufferedImage {
    val bitMatrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, 300, 300, mapOf(
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.L,
            EncodeHintType.MARGIN to 0
    ));
    val bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix)
    return bufferedImage
}

fun Image.getAsByteArray(): ByteArray {
    val outputStream = ByteArrayOutputStream()
    ImageIO.setUseCache(false)
    ImageIO.write(this as BufferedImage, "png", outputStream)
    return outputStream.toByteArray()
}