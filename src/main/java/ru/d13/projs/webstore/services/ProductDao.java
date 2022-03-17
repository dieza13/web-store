package ru.d13.projs.webstore.services;

import ru.d13.projs.webstore.models.Product;

import java.util.List;

public interface ProductDao {

    List<Product> getProducts();

    Product createProduct(Product product);

    void updateProduct(Product product);

    Product getProductByCode(String productCode);

}
