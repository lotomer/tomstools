/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.extractor.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tomstools.crawler.common.Element;
import org.tomstools.crawler.common.ElementProcessor;
import org.tomstools.crawler.extractor.NavigationExtractor;
import org.tomstools.crawler.http.Header;
import org.tomstools.crawler.http.Parameters;
import org.tomstools.crawler.http.RequestInfo;
import org.tomstools.crawler.parser.HTMLParser;

/**
 * 分页导航抽取器：从页面的分页信息中直接抽取
 * 
 * @author admin
 * @date 2014年3月15日
 * @time 上午1:08:36
 * @version 1.0
 */
public class PageNavigationExtractor implements NavigationExtractor {
    private String cssQuery;
    protected Map<Integer, String> regexGroupIndex4parameterNames;
    private Pattern pattern;
    /**
     * @param cssQuery 元素选择表达式
     * @param regexp 正则表达式
     * @param regexGroupIndex4parameterNames 正则表达式中group的索引号对应的参数名称，参数名如form_pageno
     * @since 1.0
     */
    public PageNavigationExtractor(String cssQuery, String regexp, Map<Integer,String> regexGroupIndex4parameterNames) {
        super();
        this.cssQuery = cssQuery;
        pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
                | Pattern.DOTALL);
        this.regexGroupIndex4parameterNames = regexGroupIndex4parameterNames;
    }

    public final boolean useExpression() {
        return false;
    }

    @Override
    public List<RequestInfo> getNextPageRequestInfos(Element element) {
        final List<RequestInfo> pageRequestInfos = new ArrayList<RequestInfo>();
        element.select(cssQuery, new ElementProcessor() {
            public boolean process(Element element) {
                if (null != element) {
                    Matcher matcher = pattern.matcher(element.getCode());
                    String value = null;
                    while (matcher.find()) {
                        RequestInfo requestInfo = new RequestInfo();
                        Header headers = null;
                        Parameters formDatas = null;
                        for (Entry<Integer, String> entry : regexGroupIndex4parameterNames.entrySet()) {
                            value = matcher.group(entry.getKey());
                            if (null != value){
                                // 需要将value进行html的反转义，即将&amp;转为&，将&lt;转为<，等等
                                value = HTMLParser.unescape(value);
                                // 根据参数名的前缀判断是什么类型
                                String[] paramNames = entry.getValue().split(",");
                                for (String paramName : paramNames) {
                                    if (paramName.startsWith("header_")){
                                        if (null == headers) headers = new Header();
                                        headers.put(new String(paramName.substring("header_".length())), getParameterValue(paramName, value));
                                    }else if (paramName.startsWith("form_")){
                                        // 表单参数，这里的数据统一放到第一个map中
                                        if (null == formDatas) {
                                            formDatas = new Parameters();
                                        }
                                        if (formDatas.size() < 1){
                                            formDatas.add(new HashMap<String,String>());
                                        }
                                        formDatas.get(0).put(new String(paramName.substring("form_".length())), getParameterValue(paramName, value));
                                        
                                    }else{
                                        // url
                                        requestInfo.setUrl(getParameterValue(paramName, value));
                                    }
                                }
                            }
                        }
                        requestInfo.setFormDatas(formDatas);
                        requestInfo.setHeaders(headers);
                        pageRequestInfos.add(requestInfo);
                    }
                }
                return true;
            }
        });
        return pageRequestInfos;
    }

    /**
     * 根据参数名和参数值返回实际的参数值
     * @param parameterName 参数名
     * @param parameterValue    参数值
     * @return  实际的参数值
     * @since 1.0
     */
    protected String getParameterValue(String parameterName, String parameterValue) {
        return parameterValue;
    }

    public static void main(String[] args) throws IOException {
        String regexp = "<a .*?onclick=\"QueryAction.GoPage\\('TAB',(\\d+)\\)\"";
        String selector = "div.pager td.pager>a:contains(下页)";
        BufferedReader reader = new BufferedReader(new FileReader(new File("e:/default.txt")));
        StringBuilder msg = new StringBuilder();
        String line = null;
        while (null != (line = reader.readLine())){
            msg.append(line);
            msg.append("\r\n");
        }
        reader.close();
        reader = null;
        String html = msg.toString();//"<div style=\"text-align:left;font-family:宋体;font-size:x-small\" class=\"pager\"><table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\"><tbody><tr><td align=\"right\" class=\"pager\" valign=\"bottom\" style=\"width:40%;overflow:hidden;\">共1662页&nbsp;当前只显示200页&nbsp;共49837条记录</td><td align=\"left\" class=\"pager\" valign=\"bottom\" style=\"width:60%\"> <a href=\"javascript:void(0)\" style=\"margin-right:5px;\" onclick=\"QueryAction.GoPage('TAB',1)\">首页</a><a href=\"javascript:void(0)\" style=\"margin-right:5px;\" onclick=\"QueryAction.GoPage('TAB',1)\">上页</a><a href=\"javascript:void(0)\" onclick=\"QueryAction.GoPage('TAB',1)\" style=\"margin-right:5px;\">1</a><span style=\"color: red;font-weight: bold;margin-right: 5px;\">2</span><a href=\"javascript:void(0)\" onclick=\"QueryAction.GoPage('TAB',3)\" style=\"margin-right:5px;\">3</a><a href=\"javascript:void(0)\" onclick=\"QueryAction.GoPage('TAB',4)\" style=\"margin-right:5px;\">4</a><a href=\"javascript:void(0)\" onclick=\"QueryAction.GoPage('TAB',5)\" style=\"margin-right:5px;\">5</a><a href=\"javascript:void(0)\" onclick=\"QueryAction.GoPage('TAB',6)\" style=\"margin-right:5px;\">6</a><a href=\"javascript:void(0)\" onclick=\"QueryAction.GoPage('TAB',7)\" style=\"margin-right:5px;\">7</a><a href=\"javascript:void(0)\" onclick=\"QueryAction.GoPage('TAB',8)\" style=\"margin-right:5px;\">8</a><a href=\"javascript:void(0)\" onclick=\"QueryAction.GoPage('TAB',9)\" style=\"margin-right:5px;\">9</a><a href=\"javascript:void(0)\" onclick=\"QueryAction.GoPage('TAB',10)\" style=\"margin-right:5px;\">10</a><span><a href=\"javascript:void(0)\" onclick=\"QueryAction.GoPage('TAB',11)\" style=\"margin-right:5px;\">...</a></span><a href=\"javascript:void(0)\" onclick=\"QueryAction.GoPage('TAB',3)\" style=\"margin-right:5px;\">下页</a><a href=\"javascript:void(0)\" onclick=\"QueryAction.GoPage('TAB',200)\" style=\"margin-right:5px;\">尾页</a><input type=\"text\" style=\"width:30px;\" value=\"2\" onkeypress=\"if(event.keyCode==13)QueryAction.GoPage('TAB',this.value,200);\"/><input type=\"button\" value=\"go\" onclick=\"QueryAction.GoPage('TAB',this.previousSibling.value,200)\"/> </td></tr></tbody></table></div>";
        HTMLParser p = new HTMLParser();
        Element element = p.parse(html, null);
        Map<Integer, String> regexGroupIndex4parameterNamess = new HashMap<Integer, String>();
        regexGroupIndex4parameterNamess.put(1, "url,form_TAB_QuerySubmitPagerData");
        Map<String, String> parameterTemplate = new HashMap<>();
        parameterTemplate.put("url", "http://www.landchina.com/default.aspx?tabid=263#${url}");
        parameterTemplate.put("form_TAB_QuerySubmitPagerData", "${form_TAB_QuerySubmitPagerData}");
        
        TemplatePageNavigationExtractor e = new TemplatePageNavigationExtractor(selector, regexp, regexGroupIndex4parameterNamess, parameterTemplate);
        List<RequestInfo> l = e.getNextPageRequestInfos(element);
        System.out.println(l);
    }
}
