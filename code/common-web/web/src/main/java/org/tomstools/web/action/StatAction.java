/**
 * copyright (a) 2010-2015 tomstools.org. All rights reserved.
 */
package org.tomstools.web.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.tomstools.web.model.User;
import org.tomstools.web.service.SolrService;
import org.tomstools.web.service.UserService;

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
	private static final Log LOG = LogFactory.getLog(StatAction.class);
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	@Autowired
	private SolrService solrService;

	@Autowired
	private UserService userService;

	@RequestMapping("/stat/words.do")
	public @ResponseBody String words(@RequestParam("key") String key, @RequestParam("startTime") String startTime,
			@RequestParam("endTime") String endTime, @RequestParam(value = "typeId", required = false) Integer typeId,
			HttpServletRequest req, HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");
		Date end = null;
		if (!StringUtils.isEmpty(endTime)) {
			Calendar c = Calendar.getInstance();
			c.setTime(DATE_FORMAT.parse(endTime));
			c.add(Calendar.DAY_OF_MONTH, 1);
			end = c.getTime();
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
			Calendar c = Calendar.getInstance();
			c.setTime(DATE_FORMAT.parse(endTime));
			c.add(Calendar.DAY_OF_MONTH, 1);
			end = c.getTime();
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
			Calendar c = Calendar.getInstance();
			c.setTime(DATE_FORMAT.parse(endTime));
			c.add(Calendar.DAY_OF_MONTH, 1);
			end = c.getTime();
		} else {
			end = new Date();
		}
		Date start = null;
		if (!StringUtils.isEmpty(startTime)) {
			start = DATE_FORMAT.parse(startTime);
		} else {
			start = new Date(end.getTime() - SolrService.STAT_TIME_DEFAULT);
		}

		List<Map<String, Object>> result = solrService.statWordsCountQuery(start, end, langId, countryId, siteTypeId,
				siteId);
		if (null != result) {
			return JSON.toJSONString(result);
		} else {
			return "[]";
		}
	}

	@RequestMapping("/stat/wordsQueryDetail.do")
	public @ResponseBody String wordsQueryDetail(@RequestParam("key") String key,
			@RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime,
			@RequestParam(value = "langId", required = false) Integer langId,
			@RequestParam(value = "countryId", required = false) Integer countryId,
			@RequestParam(value = "siteTypeId", required = false) Integer siteTypeId,
			@RequestParam(value = "siteId", required = false) Integer siteId,
			@RequestParam(value = "wordsId", required = false) Integer wordsId,
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "rows", required = false) Integer rows, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");
		Date end = null;
		if (!StringUtils.isEmpty(endTime)) {
			Calendar c = Calendar.getInstance();
			c.setTime(DATE_FORMAT.parse(endTime));
			c.add(Calendar.DAY_OF_MONTH, 1);
			end = c.getTime();
		} else {
			end = new Date();
		}
		Date start = null;
		if (!StringUtils.isEmpty(startTime)) {
			start = DATE_FORMAT.parse(startTime);
		} else {
			start = new Date(end.getTime() - SolrService.STAT_TIME_DEFAULT);
		}
		rows = rows == null? 10 : rows;
		int startNum = page == null ? 0 : (page - 1) * rows;
		int total = solrService.countWordsQueryDetail(start, end, langId, countryId, siteTypeId,
				siteId, wordsId);
		List<Map<String, Object>> details = solrService.statWordsQueryDetail(start, end, langId, countryId, siteTypeId,
				siteId, wordsId,startNum, rows);
		if (null == details){
			details = Collections.emptyList();
		}
		Map<String,Object> result = new HashMap<String, Object>();
		result.put("total", total);
		result.put("rows", details);
		return JSON.toJSONString(result);
	}

	@RequestMapping("/stat/wordsCount.do")
	public @ResponseBody String wordsCount(@RequestParam("key") String key, @RequestParam("startTime") String startTime,
			@RequestParam("endTime") String endTime, @RequestParam(value = "typeId", required = false) Integer typeId,
			HttpServletRequest req, HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");
		Date end = null;
		if (!StringUtils.isEmpty(endTime)) {
			Calendar c = Calendar.getInstance();
			c.setTime(DATE_FORMAT.parse(endTime));
			c.add(Calendar.DAY_OF_MONTH, 1);
			end = c.getTime();
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
	public @ResponseBody String wordsCountToday(@RequestParam("key") String key,@RequestParam(value="TYPE_ID",required=false) Integer typeId, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");
		Calendar now = Calendar.getInstance();
		Calendar start = Calendar.getInstance();
		start.set(Calendar.HOUR_OF_DAY, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);

		List<Map<String, Object>> result = solrService.statWordsCount(start.getTime(), now.getTime(), typeId);
		if (null != result) {
			return JSON.toJSONString(result);
		} else {
			return "[]";
		}
	}

	@RequestMapping("/stat/yesterday.do")
	public @ResponseBody String wordsCountYesterday(@RequestParam("key") String key,@RequestParam(value="TYPE_ID",required=false) Integer typeId, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DAY_OF_MONTH, -1);
		now.set(Calendar.HOUR_OF_DAY, 23);
        now.set(Calendar.MINUTE, 59);
		now.set(Calendar.SECOND, 59);
		Calendar start = Calendar.getInstance();
		start.setTime(now.getTime());
		start.set(Calendar.HOUR_OF_DAY, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
        
		List<Map<String, Object>> result = solrService.statWordsCount(start.getTime(), now.getTime(), typeId);
		if (null != result) {
			return JSON.toJSONString(result);
		} else {
			return "[]";
		}
	}

	@RequestMapping("/stat/siteTop.do")
	public @ResponseBody String siteTop(@RequestParam("key") String key,@RequestParam(value="TYPE_ID",required=false) Integer typeId,
			@RequestParam(value = "topNum", required = false) Integer topNum, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");
		Calendar now = Calendar.getInstance();
		Calendar start = Calendar.getInstance();
		start.set(Calendar.HOUR_OF_DAY, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		start.add(Calendar.DAY_OF_MONTH, -7);
		List<Map<String, Object>> result = solrService.siteTop(start.getTime(), now.getTime(),typeId,
				null != topNum ? topNum : 5);
		if (null != result) {
			return JSON.toJSONString(result);
		} else {
			return "[]";
		}
	}
	@RequestMapping("/query/wordsTop.do")
	public @ResponseBody String selectWordsTop(@RequestParam("key") String key,
			@RequestParam(value = "topNum", required = false) Integer topNum, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");
		User user = userService.getUserByKey(key);
		if (user != null){
			String s = userService.getConfig(user.getUserId(), "DAYS_AGO_4_TOP_WORDS");
			int days = 1;
			if (!StringUtils.isEmpty(s)){
				try {
					days = Integer.valueOf(s);
				} catch (NumberFormatException e) {
				}
			}
			
			Calendar now = Calendar.getInstance();
			Calendar start = Calendar.getInstance();
			//start.set(Calendar.HOUR_OF_DAY, 0);
			//start.set(Calendar.MINUTE, 0);
			//start.set(Calendar.SECOND, 0);
			start.add(Calendar.DAY_OF_MONTH, 0 - days);
			List<Map<String, Object>> result = solrService.selectWordsTop(start.getTime(), now.getTime(),
					null != topNum ? topNum : 5);
			if (null != result) {
				return JSON.toJSONString(result);
			}
		}
			return "[]";
	}

	@RequestMapping("/query/hotwordTop.do")
	public @ResponseBody String selectHotwordTop(@RequestParam("key") String key,
			@RequestParam(value = "topNum", required = false) Integer topNum, 
			@RequestParam(value = "DAYS", required = false) Integer days,
			@RequestParam(value = "FLAG", required = false) String flag, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");
		User user = userService.getUserByKey(key);
		if (user != null){
			if (null == days){
				String s = userService.getConfig(user.getUserId(), "DAYS_AGO_4_TOP_HOTWORD");
				if (!StringUtils.isEmpty(s)){
					try {
						days = Integer.valueOf(s);
					} catch (NumberFormatException e) {
					}
				}
			}
			
			Calendar now = Calendar.getInstance();
			Calendar start = Calendar.getInstance();
			//start.set(Calendar.HOUR_OF_DAY, 0);
			//start.set(Calendar.MINUTE, 0);
			//start.set(Calendar.SECOND, 0);
			start.add(Calendar.DAY_OF_MONTH, 0 - days);
			List<Map<String, Object>> result = solrService.selectHotwordTop(start.getTime(), now.getTime(),
					null != topNum ? topNum : 5,flag);
			if (null != result) {
				return JSON.toJSONString(result);
			}
		}
			return "[]";
	}
	@RequestMapping("/query/hotword.do")
	public @ResponseBody String selectHotword(@RequestParam("key") String key,
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "rows", required = false) Integer rows,
			@RequestParam(value = "DAYS", required = false) Integer days,
			@RequestParam(value = "FLAG", required = false) String flag, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");
		User user = userService.getUserByKey(key);
		if (user != null){
			if (null == days){
				String s = userService.getConfig(user.getUserId(), "DAYS_AGO_4_TOP_HOTWORD");
				if (!StringUtils.isEmpty(s)){
					try {
						days = Integer.valueOf(s);
					} catch (NumberFormatException e) {
					}
				}
			}
			
			Calendar now = Calendar.getInstance();
			Calendar begin = Calendar.getInstance();
			//start.set(Calendar.HOUR_OF_DAY, 0);
			//start.set(Calendar.MINUTE, 0);
			//start.set(Calendar.SECOND, 0);
			begin.add(Calendar.DAY_OF_MONTH, 0 - days);
			int total = solrService.countHot(begin.getTime(), now.getTime(),flag);
			rows = rows == null? total : rows;
			int startNum = page == null ? 0 : (page - 1) * rows;
			List<Map<String, Object>> details = solrService.selectHot(begin.getTime(), now.getTime(),flag,startNum,rows);
			if (null == details){
				details = Collections.emptyList();
			}
			Map<String,Object> result = new HashMap<String, Object>();
			result.put("total", total);
			result.put("rows", details);
			return JSON.toJSONString(result);
		}
		return "[]";
	}
	/** 媒体类型统计 */
	@RequestMapping("/stat/mediaCount.do")
	public @ResponseBody String statMediaCount(@RequestParam("key") String key,@RequestParam(value="TYPE_ID",required=false) Integer typeId, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");
		Calendar now = Calendar.getInstance();
		Calendar start = Calendar.getInstance();
		start.set(Calendar.HOUR_OF_DAY, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		start.add(Calendar.DAY_OF_MONTH, -7);
		List<Map<String, Object>> result = solrService.statMediaCount(start.getTime(), now.getTime(),typeId);
		if (null != result) {
			return JSON.toJSONString(result);
		} else {
			return "[]";
		}
	}

	/** 媒体类型分时间统计 */
	@RequestMapping("/stat/media.do")
	public @ResponseBody String statMedia(@RequestParam("key") String key,@RequestParam(value="TYPE_ID",required=false) Integer typeId, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");
		Calendar now = Calendar.getInstance();
		Calendar start = Calendar.getInstance();
		start.set(Calendar.HOUR_OF_DAY, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		start.add(Calendar.DAY_OF_MONTH, -7);
		List<Map<String, Object>> result = solrService.statMedia(start.getTime(), now.getTime(),typeId);
		if (null != result) {
			return JSON.toJSONString(result);
		} else {
			return "[]";
		}
	}

	private void get(Map<String, Object> total, Map<String, Object> fm, Date startTime, Date endTime, String key, Integer typeId) {
		List<Map<String, Object>> r = solrService.statWordsCountAll(startTime, endTime,typeId);
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
	public @ResponseBody String increment(@RequestParam("key") String key,@RequestParam(value="TYPE_ID",required=false) Integer typeId, HttpServletRequest req,
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
		get(total, fm, start.getTime(), now.getTime(), "1d",typeId);
		// 获取最近七天的数据
		start.add(Calendar.DAY_OF_MONTH, -7);
		get(total, fm, start.getTime(), now.getTime(), "7d",typeId);
		// 获取最近一个月的数据
		start.add(Calendar.DAY_OF_MONTH, 7);
		start.add(Calendar.MONTH, -1);
		get(total, fm, start.getTime(), now.getTime(), "1m",typeId);
		// 获取最近一年的数据
		start.add(Calendar.MONTH, 1);
		start.add(Calendar.YEAR, -1);
		get(total, fm, start.getTime(), now.getTime(), "1y",typeId);

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

		List<Map<String, Object>> result = solrService.selectSite(siteTypeId, "1");
		if (null != result) {
			return JSON.toJSONString(result);
		} else {
			return "[]";
		}
	}

	@RequestMapping("/query.do")
	public @ResponseBody String query(@RequestParam("key") String key,
			@RequestParam(value = "TYPE_ID", required = false) Integer typeId,
			@RequestParam(value = "start", required = false) Integer start,
			@RequestParam(value = "rows", required = false) Integer rows, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");

		List<Map<String, Object>> result = solrService.query(typeId, start, rows);
		if (null != result) {
			return JSON.toJSONString(result);
		} else {
			return "[]";
		}
	}

	@RequestMapping("/query/alertQuery.do")
	public @ResponseBody String alertQuery(@RequestParam("key") String key, @RequestParam("startTime") String startTime,
			@RequestParam("endTime") String endTime,
			@RequestParam(value = "NOTIFY_STATUS", required = false) String notifyStatus,
			@RequestParam(value = "ALERT_TYPE", required = false) String alertType, 
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "rows", required = false) Integer rows,HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");
		Date end = null;
		if (!StringUtils.isEmpty(endTime)) {
			Calendar c = Calendar.getInstance();
			c.setTime(DATE_FORMAT.parse(endTime));
			c.add(Calendar.DAY_OF_MONTH, 1);
			end = c.getTime();
		} else {
			end = new Date();
		}
		Date start = null;
		if (!StringUtils.isEmpty(startTime)) {
			start = DATE_FORMAT.parse(startTime);
		} else {
			start = new Date(end.getTime() - SolrService.STAT_TIME_DEFAULT);
		}
		int total = solrService.countAlertLog(start, end, notifyStatus, alertType);
		rows = rows == null? total : rows;
		int startNum = page == null ? 0 : (page - 1) * rows;
		List<Map<String, Object>> details = solrService.selectAlertLog(start, end, notifyStatus, alertType,startNum,rows);
		if (null == details){
			details = Collections.emptyList();
		}
		Map<String,Object> result = new HashMap<String, Object>();
		result.put("total", total);
		result.put("rows", details);
		return JSON.toJSONString(result);
	}

	@RequestMapping("/query/weeklyQuery.do")
	public @ResponseBody String weeklyQuery(@RequestParam("key") String key,
			@RequestParam(value = "YEAR", required = false) Integer year,
			@RequestParam(value = "MONTH", required = false) Integer month,
			@RequestParam(value = "WEEK", required = false) Integer week, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");

		List<Map<String, Object>> s = solrService.selectWeekly(year, month, week);

		return JSON.toJSONString(s);
	}

	@RequestMapping(value = "/weekly/upload.do", method = RequestMethod.POST)
	public @ResponseBody String upload(@RequestParam("key") String key, @RequestParam("YEAR") int year,
			@RequestParam("MONTH") int month, @RequestParam("WEEK") int week, MultipartHttpServletRequest request,
			HttpServletResponse response) {
		User user = userService.getUserByKey(key);
		String error = userService.check(user);
		if (!"".equals(error)) {
			return "NEED_LOGIN:" + error;
		}
		File path = new File(userService.getConfig(user.getUserId(), "UPLOAD_PATH"));
		if (!path.exists()) {
			// 创建目录
			if (!path.mkdirs()) {
				LOG.warn("Create directory failed! \"" + path.getAbsolutePath() + "\"");
			}
		}
		// 逐个处理文件
		Map<String, MultipartFile> files = request.getFileMap();
		MultipartFile mpf = null;
		int count = 1;
		for (Entry<String, MultipartFile> entry : files.entrySet()) {
			mpf = entry.getValue();
			String fileName = mpf.getOriginalFilename();
			int index = fileName.lastIndexOf(".");
			File filePath = new File(path, String.format("weekly-%d-%d-%d.%d.%d%s", year, month, week, count++,
					System.currentTimeMillis(), fileName.substring(index)));
			try {
				// 保存文件
				FileCopyUtils.copy(mpf.getBytes(), new FileOutputStream(filePath));
				solrService.saveWeekly(year, month, week, filePath.getAbsolutePath(), fileName, mpf.getSize(),
						mpf.getContentType(), user.getUserId());
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
				return "上传失败：" + e.getMessage() + "";
			}
		}
		return "";
	}

	@RequestMapping("/weekly/download.do")
	public void weeklyDownload(@RequestParam("key") String key, @RequestParam(value = "id", required = true) Integer id,
			HttpServletRequest req, HttpServletResponse resp) throws Exception {
		// resp.setContentType("application/json;charset=UTF-8");
		Map<String, Object> weekly = solrService.selectWeeklyById(id);
		String path = String.valueOf(weekly.get("PATH"));
		String filename = URLEncoder.encode(String.valueOf(weekly.get("FILE_NAME")), "UTF8");
		// 如果没有UA，则默认使用IE的方式进行编码，因为毕竟IE还是占多数的
		String userAgent = req.getHeader("User-Agent");
		String rtn = "filename=\"" + filename + "\"";
		if (userAgent != null) {
			userAgent = userAgent.toLowerCase();
			// IE浏览器，只能采用URLEncoder编码
			if (userAgent.indexOf("msie") != -1) {
				rtn = "filename=\"" + filename + "\"";
			}
			// Opera浏览器只能采用filename*
			else if (userAgent.indexOf("opera") != -1) {
				rtn = "filename*=UTF-8''" + filename;
			}
			// Safari浏览器，只能采用ISO编码的中文输出
			else if (userAgent.indexOf("safari") != -1) {
				rtn = "filename=\"" + new String(filename.getBytes("UTF-8"), "ISO8859-1") + "\"";
			}
			// Chrome浏览器，只能采用MimeUtility编码或ISO编码的中文输出
			else if (userAgent.indexOf("applewebkit") != -1) {
				filename = MimeUtility.encodeText(filename, "UTF8", "B");
				rtn = "filename=\"" + filename + "\"";
			}
			// FireFox浏览器，可以使用MimeUtility或filename*或ISO编码的中文输出
			else if (userAgent.indexOf("mozilla") != -1) {
				rtn = "filename*=UTF-8''" + filename;
			}
		}
		String disp = "attachment; " + rtn + "";
		try {
			resp.setContentType(String.valueOf(weekly.get("FILE_TYPE")));
			resp.setHeader("Content-disposition", disp);
			FileUtils.copyFile(new File(path), resp.getOutputStream());
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@RequestMapping("/weekly/delete.do")
	public @ResponseBody String weeklyDelete(@RequestParam("key") String key,
			@RequestParam(value = "id", required = true) Integer id, HttpServletRequest req, HttpServletResponse resp)
					throws Exception {

		String error = userService.check(key);
		if (!"".equals(error)) {
			return "NEED_LOGIN:" + error;
		}
		try {
			solrService.deleteWeeklyById(id);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return e.getMessage();
		}
		return "";
	}
	@RequestMapping("/stat/wordsAlertTop.do")
	public @ResponseBody String selectWordsAlertTop(@RequestParam("key") String key,
			@RequestParam(value = "topNum", required = false) Integer topNum, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");
		Calendar now = Calendar.getInstance();
		Calendar start = Calendar.getInstance();  //当月
		start.set(Calendar.DAY_OF_MONTH, 1);
		start.set(Calendar.HOUR_OF_DAY, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		List<Map<String, Object>> result = solrService.selectWordsAlertTop(start.getTime(), now.getTime(),
				null != topNum ? topNum : 5);
		if (null != result) {
			return JSON.toJSONString(result);
		} else {
			return "[]";
		}
	}
	
	@RequestMapping("/query/parseWords.do")
	public @ResponseBody String parseWords(@RequestParam("key") String key,
			@RequestParam(value = "WORDS", required = true) String words,HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");
		User user = userService.getUserByKey(key);
		if (user != null){
			
			List<String> result = solrService.parseWords(words);
			if (null != result) {
				return JSON.toJSONString(result);
			}
		}
		return "[]";
	}
}
