package com.github.meixuesong.aggregatepersistence;

public interface DeepComparator {
    <T> boolean isDeepEquals(T a, T b);
}
