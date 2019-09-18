package com.github.meixuesong;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.github.meixuesong")
public class AggregatePersistenceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AggregatePersistenceApplication.class, args);
    }

}
