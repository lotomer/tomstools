/**
 * copyright (a) 2010-2012 tomstools.org. All rights reserved.
 */
package org.tomstools.html.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
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
    private static final Charset CHARSET_GBK = Charset.forName("GBK");
    private static final Charset CHARSET_GB2312 = Charset.forName("GB2312");
    /**
     * 提取http请求返回的正文内容
     * @param entity    http请求返回的内容
     * @param defaultCharset 使用默认字符集
     * @return 正文内容。可能为null
     * @throws IOException
     * @throws ParseException
     * @since 1.0
     */
    public static String toString(
            final HttpEntity entity, final Charset defaultCharset) throws IOException, ParseException {
        Args.notNull(entity, "Entity");
        final InputStream instream = entity.getContent();
        if (instream == null) {
            return null;
        }
        try {
            Args.check(entity.getContentLength() <= Integer.MAX_VALUE,
                    "HTTP entity too large to be buffered in memory");
            int i = (int)entity.getContentLength();
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
            if (CHARSET_GB2312.name().equalsIgnoreCase(charset.name())){
                charset = CHARSET_GBK;
            }
            final Reader reader = new InputStreamReader(instream, charset);
            final CharArrayBuffer buffer = new CharArrayBuffer(i);
            final char[] tmp = new char[1024];
            int l;
            while((l = reader.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
            }
            return buffer.toString();
        } finally {
            instream.close();
        }
    }
}
