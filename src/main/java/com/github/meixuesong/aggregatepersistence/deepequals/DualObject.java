package com.github.meixuesong.aggregatepersistence.deepequals;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

class DualObject {
    public final Object a;
    public final Object b;

    private static final Set<Class> classUseEquals = new HashSet<>();

    static {
        classUseEquals.add(Byte.class);
        classUseEquals.add(Integer.class);
        classUseEquals.add(Long.class);
        classUseEquals.add(Double.class);
        classUseEquals.add(Character.class);
        classUseEquals.add(Float.class);
        classUseEquals.add(Boolean.class);
        classUseEquals.add(Short.class);
        classUseEquals.add(Date.class);
        classUseEquals.add(String.class);

        classUseEquals.add(Class.class);
    }


    public DualObject(Object a, Object b) {
        this.a = a;
        this.b = b;
    }

    public boolean validateType() {
        if (a == null || b == null) {
            return false;
        }

        if (!isTypeComparable()) {
            return false;
        }

        return true;
    }

    private <T> boolean collectionOrMapTypeIsMatch(Class<T> type) {
        if (type.isInstance(a)) {
            return type.isInstance(b);
        }

        if (type.isInstance(b)) {
            return type.isInstance(a);
        }

        return true;
    }

    public boolean isTypeComparable() {
        if (a.getClass().equals(b.getClass())) {
            return true;
        }

        if (!isContainerType(a) || !isContainerType(b)) {
            return false;
        }

        Class[] classes = new Class[]{Collection.class, SortedSet.class, SortedMap.class, Map.class};
        for (Class aClass : classes) {
            if (! collectionOrMapTypeIsMatch(aClass)) {
                return false;
            }
        }

        return true;
    }

    private boolean isContainerType(Object o) {
        return o instanceof Collection || o instanceof Map;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof DualObject)) {
            return false;
        }

        DualObject that = (DualObject) other;
        return a == that.a && b == that.b;
    }

    @Override
    public int hashCode() {
        int h1 = a != null ? a.hashCode() : 0;
        int h2 = b != null ? b.hashCode() : 0;
        return h1 + h2;
    }

    public boolean shouldUseEqualMethod() {
        Class key1Class = a.getClass();

        return key1Class.isPrimitive() || classUseEquals.contains(key1Class);
    }

    public boolean isContainer() {
        return isArrayContainer() || isCollectionContainer() || isMapContainer();
    }

    public boolean isArrayContainer() {
        return a.getClass().isArray();
    }

    public boolean isCollectionContainer() {
        return a instanceof Collection;
    }

    public boolean isMapContainer() {
        return a instanceof Map;
    }

    public boolean isSameSizeOfContainer() {
        if (a.getClass().isArray()) {
            return Array.getLength(a) == Array.getLength(b);
        }

        if (a instanceof Collection) {
            return ((Collection) a).size() == ((Collection) b).size();
        }

        if (a instanceof Map) {
            return ((Map) a).size() == ((Map) b).size();
        }

        throw new RuntimeException("It's not a container, can't use this method.");
    }
}
