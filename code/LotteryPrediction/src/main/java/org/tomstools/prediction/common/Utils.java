/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.prediction.common;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用工具类
 * 
 * @author lotomer
 * @date 2011-12-14
 * @time 下午02:42:29
 */
public final class Utils {
    private Utils() {
    }

    /**
     * 判断对象是否为空
     * 
     * @param data 待判断的对象
     * @return true 为null或仅包含空格的字符串； false 非空
     */
    public static boolean isEmpty(Object data) {
        return (null == data || "".equals(data.toString().trim()));
    }

    /**
     * 判断结果集是否为空
     * 
     * @param <E> 泛型
     * @param data 结果集
     * @return true 结果集为null或空； false 非空
     */
    public static <E> boolean isEmpty(Collection<E> data) {
        return (null == data || 0 == data.size());
    }

    /**
     * 关闭流
     * 
     * @param c
     */
    public static void close(Closeable c) {
        if (null != c) {
            try {
                c.close();
                c = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据正则表达式获取内容
     * 
     * @param regexp 正则表达式
     * @param content 原始内容
     * @param groupIndex 需要获取的正则表达式group的索引号对应的值
     * @return group对应的值
     * @since 1.0
     */
    public static List<String> getRegexValue(String regexp, String content, int groupIndex) {
        List<String> values = new ArrayList<String>();
        Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
                | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            values.add(matcher.group(groupIndex));
        }

        if (values.isEmpty()) {
            values.add(content);
        }

        return values;
    }
}
