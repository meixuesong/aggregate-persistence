package com.github.meixuesong.order.api;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemRequest {
    private String productId;
    private BigDecimal amount;

    public OrderItemRequest() {
    }

    public OrderItemRequest(String productId, BigDecimal amount) {
        this.productId = productId;
        this.amount = amount;
    }

    public String getProductId() {
        return productId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

}
