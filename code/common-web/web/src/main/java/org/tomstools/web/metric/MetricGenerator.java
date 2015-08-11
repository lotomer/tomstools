/**
 * 
 */
package org.tomstools.web.metric;

import java.util.Map;

import org.tomstools.web.model.WebMetric;

/**
 * 指标生成器接口
 * @author lotomer
 *
 */
public interface MetricGenerator {
	/**
	 * 生成指标数据
	 * @return 指标数据。不为null
	 * @throws Exception
	 */
	public Map<String, WebMetric> generate() throws Exception;
}
