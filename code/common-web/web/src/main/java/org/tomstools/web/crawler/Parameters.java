package org.tomstools.web.crawler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 参数信息
 * @author admin
 * @date 2014年6月19日 
 * @time 下午1:56:22
 * @version 1.0
 */
public class Parameters extends ArrayList<Map<String, String>> {
    private static final long serialVersionUID = 1443661890549268192L;
    
    /**
     * 构造函数
     * @param params 参数列表
     * @since 1.0
     */
    public Parameters(List<Map<String, String>> params){
        addAll(params);
    }

    public Parameters() {
        super();
    }
    
}
