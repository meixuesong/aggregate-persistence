package com.github.meixuesong.order;

import com.github.meixuesong.common.Versionable;
import com.github.meixuesong.order.domain.Order;
import com.github.meixuesong.order.domain.OrderItem;
import com.github.meixuesong.order.domain.OrderStatus;
import com.github.meixuesong.order.domain.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class OrderFactory {
    private OrderIdGenerator idGenerator;
    private MemberRepository memberRepository;
    private ProductRepository productRepository;

    public OrderFactory(OrderIdGenerator idGenerator, MemberRepository memberRepository, ProductRepository productRepository) {
        this.idGenerator = idGenerator;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
    }

    public Order createOrder(CreateOrderRequest request) {
        return getOrder(request);
    }

    private Order getOrder(CreateOrderRequest request) {
        Order order = new Order();

        order.setId(idGenerator.generateId());
        order.setCreateTime(request.getCreateTime());
        order.setCustomer(memberRepository.findById(request.getCustomerId()));
        order.setItems(getOrderItems(request));
        order.setVersion(Versionable.NEW_VERSION);
        order.setStatus(OrderStatus.NEW);

        return order;
    }

    private List<OrderItem> getOrderItems(CreateOrderRequest request) {
        List<String> productIds = request.getItems().stream().map(item -> item.getProductId()).collect(Collectors.toList());
        Map<String, Product> productMap = productRepository.getProductMapByIds(productIds);

        return request.getItems().stream()
                    .map(
                            item -> new OrderItem(null, productMap.get(item.getProductId()), item.getAmount()))
                    .collect(Collectors.toList());
    }
}
