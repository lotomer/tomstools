package org.tomstools.web.crawler;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

/**
 * 页面抓取工具。 XXX 暂不支持登录
 * 
 * @author lotomer
 * @date 2012-6-9
 * @time 上午09:13:01
 */
public class PageFetcher {
    private static final Log logger = LogFactory.getLog(PageFetcher.class);
    private RequestInfo requestInfo;
    private int connectionTimeOut = 8000;
    private int socketTimeOut = 10000;
    private int tryCount = 3;
    private Charset defaultCharset; // GBK与GB2312是不同的编码，其中有细微差别，一定要根据网页本身的编码来指定默认编码
    private String method = "GET";
    // private static final Charset GBK = Charset.forName("GBK");
    private static final String DEFAULT_CHARSET_NAME = "UTF-8";
    private static final Pattern locationPattern = Pattern.compile("self\\.location=\"([^\"]+?)\"",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    public PageFetcher() {
        this(DEFAULT_CHARSET_NAME);
    }

    /**
     * 
     * @param defaultCharsetName GBK与GB2312是不同的编码，其中有细微差别，一定要根据网页本身的编码来指定默认编码
     * @since 1.0
     */
    public PageFetcher(String defaultCharsetName) {
        setDefaultCharsetName(defaultCharsetName);
    }

    /**
     * 设置默认编码。GBK与GB2312是不同的编码，其中有细微差别，一定要根据网页本身的编码来指定默认编码
     * 
     * @param defaultCharsetName 默认编码
     * @since 1.0
     */
    public void setDefaultCharsetName(String defaultCharsetName) {
        try {
            defaultCharset = Charset.forName(defaultCharsetName);
        } catch (IllegalCharsetNameException e) {
            defaultCharset = Charset.forName(DEFAULT_CHARSET_NAME);
        } catch (UnsupportedCharsetException e) {
            defaultCharset = Charset.forName(DEFAULT_CHARSET_NAME);
        } catch (IllegalArgumentException e) {
            defaultCharset = Charset.forName(DEFAULT_CHARSET_NAME);
        }
    }

    /**
     * 根据指定的URL获取内容
     * 
     * @param pageUrl 指定的URL。可为null或空字符串，返回null
     * @return 页面内容。爬取失败则返回null
     */
    public String fetchPageContent(String pageUrl) {
        if (StringUtils.isEmpty(pageUrl)) {
            return "";
        }
        String responseText = null;
        RequestBuilder requestBuilder = null;
        if (HttpGet.METHOD_NAME.equalsIgnoreCase(method)) {
            requestBuilder = RequestBuilder.get();
        } else {
            requestBuilder = RequestBuilder.post();
        }
        requestBuilder.setUri(pageUrl);
        // 初始化连接
        initHttp(requestBuilder);
        // HttpPost httpPost = new HttpPost(pageUrl);
        Builder builder = RequestConfig.custom();
        // 设置启动连接超时时间
        builder.setConnectionRequestTimeout(connectionTimeOut);
        // httpclient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
        // connectionTimeOut);
        // 设置连接持续时间
        builder.setSocketTimeout(socketTimeOut);
        // 运行重复重定向
        builder.setCircularRedirectsAllowed(true);
        // httpclient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT,
        // socketTimeOut);
        logger.info("executing request \"" + requestBuilder.getUri() + "\"");
        if (logger.isInfoEnabled()) {
            logger.info("request info: " + requestInfo);
        }
        HttpClient httpClient = HttpClients.createDefault();

        for (int i = 0; i < tryCount; ++i) {
            // 设置连接持续时间，每次失败则延长5秒
            builder.setSocketTimeout(socketTimeOut + i * i * 5000);
            requestBuilder.setConfig(builder.build());
            HttpUriRequest httpUriPost = requestBuilder.build();
            HttpResponse response = null;
            try {
                response = httpClient.execute(httpUriPost);
                HttpEntity entity = response.getEntity();
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    if (entity != null) {
                        responseText = HTMLUtil.toString(entity, defaultCharset);
                        // responseText = EntityUtils.toString(entity,
                        // defaultCharset);
                        // 查看是否包含self.location自动跳转
                        if (response.getEntity().getContentLength() < 800) {
                            // 内容较少时判断是否包含跳转命令
                            Matcher matcher = locationPattern.matcher(responseText);
                            if (matcher.find()) {
                                return redirect(pageUrl, matcher.group(1),defaultCharset.name());
                            }
                        }
                        if (logger.isDebugEnabled()) {
                            logger.debug(responseText);
                        }
                    }

                    break;
                } else if (HttpStatus.SC_MULTIPLE_CHOICES <= response.getStatusLine()
                        .getStatusCode()
                        && response.getStatusLine().getStatusCode() < HttpStatus.SC_BAD_REQUEST) {
                    // 3xx 重定向
                    org.apache.http.Header location = response.getFirstHeader("Location");
                    if (null != location) {
                        return redirect(pageUrl, location.getValue(),"UTF-8");
                    }
                } else {
                    logger.error("Response status code: "
                            + response.getStatusLine().getStatusCode());
                    logger.error(EntityUtils.toString(entity));
                }
            } catch (ClientProtocolException e) {
                logger.error(e.getMessage(), e);
            } catch (UnknownHostException e) {
                logger.error(e.getMessage(), e);
                // 没有找到主机，则可以直接退出
                // break;
            } catch (ConnectTimeoutException e) {
                logger.error(e.getMessage(), e);
            } catch (SocketTimeoutException e) {
                logger.error(e.getMessage(), e);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            } finally {
                HttpClientUtils.closeQuietly(response);
            }
            logger.error("url: " + pageUrl);
            logger.error("request info: " + requestInfo);
            logger.error("try again..." + (i + 1));
        }
        HttpClientUtils.closeQuietly(httpClient);
        return responseText;
    }

    private String redirect(String pageUrl, String locationUrl, String encoding) throws MalformedURLException {
        String weburl = HTMLUtil.getRealUrl(locationUrl, HTMLUtil.getWebRoot(new URL(pageUrl)),
                pageUrl);
        // 需要对重定向的url进行编码
        try {
            weburl = HTMLUtil.urlEncode(weburl,encoding);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }
        logger.warn("redirect:" + weburl);
        PageFetcher fetcher = this;// new
                                   // PageFetcher(this.defaultCharset.name());
        // 设置Referer
        if (fetcher.getRequestInfo() == null) {
            fetcher.setRequestInfo(new RequestInfo());
        }

        if (null == fetcher.getRequestInfo().getHeaders()) {
            fetcher.getRequestInfo().setHeaders(new Header());
        }
        fetcher.getRequestInfo().getHeaders().put("Referer", pageUrl);
        return fetcher.fetchPageContent(weburl);
    }

    private void initHttp(RequestBuilder requestBuilder) {
        // 首先获取该页面对应的请求数据
        if (null != requestInfo) {
            // logger.warn("before init:" + requestInfo);
            // 请求数据不为空
            Parameters form = requestInfo.getFormDatas();
            if (!StringUtils.isEmpty(form)) {
                // 设置表单属性
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                for (Map<String, String> map : form) {
                    if (null != map) {
                        for (Entry<String, String> entry : map.entrySet()) {
                            params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                        }
                    }
                }
                // 防止乱码
                HttpEntity entity = new UrlEncodedFormEntity(params, this.defaultCharset);
                requestBuilder.setEntity(entity);
            }
            // 设置请求头信息
            Header headers = requestInfo.getHeaders();
            if (!StringUtils.isEmpty(headers)) {
                for (Entry<String, String> entry : headers.entrySet()) {
                    requestBuilder.addHeader(entry.getKey(), entry.getValue());
                }
            }
            // logger.warn("after init:" + requestInfo);
        }
    }

    /**
     * @param connectionTimeOut 设置 connectionTimeOut
     * @since 1.0
     */
    public final void setConnectionTimeOut(int connectionTimeOut) {
        if (0 < connectionTimeOut)
            this.connectionTimeOut = connectionTimeOut;
    }

    /**
     * @param socketTimeOut 设置 socketTimeOut
     * @since 1.0
     */
    public final void setSocketTimeOut(int socketTimeOut) {
        if (0 < socketTimeOut)
            this.socketTimeOut = socketTimeOut;
    }

    /**
     * @param requestInfo 设置 requestInfo
     * @since 1.0
     */
    public final void setRequestInfo(RequestInfo requestInfo) {
        this.requestInfo = requestInfo;
    }

    /**
     * @param tryCount 设置 tryCount
     * @since 1.0
     */
    public final void setTryCount(int tryCount) {
        this.tryCount = tryCount;
    }

    /**
     * @return 返回 requestInfo
     * @since 1.0
     */
    public final RequestInfo getRequestInfo() {
        return requestInfo;
    }

    /**
     * @param method 设置 method
     * @since 1.0
     */
    public final void setMethod(String method) {
        this.method = method;
    }

    public static void main(String[] args) throws UnsupportedEncodingException, URISyntaxException {

        // Matcher m = locationPattern
        // .matcher("<html><head><meta http-equiv=\"Content-Type\"content=\"text/html; charset=gb2312\" /><meta http-equiv=\"pragma\" content=\"no-cache\" /><meta http-equiv=\"cache-control\" content=\"no-store\" /><meta http-equiv=\"Connection\" content=\"Close\" /><script>function JumpSelf(){ self.location=\"/default.aspx?tabid=263&WebShieldSessionVerify=j2Ns4fh5u1QXCHuaGDfp\";}</script><script>setTimeout(\"JumpSelf()\",700);</script></head><body></body></html>");
        // if (m.find()) {
        // System.out.println(m.group(1));
        // }
        // String data =
        // "42ad98ae-c46a-40aa-aacc-c0884036eeaf:43%~湖南省|8fd0232c-aff0-45d1-a726-63fc4c3d8ea9:3%~协议出让";
        // System.out.println(URLEncoder.encode(data, "gbk"));
        String u = "http://www.landchina.com/default.aspx?tabid=263&p=42ad98ae-c46a-40aa-aacc-c0884036eeaf:43~湖南省|8fd0232c-aff0-45d1-a726-63fc4c3d8ea9:3~协议出让&WebShieldSessionVerify=QzNvGXlh1e2qWktKVgij";
        //String a = "http://www.landchina.com/default.aspx?tabid=263&p=42ad98ae-c46a-40aa-aacc-c0884036eeaf%3A43%25%7E%BA%FE%C4%CF%CA%A1%7C8fd0232c-aff0-45d1-a726-63fc4c3d8ea9%3A3%25%7E%D0%AD%D2%E9%B3%F6%C8%C3";
        System.out.println(u);
        System.out.println(URLDecoder.decode("42ad98ae-c46a-40aa-aacc-c0884036eeaf%3A43%25%7E%BA%FE%C4%CF%CA%A1%7C8fd0232c-aff0-45d1-a726-63fc4c3d8ea9%3A2%25%7E%D5%D0%C5%C4%B9%D2%B3%F6%C8%C3","utf8"));
        Charset c = Charset.forName("UTF-8");
        System.out.println(c.displayName());
        System.out.println(c.name());
        u = HTMLUtil.urlEncode(u, "utf-8");
        System.out.println(u);
        new URI(u);
        PageFetcher f = new PageFetcher("GBK");
        f.setMethod("POST");
        RequestInfo r = new RequestInfo();
        Parameters d = new Parameters();
        Map<String, String> m = new HashMap<String, String>();
        m.put("TAB_QuerySubmitPagerData", "202");
        d.add(m);
        r.setFormDatas(d);
        f.setRequestInfo(r);
        String s = f
                .fetchPageContent("http://www.landchina.com/default.aspx?tabid=263&p=42ad98ae-c46a-40aa-aacc-c0884036eeaf%3A43%25%7E%BA%FE%C4%CF%CA%A1%7C8fd0232c-aff0-45d1-a726-63fc4c3d8ea9%3A2%25%7E%D5%D0%C5%C4%B9%D2%B3%F6%C8%C3");
        System.out.println(s);
    }
}