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
 * Copyright 2012-2019 the original author or authors.
 */

package com.github.meixuesong.aggregatepersistence;


import com.github.meixuesong.aggregatepersistence.complex_object.Contract;
import com.github.meixuesong.aggregatepersistence.complex_object.ContractBuilder;
import com.github.meixuesong.aggregatepersistence.complex_object.ContractStatus;
import com.github.meixuesong.aggregatepersistence.complex_object.Loan;
import com.github.meixuesong.aggregatepersistence.complex_object.LoanCustomer;
import com.github.meixuesong.aggregatepersistence.complex_object.RepaymentPlan;
import com.github.meixuesong.aggregatepersistence.complex_object.RepaymentType;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author meixuesong
 */
public class AggregateTest {
    @Before
    public void setUp() throws Exception {
        AggregateFactory.setCopier(new SerializableDeepCopier());
    }

    @Test
    public void should_be_new_when_version_is_NEW_VERSION() {
        SampleEntity entity = new SampleEntity();
        Aggregate<SampleEntity> aggregate = AggregateFactory.createAggregate(entity);

        assertThat(aggregate.isNew(), is(true));
    }

    @Test
    public void should_not_be_new_when_version_is_larger_than_NEW_VERSION() {
        SampleEntity entity = new SampleEntity();
        entity.setVersion(1);
        Aggregate<SampleEntity> aggregate = AggregateFactory.createAggregate(entity);

        assertThat(aggregate.isNew(), is(false));
    }

    @Test
    public void should_be_unchanged_when_no_fields_changed() {
        SampleEntity entity = new SampleEntity();
        Aggregate<SampleEntity> aggregate = AggregateFactory.createAggregate(entity);

        assertThat(aggregate.isChanged(), is(false));
    }

    @Test
    public void should_be_changed_when_int_field_changed() {
        SampleEntity entity = new SampleEntity();
        Aggregate<SampleEntity> aggregate = AggregateFactory.createAggregate(entity);

        entity.setAge(10);

        assertThat(aggregate.isChanged(), is(true));
    }

    @Test
    public void should_be_changed_when_boolean_field_changed() {
        SampleEntity entity = new SampleEntity();
        Aggregate<SampleEntity> aggregate = AggregateFactory.createAggregate(entity);

        entity.setChecked(false);

        assertThat(aggregate.isChanged(), is(true));
    }

    @Test
    public void should_be_changed_when_float_field_changed() {
        SampleEntity entity = new SampleEntity();
        entity.setLength(10.0123456789F);
        Aggregate<SampleEntity> aggregate = AggregateFactory.createAggregate(entity);

        entity.setLength(10.01234F);
        assertThat(aggregate.isChanged(), is(false));

        entity.setLength(10.0123F);
        assertThat(aggregate.isChanged(), is(true));
    }

    @Test
    public void should_be_changed_when_double_field_changed() {
        SampleEntity entity = new SampleEntity();
        entity.setArea(123456789*0.001*0.000123456789D);
        Aggregate<SampleEntity> aggregate = AggregateFactory.createAggregate(entity);

        entity.setArea(123456789*0.001*0.00012345678D);
        assertThat(aggregate.isChanged(), is(true));
    }

    @Test
    public void should_be_changed_when_bigdecimal_field_changed() {
        BigDecimal moneyWith10Zero = new BigDecimal("0.000001");
        BigDecimal moneyWith11Zero = new BigDecimal("0.00001");

        SampleEntity entity = new SampleEntity();
        entity.setMoney(moneyWith10Zero);
        Aggregate<SampleEntity> aggregate = AggregateFactory.createAggregate(entity);

        entity.setMoney(moneyWith11Zero);
        assertThat(aggregate.isChanged(), is(true));
    }

    @Test
    public void should_be_changed_when_date_field_changed() throws ParseException {
        SampleEntity entity = new SampleEntity();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        entity.setBirthday(dateFormat.parse("2019-01-09 23:45:13.666"));
        Aggregate<SampleEntity> aggregate = AggregateFactory.createAggregate(entity);

        entity.setBirthday(dateFormat.parse("2019-01-09 23:45:13.667"));
        assertThat(aggregate.isChanged(), is(true));
    }

    @Test
    public void should_be_changed_when_local_date_field_changed() {
        SampleEntity entity = new SampleEntity();
        entity.setMeetingTime(LocalDate.of(2019, 1, 20));
        Aggregate<SampleEntity> aggregate = AggregateFactory.createAggregate(entity);

        entity.setMeetingTime(LocalDate.of(2019, 1, 21));
        assertThat(aggregate.isChanged(), is(true));
    }

    @Test
    public void should_find_new_child() {
        SampleEntity entity = new SampleEntity();
        Aggregate<SampleEntity> aggregate = AggregateFactory.createAggregate(entity);

        entity.getChildren().add(new SampleEntity());

        assertThat(aggregate.isChanged(), is(true));
        assertThat(getNewChildren(aggregate).size(), is(1));
        assertThat(aggregate.findNewEntitiesById(SampleEntity::getChildren, SampleEntity::getId).size(), is(1));
        assertThat(getChangedChildren(aggregate).size(), is(0));
        assertThat(aggregate.findChangedEntitiesWithOldValues(SampleEntity::getChildren, SampleEntity::getId).size(), is(0));
        assertThat(getRemovedChildren(aggregate).size(), is(0));
    }

    @Test
    public void should_be_changed_when_child_item_field_changed() {
        SampleEntity entity = new SampleEntity();
        SampleEntity child = new SampleEntity();
        child.setVersion(1);
        entity.getChildren().add(child);
        Aggregate<SampleEntity> aggregate = AggregateFactory.createAggregate(entity);

        child.setAge(child.getAge() + 1);

        assertThat(aggregate.isChanged(), is(true));
        assertThat(getNewChildren(aggregate).size(), is(0));
        assertThat(getChangedChildren(aggregate).size(), is(1));
        assertThat(aggregate.findChangedEntitiesWithOldValues(SampleEntity::getChildren, SampleEntity::getId).size(), is(1));
        assertThat(getRemovedChildren(aggregate).size(), is(0));
    }

    @Test
    public void should_find_removed_child() {
        SampleEntity entity = new SampleEntity();
        entity.getChildren().add(new SampleEntity());
        Aggregate<SampleEntity> aggregate = AggregateFactory.createAggregate(entity);

        entity.getChildren().clear();

        assertThat(aggregate.isChanged(), is(true));
        assertThat(getNewChildren(aggregate).size(), is(0));
        assertThat(getChangedChildren(aggregate).size(), is(0));
        assertThat(aggregate.findChangedEntitiesWithOldValues(SampleEntity::getChildren, SampleEntity::getId).size(), is(0));
        assertThat(getRemovedChildren(aggregate).size(), is(1));

        BigDecimal v = new BigDecimal("10.00");
    }

    @Test
    public void should_create_snapshot() {
        LocalDateTime now = LocalDateTime.now();
        Contract expectedContract = new ContractBuilder()
                .setId("ABCD")
                .setCreatedAt(now)
                .setRepaymentType(RepaymentType.DEBJ)
                .setStatus(ContractStatus.ACTIVE)
                .setCustomer(new LoanCustomer("", "", "123456200012319876", ""))
                .setInterestRate(BigDecimal.TEN)
                .setMaturityDate(now.plusYears(1).toLocalDate())
                .setCommitment(BigDecimal.valueOf(1000.00))
                .createContract();

        Aggregate<Contract> aggregate = AggregateFactory.createAggregate(expectedContract);
        Contract actualContract = aggregate.getRootSnapshot();

        assertEquals(expectedContract, actualContract);
        new JavaUtilDeepComparator().isDeepEquals(actualContract, expectedContract);
    }

    private Collection<SampleEntity> getNewChildren(Aggregate<SampleEntity> aggregate) {
        return aggregate.findNewEntities(SampleEntity::getChildren, (item) -> item.getVersion() == Versionable.NEW_VERSION);
    }

    private Collection<SampleEntity> getChangedChildren(Aggregate<SampleEntity> aggregate) {
        return aggregate.findChangedEntities(SampleEntity::getChildren, SampleEntity::getId);
    }

    private Collection<SampleEntity> getRemovedChildren(Aggregate<SampleEntity> aggregate) {
        return aggregate.findRemovedEntities(SampleEntity::getChildren, SampleEntity::getId);
    }

    @Test
    public void should_find_collection_delta_for_new_aggregate() {
        Loan loan = new Loan("ID", 12, Versionable.NEW_VERSION);
        loan.createPlans();

        Aggregate<Loan> aggregate = AggregateFactory.createAggregate(loan);
        Map<Aggregate.DeltaType, Collection<RepaymentPlan>> collectionDelta = aggregate.findCollectionDelta(Loan::getRepaymentPlans, RepaymentPlan::getNo);

        assertEquals(12, collectionDelta.get(Aggregate.DeltaType.NEW).size());

        //exists aggregate
        loan.setVersion(10);
        aggregate = AggregateFactory.createAggregate(loan);
        loan.setTotalMonth(22);
        loan.createPlans();
        Map<Aggregate.DeltaType, Collection<RepaymentPlan>> collectionDelta2 = aggregate.findCollectionDelta(Loan::getRepaymentPlans, RepaymentPlan::getNo);
        assertEquals(10, collectionDelta2.get(Aggregate.DeltaType.NEW).size());
        assertEquals(0, collectionDelta2.get(Aggregate.DeltaType.UPDATED).size());
        assertEquals(0, collectionDelta2.get(Aggregate.DeltaType.REMOVED).size());
    }

    @Test
    public void should_find_collection_delta_for_exists_aggregate() {
        Loan loan = new Loan("ID", 12, Versionable.NEW_VERSION);
        loan.createPlans();
        loan.getRepaymentPlans().get(0).setStatus("PAID");
        loan.setVersion(10);
        Aggregate<Loan> aggregate =AggregateFactory.createAggregate(loan);

        loan.setTotalMonth(22);
        loan.createPlans();

        Map<Aggregate.DeltaType, Collection<RepaymentPlan>> collectionDelta2 = aggregate.findCollectionDelta(Loan::getRepaymentPlans, RepaymentPlan::getNo);
        assertEquals(10, collectionDelta2.get(Aggregate.DeltaType.NEW).size());
        assertEquals(1, collectionDelta2.get(Aggregate.DeltaType.UPDATED).size());
        assertEquals(0, collectionDelta2.get(Aggregate.DeltaType.UPDATED).stream().findFirst().get().getNo().intValue());
        assertEquals(0, collectionDelta2.get(Aggregate.DeltaType.REMOVED).size());
    }

    @Test
    public void should_find_collection_delta_for_exists_aggregate2() {
        Loan loan = new Loan("ID", 22, Versionable.NEW_VERSION);
        loan.createPlans();
        loan.getRepaymentPlans().get(0).setStatus("PAID");
        loan.setVersion(10);
        Aggregate<Loan> aggregate =AggregateFactory.createAggregate(loan);

        loan.setTotalMonth(10);
        loan.createPlans();

        Map<Aggregate.DeltaType, Collection<RepaymentPlan>> collectionDelta2 = aggregate.findCollectionDelta(Loan::getRepaymentPlans, RepaymentPlan::getNo);
        assertEquals(0, collectionDelta2.get(Aggregate.DeltaType.NEW).size());
        assertEquals(1, collectionDelta2.get(Aggregate.DeltaType.UPDATED).size());
        assertEquals(12, collectionDelta2.get(Aggregate.DeltaType.REMOVED).size());
    }
}
