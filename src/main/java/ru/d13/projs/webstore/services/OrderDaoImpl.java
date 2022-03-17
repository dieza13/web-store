package ru.d13.projs.webstore.services;

import org.springframework.stereotype.Service;
import ru.d13.projs.webstore.models.Order;

import java.util.List;
import java.util.Optional;

@Service
public class OrderDaoImpl implements OrderDao {

    private List<Order> orders = DataRepository.Orders();

    @Override
    public List<Order> getOrders() {
        return orders;
    }

    @Override
    public Order createOrder(Order order) {
        orders.add(order);
        return order;
    }

    @Override
    public void updateOrder(Order order) {
        Optional<Order> c = orders.stream().filter(ord->ord.getId().equals(order.getId())).findFirst();
        c.ifPresent(cons->{orders.remove(c);orders.add(order);});
    }

    @Override
    public Order getOrderByCode(String orderCode) {
        return orders.stream().filter(p->p.getCode().equals(orderCode)).findFirst().orElse(null);
    }
}
