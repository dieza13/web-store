package ru.d13.projs.webstore.services;

import org.springframework.stereotype.Service;
import ru.d13.projs.webstore.models.Product;

import java.util.List;
import java.util.Optional;

@Service
public class ProductDaoImpl implements ProductDao {

    private List<Product> products = DataRepository.Products();

    @Override
    public List<Product> getProducts() {
        return products;
    }

    @Override
    public Product createProduct(Product product) {
        products.add(product);
        return product;
    }

    @Override
    public void updateProduct(Product product) {
        Optional<Product> c = products.stream().filter(prod->prod.getCode().equals(product.getCode())).findFirst();
        c.ifPresent(cons->{products.remove(c);products.add(product);});
    }

    @Override
    public Product getProductByCode(String productCode) {
        return products.stream().filter(p->p.getCode().equals(productCode)).findFirst().orElse(null);
    }
}
