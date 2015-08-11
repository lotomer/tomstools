/**
 * 
 */
package org.tomstools.web.metric;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.tomstools.common.parse.ContentParser;
import org.tomstools.common.parse.HTMLContentParser;
import org.tomstools.common.parse.JSONContentParser;
import org.tomstools.web.crawler.PageFetcher;
import org.tomstools.web.model.WebMetric;

/**
 * WEB指标生成器
 * 
 * @author lotomer
 *
 */
public class WebMetricGenerator implements MetricGenerator {
    private final static Log LOG = LogFactory.getLog(WebMetricGenerator.class);
    private List<String> urls = new ArrayList<String>();
    private List<WebMetric> metrics;
    private PageFetcher pageFetcher;
    private ContentParser contentParser;

    public WebMetricGenerator(String url, String[] urlBacks, String pageEncoding, String contentType,
            List<WebMetric> metrics) {
        Assert.notNull(metrics, "The metrics cannot be null!");
        // 保持URL
        if (!StringUtils.isEmpty(url)) {
            urls.add(url);
        }
        if (null != urlBacks) {
            for (int i = 0; i < urlBacks.length; i++) {
                if (!StringUtils.isEmpty(urlBacks[i])) {
                    urls.add(urlBacks[i]);
                }
            }
        }
        if ("json".equalsIgnoreCase(contentType)) {
            contentParser = new JSONContentParser();
        } else {
            contentParser = new HTMLContentParser();
        }
        this.metrics = metrics;
        this.pageFetcher = new PageFetcher(pageEncoding);
    }

    public Map<String, WebMetric> generate() {
        LOG.info("Start generate metrics....");
        String content = null;
        for (String url : urls) {
            content = pageFetcher.fetchPageContent(url);
            if (null != content) {
                break;
            }
        }
        if (!StringUtils.isEmpty(content)) {
            contentParser.init(content);
            Map<String, WebMetric> result = new HashMap<String, WebMetric>();
            for (WebMetric webMetric : metrics) {
                WebMetric aWebMetric = webMetric.copyValue();
                Object value = contentParser.parse(webMetric.getSelector(),null != webMetric.getValue() ? webMetric.getValue().toString(): null);
                aWebMetric.setValue(value);
                result.put(webMetric.getTitle(), aWebMetric);
            }

            return result;
        }
        return Collections.emptyMap();
    }

}
