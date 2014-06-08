package org.tomstools.prediction;


import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.tomstools.prediction.algorithm.SimpleLotteryPredictionModelAlgorithm;
import org.tomstools.prediction.lottery.LotteryRecord;
import org.tomstools.prediction.lottery.LotteryRecordCheckResult;
import org.tomstools.prediction.lottery.PredictResult;
import org.tomstools.prediction.lottery.impl.ShuangSeQiu;

public class LotteryPredictionTest extends TestCase {

    private LotteryPrediction pred;
    private ShuangSeQiu ssq;

    public LotteryPredictionTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
     // 彩票预测器
        pred = new LotteryPrediction();
        pred.setModelAlgorithm(new SimpleLotteryPredictionModelAlgorithm());
        ssq = new ShuangSeQiu();
        pred.setLottery(ssq);
        List<LotteryRecord> lotteryRecords = new ArrayList<>();
        lotteryRecords.add(new LotteryRecord(2014063,"2014-06-05",ssq).addNumber(3).addNumber(8).addNumber(17).addNumber(21).addNumber(22).addNumber(31).addNumber(16));
        lotteryRecords.add(new LotteryRecord(2014062,"2014-06-03",ssq).addNumber(6).addNumber(9).addNumber(15).addNumber(22).addNumber(25).addNumber(26).addNumber(9));
        lotteryRecords.add(new LotteryRecord(2014061,"2014-06-01",ssq).addNumber(6).addNumber(9).addNumber(15).addNumber(22).addNumber(25).addNumber(26).addNumber(9));
        // 添加彩票记录
        //pred.addLotteryRecords(lotteryRecords);
        ssq.initRecords(lotteryRecords);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testPrediction() throws Exception {
        // 开始预测
        long start = 0;
        long end = 0;
        PredictResult result = pred.predict(start,end);
        assertNotNull(result);
        // 打印预测结果
        System.out.println("预测结果：" + result);
        
        // 已知开奖结果，与预测结果进行对比
        LotteryRecord lotteryRecord = new LotteryRecord(2014063,"2014-06-07",ssq).addNumber(3).addNumber(11).addNumber(17).addNumber(21).addNumber(22).addNumber(31).addNumber(16);
        LotteryRecordCheckResult checkResult = pred.check(start,end,lotteryRecord);
        System.out.println("评估结果：" + checkResult);
    }
}
