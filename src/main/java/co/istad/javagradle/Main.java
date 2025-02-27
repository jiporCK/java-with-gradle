package co.istad.javagradle;


import co.istad.javagradle.controller.ProductController;
import co.istad.javagradle.product.Product;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final ProductController productController = new ProductController();

    public static void main(String[] args) {

        List<Product> products = productController.getAllProducts();

        products.forEach(System.out::println);

    }

}