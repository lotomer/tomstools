/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.example;

import java.util.LinkedHashMap;

import org.tomstools.crawler.config.CrawlingRule;
import org.tomstools.crawler.config.Target;
import org.tomstools.crawler.extractor.ContentExtractor;
import org.tomstools.crawler.extractor.impl.NoSubpageExtractor;
import org.tomstools.crawler.extractor.impl.PageNavigationExtractor;
import org.tomstools.crawler.parser.HTMLParser;

/**
 * 0731fdc新房咨询
 * 
 * @author admin
 * @date 2014年3月15日
 * @time 上午10:27:37
 * @version 1.0
 */
public class Floor0731fdc extends Target {
    public Floor0731fdc(CrawlingRule crawlingRule) {
        super();
        setName("0731fdc-xinfang");
        setUrl("http://floor.0731fdc.com/search.php");
        setCrawlingRule(crawlingRule);
        setParser(new HTMLParser());
        setSubpageExtractor(new NoSubpageExtractor()); 
        setNavigationExtractor(new PageNavigationExtractor("div#page ul.pageno a[title=下一页]",
                "<a .*?href=\"(.*?)\""));
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("floorName", "dl>dt>ul>li.floorname");
        params.put("type", "dl>dt>ul>li.type");
        params.put("price", "dl>dt>ul>li.price");
        params.put("tel", "dl>dd:eq(1)");
        params.put("comp", "dl>dd:eq(2)");
        params.put("addr", "dl>dd:eq(3)");
        
        setContentExtractor(new ContentExtractor("div#ddd>div.list-con",null,params,null));
    }

}
