package com.github.meixuesong.aggregatepersistence;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DeepEquals {
    private DeepEquals() {
    }

    public static final String IGNORE_CUSTOM_EQUALS = "ignoreCustomEquals";
    private static final Map<Class, Boolean> _customEquals = new ConcurrentHashMap<>();
    private static final Map<Class, Boolean> _customHash = new ConcurrentHashMap<>();

    /**
     * Compare two objects with a 'deep' comparison.  This will traverse the
     * Object graph and perform either a field-by-field comparison on each
     * object (if not .equals() method has been overridden from Object), or it
     * will call the customized .equals() method if it exists.  This method will
     * allow object graphs loaded at different times (with different object ids)
     * to be reliably compared.  Object.equals() / Object.hashCode() rely on the
     * object's identity, which would not consider to equivalent objects necessarily
     * equals.  This allows graphs containing instances of Classes that did no
     * overide .equals() / .hashCode() to be compared.  For example, testing for
     * existence in a cache.  Relying on an objects identity will not locate an
     * object in cache, yet relying on it being equivalent will.<br><br>
     * <p>
     * This method will handle cycles correctly, for example A-&gt;B-&gt;C-&gt;A.  Suppose a and
     * a' are two separate instances of the A with the same values for all fields on
     * A, B, and C.  Then a.deepEquals(a') will return true.  It uses cycle detection
     * storing visited objects in a Set to prevent endless loops.
     *
     * @param a Object one to compare
     * @param b Object two to compare
     * @return true if a is equivalent to b, false otherwise.  Equivalent means that
     * all field values of both subgraphs are the same, either at the field level
     * or via the respectively encountered overridden .equals() methods during
     * traversal.
     */
    public static boolean deepEquals(Object a, Object b) {
        return deepEquals(a, b, new HashMap());
    }

    /**
     * Compare two objects with a 'deep' comparison.  This will traverse the
     * Object graph and perform either a field-by-field comparison on each
     * object (if not .equals() method has been overridden from Object), or it
     * will call the customized .equals() method if it exists.  This method will
     * allow object graphs loaded at different times (with different object ids)
     * to be reliably compared.  Object.equals() / Object.hashCode() rely on the
     * object's identity, which would not consider to equivalent objects necessarily
     * equals.  This allows graphs containing instances of Classes that did no
     * overide .equals() / .hashCode() to be compared.  For example, testing for
     * existence in a cache.  Relying on an objects identity will not locate an
     * object in cache, yet relying on it being equivalent will.<br><br>
     * <p>
     * This method will handle cycles correctly, for example A-&gt;B-&gt;C-&gt;A.  Suppose a and
     * a' are two separate instances of the A with the same values for all fields on
     * A, B, and C.  Then a.deepEquals(a') will return true.  It uses cycle detection
     * storing visited objects in a Set to prevent endless loops.
     *
     * @param a       Object one to compare
     * @param b       Object two to compare
     * @param options Map options for compare. With no option, if a custom equals()
     *                method is present, it will be used.  If IGNORE_CUSTOM_EQUALS is
     *                present, it will be expected to be a Set of classes to ignore.
     *                It is a black-list of classes that will not be compared
     *                using .equals() even if the classes have a custom .equals() method
     *                present.  If it is and empty set, then no custom .equals() methods
     *                will be called.
     * @return true if a is equivalent to b, false otherwise.  Equivalent means that
     * all field values of both subgraphs are the same, either at the field level
     * or via the respectively encountered overridden .equals() methods during
     * traversal.
     */
    public static boolean deepEquals(Object a, Object b, Map<?, ?> options) {
        RecursiveObject recursiveObject = new RecursiveObject();
        Set<String> ignoreCustomEquals = (Set<String>) options.get(IGNORE_CUSTOM_EQUALS);

        recursiveObject.push(new DualObject(a, b));

        while (!recursiveObject.isEmpty()) {
            DualObject dualObject = recursiveObject.pop();

            if (dualObject.a == dualObject.b) {
                continue;
            }

            if (! dualObject.validateType()) {
                return false;
            }

            if (dualObject.shouldUseEqualMethod()) {
                if (! dualObject.compareByEquals()) {
                    return false;
                }

                continue;
            }

            if (dualObject.isContainer()) {
                if (!compareContainer(dualObject, recursiveObject)) {
                    return false;
                }

                continue;
            }

            Class key1Class = dualObject.a.getClass();
            if (hasCustomEquals(key1Class)) {
                if (ignoreCustomEquals == null || (ignoreCustomEquals.size() > 0 && !ignoreCustomEquals.contains(key1Class))) {
                    if (!dualObject.a.equals(dualObject.b)) {
                        return false;
                    }
                    continue;
                }
            }

            addFieldsToCompare(dualObject, recursiveObject);
        }

        return true;
    }

    private static void addFieldsToCompare(DualObject dualObject, RecursiveObject recursiveObject) {
        Collection<Field> fields = ReflectionUtils.getDeepDeclaredFields(dualObject.a.getClass());

        for (Field field : fields) {
            try {
                recursiveObject.push(new DualObject(field.get(dualObject.a), field.get(dualObject.b)));
            } catch (Exception ignored) {
                throw new RuntimeException(ignored);
            }
        }
    }

    private static boolean compareContainer(DualObject dualObject, RecursiveObject recursiveObject) {
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

    /**
     * Deeply compare to Arrays []. Both arrays must be of the same type, same length, and all
     * elements within the arrays must be deeply equal in order to return true.
     *
     * @param array1  [] type (Object[], String[], etc.)
     * @param array2  [] type (Object[], String[], etc.)
     * @param recursiveObject
     * @return true if the two arrays are the same length and contain deeply equivalent items.
     */
    private static boolean compareArrays(Object array1, Object array2, RecursiveObject recursiveObject) {
        if (Array.getLength(array1) != Array.getLength(array2)) {
            return false;
        }

        int length = Array.getLength(array1);
        for (int i = 0; i < length; i++) {
            DualObject dk = new DualObject(Array.get(array1, i), Array.get(array2, i));
            recursiveObject.push(dk);
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
     * @param col1    First collection of items to compare
     * @param col2    Second collection of items to compare
     * @param recursiveObject
     * @return boolean false if the Collections are for certain not equals. A
     * value of 'true' indicates that the Collections may be equal, and the sets
     * items will be added to the Stack for further comparison.
     */
    private static boolean compareUnorderedCollection(Collection col1, Collection col2, RecursiveObject recursiveObject) {
        if (col1.size() != col2.size()) {
            return false;
        }

        Map<Integer, Collection> fastLookup = new HashMap<>();
        for (Object o : col2) {
            int hash = deepHashCode(o);
            Collection items = fastLookup.get(hash);
            if (items == null) {
                items = new ArrayList();
                fastLookup.put(hash, items);
            }
            items.add(o);
        }

        for (Object o : col1) {
            Collection other = fastLookup.get(deepHashCode(o));
            if (other == null || other.isEmpty()) {   // fail fast: item not even found in other Collection, no need to continue.
                return false;
            }

            if (other.size() == 1) {   // no hash collision, items must be equivalent or deepEquals is false
                DualObject dk = new DualObject(o, other.iterator().next());
                recursiveObject.push(dk);
            } else {   // hash collision: try all collided items against the current item (if 1 equals, we are good - remove it
                // from collision list, making further comparisons faster)
                if (!isContained(o, other)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Deeply compare two Map instances.  After quick short-circuit tests, this method
     * uses a temporary Map so that this method can run in O(N) time.
     *
     * @param map1    Map one
     * @param map2    Map two
     * @param recursiveObject
     * @return false if the Maps are for certain not equals.  'true' indicates that 'on the surface' the maps
     * are equal, however, it will place the contents of the Maps on the stack for further comparisons.
     */
    private static boolean compareUnorderedMap(Map map1, Map map2, RecursiveObject recursiveObject) {
        // Same instance check already performed...

        if (map1.size() != map2.size()) {
            return false;
        }

        Map<Integer, Collection<Map.Entry>> fastLookup = new HashMap<>();

        for (Map.Entry entry : (Set<Map.Entry>) map2.entrySet()) {
            int hash = deepHashCode(entry.getKey());
            Collection items = fastLookup.get(hash);
            if (items == null) {
                items = new ArrayList();
                fastLookup.put(hash, items);
            }

            // Use only key and value, not specific Map.Entry type for equality check.
            // This ensures that Maps that might use different Map.Entry types still compare correctly.
            items.add(new AbstractMap.SimpleEntry(entry.getKey(), entry.getValue()));
        }

        for (Map.Entry entry : (Set<Map.Entry>) map1.entrySet()) {
            Collection<Map.Entry> other = fastLookup.get(deepHashCode(entry.getKey()));
            if (other == null || other.isEmpty()) {
                return false;
            }

            if (other.size() == 1) {
                Map.Entry entry2 = other.iterator().next();

                recursiveObject.push(new DualObject(entry.getKey(), entry2.getKey()));
                recursiveObject.push(new DualObject(entry.getValue(), entry2.getValue()));
            } else {   // hash collision: try all collided items against the current item (if 1 equals, we are good - remove it
                // from collision list, making further comparisons faster)
                if (!isContained(new AbstractMap.SimpleEntry(entry.getKey(), entry.getValue()), other)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * @return true of the passed in o is within the passed in Collection, using a deepEquals comparison
     * element by element.  Used only for hash collisions.
     */
    private static boolean isContained(Object o, Collection other) {
        Iterator i = other.iterator();
        while (i.hasNext()) {
            Object x = i.next();
            if (deepEquals(o, x)) {
                i.remove(); // can only be used successfully once - remove from list
                return true;
            }
        }
        return false;
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

            Collection<Field> fields = ReflectionUtils.getDeepDeclaredFields(obj.getClass());
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
