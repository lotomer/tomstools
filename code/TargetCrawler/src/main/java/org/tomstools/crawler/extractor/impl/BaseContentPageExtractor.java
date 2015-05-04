/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.extractor.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tomstools.common.Utils;
import org.tomstools.crawler.common.Element;
import org.tomstools.crawler.common.ElementProcessor;
import org.tomstools.crawler.extractor.ContentPageExtractor;
import org.tomstools.crawler.extractor.ContentExtractor.ConstantField;
import org.tomstools.crawler.extractor.ContentExtractor.Field;
import org.tomstools.crawler.extractor.ContentExtractor.MultipleField;
import org.tomstools.crawler.http.PageFetcher;
import org.tomstools.crawler.parser.HTMLParser;

/**
 * 子页面抽取器：包含子页面的抽取器
 * 
 * @author admin
 * @date 2014年3月15日
 * @time 上午11:17:30
 * @version 1.0
 */
public class BaseContentPageExtractor implements ContentPageExtractor {
    private String cssQuery;
    private BaseContentPageExtractor contentPageExtractor;
    private Pattern pattern; // 提取规则
    private String format;
    private Pattern patternFilter; // 过滤规则
    private PageFetcher pageFetcher;
    private List<Field> constantField; // 固定属性字段

    /**
     * @param cssQuery 元素选取表达式
     * @param format 子页面url模板
     * @param regex 
     *            提取子页面url的正则表达式，与format配合使用，将正则表达式的group数据代入format中以生成完整的子页面url
     * @param filterRegex 过滤规则的正则表达式，如果匹配上，则直接过滤不进行子页面的提取
     * @since 1.0
     */
    public BaseContentPageExtractor(String cssQuery, String format, String regex, String filterRegex) {
        this.cssQuery = cssQuery;
        pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
                | Pattern.DOTALL);
        if (Utils.isEmpty(format)) {
            this.format = "%s";
        } else {
            this.format = format;
        }
        if (!Utils.isEmpty(filterRegex)) {
            patternFilter = Pattern.compile(filterRegex, Pattern.CASE_INSENSITIVE
                    | Pattern.UNICODE_CASE | Pattern.DOTALL);
        } else {
            patternFilter = null;
        }
    }

    @Override
    public List<String> getContentPageUrls(Element element) {
        final List<String> urls = new ArrayList<String>();
        element.select(cssQuery, new ElementProcessor() {
            public boolean process(Element element) {
                if (null != element) {
                    // 首先看是否符合过滤规则，如果符合则直接过滤
                    if (null != patternFilter && patternFilter.matcher(element.getCode()).matches()) {
                        return true;
                    }
                    // 根据模板获取子页面url
                    Matcher m = pattern.matcher(element.getCode());
                    if (m.matches()) {
                        Object[] params = new Object[m.groupCount()];
                        for (int i = 0; i < params.length; i++) {
                            // 需要对取出来的值进行HTML反转义
                            params[i] = HTMLParser.unescape(m.group(i + 1));
                        }
                        String url = String.format(format, params);
                        // 子页面的url是节点的href属性值，标题是节点的正文
                        // String url = element.getAttribute("href");
                        // 如果没有href属性，则取value属性
                        if (!Utils.isEmpty(url)) {
                            urls.add(url);
                        }
                    }
                }
                return true;
            }
        });
        return urls;
    }

    /**
     * @return 返回 内容页面url抽取器
     * @since 1.0
     */
    public final BaseContentPageExtractor getContentPageExtractor() {
        return contentPageExtractor;
    }

    /**
     * @param contentPageExtractor 设置 内容页面url抽取器
     * @since 1.0
     */
    public final void setContentPageExtractor(BaseContentPageExtractor contentPageExtractor) {
        this.contentPageExtractor = contentPageExtractor;
    }

    /**
     * @param pageFetcher 设置 页面抓取器
     * @since 1.0
     */
    public final void setPageFetcher(PageFetcher pageFetcher) {
        this.pageFetcher = pageFetcher;
    }

    @Override
    public PageFetcher getPageFetcher() {
        return pageFetcher;
    }

    public static void main(String[] args) {
        Pattern p = Pattern
                .compile(
                        "<a\\s+?href=\"javascript:Floor\\.doAction\\('(\\d+?)', '([^']+?)', '([^']*?)'\\);\".*",
                        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL);
        Matcher m = p
                .matcher("<a href=\"javascript:Floor.doAction('7309', 'KF1310090036', '');\" ref=\"KF1310090036&amp;id=7309\" class=\"top\">一期15栋</a>");
        if (m.matches()) {
            System.out.println(m.groupCount());
            System.out.println(m.group(1));
        } else {
            System.out.println("===");
        }
    }

    /**
     * @param constantField 设置 固定属性字段
     * @since 1.0
     */
    public final void setConstantField(List<Field> constantField) {
        this.constantField = constantField;
    }

    @Override
    public Map<String, String> getConstantValues(Element element) {
        if (null != constantField && null != element) {
            final Map<String, String> tmpConstantFieldValues = new LinkedHashMap<String, String>();
            for (Field field : constantField) {
                if (field instanceof MultipleField) {
                    field.processData(null, tmpConstantFieldValues);
                } else if (field instanceof ConstantField) {
                    field.processData(element, tmpConstantFieldValues);
                } else {
                    field.processData(element.select(field.getSelector()), tmpConstantFieldValues);
                }

            }
            return tmpConstantFieldValues;
        }
        return null;
    }
}
