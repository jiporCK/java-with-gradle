package co.istad.javagradle.controller;

import co.istad.javagradle.product.Product;
import co.istad.javagradle.product.ProductService;
import co.istad.javagradle.product.ProductServiceImpl;

import java.util.List;

public class ProductController {

    private final ProductService productService = ProductServiceImpl.INSTANCE;

    public List<Product> getAllProducts() {

        return productService.getProducts();

    }

    public Product addProduct(Product product) {

        return productService.addProduct(product);

    }

}
