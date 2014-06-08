/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.prediction.algorithm;

import java.util.List;

import org.tomstools.prediction.lottery.Lottery;
import org.tomstools.prediction.lottery.LotteryRecord;
import org.tomstools.prediction.lottery.PredictResult;

/**
 * 彩票预测模型算法
 * @author admin
 * @date 2014年6月6日 
 * @time 下午12:30:20
 * @version 1.0
 */
public abstract class LotteryPredictionModelAlgorithm {
    protected List<LotteryRecord> records;
    
    /**
     * 默认构造函数
     * @since 1.0
     */
    public LotteryPredictionModelAlgorithm() {
        super();
    }

    /**
     * 添加历史数据
     * @param record    历史数据
     * @since 1.0
     */
    public final void addDatas(List<LotteryRecord> records){
        this.records = records;
    }
    
    /**
     * 预测
     * @param lottery 彩票信息
     * @return 预测结果
     * @throws Exception 
     * @since 1.0
     */
    public abstract PredictResult predict(Lottery lottery) throws Exception;

}
