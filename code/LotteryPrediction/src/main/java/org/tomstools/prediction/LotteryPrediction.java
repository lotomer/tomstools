/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.prediction;

import java.util.ArrayList;
import java.util.List;

import org.tomstools.prediction.algorithm.LotteryPredictionModelAlgorithm;
import org.tomstools.prediction.common.Utils;
import org.tomstools.prediction.lottery.Lottery;
import org.tomstools.prediction.lottery.LotteryRecord;
import org.tomstools.prediction.lottery.LotteryRecordCheckResult;
import org.tomstools.prediction.lottery.PredictResult;

/**
 * 定向爬虫
 * 
 * @author admin
 * @date 2014年3月12日
 * @time 下午7:18:51
 * @version 1.0
 */
public class LotteryPrediction  {
    //private final static Logger LOGGER = Logger.getLogger(LotteryPrediction.class);
    private Lottery lottery;
    private LotteryPredictionModelAlgorithm modelAlgorithm; // 模型算法
    /**
     * @param modelAlgorithm 设置 预测模型算法
     * @since 1.0
     */
    public final void setModelAlgorithm(LotteryPredictionModelAlgorithm modelAlgorithm) {
        this.modelAlgorithm = modelAlgorithm;
    }

    /**
     * @param lottery 设置 彩票
     * @since 1.0
     */
    public final void setLottery(Lottery lottery) {
        this.lottery = lottery;
    }

    /**
     * 添加历史开奖记录
     * @param lotteryRecords 开奖记录。
     * @since 1.0
     */
//    public void addLotteryRecords(List<LotteryRecord> lotteryRecords) {
//        if (!Utils.isEmpty(lotteryRecords) && null != lottery){
//            lottery.addRecords(lotteryRecords);
//        }
//    }

    /**
     * 根据指定的历史记录进行预测
     * @param start 历史记录开始期数。小于1表示从历史记录中最小期数开始
     * @param end   历史记录结束期数。小于1表示到历史记录中最新期数结束
     * @return 预测结果。可能为null
     * @throws Exception 
     * @since 1.0
     */
    public PredictResult predict(long start, long end) throws Exception {
        if (!Utils.isEmpty(lottery.getLotteryRecords())){
            if (1 < start && 1 < end && end < start){
                long t = end;
                end = start;
                start = t;
            }
            List<LotteryRecord> datas = new ArrayList<>();
            for (LotteryRecord record : lottery.getLotteryRecords()) {
                if ((start < 1 || start <= record.getQishu()) && (end < 1 || record.getQishu() <= end)){
                    datas.add(record);
                }
            }
            
            modelAlgorithm.addDatas(datas);
            return modelAlgorithm.predict(lottery);
        }else{
            return null;
        }
    }

    /**
     * 根据预测结果对开奖结果进行评估
     * @param start 历史记录开始期数。小于1表示从历史记录中最小期数开始
     * @param end   历史记录结束期数。小于1表示到历史记录中最新期数结束
     * @param lotteryRecord 待评估的开奖结果
     * @return 评估结果。可能为null
     * @throws Exception 
     * @since 1.0
     */
    public LotteryRecordCheckResult check(long start, long end, LotteryRecord lotteryRecord) throws Exception {
        // 首先获取预测结果
        PredictResult result = predict(start, end);
        
        // 再根据预测结果对开奖结果进行评估
        if (null != result){
            return result.check(lotteryRecord);
        }else{
            return null;
        }
    }
    
}
