/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/*
 * Based on Apache Harmony version of java.util.HashMap. Modified by Gil Tene.
 */

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

//import org.apache.harmony.testframework.serialization.SerializationTest;

import tests.support.Support_MapTest2;
import tests.support.Support_UnmodifiableCollectionTest;
import org.junit.Test;

public class PauselessHashMapTest extends junit.framework.TestCase {
    class MockMap extends AbstractMap {
        public Set entrySet() {
            return null;
        }
        public int size(){
            return 0;
        }
    }

    private static class MockMapNull extends AbstractMap {
        public Set entrySet() {
            return null;
        }

        public int size() {
            return 10;
        }
    }

    interface MockInterface {
        public String mockMethod();
    }

    class MockClass implements MockInterface {
        public String mockMethod() {
            return "This is a MockClass";
        }
    }

    class MockHandler implements InvocationHandler {

        Object obj;

        public MockHandler(Object o) {
            obj = o;
        }

        public Object invoke(Object proxy, Method m, Object[] args)
                throws Throwable {

            Object result = null;

            try {

                result = m.invoke(obj, args);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
            return result;
        }

    }


    PauselessHashMap hm;

    final static int hmSize = 1000;

    static Object[] objArray;

    static Object[] objArray2;
    {
        objArray = new Object[hmSize];
        objArray2 = new Object[hmSize];
        for (int i = 0; i < objArray.length; i++) {
            objArray[i] = new Integer(i);
            objArray2[i] = objArray[i].toString();
        }
    }

    /**
     * @tests java.util.PauselessHashMap#PauselessHashMap()
     */
    @Test
    public void test_Constructor() {
        // Test for method java.util.PauselessHashMap()
        new Support_MapTest2(new PauselessHashMap()).runTest();

        PauselessHashMap hm2 = new PauselessHashMap();
        assertEquals("Created incorrect PauselessHashMap", 0, hm2.size());
    }

    /**
     * @tests java.util.PauselessHashMap#PauselessHashMap(int)
     */
    @Test
    public void test_ConstructorI() {
        // Test for method java.util.PauselessHashMap(int)
        PauselessHashMap hm2 = new PauselessHashMap(5);
        assertEquals("Created incorrect PauselessHashMap", 0, hm2.size());
        try {
            new PauselessHashMap(-1);
        } catch (IllegalArgumentException e) {
            return;
        }
        fail(
                "Failed to throw IllegalArgumentException for initial capacity < 0");

        PauselessHashMap empty = new PauselessHashMap(0);
        assertNull("Empty hashmap access", empty.get("nothing"));
        empty.put("something", "here");
        assertTrue("cannot get element", empty.get("something") == "here");
    }

    /**
     * @tests java.util.PauselessHashMap#PauselessHashMap(int, float)
     */
    @Test
    public void test_ConstructorIF() {
        // Test for method java.util.PauselessHashMap(int, float)
        PauselessHashMap hm2 = new PauselessHashMap(5, (float) 0.5);
        assertEquals("Created incorrect PauselessHashMap", 0, hm2.size());
        try {
            new PauselessHashMap(0, 0);
        } catch (IllegalArgumentException e) {
            return;
        }
        fail(
                "Failed to throw IllegalArgumentException for initial load factor <= 0");

        PauselessHashMap empty = new PauselessHashMap(0, 0.75f);
        assertNull("Empty hashtable access", empty.get("nothing"));
        empty.put("something", "here");
        assertTrue("cannot get element", empty.get("something") == "here");
    }

    /**
     * @tests java.util.PauselessHashMap#PauselessHashMap(java.util.Map)
     */
    @Test
    public void test_ConstructorLjava_util_Map() {
        // Test for method java.util.PauselessHashMap(java.util.Map)
        Map myMap = new TreeMap();
        Object o0;

        for (int counter = 0; counter < hmSize; counter++) {
            myMap.put(objArray2[counter], objArray[counter]);
        }

        PauselessHashMap hm2 = new PauselessHashMap(myMap);

        for (int counter = 0; counter < hmSize; counter++) {
            assertTrue("Failed to construct correct PauselessHashMap @counter = " + counter,
                    hm.get(objArray2[counter]) == hm2.get(objArray2[counter]));
        }

        try {
            Map mockMap = new MockMap();
            hm = new PauselessHashMap(mockMap);
            fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            //empty
        }

        PauselessHashMap map = new PauselessHashMap();
        map.put("a", "a");
        SubMap map2 = new SubMap(map);
        assertTrue(map2.containsKey("a"));
        assertTrue(map2.containsValue("a"));
    }

    /**
     * @tests java.util.PauselessHashMap#clear()
     */
    @Test
    public void test_clear() {
        hm.clear();
        assertEquals("Clear failed to reset size", 0, hm.size());
        for (int i = 0; i < hmSize; i++)
            assertNull("Failed to clear all elements",
                    hm.get(objArray2[i]));

        // Check clear on a large loaded map of Integer keys
        PauselessHashMap<Integer, String> map = new PauselessHashMap<Integer, String>();
        for (int i = -32767; i < 32768; i++) {
            map.put(i, "foobar");
        }
        map.clear();
        assertEquals("Failed to reset size on large integer map", 0, hm.size());
        for (int i = -32767; i < 32768; i++) {
            assertNull("Failed to clear integer map values", map.get(i));
        }
    }

    /**
     * @tests java.util.PauselessHashMap#clone()
     */
    @Test
    public void test_clone() {
        // Test for method java.lang.Object java.util.PauselessHashMap.clone()
        PauselessHashMap hm2 = (PauselessHashMap) hm.clone();
        assertTrue("Clone answered equivalent PauselessHashMap", hm2 != hm);
        for (int counter = 0; counter < hmSize; counter++)
            assertTrue("Clone answered unequal PauselessHashMap", hm
                    .get(objArray2[counter]) == hm2.get(objArray2[counter]));

        PauselessHashMap map = new PauselessHashMap();
        map.put("key", "value");
        // get the keySet() and values() on the original Map
        Set keys = map.keySet();
        Collection values = map.values();
        assertEquals("values() does not work",
                "value", values.iterator().next());
        assertEquals("keySet() does not work",
                "key", keys.iterator().next());
        AbstractMap map2 = (AbstractMap) map.clone();
        map2.put("key", "value2");
        Collection values2 = map2.values();
        assertTrue("values() is identical", values2 != values);
        // values() and keySet() on the cloned() map should be different
        assertEquals("values() was not cloned",
                "value2", values2.iterator().next());
        map2.clear();
        map2.put("key2", "value3");
        Set key2 = map2.keySet();
        assertTrue("keySet() is identical", key2 != keys);
        assertEquals("keySet() was not cloned",
                "key2", key2.iterator().next());

        // regresion test for HARMONY-4603
        PauselessHashMap hashmap = new PauselessHashMap();
        MockClonable mock = new MockClonable(1);
        hashmap.put(1, mock);
        assertEquals(1, ((MockClonable) hashmap.get(1)).i);
        PauselessHashMap hm3 = (PauselessHashMap)hashmap.clone();
        assertEquals(1, ((MockClonable) hm3.get(1)).i);
        mock.i = 0;
        assertEquals(0, ((MockClonable) hashmap.get(1)).i);
        assertEquals(0, ((MockClonable) hm3.get(1)).i);
    }

    /**
     * @tests java.util.PauselessHashMap#containsKey(java.lang.Object)
     */
    @Test
    public void test_containsKeyLjava_lang_Object() {
        // Test for method boolean
        // java.util.PauselessHashMap.containsKey(java.lang.Object)
        assertTrue("Returned false for valid key", hm.containsKey(new Integer(
                876).toString()));
        assertTrue("Returned true for invalid key", !hm.containsKey("KKDKDKD"));

        PauselessHashMap m = new PauselessHashMap();
        m.put(null, "test");
        assertTrue("Failed with null key", m.containsKey(null));
        assertTrue("Failed with missing key matching null hash", !m
                .containsKey(new Integer(0)));
    }

    /**
     * @tests java.util.PauselessHashMap#containsValue(java.lang.Object)
     */
    @Test
    public void test_containsValueLjava_lang_Object() {
        // Test for method boolean
        // java.util.PauselessHashMap.containsValue(java.lang.Object)
        assertTrue("Returned false for valid value", hm
                .containsValue(new Integer(875)));
        assertTrue("Returned true for invalid valie", !hm
                .containsValue(new Integer(-9)));
    }

    /**
     * @tests java.util.PauselessHashMap#entrySet()
     */
    @Test
    public void test_entrySet() {
        // Test for method java.util.Set java.util.PauselessHashMap.entrySet()
        Set s = hm.entrySet();
        Iterator i = s.iterator();
        assertTrue("Returned set of incorrect size", hm.size() == s.size());
        while (i.hasNext()) {
            Map.Entry m = (Map.Entry) i.next();
            assertTrue("Returned incorrect entry set", hm.containsKey(m
                    .getKey())
                    && hm.containsValue(m.getValue()));
        }

        Iterator iter = s.iterator();
        s.remove(iter.next());
        assertEquals(1001, s.size());
    }

    /**
     * @tests java.util.PauselessHashMap#get(java.lang.Object)
     */
    @Test
    public void test_getLjava_lang_Object() {
        // Test for method java.lang.Object
        // java.util.PauselessHashMap.get(java.lang.Object)
        assertNull("Get returned non-null for non existent key",
                hm.get("T"));
        hm.put("T", "HELLO");
        assertEquals("Get returned incorrect value for existing key", "HELLO", hm.get("T")
        );

        PauselessHashMap m = new PauselessHashMap();
        m.put(null, "test");
        assertEquals("Failed with null key", "test", m.get(null));
        assertNull("Failed with missing key matching null hash", m
                .get(new Integer(0)));

        // Regression for HARMONY-206
        ReusableKey k = new ReusableKey();
        PauselessHashMap map = new PauselessHashMap();
        k.setKey(1);
        map.put(k, "value1");

        k.setKey(18);
        assertNull(map.get(k));

        k.setKey(17);
        assertNull(map.get(k));
    }

    /**
     * Tests for proxy object keys and values
     */
    @Test
    public void test_proxies() {
        // Regression for HARMONY-6237
        MockInterface proxyKey = (MockInterface) Proxy.newProxyInstance(
                MockInterface.class.getClassLoader(),
                new Class[] { MockInterface.class }, new MockHandler(
                new MockClass()));
        MockInterface proxyValue = (MockInterface) Proxy.newProxyInstance(
                MockInterface.class.getClassLoader(),
                new Class[] { MockInterface.class }, new MockHandler(
                new MockClass()));

        // Proxy key
        Object val = new Object();
        hm.put(proxyKey, val);

        assertEquals("Failed with proxy object key", val, hm
                .get(proxyKey));
        assertTrue("Failed to find proxy key", hm.containsKey(proxyKey));
        assertEquals("Failed to remove proxy object key", val,
                hm.remove(proxyKey));
        assertFalse("Should not have found proxy key", hm.containsKey(proxyKey));

        // Proxy value
        Object k = new Object();
        hm.put(k, proxyValue);

        assertTrue("Failed to find proxy object as value", hm.containsValue(proxyValue));

        // Proxy key and value
        PauselessHashMap map = new PauselessHashMap();
        map.put(proxyKey, proxyValue);
        assertTrue("Failed to find proxy key", map.containsKey(proxyKey));
        assertEquals(1, map.size());
        Object[] entries = map.entrySet().toArray();
        Map.Entry entry = (Map.Entry)entries[0];
        assertTrue("Failed to find proxy association", map.entrySet().contains(entry));
    }

    /**
     * @tests java.util.PauselessHashMap#isEmpty()
     */
    @Test
    public void test_isEmpty() {
        // Test for method boolean java.util.PauselessHashMap.isEmpty()
        assertTrue("Returned false for new map", new PauselessHashMap().isEmpty());
        assertTrue("Returned true for non-empty", !hm.isEmpty());
    }

    /**
     * @tests java.util.PauselessHashMap#keySet()
     */
    @Test
    public void test_keySet() {
        // Test for method java.util.Set java.util.PauselessHashMap.keySet()
        Set s = hm.keySet();
        assertTrue("Returned set of incorrect size()", s.size() == hm.size());
        for (int i = 0; i < objArray.length; i++)
            assertTrue("Returned set does not contain all keys", s
                    .contains(objArray[i].toString()));

        PauselessHashMap m = new PauselessHashMap();
        m.put(null, "test");
        assertTrue("Failed with null key", m.keySet().contains(null));
        assertNull("Failed with null key", m.keySet().iterator().next());

        Map map = new PauselessHashMap(101);
        map.put(new Integer(1), "1");
        map.put(new Integer(102), "102");
        map.put(new Integer(203), "203");
        Iterator it = map.keySet().iterator();
        Integer remove1 = (Integer) it.next();
        it.hasNext();
        it.remove();
        Integer remove2 = (Integer) it.next();
        it.remove();
        ArrayList list = new ArrayList(Arrays.asList(new Integer[] {
                new Integer(1), new Integer(102), new Integer(203) }));
        list.remove(remove1);
        list.remove(remove2);
        assertTrue("Wrong result", it.next().equals(list.get(0)));
        assertEquals("Wrong size", 1, map.size());
        assertTrue("Wrong contents", map.keySet().iterator().next().equals(
                list.get(0)));

        Map map2 = new PauselessHashMap(101);
        map2.put(new Integer(1), "1");
        map2.put(new Integer(4), "4");
        Iterator it2 = map2.keySet().iterator();
        Integer remove3 = (Integer) it2.next();
        Integer next;
        if (remove3.intValue() == 1)
            next = new Integer(4);
        else
            next = new Integer(1);
        it2.hasNext();
        it2.remove();
        assertTrue("Wrong result 2", it2.next().equals(next));
        assertEquals("Wrong size 2", 1, map2.size());
        assertTrue("Wrong contents 2", map2.keySet().iterator().next().equals(
                next));
    }

    /**
     * @tests java.util.PauselessHashMap#put(java.lang.Object, java.lang.Object)
     */
    @Test
    public void test_putLjava_lang_ObjectLjava_lang_Object() {
        hm.put("KEY", "VALUE");
        assertEquals("Failed to install key/value pair", "VALUE", hm.get("KEY"));

        PauselessHashMap<Object,Object> m = new PauselessHashMap<Object,Object>();
        m.put(new Short((short) 0), "short");
        m.put(null, "test");
        m.put(new Integer(0), "int");
        assertEquals("Failed adding to bucket containing null", "short", m
                .get(new Short((short) 0)));
        assertEquals("Failed adding to bucket containing null2", "int", m
                .get(new Integer(0)));

        // Check my actual key instance is returned
        PauselessHashMap<Integer, String> map = new PauselessHashMap<Integer, String>();
        for (int i = -32767; i < 32768; i++) {
            map.put(i, "foobar");
        }
        Integer myKey = new Integer(0);
        // Put a new value at the old key position
        map.put(myKey, "myValue");
        assertTrue(map.containsKey(myKey));
        assertEquals("myValue", map.get(myKey));
        boolean found = false;
        for (Iterator<Integer> itr = map.keySet().iterator(); itr.hasNext();) {
            Integer key = itr.next();
            if (found = key == myKey) {
                break;
            }
        }
        assertFalse("Should not find new key instance in hashmap", found);

        // Add a new key instance and check it is returned
        assertNotNull(map.remove(myKey));
        map.put(myKey, "myValue");
        assertTrue(map.containsKey(myKey));
        assertEquals("myValue", map.get(myKey));
        for (Iterator<Integer> itr = map.keySet().iterator(); itr.hasNext();) {
            Integer key = itr.next();
            if (found = key == myKey) {
                break;
            }
        }
        assertTrue("Did not find new key instance in hashmap", found);

        // Ensure keys with identical hashcode are stored separately
        PauselessHashMap<Object,Object> objmap = new PauselessHashMap<Object, Object>();
        for (int i = 0; i < 32768; i++) {
            objmap.put(i, "foobar");
        }
        // Put non-equal object with same hashcode
        MyKey aKey = new MyKey();
        assertNull(objmap.put(aKey, "value"));
        assertNull(objmap.remove(new MyKey()));
        assertEquals("foobar", objmap.get(0));
        assertEquals("value", objmap.get(aKey));
    }

    static class MyKey {
        public MyKey() {
            super();
        }

        public int hashCode() {
            return 0;
        }
    }
    /**
     * @tests java.util.PauselessHashMap#putAll(java.util.Map)
     */
    @Test
    public void test_putAllLjava_util_Map() {
        // Test for method void java.util.PauselessHashMap.putAll(java.util.Map)
        PauselessHashMap hm2 = new PauselessHashMap();
        hm2.putAll(hm);
        for (int i = 0; i < 1000; i++)
            assertTrue("Failed to clear all elements", hm2.get(
                    new Integer(i).toString()).equals((new Integer(i))));

        Map mockMap = new MockMap();
        hm2 = new PauselessHashMap();
        hm2.putAll(mockMap);
        assertEquals("Size should be 0", 0, hm2.size());
    }

    /**
     * @tests java.util.PauselessHashMap#putAll(java.util.Map)
     */
    @Test
    public void test_putAllLjava_util_Map_Null() {
        PauselessHashMap hashMap = new PauselessHashMap();
        try {
            hashMap.putAll(new MockMapNull());
            fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected.
        }

        try {
            hashMap = new PauselessHashMap(new MockMapNull());
            fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected.
        }
    }

    /**
     * @tests java.util.PauselessHashMap#remove(java.lang.Object)
     */
    @Test
    public void test_removeLjava_lang_Object() {
        int size = hm.size();
        Integer y = new Integer(9);
        Integer x = ((Integer) hm.remove(y.toString()));
        assertTrue("Remove returned incorrect value", x.equals(new Integer(9)));
        assertNull("Failed to remove given key", hm.get(new Integer(9)));
        assertTrue("Failed to decrement size", hm.size() == (size - 1));
        assertNull("Remove of non-existent key returned non-null", hm
                .remove("LCLCLC"));

        PauselessHashMap m = new PauselessHashMap();
        m.put(null, "test");
        assertNull("Failed with same hash as null",
                m.remove(new Integer(0)));
        assertEquals("Failed with null key", "test", m.remove(null));

        PauselessHashMap<Integer, Object> map = new PauselessHashMap<Integer, Object>();
        for (int i = 0; i < 32768; i++) {
            map.put(i, "const");
        }
        Object[] values = new Object[32768];
        for (int i = 0; i < 32768; i++) {
            values[i] = new Object();
            map.put(i, values[i]);
        }
        for (int i = 32767; i >= 0; i--) {
            assertEquals("Failed to remove same value", values[i], map.remove(i));
        }

        // Ensure keys with identical hashcode are removed properly
        map = new PauselessHashMap<Integer, Object>();
        for (int i = -32767; i < 32768; i++) {
            map.put(i, "foobar");
        }
        // Remove non equal object with same hashcode
        assertNull(map.remove(new MyKey()));
        assertEquals("foobar", map.get(0));
        map.remove(0);
        assertNull(map.get(0));
    }

    /**
     * Compatibility test to ensure we rehash the same way as the RI.
     * Not required by the spec, but some apps seem sensitive to it.
     */
    @Test
    public void test_rehash() {
        // This map should rehash on adding the ninth element.
        PauselessHashMap<MyKey, Integer> hm = new PauselessHashMap<MyKey, Integer>(10, 0.5f);

        // Ordered set of keys.
        MyKey[] keyOrder = new MyKey[9];
        for (int i = 0; i < keyOrder.length; i++) {
            keyOrder[i] = new MyKey();
        }

        // Store eight elements
        for (int i = 0; i < 8; i++) {
            hm.put(keyOrder[i], i);
        }
        // Check expected ordering (inverse of adding order)
        MyKey[] returnedKeys = hm.keySet().toArray(new MyKey[8]);
        for (int i = 0; i < 8; i++) {
            assertSame(keyOrder[i], returnedKeys[7 - i]);
        }

        // The next put causes a rehash
        hm.put(keyOrder[8], 8);
        // Check expected new ordering (adding order)
        returnedKeys = hm.keySet().toArray(new MyKey[8]);
        for (int i = 0; i < 9; i++) {
            assertSame(keyOrder[i], returnedKeys[i]);
        }
    }

    /**
     * @tests java.util.PauselessHashMap#size()
     */
    @Test
    public void test_size() {
        // Test for method int java.util.PauselessHashMap.size()
        assertTrue("Returned incorrect size",
                hm.size() == (objArray.length + 2));
    }

    /**
     * @tests java.util.PauselessHashMap#values()
     */
    @Test
    public void test_values() {
        // Test for method java.util.Collection java.util.PauselessHashMap.values()
        Collection c = hm.values();
        assertTrue("Returned collection of incorrect size()", c.size() == hm
                .size());
        for (int i = 0; i < objArray.length; i++)
            assertTrue("Returned collection does not contain all keys", c
                    .contains(objArray[i]));

        PauselessHashMap myHashMap = new PauselessHashMap();
        for (int i = 0; i < 100; i++)
            myHashMap.put(objArray2[i], objArray[i]);
        Collection values = myHashMap.values();
        new Support_UnmodifiableCollectionTest(
                "Test Returned Collection From PauselessHashMap.values()", values)
                .runTest();
        values.remove(new Integer(0));
        assertTrue(
                "Removing from the values collection should remove from the original map",
                !myHashMap.containsValue(new Integer(0)));

    }

    /**
     * @tests java.util.AbstractMap#toString()
     */
    @Test
    public void test_toString() {

        PauselessHashMap m = new PauselessHashMap();
        m.put(m, m);
        String result = m.toString();
        assertTrue("should contain self ref", result.indexOf("(this") > -1);
    }

    static class ReusableKey {
        private int key = 0;

        public void setKey(int key) {
            this.key = key;
        }

        public int hashCode() {
            return key;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof ReusableKey)) {
                return false;
            }
            return key == ((ReusableKey) o).key;
        }
    }

    @Test
    public void test_Map_Entry_hashCode() {
        //Related to HARMONY-403
        PauselessHashMap<Integer, Integer> map = new PauselessHashMap<Integer, Integer>(10);
        Integer key = new Integer(1);
        Integer val = new Integer(2);
        map.put(key, val);
        int expected = key.hashCode() ^ val.hashCode();
        assertEquals(expected, map.hashCode());
        key = new Integer(4);
        val = new Integer(8);
        map.put(key, val);
        expected += key.hashCode() ^ val.hashCode();
        assertEquals(expected, map.hashCode());
    }

    class MockClonable implements Cloneable{
        public int i;

        public MockClonable(int i) {
            this.i = i;
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return new MockClonable(i);
        }
    }

    /*
     * Regression test for HY-4750
     */
    @Test
    public void test_EntrySet() {
        PauselessHashMap map = new PauselessHashMap();
        map.put(new Integer(1), "ONE");

        Set entrySet = map.entrySet();
        Iterator e = entrySet.iterator();
        Object real = e.next();
        Map.Entry copyEntry = new MockEntry();
        assertEquals(real, copyEntry);
        assertTrue(entrySet.contains(copyEntry));

        entrySet.remove(copyEntry);
        assertFalse(entrySet.contains(copyEntry));


    }

    private static class MockEntry implements Map.Entry {

        public Object getKey() {
            return new Integer(1);
        }

        public Object getValue() {
            return "ONE";
        }

        public Object setValue(Object object) {
            return null;
        }
    }

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() {
        hm = new PauselessHashMap();
        for (int i = 0; i < objArray.length; i++) {
            hm.put(objArray2[i], objArray[i]);
            if (hm.size() != i+1) {
                assertTrue("Missed an insert", hm.size() == i+1);
            }
        }
        if (hm.get(objArray2[10]) == null) {
            assertTrue("Missed an insert of 10", hm.get(objArray2[10]) != null);
        }

        hm.put("test", null);
        hm.put(null, "test");
    }


    class SubMap<K, V> extends PauselessHashMap<K, V> {
        public SubMap(Map<? extends K, ? extends V> m) {
            super(m);
        }

        public V put(K key, V value) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * @tests serialization/deserialization.
     */
    @Test
    public void testSerializationSelf() throws Exception {
        PauselessHashMap<String, String> hm = new PauselessHashMap<String, String>();
        hm.put("key", "value");

//        SerializationTest.verifySelf(hm);

        //  regression for HARMONY-1583
        hm.put(null, "null");
//        SerializationTest.verifySelf(hm);
    }

    /**
     * @tests serialization/deserialization compatibility with RI.
     */
    @Test
    public void testSerializationCompatibility() throws Exception {
        PauselessHashMap<String, String> hm = new PauselessHashMap<String, String>();
        hm.put("key", "value");

//        SerializationTest.verifyGolden(this, hm);
    }

}
