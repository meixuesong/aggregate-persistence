package com.github.meixuesong.aggregatepersistence.complex_object;

import org.junit.Test;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class RepaymentPlan implements Serializable {
    private Integer no;
    private BigDecimal payableAmount;
    private String status;

    public RepaymentPlan(Integer no, BigDecimal payableAmount, String status) {
        this.no = no;
        this.payableAmount = payableAmount;
        this.status = status;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public BigDecimal getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(BigDecimal payableAmount) {
        this.payableAmount = payableAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
