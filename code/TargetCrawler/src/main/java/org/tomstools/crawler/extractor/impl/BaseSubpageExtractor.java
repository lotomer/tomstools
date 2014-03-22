/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.extractor.impl;

import java.util.ArrayList;
import java.util.List;

import org.tomstools.crawler.common.Element;
import org.tomstools.crawler.common.ElementProcessor;
import org.tomstools.crawler.common.Utils;

/**
 * 子页面抽取器：包含子页面的抽取器
 * 
 * @author admin
 * @date 2014年3月15日
 * @time 上午11:17:30
 * @version 1.0
 */
public class BaseSubpageExtractor extends NoSubpageExtractor {
    private String cssQuery;

    /**
     * @param cssQuery 元素选取表达式
     * @since 1.0
     */
    public BaseSubpageExtractor(String cssQuery) {
        this.cssQuery = cssQuery;
    }

    @Override
    public List<String> getSubpageUrls(Element element) {
        final List<String> urls = new ArrayList<String>();
        element.select(cssQuery, new ElementProcessor() {
            public boolean process(Element element) {
                if (null != element) {
                    // 子页面的url是节点的href属性值，标题是节点的正文
                    String url = element.getAttribute("href");
                    // 如果没有href属性，则取value属性
                    if (Utils.isEmpty(url)) {
                        url = element.getAttribute("value");
                    }
                    urls.add(url);
                }
                return true;
            }
        });
        return urls;
    }
}
