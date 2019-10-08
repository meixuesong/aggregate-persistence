package com.github.meixuesong.aggregatepersistence.deepequals;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DeepEqualsOption {
    private boolean ignoreCustomEquals = true;
    private final Set<Class> useCustomEqualsClasses = new HashSet<>();
    private final Map<Class, Comparator> useComparatorClasses = new HashMap<>();
    private double doubleOffSet = 1e-15;
    private double floatOffSet = 1e-6;


    public DeepEqualsOption() {
    }

    public boolean isIgnoreCustomEquals() {
        return ignoreCustomEquals;
    }

    public Set<Class> getUseCustomEqualsClasses() {
        return useCustomEqualsClasses;
    }

    public Map<Class, Comparator> getUseComparatorClasses() {
        return useComparatorClasses;
    }

    public void setIgnoreCustomEquals(boolean ignoreCustomEquals) {
        this.ignoreCustomEquals = ignoreCustomEquals;
    }

    public double getDoubleOffSet() {
        return doubleOffSet;
    }

    public void setDoubleOffSet(double doubleOffSet) {
        this.doubleOffSet = doubleOffSet;
    }

    public double getFloatOffSet() {
        return floatOffSet;
    }

    public void setFloatOffSet(double floatOffSet) {
        this.floatOffSet = floatOffSet;
    }
}
