package com.github.meixuesong.order;

import com.github.meixuesong.ApiTest;
import com.github.meixuesong.order.domain.Order;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderControllerTest extends ApiTest {

    @Test
    @Sql(scripts = "classpath:sql/order-test-before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:sql/order-test-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void should_query_order() {
        String orderId = "TEST_ORDER";
        ResponseEntity<Order> responseEntity = this.restTemplate.getForEntity(baseUrl + "/orders/" + orderId, Order.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        Order order = responseEntity.getBody();
        assertThat(order.getId()).isEqualTo(orderId);
        assertThat(order.getCustomer().getId()).isEqualTo("TEST_USER_ID");
        assertThat(order.getItems()).hasSize(2);
    }

    @Test
    @Sql(scripts = "classpath:sql/order-test-before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:sql/order-test-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void should_support_create_order() {
        //Given
        String customerId = "TEST_USER_ID";

        ArrayList<OrderItemRequest> items = new ArrayList<>();
        items.add(new OrderItemRequest("PROD1", BigDecimal.ONE));
        items.add(new OrderItemRequest("PROD2", BigDecimal.ONE));

        CreateOrderRequest request = new CreateOrderRequest(new Date(), customerId, items);

        //When
        ResponseEntity<Order> responseEntity = this.restTemplate.postForEntity(baseUrl + "/orders", request, Order.class);

        //Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Order order = responseEntity.getBody();
        assertThat(order.getId()).isNotNull();
        assertThat(order.getCustomer().getId()).isEqualTo(customerId);
        assertThat(order.getItems()).hasSize(2);
    }
}
