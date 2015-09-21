package org.tomstools.web.persistence;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * 业务管理数据
 * 
 * @author admin
 * @date 2015年9月14日
 * @time 上午10:50:51
 * @version 1.0
 */
public interface BusinessMgrMapper {

	// 模板管理
	public List<Map<String, Object>> selectTemplateList();

	public void deleteTemplate(@Param("ID") Integer id);

	public void saveTemplate(@Param("ID") Integer id,
			@Param("TEMPLATE_NAME") String TEMPLATE_NAME,
			@Param("TEMPLATE_CONTENT") String TEMPLATE_CONTENT,
			@Param("TEMPLATE_SCRIPT") String TEMPLATE_SCRIPT);

	public Integer selecTemplateIdByName(
			@Param("TEMPLATE_NAME") String TEMPLATE_NAME);

	public void addTemplate(@Param("TEMPLATE_NAME") String TEMPLATE_NAME,
			@Param("TEMPLATE_CONTENT") String TEMPLATE_CONTENT,
			@Param("TEMPLATE_SCRIPT") String TEMPLATE_SCRIPT);

	// 子指标管理
	public List<Map<String, Object>> selectSubmetricList();

	public void deleteSubmetric(@Param("ID") Integer id);

	public void saveSubmetric(@Param("ID") Integer id,
			@Param("SUB_WEB_METRIC_CODE") String SUB_WEB_METRIC_CODE,
			@Param("URL") String URL, @Param("URL_BAK") String URL_BAK,
			@Param("CONTENT_TYPE") String CONTENT_TYPE,
			@Param("PAGE_ENCODING") String PAGE_ENCODING);

	public Integer selecSubmetricIdByName(String SUB_WEB_METRIC_CODE);

	public void addSubmetric(
			@Param("SUB_WEB_METRIC_CODE") String SUB_WEB_METRIC_CODE,
			@Param("URL") String URL, @Param("URL_BAK") String URL_BAK,
			@Param("CONTENT_TYPE") String CONTENT_TYPE,
			@Param("PAGE_ENCODING") String PAGE_ENCODING);

	// 子指标明细
	public List<Map<String, Object>> selectSubmetricDetailList(
			@Param("ID") Integer id);

	public void deleteAllSubmetricDetail(@Param("ID") Integer id);

	public void deleteSubmetricDetail(@Param("ID") Integer id);

	public void saveSubmetricDetail(@Param("ID") Integer id,
			@Param("METRIC_TITLE") String METRIC_TITLE,
			@Param("METRIC_SELECTOR") String METRIC_SELECTOR,
			@Param("VALUE_TYPE") String VALUE_TYPE,
			@Param("ORDER_NUM") String ORDER_NUM);

	public void addSubmetricDetail(@Param("ID") Integer id,
			@Param("METRIC_TITLE") String METRIC_TITLE,
			@Param("METRIC_SELECTOR") String METRIC_SELECTOR,
			@Param("VALUE_TYPE") String VALUE_TYPE,
			@Param("ORDER_NUM") String ORDER_NUM);

	public List<Map<String, Object>> selectMetricSubsist();

	public List<Map<String, Object>> selectMetricTemplatesist();
	
	public List<Map<String, Object>> selectMetricList();

	public void deleteMetric(@Param("ID") Integer id);

	public Integer selecMetricIdByName(@Param("ID") Integer id, @Param("WEB_METRIC_CODE") String WEB_METRIC_CODE);

	public void saveMetric(@Param("ID") Integer id,@Param("WEB_METRIC_CODE") String WEB_METRIC_CODE, @Param("WEB_METRIC_TITLE") String WEB_METRIC_TITLE) ;

	public void addMetric(@Param("WEB_METRIC_CODE") String WEB_METRIC_CODE, @Param("WEB_METRIC_TITLE") String WEB_METRIC_TITLE);

	public void deletesAllRelSubs(@Param("ID") String id);

	public void saveRelSub(@Param("ID") String id, @Param("SUB_ID") int subId);

	public void deletesAllRelTemplates(@Param("ID") String id);

	public void saveRelTemplate(@Param("ID") String id, @Param("SUB_ID") int subId);
}
