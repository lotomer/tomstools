/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.example;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.tomstools.crawler.common.Element;
import org.tomstools.crawler.common.ElementProcessor;
import org.tomstools.crawler.config.CrawlingRule;
import org.tomstools.crawler.config.Target;
import org.tomstools.crawler.extractor.ContentExtractor;
import org.tomstools.crawler.extractor.impl.ExpressionNavigationExtractor;
import org.tomstools.crawler.extractor.impl.NoSubpageExtractor;
import org.tomstools.crawler.parser.HTMLParser;

/**
 * 湖南国土资源厅：采矿转让
 * 
 * @author admin
 * @date 2014年3月15日
 * @time 上午10:27:37
 * @version 1.0
 */
public class HuNanGuoTuZiYuanCaiKuangZhuanRang extends Target {
    public HuNanGuoTuZiYuanCaiKuangZhuanRang(CrawlingRule crawlingRule) {
        super();
        setName("hunan-guotu-ckzr");
        setUrl("http://www.gtzy.hunan.gov.cn/application/hdpt_422/spgl/ckzr/index_539.html");
        setCrawlingRule(crawlingRule);
        setParser(new HTMLParser());
        setSubpageExtractor(new NoSubpageExtractor());
        setNavigationExtractor(new ExpressionNavigationExtractor(
                "http://www.gtzy.hunan.gov.cn/application/hdpt_422/spgl/ckzr/index_539_%s.html",
                "1|20|1"));
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("value", "td[title]"); // \u4E00-\u9FA5 是汉字区间
        setContentExtractor(new ContentExtractor("tr",null,params,null) {
            private int index = 0;

            @Override
            protected void processValue(final Map<String, String> result, Element element,
                    LinkedHashMap<String, String> params) {
                for (final Entry<String, String> entry : params.entrySet()) {
                    element.select(entry.getValue(), new ElementProcessor() {
                        @Override
                        public boolean process(Element element) {
                            if (null != element){
                            if ("value".equals(entry.getKey())) {
                                // 解析值
                                if (0 == index % 4) {
                                    result.put("projectName", element.getAttribute("title"));
                                } else if (1 == index % 4) {
                                    result.put("pre-owner", element.getAttribute("title"));
                                } else if (2 == index % 4) {
                                    result.put("owner", element.getAttribute("title"));
                                } else if (3 == index % 4) {
                                    result.put("datetime", element.getAttribute("title"));
                                }
                                index++;
                            }
                            }else{
                                //
                            }
                            return true;
                        }
                    });
                }
            }
        });
    }

}
