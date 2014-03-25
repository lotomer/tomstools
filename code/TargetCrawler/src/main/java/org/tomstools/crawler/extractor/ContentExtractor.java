/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.extractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.tomstools.crawler.common.Element;
import org.tomstools.crawler.common.ElementProcessor;
import org.tomstools.crawler.common.FieldSplitter;

/**
 * 内容抽取器
 * 
 * @author admin
 * @date 2014年3月13日
 * @time 下午5:58:11
 * @version 1.0
 */
public class ContentExtractor {
    // private final static Logger LOGGER =
    // Logger.getLogger(BaseContentHandle.class);
    private LinkedHashMap<String, String> params;
    private String cssQuery;
    private Map<String, FieldSplitter> fieldSplitters;
    private String[] titles;
    private LinkedHashMap<String, String> constantFieldSelectors;

    /**
     * 构造函数
     * 
     * @param cssQuery 正文所在元素的选择表达式
     * @param titles 表头信息
     * @param constantFieldSelectors 固定字段选择器，不随每次属性提取而变化
     * @param params 提取属性配置信息
     * @param fieldSplitters 字段拆分器
     * @since 1.0
     */
    public ContentExtractor(String cssQuery, String[] titles,
            LinkedHashMap<String, String> constantFieldSelectors,
            LinkedHashMap<String, String> params, Map<String, FieldSplitter> fieldSplitters) {
        this.params = params;
        this.cssQuery = cssQuery;
        this.titles = titles;
        this.constantFieldSelectors = constantFieldSelectors;
        if (null != fieldSplitters) {
            this.fieldSplitters = fieldSplitters;
        } else {
            this.fieldSplitters = Collections.emptyMap();
        }
    }

    /**
     * 从节点中提取内容
     * 
     * @param element 待处理的节点
     * @return 提取出来的内容
     * @since 1.0
     */
    public List<Map<String, String>> extractContent(Element element) {
        final List<Map<String, String>> records = new ArrayList<Map<String, String>>();
        final Map<String, String> constantFieldValues = new LinkedHashMap<String, String>();
        if (null != constantFieldSelectors) {
            for (Entry<String, String> entry : constantFieldSelectors.entrySet()) {
                FieldSplitter fieldSplitter = fieldSplitters.get(entry.getKey());
                if (null != fieldSplitter) {
                    // 该字段指定了拆分器，则进行拆分
                    fieldSplitter.process(element.select(entry.getValue()).getText(), constantFieldValues);
                } else {
                    constantFieldValues.put(entry.getKey(), element.select(entry.getValue()).getText());
                }
            }
        }
        element.select(cssQuery, new ElementProcessor() {
            public boolean process(Element e) {
                if (null != e) {
                    Map<String, String> aRecord = new LinkedHashMap<String, String>();
                    aRecord.putAll(constantFieldValues);
                    processValue(aRecord, e, params);
                    records.add(aRecord);
                }
                return true;
            }
        });

        return records;
    }

    /**
     * 从节点中提取内容放到map中
     * 
     * @param result 结果数据
     * @param element 待处理的节点
     * @param params 需要提取的参数配置信息
     * @since 1.0
     */
    protected void processValue(final Map<String, String> result, Element element,
            LinkedHashMap<String, String> params) {
        for (final Entry<String, String> entry : params.entrySet()) {
            element.select(entry.getValue(), new ElementProcessor() {
                public boolean process(Element element) {
                    if (null != element) {
                        FieldSplitter fieldSplitter = fieldSplitters.get(entry.getKey());
                        if (null != fieldSplitter) {
                            // 该字段指定了拆分器，则进行拆分
                            fieldSplitter.process(element.getText(), result);
                        } else {
                            result.put(entry.getKey(), element.getText());
                        }
                    } else {
                        result.put(entry.getKey(), "");
                    }
                    return true;
                }
            });
        }
    }

    /**
     * @return 返回内容的标题
     * @since 1.0
     */
    public final String[] getTitles() {
        return titles;
    }

}
