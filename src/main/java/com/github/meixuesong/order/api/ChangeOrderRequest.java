package com.github.meixuesong.order.api;

import java.util.List;

public class ChangeOrderRequest {
    private final String orderId;
    private final String customerId;
    private final List<OrderItemRequest> items;

    public ChangeOrderRequest(String orderId, String customerId, List<OrderItemRequest> items) {

        this.orderId = orderId;
        this.customerId = customerId;
        this.items = items;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }
}
