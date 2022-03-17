package ru.d13.projs.webstore.models;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.Id;
import java.util.Date;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {

    @Id
    Long                    id;
    String                  code;
    Date                    orderDate;
    OrderStatus             status;
    Customer                customer;
    List<OrderPosition>     positions;

}
