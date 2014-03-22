/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.tomstools.common.util.Utils;
import org.tomstools.crawler.common.ElementProcessor;

/**
 * html文档解析器
 * 
 * @author admin
 * @date 2014年3月12日
 * @time 下午8:46:13
 * @version 1.0
 */
public class HTMLParser implements Parser {

    public org.tomstools.crawler.common.Element parse(String content, String param) {
        Document doc = Jsoup.parse(content);
        if (Utils.isEmpty(param)) {
            return new HTMLElement(doc);
        } else {
            return new HTMLElement(doc.select(param).first());
        }
    }

    // public void parse(String content, Map<String, String> params,
    // ContentHandle handle) {
    // Document doc = Jsoup.parse(content);
    // for (Entry<String, String> entry : params.entrySet()) {
    // Elements es = doc.select(entry.getValue());
    // for (Element element : es) {
    // handle.handle(entry.getKey(), new HTMLElement(element));
    // }
    // }
    // }

    public static void main(String[] args) {
        HTMLParser p = new HTMLParser();
        String html = "<html><head><title>开源中国社区</title></head>"
                + "<body><p class='aa bb'>这里是jsoup 项目的相关文章</p><div id='dd'><p>ppp <a>wasdfasdf</a> qqq</p></div><p class='bb'>这文章</p><div class='cc'><p>XXX <a>bbbbbb</a> 5555</p></div></body></html>";
        // System.out.println("!!!" + p.parse(html, ""));
        org.tomstools.crawler.common.Element e = p.parse(html, null);
        e.select("body p", new ElementProcessor() {
            @Override
            public boolean process(org.tomstools.crawler.common.Element element) {
                if (null != element)
                    System.out.println(element.getText());
                return true;
            }
        });
    }

    public static class HTMLElement implements org.tomstools.crawler.common.Element {
        private Element element;

        /**
         * @param element
         * @since 1.0
         */
        public HTMLElement(Element element) {
            super();
            this.element = element;
        }

        @Override
        public String getCode() {
            return element.outerHtml();
        }

        @Override
        public String getAttribute(String attributeName) {
            return element.attr(attributeName);
        }

        @Override
        public String getText() {
            return element.ownText();
        }

        /*
         * @since 1.0
         */
        @Override
        public String toString() {
            return "HTMLElement [elements=" + element + "]";
        }

        @Override
        public void select(String cssQuery, ElementProcessor processor) {
            if (Utils.isEmpty(cssQuery)) {
                return;
            }
            Elements es = element.select(cssQuery);
            if (!es.isEmpty()) {
                for (Element element : es) {
                    if (!processor.process(new HTMLElement(element))) {
                        // 处理失败则不再继续后续的处理
                        break;
                    }
                }
            } else {
                processor.process(null);
            }
        }
    }

}
