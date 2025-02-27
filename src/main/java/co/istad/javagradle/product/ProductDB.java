package co.istad.javagradle.product;

import co.istad.javagradle.utils.Utils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ProductDB {

    List<Product> products = new ArrayList<>() {{
        add(new Product(Utils.generateUuid(), "Coca", "Best Drink ever", 1.5));
        add(new Product(Utils.generateUuid(), "Pepsi", "Another great drink", 1.4));
        add(new Product(Utils.generateUuid(), "Sprite", "Refreshing lemon-lime", 1.3));
        add(new Product(Utils.generateUuid(), "Fanta", "Fruity delight", 1.2));
    }};


}
