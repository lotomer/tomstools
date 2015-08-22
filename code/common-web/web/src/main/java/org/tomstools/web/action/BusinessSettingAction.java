/**
 * 
 */
package org.tomstools.web.action;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tomstools.web.service.BusinessSettingService;
import org.tomstools.web.service.UserService;

import com.alibaba.fastjson.JSON;

/**
 * 业务配置
 * 
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/setting")
public class BusinessSettingAction {
	private static final Log LOG = LogFactory.getLog(BusinessSettingAction.class);
	@Autowired
	private BusinessSettingService businessSettingService;
	@Autowired
	private UserService userService;
	// ------------------------------------------------------------
	// -- 智能词条管理 
	// ------------------------------------------------------------

	@RequestMapping("/words/select.do")
	public @ResponseBody String selectWordsList() {
		List<Map<String, Object>> result = businessSettingService.selectWordsList();
		return JSON.toJSONString(result);
	}

	@RequestMapping("/words/delete.do")
	public @ResponseBody String deleteWords(@RequestParam("key") String key,
			@RequestParam(value = "id", required = true) Integer id, HttpServletRequest req, HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return error;
		}
		try {
			businessSettingService.deleteWords(id);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return e.getMessage();
		}
		return "";
	}

	@RequestMapping("/words/save.do")
	public @ResponseBody String saveWords(@RequestParam("key") String key,
			@RequestParam(value = "id", required = true) Integer id,
			@RequestParam(value = "TYPE_NAME", required = true) String typeName,
			@RequestParam(value = "TEMPLATE_ZM", required = false) String templateZM,
			@RequestParam(value = "TEMPLATE_FM", required = false) String templateFM,
			@RequestParam(value = "TEMPLATE_ZM_E", required = false) String templateZM_E,
			@RequestParam(value = "TEMPLATE_FM_E", required = false) String templateFM_E, HttpServletRequest req,
			HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return error;
		}
		try {
			businessSettingService.saveWords(id, typeName, templateZM, templateFM, templateZM_E, templateFM_E);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return e.getMessage();
		}
		return "";
	}

	@RequestMapping("/words/add.do")
	public @ResponseBody String addWords(@RequestParam("key") String key,
			@RequestParam(value = "TYPE_NAME", required = true) String typeName,
			@RequestParam(value = "TEMPLATE_ZM", required = false) String templateZM,
			@RequestParam(value = "TEMPLATE_FM", required = false) String templateFM,
			@RequestParam(value = "TEMPLATE_ZM_E", required = false) String templateZM_E,
			@RequestParam(value = "TEMPLATE_FM_E", required = false) String templateFM_E, HttpServletRequest req,
			HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return error;
		}
		try {
			businessSettingService.addWords(typeName, templateZM, templateFM, templateZM_E, templateFM_E);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return e.getMessage();
		}
		return "";
	}

	@RequestMapping("/crawl/status.do")
	public @ResponseBody String selectCrawlerStatus() {
		List<Map<String, Object>> result = businessSettingService.selectCrawlerStatus();
		return JSON.toJSONString(result);
	}

	@RequestMapping("/crawl/save.do")
	public @ResponseBody String saveCrawler(@RequestParam("key") String key,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "CRAWL_NAME", required = true) String crawlName,
			@RequestParam(value = "CRAWL_FREQUENCY", required = true) String crawlFrequency, HttpServletRequest req,
			HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return error;
		}
		try {
			businessSettingService.saveCrawler(id, crawlName, crawlFrequency);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return e.getMessage();
		}
		return "";
	}
	// ------------------------------------------------------------
	// -- 站点管理 
	// ------------------------------------------------------------

	@RequestMapping("/site/select.do")
	public @ResponseBody String selectSiteList() {
		List<Map<String, Object>> result = businessSettingService.selectSiteList();
		return JSON.toJSONString(result);
	}

	@RequestMapping("/site/delete.do")
	public @ResponseBody String deleteSite(@RequestParam("key") String key,
			@RequestParam(value = "id", required = true) Integer id, HttpServletRequest req, HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return error;
		}
		try {
			businessSettingService.deleteSite(id);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return e.getMessage();
		}
		return "";
	}

	@RequestMapping("/site/save.do")
	public @ResponseBody String saveSite(@RequestParam("key") String key,
			@RequestParam(value = "id", required = true) Integer id,
			@RequestParam(value = "SITE_TYPE_ID", required = true) Integer siteTypeId,
			@RequestParam(value = "LANG_ID", required = true) Integer langId,
			@RequestParam(value = "COUNTRY_CODE", required = true) Integer countryCode,
			@RequestParam(value = "SITE_NAME", required = true) String siteName,
			@RequestParam(value = "SITE_HOST", required = true) String siteHost, HttpServletRequest req,
			HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return error;
		}
		try {
			businessSettingService.saveSite(id, siteName, siteHost, siteTypeId, langId, countryCode);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return e.getMessage();
		}
		return "";
	}

	@RequestMapping("/site/add.do")
	public @ResponseBody String addSite(@RequestParam("key") String key,
			@RequestParam(value = "SITE_TYPE_ID", required = true) Integer siteTypeId,
			@RequestParam(value = "LANG_ID", required = true) Integer langId,
			@RequestParam(value = "COUNTRY_CODE", required = true) Integer countryCode,
			@RequestParam(value = "SITE_NAME", required = true) String siteName,
			@RequestParam(value = "SITE_HOST", required = true) String siteHost, HttpServletRequest req,
			HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return error;
		}
		try {
			businessSettingService.addSite(siteName, siteHost, siteTypeId, langId, countryCode);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return e.getMessage();
		}
		return "";
	}

	// ------------------------------------------------------------
	// -- 爬取URL列表管理 --
	// ------------------------------------------------------------
	@RequestMapping("/siteDetail/select.do")
	public @ResponseBody String selectSiteListDetail(@RequestParam("key") String key,
			@RequestParam(value = "SITE_ID", required = false) Integer siteId) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return error;
		}
		List<Map<String, Object>> result = businessSettingService.selectSiteDetailList(siteId);
		return JSON.toJSONString(result);
	}

	@RequestMapping("/siteDetail/delete.do")
	public @ResponseBody String deleteSiteDetail(@RequestParam("key") String key,
			@RequestParam(value = "id", required = true) Integer id, HttpServletRequest req, HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return error;
		}
		try {
			businessSettingService.deleteSiteDetail(id);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return e.getMessage();
		}
		return "";
	}

	@RequestMapping("/siteDetail/save.do")
	public @ResponseBody String saveSiteDetail(@RequestParam("key") String key,
			@RequestParam(value = "id", required = true) Integer id,
			@RequestParam(value = "URL", required = true) String url, HttpServletRequest req,
			HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return error;
		}
		try {
			businessSettingService.saveSiteDetail(id, url);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return e.getMessage();
		}
		return "";
	}

	@RequestMapping("/siteDetail/add.do")
	public @ResponseBody String addSiteDetail(@RequestParam("key") String key,
			@RequestParam(value = "SITE_ID", required = true) Integer siteId,
			@RequestParam(value = "URL", required = true) String url, HttpServletRequest req,
			HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return error;
		}
		try {
			businessSettingService.addSiteDetail(siteId, url);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return e.getMessage();
		}
		return "";
	}

	// ------------------------------------------------------------
	// -- 词汇管理 --
	// ------------------------------------------------------------
	@RequestMapping("/word/select.do")
	public @ResponseBody String selectWordList() {
		List<Map<String, Object>> result = businessSettingService.selectWordList();
		return JSON.toJSONString(result);
	}
	
	@RequestMapping("/word/delete.do")
	public @ResponseBody String deleteWord(@RequestParam("key") String key,
			@RequestParam(value = "id", required = true) String word, HttpServletRequest req, HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return error;
		}
		try {
			businessSettingService.deleteWord(word);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return e.getMessage();
		}
		return "";
	}

	@RequestMapping("/word/save.do")
	public @ResponseBody String saveWord(@RequestParam("key") String key,
			@RequestParam(value = "id", required = true) Integer id,
			@RequestParam(value = "TYPE_ID", required = true) Integer TypeId,
			@RequestParam(value = "LANG_ID", required = true) Integer langId,
			@RequestParam(value = "WORD", required = true) String word, HttpServletRequest req,
			HttpServletResponse resp) {
//		String error = userService.check(key);
//		if (!"".equals(error)) {
//			return error;
//		}
//		try {
//			businessSettingService.saveWord(id, siteName, siteHost, siteTypeId, langId, countryCode);
//		} catch (Exception e) {
//			LOG.error(e.getMessage(), e);
//			return e.getMessage();
//		}
		return "";
	}

	@RequestMapping("/word/add.do")
	public @ResponseBody String addWord(@RequestParam("key") String key,
			@RequestParam(value = "TYPE_ID", required = true) Integer typeId,
			@RequestParam(value = "LANG_ID", required = true) Integer langId,
			@RequestParam(value = "WORD", required = true) String word, HttpServletRequest req,
			HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return error;
		}
		try {
			businessSettingService.addWord(typeId,  langId, word);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return e.getMessage();
		}
		return "";
	}
}