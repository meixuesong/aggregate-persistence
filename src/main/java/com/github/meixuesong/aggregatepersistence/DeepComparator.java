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

/**
 * DeepComparator will be used to deep compare two object. Aggregate use it to find out whether entity is changed.
 * @author meixuesong
 */
public interface DeepComparator {
    /**
     * Whether a and b is deep equal.
     * @param a the object to be compared.
     * @param b the object to be compared.
     * @param <T> the type to be compared.
     * @return true if a is deep equals b, false if a is not deep equals b.
     */
    <T> boolean isDeepEquals(T a, T b);
}
