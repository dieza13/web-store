package ru.d13.projs.webstore.services;

import ru.d13.projs.webstore.models.Customer;
import ru.d13.projs.webstore.models.Order;
import ru.d13.projs.webstore.models.OrderPosition;
import ru.d13.projs.webstore.models.Product;

import java.util.Date;
import java.util.List;

public class DataRepository {

    private static List<Product> products = List.of(
            Product.builder().code("КОД_НОЖ").insertDate(new Date()).name("Нож").price(13.13).build(),
            Product.builder().code("КОД_МАЧЕТЕ").insertDate(new Date()).name("Мачете").price(13.43).build(),
            Product.builder().code("КОД_ВЕРЕВКА").insertDate(new Date()).name("Веревка").price(14.13).build(),
            Product.builder().code("КОД_КОГТИ").insertDate(new Date()).name("Когти").price(43.13).build()
    );


    private static List<Customer> customers = List.of(
            Customer.builder().name("Фредди").phone("5-55-555").address("улица Вязов").id(1l).build(),
            Customer.builder().name("Джейсон").phone("3-33-333").address("Хрустальное озеро").id(2l).build(),
            Customer.builder().name("Чаки").phone("1-11-111").address("Чикаго").id(3l).build(),
            Customer.builder().name("Чужой").phone("2-22-222").address("Космос").id(4l).build()
    );

    private static List<Order> orders = List.of(
            Order.builder().orderDate(new Date()).code("111").id(1l).customer(customers.get(0))
                    .positions(List.of(OrderPosition.builder().id(1l).count(2).product(products.get(0)).build())).build(),
            Order.builder().orderDate(new Date()).code("222").id(2l).customer(customers.get(1))
                    .positions(List.of(OrderPosition.builder().id(2l).count(1).product(products.get(1)).build())).build(),
            Order.builder().orderDate(new Date()).code("333").id(3l).customer(customers.get(2))
                    .positions(List.of(OrderPosition.builder().id(3l).count(1).product(products.get(2)).build())).build(),
            Order.builder().orderDate(new Date()).code("444").id(4l).customer(customers.get(3))
                    .positions(List.of(OrderPosition.builder().id(4l).count(2).product(products.get(3)).build())).build()

    );

    public static List<Product> Products() {
        return products;
    }

    public static List<Order> Orders() {
        return orders;
    }

    public static List<Customer> Customers() {
        return customers;
    }

}
