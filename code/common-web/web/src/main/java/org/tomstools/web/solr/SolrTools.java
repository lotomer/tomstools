/**
 * 
 */
package org.tomstools.web.solr;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.LBHttpSolrServer;
import org.apache.solr.client.solrj.request.FieldAnalysisRequest;
import org.apache.solr.client.solrj.response.AnalysisResponseBase.AnalysisPhase;
import org.apache.solr.client.solrj.response.AnalysisResponseBase.TokenInfo;
import org.apache.solr.client.solrj.response.FieldAnalysisResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.util.StringUtils;

/**
 * @author Administrator
 *
 */
public class SolrTools {
	private static final Log LOG = LogFactory.getLog(SolrTools.class);
	private static final String FIELD_TIME = "tstamp";
	private static final SimpleDateFormat SOLR_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private SolrServer server;

	public SolrTools(List<String> solrServerUrls) throws MalformedURLException {
		if (null == solrServerUrls || solrServerUrls.isEmpty()) {
			throw new RuntimeException("The solr server's url cannot be empty!");
		} else if (solrServerUrls.size() == 1) {
			server = new HttpSolrServer(solrServerUrls.get(0));
		} else {
			server = new LBHttpSolrServer(solrServerUrls.toArray(new String[0]));
		}
	}

	public SolrTools(String solrServerUrl) {
		server = new HttpSolrServer(solrServerUrl);
	}

	/**
	 * 执行搜索
	 * 
	 * @param query
	 *            搜索语句。包含要搜索的字段、内容，以及语法。列如：“content:(中国 AND 体育) AND title:(2014
	 *            OR 2015)”，表示content字段中包含“中国”和“体育”，并且title字段中包含“2014”或者“2015”
	 * @param selectFields
	 *            返回结果要返回的字段。为null或空集时将返回所有字段
	 * @param begin
	 *            过滤条件：开始时间。为null时不启用
	 * @param end
	 *            过滤条件：结束时间。为null时使用当前时间
	 * @param order
	 *            以时间字段进行排序的方式。为null表示不排序
	 * @param start
	 *            开始记录索引号。可为null，默认0
	 * @param rows
	 *            返回结果包含的记录数。可为null。默认10
	 * @return 搜索结果
	 * @throws Exception
	 */
	public QueryResponse querySortByTime(String query, List<String> selectFields, Date begin, Date end, ORDER order,
			Integer start, Integer rows) throws Exception {
		SortClause sort = null;
		if (null != order) {
			sort = new SortClause(FIELD_TIME, order);
		}

		return query(query, selectFields, begin, end, sort, start, rows);
	}

	/**
	 * 执行搜索
	 * 
	 * @param query
	 *            搜索语句。包含要搜索的字段、内容，以及语法。列如：“content:(中国 AND 体育) AND title:(2014
	 *            OR 2015)”，表示content字段中包含“中国”和“体育”，并且title字段中包含“2014”或者“2015”
	 * @param selectFields
	 *            返回结果要返回的字段。为null或空集时将返回所有字段
	 * @param begin
	 *            过滤条件：开始时间。为null时不启用
	 * @param end
	 *            过滤条件：结束时间。为null时使用当前时间
	 * @param order
	 *            以时间字段进行排序的方式。为null表示不排序
	 * @param start
	 *            开始记录索引号。可为null，默认0
	 * @param rows
	 *            返回结果包含的记录数。可为null。默认10
	 * @return 搜索结果
	 * @throws Exception
	 */
	public QueryResponse querySortByTime(String query, String selectFields, Date begin, Date end, ORDER order,
			Integer start, Integer rows) throws Exception {
		SortClause sort = null;
		if (null != order) {
			sort = new SortClause(FIELD_TIME, order);
		}

		return query(query, selectFields, begin, end, sort, start, rows);
	}

	/**
	 * 执行搜索
	 * 
	 * @param query
	 *            搜索语句。包含要搜索的字段、内容，以及语法。列如：“content:(中国 AND 体育) AND title:(2014
	 *            OR 2015)”，表示content字段中包含“中国”和“体育”，并且title字段中包含“2014”或者“2015”
	 * @param selectFields
	 *            返回结果要返回的字段。为null或空集时将返回所有字段。多个字段直接用英文逗号“,”分隔
	 * @param begin
	 *            过滤条件：开始时间。为null时不启用
	 * @param end
	 *            过滤条件：结束时间。为null时使用当前时间
	 * @param sort
	 *            排序。为null表示不排序
	 * @param start
	 *            开始记录索引号。可为null，默认0
	 * @param rows
	 *            返回结果包含的记录数。可为null。默认10
	 * @return 搜索结果
	 * @throws Exception
	 * @throws SolrServerException
	 */
	public QueryResponse query(String query, String selectFields, Date begin, Date end, SortClause sort, Integer start,
			Integer rows) throws Exception {
		List<String> fields = null;
		if (!StringUtils.isEmpty(selectFields)) {
			String[] ss = selectFields.split(",");
			fields = new ArrayList<String>(ss.length);
			for (int i = 0; i < ss.length; i++) {
				fields.add(ss[i]);
			}
		}

		return query(query, fields, begin, end, sort, start, rows);
	}

	/**
	 * 执行搜索
	 * 
	 * @param query
	 *            搜索语句。包含要搜索的字段、内容，以及语法。列如：“content:(中国 AND 体育) AND title:(2014
	 *            OR 2015)”，表示content字段中包含“中国”和“体育”，并且title字段中包含“2014”或者“2015”。
	 *            如果为null或空字符串，则表示搜索所有
	 * @param selectFields
	 *            返回结果要返回的字段。为null或空集时将返回所有字段
	 * @param begin
	 *            过滤条件：开始时间。为null时不启用
	 * @param end
	 *            过滤条件：结束时间。为null时使用当前时间
	 * @param sort
	 *            排序。为null表示不排序
	 * @param start
	 *            开始记录索引号。可为null，默认0
	 * @param rows
	 *            返回结果包含的记录数。可为null。默认10
	 * @return 搜索结果
	 * @throws Exception
	 * @throws SolrServerException
	 */
	public QueryResponse query(String query, List<String> selectFields, Date begin, Date end, SortClause sort,
			Integer start, Integer rows) throws Exception {
		SolrQuery solrQuery = new SolrQuery();

		// 查询语句
		query = !StringUtils.isEmpty(query) ? query : "*:*";
		solrQuery.setQuery(query);

		// 要返回的字段
		if (null != selectFields) {
			for (String field : selectFields) {
				solrQuery.addField(field);
			}
		}

		// 添加时间过滤条件
		if (null != begin) {
			if (null == end) {
				end = new Date();
			}
			solrQuery.add("fq", getFilterDateStr(FIELD_TIME, begin, end));
		}

		if (null != sort) {
			solrQuery.addSort(sort);
		}

		solrQuery.setStart(start);
		solrQuery.setRows(rows);
		try {
			return server.query(solrQuery);
		} catch (SolrServerException e) {
			throw new Exception(e.getMessage(), e);
		}
	}

	/**
	 * 执行搜索
	 * 
	 * @param query
	 *            搜索语句。包含要搜索的字段、内容，以及语法。列如：“content:(中国 AND 体育 NOT 男足) AND
	 *            title:(2014 OR
	 *            2015)”，表示content字段中包含“中国”和“体育”但不包含“男足”，并且title字段中包含“2014”或者“
	 *            2015”
	 * @param begin
	 *            过滤条件：开始时间。为null时不启用
	 * @param end
	 *            过滤条件：结束时间。为null时使用当前时间
	 * @return 搜索结果记录数
	 * @throws Exception
	 */
	public long count(String query, Date begin, Date end) throws Exception {
		QueryResponse result = query(query, "", begin, end, null, null, null);
		return result.getResults().getNumFound();
	}

	private String getFilterDateStr(String field, Date start, Date end) {
		return String.format(Locale.ROOT, "%s:[%s TO %s]", field, format(start), format(end));
	}

	/**
	 * 将日期格式化为SOLR支持的格式类型。如2015-08-15T22:26:58.981Z
	 * 
	 * @param date
	 *            时间，
	 * @return 格式化后的字符串。如果输入的时间为null，则返回null
	 */
	public static String format(Date date) {
		if (null != date) {
			return SOLR_DATE_FORMAT.format(date);
		} else {
			return null;
		}
	}

	public static void main(String[] args) throws Exception {
		test2("http://203.130.40.177:9898/solr");
	}

	public static void test1(String solrServer) throws Exception {
		List<String> results = new SolrTools(solrServer).getAnalysis("习大大与大家分享开发的实践经验");
		for (String word : results) {
			System.out.println(word);
		}
	}

	public static void test2(String solrServer) throws Exception {
		Calendar c = Calendar.getInstance();
		Date end = c.getTime();
		Date start = new Date(end.getTime() - 3600 * 24 * 1000);
		ArrayList<String> serverUrls = new ArrayList<String>();
		serverUrls.add(solrServer);
		SolrTools solr = new SolrTools(serverUrls);
		String query = "text:(美国  页岩气)~3";
		List<String> selectFields = new ArrayList<String>();
		selectFields.add("title");
		selectFields.add("url");
		selectFields.add("host");
		selectFields.add("tstamp");
		QueryResponse rsp = solr.querySortByTime(query, selectFields, start, end, ORDER.desc, 0, 5);
		SolrDocumentList result = rsp.getResults();
		long foundCount = result.getNumFound();
		System.out.println("Found count: " + foundCount);
		for (SolrDocument doc : result) {
			System.out.println("=============================================");
			if (doc.hasChildDocuments())
				System.out.println("child count:" + doc.getChildDocumentCount());
			System.out.println(doc.getFieldValuesMap());
		}
	}

	/**
	 * 给指定的语句分词。
	 * 
	 * @param sentence
	 *            被分词的语句
	 * @return 分词结果
	 */
	public List<String> getAnalysis(String sentence) {
		FieldAnalysisRequest request = new FieldAnalysisRequest("/analysis/field");
		request.addFieldName("title");// 字段名，随便指定一个支持中文分词的字段
		request.setFieldValue("");// 字段值，可以为空字符串，但是需要显式指定此参数
		request.setQuery(sentence);

		FieldAnalysisResponse response = null;
		try {
			response = request.process(this.server);
		} catch (Exception e) {
			LOG.error("获取查询语句的分词时遇到错误", e);
		}

		List<String> results = new ArrayList<String>();
		if (null != response) {
			Iterator<AnalysisPhase> it = response.getFieldNameAnalysis("title").getQueryPhases().iterator();
			while (it.hasNext()) {
				AnalysisPhase pharse = (AnalysisPhase) it.next();
				List<TokenInfo> list = pharse.getTokens();
				for (TokenInfo info : list) {
					results.add(info.getText());
				}

			}
		}

		return results;
	}
}
