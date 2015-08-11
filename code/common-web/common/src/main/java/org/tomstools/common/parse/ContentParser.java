/**
 * 
 */
package org.tomstools.common.parse;

/**
 * @author lotomer
 * @date 2015年7月30日
 * @version 1.0
 */
public interface ContentParser {
    /**
     * 初始化内容
     * @param content 正文内容
     */
    void init(String content);

    /**
     * 根据选择表达式选取内容
     * @param selector  选择表达式
     * @param attributeName 属性名。不为空表示选取对应属性
     * @return 结果数据。可能是字符串，也可能是字符串列表。可为null
     */
    Object parse(String selector, String attributeName);

}
