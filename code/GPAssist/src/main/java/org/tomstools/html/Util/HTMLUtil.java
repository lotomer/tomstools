/**
 * copyright (a) 2010-2012 tomstools.org. All rights reserved.
 */
package org.tomstools.html.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lotomer
 * @date 2012-6-11 
 * @time 下午02:17:47
 */
public class HTMLUtil {
    public static String parseCharset(String htmlContent) {
        String cs  = "UTF-8";
        Pattern pattern = Pattern
                .compile(
                        "<meta\\s+http-equiv=\"Content-Type\"\\s+content=\"text/html;\\s*charset=(\\S+?)\"",
                        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher matcher = pattern.matcher(htmlContent);
        if (matcher.find()) {
            cs = matcher.group(1);
        }

        return cs;
    }
    public static String getHost(String url) {
        int index = url.indexOf("/", 10);
        if (index < 10){
            return url;
        }else{
            return url.substring(0, index);
        }
    }
    public static String getRealUrl(String htmlUrl, String webRoot, String parentPath) {
        if (htmlUrl.startsWith("http://")){ // 已经是完整的URL，则直接返回
            return htmlUrl;
        }else if (htmlUrl.startsWith("/")){ // 完整的绝对路径，则加上服务器地址返回
            return webRoot + htmlUrl;
        }else{ // 相对路径，则生成相对于parentUrl的相对路径
            if (parentPath.endsWith("/")){ // 目录，则直接加上相对路径
                return webRoot + parentPath + htmlUrl;
            }else{
                Pattern pattern = Pattern.compile("(\\.htm|\\.html|\\.dhtml|\\.jsp|\\.asp|\\.aspx|\\.php)$",Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
                Matcher matcher = pattern.matcher(parentPath);
                if (matcher.find()){
                    // 是文件，则取其父目录
                    int index = parentPath.lastIndexOf("/");
                    if (-1 < index){
                        return webRoot + parentPath.substring(0, index + 1) + htmlUrl;
                    }else{
                        return webRoot + htmlUrl;
                    }
                }else{
                    return webRoot + parentPath + "/" + htmlUrl;
                }
            }
        }        
    }
    /**
     * 去除文本中的标签。
     * 如"<div>asdf"去除标签后得到"asdf"
     * @param html
     * @return 去除标签后的数据
     */
    public static String removeTags(String html) {
        return html.replaceAll("<.*?>", "");
    }
}
