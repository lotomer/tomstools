/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.extractor;

import java.util.List;

import org.tomstools.crawler.common.Element;
import org.tomstools.crawler.http.PageFetcher;

/**
 * 内容所在页面抽取器，抽取内容所在页面的url
 * @author admin
 * @date 2014年3月14日 
 * @time 下午11:30:41
 * @version 1.0
 */
public interface ContentPageExtractor {
    /**
     * 获取元素中包含的子页面url列表
     * @param element  包含子页面url的元素
     * @return 子页面url列表，不会null
     * @since 1.0
     */
    public List<String> getContentPageUrls(Element element);

    /**
     * 获取子页面解析器
     * @return 子页面解析器。可为null。如果不为null，则需继续提取
     * @since 1.0
     */
    public ContentPageExtractor getContentPageExtractor();
    
    /**
     * 获取页面抓取器
     * @return 页面抓取器
     * @since 1.0
     */
    public PageFetcher getPageFetcher();
}
