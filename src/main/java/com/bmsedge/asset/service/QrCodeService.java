package com.bmsedge.asset.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class QrCodeService {

    private static final Logger logger = LoggerFactory.getLogger(QrCodeService.class);

    @Value("${app.qr-code.size:300}")
    private int qrCodeSize;

    @Value("${app.qr-code.format:PNG}")
    private String imageFormat;

    /**
     * Generates a QR code image for the given data
     *
     * @param data The data to encode in the QR code
     * @return Byte array containing the QR code image
     * @throws Exception if QR code generation fails
     */
    public byte[] generateQr(String data) throws Exception {
        logger.debug("Generating QR code for data: {}", data);

        try {
            BitMatrix matrix = new MultiFormatWriter()
                    .encode(data, BarcodeFormat.QR_CODE, qrCodeSize, qrCodeSize);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, imageFormat, stream);

            byte[] qrCode = stream.toByteArray();
            logger.info("QR code generated successfully, size: {} bytes", qrCode.length);

            return qrCode;
        } catch (WriterException e) {
            logger.error("Failed to encode QR code data: {}", data, e);
            throw new Exception("Failed to encode QR code: " + e.getMessage(), e);
        } catch (IOException e) {
            logger.error("Failed to write QR code to stream", e);
            throw new Exception("Failed to generate QR code image: " + e.getMessage(), e);
        }
    }

    /**
     * Generates a QR code with custom size
     *
     * @param data The data to encode
     * @param width Custom width
     * @param height Custom height
     * @return Byte array containing the QR code image
     * @throws Exception if QR code generation fails
     */
    public byte[] generateQrWithCustomSize(String data, int width, int height) throws Exception {
        logger.debug("Generating custom QR code: {}x{} for data: {}", width, height, data);

        try {
            BitMatrix matrix = new MultiFormatWriter()
                    .encode(data, BarcodeFormat.QR_CODE, width, height);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, imageFormat, stream);

            return stream.toByteArray();
        } catch (WriterException | IOException e) {
            logger.error("Failed to generate custom QR code", e);
            throw new Exception("Failed to generate QR code: " + e.getMessage(), e);
        }
    }
}