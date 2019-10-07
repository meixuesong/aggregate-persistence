package com.github.meixuesong.aggregatepersistence;

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

    private static final double DOUBLE_EPLISON = 1e-15;
    private static final double FLOAT_EPLISON = 1e-6;
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

    private <T> boolean typeIsNotMatch(Class<T> type) {
        if (! type.isInstance(a) && type.isInstance(b)) {
            return true;
        }

        if (type.isInstance(a) && ! type.isInstance(b)) {
            return true;
        }

        return false;
    }

    public boolean isTypeComparable() {
        Class[] classes = new Class[]{Collection.class, SortedSet.class, SortedMap.class, Map.class};
        for (Class aClass : classes) {
            if (typeIsNotMatch(aClass)) {
                return false;
            }
        }

        if (!isContainerType(a) && !isContainerType(b) && ! a.getClass().equals(b.getClass())) {
            return false;
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

    public boolean isPrimitiveEquals() {
        Class key1Class = a.getClass();

        return key1Class.isPrimitive() || classUseEquals.contains(key1Class);
    }

    public boolean primitiveEquals() {
        if (a == b) {
            return true;
        }

        if (a == null || b == null) {
            return false;
        }

        if (a instanceof Double) {
            return compareFloatingPointNumbers(a, b, DOUBLE_EPLISON);
        }

        if (a instanceof Float) {
            return compareFloatingPointNumbers(a, b, FLOAT_EPLISON);
        }

        Class key1Class = a.getClass();
        if (key1Class.isPrimitive() || classUseEquals.contains(key1Class)) {
            return a.equals(b);
        }

        throw new RuntimeException("Not primitive object, can't use this method.");
    }

    /**
     * Compare if two floating point numbers are within a given range
     */
    private static boolean compareFloatingPointNumbers(Object a, Object b, double epsilon) {
        double a1 = a instanceof Double ? (Double) a : (Float) a;
        double b1 = b instanceof Double ? (Double) b : (Float) b;
        return nearlyEqual(a1, b1, epsilon);
    }

    /**
     * Correctly handles floating point comparisions. <br>
     * source: http://floating-point-gui.de/errors/comparison/
     *
     * @param a       first number
     * @param b       second number
     * @param epsilon double tolerance value
     * @return true if a and b are close enough
     */
    private static boolean nearlyEqual(double a, double b, double epsilon) {
        final double absA = Math.abs(a);
        final double absB = Math.abs(b);
        final double diff = Math.abs(a - b);

        if (a == b) { // shortcut, handles infinities
            return true;
        } else if (a == 0 || b == 0 || diff < Double.MIN_NORMAL) {
            // a or b is zero or both are extremely close to it
            // relative error is less meaningful here
            return diff < (epsilon * Double.MIN_NORMAL);
        } else { // use relative error
            return diff / (absA + absB) < epsilon;
        }
    }

}
