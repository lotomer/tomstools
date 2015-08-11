package org.tomstools.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.tomstools.common.PropertyParseAssistor;

public class PropertyParseAssistorTest {
    private static class B {
        String c;
    }

    private static class A {
        B b;
    }

    @Test
    public void testGetPropertyObjectString() {
        A obj = new A();
        obj.b = new B();
        obj.b.c = "Hello";
        assertEquals(obj.b.c, PropertyParseAssistor.getProperty(obj, "b.c"));
        assertNull(PropertyParseAssistor.getProperty(obj, "b.d"));
        assertNull(PropertyParseAssistor.getProperty(obj, "b.c.d"));
    }

    @Test
    public void testGetPropertyObjectStringString() {
        A obj = new A();
        obj.b = new B();
        obj.b.c = "Hello";
        assertEquals(obj.b.c, PropertyParseAssistor.getProperty(obj, "b.c","."));
        assertEquals(obj.b.c, PropertyParseAssistor.getProperty(obj, "b>c",">"));
        assertEquals(obj.b.c, PropertyParseAssistor.getProperty(obj, "b->c","->"));
    }

    @Test
    public void testGetPropertyObjectStringArray() {
        A obj = new A();
        obj.b = new B();
        obj.b.c = "Hello";
        assertEquals(obj.b.c, PropertyParseAssistor.getProperty(obj, new String[] { "b", "c" }));
    }

}
