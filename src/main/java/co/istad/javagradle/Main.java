package co.istad.javagradle;

import co.istad.javagradle.controller.ProductController;
import co.istad.javagradle.product.Product;
import co.istad.javagradle.product.ProductDB;
import co.istad.javagradle.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kh.gov.nbc.bakong_khqr.BakongKHQR;
import kh.gov.nbc.bakong_khqr.model.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final ProductController productController = new ProductController();

    private static final ProductDB productDb = ProductDB.INSTANCE;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("""
                ###### WELCOME TO PRODUCT ######
                1). Show Menu
                2). Add new product
                """);

        int op = getValidOption(scanner, 1, 3);

        switch (op) {

            case 1 -> showProducts();
            case 2 -> showProductInput(scanner);
            case 3 -> {
                System.out.println("Existing");
            }
        }

    }

    private static int getValidOption(Scanner scanner, int min, int max) {
        int option;
        while (true) {
            System.out.print("Enter number: ");
            
            if (!scanner.hasNext()) {  // Check if input is available
                System.out.println("No input detected. Exiting...");
                return -1; // Or handle this case differently
            }
            
            if (scanner.hasNextInt()) {
                option = scanner.nextInt();
                if (option >= min && option <= max) {
                    return option;
                }
            } else {
                scanner.next(); // Clear invalid input
            }
            
            System.out.println("Invalid input. Please enter a number between " + min + " and " + max + ".");
        }
    }    

    static void showProducts() {
        List<Product> products = productController.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("No products found");
        } else {
            products.forEach(System.out::println);

            generateQrForCoca(products);
        }
    }

    static void validateMd5(Md5Request md5) {

        final String API_URL = "https://api-bakong.nbc.gov.kh/v1/check_transaction_by_md5";
        final String AUTH_TOKEN = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkYXRhIjp7ImlkIjoiYzIwOWMwNDYzNjBlNDEwMSJ9LCJpYXQiOjE3NDA5Njg2MzksImV4cCI6MTc0ODc0NDYzOX0.Xv_eSRBagCkTg_WEDAP89WluTHr6W4acUu7Sn_oTfbY";

        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(md5);

            int maxAttempts = 5;
            int attempt = 0;
            int interval = 5; // seconds

            while (attempt < maxAttempts) {
                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .uri(URI.create(API_URL))
                        .header("Authorization", AUTH_TOKEN)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    JsonNode jsonResponse = objectMapper.readTree(response.body());
                    int responseCode = jsonResponse.path("responseCode").asInt();
                    String responseMessage = jsonResponse.path("responseMessage").asText();

                    if (responseCode == 0) {
                        System.out.println("✅ Success: " + responseMessage);
                        System.out.println("🔹 Data: " + jsonResponse.path("data").toPrettyString());
                        return;
                    } else {
                        System.out.println("⚠️ Retry " + (attempt + 1) + "/" + maxAttempts + " - " + responseMessage);
                    }
                } else {
                    System.out.println("❌ API Error: " + response.statusCode() + " - " + response.body());
                }

                attempt++;
                TimeUnit.SECONDS.sleep(interval); // Wait before retrying
            }

            System.out.println("⏳ Transaction validation failed after multiple attempts.");
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
        }

    }

    static void generateQrForCoca(List<Product> products) {
        products.forEach(product -> {

            if ("Coca".equals(product.getName())) {

                try {
                    IndividualInfo individualInfo = getIndividualInfo(product);

                    KHQRResponse<KHQRData> response = generateQrIndividual(individualInfo);

                    String result = String.format("""
                    response: %s
                    """, response.getData().getQr());

                    System.out.println(result);

                    BufferedImage image = Utils.generateQRCode(response.getData().getQr(), Utils.generateUuid()+ ".png");

                    displayImage(image);
                    Md5Request md5Request = new Md5Request(response.getData().getMd5());

                    validateMd5(md5Request);

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }

        });
    }

    static void showProductInput(Scanner scanner) {
        Product product = new Product();

        System.out.print("Enter product name: ");
        String name = scanner.nextLine();
        System.out.print("Enter product description: ");
        String description = scanner.nextLine();
        System.out.println("Enter product price: ");
        double price = scanner.nextDouble();

        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);

        productDb.addProduct(product);

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