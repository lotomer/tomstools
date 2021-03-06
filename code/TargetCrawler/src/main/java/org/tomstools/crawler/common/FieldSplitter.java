/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.common;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tomstools.common.Logger;
import org.tomstools.common.Utils;

/**
 * 字段拆分器
 * @author admin
 * @date 2014年3月16日 
 * @time 下午6:30:38
 * @version 1.0
 */
public class FieldSplitter {
    private static final Logger LOG = Logger.getLogger(FieldSplitter.class);
    private Pattern pattern; // 提取字段的正则表达式
    private int[] fieldIndexes; // 需要收集的字段的索引数组。从1开始计数。索引不能大于正则表达式regex中的分组数（即圆括号数）
    private String[] fieldNames; // 需要收集的字段的名称数组。索引数组和名称数组必须匹配
    private Map<String,ValueConvertible<String, String>> valueConverters; // 索引对应的字段值转换器
    /**
     * @param regex 提取内容是的正则表达式
     * @param fieldIndexes 需要收集的字段的索引数组。从1开始计数。索引不能大于正则表达式regex中的分组数（即圆括号数）
     * @param fieldNames 需要收集的字段的名称数组。索引数组和名称数组必须匹配
     * @param valueConverters 字段名对应的字段值转换器
     * @since 1.0
     */
    public FieldSplitter(String regex, int[] fieldIndexes, String[] fieldNames,Map<String, ValueConvertible<String, String>> valueConverters) {
        super();
        if (null == regex || null == fieldIndexes || null == fieldNames || fieldIndexes.length != fieldNames.length){
            throw new RuntimeException("arguments is invalid!");
        }
        
        pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.CANON_EQ);
        this.fieldIndexes = fieldIndexes;
        this.fieldNames = fieldNames;
        // 对索引进行校验
//        for (int i = 0; i < fieldIndexes.length; i++) {
//            if (fieldIndexes[i] >= fieldSize){
//                throw new ArrayIndexOutOfBoundsException("Array index out of range: " + fieldIndexes[i] + ", array size is " + fieldSize);
//            }
//        }
        
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
        
        Matcher m = pattern.matcher(text);
        if(m.find()){
         // 将拆分的字段保存
            for (int i = 0; i < fieldIndexes.length; i++) {
                // 需要校验索引是否超出拆分后的数值大小
                int index = fieldIndexes[i];
                if (m.groupCount() < index){
                    LOG.warn(text + " index is out of range! Real group count: " + m.groupCount() + ", index: " + index);
                }else{
                    if (null != valueConverters.get(this.fieldNames[i])){
                        // 需要转换值
                        result.put(this.fieldNames[i], valueConverters.get(this.fieldNames[i]).valueOf(m.group(index)));
                    }else{
                        result.put(this.fieldNames[i], m.group(index));
                    }
                }
            }
        }
    }
    
    public static void main(String[] args) {
        assertEqualBuilding("满庭芳-NONO国际公寓-10栋","满庭芳-NONO国际公寓","10栋");
        assertEqualBuilding("金科东方大院-1-1栋","金科东方大院","1-1栋");
        assertEqualBuilding("金科东方大院-3-2#-A栋","金科东方大院","3-2#-A栋");
        assertEqualBuilding("金科东方大院-二期2-23栋","金科东方大院","二期2-23栋");
        assertEqualBuilding("金科东方大院-4-1栋-B","金科东方大院","4-1栋-B");
        System.out.println("========================================");
        assertEqualFloor("·4-1栋-B1层（5户）","4-1栋-B","1");
        assertEqualFloor("·1-18栋1层（4户）","1-18栋","1");
        assertEqualFloor("·二期16栋1层（1户）","二期16栋","1");
        assertEqualFloor("·1栋-1层（20户）","1栋","-1");
        assertEqualFloor("·1、2栋及地下室-0.9层（3户）","1、2栋及地下室","-0.9");
        assertEqualFloor("·16栋-1-2层（1户）","16栋","-1-2");
        assertEqualFloor("·1栋1层（15户）","1栋","1");
        assertEqualFloor("·1栋17B层（8户）","1栋","17B");
        assertEqualFloor("·　栋1层（18户）","　栋","1");
        assertEqualFloor("·B13栋JKC层（19户）","B13栋","JKC");
        assertEqualFloor("·商业中心栋夹层层（12户）","商业中心栋","夹层");
    }

    private static void assertEqualBuilding(String s, String s1, String s2) {
        Pattern p = Pattern.compile("(.*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher m = p.matcher(s);
        if (m.find()){
            //System.out.println(s2 + "=" + m.group(2) + ": " + s2.equals(m.group(2)));
            
            String buildings_name = m.group(1);
            String building_name = s2;
            if (buildings_name.endsWith("-" + building_name)){
                buildings_name = new String(buildings_name.substring(0, buildings_name.length() - building_name.length() - "-".length()));
            }
            System.out.println(s1 + "=" + buildings_name + ": " + s1.equals(buildings_name));
            //System.out.println(buildings_name +"-----"+ building_name);
        }else{
            System.out.println("false==" + s);
        }
    }
    private static void assertEqualFloor(String s, String s1, String s2) {
        Pattern p = Pattern.compile("·(.*?栋.*?(-[^\\d]+|[^-\\d]*?))(-\\d+.*|\\d+.*|[^-]*)层.*$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher m = p.matcher(s);
        if (m.find()){
            System.out.println(s1 + "=" + m.group(1) + ": " + s1.equals(m.group(1)) + "  |  " +s2 + "=" + m.group(3) + ": " + s2.equals(m.group(3)));
        }else{
            System.out.println("false=" + s);
        }
        
    }
}
