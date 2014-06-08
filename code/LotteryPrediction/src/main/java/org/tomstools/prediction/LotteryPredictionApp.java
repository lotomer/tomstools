/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.prediction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.tomstools.prediction.algorithm.SimpleLotteryPredictionModelAlgorithm;
import org.tomstools.prediction.lottery.Lottery;
import org.tomstools.prediction.lottery.LotteryRecord;
import org.tomstools.prediction.lottery.LotteryRecordCheckResult;
import org.tomstools.prediction.lottery.PredictResult;
import org.tomstools.prediction.lottery.impl.ShuangSeQiu;

/**
 * 
 * @author admin
 * @date 2014年6月8日 
 * @time 上午8:18:06
 * @version 1.0
 */
public class LotteryPredictionApp {

    /**
     * @param args
     * @throws Exception 
     * @since 1.0
     */
    public static void main(String[] args) throws Exception {
        // 加载所有历史开奖记录
        String filePath = "E:\\work\\lecai-shuangseqiu\\lecai-shuangseqiu_20140608123614.csv";
        BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)));
        String line = null;
        Lottery lottery = new ShuangSeQiu();
        List<LotteryRecord> records = new ArrayList<>();
        while(null != (line = reader.readLine())){
            String[] values = line.split(",");
            if (3 < values.length){
                LotteryRecord record = new LotteryRecord(Long.valueOf(values[1]), values[0], lottery );
                for (int i = 2; i < values.length; i++) {
                    record.addNumber(Integer.valueOf(values[i]));
                }
                
                records.add(record);
            }else{
                System.err.println("Invalid value: " + line);
            }
        }
        
        reader.close();
        lottery.initRecords(records);
        
        // 根据期数进行升序排序
        Collections.sort(records, new Comparator<LotteryRecord>() {
            @Override
            public int compare(LotteryRecord o1, LotteryRecord o2) {
                return Long.compare(o1.getQishu(), o2.getQishu());
            }
        });
        
        //System.out.println(records);
        
        // 开始对每期数据进行预测和评估，以校验算法有效性
        LotteryPrediction prediction = new LotteryPrediction();
        prediction.setLottery(lottery);
        prediction.setModelAlgorithm(new SimpleLotteryPredictionModelAlgorithm());
        // 评测开始期数
        long start = 2013001;
        LotteryRecord record = new LotteryRecord(start, "", lottery);
        int index = Collections.binarySearch(records, record, new Comparator<LotteryRecord>() {
            @Override
            public int compare(LotteryRecord o1, LotteryRecord o2) {
                // 升序
                return Long.compare(o1.getQishu(), o2.getQishu());
            }
        });
        // 如果没有找到，则从第二期的开始
        index = index < 1 ? 1 : index;
        long startQishu = records.get(0).getQishu();
        for (int i = index; i < records.size(); i++) {
            record = records.get(i);
            // 开始预测
            PredictResult predictResult = prediction.predict(startQishu, records.get(i - 1).getQishu());
            // 开始评估
            LotteryRecordCheckResult checkResult = prediction.check(startQishu, records.get(i - 1).getQishu(), record);
            // 输出结果
            printResult(record,predictResult,checkResult);
        }
    }

    private static void printResult(LotteryRecord record, PredictResult predictResult,
            LotteryRecordCheckResult checkResult) {
        StringBuilder msg = new StringBuilder();
        msg.append(record.getQishu()).append("\t");
        while(checkResult.hasNext()){
            msg.append(checkResult.next().rank).append(",");
        }
        System.out.println(msg);
    }
}
