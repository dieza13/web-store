package ru.d13.projs.webstore.endpoints;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.d13.projs.webstore.models.Order;
import ru.d13.projs.webstore.services.OrderDao;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@Component
@WebService(serviceName = "OrderWS")
@AllArgsConstructor
public class OrderWS {

    private OrderDao orderDao;

    @WebMethod
//    @PreAuthorize("hasRole('ORDER_MASTER')")
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
