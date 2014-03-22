/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.example;

import java.util.LinkedHashMap;

import org.tomstools.crawler.config.CrawlingRule;
import org.tomstools.crawler.config.Target;
import org.tomstools.crawler.extractor.ContentExtractor;
import org.tomstools.crawler.extractor.impl.NoSubpageExtractor;
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
        setSubpageExtractor(new NoSubpageExtractor());
        setNavigationExtractor(new TemplatePageNavigationExtractor("div.year_select select", "<option .*?value=\"(.*?)\">","http://baidu.lecai.com/lottery/draw/list/50?d=%s"));
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("time", "td.td1");
        params.put("qishu", "td.td2");
        params.put("b1", "td.td3>span.result>span:eq(0)");
        params.put("b2", "td.td3>span.result>span:eq(1)");
        params.put("b3", "td.td3>span.result>span:eq(2)");
        params.put("b4", "td.td3>span.result>span:eq(3)");
        params.put("b5", "td.td3>span.result>span:eq(4)");
        params.put("b6", "td.td3>span.result>span:eq(5)");
        params.put("b7", "td.td3>span.result>span:eq(6)");
        setContentExtractor(new ContentExtractor("table#draw_list tbody>tr",null,params,null));
    }
    
}
