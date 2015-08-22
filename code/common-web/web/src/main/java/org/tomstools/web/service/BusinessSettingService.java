/**
 * 
 */
package org.tomstools.web.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tomstools.web.persistence.BusinessSettingMapper;

/**
 * 业务设置服务
 * @author Administrator
 *
 */
@Service("businessSettingService")
public class BusinessSettingService {
	@Autowired
	private BusinessSettingMapper businessSettingMapper;

	public List<Map<String, Object>> selectWordsList() {
		return businessSettingMapper.selectWordsList();
	}

	public void deleteWords(Integer id) {
		businessSettingMapper.deleteWords(id);
	}

	public void addWords(String typeName, String templateZM, String templateFM, String templateZM_E,
			String templateFM_E) {
		businessSettingMapper.addWords(typeName,templateZM,templateFM,templateZM_E,templateFM_E);
	}

	public void saveWords(Integer id, String typeName, String templateZM, String templateFM, String templateZM_E,
			String templateFM_E) {
		businessSettingMapper.saveWords(id,typeName,templateZM,templateFM,templateZM_E,templateFM_E);
	}
	public List<Map<String, Object>> selectCrawlerStatus() {
		
		return businessSettingMapper.selectCrawlerStatus();
	}

	public void saveCrawler(String id, String crawlName, String crawlFrequency) {
		businessSettingMapper.saveCrawler(id, crawlName, crawlFrequency);
	}

	public List<Map<String, Object>> selectSiteList() {
		return businessSettingMapper.selectSiteList();
	}

	public void addSite(String siteName, String siteHost, Integer siteTypeId, Integer langId, Integer countryCode) {
		businessSettingMapper.addSite(siteName, siteHost,siteTypeId, langId, countryCode);
	}

	public void saveSite(Integer id, String siteName, String siteHost, Integer siteTypeId, Integer langId, Integer countryCode) {
		businessSettingMapper.saveSite(id, siteName, siteHost,siteTypeId, langId, countryCode);
	}

	public void deleteSite(Integer id) {
		businessSettingMapper.deleteSite(id);
	}

	public List<Map<String, Object>> selectSiteDetailList(Integer siteId) {
		return businessSettingMapper.selectSiteDetailList(siteId);
	}

	public void deleteSiteDetail(Integer id) {
		businessSettingMapper.deleteSiteDetail(id);
	}

	public void saveSiteDetail(Integer id, String url) {
		businessSettingMapper.saveSiteDetail(id,url);
	}

	public void addSiteDetail(Integer siteId, String url) {
		businessSettingMapper.addSiteDetail(siteId,url);
	}

	public List<Map<String, Object>> selectWordList() {
		return businessSettingMapper.selectWordList();
	}

	public void deleteWord(String word) {
		businessSettingMapper.deleteWord(word);
	}

	public void addWord(Integer typeId, Integer langId, String word) {
		businessSettingMapper.addWord(typeId,langId,word);
	}
	
}