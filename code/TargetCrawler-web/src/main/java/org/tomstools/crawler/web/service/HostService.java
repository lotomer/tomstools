/**
 * copyright (a) 2010-2015 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.web.service;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tomstools.crawler.web.model.Detail;
import org.tomstools.crawler.web.model.Status;
import org.tomstools.crawler.web.persistence.DetailDao;
import org.tomstools.crawler.web.persistence.StatusDao;

/**
 * 
 * @author admin
 * @date 2015年5月16日
 * @time 上午2:15:33
 * @version 1.0
 */
@Service("hostService")
@Transactional
public class HostService {
    @Autowired
    private StatusDao<Status> statusDao;
    @Autowired
    private DetailDao<Detail> detailDao;

    /**
     * 获取爬取状态信息列表
     * @param   oldStatus 旧状态。null表示不指定
     * @return 爬取状态信息列表
     * @since 1.0
     */
    public List<Status> selectStatus(String oldStatus) {
        return statusDao.select(oldStatus);
    }
    /**
     * 获取指定时间范围的详细数据
     * 
     * @param startTime 开始时间字符串。格式yyyyMMddHHmmss。null表示不限制
     * @param endTime 结束时间字符串。格式yyyyMMddHHmmss。null表示不限制
     * @param rowBounds 获取的记录数信息
     * @return 详细数据列表。不会返回null
     * @since 1.0
     */
    public List<Detail> selectAllDetail(String startTime,String endTime, RowBounds rowBounds) {
        return detailDao.select(null, startTime,endTime, rowBounds);
    }/**
     * 获取指定时间范围的详细数据
     * 
     * @param startTime 开始时间字符串。格式yyyyMMddHHmmss。null表示不限制
     * @param endTime 结束时间字符串。格式yyyyMMddHHmmss。null表示不限制
     * @return 详细数据总数
     * @since 1.0
     */
    public int countAllDetail(String startTime,String endTime) {
        return detailDao.count(null, startTime,endTime);
    }
    /**
     * 根据爬取编号获取指定时间范围的详细数据
     * 
     * @param crawlId 爬取编号。
     * @param startTime 开始时间字符串。格式yyyyMMddHHmmss。null表示不限制
     * @param endTime 结束时间字符串。格式yyyyMMddHHmmss。null表示不限制
     * @param rowBounds 获取的记录数信息
     * @return 详细数据列表。不会返回null
     * @since 1.0
     */
    public List<Detail> selectDetail(int crawlId,String startTime,String endTime, RowBounds rowBounds) {
        return detailDao.select(crawlId, startTime,endTime, rowBounds);
    }/**
     * 根据爬取编号获取指定时间范围的数据总数
     * 
     * @param crawlId 爬取编号。
     * @param startTime 开始时间字符串。格式yyyyMMddHHmmss。null表示不限制
     * @param endTime 结束时间字符串。格式yyyyMMddHHmmss。null表示不限制
     * @return 详细数据总数
     * @since 1.0
     */
    public int countDetail(int crawlId,String startTime,String endTime) {
        return detailDao.count(crawlId, startTime,endTime);
    }
}
