package org.tomstools.crawler.web.util;

import junit.framework.TestCase;

public class JspMatcherTest extends TestCase {

    public JspMatcherTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testIsMatched() {
        assertTrue(JspMatcher.isMatched("http://d.cm/a.jsp"));
        assertTrue(JspMatcher.isMatched("http://d.cm/a.jsp/b.jsp"));
        assertTrue(JspMatcher.isMatched("http://d.cm/jsp/b.jsp"));
        assertTrue(JspMatcher.isMatched("http://d.cm/a.JSP"));
        assertTrue(JspMatcher.isMatched("http://d.cm/a.Jsp"));
        assertTrue(JspMatcher.isMatched("http://d.cm/a.jsp?a=wer&b"));
        assertTrue(JspMatcher.isMatched("http://d.cm/a.jsp#asdf"));
        assertTrue(JspMatcher.isMatched("http://d.cm/b/c=w/a.jsp"));

        assertFalse(JspMatcher.isMatched("http://d.cm/a.jsp/"));
        assertFalse(JspMatcher.isMatched("http://d.cm/a.jsp/sd"));
        assertFalse(JspMatcher.isMatched("http://d.cm/jsp"));
        assertFalse(JspMatcher.isMatched("http://d.cm/.jsp"));
        assertFalse(JspMatcher.isMatched("http://d.cm/a.jspx"));
    }

}
