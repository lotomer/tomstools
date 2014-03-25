/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.extractor.impl;

import java.util.ArrayList;
import java.util.List;

import org.tomstools.crawler.common.Element;
import org.tomstools.crawler.common.Utils;
import org.tomstools.crawler.extractor.NavigationExtractor;

/**
 * 使用表达式自动生成导航页面的抽取器
 * 
 * @author admin
 * @date 2014年3月15日
 * @time 上午1:09:50
 * @version 1.0
 */
public class ExpressionNavigationExtractor implements NavigationExtractor {
    private String urlTemplate;
    private String expression;
    
    /**
     * @param urlTemplate url模板字符串，使用%s做数字的占位符
     * @param expression 表达式：开始值|结束值|递增或递减的步长
     * @since 1.0
     */
    public ExpressionNavigationExtractor(String urlTemplate, String expression) {
        super();
        this.urlTemplate = urlTemplate;
        this.expression = expression;
    }

    /*
     * @since 1.0
     */
    public final boolean useExpression() {
        return true;
    }

    /*
     * @since 1.0
     */
    public List<String> getPageUrls(Element element) {
        List<String> urls = new ArrayList<String>();
        if (Utils.isEmpty(urlTemplate) || Utils.isEmpty(expression)) {
            return urls;
        }

        String[] vs = expression.split("\\|", 3);
        if (3 != vs.length) {
            return urls;
        }
        // 纯数字
        int startValue = Integer.valueOf(vs[0]);
        int endValue = Integer.valueOf(vs[1]);
        int stepValue = Math.abs(Integer.valueOf(vs[2]));
        if (startValue < endValue) {
            // 逐步增加
            for (int i = startValue; i < endValue; i = i + stepValue) {
                String nextPageUrl = String.format(urlTemplate, String.valueOf(i));
                urls.add(nextPageUrl);
            }
        } else {
            // 逐步递减
            for (int i = startValue; i > endValue; i = i - stepValue) {
                String nextPageUrl = String.format(urlTemplate, String.valueOf(i));
                urls.add(nextPageUrl);
            }
        }

        return urls;
    }

}