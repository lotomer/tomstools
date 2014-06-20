/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.example;

import org.tomstools.crawler.config.CrawlingRule;

/**
 * 彩票：大乐透
 * @author admin
 * @date 2014年3月15日 
 * @time 上午10:27:37
 * @version 1.0
 */
public class CaipiaoDaLeTou extends CaipiaoShuangSeQiu {
    public CaipiaoDaLeTou(CrawlingRule crawlingRule) {
        super(crawlingRule);
        setName("lecai-daletou");
        setUrl("http://baidu.lecai.com/lottery/draw/list/1");
      //XXX 需要修改，暂时屏蔽 setNavigationExtractor(new TemplatePageNavigationExtractor("div.year_select select", "<option .*?value=\"(.*?)\">","http://baidu.lecai.com/lottery/draw/list/1?d=%s"));
    }
    
}
