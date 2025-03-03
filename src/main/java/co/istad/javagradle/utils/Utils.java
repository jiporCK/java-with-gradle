package co.istad.javagradle.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;

public class Utils {

    public static String generateUuid() {

        String uuid = UUID.randomUUID().toString();

        String[] newUuid = uuid.split("-");

        return newUuid[0];

    }

    public static BufferedImage generateQRCode(String qrText, String filename) {

        int width = 300;
        int height = 300;

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        try {

            BitMatrix bitMatrix = qrCodeWriter.encode(qrText, BarcodeFormat.QR_CODE, width, height);

            // Save in Root Directory (Project Directory)
            String filePath = System.getProperty("user.dir") + "/" + (filename.isEmpty() ? (generateUuid() + ".png") : filename);
            Path path = FileSystems.getDefault().getPath(filePath);

            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
            System.out.println("QR Code saved at: " + filePath);

            return MatrixToImageWriter.toBufferedImage(bitMatrix);

        } catch (WriterException | IOException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

}
