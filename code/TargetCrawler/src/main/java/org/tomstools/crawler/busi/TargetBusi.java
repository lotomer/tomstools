/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.busi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.tomstools.crawler.common.Logger;
import org.tomstools.crawler.common.Utils;
import org.tomstools.crawler.config.Target;
import org.tomstools.crawler.dao.ResultDAO;

/**
 * 
 * @author admin
 * @date 2014年3月16日
 * @time 上午10:11:53
 * @version 1.0
 */
public class TargetBusi {
    private static final Logger LOGGER = Logger.getLogger(TargetBusi.class);
    private Target target;
    private ResultDAO resultDAO;
    private int batchCount; // 当前批次计数
    private int batchSize; // 当前批次容量
    private int interval; // 当前批次完成后的间隔时间。单位：毫秒
    private int totalPages; // 处理过的总页面数
    private int maxSize; // 能够处理的最大页面数。小于等于0表示不限制
    private int recordCount; // 处理的数据计数
    private String latestFlag; // 处理过的数据的最新标识
    private Collection<String> topDatas; // 置顶数据
    private Pattern topDataPattern;// 置顶数据的规则
    private List<String> newFlagDatas;
    private boolean isDown;

    /**
     * @param target
     * @param resultDAO
     * @since 1.0
     */
    public TargetBusi(Target target, ResultDAO resultDAO) {
        super();
        this.target = target;
        this.resultDAO = resultDAO;
        newFlagDatas = new ArrayList<String>();
        topDatas = new HashSet<String>();
        // 第一个元素是占位数据，给后续最新标识使用
        newFlagDatas.add(null);
        batchSize = target.getCrawlingRule().getBatchSizeInit();
        interval = target.getCrawlingRule().getBatchIntervalInit();
        maxSize = target.getCrawlingRule().getMaxSize();
        // 匹配前置数据的正则表达式
        if (!Utils.isEmpty(target.getRegex4topDataFalg())) {
            topDataPattern = Pattern.compile(target.getRegex4topDataFalg(),
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        }
    }

    /** 抓取页面计数加1 */
    public void incFetchPageCount() {
        ++batchCount;
        ++totalPages;
    }

    /** 保存记录数加1 */
    public void incRecordCount() {
        ++recordCount;
    }

    /** 获取记录数 */
    public int getRecordCount() {
        return recordCount;
    }

    /**
     * @return 返回 target
     * @since 1.0
     */
    public final Target getTarget() {
        return target;
    }

    /**
     * 判断是否有效
     * 
     * @return true 有效，可以继续；false 无效，不再继续
     * @since 1.0
     */
    public boolean checkBatchInfo() {
        // 判断是否已经达到最大数目，如果已经达到最大数目，则不再继续处理
        if (0 != maxSize && maxSize <= totalPages) {
            isDown = true;
            return false;
        }
        if (batchSize < batchCount) {
            // 这一批爬取完毕，则休息指定时间
            try {
                LOGGER.info("will sleep " + interval + "ms.");
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
            // 清空批次计数，并重新设置批次大小和间隔时长
            batchCount = 0;
            if (interval == target.getCrawlingRule().getBatchIntervalFinal()){
             // do nonthing
            } else if (interval < target.getCrawlingRule().getBatchIntervalFinal()){
                interval = interval + target.getCrawlingRule().getBatchIntervalStep();
                interval = interval > target.getCrawlingRule().getBatchIntervalFinal() ? target
                        .getCrawlingRule().getBatchIntervalFinal() : interval;
            }else{
                interval = interval - target.getCrawlingRule().getBatchIntervalStep();
                interval = interval < target.getCrawlingRule().getBatchIntervalFinal() ? target
                        .getCrawlingRule().getBatchIntervalFinal() : interval;
            }
            
            if (batchSize == target.getCrawlingRule().getBatchSizeFinal()){
                // do nonthing
            }else if (batchSize < target.getCrawlingRule().getBatchSizeFinal()){
                batchSize = batchSize + target.getCrawlingRule().getBatchSizeStep();
                batchSize = batchSize > target.getCrawlingRule().getBatchSizeFinal() ? target
                        .getCrawlingRule().getBatchSizeFinal() : batchSize;
            }else{
                batchSize = batchSize - target.getCrawlingRule().getBatchSizeStep();
                batchSize = batchSize < target.getCrawlingRule().getBatchSizeFinal() ? target
                        .getCrawlingRule().getBatchSizeFinal() : batchSize;
            }
        }
        return true;
    }

    /**
     * 判断数据是否是置顶数据
     * 
     * @param data 待判断是数据
     * @return true 是前置数据；false 不是前置数据
     * @since 1.0
     */
    private boolean isTopData(String data) {
        // 如果配置了置顶标识，并且与置顶标识匹配，则表示是置顶数据
        if (null != topDataPattern && topDataPattern.matcher(data).find()) {
            // 保存置顶数据
            newFlagDatas.add(data);
            return true;
        }
        return false;
    }

    /**
     * 准备开始定向爬取
     * 
     * @return true 准备就绪；false 未准备就绪，不能继续
     * @since 1.0
     */
    public boolean prepare() {
        LOGGER.info("prepare crawle: " + target);
        try {
            Collection<String> flags = resultDAO.prepare(target);
            for (String flag : flags) {
                // 第一个标识是最后处理过的标识，其他都是置顶数据
                if (null == latestFlag) {
                    latestFlag = flag;
                    LOGGER.warn("The old flag is: " + latestFlag);
                } else {
                    // 保存置顶数据
                    topDatas.add(flag);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            isDown = true;
            return false;
        }

        isDown = false;
        return true;
    }

    /**
     * 定向爬取完毕，进行收尾工作，如保存状态数据等
     * 
     * @since 1.0
     */
    public void finish() {
        LOGGER.info("finish. " + toString());
        LOGGER.info("last save: " + newFlagDatas);
        if (!Utils.isEmpty(newFlagDatas)){
            // 标识不为空才更新
            this.resultDAO.finish(target, newFlagDatas);
            // 结束之前需要保存 处理过的数据标识。已融合到finish中
            // resultDAO.saveProcessedFlagDatas(target, newFlagDatas);
        }
        isDown = true;
    }

    /**
     * 根据当前处理的数据判断是否即将结束。<br>
     * 如果需要中断，则会保存处理过的数据标识
     * 
     * @param data
     * @return true 需要结束之后的处理；false 还未结束，可以继续后续的处理
     * @since 1.0
     */
    public boolean willFinish(String data) {
        if (isFinished()) {
            return true;
        }
        // 需要终止处理的条件：在保存的处理标识中存在，并且不是“置顶”数据（top data）
        if (!Utils.isEmpty(latestFlag) && latestFlag.equals(data) && !topDatas.contains(data)) {
            LOGGER.warn("will finish. The lastest data is " + data);
            if (null == newFlagDatas.get(0)){
                newFlagDatas.set(0, data);
            }
            isDown = true;
            return true;
        }

        // 保存新的处理标识。不能是置顶数据
        if (null == newFlagDatas.get(0) && !isTopData(data)) {
            newFlagDatas.set(0, data);
            LOGGER.info("middle save: "+newFlagDatas);
            resultDAO.saveProcessedFlagDatas(target, newFlagDatas);
        }
        
        return false;
    }

    /**
     * 保存记录
     * 
     * @param subUrl 记录所在页面的url
     * @param record 记录内容
     * @since 1.0
     */
    public void saveRecord(String subUrl, Map<String, String> record) {
        this.resultDAO.save(target, subUrl, record);
    }

    /**
     * 判断定向爬取是否已经结束
     * 
     * @return true 已经结束；false 未结束
     * @since 1.0
     */
    public boolean isFinished() {
        return isDown;
    }

    /*
     * @since 1.0
     */
    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append("{target=").append(target).append(", batchCount=").append(batchCount)
                .append(", batchSize=").append(batchSize).append(", interval=").append(interval)
                .append(", totalPages=").append(totalPages).append(", maxSize=").append(maxSize)
                .append(", recordCount=").append(recordCount).append("}");

        return msg.toString();
    }

}
