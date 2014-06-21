/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.extractor.impl;

import java.util.Map;

import org.tomstools.common.Utils;

/**
 * 分页导航抽取器：从页面的分页信息中抽取数据，并将数据填入模板以生成新的分页
 * @author admin
 * @date 2014年3月15日
 * @time 上午1:42:44
 * @version 1.0
 */
public class TemplatePageNavigationExtractor extends PageNavigationExtractor {

    private Map<String, String> parameterTemplate;

    /**
     * @param cssQuery 元素选择表达式
     * @param regexp 正则表达式
     * @param regexGroupIndex4parameterNames 正则表达式中group的索引号对应的参数名称，参数名如form_pageno
     * @param parameterTemplate 参数对应的模板。需要替换的参数名使用“${”和“}”包裹，如：${form_pageno}
     * @since 1.0
     */
    public TemplatePageNavigationExtractor(String cssQuery, String regexp, Map<Integer,String> regexGroupIndex4parameterNames, Map<String,String> parameterTemplate){
        super(cssQuery, regexp, regexGroupIndex4parameterNames);
        this.parameterTemplate = parameterTemplate;
    }

    /*
     * @since 1.0
     */
    @Override
    protected String getParameterValue(String parameterName, String parameterValue) {
        if (Utils.isEmpty(parameterTemplate)){
            return super.getParameterValue(parameterName, parameterValue);
        }else{
            String template = parameterTemplate.get(parameterName);
            if (!Utils.isEmpty(template)){
                String regex = "\\$\\{" + parameterName + "\\}";
                return template.replaceAll(regex, parameterValue);
            }else{
                return parameterValue;
            }
        }
    }
}
