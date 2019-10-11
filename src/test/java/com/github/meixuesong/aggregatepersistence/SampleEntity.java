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

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author meixuesong
 */
@Data
public class SampleEntity implements Versionable{
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

}
