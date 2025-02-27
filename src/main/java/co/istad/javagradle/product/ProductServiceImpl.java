package co.istad.javagradle.product;

import java.util.List;

public class ProductServiceImpl implements ProductService {

    private final ProductDB productDB = new ProductDB();

    public ProductServiceImpl() {
    }

    @Override
    public List<Product> getProducts() {
        return productDB.getProducts();
    }

}
