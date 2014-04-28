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
        //ssetContentPageExtractor(new NoSubpageExtractor()); 
        setNavigationExtractor(new PageNavigationExtractor("div#page ul.pageno a[title=下一页]",
                "<a .*?href=\"(.*?)\""));
        List<Field> fields = new ArrayList<>();
        fields.add(new ContentExtractor.TextField("floorName", "dl>dt>ul>li.floorname"));
        fields.add(new ContentExtractor.TextField("type", "dl>dt>ul>li.type"));
        fields.add(new ContentExtractor.TextField("price", "dl>dt>ul>li.price"));
        fields.add(new ContentExtractor.TextField("tel", "dl>dd:eq(1)"));
        fields.add(new ContentExtractor.TextField("comp", "dl>dd:eq(2)"));
        fields.add(new ContentExtractor.TextField("addr", "dl>dd:eq(3)"));
        
        setContentExtractor(new ContentExtractor("div#ddd>div.list-con",null,null,fields));
    }

}
