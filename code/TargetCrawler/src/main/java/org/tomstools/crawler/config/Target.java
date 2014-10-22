/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.config;

import org.tomstools.crawler.extractor.ContentExtractor;
import org.tomstools.crawler.extractor.NavigationExtractor;
import org.tomstools.crawler.extractor.ContentPageExtractor;
import org.tomstools.crawler.handle.CompletedHandleable;
import org.tomstools.crawler.http.PageFetcher;
import org.tomstools.crawler.parser.Parser;

/**
 * 爬取目标
 * 
 * @author admin
 * @date 2014年3月12日
 * @time 下午6:57:09
 * @version 1.0
 */
public class Target {
    // private final static Logger LOGGER = Logger.getLogger(Target.class);
    private String url; // 目标主页面URL
    private String name; // 目标名称
    private String regex4topDataFalg; // 匹配置顶数据的正则表达式
    private String defaultCharsetName; // 默认字符集
    private Parser parser; // 目标主页面解析器
    private ContentPageExtractor contentPageExtractor; // 内容页面抽取工具
    private NavigationExtractor navigationExtractor; // 分页导航抽取工具
    private ContentExtractor contentExtractor; // 正文内容抽取工具
    private CrawlingRule crawlingRule; // 爬取规则
    private PageFetcher pageFetcher;  // 页面抓取器
    private CompletedHandleable completedHandler;
    private String baseUrl; // 基本url
    /**
     * @return 返回 url
     * @since 1.0
     */
    public final String getUrl() {
        return url;
    }

    /**
     * @return 返回 页面抓取器
     * @since 1.0
     */
    public final PageFetcher getPageFetcher() {
        return pageFetcher;
    }

    /**
     * @param pageFetcher 设置 页面抓取器
     * @since 1.0
     */
    public final void setPageFetcher(PageFetcher pageFetcher) {
        this.pageFetcher = pageFetcher;
        if (null != defaultCharsetName){
            // 指定了字符集，则使用指定的字符集
            this.pageFetcher.setDefaultCharsetName(defaultCharsetName);
        }
    }

    /**
     * @param url 设置 url
     * @since 1.0
     */
    public final void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return 返回 name
     * @since 1.0
     */
    public final String getName() {
        return name;
    }

    /**
     * @param name 设置 name
     * @since 1.0
     */
    public final void setName(String name) {
        this.name = name;
    }

    /**
     * @return 返回 匹配置顶数据的正则表达式
     * @since 1.0
     */
    public final String getRegex4topDataFalg() {
        return regex4topDataFalg;
    }

    /**
     * @param flag4noSaveLatest 设置 匹配置顶数据的正则表达式
     * @since 1.0
     */
    public final void setRegex4topDataFalg(String regex4topDataFalg) {
        this.regex4topDataFalg = regex4topDataFalg;
    }

    /**
     * @return 返回 默认字符集
     * @since 1.0
     */
    public final String getDefaultCharsetName() {
        return defaultCharsetName;
    }

    /**
     * @param defaultCharsetName 设置 默认字符集
     * @since 1.0
     */
    public final void setDefaultCharsetName(String defaultCharsetName) {
        this.defaultCharsetName = defaultCharsetName;
        if (null != pageFetcher){
            // 如果有网页抓取器，则设置字符集
            pageFetcher.setDefaultCharsetName(defaultCharsetName);
        }
    }

    /**
     * @return 返回 parser
     * @since 1.0
     */
    public final Parser getParser() {
        return parser;
    }

    /**
     * @param parser 设置 parser
     * @since 1.0
     */
    public final void setParser(Parser parser) {
        this.parser = parser;
    }

    /**
     * @return 返回 内容页面抽取器，可能为null
     * @since 1.0
     */
    public final ContentPageExtractor getContentPageExtractor() {
        return contentPageExtractor;
    }

    /**
     * @param contentPageExtractor 设置 内容页面抽取器。
     * @since 1.0
     */
    public final void setContentPageExtractor(ContentPageExtractor contentPageExtractor) {
        this.contentPageExtractor = contentPageExtractor;
    }

    /**
     * @return 返回 navigationExtractor
     * @since 1.0
     */
    public final NavigationExtractor getNavigationExtractor() {
        return navigationExtractor;
    }

    /**
     * @param navigationExtractor 设置 navigationExtractor
     * @since 1.0
     */
    public final void setNavigationExtractor(NavigationExtractor navigationExtractor) {
        this.navigationExtractor = navigationExtractor;
    }

    /**
     * @return 返回 contentExtractor
     * @since 1.0
     */
    public final ContentExtractor getContentExtractor() {
        return contentExtractor;
    }

    /**
     * @param contentExtractor 设置 contentExtractor
     * @since 1.0
     */
    public final void setContentExtractor(ContentExtractor contentExtractor) {
        this.contentExtractor = contentExtractor;
    }

    /**
     * @return 返回 crawlingRule
     * @since 1.0
     */
    public final CrawlingRule getCrawlingRule() {
        return crawlingRule;
    }

    /**
     * @param crawlingRule 设置 crawlingRule
     * @since 1.0
     */
    public final void setCrawlingRule(CrawlingRule crawlingRule) {
        this.crawlingRule = crawlingRule;
    }

    /*
     * @since 1.0
     */
    @Override
    public String toString() {
        return new StringBuilder().append("{url=").append(url).append(", name=").append(name)
                .append(", crawlingRule=").append(crawlingRule).append("}").toString();
    }

    /**
     * @return 获取完成后的钩子
     * @since 1.0
     */
    public CompletedHandleable getCompletedHandler() {
        return completedHandler;
    }

    /**
     * @param completedHandler 设置 完成后的钩子
     * @since 1.0
     */
    public final void setCompletedHandler(CompletedHandleable completedHandler) {
        this.completedHandler = completedHandler;
    }

    public final String getBaseUrl() {
        return baseUrl;
    }

    public final void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

}
