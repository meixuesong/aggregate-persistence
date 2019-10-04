package com.github.meixuesong.customer;

import org.springframework.stereotype.Repository;

@Repository
public class CustomerRepository {
    private CustomerDOMapper mapper;

    public CustomerRepository(CustomerDOMapper mapper) {
        this.mapper = mapper;
    }

    public Customer findById(String id) {
        CustomerDO customerDO = mapper.selectByPrimaryKey(id);

        return customerDO.toCustomer();
    }
}
