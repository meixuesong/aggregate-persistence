package com.github.meixuesong.order;

import org.springframework.stereotype.Service;

@Service
public class OrderIdGenerator {
    private static long currentId = 0;

    public String generateId() {
        long id = increase();
        return "SALE" + String.format("%010d", id);
    }

    private synchronized long increase() {
        return ++currentId;
    }
}
