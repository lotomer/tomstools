/**
 * 
 */
package org.tomstools.web.service;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.tomstools.analyzer.AnalyzeAssistor;
import org.tomstools.common.MD5;
import org.tomstools.common.parse.TemplateParser;
import org.tomstools.web.crawler.PageFetcher;
import org.tomstools.web.model.User;
import org.tomstools.web.persistence.BusinessSettingMapper;
import org.tomstools.web.persistence.SiteMapper;
import org.tomstools.web.persistence.SolrMapper;
import org.tomstools.web.solr.SolrTools;

/**
 * @author Administrator
 *
 */
@Service("solrService")
public class SolrService {
	private static final Log LOG = LogFactory.getLog(SolrService.class);
	public static final long STAT_TIME_DEFAULT = 15 * 24 * 3600 * 1000; // 默认统计时长。15天
	private static final String CONFIG_SOLR_URL = "SOLR_URL";
	@Autowired
	private SolrMapper solrMapper;
	@Autowired
	private BusinessSettingMapper businessSettingMapper;
	@Autowired
	private SiteMapper siteMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private MailService mailService;
	@Autowired
	private AnalyzeAssistor analyzer;
	
	public SolrService() {

	}

	// public int statWords() throws Exception {
	// SolrTools solrTool = new SolrTools(userService.getConfig(-1,
	// CONFIG_SOLR_URL));
	// int count = 0;
	// // 1、获取舆论词条规则
	// List<Map<String, Object>> words =
	// businessSettingMapper.selectWordsList();
	// for (Map<String, Object> word : words) {
	// Integer typeId = (Integer) word.get("TYPE_ID");
	// if (null == typeId) {
	// continue;
	// }
	// String templateZM = (String) word.get("TEMPLATE_ZM");
	// String templateFM = (String) word.get("TEMPLATE_FM");
	// String templateZM_E = (String) word.get("TEMPLATE_ZM_E");
	// String templateFM_E = (String) word.get("TEMPLATE_FM_E");
	// long countZM = 0;
	// long countFM = 0;
	// long countZM_E = 0;
	// long countFM_E = 0;
	// // 获取上次统计时间
	// Date lastStatTime = solrMapper.selectLastStatTime(typeId);
	// Date beginTime = null;
	// Date endTime = new Date();
	// if (null != lastStatTime) {
	// beginTime = new Date(lastStatTime.getTime());
	// } else {
	// beginTime = new Date(endTime.getTime() - STAT_TIME_DEFAULT);
	// }
	// // 获取正面信息数
	// if (!StringUtils.isEmpty(templateZM)) {
	// countZM = solrTool.count("text:" + templateZM, beginTime, endTime);
	// }
	// // 获取负面信息数
	// if (!StringUtils.isEmpty(templateFM)) {
	// countFM = solrTool.count("text:" + templateFM, beginTime, endTime);
	// }
	//
	// // 获取正面信息数
	// if (!StringUtils.isEmpty(templateZM_E)) {
	// countZM_E = solrTool.count("text:" + templateZM_E, beginTime, endTime);
	// }
	// // 获取负面信息数
	// if (!StringUtils.isEmpty(templateFM_E)) {
	// countFM_E = solrTool.count("text:" + templateFM_E, beginTime, endTime);
	// }
	// // 将数据入库
	// if (0 != countZM || 0 != countFM) {
	// solrMapper.saveStat(typeId, countZM, countFM, countZM_E, countFM_E,
	// endTime);
	// ++count;
	// }
	// }
	//
	// return count;
	// }

	public int statWordsWithHost() throws Exception {
        SolrTools solrTool = new SolrTools(userService.getConfig(-1, CONFIG_SOLR_URL));
        int count = 0;
        // 1、获取舆论词条规则
        List<Map<String, Object>> words = businessSettingMapper.selectWordsList();
        if (null == words || 0 == words.size()) {
            return count;
        }
        List<Map<String, Object>> siteInfos = siteMapper.selectSiteList();
        if (null == siteInfos || 0 == siteInfos.size()) {
            return count;
        }
        Map<String, Map<String,Object>> sites = new HashMap<String, Map<String,Object>>();
        Map<String,Object> o = new HashMap<String, Object>();
        o.put("SITE_ID", -1);
        sites.put("-1", o);
        for (Map<String, Object> siteInfo : siteInfos) {
            o = new HashMap<String, Object>();
            o.put("SITE_ID", Integer.parseInt(String.valueOf(siteInfo.get("SITE_ID"))));
            o.put("LANG", siteInfo.get("LANG"));
            sites.put(String.valueOf(siteInfo.get("SITE_HOST")),o);
        }
        Date endTime = new Date();
        for (Map<String, Object> word : words) {
            Integer typeId = (Integer) word.get("TYPE_ID");
            if (null == typeId) {
                continue;
            }
            String templateZM = (String) word.get("TEMPLATE_ZM");
            String templateFM = (String) word.get("TEMPLATE_FM");
            String templateZM_E = (String) word.get("TEMPLATE_ZM_E");
            String templateFM_E = (String) word.get("TEMPLATE_FM_E");
            // 获取上次统计时间
            Date lastStatTime = siteMapper.selectLastStatTime(typeId);
            Date beginTime = null;
            if (null != lastStatTime) {
                beginTime = lastStatTime;
            } else {
                beginTime = new Date(endTime.getTime() - STAT_TIME_DEFAULT);
            }
            try{
                // 获取正面信息数
                if (!StringUtils.isEmpty(templateZM)) {
                    count += getSiteCount(typeId, "ZM", solrTool, sites, templateZM, beginTime, endTime);
    
                }
                // 获取负面信息数
                if (!StringUtils.isEmpty(templateFM)) {
                    count += getSiteCount(typeId, "FM", solrTool, sites, templateFM, beginTime, endTime);
                }
    
                // 获取正面信息数
                if (!StringUtils.isEmpty(templateZM_E)) {
                    count += getSiteCount(typeId, "ZM_E", solrTool, sites, templateZM_E, beginTime, endTime);
                }
                // 获取负面信息数
                if (!StringUtils.isEmpty(templateFM_E)) {
                    count += getSiteCount(typeId, "FM_E", solrTool, sites, templateFM_E, beginTime, endTime);
                }
            }catch(Exception e){
                LOG.warn(e.getMessage(),e);
            }
        }
        if (0 < count){
            // 删除原统计结果
            siteMapper.deleteStat();
            // 更新统计结果
            siteMapper.saveStat(new java.sql.Date(endTime.getTime()));
        }
        return count;
    }
//	public int statWordsWithHost_bak() throws Exception {
//        SolrTools solrTool = new SolrTools(userService.getConfig(-1, CONFIG_SOLR_URL));
//        int count = 0;
//        // 1、获取舆论词条规则
//        List<Map<String, Object>> words = businessSettingMapper.selectWordsList();
//        if (null == words || 0 == words.size()) {
//            return count;
//        }
//        List<Map<String, Object>> siteInfos = siteMapper.selectSiteList();
//        if (null == siteInfos || 0 == siteInfos.size()) {
//            return count;
//        }
//        Map<String, Integer> sites = new HashMap<String, Integer>();
//        sites.put("-1", -1);
//        for (Map<String, Object> siteInfo : siteInfos) {
//            sites.put(String.valueOf(siteInfo.get("SITE_HOST")),
//                    Integer.parseInt(String.valueOf(siteInfo.get("SITE_ID"))));
//        }
//        Date endTime = new Date();
//        for (Map<String, Object> word : words) {
//            Integer typeId = (Integer) word.get("TYPE_ID");
//            if (null == typeId) {
//                continue;
//            }
//            String templateZM = (String) word.get("TEMPLATE_ZM");
//            String templateFM = (String) word.get("TEMPLATE_FM");
//            String templateZM_E = (String) word.get("TEMPLATE_ZM_E");
//            String templateFM_E = (String) word.get("TEMPLATE_FM_E");
//            // 获取上次统计时间
//            Date lastStatTime = siteMapper.selectLastStatTime(typeId);
//            Date beginTime = null;
//            if (null != lastStatTime) {
//                beginTime = lastStatTime;
//            } else {
//                beginTime = new Date(endTime.getTime() - STAT_TIME_DEFAULT);
//            }
//            Map<Integer, Long> siteZM = new HashMap<Integer, Long>();
//            Map<Integer, Long> siteFM = new HashMap<Integer, Long>();
//            Map<Integer, Long> siteZM_E = new HashMap<Integer, Long>();
//            Map<Integer, Long> siteFM_E = new HashMap<Integer, Long>();
//            // 获取正面信息数
//            if (!StringUtils.isEmpty(templateZM)) {
//                getSiteCount(typeId, "ZM", solrTool, sites, templateZM, beginTime, endTime, siteZM);
//
//            }
//            // 获取负面信息数
//            if (!StringUtils.isEmpty(templateFM)) {
//                getSiteCount(typeId, "FM", solrTool, sites, templateFM, beginTime, endTime, siteFM);
//            }
//
//            // 获取正面信息数
//            if (!StringUtils.isEmpty(templateZM_E)) {
//                getSiteCount(typeId, "ZM_E", solrTool, sites, templateZM_E, beginTime, endTime, siteZM_E);
//            }
//            // 获取负面信息数
//            if (!StringUtils.isEmpty(templateFM_E)) {
//                getSiteCount(typeId, "FM_E", solrTool, sites, templateFM_E, beginTime, endTime, siteFM_E);
//            }
//
//            // 遍历所有站点，并保存结果
//            for (Entry<String, Integer> entry : sites.entrySet()) {
//                Long sizeZM = siteZM.get(entry.getValue());
//                Long sizeFM = siteFM.get(entry.getValue());
//                Long sizeZM_E = siteZM_E.get(entry.getValue());
//                Long sizeFM_E = siteFM_E.get(entry.getValue());
//                Integer siteId = entry.getValue();
//                if (null != siteId) {
//                    sizeZM = null != sizeZM ? sizeZM : 0;
//                    sizeFM = null != sizeFM ? sizeFM : 0;
//                    sizeZM_E = null != sizeZM_E ? sizeZM_E : 0;
//                    sizeFM_E = null != sizeFM_E ? sizeFM_E : 0;
//                    if (0 != sizeZM || 0 != sizeFM || 0 != sizeZM_E || 0 != sizeFM_E) {
//                        // 将数据入库
//                        siteMapper.saveStat(typeId, siteId, sizeZM, sizeFM, sizeZM_E, sizeFM_E, endTime);
//                        ++count;
//                    }
//                }
//            }
//        }
//
//        return count;
//    }

	/**
	 * 获取统计数据
	 * 
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @param typeId
	 *            词条编号。为null表示不作为查询条件
	 * @return 统计结果。按天统计
	 */
	public List<Map<String, Object>> statWords(Date startTime, Date endTime, Integer typeId) {
		return siteMapper.selectStats(new java.sql.Date(startTime.getTime()), new java.sql.Date(endTime.getTime()), typeId);
	}

	/**
	 * 根据输入的词汇，结合智能词条进行统计
	 * 
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @param queryWord
	 *            查询词汇
	 * @return 统计结果
	 * @throws Exception
	 */
	public List<Map<String, Object>> statWordsWithWordCount(Date startTime, Date endTime, String queryWord)
			throws Exception {
		if (StringUtils.isEmpty(queryWord)) {
			return statWordsCount(startTime, endTime, null);
		}
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		SolrTools solrTool = new SolrTools(userService.getConfig(-1, CONFIG_SOLR_URL));
		// 1、获取舆论词条规则
		List<Map<String, Object>> words = businessSettingMapper.selectWordsList();
		for (Map<String, Object> word : words) {
			Integer typeId = (Integer) word.get("TYPE_ID");
			if (null == typeId) {
				continue;
			}
			String templateZM = (String) word.get("TEMPLATE_ZM");
			String templateFM = (String) word.get("TEMPLATE_FM");
			String templateZM_E = (String) word.get("TEMPLATE_ZM_E");
			String templateFM_E = (String) word.get("TEMPLATE_FM_E");
			long countZM = 0;
			long countFM = 0;
			long countZM_E = 0;
			long countFM_E = 0;
			String format = "text:%s";
			if (!StringUtils.isEmpty(queryWord)) {
				format = "text: " + queryWord + " AND (%s)";
			}
			// 获取正面信息数
			if (!StringUtils.isEmpty(templateZM)) {
				countZM = solrTool.count(String.format(format, templateZM), startTime, endTime);
			}
			// 获取负面信息数
			if (!StringUtils.isEmpty(templateFM)) {
				countFM = solrTool.count(String.format(format, templateFM), startTime, endTime);
			} // 获取正面信息数
			if (!StringUtils.isEmpty(templateZM_E)) {
				countZM_E = solrTool.count(String.format(format, templateZM_E), startTime, endTime);
			}
			// 获取负面信息数
			if (!StringUtils.isEmpty(templateFM_E)) {
				countFM_E = solrTool.count(String.format(format, templateFM_E), startTime, endTime);
			}

			// 将数据入库
			if (0 != countZM || 0 != countFM) {
				Map<String, Object> obj = new HashMap<String, Object>();
				obj.put("TYPE_ID", typeId);
				obj.put("TYPE_NAME", word.get("TYPE_NAME"));
				obj.put("SIZE_ZM", countZM);
				obj.put("SIZE_FM", countFM);
				obj.put("SIZE_ZM_E", countZM_E);
				obj.put("SIZE_FM_E", countFM_E);
				result.add(obj);
			}
		}

		return result;
	}
	
	//XXX 需要修改成直接取值
    private long getSiteCount(Integer typeId, String templateType, SolrTools solrTool, Map<String, Map<String, Object>> sites,
			String template, Date beginTime, Date endTime) throws Exception {
        if (StringUtils.isEmpty(template)) {
			return 0;
		}
		long total = 0;
		long saveCount = 0;
		int start = 0;
		int size = 1000;
		MD5 encoder = new MD5();
		while (true) {
			QueryResponse resp = solrTool.query("text:" + template, "host,title,url,tstamp,content", beginTime, endTime, null,
					start, size);
			SolrDocumentList datas = resp.getResults();
			if (null != datas) {// 没有取完，还要继续
				for (SolrDocument doc : datas) {
				    total++;
                    String host = String.valueOf(doc.getFieldValue("host"));
                    String url = String.valueOf(doc.getFieldValue("url"));
                    String content = String.valueOf(doc.getFieldValue("content"));
                    // 判断url是否是目录（以“/”结束），如果是目录，则跳过
                    if (StringUtils.isEmpty(url) || url.endsWith("/") 
                      || url.endsWith("/index.html")|| url.endsWith("/index.htm")
                      || url.endsWith("/index.php") || url.endsWith("/index.jsp")
                      || url.endsWith("/index.asp")){
                        continue;
                    }
					// 从host中解析站点
					Map<String, Object> siteInfo = getSiteIdByHost(sites, host);
					// 没有匹配上站点，则跳过
					if (null == siteInfo){
					    continue;
					}
					int siteId = Integer.parseInt(String.valueOf(siteInfo.get("SITE_ID")));
					if (-1 == siteId){
                        continue;
                    }
					// 保存明细到数据库
					// 判断对应的url是否已经存在，如果不存在则添加
					String urlEncode = new String(encoder.encrypt(url.getBytes()));
					String flag = siteMapper.checkUrl(urlEncode);
					if (StringUtils.isEmpty(flag)) {
					    saveCount++;
					    Date dt = (Date) doc.getFieldValue("tstamp");
					    // 从正文中提取作者、来源、时间
					    Date publishTime = getDate(content);
					    if (null == publishTime || publishTime.after(dt)){
					        publishTime = dt;
					    }
                        String source = null;
                        String author = null;
                        String editor = null;
                        if ("ZH".equals(siteInfo.get("LANG"))){
                            source = getSource(content);
                            author = getAuthor(content);
                            editor = getEditor(content);
                        }else{
                            source = getSourceE(content);
                            author = getAuthorE(content);
                            editor = getEditorE(content);
                        }
                        if (!StringUtils.isEmpty(source) && 128 < source.length()){
                            source = source.substring(0,128);
                        }
                        if (!StringUtils.isEmpty(author) && 128 < author.length()){
                            author = author.substring(0,128);
                        }
                        if (!StringUtils.isEmpty(editor) && 128 < editor.length()){
                            editor = editor.substring(0,128);
                        }
                        try{
                            siteMapper.saveDetail(typeId, templateType, siteId, String.valueOf(doc.getFieldValue("title")),
						        url, new java.sql.Date(dt.getTime()),source,author,editor,
						        new java.sql.Date(publishTime.getTime()),urlEncode);
                        }catch(SQLException e){
                            LOG.warn(e.getMessage(),e);
                        }
					}
				}
				if (size == datas.size()) {
					start += size;
					continue;
				}
			}
			break;
		}
		
		LOG.info("typeId:" + typeId + ", templateType:" + templateType + ", total:" + total + ", saveCount:" + saveCount);
		return saveCount;
	}
//    private void getSiteCount_bak(Integer typeId, String templateType, SolrTools solrTool, Map<String, Integer> sites,
//            String template, Date beginTime, Date endTime, Map<Integer, Long> siteCount) throws Exception {
//        if (StringUtils.isEmpty(template)) {
//            return;
//        }
//        long total = 0;
//        long saveCount = 0;
//        int start = 0;
//        int size = 1000;
//        MD5 encoder = new MD5();
//        while (true) {
//            QueryResponse resp = solrTool.query("text:" + template, "host,title,url,tstamp,content", beginTime, endTime, null,
//                    start, size);
//            SolrDocumentList datas = resp.getResults();
//            if (null != datas) {// 没有取完，还要继续
//                for (SolrDocument doc : datas) {
//                    total++;
//                    String host = String.valueOf(doc.getFieldValue("host"));
//                    String url = String.valueOf(doc.getFieldValue("url"));
//                    String content = String.valueOf(doc.getFieldValue("content"));
//                    // 判断url是否是目录（以“/”结束），如果是目录，则跳过
//                    if (StringUtils.isEmpty(url) || url.endsWith("/") 
//                      || url.endsWith("/index.html")|| url.endsWith("/index.htm")
//                      || url.endsWith("/index.php") || url.endsWith("/index.jsp")
//                      || url.endsWith("/index.asp")){
//                        continue;
//                    }
//                    // 从host中解析站点
//                    int siteId = getSiteIdByHost(sites, host);
//
//                    // 保存明细到数据库
//                    // 判断对应的url是否已经存在，如果不存在则添加
//                    String flag = siteMapper.checkUrl(new String(encoder.encrypt(url.getBytes())));
//                    if (StringUtils.isEmpty(flag)) {
//                        saveCount++;
//                        Date dt = (Date) doc.getFieldValue("tstamp");
//                        // 从正文中提取作者、来源、时间
//                        Date publishTime = getDate(content);
//                        if (null == publishTime || publishTime.after(dt)){
//                            publishTime = dt;
//                        }
//                        siteMapper.saveDetail(typeId, templateType, siteId, String.valueOf(doc.getFieldValue("title")),
//                                url, dt,getSource(content),getSource(content),getEditor(content),
//                                publishTime,new String(encoder.encrypt(url.getBytes())));
//                        // 记录次数
//                        Long count = siteCount.get(siteId);
//                        if (null != count) {
//                            siteCount.put(siteId, count + 1);
//                        } else {
//                            siteCount.put(siteId, 1l);
//                        }
//                    }
//                }
//                if (size == datas.size()) {
//                    start += size;
//                    continue;
//                }
//            }
//            break;
//        }
//        
//        LOG.info("typeId:" + typeId + ", templateType:" + templateType + ", total:" + total + ", saveCount:" + saveCount);
//    }
    private static final Pattern PATTERN_SOURCE = Pattern.compile("(来源)[：:]\\s*(\\S+)");
    private static final Pattern PATTERN_AUTHOR = Pattern.compile("(作者)[：:]\\s*([0-9a-zA-Z\u4e00-\u9fa5]+)");
    private static final Pattern PATTERN_EDITOR = Pattern.compile("(编辑|责编)[：:]\\s*([0-9a-zA-Z\u4e00-\u9fa5]+)");
    private static final Pattern PATTERN_SOURCE_E = Pattern.compile("(来源)[：:]\\s*(\\S+)");
    private static final Pattern PATTERN_AUTHOR_E = Pattern.compile("\\s(By)(( [A-Z][0-9a-z]+)+)");
    private static final Pattern PATTERN_EDITOR_E = Pattern.compile("(编辑|责编)[：:]\\s*([0-9a-zA-Z\u4e00-\u9fa5]+)");
//    private static final Pattern PATTERN_SOURCE = Pattern.compile("来源[：:]\\s{0,1}(\\S*)\\s");
//    private static final Pattern PATTERN_AUTHOR = Pattern.compile("作者[：:]\\s{0,1}([0-9a-zA-Z\u4e00-\u9fa5]*)\\s");
//    private static final Pattern PATTERN_EDITOR = Pattern.compile("(编辑|责编)[：:]\\s{0,1}([0-9a-zA-Z\u4e00-\u9fa5]*)\\s");
    private static final Pattern[] PATTERN_DATES = {
            Pattern.compile("(\\d{4}/\\d{1,2}/\\d{1,2})"),
            Pattern.compile("(\\d{4}-\\d{1,2}-\\d{1,2})"),
            Pattern.compile("(\\d{4}年\\d{1,2}月\\d{1,2}日)"),
            Pattern.compile("(\\d{1,2}月\\d{1,2}日, \\d{4})"),
            Pattern.compile("(\\d{1,2}\\s+(Dec|Nov|Oct|Sep|Aug|Jul|Jun|May|Apr|Mar|Feb|Jan)\\s+\\d{4})"),
            Pattern.compile("(\\d{1,2}\\s+(December|November|October|September|August|July|June|May|April|March|February|January)\\s+\\d{4})"),
            Pattern.compile("((December|November|October|September|August|July|June|May|April|March|February|January)\\s+\\d{1,2},\\s+\\d{4})"),
            Pattern.compile("((Dec|Nov|Oct|Sep|Aug|Jul|Jun|May|Apr|Mar|Feb|Jan)\\.\\s+\\d{1,2},\\s+\\d{4})")};
    public static final SimpleDateFormat[] DATE_FORMATS = {
            new SimpleDateFormat("yyyy/MM/dd"),
            new SimpleDateFormat("yyyy-MM-dd"),
            new SimpleDateFormat("yyyy年MM月dd日"),
            new SimpleDateFormat("MM月dd日, yyyy"),
            new SimpleDateFormat("d MMM yyyy",new Locale("en")),
            new SimpleDateFormat("d MMMM yyyy",new Locale("en")),
            new SimpleDateFormat("MMMM d, yyyy",new Locale("en")),
            new SimpleDateFormat("MMM. d, yyyy",new Locale("en")),
    };
    static final String getSource(String content){
        Matcher m = PATTERN_SOURCE.matcher(content);
        if (m.find()){
            return m.group(2);
        }else{
            return null;
        }
    }
    static final String getSourceE(String content){
        Matcher m = PATTERN_SOURCE_E.matcher(content);
        if (m.find()){
            return m.group(2);
        }else{
            return null;
        }
    }
    static final String getAuthor(String content){
        Matcher m = PATTERN_AUTHOR.matcher(content);
        if (m.find()){
            return m.group(2);
        }else{
            return null;
        }
    }
    static final String getAuthorE(String content){
        Matcher m = PATTERN_AUTHOR_E.matcher(content);
        if (m.find()){
            return m.group(2);
        }else{
            return null;
        }
    }
    static final String getEditor(String content){
        Matcher m = PATTERN_EDITOR.matcher(content);
        if (m.find()){
            return m.group(2);
        }else{
            return null;
        }
    }
    static final String getEditorE(String content){
        Matcher m = PATTERN_EDITOR_E.matcher(content);
        if (m.find()){
            return m.group(2);
        }else{
            return null;
        }
    }
    static final Date getDate(String content){
        int s = Integer.MAX_VALUE;
        Date ret = null;
        for (int i = 0; i < PATTERN_DATES.length; i++) {
            Matcher m = PATTERN_DATES[i].matcher(content);
            if (m.find()){
                if (m.start() < s){
                    s = m.start();
                    try {
                        ret = DATE_FORMATS[i].parse(m.group(1));
                    } catch (ParseException e) {
                        LOG.error(e.getMessage());
                    }
                }
            }
        }
        return ret;
    }
    private static Map<String, Object> getSiteIdByHost(Map<String, Map<String, Object>> sites, String host) {
		while (true) {
			if (sites.containsKey(host)) {
				Map<String, Object> info = sites.get(host);
				if (null != info) {
					return info;
				} else {
					return null;
				}
			}
			int index = host.indexOf(".");
			if (index < 0 || host.length() == 1) {
				break;
			}
			host = host.substring(index + 1);
		}
		return null;
	}
    //private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 获取指定时间内指定词条进行统计。<br>
	 * 如果typeId为null，则表示所有词条按词条统计
	 * 
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @param typeId
	 *            词条编号
	 * @return 统计结果。分词条统计
	 */
	public List<Map<String, Object>> statWordsCount(Date startTime, Date endTime, Integer typeId) {
	    //LOG.info("startTime:"+DATE_FORMAT.format(startTime) + "  endTime: " + DATE_FORMAT.format(endTime));
		return siteMapper.selectStatsCount(new java.sql.Date(startTime.getTime()), new java.sql.Date(endTime.getTime()), typeId);
	}

	/** 获取语言列表 */
	public List<Map<String, Object>> selectLanguage() {
		return solrMapper.selectLanguage();
	}

	/** 获取国家 */
	public List<Map<String, Object>> selectCountry() {
		return solrMapper.selectCountry();
	}

	public List<Map<String, Object>> siteTop(Date startTime, Date endTime, Integer typeId, int topNum) {
		return siteMapper.selectSiteTop(new java.sql.Date(startTime.getTime()), new java.sql.Date(endTime.getTime()), typeId,topNum);
	}
	public List<Map<String, Object>> selectWordsTop(Date startTime, Date endTime, int topNum) {
		return siteMapper.selectWordsTop(new java.sql.Date(startTime.getTime()), new java.sql.Date(endTime.getTime()), topNum);
	}

	public List<Map<String, Object>> selectHotwordTop(Date startTime, Date endTime, int topNum,String flag) {
		return siteMapper.selectHotwordTop(new java.sql.Date(startTime.getTime()), new java.sql.Date(endTime.getTime()), topNum,flag);
	}
	public List<Map<String, Object>> statMediaCount(Date startTime, Date endTime, Integer typeId) {
		return siteMapper.selectMediaCount(new java.sql.Date(startTime.getTime()), new java.sql.Date(endTime.getTime()),typeId);
	}

	public List<Map<String, Object>> statMedia(Date startTime, Date endTime, Integer typeId) {
		return siteMapper.selectMedia(new java.sql.Date(startTime.getTime()), new java.sql.Date(endTime.getTime()),typeId);
	}

	public List<Map<String, Object>> statWordsCountAll(Date startTime, Date endTime, Integer typeId) {
		return siteMapper.selectStatsCountAll(new java.sql.Date(startTime.getTime()), new java.sql.Date(endTime.getTime()),typeId);
	}

	/**
	 * 查询文章内容。根据时间倒序
	 * 
	 * @param start
	 *            开始索引号。从0开始计数
	 * @param rows
	 *            获取的记录数
	 * @return 结果
	 */
	public List<Map<String, Object>> query(Integer typeId, Integer start, Integer rows) {
		start = null != start ? start : 0;
		rows = null != rows ? rows : 10;
		// {total: 100,rows:[]}
		return siteMapper.selectDetail(typeId, start, rows);
	}

	// XXX 需要修改发布时间 publish_time
	public List<Map<String, Object>> queryFromSolr(Integer start, Integer rows) {
		start = null != start ? start : 0;
		rows = null != rows ? rows : 10;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		SolrTools solrTool = new SolrTools(userService.getConfig(-1, CONFIG_SOLR_URL));
		try {
			QueryResponse resp = solrTool.querySortByTime(null, "title,url,tstamp", null, null, ORDER.desc, start,
					rows);
			SolrDocumentList docs = resp.getResults();
			for (SolrDocument doc : docs) {
				HashMap<String, Object> row = new HashMap<String, Object>();
				result.add(row);
				row.put("title", doc.getFieldValue("title"));
				row.put("url", doc.getFieldValue("url"));
				Date tstamp = (Date) doc.getFieldValue("tstamp");
				row.put("tstamp", sdf.format(tstamp));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		// {total: 100,rows:[]}
		return result;
	}

	/**
	 * 根据语言、国家、时间等条件统计词条信息
	 * 
	 * @param start
	 *            开始时间
	 * @param end
	 *            结束时间
	 * @param langId
	 *            语言编号
	 * @param countryId
	 *            国家编号
	 * @param siteId
	 * @param siteTypeId
	 * @return
	 */
	public List<Map<String, Object>> statWordsCountQuery(Date start, Date end, Integer langId, Integer countryId,
			Integer siteTypeId, Integer siteId) {
		return siteMapper.statWordsCountQuery(new java.sql.Date(start.getTime()), new java.sql.Date(end.getTime()), langId, countryId, siteTypeId, siteId);
	}

	public List<Map<String, Object>> statWordsQueryDetail(Date startTime, Date endTime, Integer langId,
			Integer countryId, Integer siteTypeId, Integer siteId, Integer wordsId,Integer start, Integer rows) {
		start = null != start ? start : 0;
		rows = null != rows ? rows : 10;
		return siteMapper.statWordsQueryDetail(new java.sql.Date(startTime.getTime()), new java.sql.Date(endTime.getTime()), langId, countryId, siteTypeId, siteId, wordsId,start, rows);
	}
	public int countWordsQueryDetail(Date startTime, Date endTime, Integer langId, Integer countryId, Integer siteTypeId,
			Integer siteId, Integer wordsId) {
		return siteMapper.countWordsQueryDetail(new java.sql.Date(startTime.getTime()), new java.sql.Date(endTime.getTime()), langId, countryId, siteTypeId, siteId, wordsId);
	}
	/**
	 * 根据站点类型状态获取站点类型列表
	 * 
	 * @param isValid
	 *            站点类型状态。1 有效；0 无效。如果是null则表示所有
	 * @return
	 */
	public List<Map<String, Object>> selectSiteType(String isValid) {
		return solrMapper.selectSiteType(isValid);
	}

	public List<Map<String, Object>> selectSite(Integer siteTypeId, String isValid) {
		return solrMapper.selectSite(siteTypeId, isValid);
	}

	/** 预警 */
	public int alert() {
		Calendar endTime = Calendar.getInstance();
		Calendar startTime = Calendar.getInstance();
		startTime.set(Calendar.DAY_OF_MONTH, 0);
		startTime.set(Calendar.HOUR_OF_DAY, 0);
		startTime.set(Calendar.MINUTE, 0);
		startTime.set(Calendar.SECOND, 0);

		List<Map<String, Object>> statCounts = siteMapper.selectStatsCount(new java.sql.Date(startTime.getTimeInMillis()), new java.sql.Date(endTime.getTimeInMillis()),
				null);
		if (null == statCounts || statCounts.isEmpty()) {
			return 0;
		}
		Map<String, Integer> counts = new HashMap<String, Integer>();
		for (Map<String, Object> map : statCounts) {
			counts.put(String.valueOf(map.get("TYPE_ID")), Integer.parseInt(String.valueOf(map.get("SIZE_FM")))
					+ Integer.parseInt(String.valueOf(map.get("SIZE_FM_E"))));
		}
		// 获取预警配置列表
		List<Map<String, Object>> alertList = businessSettingMapper.selectAlertList();
		// 逐个处理预警配置
		int count = 0;
		for (Map<String, Object> alertInfo : alertList) {
			String metrics = String.valueOf(alertInfo.get("METRICS"));
			if (!StringUtils.isEmpty(metrics)) {
				String[] typeIds = metrics.split(Pattern.quote("$$$"))[0].split(",");
				int value = 0;
				for (int i = 0; i < typeIds.length; i++) {
					if (counts.containsKey(typeIds[i])) {
						value += counts.get(typeIds[i]);
					}
				}

				// 判断是否超过预警值
				int threhold = Integer.valueOf(String.valueOf(alertInfo.get("ALERT_VALUE")));
				if (value >= threhold) {
					// 达到预警，则生成预警信息
					solrMapper.saveAlertLog(Integer.valueOf(String.valueOf(alertInfo.get("ALERT_ID"))), threhold,
							value);
					count++;
				}

			}
		}
		return count;
	}

	public List<Map<String, Object>> selectAlertLog(Date startTime, Date endTime, String notifyStatus,
			String alertType, int start, Integer rows) {
		rows = null == rows ? Integer.MAX_VALUE : rows;
		return solrMapper.selectAlertLog(startTime, endTime, notifyStatus, alertType,start,rows);
	}
	public int countAlertLog(Date startTime, Date endTime, String notifyStatus,
			String alertType) {
		return solrMapper.countAlertLog(startTime, endTime, notifyStatus, alertType);
	}
	public List<Map<String, Object>> selectWeekly(Integer year, Integer month, Integer week) {
		return solrMapper.selectWeekly(year, month, week);
	}

	public Map<String, Object> selectWeeklyById(Integer id) {
		return solrMapper.selectWeeklyById(id);
	}

	public void saveWeekly(int year, int month, int week, String filePath, String fileName, long size,
			String contentType, int userId) {
		solrMapper.saveWeekly(year, month, week, filePath, fileName, size, contentType, userId);
	}

	public void deleteWeeklyById(Integer id) {
		solrMapper.deleteWeeklyById(id);
	}

	/**
	 * 指定时间内词条预警TopN
	 * 
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @param topNum
	 *            TopN
	 * @return
	 */
	public List<Map<String, Object>> selectWordsAlertTop(Date startTime, Date endTime, int topNum) {
		return solrMapper.selectWordsAlertTop(startTime, endTime, topNum);
	}

	public int alertProcess() {
		// 获取预警通知信息
		int count = 0;
		String tmplTitle = "【${ALERT_NAME}】预警";
		String tmplContent = "${NOTIFIER}：\n    您好。\n    您监控的词条【${WORDS}】达到预警条件！预警值：${ALERT_VALUE},当前值：${CURRENT_VALUE}";
		Calendar now = Calendar.getInstance();
		Calendar start = Calendar.getInstance(); // 当月
		start.set(Calendar.DAY_OF_MONTH, 1);
		start.set(Calendar.HOUR_OF_DAY, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		Date startTime = start.getTime();
		Date endTime = now.getTime();
		List<Map<String, Object>> alertLogs = selectAlertLog(startTime, endTime, "0", null,0,null);
		for (int i = 0; i < alertLogs.size(); i++) {
			Map<String, Object> alertLog = alertLogs.get(i);
			String notifiers = String.valueOf(alertLog.get("NOTIFIERS"));
			String alertType = String.valueOf(alertLog.get("ALERT_TYPE"));
			String metrics = String.valueOf(alertLog.get("METRICS"));
			String arr[] = metrics.split(Pattern.quote("$$$"), 2);
			if (arr.length == 2) {
				alertLog.put("WORDS", arr[1]);
			} else {
				alertLog.put("WORDS", arr[0]);
			}

			String notifierArray[] = notifiers.split(Pattern.quote("$$$"), 2);
			if (notifierArray.length == 2) {
				String[] notifierIds = notifierArray[0].split(",");
				// String[] notifierNames = notifierArray[1].split(",");
				for (int j = 0; j < notifierIds.length; j++) {
					Integer userId = null;
					try {
						userId = Integer.parseInt(notifierIds[j]);
					} catch (NumberFormatException e) {
						LOG.warn("用户编码不是数字：" + notifierIds[j]);
						continue;
					}
					if (userId != null) {
						User user = userService.getUserById(userId);
						if (null != user) {
							alertLog.put("NOTIFIER", user.getNickName());
							String title = TemplateParser.parse(alertLog, tmplTitle);
							String content = TemplateParser.parse(alertLog, tmplContent);
							if ("1".equals(alertType)) {// 邮件
								if (notifyByEmail(user.getEmail(), title, content)){
								    ++count;
								}
							} else if ("2".equals(alertType)) {// 短信
								if(notifyBySms(user.getPhoneNumber(), title, content)){
								    ++count;
								}
							}
						}
					}
				}
			}
			
			// 通知完了就更新状态
			solrMapper.updateAlertLogNotified((Long)alertLog.get("ID"), count == 0? 2:1);
		}

		return count;
	}

	private boolean notifyBySms(String phoneNumber, String title, String content) {
		if (!StringUtils.isEmpty(phoneNumber) && !StringUtils.isEmpty(content)) {
			LOG.info("发送短信:" + content);
			return true;
		}else{
		    return false;
		}
	}

	private boolean notifyByEmail(String email, String title, String content) {
		if (!StringUtils.isEmpty(email) && !StringUtils.isEmpty(content)) {
			return mailService.sendMail(title, content, email);
		}else{
		    return false;
		}
	}

	// XXX 需要重点修改热词聚合
	public long generateHot() throws Exception {
		SolrTools solrTool = new SolrTools(userService.getConfig(-1, CONFIG_SOLR_URL));
		Date endTime = new Date();
		Date beginTime = new Date(endTime.getTime() - STAT_TIME_DEFAULT);
		int count0 = doGenerateHot("0",solrTool,beginTime,endTime);
		int count1 = doGenerateHot("1",solrTool,beginTime,endTime);
		
		return count0 + count1;
	}
	private static class Word{
		final String word;
		long heat;
		public Word(String word, long heat) {
			super();
			this.word = word;
			this.heat = heat;
		}
	}
	private int doGenerateHot(String flag, SolrTools solrTool, Date beginTime, Date endTime) {
		if (StringUtils.isEmpty(flag)){
			return 0;
		}
		String configSelector = userService.getConfig(-1, "SELECTOR_4_HOT_FLAG_" + flag);
		String url = userService.getConfig(-1, "URL_4_HOT_FLAG_" + flag);
		String defaultCharsetName = userService.getConfig(-1, "CHARSET_4_HOT_FLAG_" + flag);
		PageFetcher fetcher = new PageFetcher(defaultCharsetName);
		String content = fetcher.fetchPageContent(url);
		Document document = Jsoup.parse(content);
		Elements nodes = document.select(configSelector);
		if (null == nodes){
			return 0;
		}
		List<Word> words = new ArrayList<Word>();
        for (int i = 0; i < nodes.size(); i++) {
            Element e = nodes.get(i);
            words.add(new Word(e.text(), 100 / (i+1)));
        }
        // 在本地试搜以重新排名
        for (Word word : words) {
        	List<String> terms = analyzer.getWords(word.word);
        	if (terms.isEmpty()){
        		continue;
        	}
        	StringBuilder msg = new StringBuilder();
        	for (int i = 0; i < terms.size(); i++) {
				if (i != 0){
					msg.append(" AND ");
				}
				msg.append(terms.get(i));
			}
			try {
				long cnt = solrTool.count("text:" + msg.toString(), beginTime, endTime);
				word.heat += cnt;
			} catch (Exception e) {
				LOG.error(e.getMessage(),e);
			}
		}
        
        // 保存结果
        if (words.size() > 0){
        	//先清除原有的
        	//siteMapper.saveHotHis(flag);
        	siteMapper.deleteHot(flag);
	        for (Word word : words) {
				siteMapper.saveHot(flag,word.word,word.heat);
			}
	        
	        return words.size();
        }else{
        	return 0;
        }
	}

	/**
	 * 对输入文本进行分词
	 * @param words 带分词文本
	 * @return 分词后的结果列表。不会返回null
	 */
	public List<String> parseWords(String words) {
		return analyzer.getWords(words);
	}

	public List<Map<String, Object>> selectHot(Date startTime, Date endTime, String flag, Integer start, Integer rows) {
		start = null != start ? start : 0;
		rows = null != rows ? rows : 10;
		return siteMapper.selectHot(new java.sql.Date(startTime.getTime()), new java.sql.Date(endTime.getTime()), flag, start, rows);
	}

	public int countHot(Date startTime, Date endTime, String flag) {
		return siteMapper.countHot(new java.sql.Date(startTime.getTime()), new java.sql.Date(endTime.getTime()), flag);
	}

    public List<String> queryWords(String word) {
        return siteMapper.queryWords(word);
    }
	public static void main(String[] args) {
        String[] contents = {"四中全会到五中全会，省部级一把手换了13个-搜狐评论 首页 - 新闻 - 军事 - 文化 - 历史 - 体育 - NBA - 视频 - 娱乐 - 财经 - 股票 - 科技 - 汽车 - 房产 - 时尚 - 健康 - 教育 - 母婴 - 旅游 - 美食 - 星座 > 时政评论 搜狐评论 > 时政评论 国内 | 国际 | 社会 | 军事 | 评论 四中全会到五中全会，省部级一把手换了13个 正文 我来说两句 ( 人参与) 扫描到手机 关闭 2015-11-02 08:28:08 来源： 综合 作者：周宇 邹春霞 手机看新闻 保存到博客 大 | 中 | 小 打印 　　撰文 | 周宇 邹春霞 　　十八届五中全会上的人事变动，并不像人们想象的那么大。而其实，许多变动早在之前就已展开。 　　政知局（微信ID：bqzhengzhiju）小编梳理发现，相比此前，四中全会到五中全会这一年，人事调整更显频繁。5个省部级单位换了“一把手”，地方上则有8个省份的党政“一把手”有所调整，而副部级干部的变动那就更多了。 　　 调整 　　新换的13名主帅都是谁？ 　　从去年10月召开十八届四中全会至今，已有5个部门更换了“一把手”，包括文化部、环保部、中央统战部、国家统计局和安监总局。 　　去年12月，中宣部原常务副部长雒树刚调任文化部党组书记、部长，他今年60岁，原部长蔡武2014年10月满65岁，现任全国政协外事委员会副主任。 　　同年12月的最后一天，国务院宣布中央政治局委员孙春兰兼任中央统战部部长，原部长令计划已被双开。 　　另外三位调整的“一把手”系今年调任。1月，51岁的清华大学原校长陈吉宁接棒已到退休年龄的周生贤，出任环保部党组书记，2月开始任环保部部长。 　　国家统计局则是今年5月换的帅，王保安任国家统计局局长，他此前为财政部副部长。国家统计局原局长马建堂四中全会时由中央候补委员增补为中央委员，已于今年4月调任国家行政学院常务副院长。 　　在五中全会开幕前，10月中旬，公安部原副部长杨焕宁调任国家安全生产监督管理总局任党组书记、局长。原局长杨栋梁今年8月已落马。 　　政知局（微信ID：bqzhengzhiju）小编梳理发现，虽然部委“一把手”的变动只有5人，但副部级干部调动频繁。除了各大部委几乎都有调整以外，“一行三会”、国务院副秘书长、国新办、央视、央广、新华社等都有副部级以上干部调整。 　　地方党政领导方面，政知局（微信ID：bqzhengzhiju）小编注意到，十八届二中全会后全国31个省份的省级党政一把手调整基本到位。此后至四中全会召开时，仅有山西、吉林两省受反腐影响省委党政一把手有所调整。其中，吉林省原省委书记王儒林同岗调任山西，整顿因塌方式腐败严重受损的山西省委常委班子；山西省原省委书记袁纯清则调任中央农村工作领导小组副组长。吉林省党政一把手受此影响联动调整，原省长巴音朝鲁接手省委书记一职，省长一职则由中国农行调任的金融专家蒋超良接任。 　　而十八届四中全会至五中全会期间，据政知局（微信ID：bqzhengzhiju）小编统计，贵州、辽宁、河北、海南、安徽、云南、新疆、天津等8个省份的省级党委书记或省级行政长官有所调整。鉴于苏树林已落马，福建省长换人也是迟早的事情。 　　调整的原因多样：有的是源于反腐的连带效应，如天津、贵州。天津市原市委书记孙春兰补缺统战部后，由此造成天津市长黄兴国代理市委书记一职近一年；贵州原省委书记赵克志也因周本顺落马调任河北补缺。 　　有的是新老交替引发的正常调整，如辽宁、安徽，王珉、张宝顺到龄退居二线，两省党政一把手也因此换新；也有如云南，2014年底，时年64岁的云南省原省委书记秦光荣提前退居二线，调任全国人大，继而引发调整。 　　出现人事变动的8个省份中，海南省的调整略显特别。海南省原省长蒋定之，“罕见”回乡就任江苏省省人大常委会党组书记、副主任。其省长一职由国家海洋局原局长刘赐贵“空降”海南接任。 　 　部委 　　工信部发改委人事变动频繁 　　纵观1年来的人事调整，政知局（微信ID：bqzhengzhiju）小编发现，2个部委的副部级干部变动较为频繁――工信部和发改委。 　　工信部目前1正9副的领导班子格局中，有5位副部长是过去1年调整的。最早的调整发生在今年1月，工信部办公厅原主任莫玮任党组成员。紧接着的2月，北京航空航天大学原校长怀进鹏调任工信部任副部长、党组成员。7月，青海省原副省长辛国斌到任。此后，同样在10月，五中全会前夕，工信部再来两位副部长，分别是湖南省委原常委陈肇雄和原产业政策司司长冯飞。 　　国家发改委1年来同样有5位副部级干部履新。除了任建华任党组成员，负责纪检以外，另外4人都还有副主任的职务，其中3人是正部长级，只有发改委原副秘书长王晓涛是副部级。3位正部长级的副主任分别是国务院原副秘书长、国家食品药品监管总局原局长张勇，新疆自治区原副书记、政府主席努尔?白克力，国务院研究室原党组书记、主任宁吉础４送猓努尔?白克力还兼任国家能源局局长，福建省原副省长郑栅洁8月调任能源局任副部长级副局长。被免去职务的原副主任朱之鑫、解振华、吴新雄均是1949年生人，年龄已满，原副主任徐宪平1954年生人，今年也已61岁，4人都应因年龄原因卸任。 　　去年年底以来，宣传系统人事变动频繁，包括中宣部、网信办、国新办等国家宣传管理机构，还有新华社、央视、央广等国家级媒体的负责人。 　　 地方 　　履新省级政府一把手“老将”居多 　　在一些省份，党委书记未最后确定，经常会有“代理书记”的情况。 　　人民网舆情监测室常务副秘书长单学刚曾统计过，改革开放以来，共有8个省（自治区、直辖市）出现过10次党委代理书记（第一书记）。 　　政知局（微信ID：bqzhengzhiju）小编注意到，在此前出现过的代理书记中，代理时间最长的是新疆的王乐泉，其代理长达15个月，但这段代理发生在20年前。而在最近十年，福建、西藏、上海等三地曾出现代理书记的情况，其中代理时间最长的是卢展工，他于2004年2月代理福建省委书记，10个月后转正。 　　较为特别的是天津，天津市市长黄兴国从2014年12月代理市委书记至今近一年，已超过卢展工，成为近十年来代理书记时间最长的一位。 　　而一年来，贵州、辽宁、河北、安徽、云南、天津等6个省份在此前1年间调整的是省（市）委书记。其中，贵州、辽宁、安徽、云南都是由原省长接任，属正常调整。 　　相比省级党委书记，此前一年对省级政府一把手的调整出现了“老将”居多。 　　五中全会前的最新一例省级政府主官调整，出现在贵州。贵州省第十二届人大常委会第十八次会议10月16日决定，接受陈敏尔辞去贵州省省长职务的请求，任命孙志刚为贵州省副省长、代省长。孙志刚是位“老同志”，以61岁“高龄”履任地方主政长官，这在以往非常少见。 　　这是今年以来第二例。此前一例是今年6月接任辽宁省省长的陈求发，他履新时也已超过60岁。更早一例是接替努尔?白克力出任新疆自治区主席的雪克来提?扎克尔，2014年12月以61岁“高龄”代理省级政府一把手，之后转正。 　　政知局（微信ID：bqzhengzhiju）小编注意到，四中全会以来履新的省长们都不太年轻，海南刘赐贵、云南陈豪履新时也都在60岁左右，履新年龄最小的是安徽李锦斌，接任省长时为57岁。贵州、海南、云南、辽宁等4地履新的政府主官年龄都比党委书记大，年龄差最大的如贵州，贵州省委书记陈敏尔是为“60后”，比孙志刚小6岁。   http://star.news.sohu.com/20151102/n424900992.shtml star.news.sohu.com true 综合 周宇 邹春霞 http://star.news.sohu.com/20151102/n424900992.shtml report 3731 撰文|周宇邹春霞十八届五中全会上的人事变动，并不像人们想象的那么大。而其实，许多变动早在之前就已展开。政知局（微信ID：bqzhengzhiju）小编梳理发现， (责任编辑：UN656) 原标题：四中全会到五中全会，省部级一把手换了13个 分享： [保存到博客] 手机看新闻 本文相关推荐 16名省部级参加五中全... 18届四中全会内容 三中四中五中全会区别 十三届四中全会 五中全会或出现六位省长 房辉峰大闹四中全会 一把手接受纪委全会廉检 十八届四中全会决定全文 十八届四中全会依法治... 什么是四中全会、五中全会 三中、四中、五中、六中 党的十八届四中全会精神 丈夫中820多万大奖妻子领奖    双色球头奖17注607万    茂名彩民揽双色球4107万    体彩开奖 相关新闻 相关推荐 15-11-02 中共全国人大常委会党组召开会议学习贯彻党的十八 15-11-02 盘点四中全会到五中全会人事调整:五部委换帅(图) 15-11-02 五中全会引热议 各界聚焦创新发展 15-11-02 五中全会公报解读:创新提升至国家发展全局核心位 15-11-01 从十八届五中全会看未来五年发展思路 15-11-01 新华社评论员:坚持绿色发展 建设美丽中国 三论学 更多关于 五中 全会 的新闻>> 一中二中三中四中... 十八届四中全会内容 十八届四中全会内... 十八届四中全会主... 十八届四中全会考... 二中、三中、四中... 我要发布 热词： 灭火之行 纸尿裤求婚 野猪越狱 机器人伴娘 热剧： 大好时光 云中歌 新济公活佛 三个奶爸 大秧歌 热点推荐 更多>> 哪些省部级官员胆大“妄议中央”？ 从GDP“破7”解读中国经济 中央领导人遇突发事件如何化逦梗？ 流量不清零后“消耗快”，谁来监管 荷兰王室德法领导人相继访华 中欧为何密集互动 名家专栏 更多>> 高官杀情妇 枪中有冤案 赵黎平以不那么娴熟的枪法，深夜划破宁静，才能摊开来谈…[ 详细 ] 　　　 风过耳： 从通奸到吃里扒外 中纪委爱\"新词\" 西格： 强化家庭不必让妇女回家 独家策划 二号人物崔龙海下台 铁打的老大，流水的老二，老二流动的速度要看老大的心情…[ 详细 ] 朝鲜“二号人物”崔龙海：且上任且珍惜 金正恩一“卡位”，朴槿惠就中枪 朝鲜公主金正恩妹妹浮出水面 成二号人物 边和韩国对话边放导弹 金正恩真乖了？ 朝鲜影视，徘徊于人性与政治之间 热点视频 影视剧 综艺 自媒体 娱乐播报 | 章泽天承认婚礼前怀孕 大肚照曝光 笑傲江湖 | 郭德纲嘟嘴卖萌 云中歌 | 陵云夫妇深情相吻羡煞旁人 老总起贪念盗窃百万豪车 醉驾男连撞6车称随便罚 团伙强迫12岁少女卖淫 实拍特警徒手夺刀救人质 6岁女孩心脏肠道长体外 忧郁鹦鹉自残拔掉自身毛 考古惊现史前巨人族遗骸 女贼婚礼盗走50万嫁妆 我来说两句排行榜 240 圈子 - 河南汤阴一派出所长：法律是听我说的 我就是法 [ 评 ] 129 孙红雷批女演员为红陪睡：找老婆不能是圈内人 [ 评 ] 104 圈子 - 29岁小伙初恋爱上62岁大妈 大妈嫌被管出走(图) [ 评 ] 93 美曝中国潜射“舰艇杀手” 美航母难以招架 [ 评 ] 91 刘晓庆60岁与富豪老公庆生 亲自下厨贤惠-娱乐频道图片库-大视野-搜狐!!! [ 评 ] 91 习近平：进一步提升我国装备制造能力-搜狐新闻 [ 评 ] 89 圈子 - 3人银行卡未离身被盗刷45万 银行称系统无异常 [ 评 ] 85 圈子 - 女子5万错存去世前夫账户 银行拒绝退款(图) [ 评 ] 84 我来说两句-我来说两句-深圳一嫌犯指认现场时逃脱 警方悬赏20万缉捕-新闻图片库-大视野-搜狐 [ 评 ] 社区热帖推荐 校花一时迷糊就“悲剧”了 春光乍泄……[ 详细 ] H杯“波动妹”史上最震撼弹琴 巴西3女子结婚 还要生孩子 健身房的妹纸身材娜美 这就是传说中的黑帮生活？电影还原了这么多 日本G罩杯美少女coser身份曝光 客服热线：86-10-58511234 客服邮箱： kf@vip.sohu.com 设置首页 - 搜狗输入法 - 支付中心 - 搜狐招聘 - 广告服务 - 客服中心 - 联系方式 - 保护隐私权 - About SOHU - 公司介绍 - 网站地图 - 全部新闻 - 全部博文 Copyright ? 2015 Sohu.com Inc. All Rights Reserved. 搜狐公司 版权所有 搜狐不良信息举报邮箱： jubao@contact.sohu.com AD =",
                "四中全会到五中全会 省部级一把手换了13个(名单)-新闻频道-和讯网 和讯首页 | 手机和讯 登录 注册 新闻 | 股票 | 评论 | 外汇 | 债券 | 基金 | 期货 | 黄金 | 银行 | 保险 | 数据 | 行情 | 信托 | 理财 | 收藏 | 读书 | 汽车 | 房产 | 科技 | 视频 | 博客 | 微博 | 股吧 | 论坛   滚动 　 金融资本 　 国内经济 　 产业经济 　 经济史话 时事 　 公司新闻 　 国际经济 　 生活消费 　 财经评论   专题 　 新闻周刊 　 一周深度 　 人物   访谈 话题   和讯预测    法律法规   封面   数据   公益 　 读书 　 商学院 　 部委 　 理财产品 天气 　 藏品 　 奢侈品 　 日历 　 理财培训 和讯网 > 新闻 > 时事要闻 > 正文 四中全会到五中全会 省部级一把手换了13个(名单) 字号 评论   邮件   纠错 2015-11-02 03:32:59 来源： 北青网-北京青年报   作者： 周宇 邹春霞     上周闭幕的十八届五中全会上，中央委员方面的人事调整颇受关注。自十八届二中全会以来，省部级人事便迎来密集调整。相比此前，四中全会到五中全会这一年，人事调整更显频繁。 北京 青年报记者梳理发现，四中全会以来，5个省部级单位换了“一把手”，地方上则有8个省份的省级党委书记或省级行政长官有所调整，而副部级干部的变动更是不胜枚举。出现人员调整的部委中，工信部和发改委的人事变动较为频繁。此外，履新省级政府的一把手中，以“老将”居多。 　　调整 　　5部委换帅 8个省份党政“一把手”有所调整 　　从去年10月召开十八届四中全会至今，已有5个部级单位更换了“一把手”，包括文化部、环保部、中央统战部、国家统计局和安监总局。 　　去年12月，中宣部原常务副部长 雒树刚 调任文化部党组书记、部长，他今年60岁，原部长蔡武2014年10月满65岁，现任全国政协外事委员会副主任。 　　同年12月的最后一天，国务院宣布中央政治局委员 孙春兰 兼任中央统战部部长，原部长 令计划 已被双开。 　　另外三位调整的“一把手”系今年调任。1月，51岁的清华大学原校长 陈吉宁 接棒已到退休年龄的 周生贤 ，出任环保部党组书记，2月开始任环保部部长。 　　国家统计局则是今年5月换的帅， 王保安 任国家统计局局长，他此前为财政部副部长。国家统计局原局长 马建堂 四中全会时由中央候补委员增补为中央委员，已于今年4月调任国家行政学院，任党委委员、常务副院长。 　　在五中全会开幕前，10月中旬，公安部原副部长杨焕宁调任国家安全生产监督管理总局任党组书记、局长。原局长 杨栋梁 今年8月已落马。 　　北京青年报记者梳理发现，虽然部委“一把手”的变动只有5人，但副部级干部调动频繁。除了各大部委几乎都有调整以外，“一行三会”、国务院副秘书长、国新办、央视、央广、新华社等都有副部级以上的干部调整。 　　地方党政领导方面，北青报记者注意到，十八届二中全会后全国31个省份的省级党政一把手调整基本到位。此后至四中全会召开时，仅有 山西 、 吉林 两省受反腐影响省委党政一把手有所调整。其中， 吉林 原省委书记 王儒林 同岗调任山西，整顿因塌方式腐败严重受损的山西省委常委班子，山西省原省委书记 袁纯清 则调任中央农村工作领导小组副组长，吉林省党政一把手受此影响联动调整，原省长 巴音朝鲁 接手省委书记一职，省长一职则由中国农行调任的金融专家 蒋超良 接任。 　　而十八届四中全会至五中全会期间，据北青报记者统计， 贵州 、 辽宁 、 河北 、海南、安徽、 云南 、 新疆 、 天津 等8个省份的省级党委书记或省级行政长官有所调整。 　 　调整的原因多样，具体有三个方面： 　　一是受腐败影响，如河北省原省委书记 周本顺 于今年7月落马。 　　二是源于反腐的连带效应，如天津、贵州、新疆。天津市原市委书记孙春兰补缺统战部后，由此造成天津市长 黄兴国 代理市委书记一职近一年；贵州也是如此，原省委书记 赵克志 调任河北补缺，党政一把手因而调整。 　　三是由新老交替引发的正常调整，如辽宁、安徽， 王珉 、 张宝顺 到龄退居二线，两省党政一把手也因此换新；也有如云南，2014年年底，时年64岁的云南省原省委书记 秦光荣 提前退居二线，调任全国人大，继而引发调整。 　　8个省份中，海南省的调整略显特别。海南省原省长 蒋定之 ，“罕见”回乡就任 江苏 省省人大常委会党组书记、副主任。其省长一职由国家海洋局原局长刘赐贵“空降”海南接任。在海南省委常委（扩大）会议上，官方透露蒋定之卸任海南省长前曾克服健康问题坚持工作，蒋定之随后在 全国两会 上透露是耳疾。 　　部委调整观察 　　工信部、发改委人事变动频繁 　　纵观1年来的人事调整，北青报记者发现，2个部委的副部级干部变动较为频繁――工信部和发改委。 　　工信部目前1正9副的领导班子格局中，有5位副部长是过去1年调整的。最早的调整发生在今年1月，工信部办公厅原主任莫玮任党组成员，晋升副部级。紧接着的2月，北京航空航天大学原校长怀进鹏调任工信部任副部长、党组成员。7月，青海省原副省长辛国斌到任。此后，同样在10月，五中全会前夕，工信部再来两位副部长，分别是 湖南 省委原常委陈肇雄和原产业政策司司长冯飞。 　　国家发改委1年来同样有5位副部级干部履新。除了任建华任党组成员，负责纪检以外，另外4人都还有副主任的职务，其中3人是正部长级，只有发改委原副秘书长 王晓涛 是副部级。3位正部长级的副主任分别是国务院原副秘书长、国家食品药品监管总局原局长 张勇 ，新疆自治区原副书记、政府主席努尔?白克力，国务院研究室原党组书记、主任 宁吉 。此外，努尔?白克力还兼任国家能源局局长， 福建 省原副省长 郑栅洁 8月调任能源局任副部长级副局长。被免去职务的原副主任 朱之鑫 、 解振华 、 吴新雄 均是1949年生人，年龄已满，原副主任 徐宪平 1954年生人，今年也已61岁，4人都应因年龄原因卸任。 　　此外，去年年底以来，宣传系统也出现人事变动，包括中宣部、网信办、国新办等国家宣传管理机构，还有新华社、央视、央广等国家级媒体的干部。 　 　干部来源各有不同 　　从干部提拔的来源来看，来自单位内部和外部的调任皆有。但是从数量上看，副部级干部由本单位内培养提拔的比例远高于外单位调任的。 　　比如工信部副部长莫玮、冯飞，之前都是工信部的司级干部，一个任办公厅主任，一个任产业政策司司长，今年先后升为副部长；发改委副主任王晓涛原为副秘书长；监察部副部长陈雍原为第十二纪检监察室主任。另外，财政部、公安部、教育部等近10个有副部长履新的单位均存在内部提拔的状况。 　　外单位调任同时提拔的也有，数量上则少得多，包括文化部部长雒树刚、统计局局长王保安、安监总局局长杨焕宁、国家预防腐败局副局长刘建超、中纪委秘书长杨晓超、商务部副部长钱克明、海南省长刘赐贵等都是这种情况。 　　在干部提拔中，提拔的速度不一。 　　例如杨焕宁到安监总局履新前，2001年就已经是公安部副部长，干了15年副部长才升至正部级。 　　而像杨晓超，2013年7月才从北京市财政局长任副市长，完成局级到副部的提升，紧接着1年后任北京市委常委、政法委书记，再1年就任中纪委秘书长，2年完成了副部到正部的升级。 　　地方调整观察 　　黄兴国成十年来任职时间最长代理书记 　　8个省份中，贵州、辽宁、河北、安徽、云南、天津等6个省份在此前1年间调整的是省（市）委书记。其中，贵州、辽宁、安徽、云南都是由原省长接任，属正常调整。 　　较为特别的是天津，孙春兰赴中央统战部任职后，天津市市长黄兴国从2014年12月代理市委书记至今。就在2个月前，他还经历了 天津港 ( 600717 , 股吧 )“8?12”特别重大火灾爆炸事故。8月19日，天津港爆炸举行第10场新闻发布会时，天津市代理书记、市长黄兴国首次出席发布会，表示自己对这次事故负有不可推卸的领导责任。 　　对代理省级党委书记， 人民网 ( 603000 , 股吧 ) 舆情 监测室常务副秘书长单学刚曾统计过，改革开放以来，共有8个省（自治区、直辖市）出现过10次党委代理书记（第一书记）。他分析指出，20世纪80年代起，代理书记出现多为突然事件打乱原有人事布局后的临时调整，比如原书记的健康原因或领导干部违纪被查处。 　　天津是 十八大 后人事调整中唯一出现代理书记的地方，主要是受反腐影响。2014年12月末，孙春兰在令计划落马一周后接任中央统战部部长，从而造成天津市委书记出缺，由黄兴国代理。但同时受反腐影响，河北则有所不同。首个落马的在任省委书记――河北省原省委书记周本顺落马一周内，贵州省原省委书记赵克志即从西南北上，填补了空缺。贵州省委书记则由 陈敏尔 接任，其同时兼任省长，直到中央决定孙志刚空降西南，任代理省长。 　　北青报记者注意到，在此前出现过的代理书记中，代理时间最长的是新疆的王乐泉，其代理长达15个月，但这段代理发生在20年前。而在最近十年，福建、 西藏 、 上海 等三地曾出现代理书记的情况，其中代理时间最长的是 卢展工 ，由于原书记宋德福因病修养，他于2004年2月代理福建省委书记，10个月后转正。 　　黄兴国至今代理天津市委书记一职接近一年，已超过卢展工，成为近十年来代理书记时间最长的一位。值得一提的是，此前在西藏、上海代理书记的 张庆黎 、韩正都在约7个月后迎来中央决定，前者成功转任，后者则回归原职于十八大后晋升上海市委书记。 　　履新省级政府一把手的“老将”居多 　　贵州、辽宁、海南、安徽、云南、新疆等6个省份政府一把手有调整， 苏树林 落马后福建省长后续也将调整。相比省级党委书记，此前一年对省级政府一把手的调整出现了“老将”居多、“不唯年龄论”的新特点。 　　五中全会前的最新一例省级政府主官调整，出现在贵州。贵州省第十二届人大常委会第十八次会议10月16日决定，接受 陈敏 尔辞去贵州省省长职务的请求，任命孙志刚为贵州省副省长、代省长。顶着“贵州省委书记、省长”的头衔，党政一肩挑长达2个多月的陈敏尔，终于卸下担子，专司党务。 　　值得一说的是为其分忧的新任省级行政长官孙志刚，这是位“老同志”。官方简历显示，孙志刚生于1954年5月，此前为国家卫计委副主任，正部级，但以61岁“高龄”履任地方主政长官，这在以往非常少见。 　　这是今年以来第二例。此前一例是今年6月接任辽宁省省长的 陈求发 ，1954年12月出生的他履新时也已超过60岁。更早一例是接替努尔?白克力出任新疆自治区主席的雪克来提?扎克尔，生于1953年8月，2014年12月以61岁“高龄”代理省级政府一把手，之后转正。这两位还有一个共同点，都是从通俗认为的二线（政协、人大）转到一线（党委、政府），其中雪克来提?扎克尔是在上述调整的11个月前，刚刚当选为自治区人大常委会主任。 　　北青报记者注意到，四中全会以来履新的省长们都不太年轻，海南的刘赐贵、云南的陈豪履新时也都在60岁左右，履新年龄最小的是安徽的李锦斌，1958年2月出生，接任省长时为57岁。一般认为，省级政府主官是省级党委书记的重要后备干部，如辽宁、安徽省委书记出缺后都由省长晋升，但贵州、海南、云南、辽宁等4地履新的政府主官年龄都比党委书记大，年龄差最大的如贵州，贵州省委书记陈敏尔是“60后”，生于1960年，比孙志刚小6岁。 　　部委中也存在这种情况，例如文化部部长雒树刚1955年5月生人，即将60岁时提成正部。工信部副部长莫玮1956年生人，59岁从正局升至副部。 　　不过，结合当选干部选任的新思路也不难理解，早在2013年6月召开的全国组织工作会议上，习近平曾提出选用干部“四不唯”，即“不唯票、不唯分、不唯GDP、不唯年龄”。 　　本版文/本报记者 周宇 邹春霞 （责任编辑：HN666） 相关新闻 11/02 11:14 贵州毕节市纳雍县县长郑成芳涉嫌严重违纪被查 11/02 11:06 贵州黄平县工商联：树“五好”形象标杆 11/02 09:57 中甲联赛收官战 纳欢队主场轻取贵州智诚 11/02 01:58 中甲联赛收官新疆队大胜贵州队位列第八 11/01 18:25 新疆雪豹男足主场迎战贵州智诚 4：2大胜对手 11/01 14:37 贵州秤杆作坊手艺人离世 其妻接过手艺艰难维生 11/01 14:07 中国在贵州喀斯特地区修建高难度调水工程 11/01 08:52 贵州举办贯彻巡视工作条例培训班 相关推荐 贵州 评论 还可输入 500 字 最热评论 最新评论 新闻精品推荐 特色： 新闻周刊 高清组图 专栏 热点： 聚焦证券界反腐风暴 美国研制LRSB轰炸机 习近平就俄罗斯客机失事向普京致慰问电 [ 俄公开失事客机现场卫星图 载遇难者遗体回国 ][ 残骸散落沙漠之中 ] 中韩自贸区将启12万亿美元大市场 装备制造为国企海外并购重点 最牛老外基辛格与中国领导人交往 法总统奥朗德抵达重庆参观 国产大型客机C919首架机下线 生育二孩最快也要等到明年 国产大飞机C919下线 中法海军在南海演练 俄客机残骸散落沙漠中 奶茶妹妹承认婚前怀孕 推广 热点 热点 每日要闻推荐 社区精华推荐 泽熙实控人徐翔涉操纵股票交易被抓 徐翔被抓泽熙官网登旧公告 疑员工呼吁别过多诋毁 李克强：今后五年中国经济需年均增长6.5%以上 中日韩领导会见记者 2020年实现东亚经济共同体 关键领域改革在途 “十三五”资本市场由大向强迈进 中央巡视组全面入驻金融业 或已掌握反腐线索 谁把薄发配到重庆 新版人民币三票即将暴涨 下层女如何看西门庆 券商股的庞氏骗局 精彩焦点图鉴 私募老大徐翔被警方带走 新疆现神奇天象疑似反导 排行榜 1 徐翔豢养记者美女获取内幕信息威逼公司分红 2 “徐翔犯罪团伙成员拘捕被当场击毙”系谣言 3 股市气象站：11月3日阴转晴利空洗盘有助大盘上攻 4 国资委披露C919国产大飞机客户名单已获517架订单 5 农村改革方案传递重农新信号首提政经分开 6 中法签署200亿欧元核废料回收协议 7 国企改革持续发力央企并购大潮涌动 8 国产大飞机今日正式下线已获逾500架订单 9 中纪委释疑党员能否炒股：四类人不能买卖股票 10 助力区域经济国家级经开区谋转型升级 　　【免责声明】本文仅代表作者本人观点，与和讯网无关。和讯网站对文中陈述、观点判断保持中立，不对所包含内容的准确性、可靠性或完整性提供任何明示或暗示的保证。请读者仅作参考，并请自行承担全部责任。 频 道 新闻 股票 基金 黄金 外汇 期货 保险 银行 理财 债券 互金 评论 交 易 财经新闻端 股票客户端 基金客户端 外汇客户端 期货客户端 机构底牌 现货客户端 手机和讯网 贵金属客户端 和讯恭候您的意见 - 联系我们 - 关于我们 - 广告服务 本站郑重声明：和讯公司系政府批准的证券投资咨询机构[ZX0005]。所载文章、数据仅供参考，使用前请核实，风险自负。 Copyright 和讯网 和讯信息科技有限公司 All Rights Reserved 版权所有 复制必究",
                "天然气调价或推迟至年底 民用与非民用暂不并轨_网易财经 应用 网易新闻 网易云音乐 网易云阅读 有道云笔记 网易花田 网易公开课 网易彩票 有道词典 邮箱大师 LOFTER 网易云课堂 网易首页 登录 账号： 密码： 十天内免登录 忘记密码？ 免费下载网易官方手机邮箱应用 登　录 注册免费邮箱 注册VIP邮箱（特权邮箱，付费） 欢迎您， 安全退出 考拉海购 母婴专区 美容彩妆 家居日用 进口美食 营养保健 海外直邮 客户端下载 邮箱 免费邮箱 VIP邮箱 企业邮箱 免费注册 快速注册 客户端下载 支付 一卡通充值 一卡通购买 我的网易宝 网易理财 立马赚钱 电商 彩票 贵金属 车险 电影票 火车票 秀品商城 花田 找对象 搭讪广场 我的花田 下载花田客户端 LOFTER 进入LOFTER 热门话题 专题精选 下载LOFTER客户端 BOBO 女神在线直播 女神大厅 女神资讯 下载BoBo客户端 移动端 新闻 体育 NBA 娱乐 财经 股票 汽车 科技 手机 数码 女人 论坛 视频 旅游 房产 家居 教育 读书 游戏 健康 彩票 车险 海淘 应用 酒香 网易首页 > 财经频道 > 产经 > 正文 天然气调价或推迟至年底 民用与非民用暂不并轨 2015-11-02 19:34:36　来源: 上海证券报·中国证券网 (上海) 分享到： 0 天然气 价格调整或将推迟到年底。 今年年中以来，关于天然气价格下调的传闻就不断流传。《第一财经日报》获得的消息显示，早期国家发改委价格司牵头制定的天然气价格调整方案有所变化，民用和非民用气价格并轨短期无法实现，新的调价最迟在年底推出。 此前业内曾有预计10月底调价方案出台。能源咨询公司安迅思分析师陈芸颖认为，考虑到冬季需求增加的压力，天然气价格调整实施或延至明年。 不过一位燃气行业人士称，燃气企业受到上游高价气和下游需求疲弱夹击，处境艰难，业内普遍希望能在今年内进行非居民用气价格调整。 民用和非民用气价暂不并轨 今年年中，国家发改委价格司牵头制定的气价调整方案，除了气价调整，还伴随着民用和非民用气门站价格并轨的考虑。 但是此后至今，方案一直没有出台。10月21日，在国新办发布会上，国家发改委副主任胡祖才称，暂不调整居民用气价格，将继续推行阶梯气价制度，非居民用气价格根据市场进行调整。 这意味着原来拉平民用和非民用气价的考虑被搁置，从而影响到非民用气价格调整的幅度。上述燃气行业人士分析说，民用气价不调整后，非民用气降价空间也会被压缩，调价幅度要重新设定。 国内主要的天然气供应商 中石油 、 中海油 手中都握有高价的进口气，一旦下调幅度大，将成为“烫手山芋”，承担价格倒挂的损失，让利给下游企业。 陈芸颖分析说，目前已逐步进入传统天然气需求旺季，北方一些城市已经开始供暖，天然气消费量也将逐步攀升。由于目前的天然气价格较高，实际上抑制了包括工业、车用在内的部分需求。 从供应方来看，进入冬季以后，随着天然气消费量的增加，管道气的销售压力也将有较大缓解，通过价格调整来带动需求的迫切程度相比前几个月也没有那么显著。 陈芸颖解释，在部分地区， 中石化 、中石油等上游供应方也可以采用对少量大型用户下调价格的针对性措施，来刺激部分需求。如果价格下调的靴子落地，这一部分需求有可能快速增长，进而加大冬季的天然气保供压力，如果遭遇极端天气，甚至可能出现“气荒”的现象。 气价改革不局限于调价 胡祖才10月21日还指出，石油、天然气价格改革是能源价格改革的重要组成部分，下一步还要朝着市场化方向改革，并择机放开成品油价格。 安迅思分析说，此轮气价改革不仅仅包括价格调整，还会包括与价格改革相关的其他配套政策，配合推进“十三五”的相关政策，具体政策年内有望出台，但价格调整的执行时间或延至需求旺季结束以后，以确保冬季供暖季的顺利过渡。 气价改革一直在推动。最近多地都举行居民用气阶梯价格听证会，按照部署，要在今年底全面实施阶梯气价。 从2013年底天然气价格改革就已经启动，现是全面推行“市场净回值法”形成各省门站价格。发改委今年4月起，将各省份增量气最高门站价格每立方米降低0.44元，存量气最高门站价格提高0.04元，实现价格并轨，并放开直供用户（化肥企业除外）用气门站价格，由供需双方协商确定，实现存量增量并轨。 上述燃气行业人士对本报记者称，按照计划将实现民用与非民用用气门站价的并轨，但是现在这一动作延后了。未来改革的方向是，门站价格也解除政府管制，非民用气上下游协商形成，居民用气价格由政府核定成本，逐步放开。 去年以来，国际油价下滑，与油价相关的天然气价格也出现大幅下降。今年以来，进口的LNG（液化天然气）价格低至7美元/百万英热单位，但是在进口中受到基础设施、管道等准入限制，并没有对国内气价形成大的冲击。 但即便如此，国内天然气消费增速已明显下滑。2014年我国天然气表观消费量为1800亿立方米，同比增长7.4%，是近10年来同比增速首次出现个位数增长。截至今年9月底，国内天然气消费量1322亿立方米，增速进一步降低至2.5%，消费始终疲弱。 本文来源：上海证券报·中国证券网 责任编辑：NF049 分享到： 已推荐 0 网易荐新闻 推荐成功 快去看看还有哪些新闻被推荐了吧>> 分享到： 关键词阅读： 天然气 安迅思：天然气价格调整或延迟至需求旺季结束后 2015/11/02 深度布局天然气市场 BP中国策艰难调整 2015/10/31 廉价石油正迫使沙特考虑削减其天然气补贴 2015/10/29 天然气调价预期压制甲醇期价 2015/10/20 北京阶梯气价方案二获多数票 智能天然气表将免费换 2015/10/22 延伸阅读 网易聚合阅读 网易首页 财经首页 0 人参与 财经头条 财经首页 | 股票 | 商业 | 理财 俄官员砸巨资建中式豪宅 徐翔被查震惊游资 泽熙“噩梦”或刚刚开始 徐翔被捕前已被约谈 | 盘点这些年跌落神坛的\"股神\" | 徐翔覆灭记 徐翔股灾逃顶寻踪 | 泽熙淘汰机制残酷 | 11家公司撇清徐翔概念 16上市银行不良贷款余额猛增三成 农行居首 银行零售业务成雷区 | 银行收入结构分化 | 城商行前三季逆势快增 男子为结婚盗窃近百万获刑 武汉山寨“白宫”被闲置 6个月4次辟谣 徐翔被捕前已被约谈？ 大恒科技定增或生变 泽熙定增公司遭遇＂黑天鹅＂ 中美数据云泥之别 美元指数窄幅震荡 次新股逆市上涨 次新股炒作将开启 《深化农村改革综合性实施方案》发布 专家：李克强总理第三次经济公开课传递了啥 国家级水土保持规划获批 划23预防区17治理区 沱牌舍得集团12年终于嫁出去 天洋控股为一见钟情掷38亿豪娶 “二孩效应”： 5年后楼市或将新增9亿平方米需求 领导人峰会重启 中日韩FTA谈判面临新起点 聚合阅读 重新定位新闻浏览习惯 下载网易BoBo手机客户端 0 人跟贴 | 0 人参与 网友跟贴 0 人跟贴 | 0 人参与 | 手机发跟贴 | 注册 .tie-post .tie-post-area .post-area-photo{border-left-color:#cc1b1b;border-bottom-color:#cc1b1b;} .tie-post .tie-post-area .post-area-input{ border-right-color:#cc1b1b;border-bottom-color:#cc1b1b;} .tie-post .tie-hotword {border-left-color: #cc1b1b; border-right-color:#cc1b1b;} #tieAds{ border:1px solid #cc1b1b;border-bottom-width:0;width:588px; overflow:hidden; } .tie-post .tie-post-area .post-area-hover { border-color:#aad2ff;}",
                "成品油调价窗口明日再启 或迎年内第10次下调-综合-节能-能源资讯-国际能源网 欢迎您访问国际能源网! 客服热线: 400-8256-198 | 会员服务 | 广告服务 | 网站导航 国际煤炭网 国际电力网 国际石油网 国际燃气网 国际新能源网 设为首页 加入收藏 资讯 国际 国内 财经 聚焦 深度 煤炭 价格 市场 库存 焦炭 煤层气 煤化工 电力 火电 水电 核电 农电 智能电网 特高压 输配电 电缆 石油 原油 汽柴油 燃料油 石油市场 油企 石油统计 燃气 天然气 液化气 管网 新能源 太阳能光伏 风电 新能源汽车 节能环保 气候变化 低碳经济 节能 国际能源网能源新闻 能源行业最大的门户网站 资讯 产品 求购 公司 行情 统计 首页 宏观 国际 国内 观察 聚焦 经济 政策 导读 人物 政客 专家 企业 技术 动态 标准 项目 安全 节能 环保 知识 安全 节能 环保 您当前的位置: 能源资讯 » 节能 » 综合 » 正文 成品油调价窗口明日再启 或迎年内第10次下调 国际能源网能源资讯频道   来源：人民网   作者：杜燕飞   日期：2015-11-02 关键词： 成品油 国际油价 原油市场 按照“十个工作日一调”原则，新一轮 成品油 调价窗口将于11月3日24时开启。多家机构表示，本轮计价周期内 国际 油价 维持低位震荡，油价年内第10次下调几成定局。 本轮计价周期内， 原油市场 上下起伏不定，供需面未发生较大改变。 美国 原油 日产量开始下滑，但 欧佩克 原油产量 继续增长抵消了它的影响。此外， 伊朗 有望在年底前与西方国家达成最终协议，并在制裁取消后增加原油产量，所以 市场 对于供应过剩的担忧仍在。 截至11月2日第9个工作日，参考原油品种均价为45.38美元/桶，原油变化率为-4.90%，初步预计 汽柴油 对应下调幅度为125-135元/吨。调价模型显示截至10月30日收盘，原油变化率为-4.13%，对应下调110元/吨，折合成升价为：93号 汽油 下调0.08元/升，0号 柴油 下跌0.09元/升。 “目前原油市场呈现的利空消息仍较多，原油上行乏力，本轮调价搁浅可能性不大，成品油或将迎来年内第十次下调。”分析师孙晓飞表示。 受成品油下调预期以及需求低迷的影响， 国内成品油 价格持续下滑。10月30日国内29个主要省区市中 石油 、 中石化 批发均价显示，国四93号汽油和0号柴油分别较零售到位均价低755和771元/吨;国五92号汽油和0号柴油分别较零售到位均价低957和901元/吨。 孙晓飞表示，“金九银十”旺季过后，市场或将重回疲软态势。而进入十一月份，北方地区将逐步置换负号柴油，届时0号柴油资源或现紧张现象，油价或将高位保持，但国内整体油价或将依旧呈现稳中窄幅下滑的走势。 成品油分析师王能表示，按照目前 原油期货价格 走势及下月预测来看，后期国际 原油期货 虽供过于求局面不改，但随着美国炼厂检修的结束，且冬季取暖油旺季的到来，或给油价一定的支撑。11月，国际 原油价格 存在反弹的基础，且从技术指标来看，油价下行动力在减弱，这也为下一轮 发改委 调价出现上调预期埋下可能。 点击查看更多精彩能源资讯 国际能源网声明：此资讯系转载自国际能源网合作媒体或互联网其它网站，国际能源网登载此文出于传递更多信息之目的，并不意味着赞同其观点或证实其描述。文章内容仅供参考。 [ 扩展搜索 ]  [ 加入收藏 ]  [ 告诉好友 ]  [ 打印本文 ]  [ 关闭窗口 ] 下一篇： 油价持续低迷打压欧美石油巨头业绩 上一篇： 深海海底区域资源勘探开发即将有法可依 关键词阅读： 成品油 国际油价 原油市场 • 油价持续低迷打压欧美石油巨头业绩 • 向中国运油的超级油轮数量剧减至一年多来最少 • 六家民营地炼企业获原油进口双权 垄断开始消融 • 油气市场需求低迷 中石油前三季度净利润降近七 • 油价下周二或迎年内第十次下调 幅度或超125元/ • 油价下周或迎第10次下调 预计汽油降0.1元 • 油价下周二或迎年内第十次下调 • 下周二成品油或下调：92号汽油约降0.1元/升 推荐图片新闻 更多>> 今年前5月新能源汽车 广州港制三年计划 加 股价巨震后汉能李河君 产油国增产 油价下行 首个省级电网输配电价 慎海雄：“互联网+” 低油价时代，欧佩克何 FMG董事长：警惕能源 能源人物 股价巨震后汉能李河君首度亮相 未来五年豪掷十亿治沙 图为汉能控股集团董事局主席李河君6月17日，汉能控股集团董事局主席李河君在汉能薄...[ 详细 ] 曹仁贤谈五大光伏认识误区 王亦楠：总理为何要求核电必须绝对保证安全 能源要闻推荐 曹仁贤谈五大光伏认识误区 高交会海外产生“磁铁”效应 欧洲企业期 我国应积极参与全球能源治理 附件：内蒙古西部电网输配电准许成本核定 附件:内蒙古西部电网输配电价改革试点方 国家发展改革委关于内蒙古西部电网输配电 “水十条”掀投资热 外企、上市公司竞相 全球原油难改供给过剩局面 油价上方阻力 要闻点击排行 环渤海动力煤价格指数“十九连跌”后首度 能源互联网顶层设计将于6月提交给国务院 山西中煤平朔低热值煤发电项目遭环保部拒 国际油价4日继续回落 国家发改委调研内陆核电安全性 中国核建集中开展联营挂靠、拆借资金等专 中阿博览会节水展在宁夏举行 探索水资源 中俄商讨共同开采俄大陆架油气田 国际能源网能源搜索 资讯 产品 求购 公司 行情 统计 搜索更多能源资讯 国际能源网:    国际煤炭网 国际电力网 国际石油网 国际燃气网 国际新能源网 网站首页 ｜ 关于我们 ｜ 会员服务 ｜ 广告服务 ｜ 服务条款 ｜ 隐私声明 ｜ 联系方式 ｜ 网站地图 2015  in-en.com , all rights reserved  国际能源网  服务热线：400 165 8896    京ICP备14050515号 全球首家能源产业价值链服务平台   360绿色网站     ",
                "油价下周二或迎年内第十次下调-国内-要闻-能源资讯-国际能源网 欢迎您访问国际能源网! 客服热线: 400-8256-198 | 会员服务 | 广告服务 | 网站导航 国际煤炭网 国际电力网 国际石油网 国际燃气网 国际新能源网 设为首页 加入收藏 资讯 国际 国内 财经 聚焦 深度 煤炭 价格 市场 库存 焦炭 煤层气 煤化工 电力 火电 水电 核电 农电 智能电网 特高压 输配电 电缆 石油 原油 汽柴油 燃料油 石油市场 油企 石油统计 燃气 天然气 液化气 管网 新能源 太阳能光伏 风电 新能源汽车 节能环保 气候变化 低碳经济 节能 国际能源网能源新闻 能源行业最大的门户网站 资讯 产品 求购 公司 行情 统计 首页 宏观 国际 国内 观察 聚焦 经济 政策 导读 人物 政客 专家 企业 技术 动态 标准 项目 安全 节能 环保 知识 安全 节能 环保 您当前的位置: 能源资讯 » 要闻 » 国内 » 正文 油价下周二或迎年内第十次下调 国际能源网能源资讯频道   来源：全景网    日期：2015-10-30 关键词： 成品油价格 美国原油库存 国际原油价格 幅度或超125元/吨 汽油 每升降0.09元 原油价格 连续震荡下行，使得 国内油价 年内第十次下调渐行渐近。10月29日，从多家社会 监测机构 了解到，根据目前影响 国际 原油 价格走势的因素来看， 国际油价 短期仍将低位震荡，11月3日（下周二）新一轮 国内成品油 调价窗口正式开启，预计跌幅超过125元/吨，测算到零售价格90#汽油和0# 柴油 （全国平均）每升分别降低0.09元和0.11元。 过去一周，受到 中国 GDP 增幅减缓、 伊朗 将增加出口原油、 美国 原油库存 大幅度增长等因素的影响，WTI和Brent 原油期货 持续下滑，跌至近两个月低位。29日在美联储推迟加息以及投资者获利了结等心态的强力提振下， 国际原油 收盘呈现大涨走势，一举收复下旬原油多日下跌的幅度。 受此影响，国内 成品油 价调整参考的原油变化率有所收窄，但是负向发展趋势未变。截至29日，新机制执行后第65轮计价期第7个工作日，中宇资讯测算原油变化率为-5.07%，中宇原油估价46.574美元/桶，较基准价跌2.486美元/桶。以目前 原油现货价格 水平推算，预计成品油最终下调幅度将缩窄至125元/吨，约折合90#汽油0.09元/升，93#汽油0.10元/升，0#柴油0.11元/升。“除非未来国际原油现货均价每日上涨2.5美元，则下调预期才有扭转为搁浅的可能。”中宇资讯分析师高承莎称。 成品油行业分析师杨丹也认为，近期 原油市场 上下起伏不定，但供需面未发生较大改变，尽管 美国原油 日产量开始下滑，但 欧佩克 原油产量 继续增长抵消其影响。此外，伊朗有望在年底前与西方国家达成最终协议，并在制裁取消后增加原油产量， 市场 对于供应过剩的担忧仍在，预计未来一周，国际 油价 将维持低位震荡，下周二 成品油价格 下调势在必行。 受成品油下调预期以及需求低迷的影响，国内成品油价格持续下滑。数据显示，截至10月28日国内25个主要省市93#汽油批发均价为6933元/吨，较上周跌38元/吨；0#柴油批发均价为5324元/吨，较上周价格跌13元/吨。 “后期预计全国 汽柴油价格 走势主流维稳，个别根据销售及库存情况窄幅波动。”高承莎表示。 点击查看更多精彩能源资讯 国际能源网声明：此资讯系转载自国际能源网合作媒体或互联网其它网站，国际能源网登载此文出于传递更多信息之目的，并不意味着赞同其观点或证实其描述。文章内容仅供参考。 [ 扩展搜索 ]  [ 加入收藏 ]  [ 告诉好友 ]  [ 打印本文 ]  [ 关闭窗口 ] 下一篇： 沙特阿拉伯和匈牙利签署核能合作协议 上一篇： “三桶油”前三季业绩齐降 中石油净利降近七成 关键词阅读： 成品油价格 美国原油库存 国际原油价格 • 下周二成品油或下调：92号汽油约降0.1元/升 • “两桶油”前三季日少赚逾3亿 油价低迷拖累业绩 • 中石油三季度净利降八成 油价大幅下降是主因 • 中石化前三季净利降五成 • 汽柴油零售价下周有望迎来年内第十次下调 • 能源领域成《中央定价目录》价改焦点 • 纽约原油大涨6.3%报45.94美元 • 成品油调价窗口11月3日打开 预计油价再下调 推荐图片新闻 更多>> 今年前5月新能源汽车 广州港制三年计划 加 股价巨震后汉能李河君 产油国增产 油价下行 首个省级电网输配电价 慎海雄：“互联网+” 低油价时代，欧佩克何 FMG董事长：警惕能源 能源人物 股价巨震后汉能李河君首度亮相 未来五年豪掷十亿治沙 图为汉能控股集团董事局主席李河君6月17日，汉能控股集团董事局主席李河君在汉能薄...[ 详细 ] 曹仁贤谈五大光伏认识误区 王亦楠：总理为何要求核电必须绝对保证安全 能源要闻推荐 曹仁贤谈五大光伏认识误区 高交会海外产生“磁铁”效应 欧洲企业期 我国应积极参与全球能源治理 附件：内蒙古西部电网输配电准许成本核定 附件:内蒙古西部电网输配电价改革试点方 国家发展改革委关于内蒙古西部电网输配电 “水十条”掀投资热 外企、上市公司竞相 全球原油难改供给过剩局面 油价上方阻力 要闻点击排行 环渤海动力煤价格指数“十九连跌”后首度 能源互联网顶层设计将于6月提交给国务院 山西中煤平朔低热值煤发电项目遭环保部拒 国际油价4日继续回落 国家发改委调研内陆核电安全性 中国核建集中开展联营挂靠、拆借资金等专 中阿博览会节水展在宁夏举行 探索水资源 中俄商讨共同开采俄大陆架油气田 国际能源网能源搜索 资讯 产品 求购 公司 行情 统计 搜索更多能源资讯 国际能源网:    国际煤炭网 国际电力网 国际石油网 国际燃气网 国际新能源网 网站首页 ｜ 关于我们 ｜ 会员服务 ｜ 广告服务 ｜ 服务条款 ｜ 隐私声明 ｜ 联系方式 ｜ 网站地图 2015  in-en.com , all rights reserved  国际能源网  服务热线：400 165 8896    京ICP备14050515号 全球首家能源产业价值链服务平台   360绿色网站     ",
                "法国总统奥朗德第二次到访中国 中法关系再升温--时政--人民网 帐号 密码 记住登录状态 选择去向 强国社区 强国论坛 强国博客 SNS 人民微博 人民聊吧 人民播客 E政广场 七一社区 通行证首页 |  注册   选择去向 强国社区 强国论坛 强国博客 SNS 人民微博 人民聊吧 人民播客 E政广场 七一社区 通行证首页   人民网首页 共产党新闻   要闻 时政 法治 | 国际 军事 | 台港澳 教育 | 社会 图片 观点 地方 | 财经 汽车 房产 | 体育 娱乐 文化 传媒 | 电视 社区 博客 访谈 | 游戏 彩信 动漫 RSS | 网站地图 人民网 >> 时政 法国总统奥朗德第二次到访中国 中法关系再升温 2015年11月02日13:18    来源： 中国网    手机看新闻    字号 原标题：法国总统奥朗德第二次到访中国 中法关系再升温 　　继习近平主席访英之后，德国总理安格拉 默克尔、法国总理弗朗索瓦 奥朗德相继访华。“金风玉露一相逢，便胜却人间无数”，中国展现了“大外交”情怀，与英、德、法跨越亚欧大陆共谋发展、共创希望。 　　10月26日，法国驻华大使顾山与德国驻华大使柯慕贤在《人民日报》发表署名文章，称法德“是中国在欧盟的核心伙伴”，“在经济技术领域的关键合作伙伴”，“合作热点很多”，“欢迎习近平主席值纪念联合国成立70周年之际，在纽约就发展、安全以及倡导大小国家一律平等的《联合国宪章》所作的承诺。” 　　应国家主席习近平邀请，法国总统弗朗索瓦 奥朗德于11月2日至3日对中国进行国事访问。这是奥朗德担任法国总统以来第二次到访中国。奥朗德曾于2013年4月对中国进行了37个小时的旋风式访问，开启了“中法合作新周期”。第一次访华，奥朗德自称是对中国的“认识和发现之旅”，除已开展的核能和航空领域外，更进一步加强了经贸合作，广泛开辟了新领域，尤其是在农产品、卫生保健、城市建设和数字产业等方面。 　　中法友谊独特，源自深厚的历史传承。近代，一批胸怀救国梦想的有志青年负笈法国，学习法国启蒙思想家伏尔泰、雨果、巴尔扎克、莫奈等的精神，追求独立自主和民族复兴，在两国间架起了友谊的桥梁。1963年10月，时任总统戴高乐将军授权前总理富尔携带亲笔信访华，突破意识形态，为中法建交奠定了基础，是中欧关系的重大突破之一。1973年，时任总统蓬皮杜访华，成为第一位正式访华的西方元首。中法关系风雨无阻、历久弥新，不断向前推进，遂成中欧关系的中流砥柱。法国是中国首个建立全面伙伴关系、全面战略伙伴关系、开启战略对话的西方国家。奥朗德2012年上台以来，中法关系更是“小步快跑”，明确表示中法“需要稳定、前后一致的关系”。2014年3月，习近平主席访法，在《费加罗报》发表署名文章，用“五十而知天命”形容当下的中法关系，既是对两国半世纪风雨同行的礼赞，也是对两国未来的深深期许。中法关系，有历史、有现实、有未来，必将创造更好的明天。 　　全面推进气候合作。法国作为第21届联合国气候变化大会（COP21）的主办国，奥朗德将该气候会议视为任内“一号外交行动”，希望在主场避免2009年哥本哈根大会的命运，达成“广泛、有约束力且雄心勃勃的全球性减排协议”，“将全球温度上升控制在2摄氏度以内”，“参与国发表各国的减排贡献”、“集中讨论融资与技术转让问题”。为此，奥朗德亲自领衔筹备工作，专设指导委员会、气候谈判特使和顾问，大幅提高对外气候援助承诺，促成各方拿出先行方案，颁布“欧洲乃至世界最先进”的《绿色发展能源过渡法案》，确立降低核电比例、取消煤炭补助等六大改革目标。中国作为在气候问题上“动力和能力最强”的国家，法国迫切期待中国能够积极参与对全球气候治理、乃至全球气候秩序的构建。同时，中国作为世界最大的可再生能源生产者和消费者，也是生态技术投资的主要接受者，在电动车和生态、智慧城市领域保持领先地位，法国希望借此机会进一步加强节能减排领域合作，共拓新市场。 　　全面深化经贸合作。欧债危机以来，法国经济增长乏力，2008至2014年间年均GDP增幅仅约0.1%，工业占GDP的比重不足20%。去年年底以来，受益于多方利好刺激，法国经济复苏迹象显现，经济改革力度也逐渐加大，持续推出了行政区划改革、《马克龙法案》、“新工业法国”等举措，希望通过结构性改革释放经济活力，并划定新资源开发、可持续发展城市、环保汽车、网络技术、新型医药等九大未来工业领域，计划至2023年全面实现数字化、智能化，并在本土创造47.9万个工作岗位及450亿欧元的附加值。2014年，中法贸易总额为557.9亿美元，增幅达10.9%。中法航空航天、核能、汽车等传统优势领域上不断深挖合作潜力，在刚刚结束的习近平主席访英期间，中广核主导的中方联合体和法国电力集团就共同修建和运营英国萨默塞特郡的欣克利角C核电站达成战略投资协议，项目第一期的建造成本预计将达180亿英镑（约合280亿美元）。新增长点不断涌现，中国已成长为法国红酒的第三大海外市场，达能等食品企业纷纷加大对华投资，中石化与法国道达尔公司合作开发页岩气，在香港合建全球最大规模的污泥处理厂。同时，中国对法投资不断增长，之禾、圣元、海尔等在法国进行了31项投资，占中国对欧投资项目总数的21%，创造了近7000个就业岗位。奥朗德希望法国“做吸引中资的领跑者”，并进一步扩大出口、吸引更多中国游客，搭乘中国经济快车，为法国经济复苏添动力，为谋求2017年总统连任增砝码。 　　全面落实金融合作。2012年至2014年，中法贸易人民币结算比例从6.5%快速升至44%，巴黎人民币存款规模翻番至200亿，居欧盟第二位。中法在金融领域有共同需求，法国央行计划减持美元资产，将人民币视为备选币种。法国亦极力推动巴黎成为欧洲大陆的人民币离岸结算中心，专门成立工作组谋划相关事宜。法国作为亚投行创始成员国，对亚投行建设非常关心并给予大力支持，法、德大使署名文章称，“两国股份累计约高达8%，远大于其余域外国家的规模”，“期待亚投行基于稳健治理的首批投资项目”。 　　中法将携手传承互尊、发展互信、推进互利，经济上互为优先合作伙伴，政治上建立战略互信，文化交流上欣欣向荣，战略上不断推进合作深度，恰如“一带一路”所描绘的场景，两国共铸世界和平发展的新篇章。（慕阳子 中国现代国际关系研究院欧洲所） (责编：郜碧澄(实习)、盛卉) 打印 网摘 纠错 商城 分享 推荐     分享到... 分享到人人 分享到QQ空间 最新评论 热门评论 查看全部留言 时政要闻 法国总统奥朗德第二次到访中国 中法关系再升温 中纪委释疑党员能否炒股：四类人不能买卖股票 “李总理访韩”漫评②：中国倡议为中日韩合作… 甘肃公布22个被巡视市县巡视反馈情况 国产大飞机C919向全球公开亮相 时政热图 习近平访英给百姓8大利好 影响我们生活的十大改变 习近平提到的英国文艺作品 习近平与贫困百姓在一起 习近平访美全纪录 习近平访美送出的“厚礼” 频道精选 被开除党籍的中央委员 张震同志生平旧照 习近平对教师们有啥期望？ 习近平关心西藏的10个细节 习近平对县委书记的12句告诫 盘点习近平10句反腐\"硬话\" 李克强再论面子与里子 国务院常务会议十大核心数字 国务院常务会议的十大关键词 下半年十件影响生活的大事 人民日报重要言论库 【任仲平文章】 【社  论】 【人民时评】 【评论员文章】 【望海楼】 【人民论坛】 重要理论 【人民日报理论版】 【理论书库】 【理论期刊】 【人民网论】 【人民观察】 【人民讲堂】 人民日报社概况 | 关于人民网 | 招聘英才 | 广告服务 | 合作加盟 | 供稿服务 | 网站声明 | 网站律师 | 呼叫中心 | ENGLISH 人 民 网 版 权 所 有 ，未 经 书 面 授 权 禁 止 使 用 Copyright ? 1997-2015 by www.people.com.cn all rights reserved 人 民 网 版 权 所 有 ，未 经 书 面 授 权 禁 止 使 用 Copyright ? 1997-2015 by www.people.com.cn. all rights reserved",
                "补齐干部能下的制度短板--山西频道--人民网 人民网首页 账号 密码 记住登录状态 选择去向 强国社区 强国论坛 强国博客 SNS 人民微博 人民聊吧 人民播客 E政广场 七一社区 通行证首页 |   注册   |   网站地图   选择去向 强国社区 强国论坛 强国博客 SNS 人民微博 人民聊吧 人民播客 E政广场 七一社区 通行证首页     |   网站地图 共产党新闻 要闻 时政 法治 | 国际 军事 | 台港澳 教育 | 社会 图片 观点 地方 | 财经 汽车 房产 | 体育 娱乐 文化 传媒 | 电视 社区 政务通 博客 访谈 | 游戏 彩信 动漫 RSS 人民网 >> 山西频道 >> 理论  厘清“下”的界限　打通“下”的通道  补齐干部能下的制度短板 钟宪章 2015年11月02日07:23      来源： 人民网－《人民日报》      手机看新闻 打印 网摘 纠错 商城 分享 推荐           字号 分享到... 分享到人人 分享到QQ空间 　　流水不腐，户枢不蠹。长期以来，干部能上不能下问题成为制约干部工作的一个难点，是干部队伍建设的痼疾。这一问题造成干部队伍规模偏大，部分单位超领导职数配备干部，“肥大症”现象突出；一些不合格、不称职的干部长期滞留，阻碍了干部队伍健康发展。今年7月，中共中央办公厅印发《推进领导干部能上能下若干规定（试行）》（以下简称《规定》），从制度上厘清干部“下”的界限、打通干部“下”的通道，标志着我们党在解决领导干部能上不能下问题上迈出了实质性一步。 　　对干部能下作出刚性规定，补齐干部制度短板。改革开放以来，我们党废除事实上存在的领导干部职务终身制，建立完善领导干部退休、任期、交流、问责等制度，积极推进干部制度改革和能上能下问题的解决。但从总体上看，干部能下还存在制度短板，能上能下的双向机制并没有真正形成。用什么干部是导向，不用什么干部同样是导向。只有两个导向都明确清晰，干部队伍方能始终处在活水循环状态。《规定》聚焦干部“下”的问题，明确了干部“下”的6种情况，包括到龄退休免职、任期届满离任、问责处理、不适宜担任现职调整、健康原因调整和违纪违法免职等，在“让无为者无位、不胜任者无位、违纪者无位”上划出了“硬杠杠”。《规定》还对干部“下”的原则、方式、程序、渠道等环节作出明确规定，打通干部“下”的通道，对于优化政治生态、建设高素质干部队伍具有重要指导意义。 　　明确不称职干部的调整措施，破解干部能下的瓶颈制约。实现干部能下的难点，是把那些没有大过、没有违纪违法行为但在其位不谋其政、能力素质不适应的干部调整下来。由于受传统观念影响，“不到年龄不下、不犯错误不下”在一些地方成为惯例。实现干部能下，关键是让那些不适宜担任现职的干部“下”得合理合法、服众服气，能够起到激励干部队伍建设的作用。这就要对干部调整制度作出科学设计。为此，《规定》按照从严的要求，重点对调整不适宜担任现职的干部作出明确规定，梳理了干部“不适宜担任现职”的10种具体表现。这些具体标准既有助于广大干部树立正确从政理念，也能让不称职干部“下”得有理有据。《规定》还从守纪律讲规矩的高度，提出对政治上不守规矩、廉洁上不干净、工作上不作为不担当或能力不够、作风上不实在的4类干部要坚决调整下去的要求，从制度上设定了干部廉洁从政的政治底线。 　　完善干部“下”的出路安排，保护干部干事创业的积极性。解决干部能下的问题，特别是调整不适宜担任现职的干部，不是简单“一下了之”，而应正确把握政策界限，做好后续工作。为此，《规定》提出调整不适宜担任现职的干部，应根据其一贯表现和工作需要，区分不同情形，采取调离岗位、改任非领导职务、免职、降职等多种方式予以调整。对非个人原因不能胜任现职岗位的，应当予以妥善安排，使每一位“下”的干部都能适得其所；对调整下来的干部要给予关心帮助；加强思想工作，有针对性地加强日常教育管理，使被调整的干部卸下思想包袱；对调整决定不服的，可以申请复核和提出申诉。《规定》还提出对“下”的干部也要实行动态管理，在组织调整期满后，对德才表现和工作实绩突出、因工作需要且经考察符合任职条件的，仍可以提拔任职，使这些干部还有机会上来，打通干部能上能下的通道。 　　建立健全工作责任制，保证干部能上能下制度落地生根。制度的生命力在于执行。把干部能上能下制度落到实处，重点是增强党管干部意识、落实领导机构在干部工作中的相关责任。因此，各级党委应把推进领导干部能上能下作为全面从严治党、从严管理干部的重要内容。《规定》的落脚点是建立健全推进领导干部能上能下工作责任制，提出党委（党组）要承担主体责任，党委（党组）书记是第一责任人，组织（人事）部门要承担具体工作责任。在落实“两个责任”的过程中，应切实做到“真管真严、敢管敢严、长管长严”；从实际出发，制定落实细则，定期分析领导班子和干部队伍情况，保证能者上、庸者下、劣者汰，形成良好的用人导向和制度环境。 　　（作者为中共辽宁省委党校副教授） 　　《 人民日报 》（ 2015年11月02日 07 版） 延伸阅读: 突出抓好领导干部这个“关键少数” 对不合格的“回炉”干部就该降薪 人民日报来论：干部“召回”重在明责 云飞扬：“干部召回”是一种“另类的爱” 河北日报：“干部召回”让干部更有为 分享到： (责编：赵芳、王建) 26987405 我要留言 进入讨论区 论坛 博客 微博 SNS 育儿宝 图片 注册 / 登录 发言请遵守新闻跟帖服务协议    善意回帖，理性发言! 使用其他账号登录: 同步：   社区登录 用户名： 立即注册 密  码： 找回密码    恭喜你，发表成功! 请牢记你的用户名: ，密码: ,立即进入 个人中心 修改密码。 30 s后自动返回        推荐帖子推荐帖子推荐帖子      推荐帖子推荐帖子推荐帖子 ! 5s后自动返回        推荐帖子推荐帖子推荐帖子      推荐帖子推荐帖子推荐帖子 恭喜你，发表成功! 5s后自动返回        推荐帖子推荐帖子推荐帖子      推荐帖子推荐帖子推荐帖子 最新评论 热门评论 查看全部留言 > 今日热点 王儒林：广泛凝心聚力促进富民强省 三晋儿女拔穷根 山西对症施策精准扶贫 山西省政府党组召开会议学习贯彻五中全会精神 “太行风骨”在京展出299幅作品 压轴大戏舞剧《吕梁英雄传》震撼京城 太原申请低保先过家庭经济考核关 338城市将监测PM2.5 昆山特别重大爆炸 事故处理结果公布 从揪心到痛心，亚航失联客机确认失事 中纪委官网通报155起违反八项规定精神案件 西安“最牛村支书”等41人被移送司法机关 本网关注 | 强晋社区 襄汾：辣椒丰收了 农民犯愁了 悲鸿之子携50余书画家绘26米长卷 山西省人大常委会召开机关干部… 涉嫌渎职受贿 三名副厅级官员… 长治发现康熙年四色套印本《古… 山西省将筛选贫困村 开展旅游… 山西：“亮剑行动”打击“非法… 前11月山西检察机关查处县级… 洪洞举办“大美洪洞・靓丽汾河”摄影展 小店区举行国庆升国旗仪式 长治市启动《质量强市实施方案》 1-2月山西省进出口大幅增长… 太钢顺利通过WCA评估 闻喜县大棚西瓜种植鼓起了农民… 人民网发布《2014年旅游3… 长治市国土局通过2014年度… 热点专题 | 市县专题 社会主义核心价值观进校园 学习・讨论・落实 网络媒体山西行 晋商大会 走进山西看民企 太原国际马拉松大赛 名城祁县 盐湖区 大同县 宁武县 山阴县 24小时排行  |  新闻 频道 留言 热帖 人民日报社概况 | 关于人民网 | 招聘英才 | 广告服务 | 合作加盟 | 供稿服务 | 网站声明 | 网站律师 | 呼叫中心 | ENGLISH 人 民 网 版 权 所 有 ，未 经 书 面 授 权 禁 止 使 用 Copyright © 1997-2015 by www.people.com.cn all rights reserved 人 民 网 版 权 所 有 ，未 经 书 面 授 权 禁 止 使 用 Copyright © 1997-2015 by www.people.com.cn. all rights reserved",
                "纽约油价上涨0.3%突破每桶46美元-股票频道-和讯网 和讯首页 | 手机和讯 登录 注册 新闻 | 股票 | 评论 | 外汇 | 债券 | 基金 | 期货 | 黄金 | 银行 | 保险 | 数据 | 行情 | 信托 | 理财 | 收藏 | 读书 | 汽车 | 房产 | 科技 | 视频 | 博客 | 微博 | 股吧 | 论坛 市场 个股 主力 新股 港股 研报 风险板 全球 公司 P2P 行业 券商 美股 新三板 行情中心 主力控盘 大宗交易 龙虎榜单 内部交易 公告 提示 资金流向 机构持仓 融资融券 转 融 通 公司资料 财报 分红 培训 股吧 直播 论坛 炒股大赛 证券开户 股票APP 投顾 产业链 和讯网 > 股票 > 美股市场 > 正文 纽约油价上涨0.3%突破每桶46美元 字号 评论   邮件   纠错 2015-10-30 05:18:39 来源： 新浪网   　　 >>【美股极速开户】与专业机构同行 3分钟创建账号 3个工作日内审核完毕可交易 　　北京时间30日凌晨，周四纽约原油 期货 价格收在每桶46美元以上，延续了昨日大涨逾6%的势头。市场预计原油产量下降的状况仍将继续。 　　与此同时，纽约天然气期货价格收跌，因天然气供应量已接近历史最高水平。 　　纽约商业交易所12月交割的原油期货价格上涨12美分或0.3%，收于每桶46.06美元，为一周多以来的最高收盘价。周三纽约油价大涨6.3%。 　　伦敦洲际交易所12月交割的布伦特原油期货价格下跌25美分或0.5%，收于每桶48.80美元。盘中最低曾下跌至每桶48.17美元。 　　Forex.com首席技术分析师法瓦德-拉扎克扎达(Fawad Razaqzada)表示：“原油可能最终对市场风险偏好的普遍好转作出了反应，可能会出现大幅攀升。毕竟现在大多数利空消息已经在油价中得到了反应，而且原油供应增长已经过了 高峰 。” （责任编辑：HF黄020） 相关新闻 10/22 10:18 油价或以接近阶段性底部 10/29 10:36 油价急弹 「两桶油」回升3.5%-4% 10/29 01:19 午盘：美股午盘继续上涨 油价大涨能源股攀升 10/28 10:18 石油供过于求 油价跌至数周低位 中石化现挫逾2% 10/28 05:04 欧股收跌1.1% 油价走低令石油股承压 相关推荐 油价 评论 还可输入 500 字 最热评论 最新评论 精品推荐 IPO观察 龙软科技应收账款居高不下 净利大幅下滑 [第539期]沃施园艺依赖海外市场 [第538期]千乘影视盈利模式单一 [第537期]嘉澳环保原材料价格波动 [第536期]安图生物募投项目有风险 [第535期]花王园艺存货余额畸高 [第534期]海利尔药业存环保问题 和讯议市厅 交易大师赛 名家直播室 和讯投顾志 推广 热点 热点 每日要闻推荐 社区精华推荐 实盘头名逆市抓涨停 月赛火热报名中 报名参赛 签到 做任务有收益 大赛QQ群 领红包 徐翔概念股突变黑天鹅 10余次精准买入定增股 知名私募牛散三季度大调仓 医药化工股受青睐 国家队现身半数A股公司 持股扫描 偏爱哪些股票 超4成公司Q3筹码趋向集中 45股户均持股升六成 谁把薄发配到重庆 新版人民币三票即将暴涨 下层女如何看西门庆 券商股的庞氏骗局 精彩专题图鉴 网上投洽会 温氏股份高估值遭质疑 五粮液捆绑经销商难混改 上海互联网产业为何沉沦 创业融资六秘诀 巨头强攻电影原因何在 公关如何与媒体打交道 排行榜 1 图解后市：指数已脱离单边上扬目前要多看少动关注补涨 2 史上11月涨多跌少变数大五类个股有望年底领涨 3 任泽平：新一轮行情波澜壮阔11月份历史上曾大涨42% 4 徐翔被查私募圈炸开锅徐翔概念股突变黑天鹅 5 收评：徐翔概念股集体阵亡跌逾8%华丽家族等4股跌停 6 泽熙徐翔被警方调查至少十余次精准买入定增股 7 一个好汉三个帮史玉柱131亿借壳登A股 8 徐翔每天研究股票超12小时 9 温氏股份借大华农上市或成创业板巨无霸 10 山西证券突发紧急事件15新三板股暂停转让 　　【免责声明】本文仅代表作者本人观点，与和讯网无关。和讯网站对文中陈述、观点判断保持中立，不对所包含内容的准确性、可靠性或完整性提供任何明示或暗示的保证。请读者仅作参考，并请自行承担全部责任。 频 道 新闻 股票 基金 黄金 外汇 期货 保险 银行 理财 债券 互金 评论 交 易 财经新闻端 股票客户端 基金客户端 外汇客户端 期货客户端 机构底牌 现货客户端 手机和讯网 贵金属客户端 和讯恭候您的意见 - 联系我们 - 关于我们 - 广告服务 本站郑重声明：和讯公司系政府批准的证券投资咨询机构[ZX0005]。所载文章、数据仅供参考，使用前请核实，风险自负。 Copyright 和讯网 和讯信息科技有限公司 All Rights Reserved 版权所有 复制必究",
                "China Makes Leaderboards of 2015 Platts Top 250 Global | Platts Press Release | Media | Platts Cart English Русский 中文网站 ??? My Subscription | Register | Contact Us | Forgot Password? | Help Advanced Search HOME PRODUCTS & SERVICES Oil Natural Gas Electric Power Coal Shipping Petrochemicals Metals Agriculture Conferences & Events Maps & Geospatial UDI Data & Directories Delivery Platforms & Partners Insight Magazine Energy Week TV NEWS & ANALYSIS All Commodities Oil Natural Gas Electric Power Coal Shipping Petrochemicals Metals Agriculture Latest News Headlines News Features Videos The Barrel Blog Podcasts Industry Solution Papers Commodities Bulletin Top 250 Rankings Webinars METHODOLOGY & REFERENCE Oil Natural Gas Electric Power Coal Shipping Petrochemicals Metals Agriculture Methodology & Specifications - Overview Price Assessments Subscriber Notes New & Discontinued Price Symbols Corrections Market Issues Oil Market on Close Holiday Schedule Symbol Search / Price Symbol and Page Directories Conversion Tables Glossary SUBSCRIBER SUPPORT My Subscription Help & Support Software & User Manuals Email Alerts System Notifications Register / Log In ABOUT PLATTS Overview Leadership Offices History Social Responsibility Industry Recognition Events Media Center Press Releases Platts in the News Regulatory Engagement & Market Issues Careers Contact Us Home | About Platts | Media Center | Press Release Archive | Press Releases China Makes Leaderboards of 2015 Platts Top 250 Global Energy Company Rankings? Singapore - October 27, 2015 First Time in Rankings' 14 years: 3 Chinese Companies Make it to Top 10; 2 Make it to Top 5 Both APAC and EMEA Slip in Number, Average Rank and Growth Rates Ongoing Shale Plays Advance Americas' in the Rankings Hindustan Petroleum Corporation Ltd. Chairman Nishi Vasudeva Named 2015 ‘Asia CEO of the Year' China showed its increasing strength, not only as an energy production and demand center, but also as a leader in the world energy arena, according to the Platts Top 250 Global Energy Company Rankings? , which were announced Tuesday night. The Rankings, now in their 14th year, were unveiled to more than 300 energy executives at an annual dinner in Singapore, hosted by Platts, a leading global energy and commodities information provider. The Platts Top 250 rankings reflect the financial performance of publicly traded energy companies with assets greater than U.S.$5 billion, and are based on a combination of asset value, revenue, profit and return on invested capital (ROIC) for the latest fiscal year (2014). China Sets Record with 3 Companies in Top 10 Not one, but two Chinese energy giants, CNOOC Limited (Ltd) and PetroChina Company Ltd., moved into the Top 5 for the first time since the rankings began in 2002. The state-backed companies have rivaled Big Oil of the West for years, with PetroChina having appeared in the Top 10 for 11 years, and CNOOC Ltd. hovering in the 13th and 12th spots for the last several years. Despite weaker coal markets, China Shenhua Energy jumped into the Top 10 for the first time at #9, up from #15 place last year, as shrinking earnings from leading oil producers gave it a relative boost. It was the only coal and consumable fuel company in the leaderboard and marked the first time that China has had three companies in the top ranks, traditionally dominated by European and Americas counterparts. China was among the Asia Pacific (APAC) countries highlighted in the evening's keynote address , \" Diversification and Unification of Capital for Energy Investment ,\" given by Dr. Bo Bai , managing director of Warburg Pincus, who said he remains \"cautiously optimistic\" on the future of energy investing in China, India and Southeast Asia. \"As the region opens up its blocks, and reforms its fiscal regimes to enhance risk-adjusted returns, investment dollars will pour in, allowing countries in the region to benefit from more exploration, reserves, and production,\" explained Bai. Exxon Mobil Corporation Surpasses Decade at Top Spot Exxon Mobil Corporation retained its stronghold on the #1 spot for the 11th consecutive year, though integrated oil majors made up only half of this year's Top 10, with the sector's slightly weaker standing paving the way for two refining and marketing companies - Phillips 66 and Valero Energy Corporation - to join the Top 10 in 6th place and 8th place, respectively. Besides climbing from its 2014 rank of 13th place, Phillips 66 retained its positon as the world's biggest refiner. Valero, the world's biggest independent refiner, continued to benefit from the glut of U.S. crude oils in the U.S. Gulf Coast region. The Americas moved up the regional rankings, with its Top 10 energy companies placing 12th overall, up from the average rank of 15.5 the year before. Taken together, Americas energy firms now comprise 45% of the Top 250, crowding out a number of rivals from Asia and Europe. The changes also mark an inflection point for Asia's energy sector, which for years has seen its overall standing in the Platts Rankings edge higher and higher. While Asia's emerging economies, led by China, continue to pull in the biggest share of incremental global commodity demand growth, the pace of growth is now clearly slowing. APAC Softens, Europe, Middle East and Africa (EMEA) Falters Noticeably missing from the leaderboards' 5 and 10, were Britain's BP p.l.c., France's TOTAL SA, and Russia's OJSC Gazprom. The latter state gas supplier, which placed 4th overall last year, dived 39 places to 43rd, buffeted by the ruble's collapse, its impact on long-term credit, and other factors. TOTAL, once a Top 10 fixture, tumbled to 26th place this year as its earnings and returns faltered. Last year's 2nd-ranked BP fell to 29th this year, due in large part to its weaker profits and poor 2% ROIC. Despite a host of individual corporate achievements, Asia Pacific as a whole felt its economic muster begin to wane. The region, which had shown a \"personal best\" in last year's 2014 roster with 82 energy companies in the ranks -- surpassing the \"personal best\" of Europe, Middle East and Africa (EMEA) in 2008 with 80 spots -- slipped to 78 in the latest Rankings. A drop was also seen in APAC's average ranking of 137, as compared with 134 last year. Still, APAC's fastest growing ten companies had an average 3-year compound growth rate (CGR) of 21%, bettering the 14.8% CGR of its European counterparts. The number of EMEA entries on the list continued to slide this year, with 59 companies holding an average ranking placement of 123, down from last year when the region fielded 65 energy firms averaging a position of 113. Growth rates, based on average 3-year CGR, were just 2.8% -- less than half that of APAC (6%) and less than a third of that of the Americas (10.4%). Of the Top 50 Fastest Growing energy firms, only four were based in EMEA. Shale Play Advances Americas Shale oil retained center stage, helping to usher 113 Americas companies (89 U.S. and 14 Canada) into this year's Top 250 with an average ranking of 119. This is up from 103 companies with an average rank of 126 in 2014 but still below the 2003 peak of 149 companies. Despite being in its fifth year and in a much lower price environment, the shale oil revolution in some places of the U.S. has barely begun. Basins such as the Permian in West Texas and Mexico are reportedly becoming more productive, and producers continue to knock down the cost of production, requiring fewer rigs to continue high oil output. Dominating the worlds' Top 10 and Top 50 Fastest Growing energy company rosters are the Americas' tight and shale oil producers, as well as mid-stream and refining companies that are carrying and processing their increasing production volumes. Even gas utilities like AGL Resources Inc have ridden the wave of booming shale gas volumes moving to end customers through its increased transport/pipeline assets. While overall corporate growth rates for energy companies slowed from 10.0% to 7.3% on a 3-year CGR basis, the Americas energy companies (many of which are companies directly or indirectly benefiting from shale) shown in the Fastest Growing list, collectively enjoyed a combined 56% CGR, up from 46.8% the prior year. Mixed Bag for Utilities; Highest-Ranked Utilities Move Higher Some 128 of the world's Top 250 Global Energy Companies are electric utilities, gas utilities, independent power producers, multiple utilities, and renewable electricity producers. Utilities were among the sectors advancing in the 50 Fastest Growers list. All 10 of the highest-placed utilities have moved higher in the 2015 Top 250, indicating the reduced exposure to pure commodity price risk that these diversified and often partly regulated companies face, as compared to mid-cap oil and gas concerns. Separately, the largest pure coal mining company in the world, Coal India, climbed to 38th place this year from 47th place a year ago, due in large part to its impressive growth year-over-year, including a 14% jump in drilling over the past year. \"Asia CEO of the Year\" Awarded Taking \"Asia CEO of the Year\" honors this year was Ms. Nishi Vasudeva , chairman and managing director of Hindustan Petroleum Corporation Ltd. With this win, Vasudeva is automatically in the running with other finalists for the global \"CEO of the Year\" award, which will be announced December 9 at the Platts Global Energy Awards in New York. The Platts Top 250's independent judges panel, already impressed with Hindustan Petroleum's history of peer-beating marketing margins, credited Vasudeva's exemplary leadership with steering the company to its stated best financial performance since its 1974 formation. In the past year, the refining and marketing company showed a personal best in profits , well beyond its decades' highest profit from the prior year. According to the judges, these accomplishments were even more significant when juxtaposed with the volatile global economy, a",
                " My Subscription | Register | Contact Us | Forgot Password  | Help Advanced Search      HOME     PRODUCTS & SERVICES     NEWS & ANALYSIS     METHODOLOGY & REFERENCE     SUBSCRIBER SUPPORT     ABOUT PLATTS  Skip Navigation LinksHome|News & Analysis|Latest News Headlines| China data: Sep gasoil exports surge to record high at 1.11 mil mt on weak local demand  Print  China data: Sep gasoil exports surge to record high at 1.11 mil mt on weak local demand  Singapore (Platts)--27 Oct 2015 458 am EDT/858 GMT  China's gasoil exports hit an all-time high at 1.11 million mt in September, and the exports are set to remain at a high level till the end of the year on slow domestic demand and available export quotas.  The September gasoil exports were nearly five times higher than the 225,810 mt exported a year ago and up 54% from the previous record high of 722,520 mt in August, according to data released Tuesday by the General Administration of Customs.  Refinery sources said the exports helped to ease inventory pressure generated by sluggish domestic demand. Some refineries in the country plan to export more gasoil barrels in October than September.  According to Platts' survey, state-owned China National Offshore Oil Corp., or CNOOC, plans to export up to 100,000 mt of gasoil in October, up significantly from the 50,000 mt in September, while seven Sinopec refineries plan to export 421,000 mt this month, up 27% from 331,000 in September.  Article continues below...  Request a free trial of: Oilgram News     Oilgram News Oilgram News     Oilgram News brings you fast-breaking global petroleum and gas news on and including:      Industry players, upstream and downstream markets, refineries, midstream transportation and financial reports     Supply and demand trends, government actions, exploration and technology     Daily futures summary     Weekly API statistics, and much more  Request a trial to Oilgram News     Request More Information   Two surveyed PetroChina refineries also plan to boost exports in October to 248,000 mt from 205,000 mt in September.  The Ministry of Commerce issued a total 8.83 million mt of gasoil export quotas to state-owned Sinopec, China National Petroleum Corp. and CNOOC for 2015, Platts calculations based on data from industry sources showed.  Over the first nine months, China's gasoil exports totaled 4.35 million mt, up 37.8% from 3.16 million mt in the same period last year.  This leaves a balance of 4.48 million mt of gasoil export quotas for the last three months of this year, or an average of 1.49 million mt each month over October-December.  As China's outflow of gasoil has increased, it has also expanded beyond its traditional market of Southeast Asia.  China's first substantial gasoil export to Togo, in west Africa, was seen in August, with a 79,279 mt shipment, customs data showed.  GASOLINE EXPORTS ALSO GET A BOOST IN SEPTEMBER  China's gasoline exports totaled 615,190 mt last month, up 71.1% from September 2014 and 31.7% from August 2015, according to customs data.  Gasoline exports are expected to be stable in the coming months due to recovering domestic demand and healthy inventory levels.  \"Compared with gasoil, gasoline sales are better and we don't have inventory pressure,\" said a refinery source from PetroChina.  Over January to September, China exported 4.02 million mt of gasoline, up 17.8% from the 3.42 million mt in the same period of last year.  The Ministry of Commerce issued 6.40 million mt of gasoline export quotas for 2015, leaving a balance of 2.98 million mt or a monthly average of 993,000 mt of unused quotas for the fourth quarter.  --Staff, newsdesk@platts.com --Edited by Geetha Narayanasamy, geetha.narayanasamy@platts.com              inShare2    Platts Email   PRODUCT FINDER  Step 1  Step 2  Step 3  Related News & Analysis    Video archives   Capitol Crude podcast archive   Global Oil Markets podcast archive   News features   The Barrel blog Related Products & Events    Price Assessments: Dated Brent   Oilgram News   Oilgram Price Report   African Refining Summit, 2nd Annual   Oil and Gas Acquisition and Divestiture Outlook   Middle East Crude Oil Summit, 3rd Annual  @PlattsOil on Twitter @PlattsGas on Twitter  Contact Us|Site Map|About Us|Holiday Schedule|Media Center|Platts Privacy & Cookie Notice|Terms & Conditions|For Advertisers|中文网站|Русский +1-800-PLATTS-8 / +1-800-752-8878 support@platts.com sales@platts.com ",
                " About Us   UPI en Espa ol   Log in or Register!   facebook twitter search      Top News     Entertainment     Odd News     Business     Sports     Science     Health     Analysis     Photos     Archive  Home / Business News / Energy Industry  Oil prices down on Russian production gains China adds to downward pressure with lackluster PMI for October. By Daniel J. Graeber Follow @dan_graeber Contact the Author   |   Nov. 2, 2015 at 9:19 AM Follow @crudeoilprices Comments0 Comments share with facebook share with twitter Crude oil prices start first full trading day in November in the red on word China's economy is slowing further and increased production from Russia. (UPI Photo/Monika Graff) | License Photo Sign up for our Energy Newsletter Preview our latest newsletter    NEW YORK, Nov. 2 (UPI) -- An increase in Russian crude oil production and more signs of weakness in the Chinese economy pushed crude oil prices down on the first trading day in November.  The price for Brent crude oil was down about 1.3 percent from the previous session to $48.88 per barrel in early morning trading. West Texas Intermediate, the benchmark price for U.S crude oil, was down 1.5 percent to $45.85 per barrel.  Crude oil prices are moving steadily lower, off about 11 percent for the year, because of signs of lingering global economic weakness and a surplus in supplies.  Novatek, the largest private crude oil producer in Russia, reported a 40 percent increase in production year-on-year. Among those reporting a decline, Rosneft, one of the largest companies in Russian in terms of overall output, reported a 1.1 percent decline in production for October, when weighed against last year.  Russia's momentum in production mirrors the energy companies who've reported earnings so far in the third quarter. Most companies, despite the downturn sparked by the market tilt toward the supply side, have reported an increase in overall crude oil production this year.  In terms of broader economics, China continued with a long string of reports showing emerging weakness with a drop in the manufacturing purchasing managers' index, or PMI. A reading of 49.8 in October signals factory activity in the economy is in contraction.  The Shanghai Composite Index closed down 1.7 percent for Monday, with oil majors China Petroleum and Chemical Corp. and PetroChina Ltd. among those reporting heavy losses. Like Us on Facebook for more stories from UPI.com   Related UPI Stories      Oil prices recover from Monday's loss     Russian oil production rises     Crude oil eases back sightly     Crude oil falls on news of Russian output   Comments0 Comments share with facebook share with twitter Topics: Brent Crude Offers and Articles from the Web! Ads by Adblade      These Celebrities changed our lives forever, but left too soon.NewsZoom     These 12 super foods speed up your weight loss and can potentially help melt fat!Newszoom     These are the most toxic places on earth. You'll never believe #5! Newszoom     These Celebs Take Vacations to a Whole New Level! Newszoom     Follow this one secret rule to lose weight...Venus Factor     These Stock Images Will Leave you Speechless! Newszoom  Recommended Spouses of the 2016 presidential hopefuls [PHOTOS] Next Article Spouses of the 2016 presidential hopefuls [PHOTOS] Latest Headlines Moody's: No long-term oil threat for Canada Moody's: No long-term oil threat for Canada NEW YORK, Nov. 2 (UPI) -- Canadian provinces may miss their short-term budget targets because of lower oil prices, though balance is expected long term, Moody's said. Iran talking energy with European companies  Iran talking energy with European companies TEHRAN, Nov. 2 (UPI) -- Iran said it's been vetting interest from major European energy companies eager to wade into the oil and gas sector once sanctions pressures ease. Scotland hosting new type of offshore wind program   Scotland hosting new type of offshore wind program ABERDEEN, Scotland, Nov. 2 (UPI) -- The Scottish government said it granted a license to the operators of what Edinburgh said may be the world's largest offshore floating wind energy development. North Dakota shows 3 percent increase in rig activity    North Dakota shows 3 percent increase in rig activity BISMARCK, N.D., Nov. 2 (UPI) -- In a further sign the worst may be over for North Dakota, the state reported an increase in the number of rigs exploring for or producing oil and natural gas. West Africa drawing oil interest   West Africa drawing oil interest MELBOURNE, Nov. 2 (UPI) -- West Africa continues drawing interest from explorers, with Australia's FAR Ltd. announcing the start of work in a basin in Senegal described as world class. Shell starts November with asset sales    Shell starts November with asset sales THE HAGUE, Netherlands, Nov. 2 (UPI) -- After reporting heavy losses for the third quarter, Royal Dutch Shell said it sold off stakes in Chinese and French holdings in the downstream sector. Crude oil prices steady early Friday  Crude oil prices steady early Friday NEW YORK, Oct. 30 (UPI) -- Crude oil prices stood pat in early Friday trading, as a midweek rally was offset by lingering concerns of macroeconomic growth and geopolitical issues. Exxon boasts of balance during downturn    Exxon boasts of balance during downturn IRVING, Texas, Oct. 30 (UPI) -- U.S. supermajor Exxon Mobil said declines in its exploration and production operations were offset by gains downstream, where lower prices actually helped. South African waters drawing in energy companies    South African waters drawing in energy companies STAVANGER, Norway, Oct. 30 (UPI) -- Southern African waters drew more interest from energy explorers with Norway's Statoil and Italy's Eni wading into Mozambique waters. Industry lobbies for Atlantic drilling   Industry lobbies for Atlantic drilling RICHMOND, Va., Oct. 30 (UPI) -- Industry supporters have started an ad blitz in southern U.S. states, arguing offshore oil and gas exploration can exist side-by-side with the environment. Trending Stories Lessig drops out of 2016 race, says Democrats 'won't let me be a candidate' Anonymous begins publishing Ku Klux Klan member details Lawsuit: Notre Dame tutor coerced students into sex with daughter Study: Diet diversity, moderation might be less healthy for adults Mexican navy rescues four fishermen adrift at sea for a month More Collections Notable Deaths of 2015 Notable Deaths of 2015 2015 NFL Cheerleaders 2015 NFL Cheerleaders 12 Celebrities battling chronic illnesses 12 Celebrities battling chronic illnesses Latina 'Hot List' Party in West Hollywood Latina 'Hot List' Party in West Hollywood Top archaeological finds of 2015 Top archaeological finds of 2015 2015 New York City Marathon 2015 New York City Marathon 8 things you didn't know about baby gorillas 8 things you didn't know about baby gorillas 2016 Hooters Calendar Girls in New York 2016 Hooters Calendar Girls in New York AROUND THE WEB ABOUT UPI  United Press International is a leading provider of news, photos and information to millions of readers around the globe via UPI.com and its licensing services.  With a history of reliable reporting dating back to 1907, today's UPI is a credible source for the most important stories of the day, continually updated  - a one-stop site for U.S. and world news, as well as entertainment, trends, science, health and stunning photography. UPI also provides insightful reports on key topics of geopolitical importance, including energy and security.  A Spanish version of the site reaches millions of readers in Latin America and beyond.  UPI was founded in 1907 by E.W. Scripps as the United Press (UP). It became known as UPI after a merger with the International News Service in 1958, which was founded in 1909 by William Randolph Hearst. Today, UPI is owned by News World Communications.  It is based in Washington, D.C., and Boca Raton, Fla. EXPLORE UPI.com UPI is your trusted source for ...  Top News Entertainment News Odd News Business News Sports News Science News Health News News Photos World News U.S. News Energy Resources Security Industry Archives UPI Espanol Follow UPI FacebookFacebook TwitterTwitter Google+Google+ InstagramInstagram PinterestPinterest LinkedinLinkedin RSSRSS Sign up for our daily newsletter!Newsletter  Contact  Advertise Online with UPI  Submit News Tips  Feedback  TERMS OF USE | PRIVACY POLICY  Copyright   2015 United Press International, Inc. All Rights Reserved.  UPI.com is your trusted source for world news, top news, science news, health news and current events.  We thank you for visiting us and we hope that we will be your daily stop for news updates. ",
                "国内成品油价再次上调 汽柴油每升上涨4分钱 中国 时事 社会 国际 亚太 趣闻 军事 周边 装备 图片 图说天下 财经 商业公司 评论 海外看中国 科技 探索 IT 译名 双语 漫谈 专题 图闻 锐参考 时事漫画 封面报道 军备办公室 战争之王 读书时间 国际先驱导报 首页 > 中国频道 > 中国滚动 > 正文 国内成品油价再次上调 汽柴油每升上涨4分钱 2015-10-20 17:45:02 来源： 国际在线 责任编辑： 国际在线报道：据发改委网站消息，今日，国家发展改革委发出通知，决定将汽、柴油价格每吨均提高50元，测算到零售价格90号汽油和0号柴油（全国平均）每升均提高0.04元，调价执行时间为10月20日24时。 此次成品油价格调整幅度，是按照现行成品油价格形成机制，根据10月20日前10个工作日国际市场原油平均价格变化情况计算确定的。9月下旬以来，受美国石油钻井平台数量下降等因素影响，国际市场油价呈震荡上行走势，前10个工作日平均价格有所上涨。 通知要求，中石油、中石化、中海油三大公司要组织好成品油生产和调运，确保市场稳定供应，严格执行国家价格政策。各级价格主管部门要加大市场监督检查力度，严厉查处不执行国家价格政策的行为，维护正常市场秩序。 上一页 1 2 下一页 本文系转载，不代表参考消息网的观点。参考消息网对其文字、图片与其他内容的真实性、及时性、完整性和准确性以及其权利属性均不作任何保证和承诺，请读者和相关方自行核实。 Angelababy赴整形医院 鉴定自己“是否整容” 成功嫁入豪门的10大女星 俄罗斯山顶奇观 “飞碟”盘旋似科幻大片 20岁妹子天生瞳孔异色 一黄一蓝魅惑又性感 娱乐圈内地花旦鲜为人知的首秀真容 腊肠犬“变身”泰迪熊 花园狂奔萌翻众人 泰国素食节上演利器穿脸 场面血腥惨不忍睹 剑桥学生恶搞新生遭痛批 学妹要给学长舔胸 一只在蘑菇底下避雨的猫头鹰 离散家属宴会朝鲜美女服务员抢眼 美国20岁女子腿长1.26米 欲挑战世界纪录 整牙变脸型 揭露整牙对女星的重要性 精品推荐 联合国涨“份子钱”，中国为何不接受? 7000多万人脱贫，难在哪里？ 国外如何管控危化品风险 精彩图集 中韩10大最丑明星排行榜 宋茜鹿晗榜上有名 娱乐圈要爱情不要婚姻的10大明星 Baby15岁牙套照曝光 青涩十足 猫咪手袋风靡日本 价格超真猫 热门图片 盘点娱乐圈10大男星鲜为人知的美艳亲妹妹 小周周首晒正脸照 天王女儿颜值比拼 摄影师再现15个被动物“养”大的野孩子 国粹成玩物：性感钢管舞娘京剧打扮拍片 排行榜 24小时 一周 一月 1 德媒：欧洲各国“狂热”争夺北京好感 2 支付宝推\"扶老人险\"最高赔2万 网友感叹道德沦 3 新郎临阵脱逃 美国女子婚宴豪掷22万招待流浪 4 英媒：中国股市重现生机 部分投资者悄然回归 5 外媒析常规军力排名:美遥遥领先 中日差距不大 6 冲绳为何反战？10万平民惨死 少女遭美军轮奸 7 台媒称\"康熙\"停播与\"换柱\"根源在于两岸实力消 8 日媒谈南京大屠杀申遗细节：中国外交工作准备 9 美国总统候选人特朗普：若当总统911不会发生 10 这才是战斗力！西藏军区特种部队吃饭不卸甲( 1 香港人在内地吃烤乳猪被\"吓坏\"：似猪又如鼠( 2 3天近15万人报名国考 人社部一岗位竞争888:1 3 俄陷经济危机年轻女子靠代孕挣钱：一单收费10 4 韩媒：韩国济州赌场发表声明 称未色诱中国赌 5 日本忧慰安妇也申遗成功 日高官：经费让中国 6 不朽功勋！盘点从黄埔走出的中共名将(高清组 7 德媒：欧洲各国“狂热”争夺北京好感 8 女子住店被房客偷拍裸照 向店家索天价赔偿(图 9 美媒:中国年轻人性态度开放 情趣用品商闷声发 10 外媒:中国超日本成全球第二富国家 中产达一亿 1 香港人在内地吃烤乳猪被\"吓坏\"：似猪又如鼠( 2 日本商家兴奋期待中国游客“爆买” 3 中国调查中朝边境枪击事件 韩媒：至少2人受伤 4 台媒：月球现3200多条裂缝 地球正撕裂月球表 5 港媒:上海夫妇在日被提示注意礼节 自觉受辱打 6 西媒称科学家或可通过改变鸡的基因制造恐龙 7 外媒：中国大部分“野长城”保存状况堪忧 8 中日高铁争夺转战美国 日媒忧中国拿走所有项 9 美国女拳击手因胸部重达5公斤 被迫越级比赛( 10 解放军仪仗队员遭俄美女索吻 机智应对获赞(图 关于参考消息 | 版权声明 | 广告服务 | 联系我们 | 订阅《参考消息》 | 参考消息网招聘 | 网站导航 国新网备2012001 互联网出版许可证（新出网证(京)字147号） 京ICP备11013708 京公网安备110402440030 - 参考消息报社 版权所有 -",
                "华电跨进“天然气时代” -韦博石油网 您好,欢迎来到韦博石油网！[ 请登录 ]，新用户？[ 免费注册 ]          本网介绍 | 资费说明 中文 | English 首　页 | 信息频道 | 设备名录 | 渤海商品交易所-现货 | 油品销售 | 会议信息 | 技术服务 行业新闻 | 油品信息 | 钻采设备 | 五金工具 | 原油 | PTA | 产品名称 | 产品报价 | 国内展会 | 企业建站 市场调查报告 | 燃气信息 | 炼化设备 | 机械设备 | 成品油 | 焦炭 | 运输方式 | 质量标准 | 国际展会 | 软件开发 石化自动化 | 石油小产品 | 仪器仪表 | 油站设备 | 聚酯切片 | 螺纹钢 | 注意事项 | 结算方法 | 展会新闻稿 | 网站维护 今天是 2015年10月27日 欢迎您光临本站 首页 >> 今日热点 华电跨进“天然气时代” www.oilmg.com 2015/10/20 11:40:13 韦博石油网 　　本报资深记者 吴莉 《 中国能源报 》（ 2015年10月12日   第 13 版）   　　9月16日，华电集团董事长李庆奎赴加拿大、美国调研，考察加拿大太平洋西北液化天然气（PNW）项目，会见加拿大进步能源公司等企业负责人、美国加利福尼亚州能源委员会主席等政府官员，参观芝加哥商品交易所，全面考察国际特别是北美清洁能源市场前景，探讨清洁能源产业发展、天然气发电产业链拓展机会。 　　没错，不是中石油，也不是中石化，是中国华电。 　　一家电力能源企业，如此青睐天然气产业，让人心生好奇。 　　事实上，早在2011年，华电集团就开始布局天然气业务，四年来，从上游气源获取到终端市场，公司已形成完整的上下游产业链布局，悄然跨进“天然气时代”。 　　转型提档，打造清洁发展新时空 　　“清洁低碳是能源发展大势所趋，是转型升级的必由之路，也是华电履行社会责任的必然要求。” 李庆奎表示。 　　作为一种清洁能源，天然气可有效缓解能源紧缺，减少环境污染，在世界能源消费中占据越来越重要的位置，被寄予了填补全球能源供应与需求巨大缺口的期望。有人说，天然气的黄金时代已然到来。 　　但一个不容忽视的事实是，我国天然气在一次能源消费结构占比仅在6%左右，与世界24%的平均占比相距甚远。面对 “一煤独大”的能源消费结构，发展天然气是当下中国调整能源结构最现实的选择。 　　根据预测，随着全球经济电气化程度的提高，未来世界天然气30-40%将用于发电。到2030年我国用气结构将呈现城市燃气、工业燃料和发电为主的局面，在天然气发电领域，京津冀鲁、长三角、珠三角等大气污染重点防控区，将有序发展天然气调峰电站，优先发展天然气分布式能源，这些与华电未来清洁发展定位不谋而合。 　　“传统电力能源企业依托天然气最大发电用户和分布式能源优势向天然气领域拓展，是华电集团立足长远发展，顺应能源发展形势要求，实现世界一流综合能源集团目标的重要举措 ”。华电集团总经理程念高说。 　　作为国内最大的天然气发电商和分布式能源供应商，华电集团根据国内外能源行业发展趋势和自身发展战略需要，深入开展LNG产业模式、产业政策、产业战略和市场等研究，决定培育和发展LNG这一战略新兴产业。 　　2014年6月，中国华电集团清洁能源有限公司应运而生，公司定位华电集团油气业务的资源开发、运营管理和投融资平台，统一经营和管理华电集团油气业务，同时履行华电集团油气业务管理延伸职能，为华电集团油气业务的发展战略、决策、技术提供支撑和保障。 　　成立伊始，华电清洁能源公司便按照“气源优先、集中采购、气电互保、高端起步，产业链开发、抢占先机、重点突破”的原则发展LNG产业，围绕华电集团清洁发展、“气电一体化”的目标，增强上游开发技术、中下游运营和开拓能力以及多渠道融资方式的产业发展实力，着力将天然气产业培育成华电新的经济增长点。        　　开放创新 ，激活后发优势 　　上天容易入地难。开发地下数千米深的天然气，尤其是地质条件复杂的非常规油气资源页岩气的开采，即使是深耕多年，实战经验丰富的“三桶油”尚属不易，对新进者华电绝非易事。 　　如何激活发展潜能？ 　　新战略呼唤新思路。开放创新，深度融合，是华电集团激活后发优势推动天然气产业战略发展的重要举措。公司一边努力探索非常规油气资源勘探开发的方法和技术手段，注重借鉴引入华电集团管理体系建设的先进理念和经验，以三标一体化管理体系建设为抓手。与此同时，华电重点借鉴中石油中石化等石油企业的经验和模式，以电力企业精细化管理为蓝本，不断完善整合和优化公司项目管控体系，并将体系建设渗透于公司日常工作及项目管控的方方面面，为提高公司管理水平，增强综合实力奠定基础。 　　永顺、花垣、来凤、鹤峰四个页岩区块地处湘鄂西地区，是少数民族聚居区，当地山大林密、断崖林立、植被高大、荆棘密布；区内易引发极端气候条件下的山洪等自然灾害，以及崩塌、滑坡、泥石流等次生地质灾害；交通以省道为主，道路多为盘山公路，路窄、弯急、坡陡，雨季还可能出现山体滑坡、坠石等灾害，项目施工还涉及炸药管理等。面对一系列安全隐患，华电清洁能源公司借鉴电力建设安全管控模式 ，成立了专职项目执行安全管理机构和应急工作小组，严格落实安全管理相关规定，充分做好事故预想及应急预案编制落实工作，通过实行电力企业量化管理模式和隐患跟踪排查机制，从根本上杜绝了各类事故的发生。 　　不仅是在管理上，一切有利于公司天然气战略发展的创新模式在华电都可以被采纳。组建合资公司、混合所有制经营、参股、控股等在华电已屡见不鲜。 　　湖北页岩气开发公司通过加快落实LNG气化站、储运中心及LNG综合能源利用项目工作，利用拟组建合资公司优势，打造铁、水、公路联运体系，积极开拓湖北市场并对山东、江西等市场开展针对性的培育。 　　新机生新力。正是源于这些开放和创新，华电天然气发展战略得以迅速发展。目前公司已与全球十几家主要LNG供应商进行了长协采购谈判。LNG项目有望在短期内成为华电清洁能源公司重要的经济支柱。 　　承“上”启“下”，“补链、延链、壮链” 　　天然气产业让华电清洁发展之路的脉络更清晰。然而，做好产业延伸文章，打通完善产业链，才是华电天然气产业发展的终极目标。 　　资源有底气，发展才有硬气。 　　2014年7月16日，华电清洁能源公司与中石化合作完成加拿大太平洋西北液化天然气（PNW）项目15%权益的收购，华电占比5%，拥有60万吨/年的权益气量及50万吨/年的长协气量，成功获取海外LNG气源，这是华电集团在国内非油气央企中的率先突破。 　　此外，华电清洁能源公司还与马来西亚石油等国际石油巨头开展深入交流、合作，共同对LNG贸易和投资进行研究，洽谈LNG贸易和权益收购，为公司获取长期LNG资源打下基础。 　　对页岩气这块难啃的“骨头”，华电集团也有突破性进展。中标的5个区块，分布在湖南、湖北和贵州三省，基本完成二维地震勘探概查,处于探井井位论证阶段。截至2015年4月底，野外地质调查6900平方千米，二维地震勘探1602千米，钻探4口地质调查井,湖北来凤咸丰区块的来地1井龙马溪组、鹤峰区块鹤地1井大隆组见页岩气显示。 　　国内优良的LNG站址资源有限，不仅是“三桶油”，诸多能源公司都在紧密布局。华电在浙江、广东、福建、海南等沿海地区开展了十几个LNG接收站站址比选工作，初步研究并选定了优先发展项目，所在地发改委对项目列入省“十三五”能源发展规划也给予肯定和支持。 　　终端市场是LNG全产业链盈利模式中的重要环节，拥有了终端市场对全产业链的健康稳步发展和实现利益最大化至关重要。 　　为此，华电清洁能源公司瞄准长三角、珠三角等沿海发达地区的天然气市场，拓宽从沿海到内陆的天然气发电及工业用气等市场的延伸，渗透到华中、华东、华南等发达地区的天然气输送管道及天然气终端市场。 　　从上游气源获取、页岩气勘探、LNG长协贸易，到接收站等通道建设，再到天然气终端市场布局，华电全面出击，一条承“上”启“下”的天然气产业链已然显现，天然气战略新兴产业发展的廓影也清晰可见。 来源： 《 中国能源报 》 作者： 新闻排行榜 一周排行榜 非洲发展银行行长表示东非不会落入“石油陷阱” 北京石油交易所 “油信通”产品首单签约 我国页岩气勘探开发面临机遇期 国内气价疯涨行情或终止 韩国石油战略提速 民企跨国买油破冰 首条石油管道准备开工 投 稿 箱 请将您在经营管理中遇到问题的解决办法发给我们，让更多的朋友分享您的经验。 经营管理编辑组： EOS@drs-i.com 友情链接 | 合作伙伴 阿里巴巴 中国压缩机网 中华石油信息网 中国石油工具网 中国海洋工程网 中国石油人才网 中华检测会议网 能源界 能源界 燃气人才网 中国能源投资网 深圳埃科润滑材料有限公司 石油人才网 中国新能源人才网 ourchemical 上海燃气网 机械报告网 市场调查报告网 消防车 管道疏通车 龙庆（龙德） 中国电力人物网 首聚能源网 环球经济网 QC检测仪器网 燃料油 华博安全 国际期货 中国润滑油营销网 海洋工程装备网 香港经济网 中国黄金期货网 中伊经贸网 石油工业出版社 中国石油 中亚橡胶网 石油英才网 盖德化工网 原油期货 油搜 全国工商联石油业商会 竞争情报网 冀东油田吧 富诺投资 中俄石化商务网 北京石油交易所 中海油 中国城市低碳经济网 中国石化 中国润滑油协会 行业研究网 油价查询 石油石化供应商联盟 化工报告网 今日石油价格 中国无损检测论坛 兴业金号 安迅思化工能源 石油人网 石油壹号网 天拓咨询 低碳工业网 润滑油商务网 中国能源装备网 世界石油化工展销网 首页 | 客户服务 | 会员注册 | 联系我们 | 网站地图 | 法律声明 ©2005-2008 DRS iNNOVATIONS GROUP LTD. All Rights Reserved 京ICP备13027032号",
                "中英有望签订逾2400亿元核电大单 资金潜入12股_网易财经 应用 网易新闻 网易云音乐 网易云阅读 有道云笔记 网易花田 网易公开课 网易彩票 有道词典 邮箱大师 LOFTER 网易云课堂 网易首页 登录 账号： 密码： 十天内免登录 忘记密码？ 免费下载网易官方手机邮箱应用 登　录 注册免费邮箱 注册VIP邮箱（特权邮箱，付费） 欢迎您， 安全退出 考拉海购 母婴专区 美容彩妆 家居日用 进口美食 营养保健 海外直邮 客户端下载 邮箱 免费邮箱 VIP邮箱 企业邮箱 免费注册 快速注册 客户端下载 支付 一卡通充值 一卡通购买 我的网易宝 网易理财 立马赚钱 电商 彩票 贵金属 车险 电影票 火车票 秀品商城 花田 找对象 搭讪广场 我的花田 下载花田客户端 LOFTER 进入LOFTER 热门话题 专题精选 下载LOFTER客户端 BOBO 女神在线直播 女神大厅 女神资讯 下载BoBo客户端 移动端 新闻 体育 NBA 娱乐 财经 股票 汽车 科技 手机 数码 女人 论坛 视频 旅游 房产 家居 教育 读书 游戏 健康 彩票 车险 海淘 应用 酒香 网易首页 > 财经频道 > 产经 > 正文 中英有望签订逾2400亿元核电大单 资金潜入12股 2015-09-22 07:05:00　来源: 证券日报-资本证券网 (北京) 分享到： 0 昨日， 核电 板块表现可圈可点，板块整体上涨5.03%，板块内 南风股份 、 应流股份 、 久立特材 、 中飞股份 、 兰石重装 、 丹甫股份 、 奥特迅 、 特锐德 、 中国一重 、 抚顺特钢 、 东方锆业 等11只成份股实现涨停，而 湘电股份 、 宝胜股份 、 科新机电 、 威尔泰 、 上风高科 、 永兴特钢 、 上海电气 、 中核科技 、 赣能股份 等个股涨幅也均在5%以上。 资金流向方面，据《证券日报》记者统计显示，昨日板块内共有35只概念股呈现大单资金净流入态势，其中，久立特材、特锐德、南风股份、奥特迅、东方锆业、 中国核电 、抚顺特钢、 浙富控股 、 盾安环境 、中飞股份、 中电远达 、 湖北能源 等12只概念股大单资金净流入均在1000万元以上，分别达到9798.57万元、9543.16万元、9274.93万元、6947.35万元、6310.60万元、5222.92万元、2613.23万元、2274.20万元、1553.69万元、1517.15万元、1239.66万元和1169.48万元，上述12只概念股累计吸金5.75亿元。 对此，分析人士指出，昨日核电板块强势表现，主要受到以下三大利好消息提振。 首先，9月18日，第十二届东盟国际博览会在广西南宁市会展中心开幕。中国广核集团（以下简称中广核）携我国自主核电技术最新成果“华龙一号”为代表的核电技术亮相东盟国际博览会。华龙一号是由中广核和中核集团联合研发的具有自主知识产权的三代百万千瓦级核电技术。作为中国具有完整自主知识产权三代核电品牌，华龙一号是目前国内可以自主出口的核电机型。 第二，据商务部网站消息，9月21日至24日，商务部将在美国组织举办4场贸易投资促进活动。商务部将与美 国华 盛顿州政府举办中美省州经贸合作研讨会，并将在研讨会开幕式上，会同辽宁、湖南、广东、四川、陕西5省和上海市与美方签署《中国省与美国华盛顿州贸易投资合作联合工作组谅解备忘录》。美国泰拉能源公司将与中国核工业集团签署第四代核电厂开发及商业化合作协议。 第三，9月21日，有消息称，正在访华的英国财政大臣乔治·奥斯本周一宣布，英国政府将给予欣克利角C核电站初步20亿英镑（约合197.69亿元人民币）的建设协议担保。奥斯本目前正在进行为期5天的对华访问，业内人士普遍预期， 中英 将在10月份习近平主席访英时 签订 预算高达250亿英镑（约合2415亿元人民币）欣克利角C站建设协议。 投资机会方面， 国泰君安 表示，“一带一路”国家提供了长期巨大核电潜在市场，“华龙一号”国产化技术走出去将成为趋势，海外市场前景相当巨大。核电海外扩张和内陆启动将继续拉升板块估值，装备类企业今年将先后迎来订单拐点，业绩拐点于明后年开始体现，其中手握核心原材料、新产品品类储备丰富、以及布局核电后市场的先行者会实现业绩大幅增长。建议关注：南风股份、中核科技、应流股份、浙富控股等。南风股份：核电重启业绩有望反转，3d打印进展顺利 核电重启业绩有望反转，引入核电人才助力发展。 公司是华南地区规模最大的专业从事通风与空气处理系统设计和产品开发、制造与销售的企业，HVAC产品 技术领先 ，在核电领域占有率较高。 2015年是我国核电重启之年，红沿河核电站5号机组已于3月29日正式开工，标志着我国沿海地区新建核电项目建设重新启动。据中核集团董事长孙勤表示，全国预计将有6至8台核电机组开工建设。受益于核电重启，公司业绩有望反转。 公司2014年12月29增补常南为副董事长，常南2014年9月起任南方增材副董事长，之前在核电领域积淀很深，曾任中电投核电有限公司总经理，将为公司核电及3D打印带来相关人脉资源。 与上海核工程研究院合作，3D打印进展顺利。 公司子公司南方增材2015年2月与上海核工程研究设计院签订的《技术服务合同》，南方增材为上海核工程研究院提供核电主蒸汽管道贯穿件模拟件，并配合进行市场推广，合作期两年，技术服务费200万。 通过与上海核工程研究设计院的技术合作，将加快该技术在核电领域运用步伐，开启重型金属3D打印技术产业化之门，具有重要意义。 收购中兴装备完成，协同效应明显。 公司已经完成了中兴装备100%股权收购，并于2014年7月合并报表，中兴装备主营业务是为石化、核电、新兴化工等能源工程重要装臵提供特种管件产品，与南风股份现有业务结合，协同效应明显。 投资建议：买入-A投资评级，6个月目标价125元。我们预计公司2015年-2016年的EPS分别为1.0、1.3、1.5元；公司主业反转，3D打印进展顺利，6个月目标价125元。 风险提示：3D产业化进程失败，核电建设低于预期中核科技：1H2015业绩平稳，核电重启有望带来自上而下的订单拐点 投资要点。 2015年半年报业绩略下滑，受2013年无新开工核电站影响。 公司是集工业阀门研发、设计、制造及销售为一体的制造企业，也是 中国阀门 行业、中核所属的首家上市企业。1H2015，公司营业收入5.21亿元，同比-2.94%；营业利润0.26亿元，同比-20.83%；净利润0.35亿元，同比-0.67%。我们认为业绩下滑的原因是：核电阀门订单有很强周期性。福岛事件后国内暂停新开工核电站，但核电阀门项目招标期较长，一般对业绩的体现是在项目开工后两年。因此，1H2015业绩受2013年全年无新开工核电站影响而下滑。 盈利能力平稳，下半年受核电重启利好将企稳上升。 上半年毛利率23.77%，同比-0.04pct。分产品看，核电阀门、核化工阀门、其他特种阀门、水道阀门、铸锻件毛坯的毛利率分别是30.18%，45.85%，22.75%，15.13%，14.24%，同比分别为+2.18pct、+1.14pct，+0.19pct，+0.90pct，-19.23pct。我们预测，随着下半年核化工阀门、阀门锻件募投项目相继投产，毛利率将上升。三项费用合计20.01%，同比+1.48pct，主要原因系销售费用增加所致。 公司预付账款增加68.69%，为0.69亿元，印证了订单拐点。 由于公司的预付账款账期基本上都是一年以内的，主要用途是公司增加合同备货和预付材料款等，因此预付账款的大幅度增加显示出公司的业务增长速度加快，对合同备货和原材料的需求逐步加大，说明已经进入快速发展通道，我们预计，内陆核电批准、红沿河5、6号机组开工、中英合作2500亿元核电站等事件催化已给公司带来实质性的订单进展，订单的拐点已经到来。 高壁垒、早起步是承担军工任务的必要前提。 公司是国内核工业集团系统所属企业，是首家在 A股 上市的阀门企业，面向资本市场的同时，还承担着为国防工业提供军品的任务。公司长期承担国家重点科研项目，包括核能建设专用阀门主蒸汽隔离阀、爆破阀和其他相关行业的关键阀门国产化等项目。2015年上半年，公司研发支出为1323万元，占营业收入比例为2.54%。 募投项目阀门锻件和核化工阀门已经达成投产条件。 高端核级阀门锻件生产基地建设项目，已完成项目施工建设和设备验收。该项目预计年产能6000万元，主要为核级阀门提供锻件。目前，公司核电关键阀门与核化工专用阀门的募集资金项目建设处于完工后陆续开始投入运行阶段，2014年实现业务收入3909万元。 中核集团旗下国企，有更多自上而下的订单机会。 目前核电建设运营商“中核集团、中广核、中电投”三足鼎立，阀门企业多为民营，如 江苏神通 、 纽威股份 、应流股份，而公司作为央企背景的国资企业，在承接项目上将享受更多自上而下的机会。 盈利预测与投资建议。 目前华龙一号关键阀门国产化比例要求达到85%以上，公司将积极抓住核电市场发展机遇，努力承接“华龙一号”订单，提升盈利水平。预计随着核电重启带来的订单，对应2015/16/17年的EPS为0.19/0.23/0.36元，对应PE分别为180/150/97，首次给予“增持”评级。 风险提示：核电重启低于低于预期。应流股份点评报告： 高端装备 零部件领先，核电、航空发动机领域有望快速增长 事件： 2015年上半年，公司营业收入71476.66万元，同比增长0.32%，净利润5035.58万元，同比下降34.41%。 主要观点： 1、业绩下滑源于产品毛利率下滑、管理费用提升。 2015年上半年，公司营业收入71476.66万元，同比增长0.32%，净利润5035.58万元，同比下降34.41%。利润下滑主要是由于国际油价的持续走低和国内核电项目重启缓慢，导致两大产品泵及阀门零件、机械装备构件毛利率均有一定下滑，而且管理费用增加1441万。 预计随着油价见底、核电重启，公司收入有望恢复增长、毛利率有望提升；随着公司研发推进、子公司成立，管理费用有望下降。 2、高端装备零部件制造领先。 公司是全球高端装备关键零部件制造领先企业，拥有铸造、加工、组焊为一体的完整产业链，拥有近400台各种先进数控机械加工设备，能够为客户提供核心零部件“一站式”解决方案。随着“中国制造2025”及工业4.0持续推进，公司将大幅受益。 3、拥有核一级泵阀类铸件许可证，核废料处理空间大。 公司是国内两家拥有核一级泵阀类铸件《民用核安全设备制造许可证》的企业之一。公司全资子公司安徽应流集团霍山铸造有限公司承担的装机容量为1400兆瓦先进非能动核电技术的中关键设备“CAP1400（DN450）爆破阀阀体铸件”研制项目和“CAP1400屏蔽电机主泵泵壳”研制项目通过了国家核电权威部门的出厂验收。 公司在核电工程一、二、三级产品订单较多，还加快核电站核一级产品在手订单的生产技术准备，在核电重启、订单集中的情况下，能加快进度，完成核电订单交付任务，促进公司业绩提升。 公司还与中国工程物理研究院核物理和化学研究所合作开发中子吸收材料技术的产业化，积极进军核废料处理市场，公司中子吸收材料产业化能力行业第一，市场空间巨大，未来将大幅受益。 4、积极拓展航空发动机、燃气轮机领域，定增资金提升实力。 2015年7月，公司通过全资子公司应流铸造设立安徽应流航源动力科技有限公司，目的在于加快公司在航空发动机、燃气轮机“两机”高端精密零部件制造领域的布局和建设，进一步提升公司在高端制造领域的领先优势。 公司在航空发动机领域已投入多年，增发完成后，将向航空发动机、燃气轮机零部件智能制造生产线项目投资18，937万元，该项目属产业链高端产品，主要难点之一就是高温合金涡轮叶片、导向器、机匣等核心件的精铸、机械加工和特种加工核心技术。而公司近年来一直积极向往航空发动机方向发展，并取得重大突破，项目建成后将进一步提升公司航空发动机、燃气轮机关键零部件的实力。项目建设周期为2年，内部收益率25.10%，达产后年收入达5.5亿元，利润总额9781万/年。 5、大股东参与定增，员工持股平台提升公司凝聚力，定增大幅降低财务费用。 2015年6月15日，公司发布公告，计划向八名投资者进行定增。公司实际控制人杜应流先生，部分董事、监事和高级管理人员参与认购。锁定期三十六个月，增发价格为25.67元/股，募集资金总额不超过151，453万元。 公司实际控制人杜应流先生认购780万股，占增发股本的13.2%。投资者衡胜投资、衡义投资、衡顺投资系本公司设立的员工持股平台，投资人包括公司董监高及核心员工，认购数量为1344万股，认购股份占增发股本的22.7%。 增发完成后，公司管理团队、核心管理技术人员持有公司股份，利益一致，保证了企业经营目标与股东的目标一致，提高了队伍的积极性和团队稳定性。 本次增发完成后，11.12亿元偿还银行贷款，将优化公司资产负债结构，大幅降低公司财务费用。 6、盈利预测与评级。 公司作为高端设备零部件供应商，拥有核一级泵阀类铸件许可证，积极推进核废料处理，大幅受益“中国制造2025”的推进及核电的建设，大股东、员工持股平台参与定增有效提升公司凝聚力。考虑定增的影响，预计公司2015-2017年EPS分别为0.29元、0.52元、0.71元，对应估值分别为66倍、37倍、26倍，给予“增持”评级。 7、风险提示。 高端装备零部件订单下滑，核电订单低于预期，定增进展低于预期浙富控股：收购车猫网继续践行“大能源+互联网”战略，成立浙富资本管理布局并购基金 投资要点 传统业务水力发电设备，受益于国家“一带一路”战略 公司于2014年累计获取三个重大海外水电订单，分别来自于乌干达、老挝和土耳其，三个大订单金额总计9.17亿元。2015年7月，公司中标埃塞俄比亚配网改造项目，中标金额约为1.61亿元，占公司2014年营业收入的23.48%。 成立浙富资本管理，布局并购基金 公司近日公告拟成立浙富资本管理注册资本一亿元，浙富控股持有51%股权；自然人孙毅（董事长）、姚玮、郦琪各持有标的公司20%、19%、10%股权。未来拟通过“浙富资本”打造金融投资控股子平台，借助并购基金等形式，为标的企业提供一站式金融服务。 收购四川华都，标的为核一级零部件供应商、华龙一号唯一的控制棒驱动设备供应商，2015年可贡献业绩 根据公司的“大能源”发展战略，未来公司将资源配臵重心向核电业务转移。四川华都公司作为“华龙一号”核反应控制棒驱动设备的国内唯一的生产商，未来订单受益于“华龙一号”技术的推广。目前，华都核电设备订单的订单总金额在5亿元以上，预计在2015年下半年开始产生业绩。 收购浙江格睿能源动力，依托高校技研发背景，涉足节能领域 公司在2014年收购了浙江格睿能源动力科技有限公司51%的股权，交易对价2.30亿元，收购浙江格睿股权是公司践行大能源战略的又一重要举措。格睿的业绩承诺为2015-2019年净利润不低于0.4亿元，1亿元，1.3亿元，1.3亿元和1.3亿元。 参股“梦想强音”、参股“ 二三四五 ”，多元化经营 2014年，浙富控股通过直接持有40%股权，托管11%股权，实现对梦响强音的控股。2015年6月，解除11%股份的托管权，共持有40%梦想强音股份，未来将重点放在大能源战略上。公司通过二级市场增发的方式，持有二三四五股权，占二三四五总股本的16.46%。 盈利预测与投资建议 暂不考虑石油业务和对车猫网的投资意向，预计2015年净利润为对外收购产生的2.13亿元和传统水电业务的0.85亿元，共2.98亿元对应2015/16/17年的EPS为0.15，0.21，0.30元，按照核电板块普遍估值较高，对应目标价格13.00元，故上调评级至“买入”。 风险提示：核电重启低于预期，华龙一号推广低于预期。久立特材：将高端进行到底 核心观点： 毛利提升是公司今后成长的核心脉络。公司是国内优质的民营不锈钢管材企业，发展脉络清晰，走的是“中、高、尖”的路线，2010年之前聚焦于中低端的不锈钢无缝管和焊管，成长靠“量”的拉动，在传统不锈钢无缝管和不锈钢焊管国内龙头地位奠定之后，借助上市的机遇，公司开始向高端领域进军，2010-2013年公司开始向LNG、镍质合金、复合管、核电等进军，业绩增长开始依靠“质”的提升：2012-2013年的业绩增长基本来自高毛利的LNG管、镍质合金管的放量，2014至今受油价低迷和石油反腐的影响，之前的业绩增长引擎油气管业务出现了停滞，2016年之后，随着尖端产品核电和军工的放量，未来三年公司毛利将迎来大幅向上的拐点，真正迎来全面高端化的发展阶段。 高端领域具备垄断性优势，理应享受高估值。从估值层面看，今年业绩预期小幅下滑，估值超40倍，明年我们预期估值在30倍左右，估值向上的弹性有限，但我们认为仅从PE角度对公司估值，缺少对公司竞争优势定价，在高端的LNG管、镍质合金管、核电690蒸发器管和军工等领域，公司均具备垄断性优势（LNG管国内主要是公司和武进不锈钢两家，690仅有公司和宝银，镍质合金管仅有宝钢和公司），凭借垄断性的行业地位，公司成长确定性更高，风险溢价小，理应享受高估值溢价。此外，从市值空间而言，未来核电业务的预期收入规模10-20亿，军工也有几个亿，基本上相当于在军工、核电领域再造了一个久立，市值向上的弹性大。 风险提示：核电启动进度不及预期；军工市场拓展不及预期 【相关新闻】 中英财金对话取得53项重要成果 拟开展\"沪伦通\" 马凯：深化中英双方贸易和投资合作 中国核电项目密集上马 在建规模世界第一 财经热点资讯 [滚动] 中英签下多个大单 李嘉诚布局又快人一步 (10-23 00:52) [滚动] 工资条里的这些秘密 你真的知道吗？ (10-23 00:30) [滚动] 美股早盘大涨1.5% 麦当劳领涨道指 (10-23 00:00) [滚动] 18省份GDP三季报出炉 重庆7次蝉联增速第一 (10-22 23:09) [滚动] 协鑫集成重组获通过 启源装备并购被否 (10-22 23:02) [滚动] 利民股份拟定增超7亿元 控股股东参与认购 (10-22 23:02) [滚动] 片仔癀三季报净利增25% 证金汇金持股2.68% (10-22 22:59) [滚动] 只有超级马拉松的虐心程度堪比中国A股 (10-22 22:59) [滚动] A股离奇大跌到底暗示了什么？ (10-22 22:58) [滚动] 美国9月谘商会领先指标连续第三个月不及预期 (10-22 22:47) [滚动] 股市早知道：影响市场的重要新闻汇总（10.22） (10-22 22:38) [滚动] 美国9月成屋销售远超预期 (10-22 22:16) [滚动] 光线传媒否认《港囧》“幽灵场”排片拉高票房 (10-22 22:16) [滚动] 李克强会见美前财长：人民币不存在持续贬值基础 (10-22 22:13) [滚动] 苹果与富士康合作开发太阳能 3股有望受益 (10-22 22:08) [滚动] 并购盛世下 亚洲私募股权基金却持币为王 (10-22 22:00) [滚动] 李克强谈稳定股市措施：是从中国国情出发 (10-22 21:49) [滚动] 长盈精密：汇金持股4.6% 为第二大股东 (10-22 21:44) [滚动] 浦东杭州天津三机场因正点率低受罚 (10-22 21:44) [滚动] 振东制药拟26.5亿元收购康远制药 (10-22 21:43) [滚动] 启源装备重组事项未获证监会通过 (10-22 21:42) [滚动] 全球工业低迷受害者：卡特彼勒三季度净利暴跌64% (10-22 21:36) [滚动] 美股周四小幅高开 失业金数据好于预期 (10-22 21:33) [滚动] 欧央行大招在手 加码QE还要再降息？ (10-22 21:26) [滚动] 首创集团班子成员单价万元买下属房企别墅 (10-22 21:24) [滚动] 欧洲央行释放强烈宽松信号 欧元大跌130点 (10-22 21:22) [滚动] 万讯自控终止重组 深交所“问询”背后诱因 (10-22 21:13) [滚动] 启明星辰等五公司并购重组获通过 (10-22 21:10) [滚动] 全球矿山巨头卡特彼勒三季度净利暴跌64% (10-22 21:06) [滚动] 欧洲央行释放强烈宽松信号 欧元暴跌130点 (10-22 21:03) [滚动] 李克强会见美国国际商用机器公司董事长罗睿兰 (10-22 20:57) [滚动] 欧央行行长：12月将重新审视货币政策宽松的程度 (10-22 20:51) [滚动] 中央巡视组：铁路总公司权力寻租较为严重 (10-22 20:50) [滚动] 中央巡视组：航天科技以虚假合同套取国有资金 (10-22 20:50) [滚动] 中央巡视组：鞍钢集团利益输送问题严重 (10-22 20:49) [滚动] 中央巡视组：中国一重盲目投资 涉嫌利益输送 (10-22 20:49) [滚动] 愤怒的小鸟快飞不起来了 Ravio两年裁员近一半 (10-22 20:49) [滚动] 通威股份：继续推进相关重组事宜 (10-22 20:48) [滚动] 中英将致力于构建面向21世纪全球战略伙伴关系 (10-22 20:48) [滚动] 金管局注资对港股意味着什么？ (10-22 20:36) 15家单位巡视情况：权力寻租严重 存团伙式腐败 国航和南航均表态 未收到合并重组相关信息 国务院：全国建立居住证制度 明确积分落户 多部委密集开展专项督查 破解\"政令不出中南海\" 央行开展千亿MLF操作 降准预期再度升温 郑万春辞任工行副行长 此前传将任民生银行行长 可口可乐工厂伪造环保数据 声明回避核心问题 消息称今年专项建设债规模至少增加至6000亿元 央视曝哈尔滨水投集团为暴利破坏1200亩黑土地 前9月国企利润同比降幅扩大 钢铁煤炭继续亏损 国务院再推结构性减税 高新技术企业迎福利 银行理财收益持续走低 资配荒难觅高收益投向 女子靠炫富诈骗1100万:1顿饭30万 南非大学生抗议学费上涨致激烈冲突",
                " 联 合 会       组织机构  理事会  会员  入会  动态  通知  政策  奖励  成果  安全  标准 环保  品牌  命名  评价  期刊  煤化工  专题  劳模 数据资讯频道    重要新闻  国际新闻  行业分析  经济运行  原油价格  化工专题  |  产值数据  产量数据  收入利润   进出口数据  《数据快报》 商机频道    首页  设备  塑料  橡胶  原料  合成纤维  农化  颜料染料  涂料  助剂  进口二手设备 | 供应商委员会新上线  项目  专家  企业  会展     联合会频道 网上办事 会长信箱     当前位置：首页 --> 展会信息    2015中国国际LNG技术装备展览会 举办时间:    2015-11-24/2015-11-26 举办地点:     上海新国际博览中心 举办单位:     中国石油学会 中国国际贸易促进委员会 承办单位:    上海艾灵会展有限公司 上海艾展展览服务有限公司    详细内容       　　批准单位：上海市人民政府商务委员会  　　主办单位：中国石油学会 中国国际贸易促进委员会  　　支持单位：中国石油天然气集团公司 中国石油化工集团公司 中国海洋石油总公司  　　协办单位：中国船舶重工集团公司 日本天然气协会 韩国天然气公社 国际储气罐和接收站操作协会 印尼石油协会 英国石油勘探协会 俄罗斯天然气工业股份公司 韩国高压气体工业协同组合联合会 马来西亚石油天然气行业协会 马来西亚国家石油公司  　　承办单位：上海艾灵会展有限公司 上海艾展展览服务有限公司  　　【日程安排】  　　报到地点：上海新国际博览中心(龙阳路2345号)  　　报到时间：2015年11月22-23日 布展时间：2015年11月22-23日  　　展出时间：2015年11月24-26日 撤展时间：2015年11月26日(下午14：30)  　　【展会背景】  　　天然气作为清洁能源越来越受到广大使用者的广泛关注，液化天然气在能源供应中的比正以每年约12%的高速增长，成为全球增长最迅猛的能源行业之一。近年来全球LNG的生产和贸易日趋活跃，正在成为世界油气工业新的热点。我国LNG产业发展迅速，受到全球眼光的关注,2011年世界LNG消费量同比增长9.4%的情况下，中国LNG增速达28%这一“中国速度”更是另世界瞩目。LNG继页岩气后已经成为行业发展的大热话题。根据国际能源署(IEA)的报告显示，亚洲在2015年前将成为仅次于北美洲的全球第2大天然气市场，天然气年需求量将增加到7900亿立方米。有权威机构预测，2015年中国对LNG的需求预计达到3100万立方吨。  　　【主要展商】  　　ChinaLng 2015中国国际LNG技术装备展览会 由权威机构举办与国际知名的SIPPE2015第十届上海国际石油天然气技术装备展同期举办，历届展会吸引到包括中国石油、中石化、中海油、中国化工、中国船舶重工集团公司、烟台杰瑞、山东科瑞、富瑞特装、陕鼓动力、华港集团、霍尼韦尔、沈阳远大压缩机股份、杭州福斯达实业集团、青岛瑞丰、中集安瑞科、烟台冰轮股份、中能服、北京天海工业、豪特耐管道、华谊集团、博思特能源装备、查特深冷工程、无锡辉腾科技、API培训与认证、张家港中集圣达、江南工业集团、LMF、SAMSUNG、Youngkook、Oilgear、RosneftOil、伊朗NIOC、伊朗NIGC、伊朗National Petrochemical Company、伊朗NIORDC、Business Group、Pietro Fiorentini、Saudi Arabian Oil Co、SULZER、Total、Fanavaran、Tencate、Intra Corporation、SANGBONG Corporation Intra Corporation、Armacel、 Raymonds、SHAIC、Haward、SilcoTek、Control Flow、RUSNANO、ECOIN、FNS PLUS、Kish Spanta等来自美国、澳大利亚、意大利、新加坡、加拿大、德国、英国、奥地利、韩国、迪拜、法国、瑞士、俄罗斯以及伊朗、中国香港及中国二十多个国家和地区的3500多家企业参展，共吸引超过52个国家及地区的超过25万名观众参与，展会累计成交60亿美元。全球石油天然气行业企业在上海这个国际大舞台展现最新技术装备、贸易交流、开拓商机。  　　历届主要国外展团，如：日本、韩国、新加坡、马来西亚、印度尼西亚、美国、澳大利亚、迪拜、奥地利、加拿大、意大利、挪威、墨西哥、俄罗斯、伊朗、巴基斯坦、尼日利亚等20多个国家的展示团采购团。  　　【选择ChinaLng优势】  　　  ChinaLng是亚洲最具国际影响力的液化天然气展  　　展会规模及专业采购商数量连续9年实现30%的稳步增长，2015年将有来自全球900家企业参与，专业观众预计30000人次，VIP采购商600家。  　　  ChinaLng主办机构权威及阵容强大 展会服务走向国际化及专业化水平  　　中国石油学会(CPS)是中国石油石化行业最权威组织，中国贸促会(CCPIT)具有强大的海外合作资源，艾灵公司(AIEXPO)是专著于能源行业会议、咨询、服务的国际性专业机构。  　　  ChinaLng巨大的投入及宣传推广  　　与国内外100多家媒体，40多个行业组织建立合作关系，在全球5个重点地区的展会进行推广，并组织参与40多个国内外的行业展会。  　　  ChinaLng同期举办高端的会议论坛  　　会议论坛将邀请国内外50位行业领导、专家、典型企业代表发表演讲交流， 500名专业人士参加。  　　  ChinaLng提供展商与买家商务配对 对采购团及VIP采购商提供专项资助服务  　　ChinaLng拥有强大的资源优势，将不断在国内外展会、媒体进行宣传和推广，对展商和采购商进行商务配对，争取邀请到更多的采购商到展会现场参观考察。为了确保展会效果，ChinaLng组委会对国内外油田、石油、石化、天然气等行业专业采购团级VIP采购商提供专项资助服务。  　　【展品范围】  　　■ 油、气储运成套设备及相关配件 ■ 油站油库防爆产品  　　■ 油气回收设备与技术 油.气清洁产品与设备 ■ 输气站站场设备  　　■ 汽车油改气设备及各类节油产品 ■ 零部件及专用装备  　　■ 油、气分析仪器,环保、节能和安全管理技术与设备 ■ 燃料转换系统  　　■ 成品油仓储、配件及相关设备配件类  　　■ 非油品业务类、洗车设备、GPS导航、汽车防盗、车载MP3  　　■ 天然气汽车、煤层气汽车、天然气运输车等、天然气汽车零部件双燃料或两用燃料发动机等  　　■ 液化气压缩天然气、液化石油气、煤层气相关技术设备  　　■ 安防、应急设备、通信及信息系统  　　■ 各类阀门类流体分离设备\\特种工程管道材料  　　■ 各种新型燃料汽车、环保汽车及其配套设施  　　■ LNG加注站设备类 LNG加注机主体及配套设施  　　【参展费用】  　　会刊宣传费用: 封面20000元 封底 15000元 封二13000元 扉页11000元 封三9000元 彩色内页6000元 黑白内页 3000元 文字介绍1500元  　　展馆现场广告：拱门￥23000元/个 胸牌￥20000元/期 吊绳￥30000元/期 气球￥15000元/个 门票￥10,000元/展期 手提袋￥20000元/展期 喷绘￥600元/ m2  　　其他室外广告、论坛及展会晚宴的赞助、演讲请联系组委会。  　　【参展联络】  　　地 址：上海市长逸路15号A座复旦软件园1309-1310室  　　总 机：+86-21-36411820 传 真：+86-21-65282319  　　网 址： www.chinalng-expo.com 邮 箱：2433854269@qq.com  　　联系人：钱多多 15800887941 邮 编：200441 【打印稿件】【关闭】 【大 中 小】           最新展会  更多>>     ·第五届中国国际天然气技术装备展览会 ·2015全国化工行业污染防治技术与清洁生产展览会 ·2015中国（宁波）国际工程塑料与改性塑料展览会暨化工原料与助剂采购交易... ·第五届中国石油化工装备采购国际峰会暨展览会 ·2015第六届中国（北京）国际煤化工展览会 ·第七届中国（上海）国际泵、阀门及管道展览会 ·第七届中国（上海）国际化工技术装备展览会    过期展会   更多>>     ·2014第六届中国国际管材展览...    2014-9-27 ·2014年全国TnPM大会暨第...   2014-9-20 ·2014中国国际现代分离技术展...   2014-9-19 ·2014（第十三届）中国国际化...   2014-9-13 ·2014中国化工新材料发展交流...   2014-9-3 ·第六届中国（上海）国际化工技术...    2014-8-28 ·第六届中国（上海）国际泵、阀门...   2014-8-28 联合会介绍 | 组织机构 | 法律声明 | 联系我们 | 解决方案 业务咨询：010-84885237,84885243  编辑部：010-52867232，64697972  传真电话：010-84885391  邮箱：webmail@cpcia.org.cn 地址：北京市朝阳区亚运村安慧里4区16号楼中国化工大厦   邮编：100723 主办单位：中国石油和化学工业联合会    经营授权：中国化工经济技术发展中心   ",
                " 中国能源网博客 站点群 > 韩晓平 > 媒体采访 > 为何鼓励三类企业优先使用进口原油?   为何鼓励三类企业优先使用进口原油?  03月10日, 2015 媒体采访 韩晓平  据经济之声《天下财经》报道，国家发改委最近下发通知，对进口原油使用管理有关问题进行了规定，鼓励三类企业可优先使用进口原油，如何理解这项政策？它将产生什么样的影响？天下财经编辑田芳毓就此采访了中国能源网首席信息官韩晓平。  编辑：为什么这三类企业可以有优先权呢？  韩晓平：当前的国际能源市场石油供应非常充足，要抓住这个机会推动油气体制改革。改革油气体制一个最重要的就是要放开石油进口，但是这个放开不是无序的，是有序的。  特别向什么样的企业来放开呢？第一个，你在国外开发石油，获得油源了，以前只能进行贸易，必须把这些油卖给中石油、中石化，现在你可以自己引进这样的油来进行深加工和生产。  另外一个，在整个产品中能生产附加值更高的产品，那么你可以直接进口原油来生产。  第三个就是，我炼的油能耗低，污染非常小，而且油品质量非常好，像这样的一些企业可以直接进口原油，这样对它的产品质量提高是非常有好处的。这样一个过程就把整个产业升级大大推动了一步。  编辑：优化提升的过程中，不能适应就会被淘汰，那对国内企业来说，现在应该怎么做呢？  韩晓平：这一次是有一个门槛的，首先必须大于200万吨。另外，它有些特别要求，比如说，炼一吨油能耗不能超过66公斤，这就要求，我们确实开了这扇门，但是如果你是个产能特别落后的企业，你是没有办法生存，也不会向你开放这扇门的，所以你必须要提高整个产能水平。不改进你的生产工艺，不使你的生产流程按照现代化的企业来要求，不使你的产品质量不断提升，就没有一个发展的空间。  编辑：放松原油进口，是否意味着我们将从政策层面更加重视海外的原油供给？  韩晓平：肯定是这样的。因为我们对外依存度一直在不断增加，现在已经达到60%了，所以我们不能说现在国际油价低，就随便去买油。  在这个过程中，应该更多的去支持那些有我们在海外参加勘探开发的，因为我们往往去勘探开发的那些地方，相对来说跟我们国家还是比较友好的，通过我们这样一个开发和进口，那些市场能够获得更多实实在在的好处，这样对于我们推动“一带一路”的建设，在全球共同的多边合作，保障中国未来的石油供给安全，应该说都是更有利的一个机制。  原标题：专家详解为何鼓励三类企业优先使用进口原油  版权属于: 央广网  原文地址: http://finance.cnr.cn/txcj/20150225/t20150225_517801884.shtml  转载时必须以链接形式注明原始出处及本声明。      上一篇     下一篇  相关推荐 欢迎留言 ",
                "      国内新闻     国际新闻     财经新闻     观点评论     宏观解读     企业舆情      石油技术     石油装备     钻井工程     石油历史      勘探开发     炼油化工     油品销售     燃气管道      石油院校     油田生活  24小时播不停： 尼泊尔运回中国石油美媒称减少对印依赖转向中国  |       油价大涨4%受巴西罢工与利比亚原油出口暂停提振  |      能源局：推动宁夏国家新能源综合示范区建设  |     苏树林大庆往事：当局长后就开始给亲戚安排工作  |       长庆钻井管柱自动化处理系统钻机苏里格投用  |     王宜林会见加拿大不列颠哥伦比亚(BC)省省长简蕙芝  |    中国石油集团召开坚决完成全年稳增长目标任务视频会议  |    油海观潮：顺绿色创新之势谋持续发展之道  |      大众柴油排放丑闻升级：蔓延至奥迪保时捷  |      中国石油测井公司高新成像测井技术走出“深闺”  |        尼泊尔运回中国石油美媒称减少对印依赖转向中国  |      油价大涨4%受巴西罢工与利比亚原油出口暂停提振  |      能源局：推动宁夏国家新能源综合示范区建设  |     苏树林大庆往事：当局长后就开始给亲戚安排工作  |       长庆钻井管柱自动化处理系统钻机苏里格投用  |     王宜林会见加拿大不列颠哥伦比亚(BC)省省长简蕙芝  |    中国石油集团召开坚决完成全年稳增长目标任务视频会议  |    油海观潮：顺绿色创新之势谋持续发展之道  |      大众柴油排放丑闻升级：蔓延至奥迪保时捷  |      中国石油测井公司高新成像测井技术走出“深闺”  |   您的位置： 首页 >  油品销售 巴肯油田减产导致购买成本增加美炼油厂转向进口原油 来源：FX168     责编：杨红雪     发布时间：2015-11-04  石油壹号网消息：PBF Energy公司是美国最大的独立炼油企业之一，曾花费数年时间以及投入大量资金修建Delaware市的铁路原油运输终端，计划增加来自北达科他州巴肯油田的原油输送能力，不过市场分析师周二(11月3日)指出，目前该公司已经放弃了这项计划，转而直接从海外进口原油。  据悉，PBF Energy公司已经关闭了于2013年才刚刚开业的俄克拉荷马市办公地点，该地点主要负责公司在北达科他州的原油业务。  PBF董事长Thomas OMalley是此次改变的主要推手，反映出美国东海岸炼油企业对原油来源的态度转变。  北达科他州巴肯油田产量在2015年6月录得纪录高位每日115.3万桶，而8月已经下滑至每日113万桶。供应量的降低导致巴肯原油的购买成本更加昂贵，因内陆运输费用更高。美国东海岸炼油企业开始转而使用来自拉美、中东和非洲的进口原油。消息人士透露，目前已经有3家炼油企业开始逐渐使用进口原油。  原油交易商表示，PBF公司、Philadelphia Energy Solutions公司以及Delta航空旗下的Monroe Energy公司正计划将巴肯原油使用量削减至2013年以来最低。  Philadelphia Energy Solutions公司11月来自巴肯油田的铁路运输计划仅为17车次，较2015年夏季每月100车次下降显著。美国能源信息署(EIA)数据显示，Philadelphia Energy Solutions公司在2015年1月至7月间的原油进口数量同比增长逾一倍，主要来自于尼日利亚、乍得和阿塞拜疆。 相关阅读      巴肯油田减产导致购买成本增加美炼油厂转向进口            石油壹号网 http://weibo.com/oilone/      24小时新闻排行     热点新闻排行      尼泊尔运回中国石油美媒称减少对印依赖转向...     苏树林大庆往事：当局长后就开始给亲戚安排...     中国石油集团召开新闻宣传工作领导小组会议     中国石油“一体化”模式优质高效建设津华管...  石油联播 更多  俄罗斯：超一半汽车加油站出售伪劣      中石油：加强内部巡视力度     庆祝克拉玛依油田发现60周年宣传片     天津港“8·12”特别重大火灾爆炸事故：中...  网友讨论 查看往期 本期调查     热词： 油价涨跌 油价|消 中海油|B 六连跌结束后油价涨跌的趋势与预测 正 方 [全文] 0人 0人 反 方 周三即9月16日24时油价窗口开启，预计这次将迎来“六连跌”后的首次上调，涨幅大概会[全文]      客户热线：010-83686528　传真：010-83686528-8003 　 投稿邮箱:news@oilone.cn Copyright 2012 oilone.org. All Rights Reserved 石油壹号网版权所有 京ICP备12033856号-1 京公网安备11010102000532号 未经石油壹号网书面授权，请勿转载内容或建立镜像，违者依法必究！          ",
                " 应用 网易首页 登录 注册免费邮箱      考拉海购     邮箱     支付     电商     花田     LOFTER     BOBO     移动端  新闻体育NBA娱乐财经股票汽车科技手机数码女人论坛视频旅游房产家居教育读书游戏健康彩票车险海淘应用酒香 网易首页 > 网易家居 > 正文东鹏瓷砖 涂装发展现状分析 涂装水漆化转型 2015-10-20 09:04:39　来源: 网易家居 分享到：  1  2015年1月1日，号称“史上最严”的新环保法正式施行。与此同时，年轻消费群体逐渐成为市场主力军，带来消费理念的升级，对健康环保的关注度不断提高。纵观2015年的涂装市场，低碳环保无疑是当之无愧的主旋律。  2015年1月1日，号称“史上最严”的新环保法正式施行。与此同时，年轻消费群体逐渐成为市场主力军，带来消费理念的升级，对健康环保的关注度不断提高。纵观2015年的涂装市场，低碳环保无疑是当之无愧的主旋律。市场需求和外部大环境的“内忧外患”，倒逼涂装行业向国际先进水平接轨看齐，更加环保健康的水漆消费将成为涂装市场蓝海。  当前，环境污染问题已经成为全球话题，中国的环境问题更是不容忽视。相关数据显示，目前我国年排放VOC1800万吨，其中涂料涂装行业占四分之一。而当前市场主流的涂装产品——油漆，则含有大量甲醛、苯等有害物质，挥发期长达3-15年，可对人体造成直接伤害。据中国某室内装饰协会环境检测中心透漏，全国每年由于室内污染引起的死亡人数高达11.1万人。  事实上，欧美发达国家早已经认识到油漆的不足，并改用低碳环保的替代产品。如欧盟自2004年1月1日起，全面禁止在欧盟国家内生产、销售溶剂型涂料(俗称油漆)，改用水性涂料(水漆);美国也在2004年出台政策，限制中国油漆家具美国市场的进口。  随着节能减排在世界范围内的推进，中国也积极寻求与发达国家接轨，通过法律、法规加强行业管理，引导行业逐步淘汰高污染、高排放的油性涂装产品，引导行业向低碳环保的转型。与油性涂料相比，水性涂料VOC排放降低了60%到70%，更利于环保，因此导入水性涂料是涂装行业向环保升级的必然趋势，也是国家政策倡导推广的重点。  消费者需求是市场的指南针。时代在进步，消费者也就在升级换代。随着年轻一代消费者成长为市场主体，健康消费、绿色消费等意识和理念日益得到重视。业内专家表示，2015年的涂装市场，基于消费者需求，对更加环保健康的水性涂料的市场需求将会持续增长，市场供求之间存在巨大缺口，水性涂料将成为涂装市场的蓝海，成为推动水漆替代油漆的产业升级的催化剂。  2015年，绿色、环保、低碳将成为涂料市场主旋律，替换传统油性漆的产业升级即将开启，涂料行业企业面临转型和洗牌的压力，水漆则成为撬动市场的利器。据笔者从相关渠道了解，在当前我国的涂装行业企业中，晨阳水漆是国内最先试水水性涂料企业，自上世纪90年代末，致力于水漆的研发、生产，并一直大力推广水漆在环保上的优势。16年来，晨阳水漆积极进行研发投入和技术创新，不仅建有国内唯一一家水性涂料院士工作站，还与中科院工程研究所合作建立了全国唯一一家水性涂装纳米材料实验室，晨阳水漆始终坚持产品品质第一位，严把原材料到生产的每个环节，不让一桶不合格的产品流入市场，以品质赢得了客户的青睐和消费者认可，旗下产品通过了欧盟SGS及德国TUV等国际高标准认证，与中石化、三一重工(600031,股吧)等工业名企及隆基泰和、奥润顺达等地产、节能门窗（门窗装修效果图）巨头互为合作伙伴。目前，晨阳水漆正在整体规划实施千亩产业园项目，全力打造水漆民族第一品牌。  随着社会环境保护意识的提升，绿色家装需求不断扩大，水漆将引领家装消费新趋势。我们也需要更多晨阳水漆这样的行业龙头，以加速环保水漆涂装的推广和使用，让更多消费者能享受到更健康的绿色家居生活。  文中数据来源：前瞻产业研究院《2015-2020年中国涂装行业市场需求预测与投资战略规划分析报告》:http://bg.qianzhan.com/report/detail/56dafc80ec644221.html 吴紫珊 本文来源：网易家居      涂装     水漆     转型  分享到：  网易首页家居首页 下载网易新闻客户端 1 人参与 关键词阅读 48小时评论排行 关键词： 涂装 水漆 转型      首届“泰斯特杯”硅藻泥涂装工艺大赛在京举行 2015/09/27     立邦墙面涂装体系 保护墙面有“一套” 2015/07/08     涂料涂装一体化——惠及多方的涂装之路 2014/12/17     木门涂装：三分油漆七分木材 2014/08/20     涂装界的黑马 防腐涂料发展潜力巨大 2014/07/17  下载网易BoBo手机客户端      小小演奏家 小小的梦     228     小小演奏家 小小的梦     动感热辣好声音     928     动感热辣好声音     妹纸化身白衣天使     2234     妹纸化身白衣天使     香甜可口软妹纸     294     香甜可口软妹纸  1人跟贴 | 1人参与 最新跟贴 p8663111 p8663111 [网易河北省网友] 2015-10-30 14:08:20 随着社会环境保护意识的提升，绿色家装需求不断扩大，水漆将引领家装消费新趋势。我们也需要更多晨阳水漆这样的行业龙头，以加速环保水漆涂装的推广和使用，让更多消费者能享受到更健康的绿色家居生活。      顶[0]     回复     复制  查看全部跟贴>> 1人跟贴 | 1人参与 | 手机发跟贴 | 注册 文明上网，登录发贴 账号 密码 自动登录 登录并发表  网友评论仅供其表达个人看法，并不表明网易立场。 家居推荐 进入家居频道 全家共用一缸洗澡水 外国人在日本生活时的困扰      辣妈装修140平毛坯房 29.7万基建家具全包     江南小镇100万140平3层自建房 一年全程记录     西安女孩在北京建花房，手把手教你建造全过程！  装修俱乐部 阅读全部      非洲风与东方艺术混搭     非洲风与东方艺术混搭     清晨只想让阳光叫醒我     清晨只想让阳光叫醒我      换个窗帘换种心情     空间节省技巧你得知道     橱柜采购 你得知道...     别在地板上犯错误     衣柜设计 教你省空间      瓷砖平整服帖有妙招     影视墙装修全过程     窗帘选购安装细解     预防墙身裂缝有技巧     组合书柜 实用又美观      装修日记     户型分析     施工交流     建材家具     达人扮家     装修基金  家居图集 阅读全部      三室两厅现代混搭     三室两厅现代混搭     89平地中海风情家     89平地中海风情家     165平北欧简约宅     165平北欧简约宅     89平两房两厅混搭     89平两房两厅混搭  热点新闻 网曝王丽坤疑插足于和伟婚姻 两人已相恋3年      台湾卖淫案38名女星完整特征名单曝光     买春名单曝光：天王级艺人与知名女星老公上榜     仅花10万元 北漂大叔回乡自建高大上独院别墅  精彩视频 进入视频频道      男子当街持钢管砸警车     男子当街持钢管砸警车     印度火车箱顶挤数百人     印度火车箱顶挤数百人     男生教室内遭多人暴打     男生教室内遭多人暴打     客机坠毁现场视频曝光     客机坠毁现场视频曝光  家居热图 阅读全部      彭于晏与他的绯闻女友     彭于晏与他的绯闻女友     富豪杜海涛 豪宅遍地     富豪杜海涛 豪宅遍地      谢霆锋砸4000万购豪宅     伏明霞身材走样？     邓文迪主动引诱布莱尔      揭李泽楷数栋豪宅     这个家除了砖全部网购     这二手房竟藏惊人秘密  彩票 贵金属 保险 话费 点卡 电影票      彩票:     双色球 3D 大乐透 七乐彩 时时彩 足彩     彩票开奖:     双色球 3D 大乐透 快3 11选5 七星彩     走势图:     双色球 3D 大乐透 时时彩 七星彩 11选5  点击排行 跟贴排行      12837263仅花10万元 北漂大叔回乡自建高大上独院别墅     2580207全家共用一缸洗澡水 外国人在日本生活时的困扰     3236649辣妈装修140平毛坯房 29.7万基建家具全包     488694西安女孩在北京建花房，手把手教你建造全过程！     566341卡车能拉着寝室到你的城市？侃一侃午睡这些事儿     655047天冷了，教你自制壁炉全过程！     749029江南小镇100万140平3层自建房 一年全程记录     848700刘嘉玲千万豪宅别墅内部全曝光 装潢赛皇家宫殿  博客/论坛 怕被炒美女遮肉弹身材扮丑      北京特产为啥让河南人包了     人民币剧烈贬值情景或再现     火鸡掐架场面惊人     盘点全球“最萌身高差”伴侣     牛圣山秋色  全局导航 网易 有态度 新闻 国内 评论 国际 探索 社会 军事 图片 体育 NBA CBA 综合 中超 国际足球 英超 西甲 意甲 娱乐 明星 图片 电影 资料库 电视 音乐 财经 股票 行情 产经 新股 金融 基金 商业 理财 汽车 购车 行情 选车 车型库 论坛 行业 用车 汽车图片 科技 中国概念股 科技企业库 女人 亲子 艺术 时尚 星座 情爱 免费试用 美容 女人图库 手机/数码 移动 笔记本 手机库 家电 手机论坛 平板 相机手机视频 房产/家居 北京房产 上海房产 广州房产 全部分站 楼盘库 家具 卫浴 衣柜 旅游 户外 贵州 美食 四川 景点 新疆 专题 西藏 教育 移民 考研 留学 公务员 外语 中小学 高考 校园 查看网易地图 下载网易新闻客户端 ? 1997-2015 网易公司版权所有 About NetEase | 公司简介 | 联系方法 | 招聘信息 | 客户服务 | 隐私政策 | 广告服务 | 网站地图 | 意见反馈 | 不良信息举报 ",
                " 参考消息      中国     时事     社会     国际     亚太     趣闻     军事     周边     装备      图片     图说天下     财经     商业公司     评论     海外看中国      科技     探索     IT     译名     双语     漫谈     专题     图闻     锐参考      时事漫画     封面报道     军备办公室     战争之王     读书时间     国际先驱导报      全球十大房价涨幅最高城市     全球十大房价涨幅最高城市      全球10大年平均住房价格涨幅最高城市中，中国有两座城市上榜。>>     中国战机外销仍受发动机掣肘     中国战机外销仍受发动机掣肘      在中国人仿制的“侧卫”机型中，性能最强大的是歼-15、歼-11D和歼-16。>>     中国(广州)国际智能装备暨机器人博览会     北大乾元国学：十年磨砺，正本清源，学以致用     2016年《国际先驱导报》欢迎您的订阅     ad  习马会里程碑意义 滚动新闻      山西巡视反腐完成全覆盖 党政纪处分1880人     20:55     全面建小康 扬帆再起航 ——《中共中央关于制定国民经济和社会发展第十三个五年规划的建议》诞生记     20:55     邓文中：创新不能“太听话” 发展核心科技不能“差不多”     20:41     昆明一黑车女司机遭两“姘夫”谋杀碎尸     20:41     四部门：医疗资源相对集中城市仍存医托诈骗问题     20:41     台媒:英女子划船骑单车绕地球4年 太平洋求婚未婚妻     20:34     民政部：民政政务信息工作存时效性不够等问题     20:20     中俄边境5处实体界碑、地名碑正式落成     20:14     山西巡视反腐完成全覆盖 党政纪处分1880人     20:55     全面建小康 扬帆再起航 ——《中共中央关于制定国民经济和社会发展第十三个五年规划的建议》诞生记     20:55     邓文中：创新不能“太听话” 发展核心科技不能“差不多”     20:41     昆明一黑车女司机遭两“姘夫”谋杀碎尸     20:41     四部门：医疗资源相对集中城市仍存医托诈骗问题     20:41     台媒:英女子划船骑单车绕地球4年 太平洋求婚未婚妻     20:34     民政部：民政政务信息工作存时效性不够等问题     20:20     中俄边境5处实体界碑、地名碑正式落成     20:14   首页>中国频道>时事要闻>正文 山西巡视反腐完成全覆盖 党政纪处分1880人 2015-11-04 20:55:01 来源：中国新闻网 责任编辑：  中新社太原11月4日电 (李新锁)11月4日，山西省纪委发布消息，自2015年4月以来，山西省委安排5个常规巡视组对晋中、晋城等5市及所辖44个县(市、区)、11所院校进行巡视，完成对山西全省市县(区)巡视全覆盖。  期间，巡视组共向5市移交问题线索4311件，其中立案1297件，党政纪处分1880人，移交司法机关处理127人。  2015年4月，在经历塌方式腐败、重建官场之际，山西安排5个巡视组在5个月内巡视58个单位，其中包括了地级市、县区、高校，基层政府，巡视数量体量很大，被舆论称为“史上最长巡视”。  彼时，山西省委要求，本轮巡视要“用最坚决的态度减少腐败存量，用最果断的措施遏制腐败增量”，重构良好政治生态。  11月4日，山西省委巡视办介绍，在本轮巡视中，巡视组边巡视边移交边查处，不断提高巡视质量和效果，实现对山西市县(区)巡视全覆盖。本轮巡视共发现问题线索3228条，涉及省管干部82人、处级干部358人、班子成员158人、党政一把手30人。  山西省委巡视办表示，山西大部分市县区、院校党委管党治党意识不强，党员干部党性观念淡漠，出规破矩问题频现。部分党政机关人员违规经商办企业，一些领导干部插手土地转让、矿产开发等，为亲属及特定关系人经商办企业提供方便。部分市县区选人用人导向不正，违规提拔干部问题突出，“官二代”“富二代”提拔快且身居关键岗位。此外，懒政怠政、不担当、不作为、慢作为等问题突出。公款旅游依然存在，婚丧嫁娶大操大办借机敛财问题时有发生。  实际上，在官场重建过程中，山西从未放松“挖掘腐败存量、遏制腐败增量”。  两天前，山西腐败重灾区——吕梁市两名处级官员在同一天内被“双开”。不久前，山西省长治医学院原院长王庸晋和长治医学院附属和平医院原院长、长治市政协副主席魏武夫妻二人双双落马。  对于此轮巡视成果，山西省委书记王儒林主持召开省委“五人小组”会议，对涉及省管干部的问题线索正移交省纪委、省委组织部和相关部门处理。  “正视问题不回避、惩治腐败不手软，革弊立新、激浊扬清”，不久前，王儒林在向中央巡视组汇报时表示，山西充分发挥巡视“利剑”作用，已形成惩治腐败的高压态势和强大震慑。(完) 本文系转载，不代表参考消息网的观点。参考消息网对其文字、图片与其他内容的真实性、及时性、完整性和准确性以及其权利属性均不作任何保证和承诺，请读者和相关方自行核实。      印尼巨蜥公园内打架 口水战升级为摔跤赛印尼巨蜥公园内打架 口水战升级为摔跤赛     成功嫁入豪门的10大女星成功嫁入豪门的10大女星     鹦鹉患忧郁症自残 几乎将羽毛拔光鹦鹉患忧郁症自残 几乎将羽毛拔光     最美警校女教师走红 长相清纯上课被围观最美警校女教师走红 长相清纯上课被围观  您看完此新闻的心情是      [高兴]      高兴      0人     [感人]      感人      0人     [无聊]      无聊      0人     [搞笑]      搞笑      0人     [震惊]      震惊      0人     [愤怒]      愤怒      0人     [无奈]      无奈      0人     [害怕]      害怕      0人     [难过]      难过      0人      370斤女模特乘飞机被歧视 怒减180斤华丽蜕变370斤女模特乘飞机被歧视 怒减180斤华丽蜕变     猫头鹰收起翅膀掠过天空瞬间猫头鹰收起翅膀掠过天空瞬间     盘点宠物猫狗的绝妙隐身照：傻傻分不清楚盘点宠物猫狗的绝妙隐身照：傻傻分不清楚     西班牙变性人首次参加世界小姐比赛西班牙变性人首次参加世界小姐比赛     澳洲少女天生亚洲脸 每天扮成日本动漫人物澳洲少女天生亚洲脸 每天扮成日本动漫人物     女子街头假装喝醉 测试男人反应女子街头假装喝醉 测试男人反应     泰国17岁少女选美夺冠 回乡跪谢拾荒母亲泰国17岁少女选美夺冠 回乡跪谢拾荒母亲     整牙变脸型 揭露整牙对女星的重要性整牙变脸型 揭露整牙对女星的重要性  网友评论(0人参与，0条评论) 来说两句吧...      微博登录     QQ登录  还没有评论，快来抢沙发吧！ 畅言 精品推荐 图闻 图解国产大飞机C919 锐参考 全面放开二孩，你还会生吗？ 环球调查 国外如何管控危化品风险 精彩图集      只因长太美 女子恋爱多年没法领证只因长太美 女子恋爱多年没法领证     台湾孕妇在美国高空产女 婴儿获美籍母被遣回台湾孕妇在美国高空产女 婴儿获美籍母被遣回     变性女被囚男监 万人忧其遭性侵请求转移变性女被囚男监 万人忧其遭性侵请求转移     猫咪手袋风靡日本 价格超真猫猫咪手袋风靡日本 价格超真猫  热门图片      明星恋情见光死 扒扒娱乐圈10大短命恋情明星恋情见光死 扒扒娱乐圈10大短命恋情     女主播用橡胶易容 “变身”兔子杰西卡女主播用橡胶易容 “变身”兔子杰西卡     摄影师再现15个被动物“养”大的野孩子摄影师再现15个被动物“养”大的野孩子     国粹成玩物：性感钢管舞娘京剧打扮拍片国粹成玩物：性感钢管舞娘京剧打扮拍片  排行榜      24小时     一周     一月      1     港媒：联合国报告称日本13%少女援交 日本否认     2     北美海岸百万海星染病自杀 撕裂自己内脏喷出(     3     台媒:\"火球\"划过曼谷天空 诡异蓝光照亮夜空(     4     俄媒：宇航员在空间站看不到长城 能看见道路     5     摔机着陆！美媒:印空军流血不止 或不敌巴基斯     6     韩日慰安妇问题分歧依旧 安倍独自出门吃烧烤     7     美媒:CIA官员否认因“中国网袭”撤走驻北京特     8     外媒：中国欲借C919客机挑战外国巨头     9     一周军情锐评：俄军在叙亮肌肉 西方最近比较     10     外媒称进口葡萄酒在华遇冷：波尔多红酒15元贱  关于参考消息 | 版权声明 | 广告服务 | 联系我们 | 订阅《参考消息》 | 参考消息网招聘 | 网站导航  国新网备2012001互联网出版许可证（新出网证(京)字147号）京ICP备11013708 京公网安备110402440030  - 参考消息报社 版权所有 - 关闭 关闭 ",
                " logoUPrivacy and cookiesJobsDatingOffersShopPuzzlesInvestor SubscribeRegisterLog in Telegraph.co.uk Search - enhanced by OpenText  Monday 16 November 2015      Home     Video     News     World     Sport     Finance     Comment     Culture     Travel     Life     Women     Fashion     Luxury     Tech     Cars     Film     TV      USA     Asia     China     Europe     Middle East     Australasia     Africa     South America     Central Asia     KCL Big Question     Expat     Honduras      France     Francois Hollande     Germany     Angela Merkel     Russia     Vladimir Putin     Greece     Spain     Italy      Home      News      World News      Europe      France  French air strikes will make little difference, warns Jeremy Corbyn Labour leader says the only way to deal with Isil threat is through a political settlement to Syria's long-running civil war                                                                                                             Email   El Corbyno: non-drinker, non-smoker, vegetarian Jeremy Corbyn doubts air strikes will make any difference in fight against Isil Photo: Nick Edwards/The Telegraph Michael Wilkinson  By Michael Wilkinson, Political Correspondent  9:48AM GMT 16 Nov 2015 Follow  French air strikes against Isil targets in Syria will make little difference, Labour leader Jeremy Corbyn has warned.  Following Friday's terror attacks in Paris, French warplanes mounted a series of strikes against the Isil - also referred to as Islamic State or Isis - stronghold of Raqqa.  \"I am not saying 'sit round the table with Isis', I am saying bring about a political settlement in Syria which will help then to bring some kind of unity government.\" Jeremy Corbyn  However Mr Corbyn said that the only way to deal with the threat from Isil was through a political settlement to Syria's long-running civil war.  \"Does the bombing change it  Probably not. The idea has to be surely a political settlement in Syria,\" he told ITV1's Lorraine programme.  He added: \"We have to be careful. One war doesn't necessarily bring about peace, it often can bring yet more conflicts, more mayhem and more loss.\"  The Labour leader criticised the media for focusing on Paris while at the same time giving \"hardly any publicity\" to an Isil attack in Beirut last week which killed more than 40 people or a bomb exploding at a peace rally in Turkey last month which killed 97 people.  He said: \"I think first of all what happened in Paris was appalling. This is a vibrant, multi-cultural city, young people of all faiths, and older people as well, all there together and cultures and this terrible thing happened. Likewise, which didn't unfortunately get hardly any publicity, was a bombing in Beirut last week or the killing in Turkey and I think our media needs to be able to report things that happened outside Europe as well as inside Europe. A life is a life.\"  Mr Corbyn acknowledged that achieving a settlement in Syria would be \"very difficult\" but said that international talks over the weekend appeared to have made some progress.  \"I am not saying 'sit round the table with Isis', I am saying bring about a political settlement in Syria which will help then to bring some kind of unity government - technical government - in Syria,\" he said.  The Labour leader said that the rise of Isil had raised some \"very big questions\" as to who was behind the jihadist group.  \"Who is funding Isis  Who is arming Isis  Who is providing safe havens for Isis  You have to ask questions about the arms that everyone has sold in the region, the role of Saudi Arabia in this. I think there are some very big questions,\" he said.     While Mr Corbyn said that the attacks in Paris were \"appalling\", he criticised the British media for giving little coverage to recent terrorist attacks in Turkey and Lebanon.  \"I think our media needs to be able to report things that happen outside Europe as well as inside Europe. A life is a life,\" he said. Follow @telegraphnews                                                                                                              Email     Promoted stories      What's the Most Popular Baby Name in Your State  AllAnalytics     TED Talks Live Continues With \"War & Peace\" at the… TheaterMania     Hollande says 'terrorists' at Paris Bataclan hall killed Nikkei Asian Review     Striking the Balance Between Information… CollaboristaBlog     Citi: English Proficiency Ensures Consistency Why English Matters by TOEIC     9 Tile Ideas To Upgrade Your Home Dwell     Stunning Moments of Discovery Through Travel… CNN     Top 5 Reasons The Philippines Is A Great Base… Century Spire Offices on INQUIRER.net     How Bad Were the First World War Generals  Dummies.com   Recommended by   Telegraph Sponsored      Maggie Smith: major roles     The brotherhood that inspired The Last Panthers     Seven habits to help you age happily   Recommended by  Top news galleries Gruesome food: 20 of the world's most bizarre dishes Gruesome food: 20 of the world's most bizarre dishes    As I'm A Celebrity... Get Me Out of Here! returns, we look at 20 of the strangest culinary delicacies from around the world Charles and Camilla down under Prince Charles (R) and his wife Camilla, Duchess of Duchess, wave farewell as they depart Australia in Perth    In pics: Duchess of Cornwall and Prince of Wales tour of New Zealand and Australia Pictures of the day Skipper the adventure dog goes for a paddle with his owner Jon Taylor on the canal in Wheaton Aston, Staffordshire    A paddling doggy, a London sunset and Paris in mourning Best looks from the weekend Best looks from the weekend    Jessica Alba and Gwen Stefani bring glamour to the Baby 2 Baby Gala, while other film stars flock to LA’s annual Governor’s Ball 25 great closing lines in films Claude Rains, Paul Henreid, Humphrey Bogart, Ingrid Bergman in 'Casablanca' . It had its premiere 70 years ago - on 26 November 1942   Film   Martin Chilton looks at some great final lines to movies Comments Rita Ora: Eurovision flop to X Factor New 'The Voice' judge Rita Ora with Ricky Wilson   Music    Ahead of tonight's X Factor results, here's the story of Rita Ora's unlikely rise Comments #PeaceForParis A peace vigil in Kathmandu    In pics: French cartoonist Jean Jullien's poignant drawing Paris terror attacks in pictures    In pics: Images from a night of carnage in France's capital as scores are killed and injured in Paris The 10 biggest doping scandals in sport - is the Russian drugs expose top     After Russia was accused of a state-sanctioned drugs programme in athletics, Ben Rumsby assesses the biggest doping scandals to hit sport Comments David Beckham Unicef match: Sports and celebrities including Sir Alex Ferguson and Ronaldinho turn out at Old Trafford    The stars were out for the 'Match for Children' on Saturday Comments  Ads by Google      British Expat In China       Avoid Losing 55% Of ￡70k+ Pensions Download A Free Expat Pension Guide      your.expatpensionreview.com     Expat Living In China       ￡50k-￡1m Or ￡500+ Regular Savings  Review Your Interest Rates Today!      expatsavingsreview.com     理财资讯：0元开户,炒黄金送$200      恒信贵金属,4倍赠金,开户送200美金,0佣金 0手续费,T+0双向交易,涨跌皆赚,免费开户      www.hx9999.com  Latest Video  Paris attacks: Anonymous declares war on Isil Anonymous declares war on Isil people have created a mural in Port de Solferino, Paris, under the words: WATCH LIVE Live: Paris marks minute silence Paris attacks: Girlfriend's moving tribute to British man killed at Bataclan theatre Girlfriend's moving tribute to British vicitm Mariesha Payne (L) and Christine Tadhope (R) (APTN) Survivors hid in cellar to escape gunmen Paris attacks: Sky News presenter Kay Burley reports from cafe following panic from false alarm News presenter during moment of panic BBC's Nick Robinson to have tumour removed BBC Radio 4 pips interrupted by person saying 's***'  More from the web      Profit from property without purchasing     Profit from property without purchasing      Rise in property prices, especially in London, can offer exciting opportunities for investors     View  More from the web      The future of online learning     The future of online learning      Fast, fun and fully interactive language courses available online.     View  More from the web Promoted stories The French and Indian War The French and Indian War (Dummies.com) Stormfall: Free and Addicting strategy game! Prepare for war! Stormfall: Free and Addicting strategy game! Prepare for war! (Stormfall) How to Use the SAS Pi Constant How to Use the SAS Pi Constant (AllAnalytics) Boston Children's Theatre Offers Theater Workshop to Military Families Boston Children's Theatre Offers Theater Workshop to Military Families (TheaterMania) Recommended by  SPONSORED FEATURES Telegraph Jobs Search for a new job and get help with your career today View Telegraph Jobs Why CV action words matter and how to use them View Telegraph Courses Discover a new language experience View Careers Advice What is your business worth  View  Back to top      HOME     News     UK News     Politics      Long Reads      Wikileaks     Jobs      World News     Europe     USA     China      Royal Family News      Celebrity news     Dating      Finance     Education     Defence      Weird News      Editor's Choice     Financial Services      Pictures     Video     Matt     Alex      Comment      Blogs     Crossword      Contact us     Privacy and Cookies     Advertising     Fantasy Football      Tickets      Announcements     Reader Prints      Follow Us     Apps     Epaper     Expat      Promotions     Subscriber     Syndication    Copyright of Telegraph Media Group Limited 2015  Terms and Conditions  Today's News  Archive  Style Book  Weather Forecast ",
                " IEA Logo About News Publications Topics Countries Statistics EventsWorkshopsSpeechesNewslettersIEA in the newsMultimedia International Energy Agency  > Newsroom & events > IEA journal > Issue 7 Chinese national oil companies' investments: going global for energy A CNOOC facility in China. Investment abroad by the country's NOCs can bring home technologies needed to tap unconventional reserves.  The IEA tracks Chinese oil firms’ growing overseas investments, finding they are driven by commercial interests but may affect foreign policy  3 November 2014  Chinese national oil companies (NOCs) are the new big players on the global energy scene. In the last three years, they spent a total of USD 73 billion in upstream investments and now operate in more than 40 countries to control about 7% of worldwide crude oil output, raising alarms in some quarters about supply security and price.  To address those concerns, the IEA investigated the NOCs’ spending, producing two reports. The first, which in 2011 quantified the size and growth of investments, determined that the Chinese NOCs had been responsible for 61% of acquisitions by all NOCs in 2009. It also found that the Chinese companies’ overseas investments had, for the most part, increased global oil and gas supplies for all importers. A follow-up report this year confirmed the 2011 findings, adding that combined overseas oil and gas production by Chinese companies totalled 2.5 million barrels per day (mb/d), reflecting a geographic diversification of assets.  A growing degree of independence  The IEA studies, Overseas Investments by China’s National Oil Companies: Assessing the Drivers and Impacts and Update on Overseas Investments by China’s Oil Companies, did not find cause to believe that the Chinese NOCs operate under the direct instructions of, or in close co-ordination with, the central government. Instead, the studies determined that the companies, especially the big three – China National Petroleum Corporation (CNPC), parent company of PetroChina; China Petroleum & Chemical Corporation (Sinopec Group); and China National Offshore Oil Corporation (CNOOC) – had benefitted from three decades of economic reforms to gain a great deal of power in relation to the government.  China’s soaring domestic energy consumption has increased the NOCs’ financial and economic impact, giving them the means to lobby for greater influence. With domestic production stalled at just over 4 mb/d for the past two years, imports meet 59% of Chinese demand – which grew 3% last year and is expected to overtake that of the United States in 2030. While the NOCs remain primarily owned by the state, the studies found that they have carved out significant operational and investment independence from the government because of their historical associations with former ministries; top company officials’ high ranks within the Communist Party; the fragmented and decentralised nature of Chinese energy governance structure; and the companies’ much larger sizes and lobbying power relative to the government agencies tasked with overseeing them.  No evidence emerged from the IEA research that the Chinese government requires the companies to ship back overseas production to China, as some critics have suggested. Instead, the studies found, NOCs’ decisions about equity oil’s marketing are mainly based on commercial considerations, the production-sharing contracts involving the investments, or both.  “The IEA report (2011) disproved the common misconception that China’s NOCs were acting overseas under the instruction of the Chinese government,” the 2014 study says, adding that “further research conducted for this updated publication has uncovered no evidence to suggest that the Chinese government imposes a quota on the NOCs regarding the amount of their overseas oil that they must ship to China”.  Investments in countries near and far  Two big changes the IEA detected between the studies are the companies’ new emphasis on investment in unconventional oil and gas, and a reorientation from high-risk regions to areas with more stable geopolitics. The recalibration has come as countries in the Americas and Australia have been more welcoming of Chinese investment, while investments in other regions have had less success amid rising nationalism and political uncertainty.  The first significant setback for Chinese investment was in Sudan. Chinese NOCs are the biggest investors in South Sudan’s oil industry, but the investment was originally made before the new nation came into existence.  Sudan was among the very first countries to attract Chinese interest, with activities dating back to 1995. That involvement forced China to weather international scrutiny during the Darfur crisis, but by 2010, Chinese equity production in the country, most of whose oil fields are in the south, was 210 000 barrels per day (kb/d). After South Sudan’s 2011 independence, oil transport negotiations deadlocked, and by early 2012, nearly 900 Chinese-operated wells were shut or forced to reduce production. South Sudan expelled President Liu Yingcai of the Chinese-Malaysian oil consortium Petrodar for “non-cooperation”. By the end of 2013, CNPC and Sinopec reported oil production of only 84 kb/d in South Sudan and Sudan.  Unrest in South Sudan has not gone away, with intermittent fighting this year. But contrary to the position it adopted during the so-called Arab Spring and in Syria in particular, the Chinese government has sought to be a major mediator among the various factions.  Similarly, unrest in Iraq continues to threaten Chinese NOCs’ combined 472 kb/d production entitlement – 25% of all Chinese overseas oil output. China has long viewed Iraq as a replacement for reduced flow from Iran.  From 2007 the NOCs invested no less than USD 14 billion in Iranian oil and gas fields, making the country China’s third-largest oil supplier in 2010 and 2011, with 11% of total imports. But the effects of international sanctions dropped Iran to sixth place as of the end of 2013, just after Iraq and well behind top-ranked Saudi Arabia, which provides about half of all imports.   Libya also has been a deep disappointment, with fighting there cutting exports to China by more than one-third, to 0.8% of total imports in 2013. The government also had to arrange an emergency plan to evacuate 35 000 Chinese nationals from the country during the overthrow of the Qaddafi regime, and it subsequently has been involved in complex discussions concerning its pre-2011 contracts.   Then there is Syria, where Chinese companies had a total equity production of 84 kb/d, which fell to below 53 kb/d in 2011. By the end of 2013 only the small NOC Sinochem was still producing oil there, about 2.5 kb/d.  Security challenges have affected NOCs’ operations in Myanmar and Nigeria and potentially in Central Asia, where the competitive edge held from early entry is threatened by growing ethnic tensions and terrorist threats in the some of the five countries through which the Central Asia-China pipelines run. More trouble may follow, as CNPC successfully bid in 2012 to be among the first companies to explore for oil and gas in Afghanistan.  Not all of the NOCs’ setbacks have been the result of political unrest: some of their African investments, including in Niger and Chad, are at risk of reversal of contracts.  The companies have had greater success in Russia, Saudi Arabia and Central Asian countries such as Turkmenistan and Kazakhstan, where energy deals have been combined with other investment. Sinopec entered the refining industry in Saudi Arabia by investing USD 4.5 billion in the company’s first international downstream deal. A loan-for-gas deal with Turkmenistan secured 25 billion cubic metres (bcm) of gas supply, bringing total supply capacity to 65 bcm per year.  Increased co-operation with Western firms  The uneven results of investment in high-risk regions have led to a shift in Chinese investment to more stable regions as well as closer co-operation and co-ordination with independent oil companies of Western countries. Every Chinese NOC has expanded its global production portfolio significantly, particularly in North America and Australia, usually through direct acquisition when possible. Along with smaller Chinese companies, the NOCs invested USD 38 billion in 2013 in global upstream oil and gas mergers and acquisitions, up from USD 15 billion in 2012 and USD 20 billion in 2011. This shift not only has smoothed investments and purchases, according to the IEA report this year, but also has furthered China’s mastery of techniques it hopes to use for domestic production.  China’s National Energy Administration addressed research,development and demonstration projects in 2012’s 12th Five-Year Plan for Energy Technology, which calls for domestic development of shale gas, heavy oil and other unconventional energy sources. The Ministry of Land and Resources estimates that domestic shale gas reserves exceed the United States’, and expertise from NOCs’ investments in foreign companies could help develop those reserves. Global majors including Shell, ConocoPhillips, ENI and Total have co-operation accords with NOCs to conduct seismic surveys, exploration, and joint research to develop shale gas and oil blocks in China. But the new IEA study notes the significant differences between US and Chinese reserves, which will mean a potentially challenging adaptation of North American drilling technologies to China’s geological specifications.  Commercial influences on foreign policy  Given 21 years of surging investment in countries around the world, it was inevitable that Chinese NOCs would run into challenges and generate concerns. The IEA studies found that the companies have relied heavily on Chinese government support in the Middle East and in Sudan and South Sudan, raising two questions: will China’s commercial interests help shape the country’s foreign policy in these regions, and to what extent does the existence of substantial energy and other commercial investments already influence China’s diplomatic decisions  Today, perhaps the greatest challenge facing the NOCs is that their business interests are highly dependent on the evolution of Chinese foreign policy.  But the NOCs are not waiting as they charge ahead with overseas expansions and keep the pressure on both themselves and their government to garner more experience in their dealings in the international energy arena.  IEA keeps monitoring all NOCs’ investments  Given the potential effect on global energy security and engagement, two of the Agency’s principal concerns, the IEA carefully monitors ongoing investment by all NOCs around the world. While the Chinese companies’ overseas investments may have originated as primarily commercial moves, recent events have politicised many of them, and the IEA is among many observers watching how the Chinese NOCs and their government find ways to work with each other – and other players in the global energy sector – to reconcile these political and security issues.   This article by former IEA China Programme Officer Julie Jiang, originally appeared in IEA Energy: The Journal of the International Energy Agency. Julie Jiang left the IEA in 2014 after five years, during which she co-ordinated bilateral co-operation projects with China and co-wrote four publications. Prior to joining the IEA, she was a manager for the US Foreign Commercial Service’s programme in China.   Both Overseas Investments by China’s National Oil Companies: Assessing the Drivers and Impacts and Update on Overseas Investments by China’s Oil Companies are available for free; click on the title of each to download it.  Through the end of 2014, the IEA regularly produced IEA Energy, but analysis and views contained in the journal are those of individual IEA analysts and not necessarily those of the IEA Secretariat or IEA member countries, and are not to be construed as advice on any specific issue or situation. Click here to view past issues of IEA Energy.        Browse IEA news by topic: China Oil Global Engagement inShare88 Tweet Recent News      Executive Director meets with Australian Prime Minister     IEA releases Oil Market Report for November     IEA hails launch of Canadian CO2 storage project, first to cut emissions from oil sands     Free IEA headline statistics aim to widen understanding of energy production and use     IEA updates carbon emissions data ahead of climate change negotiations, revealing recent trends      TwitterFacebookLinkedInYouTubeGooglePlus  About us      Delegates     Outlook     History     Members     Contact us     Executive office     Jobs     Training     Technology Platform  Topics      Carbon capture     Climate change     Coal     Electricity     Energy efficiency     Energy poverty     Energy security     Energy technology     Natural gas     Nuclear     Oil     Renewables     Transport  Countries      Member countries     Non-member countries  News      Events     IEA in the news     Multimedia  Statistics      Data services     Statistics by country     Policies & Measures Database      Contact us Terms and Conditions, Use and Copyright Sitemap Delegates     2015 OECD/IEA",
                " logoUPrivacy and cookiesJobsDatingOffersShopPuzzlesInvestor SubscribeRegisterLog in Telegraph.co.uk Search - enhanced by OpenText  Sunday 15 November 2015      Home     Video     News     World     Sport     Finance     Comment     Culture     Travel     Life     Women     Fashion     Luxury     Tech     Cars     Film     TV      Home      Archive  01 February 2013 Archive  News  |  Finance  |  Motoring  |  Sport  |  Travel  |  Food and Drink  |  Gardening  |  Education  |  Technology  |  Culture  |  Comment  |  Sponsored  |  Promotions  |  Women   News Egypt protesters attack Mohammed Morsi's palace Gay marriage could cost Conservatives power, poll suggests David Cameron told to honour defence promise Jo Swinson plans crackdown on scam mail Service chiefs deserve clarity on the defence budget Eastleight by-election is a wake-up call, rival warns David Cameron Major Sir Michael Parker interview: Your Majesty, I said, it’s all going terribly wrong David Cameron's schoolfriend says his rival would be 'a great prime minister' Hillary Clinton steps down as US Secretary of State Jenkins’s plea to Darling deserves a firm rebuff Dispatch: Inside Timbuktu, the city freed from its al-Qaeda tormentors How serious is the conspiracy to bring down Prime Minister David Cameron  Sorry, but Adam Afriyie - Britain’s Barack Obama - never had a hope Hillary Clinton: timeline of a political life The Lib Dems turn their backs on a founding father of democratic voting reform From Somerset to Sellafield: it's a thumbs-down to storing nuclear waste Israel 'considering further air strikes on Syria' Missing Irish tycoon found 'ragged, lost with an insult carved on his head' Have some sympathy for our glum French friends Holding a candle in the Temple Francois Hollande to remove word 'race' from French constitution Two arrested after teenager's body found burning in alleyway Super Bowl Ravens vs 49ers: how politics took over the Greatest Show on Earth Ed Koch Peter Beales Mystery acid attack leaves Victoria's Secret store assistant scarred for life Animal pictures of the week: 1 February 2013 Mid Staffs: Ignoring Francis would 'betray' patients, says charity Suicide bomb outside US embassy in Ankara Hague's 'disappointment' as Argentina pulls out of Falklands talks The week in pictures: 1 February 2013 Stalingrad anniversary: 70 years on, Russian city still gives up its WWII dead Hispanics to overtake whites in California next year Is Iran's space monkey a fake  'Robin Hood' couple could lose Kenyan holiday home to repay cannabis profits The BBC's phantom foot fetishist seems to have struck again Sir Andrew Motion: 'Count stars to help fight light pollution' Horse meat burger expert appointed to Ireland's equestrian sport body The view from the top of the Shard: interactive panorama Burglary victim’s mission to track stolen phone becomes online hit Female police officer arrested in Plebgate row Crime maps allow public greater scrunity of offences on their doorstep Teacher 'demanded ￡6,500 not to have child expelled' David Cameron given a lecture on 'debt' and 'deficit' by top statistics official Family of teenager stabbed to death 'relieved' at murder convictions Truck carrying fireworks explodes causing road bridge to collapse in China MP calls for crackdown on lobbyists Alexander Lebedev's airline Red Wings suspended Reporter whose evidence convicted April Casburn says police officer was 'sacrificed' by News International Blame your council tax hike on Jimmy Savile, says police leader Former policeman plans to quiz ex-Libyan spy chief over Yvonne Fletcher's murder Joe the orphaned baby elephant 'wasting away' after mother was poisoned 'Surreal and hilarious': Armando Iannucci receives an OBE Entrepreneur sells 8 million cans of scented fresh air in smoggy China Fireworks explosion kills 26 in China Japan 'may revisit Second World War statement' Thieves abandon toddlers in the street half a mile after stealing mother’s car Zookeeper dressed as zebra makes a break for freedom in Japan Boris Johnson opens The View From The Shard viewing deck in London New York mayor Ed Koch in his own words Former New York mayor Ed Koch dies Man overjoyed at escaping prison jailed for kissing stranger in celebration Nappy wars in Norway: supermarkets lure eastern Europeans Zimbabwe 'would be shut down if it was a company' David Cameron pledges to keep spending 'billions in overseas aid' Australian DJs behind Kate hospital hoax call will not face charges - CPS Explosion at headquarters of Mexico state oil company David Cameron backs George Osborne amid rumours of plot to oust him, Downing Street says Vegetable oil fallen off a ship may be to blame for bird deaths Pictures of the day: 1 February 2013 Sabotage suspected at toppled wind turbine as second is brought down People who abuse troops must face tougher punishments, Labour says 'Gay' dog on death row saved Whooping cough cases up tenfold as another baby dies Senior Met Police detective jailed for 15 months over corrupt payments Marion Cotillard wins Harvard's Hasty Pudding David Cameron: Police probe into 'plebgate' must be thorough Former RAF radar station with bunker and helipad for sale on eBay 'All our women look like Kate Middleton', Romanian ad campaign claims Householders up in arms over ￡20-a-year garden-gate levy Man found dead at recycling plant after climbing into a bin MPs call for criminal inquiry into toxic hip implants Russian rocket plunges into Pacific Pussy Riot member sent to prison hospital Thousands of Cambodians gather to mourn Norodom Sihanouk Shard opening: first marriage proposal 800ft above the Capital Super Bowl: Americans to eat 1.23 billion chicken wings Ashcroft: EU referendum promise has done little to boost Tory support North Korea covers tunnel at nuclear test site Woman claims she lost eye to contact lens fungus infection US teacher suspended after racy Twitter photos Serving Met Police officer arrested over alleged corrupt payments Britain to bathe in winter sunshine but only for a day Iran stepping up support for Syria, Hillary Clinton warns US considers firmer action against Chinese cyber-espionage 'Zorbing' survivor speaks of 'fear' in friend's eyes before death Argentina pulls out of Falklands talks Sharia divorces could be allowed after legal ruling Thousands of women with advanced ovarian cancer left 'frustrated' after Nice denies them drug EastEnders star Leslie Grantham blames wife for breakdown of his marriage Whatever happened to Antonia de Sancha - the kiss-and-tell lover who brought down David Mellor  Why the Duchess of Cornwall's nightie rules out queenly ambitions Liam Fox denies claims that 'plotter' Adam Afriyie is his stalking horse Kim jong-un smokes cigarette while touring hospital First butterflies, now moths decline Older pregnant women should be induced at an earlier stage to reduce stillbirths: research Britain's bulwark against fundamentalism - Songs of Praise Military search-and-rescue helicopter overhaul 'could put lives at risk' Richard Briers: The Good Life star battling emphysema Richard Dawkins attacks 'irrelevant' religion in Rowan Williams debate Assisted suicide: GMC signals doctors safe to provide medical records to Dignitas patients Sperm donors can seek more parental rights Armenian presidential candidate shot ahead of election The night sky in February 2013 200 officers a year retire or resign to avoid disciplinary proceedings Hillary Clinton: Iran stepping up support for Syria  Back to top   Finance Afren shares leap as Sinopec said to eye $1bn assets Wall Street closes in on record high as FTSE 100 surges IMF hits Argentina with first-ever censure of a country Britons pile into shares to profit from 'great rotation' A four-day week could work in Britain too, says psychologist Traders sell-off New World Oil and Gas World economies need more than new year cheer to build on Iberia workers to hold five-day strike UK manufacturing expands for second month Britain is 'broadband leader' thanks to BT cash, boss Ian Livingston claims Britain's Truphone wins backing from Roman Abramovich UK factories avoid European slowdown IAG to press ahead with Iberia cuts Swap scheme ￡10m threshold could block billions of pounds of claims, says experts Hilton backs e-visas to open door to China 'Bankruptcy lite' is up 7pc in a year How I made a year’s net salary in a month Russia's Rosneft disappoints with fourth quarter profits of ￡1.2bn Barclays boss waives bonus amid fresh questions over Qatar Barclays boss Antony Jenkins waives bonus China's Geely saves London cab maker Manganese Bronze Why David Beckham can afford to give away a ￡3.4m salary Rupert Murdoch to spend billions on video rights Qinetiq secures ￡1bn contract with the MoD US creates more jobs than expected, eases growth fears ￡40,000 cost of credit cards Are you being penny wise by shopping in pound shops  London Metal Exchange loses its only woman trader Exxon profits hit five-year high Fungus hits Tate & Lyle profits Barclays: the Qatar Question and the Companies Act 'This Government is committed to helping first-time buyers' Pensioners face cuts on fuel allowances, bus passes and TV licences How the Bank of Mum and Dad has expanded Dutch state nationalises SNS Reaal bank 'Santander failed to pay interest on my account for four years' Airline compensation: What are your rights  Taxpayers who missed the self-assessment deadline can still avoid ￡100 penalty HSBC completes ￡5.8bn sale of Ping An stake Making money: Five life-changing finds from a walk in the wild FTSE 100 post strong start to February Chinese firm Geely saves London taxi cab maker Manganese Bronze The world's best private islands for sale Tax blow for holiday home owners BT beats forecasts, helped by high-speed broadband rollout and TV push Is there any hope for first-time buyers  China manufacturing expands in Janaury Swap mis-selling victims: The restaurateur, the electrical retailer and the shop owner 'Ban fees to stop lenders profiting from company failures' Career coach: Do you really need to go to university  City Diary: A loan by any other name at the OFT, and for ￡8m Questor share tip: Cranswick brings home the bacon Questor share tip: Royal Dutch Shell a hold after tough fourth quarter Barclays investigated over claims it lent Qatar money to invest in itself UK lags Germany and France on Chinese investment Swap mis-selling and the dark heart of the City Banks face ￡10bn bill over swaps mis-selling scandal Interactive graphic: FTSE 100 enjoys best January since 1989  Back to top   Motoring The world's best performance cars What car for a fisherman  Toyota's GT86 convertible concept car Mazda6 review  Back to top   Sport Dundee United v Rangers: Ally McCoist tries to take heat out of Cup clash Roberto Mancini hopes Europe distracts Manchester United from title race Ryder Cup 2013: captain's picks key to Paul McGinley's game plan against the Americans Baltimore Ravens linebacker Ray Lewis looks for fairy-tale end in Super Bowl but dark past still haunts him How Newcastle United put the accent on French in their battle for Premier League survival Leeds Rhinos 36 Hull 6: match report St Helens coach Nathan Brown to forget old friendships for a day as he eyes Huddersfield Giants scalp Reading striker Adam Le Fondre proud to silence his doubters Dan Cole's leading role on the road to redemption Scottish Cup fifth-round previews: Rangers and Celtic in action Former Liverpool goalkeeper Alexander Doni 'nearly died' after suffering heart attack while at Anfield Six Nations 2013: Wales need to reconnect with the lost heartland of the game at Pontypool Six Nations 2013: reformed tight-head prop Dan Cole fuels England’s championship hope QPR manager Harry Redknapp delivers suspect defence for deadline-day spree Manchester City v Liverpool: Daniel Sturridge sees signs of a telepathic understanding with Luis Suarez Alan Pardew's French connection could be gamble worth taking to revive Newcastle United's fortunes Wayne Rooney hands over penalty-taking duties to Manchester United team-mate Robin van Persie Arsène Wenger admits negative pressure has affected his Arsenal players this season Hurricane Fly still looks the one to beat in the Champion Hurdle at the Cheltenham Festival Six Nations 2013: England's road to the 2015 World Cup begins at Twickenham against Scotland Six Nations 2013: Scotland boosted by mellowing of former England forward Dean Ryan Six Nations 2013: revealing statistics will not shock Scotland, says interim coach Scott Johnson England v Scotland: Joe Launchbury aims to punch above his weight again in Six Nations opening game Six Nations 2013: history of Beattie clan suggests a rerun of 1983 and Scotland's last win at Twickenham England's women put faith in youth as 2013 Six Nations title defence begins against Scotland Chelsea interim manager Rafael Benítez is left frustrated by international break Peter Odemwingie directs his fire at West Bromwich Albion after QPR transfer debacle England Saxons 9 Scotland A 13: match report England v Scotland: Gavin Hastings says visitors must hurt England and try to win ugly in Six Nations opener Six Nations 2013: what became of Scotland's stars of 1983 Six Nations 2013: Sir Ian McGeechan looks at the tactics that will be used on the opening weekend England v Scotland: Stuart Lancaster's men must avoid sudden bust after boom that saw All Blacks defeated Six Nations 2013: England promise to handle great expectations in Calcutta Cup match against Scotland Cardiff Blues 10 London Irish 6: match report Six Nations 2013: Thierry Dusautoir's influence looms large for France ahead of Italy clash Six Nations 2013: Andrea Masi says Italy are aiming for two championship victories Paolo Di Canio considering his future as Swindon Town manager Six Nations 2013: Declan Kidney's Ireland break tradition and trust in youth Celtic new boy Tom Rogic on his 'X-Factor' big break So a Scotsman believes the English to be arrogant  How outrageous! Alex Ferguson denies FA charge for comments made following Manchester United's draw with Tottenham French connection could be a gamble worth taking for Newcastle Desert Classic 2013: late surge eases Sergio Garcia's pain in Dubai Nicky Henderson looks for Cheltenham Festival clue from Captain Conan at Sandown Hurricane Fly still looks the one to beat in the Champion Hurdle Norwich City manager Chris Hughton hails the arrival of Luciano Becchio from Leeds United Six Nations 2013: let battle commence for the most-entertaining rugby competition on the planet Arsenal v Stoke City: match preview World Cup 2014: finals artwork through the years in pictures Manchester City v Liverpool: match preview West Bromwich Albion v Tottenham Hotspur: match preview Newcastle United v Chelsea: match preview Chelsea striker Fernando Torres left out of Spain squad for Uruguay friendly, while Swansea's Michu misses out Fulham v Manchester United: match preview Wigan Athletic v Southampton: match preview West Ham United v Swansea City: match preview Reading v Sunderland: match preview Everton v Aston Villa: match preview West Brom striker Peter Odemwingie dropped for Tottenham game following transfer deadline day farce Carl Froch IBF super-middleweight title defence with Mikkel Kessler to take place at 02 Arena on May 25 Six Nations 2013: Welsh hoping to cash in on Leigh Halfpenny 's speedy return from injury Queens Park Rangers v Norwich City: match preview West Ham United co-chairman David Sullivan calls agents 'scavengers' after 'deeply unpleasant' transfer window Sports pictures of the week: February 1, 2013 Manchester City manager Roberto Mancini vows to close gap on neighbours United at top of Premier League table British Basketball can now target Rio 2016 Olympics after receiving funding reprieve from UK Sport Manolo Saiz admits three Liberty Seguros-Würth riders worked with Operation Puerto doctor Eufemiano Fuentes Six Nations 2013: Wales captain Sam Warburton confident of success despite worst run in nine years Reading failed in bid to sign Gylfi Sigurdsson from Tottenham on transfer deadline day Ferrari launch new Formula One car for 2013 F1 season as Stefano Domenicali demands flying start Six Nations 2013: England v Scotland Calcutta Cup Quiz Carl Froch and Floyd Mayweather a cut above on Telegraph Sport's quarterly pound-for-pound rankings ICC Women's Cricket World Cup 2013: last-ball agony for England as Sri Lanka claim dramatic win Italy v France: Thierry Dusautoir returns to French team for Six Nations opener in Rome Liverpool's Raheem Sterling charged with assault Arsene Wenger: signing Spain left-back Nacho Monreal vital for Arsenal after Kieran Gibbs injury Britain's Shelley Rudman wins world skeleton gold after blitzing her rivals in St Moritz David Beckham joins PSG – French media hail arrival of 'David the Magnificent' Six Nations 2013: Stuart Lancaster aims to follow All Blacks model as her prepares for Scotland opener Manchester United defender Jonny Evans poised to return to action with Premier League leaders at Fulham Premier League January transfer deadline day: in pictures Manchester City manager Roberto Mancini 'sorry' for Mario Balotelli's exit to AC Milan West Indies embarassed for 70 as Australia claim for first ODI honours in Perth Phil Mickelson lips out on final hole at Phoenix Open as bid for a 59 agonisingly fails Leeds coach Brian McDermott signs ground-breaking new deal ahead of Super League opener Philippe Coutinho will prove a major success at Liverpool, insists fellow Brazilian Lucas Leiva Newcastle v Chelsea: Demba Ba will not be fazed by St James' Park fans, says Cesar Azpilicueta 'Unprofessional' Peter Odemwingie told to focus on West Bromwich Albion duties after farcical deadline day drama New Zealand v England: Ross Taylor returns to Black Caps' ODI and Twenty20 squads Six Nations 2013: England, Scotland and Wales captains describe the thrill of leading their countries Football's transfer window needs a great big brick thrown through it before this timebomb goes off Be very careful, David. Do not start waving the flag for Qatar 2022 David Beckham needs to beware the motives of Qatar moneymen behind his move to Paris St-Germain Vijay Singh's spray disaster smacks of such stupidity that the golfer should start a new career as a stand-up comic Tottenham Hotspur concede defeat in transfer window deal for Brazil striker Leandro Damiao Transfer window closes in shambles as West Brom block Peter Odemwingie's move to Queens Park Rangers Alex McLeish considering future at Nottingham Forest after failed attempt to sign George Boyd  Back to top   Travel Renzo Piano buildings around the world In pictures: New York's Grand Central Terminal celebrates its 100th birthday The week in travel: January 28-February 1, 2013 Valentine's Day holiday deals Cruise holidays: a guide for first-timers - what the brochures don't tell you Cruise holidays: a guide for first-timers - specialist options Cruise holidays: a guide for first-timers - sailing from Britain Cruise holidays: a guide for first-timers - dispelling the myths WWF: Australia in grave danger over Great Barrier Reef Travel views: January 26 to February 1, 2013 The Grand Canyon on Google Grand Canyon gets Google treatment New York's Grand Central Terminal turns 100 Just Back: memories of Auschwitz Visiting the Thiépval Memorial  Back to top   Food and Drink Is horse meat still on sale in Britain  Ministers consider 'calorie labels' for wine, beer and spirits Word of mouth: the real queen of puddings Rose Prince's winter recipes with a piquant punch Broccoli with gnocchi, sage butter and toasted hazelnuts Sweet cooked red chicory, toasted pumpernickel and speck recipe Pickled pears with fromage blanc recipe Braised wild rabbit with new-season lemons and parsley Garlic and thyme pork with celeriac and pickled roots recipe Budget recipe: pork cheek casserole with mushrooms and red wine Soul food: Karam Sethi on his grandparents' rotis The magic of couscous  Back to top   Gardening Volunteers sought to restore historic garden at Clifton Hill House How Seedy Sundays grew into a phenomenon The best snowdrop varieties for your garden RHS diary: what to do in the garden in February  Back to top   Education More privately-educated pupils win university offers Britain hands over ￡100m to Polish students Yes, Nick Clegg, only money will get your boy a good school. How sad is that  Head warns over 'bunfight' for places at top schools Britain's fastest-growing universities Record ￡2.4bn sitting unspent in school bank accounts International education league tables 'are misleading' A Very Private Tutor: Getting to know the parents  Back to top   Technology BT drops broadband caps and cuts prices Ten tech terms we could do without Plusnet tops broadband survey 'Anonymous' hacker who cost Paypal millions escapes jail Grand Theft Auto V delayed until autumn Google submits proposals to EU anti-trust investigators Sony PS4 rumours sparked by event invites Best British crowdfunding sites  Back to top   Culture New BBC drama to show the scandalous stories of the playboy Princes 'I was Gina Lollobrigida at her Barcelona wedding': Spanish pensioner speaks Jake Bugg: Bringing back a blue-collar perspective Otello, Opera North, Leeds, review Hyde Park on Hudson, review Flight, review Jennifer Lynch on Sunset Boulevard La Clemenza di Tito, Opera North, Grand Theatre, Leeds, review Anjin: The Shogun and the English Samurai, Sadler's Wells, review Short raises his game How Ebony Day and the MTV Brand New award are changing the music industry Bolshoi drops major ballet premiere after acid attack Culture pictures of the week: 1 February 2013 Tristan Sharps interview for In the Beginning was the End Deadline by Barbara Nadel: review Children's songwriting competition seeks Britain's brightest young stars Films in brief: Bullhead, A Place in the Sun, Chained Modern art pioneer dismisses contemporary works as 'humbug' The opera novice: The Mikado by Gilbert & Sullivan Musorgsky: Pictures from an Exhibition. Prokofiev: Sarcasms; Visions fugitives, classical album review Bollywood star Salman Khan to be tried for hit-and-run Japanese pop star shaves head in penance for sex scandal Kris Kristofferson leads praise for Cowboy Jack Clement Jonathan Ross and Russell Brand: we regret Sachsgate Skyfall boosts British cinema as audiences reach decade high Old Times, Harold Pinter Theatre, review Laura Linney interview for Hyde Park on Hudson Dan Stevens takes the helm at literary magazine House of Cards, Netflix, review Children under 5 should not watch TV alone, Jackanory creator argues Why we’re all waking up to Chris Evans  Back to top   Comment Lib Dems abandon founding father of voting reform It's a thumbs down to storing nuclear waste This Equality obsession is mad, bad and dangerous Is there a Tory conspiracy to bring down the PM  A moment of renewal, for the Church and country Stars in our eyes The perfumers’ skills are not to be sniffed at Chris Brown did a terrible thing, but he served his time Cameron should introduce marriage tax relief now How I made a year’s net salary in a month Gay marriage folly shows Coalition's incompetence What really holds you back in the Labour Party Time to ditch Downton as foreign policy instrument I don’t envy the Rebecca Adlingtons of sport Will the PM’s promise on defence cuts survive the general election  Dogs of war: Army officers’ canine companions  Back to top   Sponsored Decorate your conservatory in a style that sets the mood What are calories and how do you burn them  Safer Internet Day 2013 connects with respect Lower your sugar intake with Splenda's Small Steps  Back to top   Promotions Claim a free copy* of Sky at Night Magazine In pictures: awe-inspiring astronomy Save 10 per cent on a Michel Roux Jr cookery course Berry soufflé recipe  Back to top   Women Grandes dames at the door of the Dream Factory This Equality obsession is mad, bad and very dangerous David Beckham doesn't endure the mockery his wife Victoria gets 'I think I've got an STI. Help!' Children's notebook: Eczema and itchy skin Childcare tax breaks: Cameron should introduce marriage tax relief now Women's Bits: Kate Middleton's nose, Oprah Winfrey and being Harry Styles Parenting dilemma: should you make your child eat what’s on their plate  Forceps births at record high because of older and obese mothers Joy Whitby: a life spent telling children's stories on TV Roaring forties: naughty or nice   Back to top    Back to top      HOME     News     World News     Obituaries      Travel      Health     Jobs      Sport     Football     Cricket     Fantasy Football      Culture      Motoring     Dating      Finance     Personal Finance     Economics     Markets      Fashion      Property     Puzzles      Comment     My Telegraph     Letters     Columnists      Technology      Gardening     Telegraph Shop      Contact us     Privacy and Cookies     Guidelines     Advertising      Tickets      Announcements     Reader Prints      Follow Us     Apps     Epaper     Expat      Promotions     Subscriber     Syndication    Copyright of Telegraph Media Group Limited 2015  Terms and Conditions  Today's News  Archive  Style Book  Weather Forecast "
            ,};
        for (int i = 0; i < contents.length; i++) {
            System.out.println("来源：" + getSource(contents[i])+"，作者："+getAuthor(contents[i])+"，编辑：" +getEditor(contents[i])+ "，时间："+getDate(contents[i]) );
            System.out.println("来源：" + getSourceE(contents[i])+"，作者："+getAuthorE(contents[i])+"，编辑：" +getEditorE(contents[i])+ "，时间："+getDate(contents[i]) );
        }
    }

}
