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
import org.tomstools.crawler.extractor.impl.BaseContentPageExtractor;
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
        setContentPageExtractor(new BaseContentPageExtractor("div.mainCont div.list li a","","",null));  
      //XXX 需要修改，暂时屏蔽 setNavigationExtractor(new PageNavigationExtractor("div.PageBox>div.Page a:containsOwn(下一页)", "<a .*?href=\"(.*?\\.html)\""));
        List<Field> fields = new ArrayList<Field>();
        fields.add(new ContentExtractor.TextField("title", "h1"));
        fields.add(new ContentExtractor.TextField("time", "div.info span:eq(1)"));
        fields.add(new ContentExtractor.TextField("content", "div#ContentBody"));
        setContentExtractor(new ContentExtractor("div.newText.new",null,null,fields));
    }
    
}
