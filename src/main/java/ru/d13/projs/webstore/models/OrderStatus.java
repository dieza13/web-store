package ru.d13.projs.webstore.models;

public enum OrderStatus {
    completed(1),
    paid(2),
    delivered(3);
    int code;

    OrderStatus(Integer code) {
        this.code = code;
    }
}
