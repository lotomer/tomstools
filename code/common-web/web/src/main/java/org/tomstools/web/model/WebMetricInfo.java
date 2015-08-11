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
public class WebMetricInfo {
    private int id;
    private String title;
    private String name;
    private String templateContent;
    private String templateScript;
    private Collection<WebMetricSubInfo> subInfo;

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("id", id);
        result.put("title", title);
        result.put("name", name);
        result.put("templateContent", templateContent);
        for (WebMetricSubInfo sub : subInfo) {
            result.put(sub.getCode(), sub.toMap());
        }
        
        return result;
    }

    public final int getId() {
        return id;
    }

    public final void setId(int id) {
        this.id = id;
    }

    public final String getTitle() {
        return title;
    }

    public final void setTitle(String title) {
        this.title = title;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final String getTemplateContent() {
        return templateContent;
    }

    public final void setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
    }

    public final String getTemplateScript() {
        return templateScript;
    }

    public final void setTemplateScript(String templateScript) {
        this.templateScript = templateScript;
    }

    public final Collection<WebMetricSubInfo> getSubInfo() {
        return subInfo;
    }

    public final void setSubInfo(Collection<WebMetricSubInfo> subInfo) {
        this.subInfo = subInfo;
    }
}
