package com.github.meixuesong.order.domain;

public enum PaymentType {
    BALANCE_OF_ACCOUNT("余额支付");

    private String value;

    PaymentType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static PaymentType from(String value) {
        for (PaymentType paymentType : PaymentType.values()) {
            if (paymentType.value.equalsIgnoreCase(value)) {
                return paymentType;
            }
        }

        return null;
    }
}
