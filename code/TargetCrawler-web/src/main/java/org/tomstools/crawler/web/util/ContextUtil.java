/**
 * copyright (a) 2010-2015 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.web.util;

import org.apache.struts2.ServletActionContext;

/**
 * 
 * @author admin
 * @date 2015年5月16日 
 * @time 下午2:12:24
 * @version 1.0
 */
public class ContextUtil {
    /**
     * 获取请求参数值
     * @param paramName 参数名
     * @return 参数值。没有找到则返回null
     * @since 1.0
     */
    public static String getRequestParameter(String paramName){
        Object o = ServletActionContext.getContext().getParameters().get(paramName);
        if (null != o) {
            if (o instanceof String[]) {
                String[] os = (String[]) o;
                return os[0];
            }else{
                return String.valueOf(o);
            }
        }else{
            return null;
        }
    }
}
