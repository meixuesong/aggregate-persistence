package com.github.meixuesong.order.domain;

import com.github.meixuesong.common.Versionable;
import com.github.meixuesong.order.OrderPaymentException;
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

    public void checkout(BigDecimal money) {
        this.totalPayment = money;
        validatePayments();
    }

    private void validatePayments() {
        if (totalPayment.compareTo(totalPrice) != 0) {
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            throw new OrderPaymentException(String.format("付款金额不等于应收金额, 应付：%s, 实付：%s",
                    decimalFormat.format(totalPrice), decimalFormat.format(totalPayment)));
        }
    }

    @Override
    public void increaseVersion() {
        version++;
    }

    public List<OrderItem> getItems() {
        return items;
    }
}
