package com.github.meixuesong.order.domain;

public enum OrderStatus {
    NEW(0),
    DISCARD(-1),
    PAID(10),
    DELIVERING(20),
    DELIVERED(30);

    private int value;

    OrderStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static OrderStatus from(int status) {
        for (OrderStatus s : OrderStatus.values()) {
            if (s.value == status) {
                return s;
            }
        }

        throw new IllegalArgumentException("Unknown order status: " + status);
    }
}
