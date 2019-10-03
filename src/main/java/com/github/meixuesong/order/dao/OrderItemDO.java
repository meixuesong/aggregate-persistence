package com.github.meixuesong.order.dao;

import com.github.meixuesong.order.domain.OrderItem;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDO {
    private Long id;
    private String orderId;
    private String prodId;
    private BigDecimal amount = BigDecimal.ZERO;
    private BigDecimal subTotal = BigDecimal.ZERO;

    public OrderItemDO() {
    }

    public OrderItemDO(String orderId, OrderItem item) {
        id = item.getId();
        this.orderId = orderId;
        this.prodId = item.getProduct().getId();
        this.amount = item.getAmount();
        this.subTotal = item.getSubTotal();
    }
}
