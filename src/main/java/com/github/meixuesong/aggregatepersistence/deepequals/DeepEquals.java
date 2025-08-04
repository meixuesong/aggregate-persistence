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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;


/**
 * Tests two objects for differences by doing a 'deep' comparison.
 *
 * Based on the deep equals implementation of https://github.com/jdereg/java-util
 *
 * @author John DeRegnaucourt (john@cedarsoftware.com)
 * @author meixuesong
 */
public class DeepEquals {
    private DeepEqualsOption deepEqualsOption;

    public DeepEquals() {
        deepEqualsOption = new DeepEqualsDefaultOption();
    }

    public DeepEquals(DeepEqualsOption deepEqualsDefaultOption) {
        deepEqualsOption = deepEqualsDefaultOption;
    }

    /**
     * Compare two objects with a 'deep' comparison.  This will traverse the
     * Object graph and perform either a field-by-field comparison on each
     * object, or it will call the customized .equals() method depends on options.
     *
     * This method will allow object graphs loaded at different times (with different object ids)
     * to be reliably compared.  Object.equals() / Object.hashCode() rely on the
     * object's identity, which would not consider to equivalent objects necessarily
     * equals.  This allows graphs containing instances of Classes that did no
     * overide .equals() / .hashCode() to be compared.  For example, testing for
     * existence in a cache.  Relying on an objects identity will not locate an
     * object in cache, yet relying on it being equivalent will.<br><br>
     * <p>
     * This method will handle cycles correctly, for example A-&gt;B-&gt;C-&gt;A.  Suppose a and
     * a' are two separate instances of the A with the same values for all fields on
     * A, B, and C.  Then a.isDeepEquals(a') will return true.  It uses cycle detection
     * storing visited objects in a Set to prevent endless loops.
     *
     * @param a Object one to compare
     * @param b Object two to compare
     * @return true if a is equivalent to b, false otherwise.  Equivalent means that
     * all field values of both subgraphs are the same, either at the field level
     * or via the respectively encountered overridden .equals() methods during
     * traversal.
     */
    public boolean isDeepEquals(Object a, Object b) {
        return doDeepCompare(a, b);
    }

    private boolean doDeepCompare(Object a, Object b) {
        RecursiveObject recursiveObject = new RecursiveObject();
        recursiveObject.push(new DualObject(a, b));

        return equalsRecursively(recursiveObject);
    }

    private boolean equalsRecursively(RecursiveObject recursiveObject) {
        while (!recursiveObject.isEmpty()) {
            DualObject dualObject = recursiveObject.pop();
            if (dualObject.a == dualObject.b) {
                continue;
            }

            if (! dualObject.validateType()) {
                return false;
            }

            BiPredicate<DualObject, RecursiveObject> compareFunction = getCompareFunction(dualObject);
            if (compareFunction != null) {
                if (! compareFunction.test(dualObject, recursiveObject)) {
                    return false;
                }

                continue;
            }

            addFieldsToCompare(dualObject, recursiveObject);
        }

        return true;
    }

    private BiPredicate<DualObject, RecursiveObject> getCompareFunction(DualObject dualObject) {
        if (dualObject.shouldUseEqualMethod()) {
            return this::compareByEquals;
        } else if (dualObject.isContainer()) {
            return this::compareContainer;
        } else if (shouldUseCustomEquals(dualObject)) {
            return this::compareByCustomEquals;
        } else if (shouldUseComparator(dualObject)) {
            return this::compareByComparator;
        }

        return null;
    }

    private boolean compareByComparator(DualObject dualObject, RecursiveObject recursiveObject) {
        Comparator comparator = deepEqualsOption.getUseComparatorClasses().get(dualObject.a.getClass());
        if (comparator == null) {
            throw new RuntimeException("Do not support comparator");
        }

        return comparator.compare(dualObject.a, dualObject.b) == 0;
    }

    private boolean shouldUseComparator(DualObject dualObject) {
        Comparator comparator = deepEqualsOption.getUseComparatorClasses().get(dualObject.a.getClass());

        return comparator != null;
    }

    private boolean shouldUseCustomEquals(DualObject dualObject) {
        return ReflectionUtils.hasCustomEquals(dualObject.a.getClass()) &&
                (
                        deepEqualsOption.getCustomEqualsClasses().contains(dualObject.a.getClass())
                                || !deepEqualsOption.isIgnoreCustomEquals()
                );
    }

    private boolean compareByCustomEquals(DualObject dualObject, RecursiveObject recursiveObject) {
        return dualObject.a.equals(dualObject.b);
    }

    private boolean compareByEquals(DualObject dualObject, RecursiveObject recursiveObject) {
        if (dualObject.a == dualObject.b) {
            return true;
        }

        if (dualObject.a == null || dualObject.b == null) {
            return false;
        }

        if (dualObject.a instanceof Double || dualObject.b instanceof Double) {
            return compareFloatingPointNumbers(dualObject.a, dualObject.b, deepEqualsOption.getDoubleOffSet());
        }

        if (dualObject.a instanceof Float || dualObject.b instanceof Float) {
            return compareFloatingPointNumbers(dualObject.a, dualObject.b, deepEqualsOption.getFloatOffSet());
        }

        return dualObject.a.equals(dualObject.b);
    }

    /**
     * Compare if two floating point numbers are within a given range
     */
    private boolean compareFloatingPointNumbers(Object a, Object b, double epsilon) {
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
    private boolean nearlyEqual(double a, double b, double epsilon) {
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

    private boolean addFieldsToCompare(DualObject dualObject, RecursiveObject recursiveObject) {
        Collection<Field> fields = ReflectionUtils.getDeepDeclaredFields(dualObject.a.getClass());

        Set<String> ignoredFieldNames = deepEqualsOption.getIgnoreFieldNames().get(dualObject.a.getClass());

        for (Field field : fields) {
            if (ignoredFieldNames != null && ignoredFieldNames.contains(field.getName())) {
                continue;
            }

            try {
                recursiveObject.push(new DualObject(field.get(dualObject.a), field.get(dualObject.b)));
            } catch (Exception ignored) {
                throw new RuntimeException(ignored);
            }
        }

        return true;
    }

    private boolean compareContainer(DualObject dualObject, RecursiveObject recursiveObject) {
        if (! dualObject.isSameSizeOfContainer()) {
            return false;
        }

        if (dualObject.isArrayContainer()) {
            return compareUnorderedCollection(array2List(dualObject.a), array2List(dualObject.b), recursiveObject);
        }

        if (dualObject.isCollectionContainer()) {
            return compareUnorderedCollection((Collection)dualObject.a, (Collection)dualObject.b, recursiveObject);
        }

        if (dualObject.isMapContainer()) {
            return compareUnorderedMap((Map) dualObject.a, (Map) dualObject.b, recursiveObject);
        }

        return true;
    }

    private static List array2List(Object array) {
        List results = new ArrayList();

        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            results.add(Array.get(array, i));
        }

        return results;
    }

    /**
     * Deeply compare the two sets referenced by dualKey.  This method attempts
     * to quickly determine inequality by length, then if lengths match, it
     * places one collection into a temporary Map by deepHashCode(), so that it
     * can walk the other collection and look for each item in the map, which
     * runs in O(N) time, rather than an O(N^2) lookup that would occur if each
     * item from collection one was scanned for in collection two.
     *
     * @param collectionA    First collection of items to compare
     * @param collectionB    Second collection of items to compare
     * @param recursiveObject
     * @return boolean false if the Collections are for certain not equals. A
     * value of 'true' indicates that the Collections may be equal, and the sets
     * items will be added to the Stack for further comparison.
     */
    private boolean compareUnorderedCollection(Collection collectionA, Collection collectionB, RecursiveObject recursiveObject) {
        Map<Integer, Collection> map = collection2Map(collectionB);

        for (Object item : collectionA) {
            int hashCode = ReflectionUtils.deepHashCode(item);
            Collection other = map.get(hashCode);
            // fail fast: item not even found in other Collection, no need to continue.
            if (other == null || other.isEmpty()) {
                return false;
            }

            // no hash collision, items must be equivalent or isDeepEquals is false
            if (other.size() == 1) {
                recursiveObject.push(new DualObject(item, other.iterator().next()));
                map.remove(hashCode);
            } else {
                // hash collision: try all collided items against the current item (if 1 equals, we are good - remove it
                // from collision list, making further comparisons faster)
                if (!isContained(item, other)) {
                    return false;
                }
            }
        }

        return true;
    }

    private Map<Integer, Collection> collection2Map(Collection collection) {
        Map<Integer, Collection> map = new HashMap<>();
        for (Object item : collection) {
            int hash = ReflectionUtils.deepHashCode(item);
            Collection items = map.get(hash);
            if (items == null) {
                items = new ArrayList();
                map.put(hash, items);
            }
            items.add(item);
        }
        return map;
    }

    /**
     * Deeply compare two Map instances.  After quick short-circuit tests, this method
     * uses a temporary Map so that this method can run in O(N) time.
     *
     * @param mapA    Map one
     * @param mapB    Map two
     * @param recursiveObject
     * @return false if the Maps are for certain not equals.  'true' indicates that 'on the surface' the maps
     * are equal, however, it will place the contents of the Maps on the stack for further comparisons.
     */
    private boolean compareUnorderedMap(Map mapA, Map mapB, RecursiveObject recursiveObject) {
        Map<Integer, Collection<Map.Entry>> mapEntryB = initMapByHashCode(mapB);

        for (Map.Entry entryA : (Set<Map.Entry>) mapA.entrySet()) {
            Collection<Map.Entry> other = mapEntryB.get(ReflectionUtils.deepHashCode(entryA.getKey()));
            if (other == null || other.isEmpty()) {
                return false;
            }

            if (other.size() == 1) {
                Map.Entry entryB = other.iterator().next();

                recursiveObject.push(new DualObject(entryA.getKey(), entryB.getKey()));
                recursiveObject.push(new DualObject(entryA.getValue(), entryB.getValue()));
            } else {
                // hash collision: try all collided items against the current item (if 1 equals, we are good - remove it
                // from collision list, making further comparisons faster)
                if (!isContained(new AbstractMap.SimpleEntry(entryA.getKey(), entryA.getValue()), other)) {
                    return false;
                }
            }
        }

        return true;
    }

    private Map<Integer, Collection<Map.Entry>> initMapByHashCode(Map map2) {
        Map<Integer, Collection<Map.Entry>> fastLookup = new HashMap<>();

        for (Map.Entry entry : (Set<Map.Entry>) map2.entrySet()) {
            int hash = ReflectionUtils.deepHashCode(entry.getKey());
            Collection items = fastLookup.get(hash);
            if (items == null) {
                items = new ArrayList();
                fastLookup.put(hash, items);
            }

            // Use only key and value, not specific Map.Entry type for equality check.
            // This ensures that Maps that might use different Map.Entry types still compare correctly.
            items.add(new AbstractMap.SimpleEntry(entry.getKey(), entry.getValue()));
        }
        return fastLookup;
    }

    /**
     * @return true of the passed in o is within the passed in Collection, using a isDeepEquals comparison
     * element by element.  Used only for hash collisions.
     */
    private boolean isContained(Object o, Collection other) {
        Iterator i = other.iterator();
        while (i.hasNext()) {
            Object x = i.next();
            if (isDeepEquals(o, x)) {
                // can only be used successfully once - remove from list
                i.remove();
                return true;
            }
        }
        return false;
    }

}
