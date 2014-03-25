/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.example;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.tomstools.crawler.common.FieldSplitter;
import org.tomstools.crawler.common.String2DateTimeString;
import org.tomstools.crawler.common.ValueConvertible;
import org.tomstools.crawler.config.CrawlingRule;
import org.tomstools.crawler.config.Target;
import org.tomstools.crawler.extractor.ContentExtractor;
import org.tomstools.crawler.extractor.impl.PageNavigationExtractor;
import org.tomstools.crawler.parser.HTMLParser;

/**
 * 0731fdc二手房咨询
 * 
 * @author admin
 * @date 2014年3月15日
 * @time 上午10:27:37
 * @version 1.0
 */
public class ESF0731fdc extends Target {
    public ESF0731fdc(CrawlingRule crawlingRule) {
        super();
        setName("0731fdc-esf");
        setUrl("http://esf.0731fdc.com/sale");
        setRegex4topDataFalg("\\[置顶\\]");
        setCrawlingRule(crawlingRule);
        setParser(new HTMLParser());
        //setContentPageExtractor(new NoSubpageExtractor()); 
        setNavigationExtractor(new PageNavigationExtractor("div.pagination a:containsOwn(下一页)",
                "<a .*?href=\"(.*?)\">"));
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("title", "div.title>a>h5");
        params.put("info", "div.title");
        params.put("area", "div.area");
        params.put("price", "div.price");
        params.put("time", "div.time");
        
        Map<String, ValueConvertible<String, String>> valueConverter = new HashMap<String, ValueConvertible<String,String>>();
        valueConverter.put("time", new String2DateTimeString("yyyy-MM-dd", "yyyy-MM-dd hh"));
        Map<String,FieldSplitter> fieldSplitters = new HashMap<String, FieldSplitter>();
        fieldSplitters.put("area", new FieldSplitter(" ", 2, new int[]{0}, new String[]{"area"},null));
        fieldSplitters.put("price", new FieldSplitter(" ", 2, new int[]{0,1}, new String[]{"totalPrice","price"},null));
        fieldSplitters.put("time", new FieldSplitter(" ", 3, new int[]{0,1,2}, new String[]{"role","owner","time"},valueConverter ));
        
        setContentExtractor(new ContentExtractor("li>div.item",null,null,params,fieldSplitters));
    }

}
