package com.github.meixuesong.order.domain;

import com.github.meixuesong.common.Versionable;
import com.github.meixuesong.customer.Customer;
import lombok.Data;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

@Data
public class Order implements Versionable {
    private String id;
    private Date createTime;
    private Customer customer;
    private List<OrderItem> items;
    private OrderStatus status;
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private BigDecimal totalPayment = BigDecimal.ZERO;
    private int version;

    public void checkout(Payment payment) {
        if (status == OrderStatus.NEW) {
            totalPayment = payment.getAmount();
            validatePayments();
            this.status = OrderStatus.PAID;
        } else {
            throw new OrderPaymentException("The order status is not for payment.");
        }
    }

    private void validatePayments() {
        if (totalPayment.compareTo(totalPrice) != 0) {
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            throw new OrderPaymentException(String.format("Payment (%s) is not equals to total price (%s)",
                    decimalFormat.format(totalPayment), decimalFormat.format(totalPrice)));
        }
    }

    @Override
    public void increaseVersion() {
        version++;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void discard() {
        if (status != OrderStatus.NEW) {
            throw new RuntimeException("Only new order can be discardOrder.");
        }

        this.status = OrderStatus.DISCARD;
    }
}
