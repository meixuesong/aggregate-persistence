package com.github.meixuesong.order.dao;

import com.github.meixuesong.order.domain.Order;
import com.github.meixuesong.order.domain.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class OrderDO {
    private String id;
    private Date createTime;
    private String customerId;
    private int status;
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private BigDecimal totalPayment = BigDecimal.ZERO;
    private int version;

    public Order toOrder() {
        Order order = new Order();
        order.setId(getId());
        order.setCreateTime(getCreateTime());
        order.setVersion(getVersion());
        order.setTotalPayment(getTotalPrice());
        order.setTotalPrice(getTotalPrice());
        order.setStatus(OrderStatus.from(getStatus()));

        return order;
    }
}
