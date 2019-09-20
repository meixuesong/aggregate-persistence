package com.github.meixuesong.order;

import com.github.meixuesong.common.Aggregate;
import com.github.meixuesong.order.domain.Order;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order findById(String id) {
        Aggregate<Order> orderAggregate = orderRepository.findById(id);

        return orderAggregate.getRoot();
    }

}
