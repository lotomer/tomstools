/**
 * 
 */
package org.tomstools.common;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;

/**
 * 属性解析助手
 * 
 * @author lotomer
 * @date 2015年7月30日
 * @version 1.0
 */
public final class PropertyParseAssistor {
    private static final Log LOG = LogFactory.getLog(PropertyParseAssistor.class);
    private static final String SEPARATOR_DEFAULT = ".";

    /**
     * 获取对象的属性值。<br>
     * <code>假如：<br>
     * a.b.c = "hello";<br>
     * 则：<br>
     * value = ParseUtil.getProperty(a, "b.c",".");<br>
     * value的值为“hello”
     * </code>
     * 
     * @param obj
     *            对象
     * @param propertyName
     *            属性名。多级属性名之间用“.”连接，如“b.c”
     * @return 属性值。没有匹配上则返回null
     */
    public static Object getProperty(Object obj, String propertyName) {
        return getProperty(obj, propertyName, SEPARATOR_DEFAULT);
    }

    /**
     * 获取对象的属性值。<br>
     * <code>假如：<br>
     * a.b.c = "hello";<br>
     * 则：<br>
     * value = ParseUtil.getProperty(a, "b.c",".");<br>
     * value的值为“hello”
     * </code>
     * 
     * @param obj
     *            对象
     * @param propertyName
     *            属性名。多级属性名之间用separator指定的分隔符连接，如“b.c”
     * @param separator
     *            多级属性之间的分隔符
     * @return 属性值。没有匹配上则返回null
     */
    public static Object getProperty(Object obj, String propertyName, String separator) {
        if (null != obj && !StringUtils.isEmpty(propertyName)) {
            if (null == separator) {
                separator = SEPARATOR_DEFAULT;
            }

            String vNames[] = propertyName.split(Pattern.quote(separator));
            return getProperty(obj, vNames);
        }

        return null;
    }

    /**
     * 获取对象的属性值。<br>
     * <code>假如：<br>
     * a.b.c = "hello";<br>
     * 则：<br>
     * value = ParseUtil.getProperty(a, new String[]{"b","c"});<br>
     * value的值为“hello”
     * </code>
     * 
     * @param obj
     *            对象
     * @param subPropertyName
     *            属性名列表，具有层级关系。
     * @return 属性值。没有匹配上则返回null
     */
    public static Object getProperty(Object obj, String[] subPropertyName) {
        // 包含子属性
        for (int index = 0; index < subPropertyName.length; index++) {
            if (null != obj) {
                String propertyName = subPropertyName[index];
                // 如果是列表，则用索引号转化为map
                if (obj instanceof List<?>) {
                    List<?> list = (List<?>) obj;
                    Map<String, Object> map = new HashMap<String, Object>();
                    for (int i = 0; i < list.size(); i++) {
                        map.put(String.valueOf(i), list.get(i));
                    }
                    obj = map;
                }
                // 判断是不是Map，如果是map，则直接取值，不是map，则利用反射机制获取属性
                if (obj instanceof Map<?, ?>) {
                    Map<?, ?> v = (Map<?, ?>) obj;
                    obj = v.get(propertyName);
                } else if (obj instanceof String || obj instanceof Integer || obj instanceof Float) {
                    if (index + 1 <= subPropertyName.length) {
                        // 表示还有子属性
                        return null;
                    }
                } else {
                    // 从对象中提取子属性对应的值
                    Field fields[] = obj.getClass().getDeclaredFields();// 获得对象所有属性
                    Field field = null;
                    Object v = null;
                    for (int y = 0; y < fields.length; y++) {
                        field = fields[y];
                        field.setAccessible(true);// 修改访问权限
                        if (propertyName.equals(field.getName())) {
                            try {
                                v = field.get(obj);
                            } catch (IllegalArgumentException e) {
                                LOG.error(e.getMessage(), e);
                            } catch (IllegalAccessException e) {
                                LOG.error(e.getMessage(), e);
                            }
                            break;
                        }
                    }
                    obj = v;
                }
            } else {
                break;
            }
        }

        return obj;
    }

    public static String getString(Object value) {
        if (null == value) {
            return null;
        } else if (value instanceof String || value instanceof Integer || value instanceof Float) {
            return String.valueOf(value);
        }
        return JSON.toJSONString(value);
    }
}
