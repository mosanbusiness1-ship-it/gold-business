package com.mo.core.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

@Service
public class QrCodeGeneratorService {

    /**
     * Génère un QR Code à partir d'un texte donné (ex: URI OTPAUTH ou lien).
     * @param text Le contenu du QR Code (URI, URL, etc.)
     * @param width largeur en pixels
     * @param height hauteur en pixels
     * @return tableau de bytes (image PNG)
     * @throws Exception en cas d'erreur de génération
     */
    public byte[] generateQrCode(String text, int width, int height) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 1); // marges réduites

        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        ByteArrayOutputStream pngOutput = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", pngOutput);
        return pngOutput.toByteArray();
    }
}