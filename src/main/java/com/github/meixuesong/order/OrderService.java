package com.github.meixuesong.order;

import com.github.meixuesong.common.Aggregate;
import com.github.meixuesong.order.api.ChangeOrderRequest;
import com.github.meixuesong.order.api.CheckoutRequest;
import com.github.meixuesong.order.api.CreateOrderRequest;
import com.github.meixuesong.order.domain.Order;
import com.github.meixuesong.order.domain.Payment;
import com.github.meixuesong.order.domain.PaymentType;
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
        Aggregate<Order> aggregate = factory.createOrder(request);

        orderRepository.save(aggregate);

        Aggregate<Order> orderAggregate = orderRepository.findById(aggregate.getRoot().getId());
        return orderAggregate.getRoot();
    }

    @Transactional
    public Order updateOrder(ChangeOrderRequest request) {
        Aggregate<Order> aggregate = factory.getOrder(request);

        orderRepository.save(aggregate);

        Aggregate<Order> orderAggregate = orderRepository.findById(request.getOrderId());
        return orderAggregate.getRoot();
    }

    @Transactional
    public void discardOrder(String orderId) {
        Aggregate<Order> aggregate = orderRepository.findById(orderId);
        aggregate.getRoot().discard();

        orderRepository.remove(aggregate);
    }

    @Transactional
    public void checkout(String orderId, CheckoutRequest request) {
        Aggregate<Order> aggregate = orderRepository.findById(orderId);
        Order order = aggregate.getRoot();

        Payment payment = new Payment(PaymentType.from(request.getPaymentType()), request.getAmount());
        order.checkout(payment);

        orderRepository.save(aggregate);
    }
}
