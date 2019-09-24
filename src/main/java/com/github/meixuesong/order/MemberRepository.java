package com.github.meixuesong.order;

import com.github.meixuesong.order.dao.CustomerDO;
import com.github.meixuesong.order.dao.CustomerDOMapper;
import com.github.meixuesong.order.domain.Customer;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository {
    private CustomerDOMapper mapper;

    public MemberRepository(CustomerDOMapper mapper) {
        this.mapper = mapper;
    }

    public Customer findById(String id) {
        CustomerDO customerDO = mapper.selectByPrimaryKey(id);

        return customerDO.toCustomer();
    }
}
