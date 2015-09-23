/**
 * copyright (a) 2010-2015 tomstools.org. All rights reserved.
 */
package org.tomstools.web.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * WEB指标信息
 * 
 * @author admin
 * @date 2015年7月23日
 * @time 下午3:43:55
 * @version 1.0
 */
public class WebMetricSubInfo {
    private int id;
    private String code;
    private String url;
    private String urlBack;
    private String pageEncoding;
    private String contentType;
    private String headers;
    private Collection<WebMetric> metrics;

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("id", id);
        result.put("code", code);
        result.put("url", url);
        result.put("urlBack", urlBack);
        result.put("pageEncoding", pageEncoding);
        result.put("headers", headers);
        result.put("metrics", metrics);
        return result;
    }

    public final int getId() {
        return id;
    }

    public final void setId(int id) {
        this.id = id;
    }

    public final String getCode() {
        return code;
    }

    public final void setCode(String code) {
        this.code = code;
    }

    public final String getUrl() {
        return url;
    }

    public final void setUrl(String url) {
        this.url = url;
    }

    public final String getUrlBack() {
        return urlBack;
    }

    public final void setUrlBack(String urlBack) {
        this.urlBack = urlBack;
    }

    public final String getPageEncoding() {
        return pageEncoding;
    }

    public final void setPageEncoding(String pageEncoding) {
        this.pageEncoding = pageEncoding;
    }

    public final String getContentType() {
        return contentType;
    }

    public final void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getHeaders() {
		return headers;
	}

	public void setHeaders(String headers) {
		this.headers = headers;
	}

	public final Collection<WebMetric> getMetrics() {
        return metrics;
    }

    public final void setMetrics(Collection<WebMetric> metrics) {
        this.metrics = metrics;
    }
}
