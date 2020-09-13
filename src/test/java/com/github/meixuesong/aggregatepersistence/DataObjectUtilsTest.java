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

import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author meixuesong
 */
public class DataObjectUtilsTest {
    @Test
    public void should_get_delta_object() {
        //Given
        Date birthday = new Date();
        String id = "ID";
        int length = 100;
        String money = "100.00";

        Date newBirthday = new Date(birthday.getTime() + 1000);
        String newId = "ID2";
        int newLength = 200;

        SampleEntity entity1 = new SampleEntity(birthday, id, length, new BigDecimal(money), Arrays.asList(new SampleEntity()));
        SampleEntity entity2 = new SampleEntity(newBirthday, newId, newLength, new BigDecimal(money), Arrays.asList(new SampleEntity()));

        //When
        SampleEntity actualDelta = DataObjectUtils.getDelta(entity1, entity2);

        //Then
        SampleEntity expectedDelta = new SampleEntity(newBirthday, newId, newLength, null, null);

        assertEquals(expectedDelta, actualDelta);
    }

    @Test
    public void should_get_delta_when_children_changed() {
        //Given
        Date birthday = new Date();
        String id = "ID";
        int length = 100;
        String money = "100.00";

        SampleEntity entity1 = new SampleEntity(birthday, id, length, new BigDecimal(money), Arrays.asList(new SampleEntity()));
        SampleEntity entity2 = new SampleEntity(birthday, id, length, new BigDecimal(money), Arrays.asList(new SampleEntity(), new SampleEntity()));

        //When
        SampleEntity actualDelta = DataObjectUtils.getDelta(entity1, entity2);

        //Then
        SampleEntity expectedDelta = new SampleEntity(null, null, null, null, Arrays.asList(new SampleEntity(), new SampleEntity()));

        assertEquals(expectedDelta, actualDelta);
    }

    @Test
    public void should_ignore_specify_field() {
        //Given
        Date birthday = new Date();
        String id = "ID";
        int length = 100;
        String money = "100.00";

        SampleEntity entity1 = new SampleEntity(birthday, id, length, new BigDecimal(money), Arrays.asList(new SampleEntity()));
        SampleEntity entity2 = new SampleEntity(birthday, id, length, new BigDecimal(money), Arrays.asList(new SampleEntity(), new SampleEntity()));

        //When
        SampleEntity actualDelta = DataObjectUtils.getDelta(entity1, entity2, "children");

        //Then
        SampleEntity expectedDelta = new SampleEntity(null, null, null, null, null);

        assertEquals(expectedDelta, actualDelta);
    }

    static class SampleEntity {
        private String id;
        private boolean checked;
        private int age;
        private Integer length;
        private double area;
        private Double area2;
        private BigDecimal money;
        private Date birthday;
        private LocalDate meetingTime;
        private List<SampleEntity> children;

        public SampleEntity() {
        }

        public SampleEntity(Date newBirthday, String newId, Integer newLength, BigDecimal money, List<SampleEntity> children){
            birthday = newBirthday;
            id = newId;
            length = newLength;
            this.money = money;
            this.children = children;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SampleEntity that = (SampleEntity) o;

            if (checked != that.checked) return false;
            if (age != that.age) return false;
            if (Double.compare(that.area, area) != 0) return false;
            if (id != null ? !id.equals(that.id) : that.id != null) return false;
            if (length != null ? !length.equals(that.length) : that.length != null) return false;
            if (area2 != null ? !area2.equals(that.area2) : that.area2 != null) return false;
            if (money != null ? !money.equals(that.money) : that.money != null) return false;
            if (birthday != null ? !birthday.equals(that.birthday) : that.birthday != null) return false;
            if (meetingTime != null ? !meetingTime.equals(that.meetingTime) : that.meetingTime != null) return false;
            return children != null ? children.equals(that.children) : that.children == null;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            result = id != null ? id.hashCode() : 0;
            result = 31 * result + (checked ? 1 : 0);
            result = 31 * result + age;
            result = 31 * result + (length != null ? length.hashCode() : 0);
            temp = Double.doubleToLongBits(area);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            result = 31 * result + (area2 != null ? area2.hashCode() : 0);
            result = 31 * result + (money != null ? money.hashCode() : 0);
            result = 31 * result + (birthday != null ? birthday.hashCode() : 0);
            result = 31 * result + (meetingTime != null ? meetingTime.hashCode() : 0);
            result = 31 * result + (children != null ? children.hashCode() : 0);
            return result;
        }
    }

}
