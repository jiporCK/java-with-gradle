package co.istad.javagradle.product;

import co.istad.javagradle.controller.ProductController;
import co.istad.javagradle.utils.Utils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum ProductDB {

    INSTANCE;

    private final List<Product> products;

    ProductDB() {

        products = new ArrayList<>() {{
            add(new Product(Utils.generateUuid(), "Coca", "Best Drink ever", 0.02));
            add(new Product(Utils.generateUuid(), "Pepsi", "Another great drink", 1.4));
            add(new Product(Utils.generateUuid(), "Sprite", "Refreshing lemon-lime", 1.3));
            add(new Product(Utils.generateUuid(), "Fanta", "Fruity delight", 1.2));
        }};

    }

    public Product addProduct(Product product) {
        products.add(product);

        return product;
    }

}
