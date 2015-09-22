/**
 * 
 */
package org.tomstools.common.parse;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.tomstools.common.PropertyParseAssistor;

/**
 * 模板解析器
 * 
 * @author lotomer
 * @date 2015年7月24日
 * @version 1.0
 */
public class TemplateParser {
    private static final Log LOG = LogFactory.getLog(TemplateParser.class);

    private TemplateParser() {
    }

    /**
     * 解析模板中的变量，用map中的变量值替换，返回替换后的结果<br>
     * 没有替换的变量显示原模板中的内容。<br>
     * 转义字符为“\”
     * @param variables 变量键值对列表，可为null。
     * @param template  模板。可为null
     * @return 替换后的内容。template为null时返回结果为null
     */
    public static String parse(Map<String, ?> variables, String template) {
        return parse(variables, template, true);
    }
    /**
     * 解析模板中的变量，用map中的变量值替换，返回替换后的结果<br>
     * 没有替换的变量显示原模板中的内容。<br>
     * 转义字符为“\”
     * @param variables 变量键值对列表，可为null。
     * @param template  模板。可为null
     * @param keepVariable 如果变量没有匹配到值，是否保留该变量。true 保留该变量；false 用空字符串替换该变量
     * @return 替换后的内容。template为null时返回结果为null
     */
    public static String parse(Map<String, ?> variables, String template,boolean keepVariable) {
        LOG.debug("Start parse template.");
        if (!StringUtils.isEmpty(template)) {
            StringBuilder content = new StringBuilder();
            char[] cs = template.toCharArray();
            boolean prepared = false;
            boolean isStart = false;
            boolean needEscape = false;
            int startIndex = -1;
            int endIndex = -1;
            for (int i = 0; i < cs.length; i++) {
                if (needEscape) {
                    if (prepared) {
                        content.append('$');
                    }
                    if (isStart) {
                        if (-1 < startIndex) {
                            String tmp = new String(cs, startIndex - 1, i - startIndex);
                            content.append(tmp);
                        }
                    }
                    content.append(cs[i]);
                    needEscape = false;
                    prepared = false;
                    isStart = false;
                } else if ('\\' == cs[i]) {
                    // 转义字符
                    needEscape = true;
                    // prepared = false;
                    // isStart = false;
                    // startIndex = -1;
                    // endIndex = -1;
                } else if ('$' == cs[i]) {
                    // 可能是变量
                    if (prepared) {
                        // 已经准备好，则重新准备，将前面的字符直接做常量
                        int j = isStart ? 2 : 1;
                        if (-1 < startIndex) {
                            String tmp = new String(cs, startIndex - j, i - startIndex + j);
                            content.append(tmp);
                        }
                    }
                    prepared = true;
                    isStart = false;
                    startIndex = -1;
                    endIndex = -1;
                } else if ('{' == cs[i]) {
                    // 如果已经准备好，则表示是开头
                    if (prepared) {
                        isStart = true;
                        startIndex = i + 1;
                        endIndex = -1;
                    } else {
                        content.append(cs[i]);
                    }
                } else if ('}' == cs[i]) {
                    if (isStart) {
                        endIndex = i;
                        isStart = false;
                        prepared = false;
                        // 获取变量
                        if (startIndex < endIndex) {
                            String vName = new String(cs, startIndex, endIndex - startIndex);
                            String names[] = vName.split("\\.");
                            Object value = variables.get(names[0]);
                            if (null != value) {
                                String v = PropertyParseAssistor.getString(PropertyParseAssistor.getProperty(value,Arrays.copyOfRange(names, 1, names.length)));
                                if (null != v) {
                                    content.append(v.replaceAll("'", "\\\\'"));
                                    continue;
                                }
                            }
                            // 没有被替换，则使用原变量模板的内容
                            if (keepVariable){
                                content.append("${").append(vName).append("}");
                            }
                        } else {
                            // content.append(cs[i]);
                        }
                    } else {
                        content.append(cs[i]);
                    }
                } else {
                    if (!isStart) {
                        // 不是变量，则直接当常量
                        if (prepared) {
                            content.append('$');
                            prepared = false;
                        }
                        content.append(cs[i]);
                    } else {
                        // 变量
                    }
                }
            }
            return content.toString();
        }

        return null;
    }
}
