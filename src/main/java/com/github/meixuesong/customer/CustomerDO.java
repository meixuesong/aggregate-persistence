package com.github.meixuesong.customer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerDO {
    private String id;
    private String name;

    public Customer toCustomer() {
        Customer customer = new Customer(id, name);

        return customer;
    }
}
