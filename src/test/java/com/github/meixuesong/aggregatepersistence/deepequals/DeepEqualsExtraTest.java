package com.github.meixuesong.aggregatepersistence.deepequals;

import com.github.meixuesong.aggregatepersistence.SampleEntity;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.core.Is.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class DeepEqualsExtraTest {
    @Test
    public void should_support_custom_comparator() {
        SampleEntity sampleEntity1 = new SampleEntity();
        SampleEntity sampleEntity2 = new SampleEntity();
        sampleEntity2.setBirthday(sampleEntity1.getBirthday());
        sampleEntity2.setMeetingTime(sampleEntity1.getMeetingTime());

        sampleEntity1.setMoney(new BigDecimal("10.000001"));
        sampleEntity2.setMoney(BigDecimal.TEN);

        DeepEquals deepEquals = new DeepEquals(new DeepEqualsDefaultOption());
        assertThat(deepEquals.isDeepEquals(sampleEntity1, sampleEntity2), is(true));
    }
}
