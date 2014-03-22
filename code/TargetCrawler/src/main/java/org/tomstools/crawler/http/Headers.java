/**
 * copyright (a) 2010-2012 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.http;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP请求头信息
 * @author lotomer
 * @date 2012-6-12 
 * @time 下午02:57:56
 */
public class Headers {

    private Map<String, String> params;

    public Headers(){
        params = new HashMap<String, String>();
    }
    
    /**
     * 添加HTTP头信息
     * @param name  名称
     * @param value 值
     */
    public void addHeader(String name, String value){
        params.put(name, value);
    }

    /**
     * 获取请求头列表
     * @return 请求头列表。不为null
     */
    public final Map<String, String> getParams() {
        return params;
    }
    
    
}
