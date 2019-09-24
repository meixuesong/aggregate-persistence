package com.github.meixuesong.order;

import com.github.meixuesong.common.Aggregate;
import com.github.meixuesong.order.dao.CustomerDO;
import com.github.meixuesong.order.dao.CustomerDOMapper;
import com.github.meixuesong.order.dao.OrderDO;
import com.github.meixuesong.order.dao.OrderDOMapper;
import com.github.meixuesong.order.dao.OrderItemDO;
import com.github.meixuesong.order.dao.OrderItemDOMapper;
import com.github.meixuesong.order.domain.Order;
import com.github.meixuesong.order.domain.OrderItem;
import com.github.meixuesong.order.domain.Product;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class OrderRepository {
    private OrderDOMapper orderMapper;
    private OrderItemDOMapper orderItemMapper;
    private ProductRepository productRepository;
    private CustomerDOMapper customerMapper;

    public OrderRepository(OrderDOMapper orderMapper, OrderItemDOMapper orderItemMapper, ProductRepository productRepository, CustomerDOMapper customerMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.productRepository = productRepository;
        this.customerMapper = customerMapper;
    }

    public Aggregate<Order> findById(String id) {
        OrderDO orderDO = orderMapper.selectByPrimaryKey(id);
        if (orderDO == null) {
            throw new EntityNotFoundException("Order(" + id + ") not found");
        }

        Order order = orderDO.toOrder();
        CustomerDO customerDO = customerMapper.selectByPrimaryKey(orderDO.getCustomerId());
        order.setCustomer(customerDO.toCustomer());
        order.setItems(getOrderItems(id));

        return new Aggregate<>(order);
    }

    private List<OrderItem> getOrderItems(String id) {
        List<OrderItemDO> itemDOs = orderItemMapper.selectByOrderId(id);
        List<String> prodIds = itemDOs.stream().map(i -> i.getProdId()).collect(Collectors.toList());
        Map<String, Product> productMap = productRepository.getProductMapByIds(prodIds);

        return itemDOs.stream()
                .map(itemDO ->
                        new OrderItem(itemDO.getId(), productMap.get(itemDO.getProdId()), itemDO.getAmount()))
                .collect(Collectors.toList());
    }

    public void save(Aggregate<Order> orderAggregate) {
        if (orderAggregate.isNew()) {
            Order order = orderAggregate.getRoot();
            order.increaseVersion();

            OrderDO orderDO = new OrderDO(order);
            orderMapper.insert(orderDO);

            List<OrderItemDO> itemDOs = order.getItems().stream().map(item -> new OrderItemDO(order.getId(), item)).collect(Collectors.toList());
            orderItemMapper.insertAll(itemDOs);
        }
    }

}
