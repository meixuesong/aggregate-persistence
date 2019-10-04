package com.github.meixuesong.order.api;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CheckoutRequest {
    private final String paymentType;
    private final BigDecimal amount;

    public CheckoutRequest(String paymentType, BigDecimal amount) {
        this.paymentType = paymentType;
        this.amount = amount;
    }
}
