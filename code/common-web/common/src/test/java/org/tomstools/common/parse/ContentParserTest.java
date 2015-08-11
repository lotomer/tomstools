package org.tomstools.common.parse;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.tomstools.common.parse.ContentParser;
import org.tomstools.common.parse.HTMLContentParser;
import org.tomstools.common.parse.JSONContentParser;

public class ContentParserTest {

    @Before
    public void setUp() throws Exception {
        
    }

    @Test
    public void testParseWithJSON() {
        ContentParser parser = new JSONContentParser();
        String content = "{a:{b:{c:\"Hello\",cc:3},bb:2},aa:1}";
        parser.init(content);
        assertEquals(1, parser.parse("aa", null));
        assertEquals(2, parser.parse("a.bb", null));
        assertEquals(3, parser.parse("a.b.cc", null));
        assertEquals("Hello", parser.parse("a.b.c", null));
        assertNull(parser.parse("d", null));
        assertNull(parser.parse("a.bbb", null));
    }
    @Test
    public void testParseWithJSONArray() {
        ContentParser parser = new JSONContentParser();
        String content = "[{a:{b:{c:\"Hello\",cc:3},bb:2},aa:1},{a:0}]";
        parser.init(content);
        assertEquals(1, parser.parse("0.aa", null));
        assertEquals(2, parser.parse("0.a.bb", null));
        assertEquals(3, parser.parse("0.a.b.cc", null));
        assertEquals("Hello", parser.parse("0.a.b.c", null));
        assertNull(parser.parse("0.d", null));
        assertNull(parser.parse("0.a.bbb", null));
        
        assertEquals(0, parser.parse("1.a", null));
        assertNull(parser.parse("1.d", null));
        assertNull(parser.parse("1.a.bbb", null));
    }
    @Test
    public void testParseWithHTML() {
        ContentParser parser = new HTMLContentParser();
        String content = "<a><b><c>Hello</c><cc>3</cc></b><bb>2</bb></a><aa>1</aa>";
        parser.init(content);
        assertEquals("1", parser.parse("aa", null));
        assertEquals("2", parser.parse("a>bb", null));
        assertEquals("3", parser.parse("a>b>cc", null));
        assertEquals("Hello", parser.parse("a>b>c", null));
        assertNull(parser.parse("d", null));
        assertNull(parser.parse("a>bbb", null));
    }

}
