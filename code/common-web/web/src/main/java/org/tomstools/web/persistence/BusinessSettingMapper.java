package org.tomstools.web.persistence;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * 业务设置
 * 
 * @author Administrator
 *
 */
public interface BusinessSettingMapper {

	//////////////////////////////////////////////////
	// 爬虫
	//////////////////////////////////////////////////
	public List<Map<String, Object>> selectCrawlerStatus();

	public void saveCrawler(@Param("CRAWL_CODE") String id, @Param("CRAWL_NAME") String crawlName,
			@Param("CRAWL_FREQUENCY") String crawlFrequency);

	//////////////////////////////////////////////////
	// 词条
	//////////////////////////////////////////////////
	public List<Map<String, Object>> selectWordsList();
	public Integer selecTypeIdByName(@Param("TYPE_NAME") String typeName);

	public void deleteWords(@Param("TYPE_ID") Integer id);

	public void addWords(@Param("TYPE_NAME") String typeName, @Param("TEMPLATE_ZM") String templateZM,
			@Param("TEMPLATE_FM") String templateFM, @Param("TEMPLATE_ZM_E") String templateZM_E,
			@Param("TEMPLATE_FM_E") String templateFM_E);

	public void saveWords(@Param("TYPE_ID") Integer id, @Param("TYPE_NAME") String typeName,
			@Param("TEMPLATE_ZM") String templateZM, @Param("TEMPLATE_FM") String templateFM,
			@Param("TEMPLATE_ZM_E") String templateZM_E, @Param("TEMPLATE_FM_E") String templateFM_E);

	//////////////////////////////////////////////////
	// 站点
	//////////////////////////////////////////////////
	public List<Map<String, Object>> selectSiteList();
	public Integer selectSiteIdByName(@Param("SITE_NAME") String siteName);

	public void deleteSite(@Param("SITE_ID") Integer id);

	public void saveSite(@Param("SITE_ID") Integer id, @Param("SITE_NAME") String siteName,
			@Param("SITE_HOST") String siteHost, @Param("SITE_TYPE_ID") Integer siteTypeId,
			@Param("LANG_ID") Integer langId, @Param("COUNTRY_CODE") Integer countryCode);

	public void addSite(@Param("SITE_NAME") String siteName, @Param("SITE_HOST") String siteHost,
			@Param("SITE_TYPE_ID") Integer siteTypeId, @Param("LANG_ID") Integer langId,
			@Param("COUNTRY_CODE") Integer countryCode);

	//////////////////////////////////////////////////
	// 站点URL列表
	//////////////////////////////////////////////////
	public List<Map<String, Object>> selectSiteDetailList(@Param("SITE_ID") Integer siteId);
	public Integer selectIdBySiteIdAndUrl(@Param("SITE_ID") Integer siteId, @Param("URL") String url);

	public void deleteSiteDetail(@Param("ID") Integer id);

	public void saveSiteDetail(@Param("ID") Integer id, @Param("URL") String url);

	public void addSiteDetail(@Param("SITE_ID") Integer siteId, @Param("URL") String url);

	//////////////////////////////////////////////////
	// 词汇管理
	//////////////////////////////////////////////////
	public int countWord();
	public List<Map<String, Object>> selectWordList(@Param("START") Integer start, @Param("ROWS") Integer rows);
	public Integer selectWordIdByWord(@Param("WORD") String word);

	public void deleteWord(@Param("WORD") String word);

	public void addWord(@Param("TENDENCY") Integer typeId, @Param("LANG_ID") Integer langId,
			@Param("WORD") String word);

	//////////////////////////////////////////////////
	// 预警管理
	//////////////////////////////////////////////////
	public List<Map<String, Object>> selectAlertList();
	public Integer selectAlertIdByName(@Param("ALERT_NAME") String alertName);

	public void deleteAlert(@Param("ALERT_ID") Integer id);

	public void saveAlert(@Param("ALERT_ID") Integer id, @Param("ALERT_NAME") String alertName,
			@Param("ALERT_TYPE") String alertType, @Param("ALERT_VALUE") String alertValue,
			@Param("NOTIFIERS") String notifiers, @Param("METRICS") String metrics);

	public void addAlert(@Param("ALERT_NAME") String alertName,
			@Param("ALERT_TYPE") String alertType, @Param("ALERT_VALUE") String alertValue,
			@Param("NOTIFIERS") String notifiers, @Param("METRICS") String metrics);

	public List<Map<String, Object>> selectCrawlerList();

	public String selectCrawlerError(@Param("STATUS_ID") long id);

}
