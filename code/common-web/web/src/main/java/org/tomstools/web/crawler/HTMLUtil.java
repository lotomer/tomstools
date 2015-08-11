package org.tomstools.web.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;
import org.springframework.util.StringUtils;

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
        if (StringUtils.isEmpty(url)) {
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
                if (!matcher.find()) {
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

    private static final Charset CHARSET_GBK = Charset.forName("GBK");
    private static final Charset CHARSET_GB2312 = Charset.forName("GB2312");

    /**
     * 提取http请求返回的正文内容
     * 
     * @param entity http请求返回的内容
     * @param defaultCharset 使用默认字符集
     * @return 正文内容。可能为null
     * @throws IOException
     * @throws ParseException
     * @since 1.0
     */
    public static String toString(final HttpEntity entity, final Charset defaultCharset)
            throws IOException, ParseException {
        Args.notNull(entity, "Entity");
        final InputStream instream = entity.getContent();
        if (instream == null) {
            return null;
        }
        try {
            Args.check(entity.getContentLength() <= Integer.MAX_VALUE,
                    "HTTP entity too large to be buffered in memory");
            int i = (int) entity.getContentLength();
            if (i < 0) {
                i = 4096;
            }
            Charset charset = null;
            try {
                final ContentType contentType = ContentType.get(entity);
                if (contentType != null) {
                    charset = contentType.getCharset();
                }
            } catch (final UnsupportedCharsetException ex) {
                throw new UnsupportedEncodingException(ex.getMessage());
            }
            if (charset == null) {
                charset = defaultCharset;
            }
            if (charset == null) {
                charset = HTTP.DEF_CONTENT_CHARSET;
            }
            // 如果是GB2312，则改用GBK
            if (CHARSET_GB2312.name().equalsIgnoreCase(charset.name())) {
                charset = CHARSET_GBK;
            }
            final Reader reader = new InputStreamReader(instream, charset);
            final CharArrayBuffer buffer = new CharArrayBuffer(i);
            final char[] tmp = new char[1024];
            int l;
            while ((l = reader.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
            }
            return buffer.toString();
        } finally {
            instream.close();
        }
    }

    /**
     * 给url编码
     * 
     * @param url 待编码url
     * @return 编码后的url
     * @throws UnsupportedEncodingException
     * @since 1.0
     */
    public static final String urlEncode(String url, String encoding)
            throws UnsupportedEncodingException {
        if (null == url)
            return null;
        int index = url.indexOf("?");
        if (-1 < index) {
            StringBuilder retValue = new StringBuilder();
            retValue.append(url.substring(0, index)).append("?");
            String[] params = url.substring(index + 1).split("&");
            boolean isFirst = true;
            for (String param : params) {
                if (!isFirst) {
                    retValue.append("&");
                } else {
                    isFirst = false;
                }
                String[] vs = param.split("=", 2);
                if (vs.length < 2) {
                    retValue.append(URLEncoder.encode(param, encoding));
                } else {
                    retValue.append(vs[0]).append("=").append(URLEncoder.encode(vs[1], encoding));
                }
            }
            return retValue.toString();
        } else {
            return url;
        }
    }


    public static void main(String[] args) {
    }
}
