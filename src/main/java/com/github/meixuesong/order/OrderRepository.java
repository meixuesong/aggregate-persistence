package com.github.meixuesong.order;

import com.github.meixuesong.common.Aggregate;
import com.github.meixuesong.order.dao.CustomerDO;
import com.github.meixuesong.order.dao.CustomerDOMapper;
import com.github.meixuesong.order.dao.OrderDO;
import com.github.meixuesong.order.dao.OrderDOMapper;
import com.github.meixuesong.order.dao.OrderItemDO;
import com.github.meixuesong.order.dao.OrderItemDOMapper;
import com.github.meixuesong.order.dao.ProductDO;
import com.github.meixuesong.order.dao.ProductDOMapper;
import com.github.meixuesong.order.domain.Order;
import com.github.meixuesong.order.domain.OrderItem;
import com.github.meixuesong.order.domain.Product;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OrderRepository {
    private OrderDOMapper orderMapper;
    private OrderItemDOMapper orderItemMapper;
    private ProductDOMapper productMapper;
    private CustomerDOMapper customerMapper;

    public OrderRepository(OrderDOMapper orderMapper, OrderItemDOMapper orderItemMapper, ProductDOMapper productMapper, CustomerDOMapper customerMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.productMapper = productMapper;
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

    private ArrayList<OrderItem> getOrderItems(String id) {
        List<OrderItemDO> itemDOs = orderItemMapper.selectByOrderId(id);
        Map<String, Product> products = getProductDict(itemDOs);

        ArrayList<OrderItem> items = new ArrayList<>();
        for (OrderItemDO itemDO : itemDOs) {
            Product product = products.get(itemDO.getProdId());
            items.add(new OrderItem(itemDO.getId(), product, itemDO.getAmount()));
        }

        return items;
    }

    private Map<String, Product> getProductDict(List<OrderItemDO> itemDOs) {
        List<String> prodIds = new ArrayList<>();
        for (OrderItemDO itemDO : itemDOs) {
            prodIds.add(itemDO.getProdId());
        }

        List<ProductDO> productDOs = productMapper.queryListByIDs(prodIds);
        Map<String, Product> products = new HashMap<>();
        for (ProductDO productDO : productDOs) {
            products.put(productDO.getId(), productDO.toProduct());
        }
        return products;
    }

}
