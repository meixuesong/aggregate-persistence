package com.github.meixuesong.aggregatepersistence;


import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AggregateTest {
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
        assertThat(getChangedChildren(aggregate).size(), is(0));
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
        assertThat(getRemovedChildren(aggregate).size(), is(1));

        BigDecimal v = new BigDecimal("10.00");
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
}
