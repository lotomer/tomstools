/**
 * copyright (a) 2010-2012 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.http;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.tomstools.crawler.common.Logger;
import org.tomstools.crawler.common.Utils;


/**
 * 页面抓取工具。 XXX 暂不支持登录
 * 
 * @author lotomer
 * @date 2012-6-9
 * @time 上午09:13:01
 */
public class PageFetcher {
    private static final Logger logger = Logger.getLogger(PageFetcher.class);
    private Map<String, RequestInfo> requestInfos = new HashMap<String, RequestInfo>();
    private int connectionTimeOut = 8000;
    private int socketTimeOut = 10000;
    private int tryCount = 3;
    private Charset defaultCharset;
    private static final String DEFAULT_CHARSET_NAME = "UTF8";
    public PageFetcher() {
        this(DEFAULT_CHARSET_NAME);
    }

    public PageFetcher(String defaultCharsetName) {
        try{
            defaultCharset = Charset.forName(defaultCharsetName);
        }catch(IllegalCharsetNameException e){
            defaultCharset = Charset.forName(DEFAULT_CHARSET_NAME);
        }catch(UnsupportedCharsetException e){
            defaultCharset = Charset.forName(DEFAULT_CHARSET_NAME);
        }catch(IllegalArgumentException e){
            defaultCharset = Charset.forName(DEFAULT_CHARSET_NAME);
        }
    }

    public boolean login(URL url) {
        // 首先获取该页面对应的请求数据
        RequestInfo requestInfo = requestInfos.get(url.getHost());
        if (null == requestInfo) {
            // 没有该页面对应的请求数据，则获取该站点对应的请求数据
            requestInfo = requestInfos.get(url);
        }

        if (null == requestInfo) {
            return true;
        } else {
            // XXX 需要登录
            return false;
        }
    }

    /**
     * 根据指定的URL获取内容
     * 
     * @param pageUrl 指定的URL。可为null或空字符串，返回null
     * @return 页面内容。爬取失败则返回null
     */
    public String fetchPageContent(String pageUrl) {
        if (Utils.isEmpty(pageUrl)) {
            return null;
        }
        String responseText = null;
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(pageUrl);
        // 设置启动连接超时时间
        httpclient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
                connectionTimeOut);
        // 设置连接持续时间
        httpclient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, socketTimeOut);
        // 初始化连接
        initHttp(httpget);
        logger.debug("executing request " + httpget.getURI());
        for (int i = 0; i < tryCount; ++i) {
            // 设置连接持续时间，每次失败则延长5秒
            httpclient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, socketTimeOut + i * i * 5000);
            try {
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                logger.debug("Response status code: " + response.getStatusLine().getStatusCode());
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    if (entity != null) {
                        //byte[] contents = EntityUtils.toByteArray(entity);
                        Charset charset = ContentType.getOrDefault(entity).getCharset();
                        if (!Utils.isEmpty(charset)) {
                            //charsetName = HTMLUtil.parseCharset(new String(contents)).toUpperCase();
                            charset = defaultCharset;
                        }
                        logger.debug("page charset: " + charset);
                        responseText = EntityUtils.toString(entity, charset);
                        logger.debug(responseText);
                    }
                }

                // Do not feel like reading the response body
                // Call abort on the request object
                httpget.abort();

                // When HttpClient instance is no longer needed,
                // shut down the connection manager to ensure
                // immediate deallocation of all system resources
                httpclient.getConnectionManager().shutdown();

                break;
            } catch (ClientProtocolException e) {
                logger.error(e.getMessage(), e);
            } catch(UnknownHostException e){
                logger.error(e.getMessage(), e);
                // 没有找到主机，则可以直接退出
                //break;
            }  catch(ConnectTimeoutException e){
                logger.error(e.getMessage(), e);
            }catch(SocketTimeoutException e){
                logger.error(e.getMessage(), e);
            }catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            logger.info("try again..." + (i + 1));
        }
        return responseText;
    }

    private void initHttp(HttpGet httpget) {
        // 首先获取该页面对应的请求数据
        RequestInfo requestInfo = requestInfos.get(httpget.getURI());
        if (null == requestInfo) {
            // 没有该页面对应的请求数据，则获取该站点对应的请求数据
            requestInfo = requestInfos.get(httpget.getURI().getHost());
        }

        if (null != requestInfo) {
            // 请求数据不为空
            Form form = requestInfo.getForm();
            if (form.needLogin()) {
                // 登录
                // XXX 暂时不对单个页面的登录做处理，以后需要再添加
            }
            Map<String, String> attrs = form.getAttributes();
            if (!attrs.isEmpty()) {
                // 设置表单属性
                BasicHttpParams params = new BasicHttpParams();
                httpget.setParams(params);
                Set<Entry<String, String>> set = attrs.entrySet();
                for (Entry<String, String> entry : set) {
                    params.setParameter(entry.getKey(), entry.getKey());
                }
            }

            // 设置请求头信息
            Set<Entry<String, String>> set = requestInfo.getHeaders().getParams().entrySet();
            for (Entry<String, String> entry : set) {
                httpget.addHeader(entry.getKey(), entry.getKey());
            }
        }
    }

    String excludeContent(String content, String regexpFilterExclude) {
        // 需要过滤的内容
        if (!Utils.isEmpty(regexpFilterExclude)) {
            Pattern pattern = Pattern.compile(regexpFilterExclude, Pattern.CASE_INSENSITIVE
                    | Pattern.UNICODE_CASE | Pattern.DOTALL);
            Matcher matcher = pattern.matcher(content);
            int iPosStart = 0;
            int iPosEnd = content.length();
            StringBuffer result = new StringBuffer();
            while (matcher.find()) {
                result.append(content.substring(iPosStart, matcher.start()));
                iPosStart = matcher.end();
            }
            result.append(content.substring(iPosStart, iPosEnd));
            content = result.toString();
        }
        return content;
    }

    String includeContent(String content, String regexpFilterInclude) {
        // 需要包含的内容
        if (!Utils.isEmpty(regexpFilterInclude)) {
            Pattern pattern = Pattern.compile(regexpFilterInclude, Pattern.CASE_INSENSITIVE
                    | Pattern.UNICODE_CASE | Pattern.DOTALL);
            Matcher matcher = pattern.matcher(content);
            StringBuilder result = new StringBuilder();
            boolean matched = false;
            while (matcher.find()) {
                matched = true;
                result.append(matcher.group(1));
                result.append(" ");
            }
            if (matched) {
                content = result.toString().trim();
            }
        }

        return content;
    }

    /**
     * @param connectionTimeOut 设置 connectionTimeOut
     * @since 1.0
     */
    public final void setConnectionTimeOut(int connectionTimeOut) {
        this.connectionTimeOut = connectionTimeOut;
    }

    /**
     * @param socketTimeOut 设置 socketTimeOut
     * @since 1.0
     */
    public final void setSocketTimeOut(int socketTimeOut) {
        this.socketTimeOut = socketTimeOut;
    }

}