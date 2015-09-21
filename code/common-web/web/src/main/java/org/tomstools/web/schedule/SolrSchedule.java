/**
 * 
 */
package org.tomstools.web.schedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.tomstools.web.service.SolrService;

/**
 * @author Administrator
 *
 */
public class SolrSchedule {
	private static final Log LOG = LogFactory.getLog(SolrSchedule.class);
	@Autowired
	SolrService solrService;
	/**
	 * 统计舆论词
	 */
	//@Scheduled(cron="* * 1/8 * * ?")
	public void statWords(){
		LOG.info("【统计舆论词条】调度任务开始执行...");
		try {
			//LOG.info("【统计舆论词条】调度任务执行完毕。" + solrService.statWords());
		} catch (Exception e) {
			LOG.error("【统计舆论词条】调度任务执行失败：" + e.getMessage(),e);
		}
	}
	
	public void statWordsWithHost(){
		LOG.info("【统计网站舆论词条】调度任务开始执行...");
		try {
			LOG.info("【统计网站舆论词条】调度任务执行完毕。" + solrService.statWordsWithHost());
		} catch (Exception e) {
			LOG.error("【统计网站舆论词条】调度任务执行失败：" + e.getMessage(),e);
		}
	}
	
	public void alert(){
		LOG.info("【预警】调度任务开始执行...");
		try {
			LOG.info("【预警】调度任务执行完毕。" + solrService.alert());
		} catch (Exception e) {
			LOG.error("【预警】调度任务执行失败：" + e.getMessage(),e);
		}
	}
	
	public void alertProcess(){
		LOG.info("【预警处理】调度任务开始执行...");
		try {
			LOG.info("【预警处理】调度任务执行完毕。" + solrService.alertProcess());
		} catch (Exception e) {
			LOG.error("【预警处理】调度任务执行失败：" + e.getMessage(),e);
		}
	}
	

	public void generateHot(){
		LOG.info("【热点生成】调度任务开始执行...");
		try {
			LOG.info("【热点生成】调度任务执行完毕。" + solrService.generateHot());
		} catch (Exception e) {
			LOG.error("【热点生成】调度任务执行失败：" + e.getMessage(),e);
		}
	}
}
