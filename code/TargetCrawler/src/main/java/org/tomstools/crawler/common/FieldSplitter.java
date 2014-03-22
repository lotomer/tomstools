/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.common;

import java.util.Collections;
import java.util.Map;

/**
 * 字段拆分器
 * @author admin
 * @date 2014年3月16日 
 * @time 下午6:30:38
 * @version 1.0
 */
public class FieldSplitter {
    private String separator; // 拆分时的分隔符
    private int fieldSize;  // 拆分的字段个数
    private int[] fieldIndexes; // 需要收集的字段的索引数组。从0开始计数。索引必须小于{@link #fieldSize}
    private String[] fieldNames; // 需要收集的字段的名称数组。索引数组和名称数组必须匹配
    private Map<String,ValueConvertible<String, String>> valueConverters; // 索引对应的字段值转换器
    /**
     * @param separator 拆分时的分隔符
     * @param fieldSize 拆分的字段个数
     * @param fieldIndexes 需要收集的字段的索引数组。从0开始计数。索引必须小于{@link #fieldSize}
     * @param fieldNames 需要收集的字段的名称数组。索引数组和名称数组必须匹配
     * @param valueConverters 字段名对应的字段值转换器
     * @since 1.0
     */
    public FieldSplitter(String separator, int fieldSize, int[] fieldIndexes, String[] fieldNames,Map<String, ValueConvertible<String, String>> valueConverters) {
        super();
        if (null == separator || fieldSize < 2 || null == fieldIndexes || null == fieldNames || fieldIndexes.length != fieldNames.length){
            throw new RuntimeException("arguments is invalid!");
        }
        this.separator = separator;
        this.fieldSize = fieldSize;
        this.fieldIndexes = fieldIndexes;
        this.fieldNames = fieldNames;
        // 对索引进行校验
        for (int i = 0; i < fieldIndexes.length; i++) {
            if (fieldIndexes[i] >= fieldSize){
                throw new ArrayIndexOutOfBoundsException("Array index out of range: " + fieldIndexes[i] + ", array size is " + fieldSize);
            }
        }
        
        if (null != valueConverters){
            this.valueConverters = valueConverters;
        }else{
            this.valueConverters = Collections.emptyMap();
        }
    }

    /**
     * 执行字段拆分，并将结果保存到指定的map中
     * @param text      待拆分字段
     * @param result    结果保存容器
     * @since 1.0
     */
    public void process(String text, Map<String, String> result) {
        if (Utils.isEmpty(text) || null == result){
            return;
        }
        
        String[] values = text.split(this.separator,this.fieldSize);
        // 将拆分的字段保存
        for (int i = 0; i < fieldIndexes.length; i++) {
            // 需要校验索引是否超出拆分后的数值大小
            int index = fieldIndexes[i];
            if (index < values.length){
                if (null != valueConverters.get(this.fieldNames[i])){
                    // 需要转换值
                    result.put(this.fieldNames[i], valueConverters.get(this.fieldNames[i]).valueOf(values[index]));
                }else{
                    result.put(this.fieldNames[i], values[index]);
                }
            }
        }
    }
}
