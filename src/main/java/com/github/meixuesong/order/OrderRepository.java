package com.github.meixuesong.order;

import com.github.meixuesong.common.Aggregate;
import com.github.meixuesong.customer.CustomerDO;
import com.github.meixuesong.customer.CustomerDOMapper;
import com.github.meixuesong.order.dao.OrderDO;
import com.github.meixuesong.order.dao.OrderDOMapper;
import com.github.meixuesong.order.dao.OrderItemDO;
import com.github.meixuesong.order.dao.OrderItemDOMapper;
import com.github.meixuesong.order.domain.Order;
import com.github.meixuesong.order.domain.OrderItem;
import com.github.meixuesong.product.Product;
import com.github.meixuesong.product.ProductRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;
import javax.persistence.OptimisticLockException;
import java.util.Collection;
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

    public void save(Aggregate<Order> orderAggregate) {
        if (orderAggregate.isNew()) {
            insertNewAggregate(orderAggregate);
        } else if (orderAggregate.isChanged()) {
            updateAggregateRoot(orderAggregate);
            removeEntities(orderAggregate);
            updateEntities(orderAggregate);
            insertEntities(orderAggregate);
        }
    }

    public void remove(Aggregate<Order> aggregate) {
        Order order = aggregate.getRoot();
        if (orderMapper.delete(new OrderDO(order)) != 1) {
            throw new OptimisticLockException(String.format("Delete order (%s) error, it's not found or changed by another user", order.getId()));
        }
        orderItemMapper.deleteByOrderId(order.getId());
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

    private void insertNewAggregate(Aggregate<Order> orderAggregate) {
        Order order = orderAggregate.getRoot();
        order.increaseVersion();

        orderMapper.insert(new OrderDO(order));

        List<OrderItemDO> itemDOs = order.getItems().stream().map(item -> new OrderItemDO(order.getId(), item)).collect(Collectors.toList());
        orderItemMapper.insertAll(itemDOs);
    }

    private void updateAggregateRoot(Aggregate<Order> orderAggregate) {
        Order order = orderAggregate.getRoot();
        if (orderMapper.updateByPrimaryKey(new OrderDO(order)) != 1) {
            throw new OptimisticLockException(String.format("Update order (%s) error, it's not found or changed by another user", order.getId()));
        };
    }

    private void insertEntities(Aggregate<Order> orderAggregate) {
        Collection<OrderItem> newEntities = orderAggregate.findInsertedEntities(Order::getItems, (item) -> item.getId() == null);
        if (newEntities.size() > 0) {
            List<OrderItemDO> itemDOs = newEntities.stream().map(item -> new OrderItemDO(orderAggregate.getRoot().getId(), item)).collect(Collectors.toList());
            orderItemMapper.insertAll(itemDOs);
        }
    }

    private void updateEntities(Aggregate<Order> orderAggregate) {
        Collection<OrderItem> updatedEntities = orderAggregate.findUpdatedEntities(Order::getItems, OrderItem::getId);
        updatedEntities.stream().forEach((item) -> {
            if (orderItemMapper.updateByPrimaryKey(new OrderItemDO(orderAggregate.getRoot().getId(), item)) != 1) {
                throw new OptimisticLockException(String.format("Update order item (%d) error, it's not found", item.getId()));
            }
        });
    }

    private void removeEntities(Aggregate<Order> orderAggregate) {
        Collection<OrderItem> removedEntities = orderAggregate.findRemovedEntities(Order::getItems, OrderItem::getId);
        removedEntities.stream().forEach((item) -> {
            if (orderItemMapper.deleteByPrimaryKey(item.getId()) != 1) {
                throw new OptimisticLockException(String.format("Delete order item (%d) error, it's not found", item.getId()));
            }
        });
    }
}
