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

import com.cedarsoftware.util.DeepEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * JavaUtilDeepComparator use deepEquals, which is based on https://github.com/jdereg/java-util, to implement the DeepComparator interface.
 *
 * @author meixuesong
 */
public class JavaUtilDeepComparator implements DeepComparator {
    private Set<Class<?>> ignoreCustomEqualsClasses = new HashSet<>();

    @Override
    public <T> boolean isDeepEquals(T a, T b) {
        if (a == null && b == null) {
            return true;
        }

        if (a != null && b != null) {
            Map<String, Object> options = new HashMap<>();
            Set<Class<?>> ignoreCustomEquals = new HashSet<>(ignoreCustomEqualsClasses);
            options.put(DeepEquals.IGNORE_CUSTOM_EQUALS, ignoreCustomEquals);

            return DeepEquals.deepEquals(a, b, options);
        }

        return false;
    }


    public void addIgnoreEqualsClass(Class<?> clazz) {
        if (ignoreCustomEqualsClasses == null) {
            ignoreCustomEqualsClasses = new HashSet<>();
        }
        ignoreCustomEqualsClasses.add(clazz);
    }

    public void addIgnoreEqualsClasses(Set<Class<?>> clazzes) {
        if (ignoreCustomEqualsClasses == null) {
            ignoreCustomEqualsClasses = new HashSet<>();
        }
        ignoreCustomEqualsClasses.addAll(clazzes);
    }

    public void removeIgnoreEqualsClass(Class<?> clazz) {
        if (ignoreCustomEqualsClasses != null) {
            ignoreCustomEqualsClasses.remove(clazz);
        }
    }

    public void removeIgnoreEqualsClasses(Set<Class<?>> clazzes) {
        if (ignoreCustomEqualsClasses != null) {
            ignoreCustomEqualsClasses.removeAll(clazzes);
        }
    }
}
