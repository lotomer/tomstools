/**
 * copyright (a) 2010-2015 tomstools.org. All rights reserved.
 */
package org.tomstools.web.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.tomstools.common.parse.TemplateParser;
import org.tomstools.web.metric.WebMetricGenerator;
import org.tomstools.web.model.User;
import org.tomstools.web.model.WebMetric;
import org.tomstools.web.model.WebMetricInfo;
import org.tomstools.web.model.WebMetricSubInfo;
import org.tomstools.web.persistence.WebMetricMapper;

/**
 * web指标服务
 * 
 * @author admin
 * @date 2015年7月23日
 * @time 上午10:49:30
 * @version 1.0
 */
@Service("webMetricService")
@Transactional
public class WebMetricService {
    private final static Log LOG = LogFactory.getLog(WebMetricService.class);
    @Autowired
    private WebMetricMapper webMetricMapper;

    /**
     * 获取WEB指标信息
     * 
     * @param metricName
     *            指标名
     * @param params
     *            参数
     * @param user
     *            用户信息
     * @return 指标信息。没有指标名对应的有效配置则返回为null
     * @since 1.0
     */
    public WebMetricInfo getWebMetric(String metricName, Map<String, String> params, User user) {
        LOG.info("Get web metric: " + metricName);
        WebMetricInfo metricInfo = webMetricMapper.selectWebMetric(metricName);
        if (null != metricInfo) {
            WebMetricInfo template = webMetricMapper.selectWebMetricTemplate(metricInfo.getId());
            if (!StringUtils.isEmpty(template)) {
                metricInfo.setTemplateContent(template.getTemplateContent());
                metricInfo.setTemplateScript(template.getTemplateScript());
            }
            List<WebMetricSubInfo> subInfos = webMetricMapper.selectWebMetricSub(metricInfo.getId());
            if (null != subInfos) {
                Map<String, Object> configs = new HashMap<String, Object>();
                configs.put("config", user.getConfigs());
                // 请求参数也需要进行替换
                for (Entry<String, String> entry : params.entrySet()) {
                    entry.setValue(TemplateParser.parse(configs, entry.getValue()));
                }
                configs.put("request", params);
                for (WebMetricSubInfo subInfo : subInfos) {
                    List<WebMetric> metrics = webMetricMapper.selectWebMetricSubDetail(subInfo.getId());
                    if (null != metrics) {
                        String urlBack = subInfo.getUrlBack();
                        String[] urlBacks = null;
                        if (!StringUtils.isEmpty(urlBack)) {
                            urlBacks = urlBack.split(" ");
                            for (int i = 0; i < urlBacks.length; i++) {
                                urlBacks[i] = TemplateParser.parse(configs, urlBacks[i]);
                            }
                        }
                        WebMetricGenerator webMetricGenerator = new WebMetricGenerator(
                                TemplateParser.parse(configs, subInfo.getUrl()), urlBacks, subInfo.getPageEncoding(),
                                subInfo.getContentType(),TemplateParser.parse(configs, subInfo.getHeaders(),false), metrics);
                        subInfo.setMetrics(webMetricGenerator.generate().values());
                    }
                }

                metricInfo.setSubInfo(subInfos);
                Map<String, Object> attrs = metricInfo.toMap();
                if (null != params) {
                    attrs.put("request", params);
                    attrs.put("config", user.getConfigs());
                }
                // 解析模板
                if (!StringUtils.isEmpty(metricInfo.getTemplateContent())) {
                    metricInfo.setTemplateContent(TemplateParser.parse(attrs, metricInfo.getTemplateContent(),false));
                }
                if (!StringUtils.isEmpty(metricInfo.getTemplateScript())) {
                    metricInfo.setTemplateScript(TemplateParser.parse(attrs, metricInfo.getTemplateScript(),false));
                }
            }
        }

        return metricInfo;
    }
}