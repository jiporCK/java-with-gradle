package co.istad.javagradle.controller;

import co.istad.javagradle.product.Product;
import co.istad.javagradle.product.ProductService;
import co.istad.javagradle.product.ProductServiceImpl;

import java.util.List;

public class ProductController {

    private final ProductService productService = new ProductServiceImpl();

    public List<Product> getAllProducts() {

        return productService.getProducts();

    }

}
