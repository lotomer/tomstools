/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.extractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.tomstools.common.Logger;
import org.tomstools.common.Utils;
import org.tomstools.crawler.common.Element;
import org.tomstools.crawler.common.ElementProcessor;
import org.tomstools.crawler.common.FieldSplitter;
import org.tomstools.crawler.config.Target;
import org.tomstools.crawler.parser.HTMLParser;
import org.tomstools.crawler.spring.ApplicationContext;

/**
 * 内容抽取器
 * 
 * @author admin
 * @date 2014年3月13日
 * @time 下午5:58:11
 * @version 1.0
 */
public class ContentExtractor {
    private final static Logger LOGGER = Logger.getLogger(ContentExtractor.class);
    private String contentSelector;
    private List<Field> fields;
    private List<Field> constantField;
    private String[] titles;
    private ContentExtractor contentExtractor;

    /**
     * 构造函数
     * 
     * @param contentSelector 正文所在元素的选择表达式
     * @param titles 表头信息
     * @param constantField 常量字段
     * @param fields 正文字段
     * @since 1.0
     */
    public ContentExtractor(String contentSelector, String[] titles, List<Field> constantField,
            List<Field> fields) {
        this.contentSelector = contentSelector;
        this.titles = titles;
        this.fields = fields;
        this.constantField = constantField;
    }

    /**
     * 从节点中提取内容
     * 
     * @param element 待处理的节点
     * @return 提取出来的内容
     * @since 1.0
     */
    public List<Map<String, String>> extractContent(final Element element) {
        return extractContent(element, null);
    }

    List<Map<String, String>> extractContent(final Element element,
            final Map<String, String> constantFieldValues) {
        if (null == element) {
            return Collections.emptyList();
        }
        final List<Map<String, String>> records = new ArrayList<Map<String, String>>();
        final Map<String, String> tmpConstantFieldValues = new LinkedHashMap<String, String>();
        if (null != constantFieldValues) {
            tmpConstantFieldValues.putAll(constantFieldValues);
        }
        // 获取常量字段
        if (null != constantField) {
            for (Field field : constantField) {
                if (field instanceof MultipleField) {
                    field.processData(null, tmpConstantFieldValues);
                }else{
                    field.processData(element.select(field.selector), tmpConstantFieldValues);
                }
                
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(element + ",cssQuery:" + contentSelector);
        }

        // 处理正文
        element.select(contentSelector, new ElementProcessor() {
            public boolean process(Element e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(e);
                }
                if (null != e) {
                    // 如果包含子内容抽取器，则使用子内容抽取器进行抽取
                    if (null != contentExtractor) {
                        records.addAll(contentExtractor.extractContent(e, tmpConstantFieldValues));
                    } else {
                        // 直接自己抽取
                        Map<String, String> aRecord = new LinkedHashMap<String, String>();
                        aRecord.putAll(tmpConstantFieldValues);
                        processValue(aRecord, e, fields);
                        if (!aRecord.isEmpty()) {
                            records.add(aRecord);
                        }
                    }
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
        if (null != element) {
            for (final Field field : fields) {
                if (field instanceof MultipleField) {
                    field.processData(null, result);
                }else{
                    element.select(field.selector, new ElementProcessor() {
                        public boolean process(Element element) {
                            if (null != element) {
                                field.processData(element, result);
                            } else {
                                result.put(field.name, "");
                            }
                            return true;
                        }
                    });
                }
            }
        }
    }

    /**
     * @return 返回内容的标题
     * @since 1.0
     */
    public final String[] getTitles() {
        if (null != contentExtractor) {
            return contentExtractor.titles;
        } else {
            return titles;
        }
    }

    /**
     * @param contentExtractor 设置子内容抽取器
     * @since 1.0
     */
    public final void setContentExtractor(ContentExtractor contentExtractor) {
        this.contentExtractor = contentExtractor;
    }

    /**
     * 字段对象
     * 
     * @author admin
     * @date 2014年4月28日
     * @time 上午11:23:10
     * @version 1.0
     */
    public static abstract class Field {
        protected final String name;
        protected final String selector;
        private final int valueType;
        private final FieldSplitter fieldSplitter;
        /** 字段值类型：源码 */
        public static int VALUE_TYPE_CODE = 0;
        /** 字段值类型：正文文本，不包含子节点的文本内容 */
        public static int VALUE_TYPE_OWN_TEXT = 1;
        /** 字段值类型：正文文本，包含子节点的文本内容 */
        public static int VALUE_TYPE_TEXT = 2;
        /** 字段值类型：解码过的正文文本，包含子节点的文本内容 */
        public static int VALUE_TYPE_TEXT_UNESCAPED = 3;
        /** 字段值类型：解码过的正文文本，不包含子节点的文本内容 */
        public static int VALUE_TYPE_OWN_TEXT_UNESCAPED = 4;
        /** 字段值类型：标签属性值 */
        public static int VALUE_TYPE_ATTRIBUTE = 5;
        /** 字段值类型：组合字段 */
        public static int VALUE_TYPE_MULTIPLE = 6;

        /**
         * 构造函数
         * 
         * @param name 字段名
         * @param selector 字段选择器
         * @param valueType 字段值类别。源码{@link Field#VALUE_TYPE_CODE}或正文文本
         *            {@link Field#VALUE_TYPE_CODE}。
         * @param fieldSplitter 字段拆分器
         * @since 1.0
         */
        public Field(String name, String selector, int valueType, FieldSplitter fieldSplitter) {
            super();
            this.name = name;
            this.selector = selector;
            this.valueType = valueType;
            this.fieldSplitter = fieldSplitter;
        }

        public void processData(Element element, Map<String, String> fieldValueCollector) {
            if (null != element){
                if (null != fieldSplitter) {
                    // 该字段指定了拆分器，则进行拆分
                    fieldSplitter.process(getText(element), fieldValueCollector);
                } else {
                    fieldValueCollector.put(name, getText(element));
                }
            }
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

        /**
         * 从节点中获取文本内容
         * 
         * @param e 节点
         * @return 文本内容
         * @since 1.0
         */
        protected abstract String getText(Element e);
    }

    /**
     * 源码字段，字段内容取源码
     * 
     * @author admin
     * @date 2014年4月28日
     * @time 上午11:25:25
     * @version 1.0
     */
    public static class CodeField extends Field {
        public CodeField(String name, String selector) {
            super(name, selector, VALUE_TYPE_CODE, null);
        }

        public CodeField(String name, String selector, FieldSplitter fieldSplitter) {
            super(name, selector, VALUE_TYPE_CODE, fieldSplitter);
        }

        @Override
        protected String getText(Element e) {
            return e.getCode();
        }
    }

    /**
     * 正文文本字段，字段内容取正文文本。包含子节点的文本内容
     * 
     * @author admin
     * @date 2014年4月28日
     * @time 上午11:29:20
     * @version 1.0
     */
    public static class TextField extends Field {
        public TextField(String name, String selector) {
            super(name, selector, VALUE_TYPE_TEXT, null);
        }

        public TextField(String name, String selector, FieldSplitter fieldSplitter) {
            super(name, selector, VALUE_TYPE_TEXT, fieldSplitter);
        }

        @Override
        protected String getText(Element e) {
            return e.getText();
        }
    }

    /**
     * 正文文本字段，字段内容取正文文本。包含子节点的文本内容
     * 
     * @author admin
     * @date 2014年4月28日
     * @time 上午11:29:20
     * @version 1.0
     */
    public static class UnescapedTextField extends Field {
        public UnescapedTextField(String name, String selector) {
            super(name, selector, VALUE_TYPE_TEXT_UNESCAPED, null);
        }

        public UnescapedTextField(String name, String selector, FieldSplitter fieldSplitter) {
            super(name, selector, VALUE_TYPE_TEXT_UNESCAPED, fieldSplitter);
        }

        @Override
        protected String getText(Element e) {
            return e.getUnescapedText();
        }
    }

    /**
     * 正文文本字段，字段内容取正文文本。不包含子节点的文本内容
     * 
     * @author admin
     * @date 2014年4月28日
     * @time 上午11:29:20
     * @version 1.0
     */
    public static class OwnTextField extends Field {
        public OwnTextField(String name, String selector) {
            super(name, selector, VALUE_TYPE_OWN_TEXT, null);
        }

        public OwnTextField(String name, String selector, FieldSplitter fieldSplitter) {
            super(name, selector, VALUE_TYPE_OWN_TEXT, fieldSplitter);
        }

        @Override
        protected String getText(Element e) {
            return e.getOwnText();
        }
    }

    /**
     * 正文文本字段，字段内容取解码后的正文文本。不包含子节点的文本内容
     * 
     * @author admin
     * @date 2014年4月28日
     * @time 上午11:29:20
     * @version 1.0
     */
    public static class UnescapedOwnTextField extends Field {
        public UnescapedOwnTextField(String name, String selector) {
            super(name, selector, VALUE_TYPE_OWN_TEXT_UNESCAPED, null);
        }

        public UnescapedOwnTextField(String name, String selector, FieldSplitter fieldSplitter) {
            super(name, selector, VALUE_TYPE_OWN_TEXT_UNESCAPED, fieldSplitter);
        }

        @Override
        protected String getText(Element e) {
            return e.getUnescapedOwnText();
        }
    }

    /**
     * 多字段组合
     * 
     * @author admin
     * @date 2014年4月28日
     * @time 上午11:29:20
     * @version 1.0
     */
    public static class MultipleField extends Field {
        public MultipleField(String name, String selector) {
            super(name, selector, VALUE_TYPE_MULTIPLE, null);
        }

        public MultipleField(String name, String selector, FieldSplitter fieldSplitter) {
            super(name, selector, VALUE_TYPE_MULTIPLE, fieldSplitter);
        }

        /*
         * @since 1.0
         */
        @Override
        public void processData(Element element, Map<String, String> fieldValueCollector) {
            if (!Utils.isEmpty(selector)){
                String[] vs = selector.split(",",2);
                if (0 < vs.length){
                    String[] fields = vs[0].split("\\|");
                    if (2 == vs.length){
                        //包含内容输出模式
                        String mode = vs[1];
                        // 组个变量替换
                        String fieldValue;
                        for (int i = 0; i < fields.length; i++) {
                            fieldValue = fieldValueCollector.get(fields[i]);
                            if (null != fieldValue){
                                //LOGGER.info("name:" +name+",selector:" +selector+",field:" + fields[i] + ",value:" + fieldValue);
                                mode = mode.replaceAll("\\$\\{" + fields[i] + "\\}", fieldValue);
                            }
                        }
                        fieldValueCollector.put(name, mode);
                    }else{
                        // 不包含内容输出模式则依次输出内容
                        StringBuilder msg = new StringBuilder();
                        for (int i = 0; i < fields.length; i++) {
                            msg.append(fields[i]);
                        }
                        fieldValueCollector.put(name, msg.toString());
                    }
                }
            }
        }

        @Override
        protected String getText(Element e) {
            return e.getUnescapedOwnText();
        }
    }

    /**
     * 标签属性字段
     * 
     * @author admin
     * @date 2014年6月23日
     * @time 上午11:34:20
     * @version 1.0
     */
    public static class AttributeField extends Field {
        private String attributeName;

        public AttributeField(String name, String selector) {
            super(name, selector, VALUE_TYPE_ATTRIBUTE, null);
        }

        public AttributeField(String name, String selector, FieldSplitter fieldSplitter) {
            super(name, selector, VALUE_TYPE_ATTRIBUTE, fieldSplitter);
        }

        /**
         * @param attributeName 设置属性名
         * @since 1.0
         */
        public final void setAttributeName(String attributeName) {
            this.attributeName = attributeName;
        }

        @Override
        protected String getText(Element e) {
            return e.getAttribute(attributeName);
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(
                "C:\\Users\\admin\\Desktop\\aaaa.html")));
        StringBuilder msg = new StringBuilder();
        String line = null;
        while (null != (line = reader.readLine())) {
            msg.append(line);
            msg.append("\r\n");
        }
        reader.close();
        reader = null;
        Target target = (Target) ApplicationContext.getInstance().getBean(
                "target-landchina-gonggaojieguo-hunan-xycr");
        ContentExtractor ex = target.getContentExtractor();
        HTMLParser p = new HTMLParser();
        Element body = p.parse(msg.toString(), null);
        List<Map<String, String>> aa = ex.extractContent(body);
        System.out.println(aa);
    }
}
