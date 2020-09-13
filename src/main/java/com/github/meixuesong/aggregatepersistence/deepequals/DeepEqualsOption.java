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

package com.github.meixuesong.aggregatepersistence.deepequals;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The Option of DeepEquals
 * @author meixuesong
 */
public class DeepEqualsOption {
    private boolean ignoreCustomEquals = true;
    private final Set<Class> useCustomEqualsClasses = new HashSet<>();
    private final Map<Class, Comparator> useComparatorClasses = new HashMap<>();
    private final Map<Class, Set<String>> ignoreFieldNames = new HashMap<>();
    private double doubleOffSet = 1e-15;
    private double floatOffSet = 1e-6;


    public DeepEqualsOption() {
    }

    /**
     * When compare two object, if ignoreCustomEquals is true, DeepEquals will ignore custom equals method (unless it was
     * included in the customEqualsClasses), and compare them field by field.
     *
     * @return
     *   true if ignore custom equals method.
     *   false if use custom equals method to compare two objects.
     */
    public boolean isIgnoreCustomEquals() {
        return ignoreCustomEquals;
    }

    /**
     * Using equals method to compare
     * @return Classes using equals method to compare with another object.
     */
    public Set<Class> getCustomEqualsClasses() {
        return useCustomEqualsClasses;
    }

    /**
     * Using comparator to compare
     * @return Classes and their comparator.
     */
    public Map<Class, Comparator> getUseComparatorClasses() {
        return useComparatorClasses;
    }

    /**
     * set whether ignore custom equals method when compare two objects.
     * @param ignoreCustomEquals ignoreCustomEquals
     */
    public void setIgnoreCustomEquals(boolean ignoreCustomEquals) {
        this.ignoreCustomEquals = ignoreCustomEquals;
    }

    /**
     * When compare two double number, doubleOffSet is used to decide whether they are equals.
     * For example, 1.001 is equals to 1.00101 if offset is equal or larger then 0.00001
     * @return the offset
     */
    public double getDoubleOffSet() {
        return doubleOffSet;
    }

    /**
     * Set the double offset.
     *
     * When compare two double number, doubleOffSet is used to decide whether they are equals.
     * For example, 1.001 is equals to 1.00101 if offset is equal or larger then 0.00001
     * @param doubleOffSet doubleOffSet
     */
    public void setDoubleOffSet(double doubleOffSet) {
        this.doubleOffSet = doubleOffSet;
    }

    /**
     * When compare two float number, floatOffSet is used to decide whether they are equals.
     * For example, 1.001 is equals to 1.00101 if offset is equal or larger then 0.00001
     * @return the offset
     */
    public double getFloatOffSet() {
        return floatOffSet;
    }

     /**
     * Set the float offset.
     *
     * When compare two float number, floatOffSet is used to decide whether they are equals.
     * For example, 1.001 is equals to 1.00101 if offset is equal or larger then 0.00001
     *
     * @param floatOffSet floatOffSet
     */
    public void setFloatOffSet(double floatOffSet) {
        this.floatOffSet = floatOffSet;
    }

    /**
     * Specify the field names to ignore when compare the Class instance.
     * For example, to compare Person.class, we can specify the ignoreFieldNames include : age and children
     * @return a map
     */
    public Map<Class, Set<String>> getIgnoreFieldNames() {
        return ignoreFieldNames;
    }
}
