package com.github.meixuesong.customer;

import lombok.Data;

@Data
public class Customer {
    private String id;
    private String name;

    public Customer() {
    }

    public Customer(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
