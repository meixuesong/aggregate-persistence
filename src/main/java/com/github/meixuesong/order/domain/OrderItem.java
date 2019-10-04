package com.github.meixuesong.order.domain;

import com.github.meixuesong.product.Product;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class OrderItem {
    private Long id;
    private Product product;
    private BigDecimal amount;
    private BigDecimal subTotal;

    public OrderItem() {
    }

    public OrderItem(Long id, Product product, BigDecimal amount) {
        if (product == null) {
            throw new IllegalArgumentException("product is null");
        }

        this.id = id;
        this.product = product;
        this.amount = amount;
        this.subTotal = product.getPrice().multiply(amount).setScale(2, RoundingMode.HALF_UP);
    }

}
