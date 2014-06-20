package org.tomstools.crawler.util;

import junit.framework.TestCase;

public class HTMLUtilTest extends TestCase {

    public HTMLUtilTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testProtocolPattern() throws Exception {
     // 包含协议头
        assertTrue(HTMLUtil.PATTERN_PROTOCOL.matcher("http://aaa.dd/").find());
        assertFalse(HTMLUtil.PATTERN_PROTOCOL.matcher("/aaa.dd://").find());
        assertFalse(HTMLUtil.PATTERN_PROTOCOL.matcher("aaa.dd:80//").find());
        assertFalse(HTMLUtil.PATTERN_PROTOCOL.matcher("aaa.dd://").find());
        assertFalse(HTMLUtil.PATTERN_PROTOCOL.matcher("aaa.dd//").find());
    }
    
    public void testHTMLDirectoryPattern() throws Exception {
        // 是文件
        assertTrue(HTMLUtil.PATTERN_DIRECTORY.matcher("http://aaa.dd/").find() );
        assertFalse(HTMLUtil.PATTERN_DIRECTORY.matcher("http://aaa.dd/a").find());
        assertFalse(HTMLUtil.PATTERN_DIRECTORY.matcher("http://aaa.dd/d?").find());
        assertTrue(HTMLUtil.PATTERN_DIRECTORY.matcher("http://aaa.dd/?").find());
        assertTrue(HTMLUtil.PATTERN_DIRECTORY.matcher("http://aaa.dd/a/?d").find());
        assertFalse(HTMLUtil.PATTERN_DIRECTORY.matcher("http://aaa.dd/a/b?d").find());
    }
}
