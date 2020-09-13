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

import java.io.Serializable;

public class LoanCustomer implements Serializable {
    private final String id;
    private final String name;
    private final String idNumber;
    private final String mobilePhone;

    public LoanCustomer(String id, String name, String idNumber, String mobilePhone) {
        this.id = id;
        this.name = name;
        this.idNumber = idNumber;
        this.mobilePhone = mobilePhone;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoanCustomer that = (LoanCustomer) o;

        if (!id.equals(that.id)) return false;
        if (!name.equals(that.name)) return false;
        if (!idNumber.equals(that.idNumber)) return false;
        return mobilePhone.equals(that.mobilePhone);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + idNumber.hashCode();
        result = 31 * result + mobilePhone.hashCode();
        return result;
    }
}
