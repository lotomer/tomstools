/**
 * copyright (a) 2010-2015 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.web.util;

import java.util.regex.Pattern;

/**
 * jsp文件匹配器
 * @author admin
 * @date 2015年5月16日 
 * @time 上午10:51:18
 * @version 1.0
 */
public final class JspMatcher {
    private static final Pattern JSP_PATTERN = Pattern.compile("\\w\\.jsp($|\\?|#)",Pattern.CASE_INSENSITIVE);
    
    /**
     * 判断是否匹配
     * @param uri uri字符串
     * @return true 是jsp文件；false 不是jsp文件
     * @since 1.0
     */
    public static boolean isMatched(String uri){
        if (null == uri || "".equals(uri)){
            return false;
        }else{
            return JSP_PATTERN.matcher(uri).find();
        }
    }
}
