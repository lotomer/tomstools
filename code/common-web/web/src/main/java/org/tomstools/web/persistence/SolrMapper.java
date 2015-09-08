package org.tomstools.web.persistence;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface SolrMapper {

	/**
	 * 获取最后一次统计的时间
	 * 
	 * @param typeId
	 *            词条编号
	 * @return 最后一次统计的时间
	 */
	// public Date selectLastStatTime(@Param("TYPE_ID") Integer typeId);

	/**
	 * 将统计结果保存
	 * 
	 * @param typeId
	 *            词条编号
	 * @param countZM
	 *            正面信息数
	 * @param countFM
	 *            负面信息数
	 * @param countFM_E
	 *            英文正面信息数
	 * @param countZM_E
	 *            英文负面信息数
	 * @param statTime
	 *            统计时间
	 */
	// public void saveStat(@Param("TYPE_ID") Integer typeId, @Param("SIZE_ZM")
	// long countZM,
	// @Param("SIZE_FM") long countFM, @Param("SIZE_ZM_E") long countZM_E,
	// @Param("SIZE_FM_E") long countFM_E, @Param("STAT_TIME") Date statTime);

	/** 获取语言列表 */
	public List<Map<String, Object>> selectLanguage();

	/** 获取国家 */
	public List<Map<String, Object>> selectCountry();

	/**
	 * 获取站点类型列表
	 * 
	 * @param isValid
	 *            是否有效。1 有效；0 无效。如果为null，则默认所有
	 */
	public List<Map<String, Object>> selectSiteType(@Param("IS_VALID") String isValid);

	/** 根据站点类型编号获取站点列表。如果站点类型编号为null，则获取所有站点列表 */
	public List<Map<String, Object>> selectSite(@Param("SITE_TYPE_ID") Integer siteTypeId,
			@Param("IS_VALID") String isValid);

	/**
	 * 查询预警信息
	 * 
	 * @param alertType
	 * @param rows 
	 * @param start 
	 */
	public List<Map<String, Object>> selectAlertLog(@Param("START_TIME") Date startTime,
			@Param("END_TIME") Date endTime, @Param("NOTIFY_STATUS") String notifyStatus,
			@Param("ALERT_TYPE") String alertType, @Param("START") int start, @Param("ROWS") int rows);
	public int countAlertLog(@Param("START_TIME") Date startTime,
			@Param("END_TIME") Date endTime, @Param("NOTIFY_STATUS") String notifyStatus,
			@Param("ALERT_TYPE") String alertType);

	public void saveAlertLog(@Param("ALERT_ID") Integer alertId, @Param("ALERT_VALUE") int threhold,
			@Param("CURRENT_VALUE") int value);

	public void updateAlertLogNotified(@Param("ID")  Long id, @Param("NOTIFY_STATUS") int notifyStatus);
	
	public List<Map<String, Object>> selectWeekly(@Param("YEAR") Integer year, @Param("MONTH") Integer month,
			@Param("WEEK") Integer week);

	public Map<String, Object> selectWeeklyById(@Param("ID") Integer id);
	public void deleteWeeklyById(@Param("ID") Integer id);
	public void saveWeekly(@Param("YEAR") int year, @Param("MONTH") int month, @Param("WEEK") int week,
			@Param("PATH") String filePath, @Param("FILE_NAME") String fileName, @Param("FILE_SIZE") long size,
			@Param("FILE_TYPE") String contentType, @Param("USER_ID") int userId);

	public List<Map<String, Object>> selectWordsAlertTop(@Param("START_TIME") Date startTime, @Param("END_TIME") Date endTime,
			@Param("TOP_NUM") int topNum);

}
