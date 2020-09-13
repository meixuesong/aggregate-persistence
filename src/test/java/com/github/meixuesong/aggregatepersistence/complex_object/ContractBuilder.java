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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ContractBuilder {
    private String id;
    private LoanCustomer customer;
    private BigDecimal interestRate;
    private RepaymentType repaymentType;
    private LocalDate maturityDate;
    private BigDecimal commitment;
    private LocalDateTime createdAt = LocalDateTime.now();
    private ContractStatus status = ContractStatus.ACTIVE;

    public ContractBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public ContractBuilder setCustomer(LoanCustomer customer) {
        this.customer = customer;
        return this;
    }

    public ContractBuilder setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
        return this;
    }

    public ContractBuilder setRepaymentType(RepaymentType repaymentType) {
        this.repaymentType = repaymentType;
        return this;
    }

    public ContractBuilder setMaturityDate(LocalDate maturityDate) {
        this.maturityDate = maturityDate;
        return this;
    }

    public ContractBuilder setCommitment(BigDecimal commitment) {
        this.commitment = commitment;
        return this;
    }

    public ContractBuilder setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public ContractBuilder setStatus(ContractStatus status) {
        this.status = status;
        return this;
    }

    public Contract createContract() {
        return new Contract(id, customer, interestRate, repaymentType, maturityDate, commitment, createdAt, status);
    }
}
