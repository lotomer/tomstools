package org.tomstools.web.persistence;

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
	//public Date selectLastStatTime(@Param("TYPE_ID") Integer typeId);

	/**
	 * 将统计结果保存
	 * 
	 * @param typeId
	 *            词条编号
	 * @param countZM
	 *            正面信息数
	 * @param countFM
	 *            负面信息数
	 * @param countFM_E 英文正面信息数
	 * @param countZM_E 英文负面信息数
	 * @param statTime
	 *            统计时间
	 */
//	public void saveStat(@Param("TYPE_ID") Integer typeId, @Param("SIZE_ZM") long countZM,
//			@Param("SIZE_FM") long countFM, @Param("SIZE_ZM_E")  long countZM_E, @Param("SIZE_FM_E") long countFM_E, @Param("STAT_TIME") Date statTime);


	/** 获取语言列表 */
	public List<Map<String, Object>> selectLanguage();

	/** 获取国家 */
	public List<Map<String, Object>> selectCountry();

	/** 获取站点类型列表 
	 * @param isValid 是否有效。1 有效；0 无效。如果为null，则默认所有*/
	public List<Map<String, Object>> selectSiteType(@Param("IS_VALID") String isValid);

	/** 根据站点类型编号获取站点列表。如果站点类型编号为null，则获取所有站点列表 */
	public List<Map<String, Object>> selectSite(@Param("SITE_TYPE_ID") Integer siteTypeId,@Param("IS_VALID") String isValid);
}