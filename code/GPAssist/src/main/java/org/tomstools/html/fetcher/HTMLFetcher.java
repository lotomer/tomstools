/**
 * copyright (a) 2010-2012 tomstools.org. All rights reserved.
 */
package org.tomstools.html.fetcher;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.tomstools.common.log.Logger;
import org.tomstools.common.util.Utils;
import org.tomstools.html.Util.HTMLUtil;

/**
 * HTML页面抓取工具。
 * 
 * @author lotomer
 * @date 2012-6-9
 * @time 上午09:13:01
 */
public class HTMLFetcher {
    private static final Logger LOG = Logger.getLogger(HTMLFetcher.class);
    // private Map<String, RequestInfo> requestInfos = new HashMap<String,
    // RequestInfo>();
    private HttpHost proxy;

    public HTMLFetcher() {
        super();
    }

    public HTMLFetcher(String proxyHost, int proxyPort, String proxyProtocal) {
        super();
        if (Utils.isEmpty(proxyProtocal)){
            proxyProtocal = "http";
        }
        
        proxy = new HttpHost(proxyHost, proxyPort, proxyProtocal);
    }

    public boolean login(URL url) {
        // // 首先获取该页面对应的请求数据
        // RequestInfo requestInfo = requestInfos.get(url.getHost());
        // if (null == requestInfo){
        // // 没有该页面对应的请求数据，则获取该站点对应的请求数据
        // requestInfo = requestInfos.get(url);
        // }
        //
        // if (null == requestInfo){
        // return true;
        // }else{
        // // XXX 需要登录
        // return false;
        // }
        return true;
    }

    /**
     * 根据指定的URL获取内容
     * 
     * @param htmlUrl 指定的URL
     * @param regexpFilterInclude
     *            内容过滤器。需要包含哪些内容的正则表达式，包含的内容必须以分组的形式出现，并且只支持一组，如&lt;div&gt;(.*?)
     *            &lt;/div&gt;。 可为null，表示包含全部
     * @param regexpFilterExclude 内容过滤器。不能包含的内容的正则表达式。可为null，表示包含全部。
     *            如果regexpFilterInclude不为null，则在regexpFilterInclude处理的基础上进行第二次过滤
     * @return 页面内容。null 为出现了异常
     */
    public String fetchHTMLContent(String htmlUrl) {
        String result = doFetchHTMLContent(htmlUrl);
        if (null == result){
            // 出现了异常，重试一次
            LOG.warn("try again!" + htmlUrl);
            result = doFetchHTMLContent(htmlUrl);
        }
        
        return result;
    }
    
    private String doFetchHTMLContent(String htmlUrl) {
        String responseText = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(htmlUrl);
        if (null != proxy) {
            // 设置代理
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        }
        // 设置启动连接超时时间
        httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
        // 设置连接持续时间
        httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
        // 初始化连接
        initHttp(httpGet);
        LOG.info("executing request " + httpGet.getURI());
        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            LOG.info("Response status code: " + response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                if (entity != null) {
                    String contents = EntityUtils.toString(entity);
                    String charset = EntityUtils.getContentCharSet(entity);
                    if (Utils.isEmpty(charset)) {
                        charset = HTMLUtil.parseCharset(new String(contents)).toUpperCase();
                    }
                    // System.out.println(charset);
                    responseText = contents;
                    // logger.info(responseText);
                }
            }

            // Do not feel like reading the response body
            // Call abort on the request object
            httpGet.abort();

            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpClient.getConnectionManager().shutdown();
        } catch (ClientProtocolException e) {
            LOG.error(e.getMessage(), e);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return responseText;
    }

    private void initHttp(HttpGet httpget) {
        // // 首先获取该页面对应的请求数据
        // RequestInfo requestInfo = requestInfos.get(httpget.getURI());
        // if (null == requestInfo){
        // // 没有该页面对应的请求数据，则获取该站点对应的请求数据
        // requestInfo = requestInfos.get(httpget.getURI().getHost());
        // }
        //
        // if (null != requestInfo){
        // // 请求数据不为空
        // Form form = requestInfo.getForm();
        // if (form.needLogin()){
        // // 登录
        // // XXX 暂时不对单个页面的登录做处理，以后需要再添加
        // }
        // Map<String, String> attrs = form.getAttributes();
        // if (!attrs.isEmpty()){
        // // 设置表单属性
        // BasicHttpParams params = new BasicHttpParams();
        // httpget.setParams(params);
        // Set<Entry<String, String>> set = attrs.entrySet();
        // for (Entry<String, String> entry : set) {
        // params.setParameter(entry.getKey(), entry.getKey());
        // }
        // }
        //
        // //设置请求头信息
        // Set<Entry<String, String>> set =
        // requestInfo.getHeaders().getParams().entrySet();
        // for (Entry<String, String> entry : set) {
        // httpget.addHeader(entry.getKey(), entry.getKey());
        // }
        // }
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
}