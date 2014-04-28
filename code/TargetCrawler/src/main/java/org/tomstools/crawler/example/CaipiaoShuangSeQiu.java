/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.example;

import java.util.ArrayList;
import java.util.List;

import org.tomstools.crawler.config.CrawlingRule;
import org.tomstools.crawler.config.Target;
import org.tomstools.crawler.extractor.ContentExtractor;
import org.tomstools.crawler.extractor.ContentExtractor.Field;
import org.tomstools.crawler.extractor.impl.TemplatePageNavigationExtractor;
import org.tomstools.crawler.parser.HTMLParser;

/**
 * 彩票：双色球
 * @author admin
 * @date 2014年3月15日 
 * @time 上午10:27:37
 * @version 1.0
 */
public class CaipiaoShuangSeQiu extends Target {
    public CaipiaoShuangSeQiu(CrawlingRule crawlingRule) {
        super();
        setName("lecai-shuangseqiu");
        setUrl("http://baidu.lecai.com/lottery/draw/list/50");
        setCrawlingRule(crawlingRule);
        setParser(new HTMLParser());
        //setContentPageExtractor(new NoSubpageExtractor());
        setNavigationExtractor(new TemplatePageNavigationExtractor("div.year_select select", "<option .*?value=\"(.*?)\">","http://baidu.lecai.com/lottery/draw/list/50?d=%s"));
        List<Field> fields = new ArrayList<>();
        fields.add(new ContentExtractor.TextField("time", "td.td1"));
        fields.add(new ContentExtractor.TextField("qishu", "td.td2"));
        fields.add(new ContentExtractor.TextField("b1", "td.td3>span.result>span:eq(0)"));
        fields.add(new ContentExtractor.TextField("b2", "td.td3>span.result>span:eq(1)"));
        fields.add(new ContentExtractor.TextField("b3", "td.td3>span.result>span:eq(2)"));
        fields.add(new ContentExtractor.TextField("b4", "td.td3>span.result>span:eq(3)"));
        fields.add(new ContentExtractor.TextField("b5", "td.td3>span.result>span:eq(4)"));
        fields.add(new ContentExtractor.TextField("b6", "td.td3>span.result>span:eq(5)"));
        fields.add(new ContentExtractor.TextField("b7", "td.td3>span.result>span:eq(6)"));
        setContentExtractor(new ContentExtractor("table#draw_list tbody>tr",null,null,fields));
    }
    
}
