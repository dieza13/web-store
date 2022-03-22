package ru.d13.projs.webstore.models;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.Id;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderPosition {

    @Id
    Long        id;
    Integer     count;
    Product     product;

}
