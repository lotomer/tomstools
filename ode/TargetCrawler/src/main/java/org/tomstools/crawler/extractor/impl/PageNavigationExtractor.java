/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.extractor.impl;

import java.util.ArrayList;
import java.util.List;

import org.tomstools.common.util.Utils;
import org.tomstools.crawler.common.Element;
import org.tomstools.crawler.common.ElementProcessor;
import org.tomstools.crawler.extractor.NavigationExtractor;

/**
 * 分页导航抽取器：从页面的分页信息中直接抽取
 * 
 * @author admin
 * @date 2014年3月15日
 * @time 上午1:08:36
 * @version 1.0
 */
public class PageNavigationExtractor implements NavigationExtractor {
    private String cssQuery;
    protected String regexWithOneGroup;

    /**
     * @param cssQuery 元素选择表达式
     * @param regexWithOneGroup 下一页对应的url的正则表达式，group 1对应url值
     * @since 1.0
     */
    public PageNavigationExtractor(String cssQuery, String regexWithOneGroup) {
        super();
        this.cssQuery = cssQuery;
        this.regexWithOneGroup = regexWithOneGroup;
    }

    @Override
    public final boolean useExpression() {
        return false;
    }

    @Override
    public List<String> getPageUrls(Element element) {
        final List<String> urls = new ArrayList<String>();
        element.select(cssQuery, new ElementProcessor() {
            @Override
            public boolean process(Element element) {
                if (null != element) {
                    List<String> nextPageUrls = Utils.getRegexValue(regexWithOneGroup,
                            element.getCode(), 1);
                    for (String nextPageUrl : nextPageUrls) {
                        urls.add(getUrl(nextPageUrl));
                    }
                }
                return true;
            }
        });
        return urls;
    }

    protected String getUrl(String url) {
        return url;
    }

}
