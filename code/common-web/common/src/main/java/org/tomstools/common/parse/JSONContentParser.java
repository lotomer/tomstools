/**
 * 
 */
package org.tomstools.common.parse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tomstools.common.PropertyParseAssistor;

import com.alibaba.fastjson.JSON;

/**
 * JSON内容解析器
 * 
 * @author lotomer
 * @date 2015年7月30日
 * @version 1.0
 */
public class JSONContentParser implements ContentParser {
    // private final static Log LOG =
    // LogFactory.getLog(JSONContentParser.class);
    private Object obj;

    public void init(String content) {
        this.obj = JSON.parse(content);
        if (obj instanceof List<?>) {
            Map<String, Object> value = new HashMap<String, Object>();
            List<?> vs = (List<?>) obj;
            for (int i = 0; i < vs.size(); i++) {
                value.put(String.valueOf(i), vs.get(i));
            }
            this.obj = value;
        }
    }

    public Object parse(String selector, String attributeName) {
        return PropertyParseAssistor.getProperty(obj, selector);
    }
}
