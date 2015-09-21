package org.tomstools.web.action;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tomstools.web.service.BusinessMgrService;
import org.tomstools.web.service.UserService;

import com.alibaba.fastjson.JSON;

@Controller
@RequestMapping("/business")
public class BusinessMgrAction {
	private static Log LOG = LogFactory.getLog(BusinessMgrAction.class);
	@Autowired
	private UserService userService;
	@Autowired
	private BusinessMgrService businessSettingService;

	// ------------------------------------------------------------
	// -- 模板管理
	// ------------------------------------------------------------
	@RequestMapping("/template/select.do")
	public @ResponseBody String selectTemplateList() {
		List<Map<String, Object>> result = businessSettingService
				.selectTemplateList();
		return JSON.toJSONString(result);
	}

	@RequestMapping("/template/delete.do")
	public @ResponseBody String deleteTemplate(@RequestParam("key") String key,
			@RequestParam(value = "id", required = true) Integer id,
			HttpServletRequest req, HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return "NEED_LOGIN:" + error;
		}
		try {
			businessSettingService.deleteTemplate(id);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return "删除失败";// e.getMessage();
		}
		return "";
	}

	@RequestMapping("/template/save.do")
	public @ResponseBody String saveTemplate(
			@RequestParam("key") String key,
			@RequestParam(value = "id", required = true) Integer id,
			@RequestParam(value = "TEMPLATE_NAME", required = true) String TEMPLATE_NAME,
			@RequestParam(value = "TEMPLATE_CONTENT", required = false) String TEMPLATE_CONTENT,
			@RequestParam(value = "TEMPLATE_SCRIPT", required = false) String TEMPLATE_SCRIPT,
			HttpServletRequest req, HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return "NEED_LOGIN:" + error;
		}
		try {
			businessSettingService.saveTemplate(id, TEMPLATE_NAME,
					TEMPLATE_CONTENT, TEMPLATE_SCRIPT);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			// return e.getMessage();
			return "保存失败";
		}
		return "";
	}

	@RequestMapping("/template/add.do")
	public @ResponseBody String addTemplate(
			@RequestParam("key") String key,
			@RequestParam(value = "TEMPLATE_NAME", required = true) String TEMPLATE_NAME,
			@RequestParam(value = "TEMPLATE_CONTENT", required = false) String TEMPLATE_CONTENT,
			@RequestParam(value = "TEMPLATE_SCRIPT", required = false) String TEMPLATE_SCRIPT,
			HttpServletRequest req, HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return "NEED_LOGIN:" + error;
		}

		Integer id = businessSettingService
				.selecTemplateIdByName(TEMPLATE_NAME);
		if (null != id) {
			return "已经存在！";
		}
		try {
			businessSettingService.addTemplate(TEMPLATE_NAME, TEMPLATE_CONTENT,
					TEMPLATE_SCRIPT);
			return "";
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return "新增失败";
		}
	}

	// ------------------------------------------------------------
	// -- 子指标管理
	// ------------------------------------------------------------
	@RequestMapping("/submetric/select.do")
	public @ResponseBody String selectSubmetricist() {
		List<Map<String, Object>> result = businessSettingService
				.selectSubmetricList();
		return JSON.toJSONString(result);
	}

	@RequestMapping("/submetric/delete.do")
	public @ResponseBody String deleteSubmetric(
			@RequestParam("key") String key,
			@RequestParam(value = "id", required = true) Integer id,
			HttpServletRequest req, HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return "NEED_LOGIN:" + error;
		}
		try {
			businessSettingService.deleteSubmetric(id);
			// 删除对应的子配置项
			businessSettingService.deleteAllSubmetricDetail(id);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return "删除失败";// e.getMessage();
		}
		return "";
	}

	@RequestMapping("/submetric/save.do")
	public @ResponseBody String saveSubmetric(
			@RequestParam("key") String key,
			@RequestParam(value = "id", required = true) Integer id,
			@RequestParam(value = "SUB_WEB_METRIC_CODE", required = true) String SUB_WEB_METRIC_CODE,
			@RequestParam(value = "URL", required = false) String URL,
			@RequestParam(value = "URL_BAK", required = false) String URL_BAK,
			@RequestParam(value = "CONTENT_TYPE", required = false) String CONTENT_TYPE,
			@RequestParam(value = "PAGE_ENCODING", required = false) String PAGE_ENCODING,
			HttpServletRequest req, HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return "NEED_LOGIN:" + error;
		}
		try {
			businessSettingService.saveSubmetric(id, SUB_WEB_METRIC_CODE, URL,
					URL_BAK, CONTENT_TYPE, PAGE_ENCODING);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			// return e.getMessage();
			return "保存失败";
		}
		return "";
	}

	@RequestMapping("/submetric/add.do")
	public @ResponseBody String addSubmetric(
			@RequestParam("key") String key,
			@RequestParam(value = "SUB_WEB_METRIC_CODE", required = true) String SUB_WEB_METRIC_CODE,
			@RequestParam(value = "URL", required = false) String URL,
			@RequestParam(value = "URL_BAK", required = false) String URL_BAK,
			@RequestParam(value = "CONTENT_TYPE", required = false) String CONTENT_TYPE,
			@RequestParam(value = "PAGE_ENCODING", required = false) String PAGE_ENCODING,
			HttpServletRequest req, HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return "NEED_LOGIN:" + error;
		}

		Integer id = businessSettingService
				.selecSubmetricIdByName(SUB_WEB_METRIC_CODE);
		if (null != id) {
			return "已经存在！";
		}
		try {
			businessSettingService.addSubmetric(SUB_WEB_METRIC_CODE, URL,
					URL_BAK, CONTENT_TYPE, PAGE_ENCODING);
			return "";
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return "新增失败";
		}
	}

	// ------------------------------------------------------------
	// -- 子指标明细管理
	// ------------------------------------------------------------
	@RequestMapping("/submetricDetail/select.do")
	public @ResponseBody String selectSubmetricDetailist(
			@RequestParam(value = "id", required = false) Integer id) {
		if (null != id) {
			List<Map<String, Object>> result = businessSettingService
					.selectSubmetricDetailList(id);
			return JSON.toJSONString(result);
		} else {
			return "[]";
		}
	}

	@RequestMapping("/submetricDetail/deleteAll.do")
	public @ResponseBody String deleteAllSubPage(
			@RequestParam("key") String key,
			@RequestParam(value = "id", required = true) Integer id,
			HttpServletRequest req, HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return error;
		}
		try {
			businessSettingService.deleteAllSubmetricDetail(id);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return e.getMessage();
		}
		return "";
	}

	@RequestMapping("/submetricDetail/delete.do")
	public @ResponseBody String deleteSubmetricDetail(
			@RequestParam("key") String key,
			@RequestParam(value = "id", required = true) Integer id,
			HttpServletRequest req, HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return "NEED_LOGIN:" + error;
		}
		try {
			businessSettingService.deleteSubmetricDetail(id);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return "删除失败";// e.getMessage();
		}
		return "";
	}

	@RequestMapping("/submetricDetail/save.do")
	public @ResponseBody String saveSubmetricDetail(
			@RequestParam("key") String key,
			@RequestParam(value = "id", required = true) Integer id,
			@RequestParam(value = "METRIC_TITLE", required = true) String METRIC_TITLE,
			@RequestParam(value = "METRIC_SELECTOR", required = true) String METRIC_SELECTOR,
			@RequestParam(value = "VALUE_TYPE", required = false) String VALUE_TYPE,
			@RequestParam(value = "ORDER_NUM", required = false) String ORDER_NUM,
			HttpServletRequest req, HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return "NEED_LOGIN:" + error;
		}
		if (StringUtils.isEmpty(ORDER_NUM)) {
			ORDER_NUM = "0";
		}
		try {
			businessSettingService.saveSubmetricDetail(id, METRIC_TITLE,
					METRIC_SELECTOR, VALUE_TYPE, ORDER_NUM);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			// return e.getMessage();
			return "保存失败";
		}
		return "";
	}

	@RequestMapping("/submetricDetail/add.do")
	public @ResponseBody String addSubmetricDetail(
			@RequestParam("key") String key,
			@RequestParam(value = "id", required = true) Integer id,
			@RequestParam(value = "METRIC_TITLE", required = true) String METRIC_TITLE,
			@RequestParam(value = "METRIC_SELECTOR", required = true) String METRIC_SELECTOR,
			@RequestParam(value = "VALUE_TYPE", required = false) String VALUE_TYPE,
			@RequestParam(value = "ORDER_NUM", required = false) String ORDER_NUM,
			HttpServletRequest req, HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return "NEED_LOGIN:" + error;
		}
		if (StringUtils.isEmpty(ORDER_NUM)) {
			ORDER_NUM = "0";
		}
		try {
			businessSettingService.addSubmetricDetail(id, METRIC_TITLE,
					METRIC_SELECTOR, VALUE_TYPE, ORDER_NUM);
			return "";
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return "新增失败";
		}
	}

	// ------------------------------------------------------------
	// -- 指标管理
	// ------------------------------------------------------------
	@RequestMapping("/metric/selectSubs.do")
	public @ResponseBody String selectMetricSubsist() {
		List<Map<String, Object>> result = businessSettingService
				.selectMetricSubsist();
		return JSON.toJSONString(result);
	}

	@RequestMapping("/metric/selectTemplate.do")
	public @ResponseBody String selectMetricTemplatesist() {
		List<Map<String, Object>> result = businessSettingService
				.selectMetricTemplatesist();
		return JSON.toJSONString(result);
	}

	@RequestMapping("/metric/select.do")
	public @ResponseBody String selectMetriclist() {
		List<Map<String, Object>> result = businessSettingService
				.selectMetricList();
		return JSON.toJSONString(result);
	}

	@RequestMapping("/metric/delete.do")
	public @ResponseBody String deleteMetric(@RequestParam("key") String key,
			@RequestParam(value = "id", required = true) Integer id,
			HttpServletRequest req, HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return "NEED_LOGIN:" + error;
		}
		try {
			businessSettingService.deleteMetric(id);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return "删除失败";// e.getMessage();
		}
		return "";
	}

	@RequestMapping("/metric/save.do")
	public @ResponseBody String saveMetric(
			@RequestParam("key") String key,
			@RequestParam(value = "id", required = true) Integer id,
			@RequestParam(value = "WEB_METRIC_CODE", required = true) String WEB_METRIC_CODE,
			@RequestParam(value = "WEB_METRIC_TITLE", required = true) String WEB_METRIC_TITLE,
			HttpServletRequest req, HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return "NEED_LOGIN:" + error;
		}
		
		try {
			Integer tmpId = businessSettingService
					.selecMetricIdByName(id,WEB_METRIC_CODE);
			if (null != tmpId) {
				return WEB_METRIC_CODE + "指标编码已经存在！";
			}
			businessSettingService.saveMetric(id, WEB_METRIC_CODE,
					WEB_METRIC_TITLE);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			// return e.getMessage();
			return "保存失败";
		}
		return "";
	}

	@RequestMapping("/metric/add.do")
	public @ResponseBody String addMetric(
			@RequestParam("key") String key,
			@RequestParam(value = "WEB_METRIC_CODE", required = true) String WEB_METRIC_CODE,
			@RequestParam(value = "WEB_METRIC_TITLE", required = true) String WEB_METRIC_TITLE,
			HttpServletRequest req, HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return "NEED_LOGIN:" + error;
		}
		Integer tmpId = businessSettingService
				.selecMetricIdByName(null,WEB_METRIC_CODE);
		if (null != tmpId) {
			return WEB_METRIC_CODE + "指标编码已经存在！";
		}
		try {
			businessSettingService.addMetric(WEB_METRIC_CODE, WEB_METRIC_TITLE);
			return "";
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return "新增失败";
		}
	}

	@RequestMapping("/metric/saveRelSubs.do")
	public @ResponseBody String saveRelSubs(
			@RequestParam("key") String key,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "REL_IDS", required = true) String relIds,
			HttpServletRequest req, HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return "NEED_LOGIN:" + error;
		}
		try {
			businessSettingService.saveRelSubs(id, relIds);
			return "";
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return "新增失败";
		}
	}
	@RequestMapping("/metric/saveRelTemplates.do")
	public @ResponseBody String saveRelTemplates(
			@RequestParam("key") String key,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "REL_IDS", required = true) String relIds,
			HttpServletRequest req, HttpServletResponse resp) {
		String error = userService.check(key);
		if (!"".equals(error)) {
			return "NEED_LOGIN:" + error;
		}
		try {
			businessSettingService.saveRelTemplates(id, relIds);
			return "";
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return "新增失败";
		}
	}
}
