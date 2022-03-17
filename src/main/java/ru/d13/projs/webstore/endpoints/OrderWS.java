package ru.d13.projs.webstore.endpoints;

import org.springframework.security.access.prepost.PreAuthorize;
import ru.d13.projs.webstore.models.Order;
import ru.d13.projs.webstore.services.OrderDao;
import ru.d13.projs.webstore.services.OrderDaoImpl;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@WebService(serviceName = "OrderWS")
public class OrderWS {

    private OrderDao orderDao;

    public OrderWS() {
        orderDao = new OrderDaoImpl();
    }

    @WebMethod
    @PreAuthorize("hasRole('ORDER_MASTER')")
    public List<Order> getOrders() {
        return orderDao.getOrders();
    }

    @WebMethod
    public Order createOrder(@WebParam(name = "order") Order order) {
        return orderDao.createOrder(order);
    }

    @WebMethod
    public void updateOrder(@WebParam(name = "order") Order order) {
        orderDao.updateOrder(order);
    }

    @WebMethod
    public Order getOrderByCode(@WebParam(name = "order_code") String orderCode) {
        return orderDao.getOrderByCode(orderCode);
    }

}
