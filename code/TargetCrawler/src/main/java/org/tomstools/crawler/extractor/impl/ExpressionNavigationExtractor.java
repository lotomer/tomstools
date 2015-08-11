/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.extractor.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.tomstools.common.Utils;
import org.tomstools.crawler.common.Element;
import org.tomstools.crawler.extractor.NavigationExtractor;
import org.tomstools.crawler.http.Header;
import org.tomstools.crawler.http.Parameters;
import org.tomstools.crawler.http.RequestInfo;

/**
 * 使用表达式自动生成导航页面的抽取器
 * 
 * @author admin
 * @date 2014年3月15日
 * @time 上午1:09:50
 * @version 1.0
 */
public class ExpressionNavigationExtractor implements NavigationExtractor {
    private String expression;
    //private RequestInfo requestInfo;
    private Map<String, String> parameterTemplate;
    /**
     * @param expression 表达式：开始值|结束值|递增或递减的步长
     * @since 1.0
     */
    public ExpressionNavigationExtractor( String expression,Map<String,String> parameterTemplate) {
        super();
        this.parameterTemplate = parameterTemplate;
        this.expression = expression;
    }

    /*
     * @since 1.0
     */
    public final boolean useExpression() {
        return true;
    }

    
    public List<RequestInfo> getNextPageRequestInfos(Element element) {
        List<RequestInfo> pageRequestInfos = new ArrayList<RequestInfo>();
        if (Utils.isEmpty(parameterTemplate) || Utils.isEmpty(expression)) {
            return pageRequestInfos;
        }

        String[] vs = expression.split("\\|", 3);
        if (3 != vs.length) {
            return pageRequestInfos;
        }
        // 纯数字
        int startValue = Integer.valueOf(vs[0]);
        int endValue = Integer.valueOf(vs[1]);
        int stepValue = Math.abs(Integer.valueOf(vs[2]));
        if (startValue < endValue) {
            // 逐步增加
            for (int i = startValue; i < endValue; i = i + stepValue) {
                pageRequestInfos.add(getRequestInfo(String.valueOf(i)));
            }
        } else {
            // 逐步递减
            for (int i = startValue; i > endValue; i = i - stepValue) {
                pageRequestInfos.add(getRequestInfo(String.valueOf(i)));
            }
        }

        return pageRequestInfos;
    }

    private RequestInfo getRequestInfo(String pageNum) {
        RequestInfo pageRequestInfo = new RequestInfo();
        Parameters formDatas = new Parameters();
        formDatas.add(new HashMap<String,String>());
        pageRequestInfo.setFormDatas(formDatas );
        Header headers = new Header();
        pageRequestInfo.setHeaders(headers );
        for (Entry<String, String> entry : this.parameterTemplate.entrySet()) {
            if (entry.getKey().startsWith("header_")){
                headers.put(new String(entry.getKey().substring("header_".length())), getParameterValue(entry.getKey(), pageNum));
            }else if (entry.getKey().startsWith("form_")){
                // 表单参数，这里的数据统一放到第一个map中
                formDatas.get(0).put(new String(entry.getKey().substring("form_".length())), getParameterValue(entry.getKey(), pageNum));
                
            }else{
                // url
                pageRequestInfo.setUrl(getParameterValue(entry.getKey(), pageNum));
            }
        }
        return pageRequestInfo;
    }
    private String getParameterValue(String parameterName, String parameterValue) {
        if (Utils.isEmpty(parameterTemplate)){
            return parameterValue;
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
//    /**
//     * @return 返回 requestInfo
//     * @since 1.0
//     */
//    public final RequestInfo getRequestInfo() {
//        return requestInfo;
//    }
//
//    /**
//     * @param requestInfo 设置 requestInfo
//     * @since 1.0
//     */
//    public final void setRequestInfo(RequestInfo requestInfo) {
//        this.requestInfo = requestInfo;
//    }

    
}
