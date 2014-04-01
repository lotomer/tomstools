/**
 * copyright (a) 2010-2012 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.util;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lotomer
 * @date 2012-6-11
 * @time 下午02:17:47
 */
public class HTMLUtil {
    public static String parseCharset(String htmlContent) {
        String cs = "UTF-8";
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

    private static Pattern pattern = Pattern.compile(
            "(\\.htm|\\.html|\\.dhtml|\\.jsp|\\.asp|\\.aspx|\\.php)$", Pattern.CASE_INSENSITIVE
                    | Pattern.UNICODE_CASE);

    public static String getRealUrl(String htmlUrl, String webRoot, String parentPath) {
        if (htmlUrl.startsWith("http://") || htmlUrl.startsWith("https://")) { // 已经是完整的URL，则直接返回
            return htmlUrl;
        } else if (htmlUrl.startsWith("/")) { // 完整的绝对路径，则加上服务器地址返回
            return webRoot + htmlUrl;
        } else if (htmlUrl.startsWith("?")) {
            return webRoot + parentPath + htmlUrl;
        } else { // 相对路径，则生成相对于parentUrl的相对路径
            if (parentPath.endsWith("/")) { // 目录，则直接加上相对路径
                return webRoot + parentPath + htmlUrl;
            } else {
                // 不是目录，则看输入的url是否只带了查询参数，即以问号?开头
                Matcher matcher = pattern.matcher(parentPath);
                if (matcher.find()) {
                    // 是文件，则取其父目录
                    int index = parentPath.lastIndexOf("/");
                    if (-1 < index) {
                        return webRoot + parentPath.substring(0, index + 1) + htmlUrl;
                    } else {
                        return webRoot + htmlUrl;
                    }
                } else {
                    return webRoot + parentPath + "/" + htmlUrl;
                }
            }
        }
    }

    /**
     * 获取url对应的网站根网址
     * 
     * @param url 完整url
     * @return 网站根网址
     * @since 1.0
     */
    public static String getWebRoot(URL url) {
        if (-1 < url.getPort()) {
            return url.getProtocol() + "://" + url.getHost() + ":" + url.getPort();
        } else {
            return url.getProtocol() + "://" + url.getHost();
        }
    }
}
