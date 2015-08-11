package org.tomstools.common;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.tomstools.common.URLUtil;

public class URLUtilTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testRemoveURLQueryAttibute() {
        assertEquals("http://w.com/a?", URLUtil.removeURLQueryAttibute("http://w.com/a?key=bakk", "key"));
        assertEquals("http://w.com/a?", URLUtil.removeURLQueryAttibute("http://w.com/a?key=bakk&", "key"));
        assertEquals("http://w.com/a?#111", URLUtil.removeURLQueryAttibute("http://w.com/a?key=bakk#111", "key"));
        assertEquals("http://w.com/a?akey=bbb", URLUtil.removeURLQueryAttibute("http://w.com/a?key=bakk&akey=bbb", "key"));
        assertEquals("http://w.com/a?akey=bakk", URLUtil.removeURLQueryAttibute("http://w.com/a?akey=bakk", "key"));
    }

}
