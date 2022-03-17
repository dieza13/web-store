package ru.d13.projs.webstore.services;

import ru.d13.projs.webstore.models.Order;

import java.util.List;

public interface OrderDao {



    List<Order> getOrders();

    Order createOrder(Order order);

    void updateOrder(Order order);

    Order getOrderByCode(String orderCode);

}
