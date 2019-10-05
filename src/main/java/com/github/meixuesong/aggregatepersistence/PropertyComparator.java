package com.github.meixuesong.aggregatepersistence;

public interface PropertyComparator {
    <T> boolean isAllPropertiesEqual(T a, T b);
}
