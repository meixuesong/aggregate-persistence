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

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author meixuesong
 */
public class SampleEntity implements Versionable, Serializable {
    private String id = "";
    private boolean checked = true;
    private int age = 0;
    private long milliseconds = 0;
    private float length = 0.00F;
    private double area = 0.00D;
    private BigDecimal money = BigDecimal.ZERO;
    private Date birthday = new Date();
    private LocalDate meetingTime = LocalDate.now();
    private List<SampleEntity> children = new ArrayList<>();
    private int version = Versionable.NEW_VERSION;

    @Override
    public int getVersion() {
        return this.version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public LocalDate getMeetingTime() {
        return meetingTime;
    }

    public void setMeetingTime(LocalDate meetingTime) {
        this.meetingTime = meetingTime;
    }

    public List<SampleEntity> getChildren() {
        return children;
    }

    public void setChildren(List<SampleEntity> children) {
        this.children = children;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
