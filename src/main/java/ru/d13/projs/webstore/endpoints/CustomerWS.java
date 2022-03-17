package ru.d13.projs.webstore.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import ru.d13.projs.webstore.models.Customer;
import ru.d13.projs.webstore.services.CustomerDao;
import ru.d13.projs.webstore.services.CustomerDaoImpl;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import java.util.List;

@Service
@WebService
public class CustomerWS extends SpringBeanAutowiringSupport {

    public CustomerWS() {
        customerDao = new CustomerDaoImpl();
    }

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
