/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2020 the original author or authors.
 */

package com.github.meixuesong.aggregatepersistence.complex_object;

import com.github.meixuesong.aggregatepersistence.Versionable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Contract implements Versionable, Serializable {
    private String id;
    private LoanCustomer customer;
    private BigDecimal interestRate;
    private RepaymentType repaymentType;
    private LocalDate maturityDate;
    private BigDecimal commitment;
    private LocalDateTime createdAt;
    private ContractStatus status;
    private int version;


    Contract(String id, LoanCustomer customer, BigDecimal interestRate, RepaymentType repaymentType, LocalDate maturityDate, BigDecimal commitment, LocalDateTime createdAt, ContractStatus status) {
        this.id = id;
        this.customer = customer;
        this.interestRate = interestRate;
        this.repaymentType = repaymentType;
        this.maturityDate = maturityDate;
        this.commitment = commitment;
        this.createdAt = createdAt;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public LoanCustomer getCustomer() {
        return customer;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public RepaymentType getRepaymentType() {
        return repaymentType;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }

    public BigDecimal getCommitment() {
        return commitment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public ContractStatus getStatus() {
        return status;
    }

    public void setCommitment(BigDecimal commitment) {
        this.commitment = commitment;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contract contract = (Contract) o;

        if (version != contract.version) return false;
        if (!id.equals(contract.id)) return false;
        if (!customer.equals(contract.customer)) return false;
        if (!interestRate.equals(contract.interestRate)) return false;
        if (repaymentType != contract.repaymentType) return false;
        if (!maturityDate.equals(contract.maturityDate)) return false;
        if (!commitment.equals(contract.commitment)) return false;
        if (!createdAt.equals(contract.createdAt)) return false;
        return status == contract.status;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + customer.hashCode();
        result = 31 * result + interestRate.hashCode();
        result = 31 * result + repaymentType.hashCode();
        result = 31 * result + maturityDate.hashCode();
        result = 31 * result + commitment.hashCode();
        result = 31 * result + createdAt.hashCode();
        result = 31 * result + status.hashCode();
        result = 31 * result + version;
        return result;
    }
}
