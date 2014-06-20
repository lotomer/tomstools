/**
 * copyright (a) 2010-2012 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.util;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tomstools.crawler.common.Utils;

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

    /**
     * 匹配html文件
     */
    final static Pattern PATTERN_DIRECTORY = Pattern.compile("(/$|/\\?\\S*$)",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    /**
     * 匹配包含协议的url
     */
    final static Pattern PATTERN_PROTOCOL = Pattern.compile("[^:/]+?://\\S+?");

    /**
     * 获取完整的url。
     * 
     * @param url 可能不完整的url
     * @param webRoot 网站域名及根目录（含协议）
     * @param referUrl 关联的url。相对路径时需要
     * @return 完整的url
     * @since 1.0
     */
    public static String getRealUrl(String url, String webRoot, String referUrl) {
        if (Utils.isEmpty(url)) {
            return null;
        } else if (PATTERN_PROTOCOL.matcher(url).find()) { // 已经是完整的URL，则直接返回
            return url;
        } else if (url.startsWith("/")) { // 完整的绝对路径，则加上服务器地址返回
            return webRoot + url;
        } else if (url.startsWith("?")) {
            return webRoot + referUrl + url;
        } else { // 相对路径，则生成相对于parentUrl的相对路径
            if (referUrl.endsWith("/")) { // 目录，则直接加上相对路径
                return webRoot + referUrl + url;
            } else {
                // 不是目录，则看输入的url是否只带了查询参数，即以问号?开头
                Matcher matcher = PATTERN_DIRECTORY.matcher(referUrl);
                if (matcher.find()) {
                    // 是文件，则取其父目录
                    int index = referUrl.lastIndexOf("/");
                    if (-1 < index) {
                        return webRoot + referUrl.substring(0, index + 1) + url;
                    } else {
                        return webRoot + url;
                    }
                } else {
                    return webRoot + referUrl + "/" + url;
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

    public static void main(String[] args) {

    }
}
