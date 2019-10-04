package com.github.meixuesong.order.api;

import com.github.meixuesong.order.OrderService;
import com.github.meixuesong.order.domain.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public Order findUser(@PathVariable String id) {
        Order order = orderService.findById(id);

        return order;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(@RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request);
        return order;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Order updateOrder(@PathVariable String id, @RequestBody ChangeOrderRequest request) {
        if (id == null || !id.equalsIgnoreCase(request.getOrderId())) {
            throw new IllegalArgumentException("id should equals request.orderId");
        }
        Order order = orderService.updateOrder(request);
        return order;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void discardOrder(@PathVariable String id) {
        orderService.discardOrder(id);
    }

    @PostMapping("/{id}/payment")
    @ResponseStatus(HttpStatus.CREATED)
    public void checkout(@PathVariable String id, @RequestBody CheckoutRequest request) {
        orderService.checkout(id, request);
    }
}
