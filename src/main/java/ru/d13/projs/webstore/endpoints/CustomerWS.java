package ru.d13.projs.webstore.endpoints;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.d13.projs.webstore.models.Customer;
import ru.d13.projs.webstore.services.CustomerDao;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@Component
@WebService
@AllArgsConstructor
public class CustomerWS {

    private CustomerDao customerDao;

    @WebMethod
    public List<Customer> getCustomers() {
        return customerDao.getCustomers();
    }

    @WebMethod
    public Customer createCustomer(@WebParam(name = "customer")Customer customer) {
        return customerDao.createCustomer(customer);
    }

    @WebMethod
    public void updateCustomer(@WebParam(name = "customer")Customer customer) {
        customerDao.updateCustomer(customer);
    }

    @WebMethod
    public Customer getCustomerById(@WebParam(name = "customerId") Long customerId) {
        return customerDao.getCustomerById(customerId);
    }
}
