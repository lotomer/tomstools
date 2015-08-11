package org.tomstools.web.crawler;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author admin
 * @date 2014年6月23日 
 * @time 下午11:57:26
 * @version 1.0
 */
public class Header extends HashMap<String, String> {
    private static final long serialVersionUID = -2048763895183140436L;

    /**
     * 构造函数
     * @param params 参数列表
     * @since 1.0
     */
    public Header(Map<String,String> headers){
        super(headers);
    }

    public Header() {
        super();
    }
}
