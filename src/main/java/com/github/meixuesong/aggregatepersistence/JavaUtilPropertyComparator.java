package com.github.meixuesong.aggregatepersistence;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class JavaUtilPropertyComparator implements PropertyComparator {
    @Override
    public <T> boolean isAllPropertiesEqual(T a, T b) {
        // told to skip all custom .equals() - so it will compare all fields
        Map<String, Object> options = new HashMap<>();
        options.put(DeepEquals.IGNORE_CUSTOM_EQUALS, new HashSet());

        return DeepEquals.deepEquals(a, b, options);
    }
}
