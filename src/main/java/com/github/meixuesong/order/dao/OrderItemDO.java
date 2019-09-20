package com.github.meixuesong.order.dao;

import com.github.meixuesong.order.domain.OrderItem;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemDO {
    private Long id;
    private String orderId;
    private String prodId;
    private BigDecimal amount = BigDecimal.ZERO;
    private BigDecimal subTotal = BigDecimal.ZERO;

}
