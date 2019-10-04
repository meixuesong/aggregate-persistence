package com.github.meixuesong.common;

public interface PropertyComparator {
    <T> boolean isAllPropertiesEqual(T a, T b);
}
