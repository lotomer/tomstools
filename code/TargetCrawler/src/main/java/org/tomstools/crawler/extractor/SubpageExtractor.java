/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.extractor;

import java.util.List;

import org.tomstools.crawler.common.Element;

/**
 * 子页面抽取器
 * @author admin
 * @date 2014年3月14日 
 * @time 下午11:30:41
 * @version 1.0
 */
public interface SubpageExtractor {
    /**
     * 获取元素中包含的子页面url列表
     * @param element  包含子页面url的元素
     * @return 子页面url列表，不会null
     * @since 1.0
     */
    public List<String> getSubpageUrls(Element element);

}
