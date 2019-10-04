package com.github.meixuesong.order;

import com.github.meixuesong.ApiTest;
import com.github.meixuesong.order.api.ChangeOrderRequest;
import com.github.meixuesong.order.api.CheckoutRequest;
import com.github.meixuesong.order.api.CreateOrderRequest;
import com.github.meixuesong.order.api.OrderItemRequest;
import com.github.meixuesong.order.domain.Order;
import com.github.meixuesong.order.domain.OrderStatus;
import org.assertj.core.data.Offset;
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
        String customerId = "TEST_USER_ID";
        String orderId = "TEST_ORDER";
        ResponseEntity<Order> responseEntity = this.restTemplate.getForEntity(baseUrl + "/orders/" + orderId, Order.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        Order order = responseEntity.getBody();
        assertThat(order.getId()).isEqualTo(orderId);
        assertThat(order.getCustomer().getId()).isEqualTo(customerId);
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

    @Test
    @Sql(scripts = "classpath:sql/order-test-before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:sql/order-test-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void should_support_update_order() {
        //Given
        String orderId = "TEST_ORDER";
        String newCustomerId = "NEW_TEST_USER_ID";

        ArrayList<OrderItemRequest> items = new ArrayList<>();
        items.add(new OrderItemRequest("PROD1", new BigDecimal("1.00")));
        items.add(new OrderItemRequest("PROD2", BigDecimal.TEN));

        ChangeOrderRequest request = new ChangeOrderRequest(orderId, newCustomerId, items);

        //When
        this.restTemplate.put(baseUrl + "/orders/"+ orderId, request);

        //Then
        ResponseEntity<Order> responseEntity = this.restTemplate.getForEntity(baseUrl + "/orders/" + orderId, Order.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        Order order = responseEntity.getBody();
        assertThat(order.getId()).isEqualTo(orderId);
        assertThat(order.getCustomer().getId()).isEqualTo(newCustomerId);
        assertThat(order.getItems()).hasSize(2);
        assertThat(order.getItems().get(0).getAmount()).isCloseTo(BigDecimal.ONE, Offset.offset(BigDecimal.ZERO));
        assertThat(order.getItems().get(1).getAmount()).isCloseTo(BigDecimal.TEN, Offset.offset(BigDecimal.ZERO));
    }

    @Test
    @Sql(scripts = "classpath:sql/order-test-before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:sql/order-test-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void should_support_new_order_item() {
        //Given
        String orderId = "TEST_ORDER";
        String newCustomerId = "TEST_USER_ID";

        ArrayList<OrderItemRequest> items = new ArrayList<>();
        items.add(new OrderItemRequest("PROD1", new BigDecimal("1.00")));
        items.add(new OrderItemRequest("PROD2", BigDecimal.TEN));
        items.add(new OrderItemRequest("PROD3", BigDecimal.TEN));

        ChangeOrderRequest request = new ChangeOrderRequest(orderId, newCustomerId, items);

        //When
        this.restTemplate.put(baseUrl + "/orders/"+ orderId, request);

        //Then
        ResponseEntity<Order> responseEntity = this.restTemplate.getForEntity(baseUrl + "/orders/" + orderId, Order.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        Order order = responseEntity.getBody();
        assertThat(order.getId()).isEqualTo(orderId);
        assertThat(order.getCustomer().getId()).isEqualTo(newCustomerId);
        assertThat(order.getItems()).hasSize(3);
        assertThat(order.getItems().get(0).getAmount()).isCloseTo(BigDecimal.ONE, Offset.offset(BigDecimal.ZERO));
        assertThat(order.getItems().get(1).getAmount()).isCloseTo(BigDecimal.TEN, Offset.offset(BigDecimal.ZERO));
        assertThat(order.getItems().get(2).getAmount()).isCloseTo(BigDecimal.TEN, Offset.offset(BigDecimal.ZERO));
    }

    @Test
    @Sql(scripts = "classpath:sql/order-test-before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:sql/order-test-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void should_support_remove_orderItems() {
        //Given
        String orderId = "TEST_ORDER";

        //When
        this.restTemplate.delete(baseUrl + "/orders/"+ orderId);

        //Then
        ResponseEntity<Order> responseEntity = this.restTemplate.getForEntity(baseUrl + "/orders/" + orderId, Order.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Sql(scripts = "classpath:sql/order-test-before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:sql/order-test-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void should_checkout_order() {
        //Given
        String orderId = "TEST_ORDER";
        BigDecimal amount = new BigDecimal("9000");
        CheckoutRequest request = new CheckoutRequest("CASH", amount);

        //When
        this.restTemplate.postForLocation(baseUrl + "/orders/"+orderId+"/payment", request);

        //Then
        ResponseEntity<Order> responseEntity = this.restTemplate.getForEntity(baseUrl + "/orders/" + orderId, Order.class);
        assertThat(responseEntity.getBody().getTotalPayment()).isCloseTo(amount, Offset.offset(new BigDecimal("0.0001")));
        assertThat(responseEntity.getBody().getStatus()).isEqualTo(OrderStatus.PAID);
    }

}
