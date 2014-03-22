/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.common;

/**
 * 数据转换器接口
 * @author admin
 * @date 2014年3月16日 
 * @time 下午8:21:34
 * @version 1.0
 */
public interface ValueConvertible<V1,V2> {
    /**
     * 值转换
     * @param v
     * @return 不会返回null
     * @since 1.0
     */
    public V2 valueOf(V1 v);
}
