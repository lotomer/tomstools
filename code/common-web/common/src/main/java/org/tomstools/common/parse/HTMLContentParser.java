/**
 * 
 */
package org.tomstools.common.parse;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * HTML内容解析器
 * @author lotomer
 * @date 2015年7月30日
 * @version 1.0
 */
public class HTMLContentParser implements ContentParser {
    private final static Log LOG = LogFactory.getLog(HTMLContentParser.class);
    private Document doc;

    public void init(String content) {
        this.doc = Jsoup.parse(content);
    }

    public Object parse(String selector, String attributeName) {
        Elements es = doc.select(selector);
        Object ret = null;
        if (1 == es.size()) {
            // 没有指定，则去text
            ret = getText(es.get(0),attributeName);
        } else if (1 < es.size()) {
            List<String> values = new ArrayList<String>();
            for (int i = 0; i < es.size(); i++) {
                Element e = es.get(i);
                // 没有指定，则去text
                values.add(getText(e, attributeName));
            }
            ret = values;
        } else {
            LOG.warn("Not match element by selector: " + selector);
        }
        return ret;
    }

    private String getText(Element element, String attributeName) {
        if (attributeName == null || "".equals(attributeName)) {
            return element.text();
        } else {
            return element.attr(attributeName.toString());
        }
    }

}
