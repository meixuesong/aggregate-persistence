package com.github.meixuesong.user;


import com.github.meixuesong.common.Aggregate;
import com.github.meixuesong.common.Versionable;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AggregateTest {
    @Test
    public void should_not_changed_when_aggregate_not_changed() {
        User user = new User("9999", "Mei", "18823780101", "Beijing", 1);
        Aggregate<User> userAggregate = new Aggregate<>(user);

        assertThat(userAggregate.isChanged(), is(false));
    }

    @Test
    public void should_changed_when_change_property() {
        User user = new User("9999", "Mei", "18823780101", "Beijing", 1);
        Aggregate<User> userAggregate = new Aggregate<>(user);

        String newPhone = "1990011999";
        user.setPhone(newPhone);

        assertThat(userAggregate.isChanged(), is(true));
    }

    @Test
    public void should_be_new_when_version_is_zero() {
        User user = new User("9999", "Mei", "18823780101", "Beijing", Versionable.NEW_VERSION);
        Aggregate<User> userAggregate = new Aggregate<>(user);

        assertThat(userAggregate.isNew(), is(true));
    }
}
