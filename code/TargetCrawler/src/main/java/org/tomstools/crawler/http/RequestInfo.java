/**
 * copyright (a) 2010-2012 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.http;

import java.util.Map.Entry;

import org.tomstools.common.Utils;

/**
 * http请求信息
 * @author lotomer
 * @date 2014-6-12
 * @time 下午02:57:26
 * @version 1.0
 */
public class RequestInfo {
    //private static final Logger LOGGER = Logger.getLogger(RequestInfo.class);
    private String url;
    private Parameters formDatas; // 表单数据
    private Header headers; // 请求头
    private String baseUrl;
    private Proxy proxy;
    public RequestInfo(RequestInfo other) {
        if (null != other){
            this.url = other.url;
            this.formDatas = new Parameters(other.formDatas);
            this.headers = new Header(other.headers);
            this.proxy = new Proxy(other.proxy);
        }
    }

    public RequestInfo() {
    }

    /**
     * @return 返回 url
     * @since 1.0
     */
    public final String getUrl() {
        return url;
    }

    /**
     * @param url 设置 url
     * @since 1.0
     */
    public final void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return 返回 表单数据
     * @since 1.0
     */
    public final Parameters getFormDatas() {
        return formDatas;
    }

    /**
     * @param formDatas 设置 表单数据
     * @since 1.0
     */
    public final void setFormDatas(Parameters formDatas) {
        this.formDatas = formDatas;
    }

    /**
     * @return 返回 headers
     * @since 1.0
     */
    public final Header getHeaders() {
        return headers;
    }

    /**
     * @param headers 设置 headers
     * @since 1.0
     */
    public final void setHeaders(Header headers) {
        this.headers = headers;
    }

    
    public final Proxy getProxy() {
		return proxy;
	}

	public final void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

	/**
     * 从另一个对象中复制有效数据。有效数据即值不为null
     * 
     * @param other 包含有效数据的另一个对象
     * @since 1.0
     */
    public void copyValidDatas(final RequestInfo other) {
        if (null != other) {
            // 复制url
            if (null != other.url) {
                // 判断新url是否以&开头，如果是，则直接追加在原url上
                if (other.url.startsWith("&")){
                    if (baseUrl == null) baseUrl = url;
                    url = baseUrl + other.url;
                }else{
                    url = other.url;
                }
            }
            // 复制请求头
            if (!Utils.isEmpty(other.headers)) {
                for (Entry<String, String> header : other.headers.entrySet()) {
                    if (null != header.getValue()) {
                        if (null == headers){
                            headers = new Header();
                        }
                        headers.put(header.getKey(), header.getValue());
                    }
                }
            }
            // 复制表单数据
            if (!Utils.isEmpty(other.formDatas)) {
                // 所有不能重复的属性全部在第一个map中，其他重复的属性在后面的map中
                if (null == formDatas){
                    formDatas = new Parameters();
                }
                for (int i = 0; i < other.formDatas.size(); i++) {
                    if (formDatas.size() < i + 1){
                        formDatas.add(other.formDatas.get(i));
                    }else{
                        formDatas.get(i).putAll(other.formDatas.get(i));
                    }
                }
            }
            // 复制代理
            if (!Utils.isEmpty(other.proxy)) {
            	proxy = new Proxy(other.proxy);
            }
        }
    }

    /*
     * @since 1.0
     */
    
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RequestInfo [url=").append(url).append(", formDatas=").append(formDatas)
                .append(", headers=").append(headers).append("]");
        return builder.toString();
    }
    
    
}
