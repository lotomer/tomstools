/**
 * copyright (a) 2010-2015 tomstools.org. All rights reserved.
 */
package org.tomstools.web.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.tomstools.web.persistence.BusinessMgrMapper;

/**
 * 业务管理服务
 * 
 * @author admin
 * @date 2015年9月14日
 * @time 上午10:49:30
 * @version 1.0
 */
@Service("businessMgrService")
@Transactional()
public class BusinessMgrService {
	// private final static Log LOG =
	// LogFactory.getLog(BusinessMgrService.class);
	@Autowired
	private BusinessMgrMapper businessMgrMapper;

	public List<Map<String, Object>> selectTemplateList() {
		return businessMgrMapper.selectTemplateList();
	}

	public void deleteTemplate(Integer id) {
		businessMgrMapper.deleteTemplate(id);
	}

	public void saveTemplate(Integer id, String tEMPLATE_NAME,
			String tEMPLATE_CONTENT, String tEMPLATE_SCRIPT) {
		businessMgrMapper.saveTemplate(id, tEMPLATE_NAME, tEMPLATE_CONTENT,
				tEMPLATE_SCRIPT);
	}

	public Integer selecTemplateIdByName(String tEMPLATE_NAME) {
		return businessMgrMapper.selecTemplateIdByName(tEMPLATE_NAME);
	}

	public void addTemplate(String tEMPLATE_NAME, String tEMPLATE_CONTENT,
			String tEMPLATE_SCRIPT) {
		businessMgrMapper.addTemplate(tEMPLATE_NAME, tEMPLATE_CONTENT,
				tEMPLATE_SCRIPT);
	}

	public List<Map<String, Object>> selectSubmetricList() {
		return businessMgrMapper.selectSubmetricList();
	}

	public void deleteSubmetric(Integer id) {
		businessMgrMapper.deleteSubmetric(id);
	}

	public void saveSubmetric(Integer id, String SUB_WEB_METRIC_CODE,
			String URL, String URL_BAK, String CONTENT_TYPE,
			String PAGE_ENCODING) {
		businessMgrMapper.saveSubmetric(id, SUB_WEB_METRIC_CODE, URL, URL_BAK,
				CONTENT_TYPE, PAGE_ENCODING);
	}

	public Integer selecSubmetricIdByName(String SUB_WEB_METRIC_CODE) {
		return businessMgrMapper.selecSubmetricIdByName(SUB_WEB_METRIC_CODE);
	}

	public void addSubmetric(String SUB_WEB_METRIC_CODE, String URL,
			String URL_BAK, String CONTENT_TYPE, String PAGE_ENCODING) {
		businessMgrMapper.addSubmetric(SUB_WEB_METRIC_CODE, URL, URL_BAK,
				CONTENT_TYPE, PAGE_ENCODING);
	}

	public List<Map<String, Object>> selectSubmetricDetailList(Integer id) {
		return businessMgrMapper.selectSubmetricDetailList(id);
	}

	public void deleteAllSubmetricDetail(Integer id) {
		businessMgrMapper.deleteAllSubmetricDetail(id);
	}

	public void deleteSubmetricDetail(Integer id) {
		businessMgrMapper.deleteSubmetricDetail(id);
	}

	public void saveSubmetricDetail(Integer id, String METRIC_TITLE,
			String METRIC_SELECTOR, String VALUE_TYPE, String ORDER_NUM) {
		businessMgrMapper.saveSubmetricDetail(id, METRIC_TITLE,
				METRIC_SELECTOR, VALUE_TYPE, ORDER_NUM);
	}

	public void addSubmetricDetail(Integer id, String METRIC_TITLE,
			String METRIC_SELECTOR, String VALUE_TYPE, String ORDER_NUM) {
		businessMgrMapper.addSubmetricDetail(id, METRIC_TITLE, METRIC_SELECTOR,
				VALUE_TYPE, ORDER_NUM);
	}

	public List<Map<String, Object>> selectMetricSubsist() {
		return businessMgrMapper.selectMetricSubsist();
	}

	public List<Map<String, Object>> selectMetricTemplatesist() {
		return businessMgrMapper.selectMetricTemplatesist();
	}

	public List<Map<String, Object>> selectMetricList() {
		return businessMgrMapper.selectMetricList();
	}

	public void deleteMetric(Integer id) {
		// 需要删除所有对应的关联数据
		businessMgrMapper.deletesAllRelSubs(String.valueOf(id));
		businessMgrMapper.deletesAllRelTemplates(String.valueOf(id));
		businessMgrMapper.deleteMetric(id);
	}

	public Integer selecMetricIdByName(Integer id, String WEB_METRIC_CODE) {
		return businessMgrMapper.selecMetricIdByName(id,WEB_METRIC_CODE);
	}

	public void saveMetric(Integer id, String WEB_METRIC_CODE,
			String WEB_METRIC_TITLE) {
		businessMgrMapper.saveMetric(id, WEB_METRIC_CODE, WEB_METRIC_TITLE);
	}

	public void addMetric(String WEB_METRIC_CODE, String WEB_METRIC_TITLE) {
		businessMgrMapper.addMetric(WEB_METRIC_CODE, WEB_METRIC_TITLE);
	}

	public void saveRelSubs(String id, String relIds) {
		// 首先清除原有角色的关联数据
		businessMgrMapper.deletesAllRelSubs(id);
		if (!StringUtils.isEmpty(relIds)) {
			String[] ids = relIds.split(",");
			for (int i = 0; i < ids.length; i++) {
				if (!StringUtils.isEmpty(ids[i])) {
					businessMgrMapper.saveRelSub(id, Integer.parseInt(ids[i]));
				}
			}
		}
	}

	public void saveRelTemplates(String id, String relIds) {
		// 首先清除原有角色的关联数据
		businessMgrMapper.deletesAllRelTemplates(id);
		if (!StringUtils.isEmpty(relIds)) {
			String[] ids = relIds.split(",");
			for (int i = 0; i < ids.length; i++) {
				if (!StringUtils.isEmpty(ids[i])) {
					businessMgrMapper.saveRelTemplate(id,
							Integer.parseInt(ids[i]));
				}
			}
		}
	}
}
