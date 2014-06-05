/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.extractor.impl;

/**
 * 分页导航抽取器：从页面的分页信息中抽取数据，并将数据填入模板以生成新的分页
 * @author admin
 * @date 2014年3月15日
 * @time 上午1:42:44
 * @version 1.0
 */
public class TemplatePageNavigationExtractor extends PageNavigationExtractor {
    private String urlTemplate;

    /**
     * 
     * @param cssQuery  元素选择表达式
     * @param regexWithOneGroup 下一页对应的url的正则表达式，group 1对应url值
     * @param urlTemplate url模板字符串，使用%s做数字的占位符
     * @since 1.0
     */
    public TemplatePageNavigationExtractor(String cssQuery, String regexWithOneGroup,
            String urlTemplate) {
        super(cssQuery, regexWithOneGroup);
        this.urlTemplate = urlTemplate;
    }

    /*
     * @since 1.0
     */
    @Override
    protected String getUrl(String url) {
        return String.format(urlTemplate, url);
    }

}
