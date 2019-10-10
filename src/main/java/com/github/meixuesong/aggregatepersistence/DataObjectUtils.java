package com.github.meixuesong.aggregatepersistence;

import com.github.meixuesong.aggregatepersistence.deepequals.DeepEquals;
import com.github.meixuesong.aggregatepersistence.deepequals.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DataObjectUtils {
    public <T> T getDelta(T old, T current) {
        return getDelta(old, current, new String[]{});
    }

    public <T> T getDelta(T old, T current, String... ignoredFields) {
        T result = createInstance(current.getClass());

        Set<String> ignoreFieldSet = new HashSet<>(Arrays.asList(ignoredFields));
        Collection<Field> fields = ReflectionUtils.getDeepDeclaredFields(current.getClass());
        for (Field field : fields) {
            if (ignoreFieldSet.contains(field.getName())) {
                continue;
            }

            try {
                if (! new DeepEquals().isDeepEquals(field.get(old), field.get(current))) {
                    field.set(result, field.get(current));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


        return result;
    }

    private <T> T createInstance(Class<?> aClass) {
        T result;
        try {
            result = (T) aClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
