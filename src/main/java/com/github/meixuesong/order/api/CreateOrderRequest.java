package com.github.meixuesong.order.api;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CreateOrderRequest {
    private Date createTime;
    private String customerId;
    private List<OrderItemRequest> items;

    public CreateOrderRequest() {
    }

    public CreateOrderRequest(Date createTime, String customerId, List<OrderItemRequest> items) {
        this.createTime = createTime;
        this.customerId = customerId;
        this.items = items;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

}
