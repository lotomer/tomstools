package org.tomstools.nutch.parse.html;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.metadata.Nutch;
import org.apache.nutch.parse.HTMLMetaTags;
import org.apache.nutch.parse.HtmlParseFilters;
import org.apache.nutch.parse.Outlink;
import org.apache.nutch.parse.Parse;
import org.apache.nutch.parse.ParseData;
import org.apache.nutch.parse.ParseImpl;
import org.apache.nutch.parse.ParseResult;
import org.apache.nutch.parse.ParseStatus;
import org.apache.nutch.parse.Parser;
import org.apache.nutch.parse.html.DOMBuilder;
import org.apache.nutch.parse.html.DOMContentUtils;
import org.apache.nutch.parse.html.HTMLMetaProcessor;
import org.apache.nutch.protocol.Content;
import org.apache.nutch.util.EncodingDetector;
import org.apache.nutch.util.NutchConfiguration;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentFragment;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TMHtmlParser implements Parser {
	public static final Logger LOG = LoggerFactory
			.getLogger("org.apache.nutch.parse.html");

	// I used 1000 bytes at first, but found that some documents have
	// meta tag well past the first 1000 bytes.
	// (e.g. http://cn.promo.yahoo.com/customcare/music.html)
	private static final int CHUNK_SIZE = 2000;

	// NUTCH-1006 Meta equiv with single quotes not accepted
	private static Pattern metaPattern = Pattern.compile(
			"<meta\\s+([^>]*http-equiv=(\"|')?content-type(\"|')?[^>]*)>",
			Pattern.CASE_INSENSITIVE);
	private static Pattern charsetPattern = Pattern.compile(
			"charset=\\s*([a-z][_\\-0-9a-z]*)", Pattern.CASE_INSENSITIVE);
	private static Pattern charsetPatternHTML5 = Pattern.compile(
			"<meta\\s+charset\\s*=\\s*[\"']?([a-z][_\\-0-9a-z]*)[^>]*>",
			Pattern.CASE_INSENSITIVE);

	private String parserImpl;

	/**
	 * Given a <code>byte[]</code> representing an html file of an
	 * <em>unknown</em> encoding, read out 'charset' parameter in the meta tag
	 * from the first <code>CHUNK_SIZE</code> bytes. If there's no meta tag for
	 * Content-Type or no charset is specified, the content is checked for a
	 * Unicode Byte Order Mark (BOM). This will also cover non-byte oriented
	 * character encodings (UTF-16 only). If no character set can be determined,
	 * <code>null</code> is returned. <br />
	 * See also
	 * http://www.w3.org/International/questions/qa-html-encoding-declarations,
	 * http://www.w3.org/TR/2011/WD-html5-diff-20110405/#character-encoding, and
	 * http://www.w3.org/TR/REC-xml/#sec-guessing
	 * 
	 * @param content
	 *            <code>byte[]</code> representation of an html file
	 */

	private static String sniffCharacterEncoding(byte[] content) {
		int length = content.length < CHUNK_SIZE ? content.length : CHUNK_SIZE;

		// We don't care about non-ASCII parts so that it's sufficient
		// to just inflate each byte to a 16-bit value by padding.
		// For instance, the sequence {0x41, 0x82, 0xb7} will be turned into
		// {U+0041, U+0082, U+00B7}.
		String str = "";
		try {
			str = new String(content, 0, length, Charset.forName("ASCII")
					.toString());
		} catch (UnsupportedEncodingException e) {
			// code should never come here, but just in case...
			return null;
		}

		Matcher metaMatcher = metaPattern.matcher(str);
		String encoding = null;
		if (metaMatcher.find()) {
			Matcher charsetMatcher = charsetPattern.matcher(metaMatcher
					.group(1));
			if (charsetMatcher.find())
				encoding = new String(charsetMatcher.group(1));
		}
		if (encoding == null) {
			// check for HTML5 meta charset
			metaMatcher = charsetPatternHTML5.matcher(str);
			if (metaMatcher.find()) {
				encoding = new String(metaMatcher.group(1));
			}
		}
		if (encoding == null) {
			// check for BOM
			if (content.length >= 3 && content[0] == (byte) 0xEF
					&& content[1] == (byte) 0xBB && content[2] == (byte) 0xBF) {
				encoding = "UTF-8";
			} else if (content.length >= 2) {
				if (content[0] == (byte) 0xFF && content[1] == (byte) 0xFE) {
					encoding = "UTF-16LE";
				} else if (content[0] == (byte) 0xFE
						&& content[1] == (byte) 0xFF) {
					encoding = "UTF-16BE";
				}
			}
		}

		return encoding;
	}

	private String defaultCharEncoding;

	private Configuration conf;

	private DOMContentUtils utils;

	private HtmlParseFilters htmlParseFilters;

	private String cachingPolicy;

	private HashMap<String, Map<String, XPathSelector>> hostsFieldsSelector;

	public ParseResult getParse(Content content) {
		HTMLMetaTags metaTags = new HTMLMetaTags();

		URL base;
		try {
			base = new URL(content.getBaseUrl());
		} catch (MalformedURLException e) {
			return new ParseStatus(e).getEmptyParseResult(content.getUrl(),
					getConf());
		}

		String text = "";
		String title = "";
		Outlink[] outlinks = new Outlink[0];
		Metadata metadata = new Metadata();

		// parse the content
		DocumentFragment root;
		try {
			byte[] contentInOctets = content.getContent();
			InputSource input = new InputSource(new ByteArrayInputStream(
					contentInOctets));

			EncodingDetector detector = new EncodingDetector(conf);
			detector.autoDetectClues(content, true);
			detector.addClue(sniffCharacterEncoding(contentInOctets), "sniffed");
			String encoding = detector.guessEncoding(content,
					defaultCharEncoding);

			metadata.set(Metadata.ORIGINAL_CHAR_ENCODING, encoding);
			metadata.set(Metadata.CHAR_ENCODING_FOR_CONVERSION, encoding);

			input.setEncoding(encoding);
			if (LOG.isTraceEnabled()) {
				LOG.trace("Parsing...");
			}
			root = parse(input);
			// start by Lotomer
			// 获取域名对应的内容提取模板，如果没有获取到，则试试上一级域名直到顶级域名，如果没有则使用默认
			String domain = base.getHost().toLowerCase();
			Map<String, XPathSelector> fieldsSelector = null;
			while (true) {
				fieldsSelector = hostsFieldsSelector.get(domain);
				if (null == fieldsSelector) {
					// 没有找到相应的规则，则缩短域名继续寻找
					int index = domain.indexOf(".");
					if (-1 < index) {
						// 还有上一级域名
						domain = domain.substring(index + 1);
					} else {
						// 已经扫描完了，则退出循环
						break;
					}
				} else {
					break;
				}
			}
			if (null != fieldsSelector) {
				text = null; // 标记该网站必须使用提取器，如果没有提取到内容，说明提取器配置需要更新
				DocumentParser docParser = new DocumentParser();
				docParser.addFieldsSelector(fieldsSelector);
				Map<String, String> fields = docParser.parse(new String(
						contentInOctets, encoding));
				for (Entry<String, String> field : fields.entrySet()) {
					if (field.getKey().contains("content")){
						text = field.getValue();
					}else if (field.getKey().contains("title")){
						title = field.getValue();
					}else{
						metadata.add(field.getKey(), field.getValue());
					}
				}
				//LOG.info("=========================");
				//LOG.info(metadata.toString());
			}
			// finished by Lotomer
		} catch (IOException e) {
			return new ParseStatus(e).getEmptyParseResult(content.getUrl(),
					getConf());
		} catch (DOMException e) {
			return new ParseStatus(e).getEmptyParseResult(content.getUrl(),
					getConf());
		} catch (SAXException e) {
			return new ParseStatus(e).getEmptyParseResult(content.getUrl(),
					getConf());
		} catch (Exception e) {
			LOG.error("Error: ", e);
			return new ParseStatus(e).getEmptyParseResult(content.getUrl(),
					getConf());
		}

		// get meta directives
		HTMLMetaProcessor.getMetaTags(metaTags, root, base);
		if (LOG.isTraceEnabled()) {
			LOG.trace("Meta tags for " + base + ": " + metaTags.toString());
		}

		// check meta directives
		if (!metaTags.getNoIndex() && text != null && text.isEmpty()) { // okay to index
			StringBuffer sb = new StringBuffer();
			if (LOG.isTraceEnabled()) {
				LOG.trace("Getting text...");
			}
			utils.getText(sb, root); // extract text
			text = sb.toString();
			sb.setLength(0);
			if (LOG.isTraceEnabled()) {
				LOG.trace("Getting title...");
			}
			utils.getTitle(sb, root); // extract title
			title = sb.toString().trim();
		}

		if (!metaTags.getNoFollow()) { // okay to follow links
			ArrayList<Outlink> l = new ArrayList<Outlink>(); // extract outlinks
			URL baseTag = utils.getBase(root);
			if (LOG.isTraceEnabled()) {
				LOG.trace("Getting links...");
			}
			utils.getOutlinks(baseTag != null ? baseTag : base, l, root);
			outlinks = l.toArray(new Outlink[l.size()]);
			if (LOG.isTraceEnabled()) {
				LOG.trace("found " + outlinks.length + " outlinks in "
						+ content.getUrl());
			}
		}

		ParseStatus status = new ParseStatus(ParseStatus.SUCCESS);
		if (text == null){
			text="";
		}else if (text.isEmpty()) {
			status.setMajorCode(ParseStatus.FAILED);
			status.setMinorCode(ParseStatus.FAILED_MISSING_CONTENT);
		}
		if (metaTags.getRefresh()) {
			status.setMinorCode(ParseStatus.SUCCESS_REDIRECT);
			status.setArgs(new String[] { metaTags.getRefreshHref().toString(),
					Integer.toString(metaTags.getRefreshTime()) });
		}
		ParseData parseData = new ParseData(status, title, outlinks,
				content.getMetadata(), metadata);
		ParseResult parseResult = ParseResult.createParseResult(
				content.getUrl(), new ParseImpl(text, parseData));

		// run filters on parse
		ParseResult filteredParse = this.htmlParseFilters.filter(content,
				parseResult, metaTags, root);
		if (metaTags.getNoCache()) { // not okay to cache
			for (Map.Entry<org.apache.hadoop.io.Text, Parse> entry : filteredParse)
				entry.getValue().getData().getParseMeta()
						.set(Nutch.CACHING_FORBIDDEN_KEY, cachingPolicy);
		}
		return filteredParse;
	}

	private DocumentFragment parse(InputSource input) throws Exception {
		if (parserImpl.equalsIgnoreCase("tagsoup"))
			return parseTagSoup(input);
		else
			return parseNeko(input);
	}

	private DocumentFragment parseTagSoup(InputSource input) throws Exception {
		HTMLDocumentImpl doc = new HTMLDocumentImpl();
		DocumentFragment frag = doc.createDocumentFragment();
		DOMBuilder builder = new DOMBuilder(doc, frag);
		org.ccil.cowan.tagsoup.Parser reader = new org.ccil.cowan.tagsoup.Parser();
		reader.setContentHandler(builder);
		reader.setFeature(org.ccil.cowan.tagsoup.Parser.ignoreBogonsFeature,
				true);
		reader.setFeature(org.ccil.cowan.tagsoup.Parser.bogonsEmptyFeature,
				false);
		reader.setProperty("http://xml.org/sax/properties/lexical-handler",
				builder);
		reader.parse(input);
		return frag;
	}

	private DocumentFragment parseNeko(InputSource input) throws Exception {
		DOMFragmentParser parser = new DOMFragmentParser();
		try {
			parser.setFeature(
					"http://cyberneko.org/html/features/scanner/allow-selfclosing-iframe",
					true);
			parser.setFeature(
					"http://cyberneko.org/html/features/augmentations", true);
			parser.setProperty(
					"http://cyberneko.org/html/properties/default-encoding",
					defaultCharEncoding);
			parser.setFeature(
					"http://cyberneko.org/html/features/scanner/ignore-specified-charset",
					true);
			parser.setFeature(
					"http://cyberneko.org/html/features/balance-tags/ignore-outside-content",
					false);
			parser.setFeature(
					"http://cyberneko.org/html/features/balance-tags/document-fragment",
					true);
			parser.setFeature(
					"http://cyberneko.org/html/features/report-errors",
					LOG.isTraceEnabled());
		} catch (SAXException e) {
		}
		// convert Document to DocumentFragment
		HTMLDocumentImpl doc = new HTMLDocumentImpl();
		doc.setErrorChecking(false);
		DocumentFragment res = doc.createDocumentFragment();
		DocumentFragment frag = doc.createDocumentFragment();
		parser.parse(input, frag);
		res.appendChild(frag);

		try {
			while (true) {
				frag = doc.createDocumentFragment();
				parser.parse(input, frag);
				if (!frag.hasChildNodes())
					break;
				if (LOG.isInfoEnabled()) {
					LOG.info(" - new frag, " + frag.getChildNodes().getLength()
							+ " nodes.");
				}
				res.appendChild(frag);
			}
		} catch (Exception e) {
			LOG.error("Error: ", e);
		}
		;
		return res;
	}

	public void setConf(Configuration conf) {
		this.conf = conf;
		this.htmlParseFilters = new HtmlParseFilters(getConf());
		this.parserImpl = getConf().get("parser.html.impl", "neko");
		this.defaultCharEncoding = getConf().get(
				"parser.character.encoding.default", "windows-1252");
		this.utils = new DOMContentUtils(conf);
		this.cachingPolicy = getConf().get("parser.caching.forbidden.policy",
				Nutch.CACHING_FORBIDDEN_CONTENT);
		hostsFieldsSelector = new HashMap<String, Map<String, XPathSelector>>();
		try {
			init();
		} catch (ClassNotFoundException e) {
			LOG.error(e.getMessage(), e);
		} catch (SQLException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private void init() throws ClassNotFoundException, SQLException {
		String jdbcDriver = getConf().get("tm.parser.jdbc.driver");
		String jdbcUrl = getConf().get("tm.parser.jdbc.url");
		String jdbcUser = getConf().get("tm.parser.jdbc.user");
		String jdbcPassword = getConf().get("tm.parser.jdbc.password");
		String sql = getConf().get("tm.parser.sql");
		Class.forName(jdbcDriver);
		Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUser,
				jdbcPassword);
		PreparedStatement pstmt = null;
		ResultSet result = null;
		try {
			pstmt = conn.prepareStatement(sql);
			result = pstmt.executeQuery();
			while (result.next()) {
				String host = result.getString("host").toLowerCase();
				String field = result.getString("field");
				String xpath = result.getString("xpath");
				Map<String, XPathSelector> selectors = hostsFieldsSelector.get(host);
				if (selectors == null){
					selectors = new HashMap<String, XPathSelector>();
					hostsFieldsSelector.put(host, selectors);
				}
				selectors.put(field.toLowerCase(), new XPathSelector(xpath));
			}
		}finally{
			if (null != result){
				result.close();
				result = null;
			}
			if (null != pstmt){
				pstmt.close();
				pstmt = null;
			}
		}

		// 加载解析器
//		Map<String, XPathSelector> value = new HashMap<String, XPathSelector>();
//		value.put("_title", new XPathSelector("//div[@class='detail']/h1"));
//		value.put("_time", new XPathSelector(
//				"//div[@class='detail']/h4/div/span[1]"));
//		value.put("_author", new XPathSelector(
//				"//div[@class='detail']/h4/div/span[last()]"));
//		value.put(
//				"_content",
//				new XPathSelector(
//						"//div[@class='detail']/div[@class='con news_content']/p[position() < last()]"));
//		hostsFieldsSelector.put("csdn.net", value);
	}

	public Configuration getConf() {
		return this.conf;
	}
	
	public static void main(String[] args) throws Exception {
		// LOG.setLevel(Level.FINE);
		String name = "G:\\临时目录\\CSDN.NET.htm";
		if (0 < args.length) {
			name = args[0];
		}
		String url = "file:" + name;
		File file = new File(name);
		byte[] bytes = new byte[(int) file.length()];
		DataInputStream in = new DataInputStream(new FileInputStream(file));
		in.readFully(bytes);
		in.close();
		Configuration conf = NutchConfiguration.create();
		conf.setStrings("plugin.folders", "D:\\works\\工具\\nutch-1.10\\plugins");
		conf.set("tm.parser.jdbc.driver","com.mysql.jdbc.Driver");
		conf.set("tm.parser.jdbc.url","jdbc:mysql://127.0.0.1:3306/common?useUnicode=true&amp;characterEncoding=UTF-8");
		conf.set("tm.parser.jdbc.user","root");
		conf.set("tm.parser.jdbc.password","root123");
		conf.set("tm.parser.sql","select host,field,xpath from T_CRAWL_PARSE_CONFIG where IS_VALID='1'");
		TMHtmlParser parser = new TMHtmlParser();
		parser.setConf(conf);
		ParseResult parse = parser
				.getParse(new Content(url, "http://www.csdn.net", bytes,
						"text/html", new Metadata(), conf));
		if (null != parse) {
			for (Iterator<Entry<Text, Parse>> i = parse.iterator(); i.hasNext();) {
				Entry<Text, Parse> entry = i.next();
				System.out.println(entry.getKey() + ":" + entry.getValue());
				System.out.println(entry.getValue().getText());
				System.out.println("-----------------------");
				System.out.println(entry.getValue().getData());
			}
		}
	}
}
