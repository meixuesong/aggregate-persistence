# aggregate-persistence

![](https://travis-ci.com/meixuesong/aggregate-persistence.svg?branch=master)

## 1. 简介
领域驱动设计(DDD)已经被业界认为是行之有效的复杂问题解决之道。随着微服务的流行，DDD也被更多的团队采纳。然而在DDD落地时，聚合(Aggregate)的持久化一直没有一种优雅的方式解决。

在DDD实践中，聚合应该作为一个完整的单元进行读取和持久化，以确保业务的不变性或者说业务规则不变破坏。例如，订单总金额应该与订单明细金额之和一致。

由于领域模型和保存在数据库中的模型可能不一致，并且聚合可能涉及多个实体，因此Hibernate, Mybatis和Spring Data等框架直接用于聚合持久化时，总是面临一些困难，而且代码也不够优雅。因此有人建议NoSQL最适合做聚合的持久化，每个聚合实例就是一个文档。然而并不是所有系统都适合用NoSQL。另一个常见方法是通过领域事件来实现持久化，也就是根据领域事件的不同，执行不同的SQL。但这样的话，Repository层就要引入一些逻辑判断，代码冗余增加了维护成本。

本项目旨在提供一种优雅的、轻量级聚合持久化方案。该方案简单易行，可以真正做到领域对象是干净的对象模型，只处理业务逻辑。而Repository持久化层完全与业务无关，只负责聚合的持久化。

方案的核心是`Aggregate<T>`容器。当Repository查询聚合时，返回`Aggregate<T>`，它保留了聚合的历史快照。因此在保存聚合时，就可以与快照进行对比，找到需要修改的实体和字段，然后完成持久化工作。

聚合Aggregate作为聚合的载体，提供以下功能：
* `public R getRoot()`：获取聚合根
* `public R getRootSnapshot()`: 获取聚合根的历史快照
* `public boolean isChanged()`: 聚合是否发生了变化
* `public boolean isNew()`：是否为新的聚合
* `public <T> Collection<T> findNewEntities(Function<R, Collection<T>> getCollection, Predicate<T> isNew)`：在实体集合（例如订单的所有订单明细行中）找到新的实体
* `public <T, ID> Collection<T> findChangedEntities(Function<R, Collection<T>> getCollection, Function<T, ID> getId)`：在实体集合（例如所有订单明细行中）找到发生变更的实体
* `public <T, ID> Collection<T> findRemovedEntities(Function<R, Collection<T>> getCollection, Function<T, ID> getId)`：在实体集合（例如所有订单明细行中）找到已经删除的实体

工具类`DataObjectUtils`则提供了对象的对比功能。它可以帮助你修改数据库时只update那些变化了的字段。以Person为例，`DataObjectUtils.getDelta(personSnapshot, personCurrent)`将返回Delta值。如果属性没有发生变化，Delta的对应属性值为null, 否则为修改后的值。下表展示了这种差别，personCurrent是修改后的值，personSnapshot是旧值。

 Object | ID | NAME | AGE | ADDRESS 
 ------------- |----|-----|-----|-----
 personCurrent | 001 | Mike | 20 | Beijing 
 personSnapshot | 001 | Mike | 21 | Shanghai 
 delta | null | null | 21 | Shanghai 

## 2. 使用Aggregate-Persistence

在项目中加入依赖：

```xml
        <dependency>
            <groupId>com.github.meixuesong</groupId>
            <artifactId>aggregate-persistence</artifactId>
            <version>1.0.0</version>
        </dependency>
```

## 3. 示例
接下来我们通过一个完整的示例展示Aggregate-Persistence的功能。本例以订单聚合的持久化为例，包括两个实体：订单（Order）和订单明细行（OrderItem）：

```java
public class Order implements Versionable {
    private String id;
    private Date createTime;
    private Customer customer;
    private List<OrderItem> items;
    private OrderStatus status;
    private BigDecimal totalPrice;
    private BigDecimal totalPayment;
    private int version;
}

public class OrderItem {
    private Long id;
    private Product product;
    private BigDecimal amount;
    private BigDecimal subTotal;
}
```

OrderRepository完成订单的持久化工作，主要方法如下：

```java
public class OrderRepository {
    Aggregate<Order> findById(String orderId);
    void save(Aggregate<Order> orderAggregate);
    void remove(Order order);
}
```

在本例中，领域模型与数据模型不一致。因此Repository将Domain model(e.g. Order)转换成Data object(e.g. OrderDO)，然后使用Mybatis完成持久化。查询时，进行反向操作，将Data object转换成Domain model.

### 3.1 查询订单
当创建Order聚合后，调用`AggregateFactory.createAggregate`创建Aggregate对象，它将自动保存Order的快照，以供后续对比。

```java
public Aggregate<Order> findById(String id) {
    OrderDO orderDO = orderMapper.selectByPrimaryKey(id);
    if (orderDO == null) {
        throw new EntityNotFoundException("Order(" + id + ") not found");
    }

    Order order = orderDO.toOrder();
    order.setCustomer(customerRepository.findById(orderDO.getCustomerId()));
    order.setItems(getOrderItems(id));

    return AggregateFactory.createAggregate(order);
}
```

### 3.2 保存新增订单、修改订单

使用`save`接口方法完成订单的新增和修改操作，示例代码如下：

```java
void save(Aggregate<Order> orderAggregate) {
    if (orderAggregate.isNew()) {
        //insert order
        Order order = orderAggregate.getRoot();
        orderMapper.insert(new OrderDO(order));
        //insert order items
        List<OrderItemDO> itemDOs = order.getItems().stream().map(item -> new OrderItemDO(order.getId(), item)).collect(Collectors.toList());
        orderItemMapper.insertAll(itemDOs);
    } else if (orderAggregate.isChanged()) {
        //update order 
        updateAggregateRoot(orderAggregate);
        //delete the removed order items from DB
        removeOrderItems(orderAggregate);
        //update the changed order items
        updateOrderItems(orderAggregate);
        //insert the new order items into DB
        insertOrderItems(orderAggregate);
    }
}
```

上例代码中，当`orderAggregate.isNew()`为true时，调用Mybatis Mapper插入数据。否则如果聚合已经被修改，则需要更新数据。

首先更新聚合根。领域对象(Order)首先被转换成数据对象（OrderDO），然后DataObjectUtils对比OrderDO的历史版本，得到Delta值，最终调用MyBatis的update selective方法更新到数据库中。代码如下：

```java
private void updateAggregateRoot(Aggregate<Order> orderAggregate) {
    //get changed fields and its value
    OrderDO delta = getOrderDODelta(orderAggregate);
    //only update changed fields, avoid update all fields
    if (orderMapper.updateByPrimaryKeySelective(delta) != 1) {
        throw new OptimisticLockException(String.format("Update order (%s) error, it’s not found or changed by another user", orderAggregate.getRoot().getId()));
    }
}

private OrderDO getOrderDODelta(Aggregate<Order> orderAggregate) {
    OrderDO current = new OrderDO(orderAggregate.getRoot());
    OrderDO old = new OrderDO(orderAggregate.getRootSnapshot());
    //compare field by field, if field is not changed, its value is null, otherwise its value is current new value
    OrderDO delta = DataObjectUtils.getDelta(old, current);
    //because id and version is null, so set to new value, and then mapper can update by id and version
    delta.setId(current.getId());
    delta.setVersion(current.getVersion());

    return delta;
}
```

对于订单明细行的增删改，都是通过Aggregate找到新增、删除和修改的实体，然后完成数据库操作。代码示例如下：

```java
private void removeOrderItems(Aggregate<Order> orderAggregate) {
    Collection<OrderItem> removedEntities = orderAggregate.findRemovedEntities(Order::getItems, OrderItem::getId);
    removedEntities.stream().forEach((item) -> {
        if (orderItemMapper.deleteByPrimaryKey(item.getId()) != 1) {
            throw new OptimisticLockException(String.format("Delete order item (%d) error, it's not found", item.getId()));
        }
    });
}

private void updateOrderItems(Aggregate<Order> orderAggregate) {
    Collection<OrderItem> updatedEntities = orderAggregate.findChangedEntities(Order::getItems, OrderItem::getId);
    updatedEntities.stream().forEach((item) -> {
        if (orderItemMapper.updateByPrimaryKey(new OrderItemDO(orderAggregate.getRoot().getId(), item)) != 1) {
            throw new OptimisticLockException(String.format("Update order item (%d) error, it’s not found", item.getId()));
        }
    });
}

private void insertOrderItems(Aggregate<Order> orderAggregate) {
    //OrderItem.getId()为空表示新增实体
    Collection<OrderItem> newEntities = orderAggregate.findNewEntities(Order::getItems, (item) -> item.getId() == null);
    if (newEntities.size() > 0) {
        List<OrderItemDO> itemDOs = newEntities.stream().map(item -> new OrderItemDO(orderAggregate.getRoot().getId(), item)).collect(Collectors.toList());
        orderItemMapper.insertAll(itemDOs);
    }
}
```

### 3.3 删除订单

```java
public void remove(Aggregate<Order> aggregate) {
    Order order = aggregate.getRoot();
    if (orderMapper.delete(new OrderDO(order)) != 1) {
        throw new OptimisticLockException(String.format("Delete order (%s) error, it's not found or changed by another user", order.getId()));
    }
    orderItemMapper.deleteByOrderId(order.getId());
}
```

完整的示例代码见[订单示例项目](https://github.com/meixuesong/aggregate-persistence-sample)

