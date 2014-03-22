/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.extractor;

import java.util.List;

import org.tomstools.crawler.common.Element;

/**
 * 分页导航抽取器
 * @author admin
 * @date 2014年3月14日 
 * @time 下午11:48:19
 * @version 1.0
 */
public interface NavigationExtractor {

    /**
     * 
     * @return
     * @since 1.0
     */
    public boolean useExpression();

    /**
     * 从元素中获取分页导航url列表
     * @param element 元素
     * @return 分页导航url列表。不会为null，但允许集合为空
     * @since 1.0
     */
    public List<String> getPageUrls(Element element);

}
