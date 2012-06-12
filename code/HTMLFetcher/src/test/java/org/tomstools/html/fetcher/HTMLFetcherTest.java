package org.tomstools.html.fetcher;

import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;

public class HTMLFetcherTest extends TestCase {

    public void testExcludeContentWithEmptyFilter() {
        String content = "a<div name=\"dd\"><a href=\"asdf\">df</a><script type=\"text/javascript\">script</script><link a=\"d\">link</link>haha</div>";
        String regexpFilterExclude = "";
        String result = new HTMLFetcher().excludeContent(content, regexpFilterExclude);
        assertEquals("a<div name=\"dd\"><a href=\"asdf\">df</a><script type=\"text/javascript\">script</script><link a=\"d\">link</link>haha</div>", result);
    }
    public void testExcludeContent() {
        String content = "a<div name=\"dd\"><a href=\"asdf\">df</a><script type=\"text/javascript\">script</script><link a=\"d\">link</link>haha</div>";
        String regexpFilterExclude = "<script .*?</script>";
        String result = new HTMLFetcher().excludeContent(content, regexpFilterExclude);
        assertEquals("a<div name=\"dd\"><a href=\"asdf\">df</a><link a=\"d\">link</link>haha</div>", result);
    }
    public void testExcludeContentMore() {
        String content = "a<div name=\"dd\"><a href=\"asdf\">df</a><script type=\"text/javascript\">script</script>a<link a=\"d\">link</link>haha</div>";
        String regexpFilterExclude = "<script .*?</script>|<link .*?</link>|<a .*?>|</a>";
        String result = new HTMLFetcher().excludeContent(content, regexpFilterExclude);
        assertEquals("a<div name=\"dd\">dfahaha</div>", result);
    }
    public void testIncludeContentWithEmptyFilter() {
        String content = "a<div name=\"dd\"><a href=\"asdf\">df</a><script type=\"text/javascript\">script</script><link a=\"d\">link</link>haha</div>";
        String regexpFilterInclude = "";
        String result = new HTMLFetcher().includeContent(content, regexpFilterInclude);
        assertEquals("a<div name=\"dd\"><a href=\"asdf\">df</a><script type=\"text/javascript\">script</script><link a=\"d\">link</link>haha</div>", result);
    }
    public void testIncludeContent() {
        String content = "a<div name=\"dd\"><a href=\"asdf\">df</a><script type=\"text/javascript\">script</script><link a=\"d\">link</link>haha</div>";
        String regexpFilterInclude = "<div name=\"dd\">(.*?)</div>";
        String result = new HTMLFetcher().includeContent(content, regexpFilterInclude);
        assertEquals("<a href=\"asdf\">df</a><script type=\"text/javascript\">script</script><link a=\"d\">link</link>haha", result);
    }
    
    public static void main(String[] args) throws UnsupportedEncodingException {
        String ss = "ÄúÕýÔÚ·ÃÎÊµÄÊÇÔÚÏßÊé°É£";
        System.out.println(ss);
        System.out.println(new String(ss.getBytes("gb2312")));
        System.out.println(new String(ss.getBytes("gbk")));
        System.out.println(new String(ss.getBytes("utf-8"),"gb2312")); 
        System.out.println(new String(ss.getBytes("ISO-8859-1")));
        System.out.println(new String(ss.getBytes("GB18030")));
        System.out.println(new String(ss.getBytes("UTF-16")));
        System.out.println(new String(ss.getBytes("ASCII")));
        
    }
}
