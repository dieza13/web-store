package ru.d13.projs.webstore.models;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.Id;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Customer {

    @Id
    Long        id;
    String      name;
    String      email;
    String      address;
    String      phone;

}
