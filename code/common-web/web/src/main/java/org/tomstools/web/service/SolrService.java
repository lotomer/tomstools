/**
 * 
 */
package org.tomstools.web.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.tomstools.common.parse.TemplateParser;
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
	public static final long STAT_TIME_DEFAULT = 7 * 24 * 3600 * 1000; // 默认统计时长。7天
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
		Map<String, Integer> sites = new HashMap<String, Integer>();
		sites.put("-1", -1);
		for (Map<String, Object> siteInfo : siteInfos) {
			sites.put(String.valueOf(siteInfo.get("SITE_HOST")),
					Integer.parseInt(String.valueOf(siteInfo.get("SITE_ID"))));
		}
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
			Date endTime = new Date();
			if (null != lastStatTime) {
				beginTime = new Date(lastStatTime.getTime());
			} else {
				beginTime = new Date(endTime.getTime() - STAT_TIME_DEFAULT);
			}
			Map<Integer, Long> siteZM = new HashMap<Integer, Long>();
			Map<Integer, Long> siteFM = new HashMap<Integer, Long>();
			Map<Integer, Long> siteZM_E = new HashMap<Integer, Long>();
			Map<Integer, Long> siteFM_E = new HashMap<Integer, Long>();
			// 获取正面信息数
			if (!StringUtils.isEmpty(templateZM)) {
				getSiteCount(typeId, "ZM", solrTool, sites, templateZM, beginTime, endTime, siteZM);

			}
			// 获取负面信息数
			if (!StringUtils.isEmpty(templateFM)) {
				getSiteCount(typeId, "FM", solrTool, sites, templateFM, beginTime, endTime, siteFM);
			}

			// 获取正面信息数
			if (!StringUtils.isEmpty(templateZM_E)) {
				getSiteCount(typeId, "ZM_E", solrTool, sites, templateZM_E, beginTime, endTime, siteZM_E);
			}
			// 获取负面信息数
			if (!StringUtils.isEmpty(templateFM_E)) {
				getSiteCount(typeId, "FM_E", solrTool, sites, templateFM_E, beginTime, endTime, siteFM_E);
			}

			// 遍历所有站点，并保存结果
			for (Entry<String, Integer> entry : sites.entrySet()) {
				Long sizeZM = siteZM.get(entry.getValue());
				Long sizeFM = siteFM.get(entry.getValue());
				Long sizeZM_E = siteZM_E.get(entry.getValue());
				Long sizeFM_E = siteFM_E.get(entry.getValue());
				Integer siteId = entry.getValue();
				if (null != siteId) {
					sizeZM = null != sizeZM ? sizeZM : 0;
					sizeFM = null != sizeFM ? sizeFM : 0;
					sizeZM_E = null != sizeZM_E ? sizeZM_E : 0;
					sizeFM_E = null != sizeFM_E ? sizeFM_E : 0;
					if (0 != sizeZM || 0 != sizeFM || 0 != sizeZM_E || 0 != sizeFM_E) {
						// 将数据入库
						siteMapper.saveStat(typeId, siteId, sizeZM, sizeFM, sizeZM_E, sizeFM_E, endTime);
						++count;
					}
				}
			}
		}

		return count;
	}

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
		return siteMapper.selectStats(startTime, endTime, typeId);
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

	private void getSiteCount(Integer typeId, String templateType, SolrTools solrTool, Map<String, Integer> sites,
			String template, Date beginTime, Date endTime, Map<Integer, Long> siteCount) throws Exception {
		if (StringUtils.isEmpty(template)) {
			return;
		}
		int start = 0;
		int size = 1000;
		while (true) {
			QueryResponse resp = solrTool.query("text:" + template, "host,title,url,tstamp", beginTime, endTime, null,
					start, size);
			SolrDocumentList datas = resp.getResults();
			if (null != datas) {// 没有取完，还要继续
				for (SolrDocument doc : datas) {
					String host = String.valueOf(doc.getFieldValue("host"));
					// 从host中解析站点
					int siteId = getSiteIdByHost(sites, host);
					// 记录次数
					Long count = siteCount.get(siteId);
					if (null != count) {
						siteCount.put(siteId, count + 1);
					} else {
						siteCount.put(siteId, 1l);
					}

					// 保存明细到数据库
					// 判断对应的url是否已经存在，如果不存在则添加
					String flag = siteMapper.checkUrl(String.valueOf(doc.getFieldValue("url")));
					if (StringUtils.isEmpty(flag)) {
						siteMapper.saveDetail(typeId, templateType, siteId, String.valueOf(doc.getFieldValue("title")),
								String.valueOf(doc.getFieldValue("url")), (Date) doc.getFieldValue("tstamp"));
					}
				}
				if (size == datas.size()) {
					start += size;
					continue;
				}
			}
			break;
		}
	}

	private static int getSiteIdByHost(Map<String, Integer> sites, String host) {
		while (true) {
			if (sites.containsKey(host)) {
				Integer id = sites.get(host);
				if (null != id) {
					return id;
				} else {
					return -1;
				}
			}
			int index = host.indexOf(".");
			if (index < 0 || host.length() == 1) {
				break;
			}
			host = host.substring(index + 1);
		}
		return -1;
	}

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
		return siteMapper.selectStatsCount(startTime, endTime, typeId);
	}

	/** 获取语言列表 */
	public List<Map<String, Object>> selectLanguage() {
		return solrMapper.selectLanguage();
	}

	/** 获取国家 */
	public List<Map<String, Object>> selectCountry() {
		return solrMapper.selectCountry();
	}

	public List<Map<String, Object>> siteTop(Date startTime, Date endTime, int topNum) {
		return siteMapper.selectSiteTop(startTime, endTime, topNum);
	}

	public List<Map<String, Object>> statMediaCount(Date startTime, Date endTime) {
		return siteMapper.selectMediaCount(startTime, endTime);
	}

	public List<Map<String, Object>> statMedia(Date startTime, Date endTime) {
		return siteMapper.selectMedia(startTime, endTime);
	}

	public List<Map<String, Object>> statWordsCountAll(Date startTime, Date endTime) {
		return siteMapper.selectStatsCountAll(startTime, endTime);
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
		return siteMapper.statWordsCountQuery(start, end, langId, countryId, siteTypeId, siteId);
	}

	public List<Map<String, Object>> statWordsQueryDetail(Date startTime, Date endTime, Integer langId,
			Integer countryId, Integer siteTypeId, Integer siteId, Integer wordsId,Integer start, Integer rows) {
		start = null != start ? start : 0;
		rows = null != rows ? rows : 10;
		return siteMapper.statWordsQueryDetail(startTime, endTime, langId, countryId, siteTypeId, siteId, wordsId,start, rows);
	}
	public int countWordsQueryDetail(Date startTime, Date endTime, Integer langId, Integer countryId, Integer siteTypeId,
			Integer siteId, Integer wordsId) {
		return siteMapper.countWordsQueryDetail(startTime, endTime, langId, countryId, siteTypeId, siteId, wordsId);
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

		List<Map<String, Object>> statCounts = siteMapper.selectStatsCount(startTime.getTime(), endTime.getTime(),
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
							++count;
							alertLog.put("NOTIFIER", user.getNickName());
							String title = TemplateParser.parse(alertLog, tmplTitle);
							String content = TemplateParser.parse(alertLog, tmplContent);
							if ("1".equals(alertType)) {// 邮件
								notifyByEmail(user.getEmail(), title, content);
							} else if ("2".equals(alertType)) {// 短信
								notifyBySms(user.getPhoneNumber(), title, content);
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

	private void notifyBySms(String phoneNumber, String title, String content) {
		if (!StringUtils.isEmpty(phoneNumber) && !StringUtils.isEmpty(content)) {
			LOG.info("发送短信:" + content);
		}
	}

	private void notifyByEmail(String email, String title, String content) {
		if (!StringUtils.isEmpty(email) && !StringUtils.isEmpty(content)) {
			mailService.sendMail(title, content, email);
		}
	}
}
