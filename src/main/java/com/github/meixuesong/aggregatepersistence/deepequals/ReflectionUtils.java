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

package com.github.meixuesong.aggregatepersistence.deepequals;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Based on the deep equals implementation of https://github.com/jdereg/java-util
 *
 * @author John DeRegnaucourt (john@cedarsoftware.com)
 *         <br>
 *         Copyright (c) Cedar Software LLC
 *         <br><br>
 *         Licensed under the Apache License, Version 2.0 (the "License");
 *         you may not use this file except in compliance with the License.
 *         You may obtain a copy of the License at
 *         <br><br>
 *         http://www.apache.org/licenses/LICENSE-2.0
 *         <br><br>
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *         See the License for the specific language governing permissions and
 *         limitations under the License.
 */
public final class ReflectionUtils
{
    private static final Map<Class, Collection<Field>> _reflectedFields = new ConcurrentHashMap<>();
    private static final Map<Class, Boolean> _customEquals = new ConcurrentHashMap<>();
    private static final Map<Class, Boolean> _customHash = new ConcurrentHashMap<>();

    private ReflectionUtils()
    {
        super();
    }

    /**
     *
     * @return the Annotation if found, null otherwise.
     */
    /**
     * Determine if the passed in class (classToCheck) has the annotation (annoClass) on itself,
     *      * any of its super classes, any of it's interfaces, or any of it's super interfaces.
     *      * This is a exhaustive check throughout the complete inheritance hierarchy.
     * @param classToCheck classToCheck
     * @param annoClass annoClass
     * @param <T> T
     * @return T
     */
    public static <T extends Annotation> T getClassAnnotation(final Class<?> classToCheck, final Class<T> annoClass)
    {
        final Set<Class> visited = new HashSet<>();
        final LinkedList<Class> stack = new LinkedList<>();
        stack.add(classToCheck);

        while (!stack.isEmpty())
        {
            Class classToChk = stack.pop();
            if (classToChk == null || visited.contains(classToChk))
            {
                continue;
            }
            visited.add(classToChk);
            T a = (T) classToChk.getAnnotation(annoClass);
            if (a != null)
            {
                return a;
            }
            stack.push(classToChk.getSuperclass());
            addInterfaces(classToChk, stack);
        }
        return null;
    }

    private static void addInterfaces(final Class<?> classToCheck, final LinkedList<Class> stack)
    {
        for (Class interFace : classToCheck.getInterfaces())
        {
            stack.push(interFace);
        }
    }

    public static <T extends Annotation> T getMethodAnnotation(final Method method, final Class<T> annoClass)
    {
        final Set<Class> visited = new HashSet<>();
        final LinkedList<Class> stack = new LinkedList<>();
        stack.add(method.getDeclaringClass());

        while (!stack.isEmpty())
        {
            Class classToChk = stack.pop();
            if (classToChk == null || visited.contains(classToChk))
            {
                continue;
            }
            visited.add(classToChk);
            Method m = getMethod(classToChk, method.getName(), method.getParameterTypes());
            if (m == null)
            {
                continue;
            }
            T a = m.getAnnotation(annoClass);
            if (a != null)
            {
                return a;
            }
            stack.push(classToChk.getSuperclass());
            addInterfaces(method.getDeclaringClass(), stack);
        }
        return null;
    }

    public static Method getMethod(Class<?> c, String method, Class<?>...types)  {
        try
        {
            return c.getMethod(method, types);
        }
        catch (Exception nse)
        {
            return null;
        }
    }

    /**
     * Get all non static, non transient, fields of the passed in class, including
     * private fields. Note, the special this$ field is also not returned.  The result
     * is cached in a static ConcurrentHashMap to benefit execution performance.
     * @param c Class instance
     * @return Collection of only the fields in the passed in class
     * that would need further processing (reference fields).  This
     * makes field traversal on a class faster as it does not need to
     * continually process known fields like primitives.
     */
    public static Collection<Field> getDeepDeclaredFields(Class<?> c)
    {
        if (_reflectedFields.containsKey(c))
        {
            return _reflectedFields.get(c);
        }
        Collection<Field> fields = new ArrayList<>();
        Class curr = c;

        while (curr != null)
        {
            getDeclaredFields(curr, fields);
            curr = curr.getSuperclass();
        }
        _reflectedFields.put(c, fields);
        return fields;
    }

    /**
     * Get all non static, non transient, fields of the passed in class, including
     * private fields. Note, the special this$ field is also not returned.  The
     * resulting fields are stored in a Collection.
     * @param c Class instance
     * that would need further processing (reference fields).  This
     * makes field traversal on a class faster as it does not need to
     * continually process known fields like primitives.
     * @param fields fields
     */
    public static void getDeclaredFields(Class<?> c, Collection<Field> fields) {
        try
        {
            Field[] local = c.getDeclaredFields();

            for (Field field : local)
            {
                try
                {
                    field.setAccessible(true);
                }
                catch (Exception ignored) { }

                int modifiers = field.getModifiers();
                if (!Modifier.isStatic(modifiers) &&
                        !field.getName().startsWith("this$") &&
                        !Modifier.isTransient(modifiers))
                {   // speed up: do not count static fields, do not go back up to enclosing object in nested case, do not consider transients
                    fields.add(field);
                }
            }
        }
        catch (Throwable ignored)
        {
            throw new RuntimeException(ignored);
        }

    }

    /**
     * Return all Fields from a class (including inherited), mapped by
     * String field name to java.lang.reflect.Field.
     * @param c Class whose fields are being fetched.
     * @return Map of all fields on the Class, keyed by String field
     * name to java.lang.reflect.Field.
     */
    public static Map<String, Field> getDeepDeclaredFieldMap(Class<?> c)
    {
        Map<String, Field> fieldMap = new HashMap<>();
        Collection<Field> fields = getDeepDeclaredFields(c);
        for (Field field : fields)
        {
            String fieldName = field.getName();
            if (fieldMap.containsKey(fieldName))
            {   // Can happen when parent and child class both have private field with same name
                fieldMap.put(field.getDeclaringClass().getName() + '.' + fieldName, field);
            }
            else
            {
                fieldMap.put(fieldName, field);
            }
        }

        return fieldMap;
    }

    /**
     * Return the name of the class on the object, or "null" if the object is null.
     * @param o Object to get the class name.
     * @return String name of the class or "null"
     */
    public static String getClassName(Object o)
    {
        return o == null ? "null" : o.getClass().getName();
    }

    public static String getClassNameFromByteCode(byte[] byteCode) throws Exception
    {
        InputStream is = new ByteArrayInputStream(byteCode);
        DataInputStream dis = new DataInputStream(is);
        dis.readLong(); // skip header and class version
        int cpcnt = (dis.readShort() & 0xffff) - 1;
        int[] classes = new int[cpcnt];
        String[] strings = new String[cpcnt];
        for (int i=0; i < cpcnt; i++)
        {
            int t = dis.read();
            if (t == 7)
            {
                classes[i] = dis.readShort() & 0xffff;
            }
            else if (t == 1)
            {
                strings[i] = dis.readUTF();
            }
            else if (t == 5 || t == 6)
            {
                dis.readLong();
                i++;
            }
            else if (t == 8)
            {
                dis.readShort();
            }
            else
            {
                dis.readInt();
            }
        }
        dis.readShort(); // skip access flags
        return strings[classes[(dis.readShort() & 0xffff) - 1] - 1].replace('/', '.');
    }

    /**
     * Determine if the passed in class has a non-Object.equals() method.  This
     * method caches its results in static ConcurrentHashMap to benefit
     * execution performance.
     *
     * @param c Class to check.
     * @return true, if the passed in Class has a .equals() method somewhere between
     * itself and just below Object in it's inheritance.
     */
    public static boolean hasCustomEquals(Class<?> c) {
        Class origClass = c;
        if (_customEquals.containsKey(c)) {
            return _customEquals.get(c);
        }

        while (!Object.class.equals(c)) {
            try {
                c.getDeclaredMethod("equals", Object.class);
                _customEquals.put(origClass, true);
                return true;
            } catch (Exception ignored) {
            }
            c = c.getSuperclass();
        }
        _customEquals.put(origClass, false);
        return false;
    }

    /**
     * Get a deterministic hashCode (int) value for an Object, regardless of
     * when it was created or where it was loaded into memory.  The problem
     * with java.lang.Object.hashCode() is that it essentially relies on
     * memory location of an object (what identity it was assigned), whereas
     * this method will produce the same hashCode for any object graph, regardless
     * of how many times it is created.<br><br>
     * <p>
     * This method will handle cycles correctly (A-&gt;B-&gt;C-&gt;A).  In this case,
     * Starting with object A, B, or C would yield the same hashCode.  If an
     * object encountered (root, suboject, etc.) has a hashCode() method on it
     * (that is not Object.hashCode()), that hashCode() method will be called
     * and it will stop traversal on that branch.
     *
     * @param obj Object who hashCode is desired.
     * @return the 'deep' hashCode value for the passed in object.
     */
    public static int deepHashCode(Object obj) {
        Set<Object> visited = new HashSet<>();
        LinkedList<Object> stack = new LinkedList<>();
        stack.addFirst(obj);
        int hash = 0;

        while (!stack.isEmpty()) {
            obj = stack.removeFirst();
            if (obj == null || visited.contains(obj)) {
                continue;
            }

            visited.add(obj);

            if (obj.getClass().isArray()) {
                int len = Array.getLength(obj);
                for (int i = 0; i < len; i++) {
                    stack.addFirst(Array.get(obj, i));
                }
                continue;
            }

            if (obj instanceof Collection) {
                stack.addAll(0, (Collection) obj);
                continue;
            }

            if (obj instanceof Map) {
                stack.addAll(0, ((Map) obj).keySet());
                stack.addAll(0, ((Map) obj).values());
                continue;
            }

            if (obj instanceof Double || obj instanceof Float) {
                // just take the integral value for hashcode
                // equality tests things more comprehensively
                stack.add(Math.round(((Number) obj).doubleValue()));
                continue;
            }

            if (hasCustomHashCode(obj.getClass())) {   // A real hashCode() method exists, call it.
                hash += obj.hashCode();
                continue;
            }

            Collection<Field> fields = getDeepDeclaredFields(obj.getClass());
            for (Field field : fields) {
                try {
                    stack.addFirst(field.get(obj));
                } catch (Exception ignored) {
                }
            }
        }
        return hash;
    }

    /**
     * Determine if the passed in class has a non-Object.hashCode() method.  This
     * method caches its results in static ConcurrentHashMap to benefit
     * execution performance.
     *
     * @param c Class to check.
     * @return true, if the passed in Class has a .hashCode() method somewhere between
     * itself and just below Object in it's inheritance.
     */
    public static boolean hasCustomHashCode(Class<?> c) {
        Class origClass = c;
        if (_customHash.containsKey(c)) {
            return _customHash.get(c);
        }

        while (!Object.class.equals(c)) {
            try {
                c.getDeclaredMethod("hashCode");
                _customHash.put(origClass, true);
                return true;
            } catch (Exception ignored) {
            }
            c = c.getSuperclass();
        }
        _customHash.put(origClass, false);
        return false;
    }
}
