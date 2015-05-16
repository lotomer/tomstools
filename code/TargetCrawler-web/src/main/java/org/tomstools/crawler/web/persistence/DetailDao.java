package org.tomstools.crawler.web.persistence;

import java.util.List;

import org.apache.ibatis.session.RowBounds;

/**
 * 网站数据
 * 
 * @param <T>
 * @author admin
 * @date 2015年5月16日
 * @time 上午11:11:02
 * @version 1.0
 */
public interface DetailDao<T> {
    /**
     * 根据爬取编号获取指定时间范围的详细数据
     * 
     * @param crawlId 爬取编号。null表示不限制
     * @param startTime 开始时间字符串。格式yyyyMMddHHmmss。null表示不限制
     * @param endTime 结束时间字符串。格式yyyyMMddHHmmss。null表示不限制
     * @param rowBounds 获取的记录数信息
     * @return 详细数据列表。不能返回null
     * @since 1.0
     */
    public List<T> select(Integer crawlId, String startTime, String endTime, RowBounds rowBounds);
    /**
     * 根据爬取编号获取指定时间范围的数据总数
     * 
     * @param crawlId 爬取编号。null表示不限制
     * @param startTime 开始时间字符串。格式yyyyMMddHHmmss。null表示不限制
     * @param endTime 结束时间字符串。格式yyyyMMddHHmmss。null表示不限制
     * @param rowBounds 获取的记录数信息
     * @return 数据总数
     * @since 1.0
     */
    public int count(Integer crawlId, String startTime, String endTime);
}
