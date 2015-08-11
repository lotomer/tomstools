/**
 * 
 */
package org.tomstools.common;

/**
 * @author lotomer
 * @date 2015年8月10日
 * @version 1.0
 */
public final class URLUtil {
    public static String removeURLQueryAttibute(String url,String attributeName){
        if (null == attributeName || "".equals(attributeName)){
            return url;
        }
        if (null == url || "".equals(url)){
            return url;
        }
        String reg = "(\\?|\\&)" + attributeName + "=[^$&#]*[&]{0,1}";
        return url.replaceAll(reg,"$1");
    }
}
