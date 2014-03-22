/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.common;

/**
 * 元素接口
 * @author admin
 * @date 2014年3月14日 
 * @time 下午1:03:44
 * @version 1.0
 */
public interface Element {
    /**
     * 获取元素源码
     * @return 元素源码
     * @since 1.0
     */
    public String getCode();
    /**
     * 获取属性值
     * @param attributeName 属性名
     * @return  属性名对应的属性值。可能为空字符串或null
     * @since 1.0
     */
    public String getAttribute(String attributeName);
    /**
     * 获取元素文本内容
     * @return 文本内容
     * @since 1.0
     */
    public String getText();
    /**
     * 选择符合条件的节点并逐个处理
     * @param cssQuery  选择条件
     * @param processor 处理程序
     * @since 1.0
     */
    public void select(String cssQuery, ElementProcessor processor);
}
