/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.parser;

import org.tomstools.crawler.common.Element;




/**
 * 内容解析器接口
 * @author admin
 * @date 2014年3月12日 
 * @time 下午8:42:53
 * @version 1.0
 */
public interface Parser {
    /**
     * 根据参数解析内容
     * @param content 待解析的内容
     * @param selector 节点选择表达式。可谓空或null
     * @return  解析后的内容。解析失败则返回null
     * @since 1.0
     */
    public Element parse(String content, String selector);
}
