package com.example.Feature;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.example.librabry_management.*;
import com.example.Controller.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class QRCodeGenerator {

    /**
     * @param bookData
     * @param filePath
     * Generated QR Code.
     */
        public static void generateQRCode(String bookData, String filePath) {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            try {
                BitMatrix bitMatrix = qrCodeWriter.encode(bookData, BarcodeFormat.QR_CODE, 300, 300);
                Path path = FileSystems.getDefault().getPath(filePath);
                MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
            } catch (WriterException | IOException e) {
                e.printStackTrace();
            }
        }
}
