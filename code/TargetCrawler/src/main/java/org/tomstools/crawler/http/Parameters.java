/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.http;

import java.util.HashMap;
import java.util.Map;

/**
 * 参数信息
 * @author admin
 * @date 2014年6月19日 
 * @time 下午1:56:22
 * @version 1.0
 */
public class Parameters extends HashMap<String, String> {
    private static final long serialVersionUID = 1443661890549268192L;
    
    /**
     * 构造函数
     * @param params 参数列表
     * @since 1.0
     */
    public Parameters(Map<String,String> params){
        super(params);
    }

    public Parameters() {
        super();
    }
    
}
