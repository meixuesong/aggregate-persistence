package com.github.meixuesong.common;


import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AggregateTest {
    @Test
    public void should_be_unchanged_when_no_fields_changed() {
        DataClass data = new DataClass();
        Aggregate<DataClass> aggregate = new Aggregate<>(data);

        assertThat(aggregate.isChanged(), is(false));
    }

    @Test
    public void should_be_changed_when_int_field_changed() {
        DataClass data = new DataClass();
        Aggregate<DataClass> aggregate = new Aggregate<>(data);

        data.setAge(10);

        assertThat(aggregate.isChanged(), is(true));
    }

    @Test
    public void should_be_changed_when_boolean_field_changed() {
        DataClass data = new DataClass();
        Aggregate<DataClass> aggregate = new Aggregate<>(data);

        data.setChecked(false);

        assertThat(aggregate.isChanged(), is(true));
    }

    @Test
    public void should_be_changed_when_float_field_changed() {
        DataClass data = new DataClass();
        data.setLength(10.0123456789F);
        Aggregate<DataClass> aggregate = new Aggregate<>(data);

        data.setLength(10.0123456F);
        assertThat(aggregate.isChanged(), is(false));

        data.setLength(10.01234F);
        assertThat(aggregate.isChanged(), is(true));
    }

    @Test
    public void should_be_changed_when_double_field_changed() {
        DataClass data = new DataClass();
        data.setArea(123456789*0.001*0.000123456789D);
        Aggregate<DataClass> aggregate = new Aggregate<>(data);

        data.setArea(123456789*0.001*0.00012345678D);
        assertThat(aggregate.isChanged(), is(true));
    }

    @Test
    public void should_be_changed_when_bigdecimal_field_changed() {
        DataClass data = new DataClass();
        data.setMoney(new BigDecimal("0.00000000001"));
        Aggregate<DataClass> aggregate = new Aggregate<>(data);

        data.setMoney(new BigDecimal("0.000000000001"));
        assertThat(aggregate.isChanged(), is(true));
    }

    @Test
    public void should_be_changed_when_date_field_changed() throws ParseException {
        DataClass data = new DataClass();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        data.setBirthday(dateFormat.parse("2019-01-09 23:45:13.666"));
        Aggregate<DataClass> aggregate = new Aggregate<>(data);

        data.setBirthday(dateFormat.parse("2019-01-09 23:45:13.667"));
        assertThat(aggregate.isChanged(), is(true));
    }

    @Test
    public void should_be_changed_when_localdate_field_changed() {
        DataClass data = new DataClass();
        data.setMeetingTime(LocalDate.of(2019, 1, 20));
        Aggregate<DataClass> aggregate = new Aggregate<>(data);

        data.setMeetingTime(LocalDate.of(2019, 1, 21));
        assertThat(aggregate.isChanged(), is(true));
    }

    @Test
    public void should_be_changed_when_collection_field_changed() {
        DataClass data = new DataClass();
        Aggregate<DataClass> aggregate = new Aggregate<>(data);

        data.setChildren(Arrays.asList(new DataClass()));
        assertThat(aggregate.isChanged(), is(true));
    }

    @Test
    public void should_be_changed_when_collection_item_field_changed() {
        DataClass data = new DataClass();
        DataClass child = new DataClass();
        data.setChildren(Arrays.asList(child));
        Aggregate<DataClass> aggregate = new Aggregate<>(data);

        child.setAge(child.getAge() + 1);
        assertThat(aggregate.isChanged(), is(true));
    }
}
