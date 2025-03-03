package co.istad.javagradle.product;

import java.util.List;

public interface ProductService {

    List<Product> getProducts();

    Product addProduct(Product product);

}
