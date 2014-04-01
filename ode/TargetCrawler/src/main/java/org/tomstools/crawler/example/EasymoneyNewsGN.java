/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.example;

import java.util.LinkedHashMap;

import org.tomstools.crawler.config.CrawlingRule;
import org.tomstools.crawler.config.Target;
import org.tomstools.crawler.extractor.ContentExtractor;
import org.tomstools.crawler.extractor.impl.BaseSubpageExtractor;
import org.tomstools.crawler.extractor.impl.PageNavigationExtractor;
import org.tomstools.crawler.parser.HTMLParser;

/**
 * 东方财富网资讯：国内
 * @author admin
 * @date 2014年3月15日 
 * @time 上午10:27:37
 * @version 1.0
 */
public class EasymoneyNewsGN extends Target {
    public EasymoneyNewsGN(CrawlingRule crawlingRule) {
        super();
        setName("dongfang-guonei");
        setUrl("http://finance.eastmoney.com/news/cgnjj.html");
        setCrawlingRule(crawlingRule);
        setParser(new HTMLParser());
        setSubpageExtractor(new BaseSubpageExtractor("div.mainCont div.list li a"));  
        setNavigationExtractor(new PageNavigationExtractor("div.PageBox>div.Page a:containsOwn(下一页)", "<a .*?href=\"(.*?\\.html)\""));
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("title", "h1");
        params.put("time", "div.info span:eq(1)");
        params.put("content", "div#ContentBody");
        setContentExtractor(new ContentExtractor("div.newText.new",null,params,null));
    }
    
}
