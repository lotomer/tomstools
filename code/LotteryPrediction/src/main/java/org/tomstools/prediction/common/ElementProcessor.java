/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.prediction.common;

/**
 * 元素处理器接口
 * @author admin
 * @date 2014年3月14日 
 * @time 下午4:54:31
 * @version 1.0
 */
public interface ElementProcessor {

    /**
     * 处理元素
     * @param element   待处理元素，可能为null
     * @return  true 处理成功；false 处理失败
     * @since 1.0
     */
    public boolean process(Element element);

}
