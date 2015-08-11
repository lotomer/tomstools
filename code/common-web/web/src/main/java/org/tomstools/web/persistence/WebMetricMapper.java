package org.tomstools.web.persistence;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.tomstools.web.model.WebMetric;
import org.tomstools.web.model.WebMetricInfo;
import org.tomstools.web.model.WebMetricSubInfo;

/**
 * WEB指标数据
 * 
 * @author admin
 * @date 2015年7月23日
 * @time 上午10:55:51
 * @version 1.0
 */
public interface WebMetricMapper {

	/**
	 * 根据指标名称获取WEB指标配置信息
	 * 
	 * @param metricName
	 *            WEB指标名称
	 * @return WEB指标配置信息
	 * @since 1.0
	 */
	// @Select("SELECT WEB_METRIC_ID id,WEB_METRIC_CODE name,WEB_METRIC_TITLE title FROM T_METRIC_WEB WHERE WEB_METRIC_CODE=#{metricName} AND IS_VALID='1'")
	public WebMetricInfo selectWebMetric(@Param("metricName") String metricName);

	//@Select("SELECT s.SUB_WEB_METRIC_ID id,SUB_WEB_METRIC_CODE code, PAGE_ENCODING pageEncoding,url,URL_BAK urlBak,CONTENT_TYPE contentType FROM T_METRIC_WEB_SUB s LEFT JOIN T_REL_WEB_METRIC_SUB rs ON s.SUB_WEB_METRIC_ID=rs.SUB_WEB_METRIC_ID WHERE rs.WEB_METRIC_ID=#{metricId} AND IS_VALID='1'")
	public List<WebMetricSubInfo> selectWebMetricSub(@Param("metricId") int metricId);
	/**
	 * 
	 * 根据指标编号获取WEB指标子配置明细
	 * 
	 * @param subMetricId
	 *            WEB指标子编号
	 * @return WEB指标子配置明细
	 * @since 1.0
	 */
	// @Select("SELECT METRIC_TITLE title,METRIC_SELECTOR selector,VALUE_TYPE value,ORDER_NUM orderNum FROM T_METRIC_WEB_SUB_DETAIL WHERE SUB_WEB_METRIC_ID=#{subMetricId} AND IS_VALID='1'")
	public List<WebMetric> selectWebMetricSubDetail(@Param("subMetricId") int subMetricId);

	/**
	 * 根据指标编号获取展现模板
	 * @param metricId 指标编号
	 * @return 展现模板
	 */
	//@Select("SELECT m.TEMPLATE_CONTENT templateContent,m.TEMPLATE_SCRIPT templateScript FROM T_REL_METRIC_TEMPLATE r  LEFT JOIN T_WEB_METRIC_TEMPLATE m ON (r.TEMPLATE_ID=m.TEMPLATE_ID) WHERE WEB_METRIC_ID=#{metricId} AND r.IS_VALID='1' AND m.IS_VALID='1'")
    public WebMetricInfo selectWebMetricTemplate(@Param("metricId") int metricId);
 }
