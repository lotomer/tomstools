/**
 * 
 */
package org.tomstools.web.action;

import java.util.Collections;
import java.util.HashMap;
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
			return "NEED_LOGIN:" + error;
		}
		try {
			businessSettingService.deleteWords(id);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return "删除失败";// e.getMessage();
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
			return "NEED_LOGIN:" + error;
		}
		try {
			businessSettingService.saveWords(id, typeName, templateZM, templateFM, templateZM_E, templateFM_E);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			// return e.getMessage();
			return "保存失败";
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
			return "NEED_LOGIN:" + error;
		}

		Integer id = businessSettingService.selecTypeIdByName(typeName);
		if (null != id) {
			return "词条已经存在！";
		}
		try {
			businessSettingService.addWords(typeName, templateZM, templateFM, templateZM_E, templateFM_E);
			return "";
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return "新增失败";
		}
	}

	@RequestMapping("/crawl/error.do")
	public @ResponseBody String selectCrawlerError(@RequestParam("key") String key,
			@RequestParam(value = "id", required = true) Long id, HttpServletRequest req, HttpServletResponse resp)
		            throws Exception {
		String result = businessSettingService.selectCrawlerError(id);
		return result;
	}
	@RequestMapping("/crawl/select.do")
	public @ResponseBody String selectCrawlerList() {
		List<Map<String, Object>> result = businessSettingService.selectCrawlerList();
		return JSON.toJSONString(result);
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
			return "NEED_LOGIN:" + error;
		}
		try {
			businessSettingService.saveCrawler(id, crawlName, crawlFrequency);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return "新增失败";// e.getMessage();
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
			return "NEED_LOGIN:" + error;
		}
		try {
			businessSettingService.deleteSite(id);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return "删除失败";// e.getMessage();
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
			return "NEED_LOGIN:" + error;
		}
		try {
			businessSettingService.saveSite(id, siteName, siteHost, siteTypeId, langId, countryCode);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return "保存失败";// e.getMessage();
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
			return "NEED_LOGIN:" + error;
		}
		Integer id = businessSettingService.selectSiteIdByName(siteName);
		if (null != id) {
			return "网站已经存在！";
		}
		try {
			businessSettingService.addSite(siteName, siteHost, siteTypeId, langId, countryCode);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return "新增失败";// e.getMessage();
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
			return "NEED_LOGIN:" + error;
		}
		List<Map<String, Object>> result = businessSettingService.selectSiteDetailList(siteId);
		return JSON.toJSONString(result);
	}

	@RequestMapping("/siteDetail/delete.do")
	public @ResponseBody String deleteSiteDetail(@RequestParam("key") String key,
			@RequestParam(value = "id", required = true) Integer id, HttpServletRequest req, HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return "NEED_LOGIN:" + error;
		}
		try {
			businessSettingService.deleteSiteDetail(id);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return "删除";// e.getMessage();
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
			return "NEED_LOGIN:" + error;
		}
		try {
			businessSettingService.saveSiteDetail(id, url);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return "保存失败";// e.getMessage();
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
			return "NEED_LOGIN:" + error;
		}
		Integer id = businessSettingService.selectIdBySiteIdAndUrl(siteId, url);
		if (null != id) {
			return "网站对应的URL已经存在！";
		}
		try {
			businessSettingService.addSiteDetail(siteId, url);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return "新增失败";// e.getMessage();
		}
		return "";
	}

	// ------------------------------------------------------------
	// -- 词汇管理 --
	// ------------------------------------------------------------
	@RequestMapping("/word/select.do")
	public @ResponseBody String selectWordList(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "rows", required = false) Integer rows, HttpServletRequest req,
            HttpServletResponse resp) {
		rows = rows == null? 10 : rows;
        int startNum = page == null ? 0 : (page - 1) * rows;
        int total = businessSettingService.countWord();
        List<Map<String, Object>> details = businessSettingService.selectWordList(startNum, rows);
        if (null == details){
            details = Collections.emptyList();
        }
        Map<String,Object> result = new HashMap<String, Object>();
        result.put("total", total);
        result.put("rows", details);
        return JSON.toJSONString(result);
	}

	@RequestMapping("/word/delete.do")
	public @ResponseBody String deleteWord(@RequestParam("key") String key,
			@RequestParam(value = "id", required = true) String word, HttpServletRequest req,
			HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return "NEED_LOGIN:" + error;
		}
		try {
			businessSettingService.deleteWord(word);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return "删除失败";// e.getMessage();
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
		// String error = userService.check(key);
		// if (!"".equals(error)) {
		// return "NEED_LOGIN:" + error;
		// }
		// try {
		// businessSettingService.saveWord(id, siteName, siteHost, siteTypeId,
		// langId, countryCode);
		// } catch (Exception e) {
		// LOG.error(e.getMessage(), e);
		// return e.getMessage();
		// }
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
			return "NEED_LOGIN:" + error;
		}
		Integer id = businessSettingService.selectWordIdByWord(word);
		if (null != id) {
			return "词汇已经存在！";
		}
		try {
			businessSettingService.addWord(typeId, langId, word);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return "新增失败";// e.getMessage();
		}
		return "";
	}

	// ------------------------------------------------------------
	// -- 预警管理
	// ------------------------------------------------------------

	@RequestMapping("/alert/select.do")
	public @ResponseBody String selectAlertList() {
		List<Map<String, Object>> result = businessSettingService.selectAlertList();
		return JSON.toJSONString(result);
	}

	@RequestMapping("/alert/delete.do")
	public @ResponseBody String deleteAlert(@RequestParam("key") String key,
			@RequestParam(value = "id", required = true) Integer id, HttpServletRequest req, HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return "NEED_LOGIN:" + error;
		}
		try {
			businessSettingService.deleteAlert(id);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return "删除失败";// e.getMessage();
		}
		return "";
	}

	@RequestMapping("/alert/save.do")
	public @ResponseBody String saveAlert(@RequestParam("key") String key,
			@RequestParam(value = "id", required = true) Integer id,
			@RequestParam(value = "ALERT_NAME", required = true) String alertName,
			@RequestParam(value = "ALERT_TYPE", required = true) String alertType,
			@RequestParam(value = "ALERT_VALUE", required = true) String alertValue,
			@RequestParam(value = "NOTIFIERS", required = true) String notifiers,
			@RequestParam(value = "METRICS", required = true) String metrics, HttpServletRequest req,
			HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return "NEED_LOGIN:" + error;
		}
		try {
			businessSettingService.saveAlert(id, alertName, alertType, alertValue, notifiers, metrics);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return "保存失败";// e.getMessage();
		}
		return "";
	}

	@RequestMapping("/alert/add.do")
	public @ResponseBody String addAlert(@RequestParam("key") String key,
			@RequestParam(value = "ALERT_NAME", required = true) String alertName,
			@RequestParam(value = "ALERT_TYPE", required = true) String alertType,
			@RequestParam(value = "ALERT_VALUE", required = true) String alertValue,
			@RequestParam(value = "NOTIFIERS", required = true) String notifiers,
			@RequestParam(value = "METRICS", required = true) String metrics, HttpServletRequest req,
			HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return "NEED_LOGIN:" + error;
		}
		Integer id = businessSettingService.selectAlertIdByName(alertName);
		if (null != id) {
			return "预警名称对应的预警信息已经存在！";
		}
		try {
			businessSettingService.addAlert(alertName, alertType, alertValue, notifiers, metrics);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return "新增";// e.getMessage();
		}
		return "";
	}
}
