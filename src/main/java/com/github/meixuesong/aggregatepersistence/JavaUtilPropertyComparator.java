package com.github.meixuesong.aggregatepersistence;

import com.github.meixuesong.aggregatepersistence.deepequals.DeepEquals;

public class JavaUtilPropertyComparator implements PropertyComparator {
    @Override
    public <T> boolean isAllPropertiesEqual(T a, T b) {
        return new DeepEquals().isDeepEquals(a, b);
    }
}
