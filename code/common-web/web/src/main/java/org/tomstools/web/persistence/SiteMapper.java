package org.tomstools.web.persistence;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface SiteMapper {

	/** 获取站点 */
	public List<Map<String, Object>> selectSiteList();

	/**
	 * 获取最后一次统计的时间
	 * 
	 * @param typeId
	 *            词条编号
	 * @return 最后一次统计的时间
	 */
	public Date selectLastStatTime(@Param("TYPE_ID") Integer typeId);

	public void saveStat(@Param("TYPE_ID") Integer typeId, @Param("SITE_ID") Integer siteId,
			@Param("SIZE_ZM") long countZM, @Param("SIZE_FM") long countFM, @Param("SIZE_ZM_E") long countZM_E,
			@Param("SIZE_FM_E") long countFM_E, @Param("STAT_TIME") Date statTime);

	public List<Map<String, Object>> selectSiteTop(@Param("START_TIME") Date startTime, @Param("END_TIME") Date endTime,
			@Param("TOP_NUM") int topNum);

	public List<Map<String, Object>> selectMediaCount(@Param("START_TIME") Date startTime,
			@Param("END_TIME") Date endTime);

	public List<Map<String, Object>> selectMedia(@Param("START_TIME") Date startTime, @Param("END_TIME") Date endTime);

	public void saveDetail(@Param("SITE_ID") int siteId, @Param("TITLE") String title, @Param("URL") String url,
			@Param("TSTAMP") Date tstamp);

	public List<Map<String, Object>> selectDetail(@Param("START") Integer start, @Param("ROWS") Integer rows);

	public List<Map<String, Object>> statWordsCountQuery(@Param("START_TIME") Date startTime,
			@Param("END_TIME") Date endTime, @Param("LANG_ID") Integer langId,
			@Param("COUNTRY_CODE") Integer countryId, @Param("SITE_TYPE_ID")Integer siteTypeId, @Param("SITE_ID")Integer siteId);
	
	
	
	/**
	 * 获取指定时间内指定词条按日统计结果。<br>
	 * 如果typeId为null，则表示所有词条按词条按日统计
	 * @param startTime	开始时间
	 * @param endTime 结束时间
	 * @param typeId 词条编号。
	 * @return 统计结果。分词条，并按日统计
	 */
	public List<Map<String, Object>> selectStats(@Param("START_TIME") Date startTime, @Param("END_TIME") Date endTime,
			@Param("TYPE_ID") Integer typeId);

	/**
	 * 获取指定时间内指定词条进行统计。<br>
	 * 如果typeId为null，则表示所有词条按词条统计
	 * @param startTime	开始时间
	 * @param endTime 结束时间
	 * @param typeId 词条编号
	 * @return 统计结果。分词条统计
	 */
	public List<Map<String, Object>> selectStatsCount(@Param("START_TIME") Date startTime,
			@Param("END_TIME") Date endTime, @Param("TYPE_ID") Integer typeId);
	/**直接获取指定时间内的正面信息数和负面信息数*/
	public List<Map<String, Object>> selectStatsCountAll(@Param("START_TIME") Date startTime, @Param("END_TIME") Date endTime);

}