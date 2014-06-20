/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.tomstools.crawler.common.ElementProcessor;
import org.tomstools.crawler.common.Utils;

/**
 * html文档解析器
 * 
 * @author admin
 * @date 2014年3月12日
 * @time 下午8:46:13
 * @version 1.0
 */
public class HTMLParser implements Parser {
    /**
     * 将经过HTML转义后的字符串转换为转义之前的字符串。<br>
     * <pre>
     * 比如：
     *      > 转义之后就变成了 &amp;gt;
     *      < 转义之后就变成了 &amp;lt;
     *      & 转义之后就变成了 &amp;amp;
     * </pre>
     * @param text HTML escaped string
     * @return 没有经过HTML转移的字符串
     * @since 1.0
     */
    public static String unescape(String text){
        return org.jsoup.parser.Parser.unescapeEntities(text, false);
    }
    
    public org.tomstools.crawler.common.Element parse(String content, String selector) {
        Document doc = Jsoup.parse(content);
        if (Utils.isEmpty(selector)) {
            return new HTMLElement(doc);
        } else {
            return new HTMLElement(doc.select(selector).first());
        }
    }

    public static void main(String[] args) {
        HTMLParser p = new HTMLParser();
        String s = "http://www.landchina.com/default.aspx/default.aspx?tabid=382&ampcomname=default&amp;wmguid=75c72564-ffd9-426a-954b-8ac2df0903b7&amp;recorderguid=9520adbe-5a22-4eb0-91bb-53f105a202f7";
        String html = "<html><head><title>开源中国社区</title></head>"
                + "<body><p>000</p><div><p>11111</p><p><span title=\"222222\">22...</span></p><p>3333</p></div><p><span title=\"444444\">44...</span></p></body></html>";
        System.out.println(org.jsoup.parser.Parser.unescapeEntities(s, false));
        System.out.println("" + p.parse(s, "").getText());
        org.tomstools.crawler.common.Element e = p.parse(html, null);
        e.select("div>p>span,div>p:not(:has(span))", new ElementProcessor() {
            public boolean process(org.tomstools.crawler.common.Element element) {
                if (null != element){
                    System.out.println(element.getOwnText());
                }
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

        public String getCode() {
            return element.outerHtml();
        }

        public String getAttribute(String attributeName) {
            return element.attr(attributeName);
        }

        public String getText() {
            return element.text();
        }
        public String getOwnText() {
            // 先判断是否包含title属性，如果包含title属性则直接返回title属性
            if(element.hasAttr("title")){
                return element.attr("title");
            }else{
                return element.ownText();
            }
        }

        /*
         * @since 1.0
         */
        @Override
        public String toString() {
            return "{element=" + element.outerHtml() + "}";
        }

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

        @Override
        public org.tomstools.crawler.common.Element select(String cssQuery) {
            if (!Utils.isEmpty(cssQuery)) {
                Elements es = element.select(cssQuery);
                if (!es.isEmpty()) {
                    return new HTMLElement(es.first());
                }
            }

            return null;
        }

        @Override
        public String getUnescapedText() {
            return unescape(getText());
        }

        @Override
        public String getUnescapedOwnText() {
            return unescape(getOwnText());
        }
    }

}
