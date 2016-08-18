package org.tomstools.nutch.parse.html;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPather;
import org.htmlcleaner.XPatherException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lotomer
 *
 */
public class XPathSelector {
	private static final Logger LOG = LoggerFactory
			.getLogger(XPathSelector.class);
	private static final String DEFAULT_SEPARATOR = " ";
	private XPather xPather;
	
	public XPathSelector(String xPathExp) {
		xPather = new XPather(xPathExp);
	}

	public Object[] evaluate(String html) throws XPatherException {
		HtmlCleaner hc = new HtmlCleaner();
		TagNode node = hc.clean(html);
		return evaluate(node);
	}

	public Object[] evaluate(TagNode node) throws XPatherException {
		return xPather.evaluateAgainstNode(node);
	}

	/**
	 * 根据xpath提取内容
	 * 
	 * @param node
	 *            待提取对象
	 * @return 根据xpath提取出来的内容。null表示提取失败
	 */
	public String evaluate2string(TagNode node) {
		return evaluate2string(node, DEFAULT_SEPARATOR);
	}

	/**
	 * 根据xpath提取内容
	 * @param node 待提取对象
	 * @param separator 多个内容连接符号
	 * @return 根据xpath提取出来的内容。null表示提取失败
	 */
	public String evaluate2string(TagNode node, String separator) {
		Object[] result = null;
		try {
			result = xPather.evaluateAgainstNode(node);
		} catch (XPatherException e) {
			LOG.warn(e.getMessage(), e);
		}
		if (null != result && result.length != 0) {
			StringBuilder msg = new StringBuilder();
			for (Object o : result) {
				if (msg.length() != 0) {
					msg.append(separator);
				}
				if (o instanceof TagNode) {
					TagNode n = (TagNode) o;
					msg.append(n.getText());
				} else {
					msg.append(o.toString());
				}
			}
			return msg.toString();
		} else {
			return null;
		}
	}

	public static void main(String[] args) throws Exception {
		// testJsoupXpath();
		testHtmlCleaner(args);
	}

	public static void testHtmlCleaner(String[] args) throws IOException, XPatherException,
			ParserConfigurationException, XPathExpressionException {
		String url = "http://zhidao.baidu.com/daily";
		String xpath = "//p[@class='banner-daily-info']/span[1]";
		if (args.length > 1){
			url = args[0];
			xpath = args[1];
		}
		String html = null;
		try {
			Connection connect = Jsoup.connect(url);
			html = connect.get().body().html();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Document dom = tn.;//new DomSerializer(new
		// CleanerProperties()).createDOM(tn);
		System.out.println("==========");
		long st = System.currentTimeMillis();
		XPathSelector selector = new XPathSelector(xpath);
		HtmlCleaner hc = new HtmlCleaner();
		TagNode node = hc.clean(html);
		System.out.println(System.currentTimeMillis() - st);
		st = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			// selector.evaluate(node);
		}

		System.out.println(System.currentTimeMillis() - st);

		System.out.println(selector.evaluate2string(node));
		// result = xPath.evaluate(xpath, , XPathConstants.NODESET);
		// if (result instanceof NodeList) {
		// NodeList nodeList = (NodeList) result;
		// System.out.println(nodeList.getLength());
		// for (int i = 0; i < nodeList.getLength(); i++) {
		// Node node = nodeList.item(i);
		// System.out.println(node.getNodeValue() == null ? node
		// .getTextContent() : node.getNodeValue());
		// }
		// }
	}
}
