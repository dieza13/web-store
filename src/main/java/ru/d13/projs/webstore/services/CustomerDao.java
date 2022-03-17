package ru.d13.projs.webstore.services;

import ru.d13.projs.webstore.models.Customer;

import java.util.List;

public interface CustomerDao {

    List<Customer> getCustomers();

    Customer createCustomer(Customer customer);

    void updateCustomer(Customer customer);

    Customer getCustomerById(Long customerId);

}
