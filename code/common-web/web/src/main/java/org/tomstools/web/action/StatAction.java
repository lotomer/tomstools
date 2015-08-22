/**
 * copyright (a) 2010-2015 tomstools.org. All rights reserved.
 */
package org.tomstools.web.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tomstools.web.service.SolrService;

import com.alibaba.fastjson.JSON;

/**
 * ajax
 * 
 * @author admin
 * @date 2015年5月16日
 * @time 上午9:42:03
 * @version 1.0
 */
@Controller
@RequestMapping("/crawl")
public class StatAction {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	@Autowired
	private SolrService solrService;

	@RequestMapping("/stat/words.do")
	public @ResponseBody String words(@RequestParam("key") String key, @RequestParam("startTime") String startTime,
			@RequestParam("endTime") String endTime, @RequestParam(value = "typeId", required = false) Integer typeId,
			HttpServletRequest req, HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");
		Date end = null;
		if (!StringUtils.isEmpty(endTime)) {
			end = DATE_FORMAT.parse(endTime);
		} else {
			end = new Date();
		}
		Date start = null;
		if (!StringUtils.isEmpty(startTime)) {
			start = DATE_FORMAT.parse(startTime);
		} else {
			start = new Date(end.getTime() - SolrService.STAT_TIME_DEFAULT);
		}

		List<Map<String, Object>> s = solrService.statWords(start, end, typeId);

		return JSON.toJSONString(s);
	}

	@RequestMapping("/stat/wordsWithWordCount.do")
	public @ResponseBody String wordsWithWordCount(@RequestParam("key") String key,
			@RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime,
			@RequestParam(value = "word", required = false) String word, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");
		Date end = null;
		if (!StringUtils.isEmpty(endTime)) {
			end = DATE_FORMAT.parse(endTime);
		} else {
			end = new Date();
		}
		Date start = null;
		if (!StringUtils.isEmpty(startTime)) {
			start = DATE_FORMAT.parse(startTime);
		} else {
			start = new Date(end.getTime() - SolrService.STAT_TIME_DEFAULT);
		}

		List<Map<String, Object>> s = solrService.statWordsWithWordCount(start, end, word);

		return JSON.toJSONString(s);
	}

	@RequestMapping("/stat/wordsCountQuery.do")
	public @ResponseBody String wordsCountQuery(@RequestParam("key") String key,
			@RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime,
			@RequestParam(value = "langId", required = false) Integer langId,
			@RequestParam(value = "countryId", required = false) Integer countryId,
			@RequestParam(value = "siteTypeId", required = false) Integer siteTypeId,
			@RequestParam(value = "siteId", required = false) Integer siteId, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");
		Date end = null;
		if (!StringUtils.isEmpty(endTime)) {
			end = DATE_FORMAT.parse(endTime);
		} else {
			end = new Date();
		}
		Date start = null;
		if (!StringUtils.isEmpty(startTime)) {
			start = DATE_FORMAT.parse(startTime);
		} else {
			start = new Date(end.getTime() - SolrService.STAT_TIME_DEFAULT);
		}

		List<Map<String, Object>> result = solrService.statWordsCountQuery(start, end, langId, countryId,siteTypeId,siteId);
		if (null != result) {
			return JSON.toJSONString(result);
		} else {
			return "[]";
		}
	}

	@RequestMapping("/stat/wordsCount.do")
	public @ResponseBody String wordsCount(@RequestParam("key") String key, @RequestParam("startTime") String startTime,
			@RequestParam("endTime") String endTime, @RequestParam(value = "typeId", required = false) Integer typeId,
			HttpServletRequest req, HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");
		Date end = null;
		if (!StringUtils.isEmpty(endTime)) {
			end = DATE_FORMAT.parse(endTime);
		} else {
			end = new Date();
		}
		Date start = null;
		if (!StringUtils.isEmpty(startTime)) {
			start = DATE_FORMAT.parse(startTime);
		} else {
			start = new Date(end.getTime() - SolrService.STAT_TIME_DEFAULT);
		}

		List<Map<String, Object>> result = solrService.statWordsCount(start, end, typeId);
		if (null != result) {
			return JSON.toJSONString(result);
		} else {
			return "[]";
		}
	}

	@RequestMapping("/stat/today.do")
	public @ResponseBody String wordsCountToday(@RequestParam("key") String key, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");
		Calendar now = Calendar.getInstance();
		Calendar start = Calendar.getInstance();
		start.set(Calendar.HOUR_OF_DAY, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);

		List<Map<String, Object>> result = solrService.statWordsCount(start.getTime(), now.getTime(), null);
		if (null != result) {
			return JSON.toJSONString(result);
		} else {
			return "[]";
		}
	}

	@RequestMapping("/stat/yesterday.do")
	public @ResponseBody String wordsCountYesterday(@RequestParam("key") String key, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");
		Calendar now = Calendar.getInstance();
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		Calendar start = Calendar.getInstance();
		start.setTime(now.getTime());
		start.add(Calendar.DAY_OF_MONTH, -1);

		List<Map<String, Object>> result = solrService.statWordsCount(start.getTime(), now.getTime(), null);
		if (null != result) {
			return JSON.toJSONString(result);
		} else {
			return "[]";
		}
	}

	@RequestMapping("/stat/siteTop.do")
	public @ResponseBody String siteTop(@RequestParam("key") String key,
			@RequestParam(value = "topNum", required = false) Integer topNum, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");
		Calendar now = Calendar.getInstance();
		Calendar start = Calendar.getInstance();
		start.set(Calendar.HOUR_OF_DAY, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		start.add(Calendar.DAY_OF_MONTH, -7);
		List<Map<String, Object>> result = solrService.siteTop(start.getTime(), now.getTime(),
				null != topNum ? topNum : 5);
		if (null != result) {
			return JSON.toJSONString(result);
		} else {
			return "[]";
		}
	}

	/** 媒体类型统计 */
	@RequestMapping("/stat/mediaCount.do")
	public @ResponseBody String statMediaCount(@RequestParam("key") String key, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");
		Calendar now = Calendar.getInstance();
		Calendar start = Calendar.getInstance();
		start.set(Calendar.HOUR_OF_DAY, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		start.add(Calendar.DAY_OF_MONTH, -7);
		List<Map<String, Object>> result = solrService.statMediaCount(start.getTime(), now.getTime());
		if (null != result) {
			return JSON.toJSONString(result);
		} else {
			return "[]";
		}
	}

	/** 媒体类型分时间统计 */
	@RequestMapping("/stat/media.do")
	public @ResponseBody String statMedia(@RequestParam("key") String key, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");
		Calendar now = Calendar.getInstance();
		Calendar start = Calendar.getInstance();
		start.set(Calendar.HOUR_OF_DAY, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		start.add(Calendar.DAY_OF_MONTH, -7);
		List<Map<String, Object>> result = solrService.statMedia(start.getTime(), now.getTime());
		if (null != result) {
			return JSON.toJSONString(result);
		} else {
			return "[]";
		}
	}

	private void get(Map<String, Object> total, Map<String, Object> fm, Date startTime, Date endTime, String key) {
		List<Map<String, Object>> r = solrService.statWordsCountAll(startTime, endTime);
		if (null != r && 0 != r.size() && null != r.get(0)) {
			Long sizeZM = Long.valueOf(String.valueOf(r.get(0).get("SIZE_ZM")));
			Long sizeFM = Long.valueOf(String.valueOf(r.get(0).get("SIZE_FM")));
			sizeZM = sizeZM != null ? sizeZM : 0;
			sizeFM = sizeFM != null ? sizeFM : 0;
			total.put(key, sizeZM + sizeFM);
			fm.put(key, sizeFM);
		} else {
			total.put(key, 0);
			fm.put(key, 0);
		}
	}

	/**
	 * 新增分布。<br>
	 * 时间维度分为：今日、最近七天、最近一个月、最近一年 内容分为：总数、负面数
	 */
	@RequestMapping("/stat/increment.do")
	public @ResponseBody String increment(@RequestParam("key") String key, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> total = new HashMap<String, Object>();
		Map<String, Object> fm = new HashMap<String, Object>();
		result.add(total);
		result.add(fm);
		total.put("name", "新增总文章数");
		fm.put("name", "新增负面文章");
		Calendar now = Calendar.getInstance();
		Calendar start = Calendar.getInstance();
		start.set(Calendar.HOUR_OF_DAY, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		// start.add(Calendar.DAY_OF_MONTH, -7);
		// 获取今天数据
		get(total, fm, start.getTime(), now.getTime(), "1d");
		// 获取最近七天的数据
		start.add(Calendar.DAY_OF_MONTH, -7);
		get(total, fm, start.getTime(), now.getTime(), "7d");
		// 获取最近一个月的数据
		start.add(Calendar.DAY_OF_MONTH, 7);
		start.add(Calendar.MONTH, -1);
		get(total, fm, start.getTime(), now.getTime(), "1m");
		// 获取最近一年的数据
		start.add(Calendar.MONTH, 1);
		start.add(Calendar.YEAR, -1);
		get(total, fm, start.getTime(), now.getTime(), "1y");

		return JSON.toJSONString(result);
	}

	@RequestMapping("/lang.do")
	public @ResponseBody String lang(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");

		List<Map<String, Object>> result = solrService.selectLanguage();
		if (null != result) {
			return JSON.toJSONString(result);
		} else {
			return "[]";
		}
	}

	@RequestMapping("/country.do")
	public @ResponseBody String country(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");

		List<Map<String, Object>> result = solrService.selectCountry();
		if (null != result) {
			return JSON.toJSONString(result);
		} else {
			return "[]";
		}
	}

	@RequestMapping("/selectSiteType.do")
	public @ResponseBody String selectSiteType(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");

		List<Map<String, Object>> result = solrService.selectSiteType("1");
		if (null != result) {
			return JSON.toJSONString(result);
		} else {
			return "[]";
		}
	}

	@RequestMapping("/selectSite.do")
	public @ResponseBody String selectSite(@RequestParam(value = "siteTypeId", required = false) Integer siteTypeId,
			HttpServletRequest req, HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");

		List<Map<String, Object>> result = solrService.selectSite(siteTypeId,"1");
		if (null != result) {
			return JSON.toJSONString(result);
		} else {
			return "[]";
		}
	}

	@RequestMapping("/query.do")
	public @ResponseBody String query(@RequestParam("key") String key,
			@RequestParam(value = "start", required = false) Integer start,
			@RequestParam(value = "rows", required = false) Integer rows, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");

		List<Map<String, Object>> result = solrService.query(start, rows);
		if (null != result) {
			return JSON.toJSONString(result);
		} else {
			return "[]";
		}
	}
}
