package ru.d13.projs.webstore.services;

import org.springframework.stereotype.Service;
import ru.d13.projs.webstore.models.Customer;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CustomerDaoImpl implements CustomerDao {

    private List<Customer> customers = DataRepository.Customers();

    private Long nextVal() {
        return customers.stream().max(Comparator.comparingLong(Customer::getId)).orElse(Customer.builder().id(1L).build()).getId();
    }

//    @Override
    public List<Customer> getCustomers() {
        return customers;
    }

    @Override
    public Customer createCustomer(Customer customer) {
        customer.setId(nextVal());
        customers.add(customer);
        return customer;
    }

    @Override
    public void updateCustomer(Customer customer) {
        Optional c = customers.stream().filter(cust-> Objects.equals(cust.getId(), customer.getId())).findFirst();
        c.ifPresent(cons->{customers.remove(c);customers.add(customer);});
    }

    @Override
    public Customer getCustomerById(Long customerId) {
        return customers.stream().filter(c-> Objects.equals(c.getId(), customerId)).findFirst().orElse(null);
    }
}
