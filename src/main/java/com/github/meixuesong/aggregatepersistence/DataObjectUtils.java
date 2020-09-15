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

import com.github.meixuesong.aggregatepersistence.deepequals.DeepEquals;
import com.github.meixuesong.aggregatepersistence.deepequals.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * The data object utiles. Data object are persistence object, which is used to persistent to DB
 * This utils compare two data object, find which fields changed, and what's the new value.
 *
 * For example, if there are two instance(i.e. old, new value) of SampleObject, they have same id and length, but different area.
 * This utils will return a new instance of SampleObject, the id and length will be null because they are unchanged, and the area will be the new value.
 * <pre><code class="java">
 *     class SampleObject {
 *         private String id;
 *         private Integer length;
 *         private Double area;
 *     }
 * </code></pre>
 *
 * @author meixuesong
 */
public class DataObjectUtils {
    private DataObjectUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Get the delta of two objects.
     * @param old the old object
     * @param current the new object
     * @param <T> the type to be compare.
     * @return the delta object which unchanged field is null and the changed field will have the value of current object
     */
    public static <T> T getDelta(T old, T current) {
        return getDelta(old, current, new String[]{});
    }

    /**
     * Get the delta of two objects.
     * @param old the old object
     * @param current the new object
     * @param <T> the type to be compare.
     * @param ignoredFields the set of field names to be ignored when compare current to old object.
     * @return the delta object which unchanged field is null and the changed field will have the value of current object
     */
    public static <T> T getDelta(T old, T current, String... ignoredFields) {
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

    private static <T> T createInstance(Class<?> aClass) {
        T result;
        try {
            result = (T) aClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
