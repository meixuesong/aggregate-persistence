package com.github.meixuesong.aggregatepersistence;

import com.github.meixuesong.aggregatepersistence.deepequals.DeepEquals;

public class JavaUtilDeepComparator implements DeepComparator {
    @Override
    public <T> boolean isDeepEquals(T a, T b) {
        return new DeepEquals().isDeepEquals(a, b);
    }
}
