package com.github.meixuesong.aggregatepersistence.complex_object;

import com.github.meixuesong.aggregatepersistence.Versionable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Loan implements Versionable, Serializable {
    private String id;
    private Integer totalMonth;
    private List<RepaymentPlan> repaymentPlans;
    private int version;

    public Loan(String id, Integer totalMonth, int version) {
        this.id = id;
        this.totalMonth = totalMonth;
        this.version = version;
    }

    public void createPlans() {
        List<RepaymentPlan> plans = new ArrayList<>();
        for (int i = 0; i < totalMonth; i++) {
            plans.add(new RepaymentPlan(i, BigDecimal.ONE, "PLAN"));
        }

        repaymentPlans = plans;
    }

    public void payPlan(int planNo) {
        for (RepaymentPlan repaymentPlan : repaymentPlans) {
            if (repaymentPlan.getNo().equals(planNo)) {
                repaymentPlan.setStatus("PAID");
                return;
            }
        }

        throw new IllegalArgumentException(String.format("loan no (%s, %d) not exists.", id, planNo));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getTotalMonth() {
        return totalMonth;
    }

    public void setTotalMonth(Integer totalMonth) {
        this.totalMonth = totalMonth;
    }

    public List<RepaymentPlan> getRepaymentPlans() {
        return repaymentPlans;
    }

    public void setRepaymentPlans(List<RepaymentPlan> repaymentPlans) {
        this.repaymentPlans = repaymentPlans;
    }

    @Override
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
