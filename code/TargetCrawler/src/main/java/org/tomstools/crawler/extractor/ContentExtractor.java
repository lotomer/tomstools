/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.extractor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    //private LinkedHashMap<String, String> params;
    private String cssQuery;
    private List<Field> fields;
    private List<Field> constantField;
//    private Map<String, FieldSplitter> fieldSplitters;
    private String[] titles;
//    private LinkedHashMap<String, String> constantFieldSelectors;
    /**
     * 构造函数
     * @param cssQuery  正文所在元素的选择表达式
     * @param titles 表头信息
     * @param constantField 常量字段
     * @param fields 正文字段
     * @since 1.0
     */
    public ContentExtractor(String cssQuery,String[] titles,List<Field> constantField,List<Field> fields){
        this.cssQuery = cssQuery;
        this.titles = titles;
        this.fields = fields;
        this.constantField = constantField;
    }
    /**
     * 构造函数
     * 
     * @param cssQuery 正文所在元素的选择表达式
     * @param titles 表头信息
     * @param constantFieldSelectors 固定字段选择器，不随每次属性提取而变化
     * @param params 提取固定属性配置信息
     * @param fieldSplitters 字段拆分器
     * @since 1.0
     */
//    public ContentExtractor(String cssQuery, String[] titles,
//            LinkedHashMap<String, String> constantFieldSelectors,
//            LinkedHashMap<String, String> params, Map<String, FieldSplitter> fieldSplitters) {
//        this.params = params;
//        this.cssQuery = cssQuery;
//        this.titles = titles;
//        this.constantFieldSelectors = constantFieldSelectors;
//        if (null != fieldSplitters) {
//            this.fieldSplitters = fieldSplitters;
//        } else {
//            this.fieldSplitters = Collections.emptyMap();
//        }
//    }

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
        // 获取常量字段
        if (null != constantField) {
            for (Field field : constantField) {
                FieldSplitter fieldSplitter = field.fieldSplitter;
                if (null != fieldSplitter) {
                    // 该字段指定了拆分器，则进行拆分
                    if (field.valueType == Field.VALUE_TYPE_TEXT){
                        fieldSplitter.process(element.select(field.selector).getText(), constantFieldValues);
                    }else{
                        fieldSplitter.process(element.select(field.selector).getCode(), constantFieldValues);
                    }
                } else {
                    if (field.valueType == Field.VALUE_TYPE_TEXT){
                        constantFieldValues.put(field.name, element.select(field.selector).getText());
                    }else{
                        constantFieldValues.put(field.name, element.select(field.selector).getCode());
                    }
                }
            }
        }
        element.select(cssQuery, new ElementProcessor() {
            public boolean process(Element e) {
                if (null != e) {
                    Map<String, String> aRecord = new LinkedHashMap<String, String>();
                    aRecord.putAll(constantFieldValues);
                    processValue(aRecord, e, fields);
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
            List<Field> fields) {
        for (final Field entry : fields) {
            element.select(entry.selector, new ElementProcessor() {
                public boolean process(Element element) {
                    if (null != element) {
                        FieldSplitter fieldSplitter = entry.fieldSplitter;
                        if (null != fieldSplitter) {
                            // 该字段指定了拆分器，则进行拆分
                            fieldSplitter.process(element.getText(), result);
                        } else {
                            result.put(entry.name, element.getText());
                        }
                    } else {
                        result.put(entry.name, "");
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

    /**
     * 字段对象
     * @author admin
     * @date 2014年4月28日 
     * @time 上午11:23:10
     * @version 1.0
     */
    public static class Field {
        private final String name;
        private final String selector;
        private final int valueType;
        private final FieldSplitter fieldSplitter;
        /** 字段值类型：源码*/
        public static int VALUE_TYPE_CODE = 0;
        /** 字段值类型：正文文本*/
        public static int VALUE_TYPE_TEXT = 1;
        /**
         * 构造函数
         * @param name      字段名
         * @param selector  字段选择器
         * @param valueType 字段值类别。源码{@link Field#VALUE_TYPE_CODE}或正文文本{@link Field#VALUE_TYPE_CODE}。
         * @param fieldSplitter 字段拆分器
         * @since 1.0
         */
        public Field(String name, String selector, int valueType,FieldSplitter fieldSplitter) {
            super();
            this.name = name;
            this.selector = selector;
            this.valueType = valueType;
            this.fieldSplitter = fieldSplitter;
        }
        /**
         * @return 返回 name
         * @since 1.0
         */
        public final String getName() {
            return name;
        }
        /**
         * @return 返回 selector
         * @since 1.0
         */
        public final String getSelector() {
            return selector;
        }
        /**
         * @return 返回 valueType
         * @since 1.0
         */
        public final int getValueType() {
            return valueType;
        }
        /**
         * @return 返回 fieldSplitter
         * @since 1.0
         */
        public final FieldSplitter getFieldSplitter() {
            return fieldSplitter;
        }
    }
    /**
     * 源码字段，字段内容取源码
     * 
     * @author admin
     * @date 2014年4月28日 
     * @time 上午11:25:25
     * @version 1.0
     */
    public static class CodeField extends Field{
        public CodeField(String name, String selector) {
            super(name, selector, VALUE_TYPE_CODE,null);
        }
        public CodeField(String name, String selector,FieldSplitter fieldSplitter) {
            super(name, selector, VALUE_TYPE_CODE,fieldSplitter);
        }
    }
    /**
     * 正文文本字段，字段内容取正文文本
     * 
     * @author admin
     * @date 2014年4月28日 
     * @time 上午11:29:20
     * @version 1.0
     */
    public static class TextField extends Field{
        public TextField(String name, String selector) {
            super(name, selector, VALUE_TYPE_TEXT,null);
        }
        public TextField(String name, String selector,FieldSplitter fieldSplitter) {
            super(name, selector, VALUE_TYPE_TEXT,fieldSplitter);
        }
    }
}
