package ru.d13.projs.webstore.endpoints;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.d13.projs.webstore.models.Product;
import ru.d13.projs.webstore.services.ProductDao;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import java.util.List;

@Component
@WebService(serviceName = "ProductWS")
@AllArgsConstructor
public class ProductWS {

    private ProductDao productDao;

    @WebMethod
    public List<Product> getProducts() {
        return productDao.getProducts();
    }

    @WebMethod
    public Product createProduct(@WebParam(name = "product")Product product) {
        return productDao.createProduct(product);
    }

    @WebMethod
    public void updateProduct(@WebParam(name = "product")Product product) {
        productDao.updateProduct(product);
    }

    @WebMethod
    public Product getProductByCode(@WebParam(name = "productCode") String productCode) {
        return productDao.getProductByCode(productCode);
    }
}
