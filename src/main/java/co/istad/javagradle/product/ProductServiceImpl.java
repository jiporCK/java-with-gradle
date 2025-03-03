package co.istad.javagradle.product;

import java.util.List;

public enum ProductServiceImpl implements ProductService {

    INSTANCE;

    private final ProductDB productDB = ProductDB.INSTANCE;

    @Override
    public List<Product> getProducts() {

        return productDB.getProducts();

    }

    @Override
    public Product addProduct(Product product) {
        return productDB.addProduct(product);
    }

}
