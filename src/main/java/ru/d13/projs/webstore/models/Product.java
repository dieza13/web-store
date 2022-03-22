package ru.d13.projs.webstore.models;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.Id;
import java.util.Date;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {

    @Id
    String      code;
    String      name;
    double      price;
    Date        insertDate;

}
