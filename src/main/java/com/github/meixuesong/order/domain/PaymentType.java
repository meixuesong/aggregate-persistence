package com.github.meixuesong.order.domain;

public enum PaymentType {
    CASH("CASH"),
    CREDIT_CARD("CREDIT_CARD");

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
