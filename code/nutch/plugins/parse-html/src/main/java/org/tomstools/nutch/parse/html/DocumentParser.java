/**
 * 
 */
package org.tomstools.nutch.parse.html;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

/**
 * @author Lotomer
 *
 */
public class DocumentParser {
	private static final Logger LOG = LoggerFactory.getLogger(DocumentParser.class);
	private Map<String,XPathSelector> fieldsSelector;
	public DocumentParser(){
		fieldsSelector = new HashMap<String, XPathSelector>();
	}
	/**
	 * 批量添加字段选择器
	 */
	public void addFieldsSelector(Map<String,XPathSelector> fieldsSelector){
		this.fieldsSelector.putAll(fieldsSelector);
	}
	
	/**
	 * 添加字段选择器
	 */
	public void addFieldSelector(String fieldName,XPathSelector selector){
		this.fieldsSelector.put(fieldName, selector);
	}
	/**
	 * 从html页面中提取字段
	 * @param html 待提取网页
	 * @return 提取出来在字段内容。可为空，不为null。
	 */
	public Map<String,String> parse(String html){
		if (null != fieldsSelector && !fieldsSelector.isEmpty()){
			HtmlCleaner hc = new HtmlCleaner();
			TagNode node = hc.clean(html);
			
			Map<String, String> result = new HashMap<String, String>();
			for (Entry<String, XPathSelector> entry : fieldsSelector.entrySet()) {
				String fieldValue = entry.getValue().evaluate2string(node);
				if (null != fieldValue){
					result.put(entry.getKey(), fieldValue);
				}
			}
			return result;
		}
		
		return Collections.emptyMap();
	}
	public Map<String,String>  parse(InputSource input) {
		if (null != fieldsSelector && !fieldsSelector.isEmpty()){
			
			try {
				Reader reader = new InputStreamReader(input.getByteStream(), input.getEncoding());
				TagNode node = new HtmlCleaner().clean(reader);
				Map<String, String> result = new HashMap<String, String>();
				for (Entry<String, XPathSelector> entry : fieldsSelector.entrySet()) {
					String fieldValue = entry.getValue().evaluate2string(node);
					if (null != fieldValue){
						result.put(entry.getKey(), fieldValue);
					}
				}
				
				return result;
			} catch (IOException e) {
				LOG.warn(e.getMessage(), e);
			}
		}
		
		return Collections.emptyMap();
	}
	
	public static void main(String[] args) throws IOException, XPatherException,
			ParserConfigurationException, XPathExpressionException {
		String url = "http://zhidao.baidu.com/daily";
		String html = null;
		try {
			Connection connect = Jsoup.connect(url);
			html = connect.get().body().html();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("==========");
		long st = System.currentTimeMillis();
		DocumentParser parser = new DocumentParser();
		parser.addFieldSelector("title", new XPathSelector("//p[@class='banner-daily-info']/span[1]"));
		parser.addFieldSelector("author", new XPathSelector("//div[@class='banner-wp']//p[@class='banner-daily-info']/span[2]"));
		
		System.out.println(System.currentTimeMillis() - st);
		st = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			parser.parse(html);
		}

		System.out.println(System.currentTimeMillis() - st);

		System.out.println(parser.parse(html));
	}
}
