package co.istad.javagradle;

import co.istad.javagradle.controller.ProductController;
import co.istad.javagradle.product.Product;
import co.istad.javagradle.utils.Utils;
import kh.gov.nbc.bakong_khqr.BakongKHQR;
import kh.gov.nbc.bakong_khqr.model.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

public class Main {

    private static final ProductController productController = new ProductController();

    public static void main(String[] args) {

        List<Product> products = productController.getAllProducts();

        products.forEach(System.out::println);

        products.forEach(product -> {

            if ("Coca".equals(product.getName())) {

                try {
                    IndividualInfo individualInfo = getIndividualInfo(product);

                    KHQRResponse<KHQRData> response = generateQrIndividual(individualInfo);

                    String result = String.format("""
                    response: %s
                    """, response.getData().getQr());

                    System.out.println(result);

                        BufferedImage image = Utils.generateQRCode(response.getData().getQr(), "qrcode.png");

                        displayImage(image);

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }

        });

    }

    static KHQRResponse<KHQRData> generateQrIndividual(IndividualInfo individualInfo) {
        // Generate KHQR for Individual
        KHQRResponse<KHQRData> response = BakongKHQR.generateIndividual(individualInfo);

        if (response.getKHQRStatus().getCode() == 0) {

            verifyQR(response.getData().getQr());

            System.out.println("QR Data: " + response.getData().getQr());
            System.out.println("MD5 Hash: " + response.getData().getMd5());
        } else {
            System.err.println("Error: " + response.getKHQRStatus().getMessage());
        }
        return response;
    }

    static IndividualInfo getIndividualInfo(Product product) {

        IndividualInfo individualInfo = new IndividualInfo();

        // Populate IndividualInfo
        individualInfo.setAccountInformation("500214565");
        individualInfo.setBakongAccountId("sreng_chipor@aclb");
        individualInfo.setAcquiringBank("ACLEDA");
        individualInfo.setCurrency(KHQRCurrency.valueOf("USD"));
        individualInfo.setAmount(product.getPrice());
        individualInfo.setMerchantName("ROS Cambodia");
        individualInfo.setMerchantCity("Phnom Penh");

        return individualInfo;

    }

    static void verifyQR(String qrCode) {
        try {
            // Verify KHQR Code
            KHQRResponse<CRCValidation> response = BakongKHQR.verify(qrCode);

            if (response.getKHQRStatus().getCode() == 0) {
                System.out.println("QR Code Valid: " + response.getData().isValid());
            } else {
                System.err.println("Error: " + response.getKHQRStatus().getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

//    static void saveUserRecord(MenuOrder menuOrder) {
//
//    }

    static void displayImage(BufferedImage image) {
        JFrame frame = new JFrame("QR Code");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set a larger frame size
        int frameWidth = 600;
        int frameHeight = 600;
        frame.setSize(frameWidth, frameHeight);

        // Create a panel with centered image
        JPanel panel = new JPanel(new GridBagLayout());
        JLabel label = new JLabel(new ImageIcon(image));
        panel.add(label);

        frame.add(panel);
        frame.setLocationRelativeTo(null); // Center the frame on screen
        frame.setVisible(true);
    }

    public static void renderFromBase64(String base64Image) {
        try {
            // Decode Base64 to byte array
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            // Convert to BufferedImage
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            BufferedImage image = ImageIO.read(bis);

            // Render in console
            renderImageInConsole(image);

        } catch (IOException e) {
            System.err.println("Error decoding Base64 image: " + e.getMessage());
        }
    }

    public static void renderImageInConsole(BufferedImage image) {
        for (int y = 0; y < image.getHeight(); y ++) { // Step 2 to combine two rows into one
            for (int x = 0; x < image.getWidth(); x ++) { // Step 2 for smaller width
                int topPixel = image.getRGB(x, y) & 0xFFFFFF;
                int bottomPixel = (y + 1 < image.getHeight()) ? image.getRGB(x, y + 1) & 0xFFFFFF : 0xFFFFFF;

                if (topPixel == 0 && bottomPixel == 0) {
                    System.out.print("█");  // Full block
                } else if (topPixel == 0) {
                    System.out.print("▀");  // Upper block filled
                } else if (bottomPixel == 0) {
                    System.out.print("▄");  // Lower block filled
                } else {
                    System.out.print(" ");  // Space for white pixels
                }
            }
            System.out.println();
        }
    }

}