package com.github.meixuesong.order;

import com.github.meixuesong.common.Aggregate;
import com.github.meixuesong.order.domain.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private OrderRepository orderRepository;
    private OrderFactory factory;

    public OrderService(OrderRepository orderRepository, OrderFactory factory) {
        this.orderRepository = orderRepository;
        this.factory = factory;
    }

    public Order findById(String id) {
        Aggregate<Order> orderAggregate = orderRepository.findById(id);

        return orderAggregate.getRoot();
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        Order order = factory.createOrder(request);

        orderRepository.save(new Aggregate<>(order));

        Aggregate<Order> orderAggregate = orderRepository.findById(order.getId());
        return orderAggregate.getRoot();
    }


}
