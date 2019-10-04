package com.github.meixuesong.order.dao;

import com.github.meixuesong.order.domain.Order;
import com.github.meixuesong.order.domain.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderDO {
    private String id;
    private Date createTime;
    private String customerId;
    private int status;
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private BigDecimal totalPayment = BigDecimal.ZERO;
    private int version;

    public OrderDO() {
    }

    public Order toOrder() {
        Order order = new Order();
        order.setId(getId());
        order.setCreateTime(getCreateTime());
        order.setVersion(getVersion());
        order.setTotalPayment(getTotalPayment());
        order.setTotalPrice(getTotalPrice());
        order.setStatus(OrderStatus.from(getStatus()));

        return order;
    }

    public OrderDO(Order order) {
        setId(order.getId());
        setCreateTime(order.getCreateTime());
        setCustomerId(order.getCustomer().getId());
        setStatus(order.getStatus().getValue());
        setTotalPayment(order.getTotalPayment());
        setTotalPrice(order.getTotalPrice());
        setVersion(order.getVersion());
    }

}
