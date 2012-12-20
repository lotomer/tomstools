/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.common.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;

/**
 * 通用工具类
 * @author lotomer
 * @date 2011-12-14 
 * @time 下午02:42:29
 */
public final class Utils {
    private Utils(){}
    
    /**
     * 判断对象是否为空
     * @param data 待判断的对象
     * @return true 为null或仅包含空格的字符串； false 非空
     */
    public static boolean isEmpty(Object data){
        return (null == data || "".equals(data.toString().trim()));
    }
    
    /**
     * 判断结果集是否为空
     * @param <E> 泛型
     * @param data 结果集
     * @return true 结果集为null或空； false 非空
     */
    public static <E> boolean  isEmpty(Collection<E> data){
        return (null == data || 0 == data.size());
    }
    
    /**
     * 关闭流
     * 
     * @param c
     */
    public static void close(Closeable c) {
        if (null != c) {
            try {
                c.close();
                c = null;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
