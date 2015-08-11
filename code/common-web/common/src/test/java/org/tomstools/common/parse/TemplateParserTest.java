package org.tomstools.common.parse;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.tomstools.common.parse.TemplateParser;

public class TemplateParserTest {
    static class O {
        String title;
        Object value;
    }
    @Test
    public void testParse() {
        Map<String, Object> variables = new HashMap<String, Object>();
        String template = "a${aa}bcd${b}ef${ccc}g";
        assertEquals("a${aa}bcd${b}ef${ccc}g",TemplateParser.parse(variables, template));
        variables.put("aa", "1");
        assertEquals("a1bcd${b}ef${ccc}g",TemplateParser.parse(variables, template));
        variables.put("aa", "11");
        assertEquals("a11bcd${b}ef${ccc}g",TemplateParser.parse(variables, template));
        variables.put("b", "1");
        assertEquals("a11bcd1ef${ccc}g",TemplateParser.parse(variables, template));
        variables.put("b", "11");
        assertEquals("a11bcd11ef${ccc}g",TemplateParser.parse(variables, template));
        variables.put("b", "111");
        assertEquals("a11bcd111ef${ccc}g",TemplateParser.parse(variables, template));
        variables.put("ccc", "1");
        assertEquals("a11bcd111ef1g",TemplateParser.parse(variables, template));
        variables.put("ccc", "11");
        assertEquals("a11bcd111ef11g",TemplateParser.parse(variables, template));
        variables.put("ccc", "111");
        assertEquals("a11bcd111ef111g",TemplateParser.parse(variables, template));
    }
    @Test
    public void testParseWithEscape() {
        Map<String, Object> variables = new HashMap<String, Object>();
        String template = "a\\${aa}bcd$\\{b}e\\\\f${ccc\\}g";
        assertEquals("a${aa}bcd${b}e\\f${ccc}g",TemplateParser.parse(variables, template));
        variables.put("aa", "1");
        assertEquals("a${aa}bcd${b}e\\f${ccc}g",TemplateParser.parse(variables, template));
        variables.put("aa", "11");
        assertEquals("a${aa}bcd${b}e\\f${ccc}g",TemplateParser.parse(variables, template));
        variables.put("b", "1");
        assertEquals("a${aa}bcd${b}e\\f${ccc}g",TemplateParser.parse(variables, template));
        variables.put("b", "11");
        assertEquals("a${aa}bcd${b}e\\f${ccc}g",TemplateParser.parse(variables, template));
        variables.put("b", "111");
        assertEquals("a${aa}bcd${b}e\\f${ccc}g",TemplateParser.parse(variables, template));
        variables.put("ccc", "1");
        assertEquals("a${aa}bcd${b}e\\f${ccc}g",TemplateParser.parse(variables, template));
        variables.put("ccc", "11");
        assertEquals("a${aa}bcd${b}e\\f${ccc}g",TemplateParser.parse(variables, template));
        variables.put("ccc", "111");
        assertEquals("a${aa}bcd${b}e\\f${ccc}g",TemplateParser.parse(variables, template));
    }

    @Test
    public void testParseError() {
        Map<String, Object> variables = new HashMap<String, Object>();
        String template = "a${aabcd${b}ef${ccc}g";
        assertEquals("a${aabcd${b}ef${ccc}g",TemplateParser.parse(variables, template));
        variables.put("b", "1");
        assertEquals("a${aabcd1ef${ccc}g",TemplateParser.parse(variables, template));
        variables.put("b", "11");
        assertEquals("a${aabcd11ef${ccc}g",TemplateParser.parse(variables, template));
    }
    
    @Test
    public void testParseSubProperty() {
        Map<String, Object> variables = new HashMap<String, Object>();
        String template = "a${aa.title}b${aa.value.a}cd${b}ef${ccc}g";
        assertEquals("a${aa.title}b${aa.value.a}cd${b}ef${ccc}g",TemplateParser.parse(variables, template));
        O webMetric = new O();
        webMetric.title = "MyTitle";
        Map<String,Object> value = new HashMap<String, Object>();
        value.put("a", 111);
        webMetric.value = value;
        variables.put("aa", webMetric);
        assertEquals("aMyTitleb111cd${b}ef${ccc}g",TemplateParser.parse(variables, template));
        variables.put("aa", "11");
        assertEquals("a${aa.title}b${aa.value.a}cd${b}ef${ccc}g",TemplateParser.parse(variables, template));
        variables.put("b", "1");
        assertEquals("a${aa.title}b${aa.value.a}cd1ef${ccc}g",TemplateParser.parse(variables, template));
        variables.put("b", "11");
        assertEquals("a${aa.title}b${aa.value.a}cd11ef${ccc}g",TemplateParser.parse(variables, template));
        variables.put("b", "111");
        assertEquals("a${aa.title}b${aa.value.a}cd111ef${ccc}g",TemplateParser.parse(variables, template));
        variables.put("ccc", "1");
        assertEquals("a${aa.title}b${aa.value.a}cd111ef1g",TemplateParser.parse(variables, template));
        variables.put("ccc", "11");
        assertEquals("a${aa.title}b${aa.value.a}cd111ef11g",TemplateParser.parse(variables, template));
        variables.put("ccc", "111");
        assertEquals("a${aa.title}b${aa.value.a}cd111ef111g",TemplateParser.parse(variables, template));
    }
    @Test
    public void testParseWithEscape1() {
        Map<String, Object> variables = new HashMap<String, Object>();
        String template = "\\\\f${ccc\\}g";
        assertEquals("\\f${ccc}g",TemplateParser.parse(variables, template));
    }
    @Test
    public void testParse1() {
        Map<String, Object> variables = new HashMap<String, Object>();
        String template = "$('#divMetric').datagrid({    columns:[[        {";
        assertEquals(template,TemplateParser.parse(variables, template));
    }
    @Test
    public void testParse2() {
        Map<String, Object> variables = new HashMap<String, Object>();
        String template = "$('#divMetric').datagrid({    columns:[[        {field:'title',title:'指标名',width:100},        {field:'value',title:'指标值',width:100}    ]],    data:metrics});";
        assertEquals(template,TemplateParser.parse(variables, template));
    }
    @Test
    public void testParseUrl() throws Exception {
        Map<String, Object> variables = new HashMap<String, Object>();
        HashMap<String, Object> config = new HashMap<String, Object>();
        config.put("address", "http://www.baidu.com");
        variables.put("config", config);
        String template = "${config.address}/ad/fw?we=2&a";
        assertEquals("http://www.baidu.com/ad/fw?we=2&a",TemplateParser.parse(variables, template));
    }
}
