package com.github.meixuesong.order.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface OrderItemDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderItemDO record);

    int insertSelective(OrderItemDO record);

    OrderItemDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderItemDO record);

    int updateByPrimaryKey(OrderItemDO record);

    List<OrderItemDO> selectByOrderId(String orderId);

    void insertAll(List<OrderItemDO> itemDOs);

    void deleteByOrderId(String orderId);
}
