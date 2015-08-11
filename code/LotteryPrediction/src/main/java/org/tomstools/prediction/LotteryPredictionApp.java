/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.prediction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.tomstools.prediction.algorithm.SimpleLotteryPredictionModelAlgorithm;
import org.tomstools.prediction.lottery.Lottery;
import org.tomstools.prediction.lottery.LotteryRecord;
import org.tomstools.prediction.lottery.LotteryRecordCheckResult;
import org.tomstools.prediction.lottery.LotteryWinResult;
import org.tomstools.prediction.lottery.PredictResult;
import org.tomstools.prediction.lottery.RecordNumber;
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
    @SuppressWarnings("resource")
    public static void main(String[] args) throws Exception {
        String select4type = null;
        if (args.length < 1) {
            System.err.println("Invalid arguments!");
            System.out.println("args: filename [numberSelect4types]");
            System.out.println("    filename  开奖结果记录文件");
            System.out.println("    numberSelect4types 预测给每种球选取的个数，每种球用英文逗号分隔“,”。默认为标准开奖个数。可选");
            System.exit(-1);
        }
        if (2 < args.length) {
            select4type = args[1];
        }
        // 加载所有历史开奖记录
        String filePath = args[0];
        BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)));
        Lottery lottery = new ShuangSeQiu();
        int[] startIndex4types = lottery.getStartIndex4types();
        int[] numSelect4type;
        if (null != select4type) {
            String[] vs = select4type.split(",");
            if (vs.length != startIndex4types.length) {
                throw new Exception("Argument error: Not enough number selected for type! Expect "
                        + startIndex4types.length + ", but was " + vs.length);
            }
            numSelect4type = new int[startIndex4types.length];
            for (int i = 0; i < vs.length; i++) {
                numSelect4type[i] = Integer.valueOf(vs[i]);
            }
        } else {
            numSelect4type = lottery.getNumCount4types();
        }
        // 第一行是标题
        String line = reader.readLine();
        List<LotteryRecord> records = new ArrayList<>();
        while (null != (line = reader.readLine())) {
            String[] values = line.split(",");
            if (1 < values.length) {
                LotteryRecord record = new LotteryRecord(Long.valueOf(values[0]),
                        lottery);
                for (int i = 1; i < values.length; i++) {
                    record.addNumber(Integer.valueOf(values[i]));
                }

                records.add(record);
            } else {
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

        // System.out.println(records);

        // 开始对每期数据进行预测和评估，以校验算法有效性
        LotteryPrediction prediction = new LotteryPrediction();
        prediction.setLottery(lottery);
        prediction.setModelAlgorithm(new SimpleLotteryPredictionModelAlgorithm());
        // 评测开始期数
        long start = 0;
        LotteryRecord record = new LotteryRecord(start, lottery);
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
            PredictResult predictResult = prediction.predict(startQishu, records.get(i - 1)
                    .getQishu());
            // 开始评估
            LotteryRecordCheckResult checkResult = prediction.check(startQishu, records.get(i - 1)
                    .getQishu(), record);
            // 评估预测结果中奖情况
            LotteryWinResult winResult = prediction.testPrediction(startQishu, records.get(i - 1)
                    .getQishu(), record, numSelect4type);
            // 输出评估结果
            printResult(record, predictResult, checkResult,winResult);
        }
    }

    private static void printResult(LotteryRecord record, PredictResult predictResult,
            LotteryRecordCheckResult checkResult, LotteryWinResult winResult) {
        StringBuilder msg = new StringBuilder();
        msg.append(record.getQishu()).append("\t").append(Arrays.toString(record.getNumbers())).append("\t");
        boolean isFirst = true;
        while (checkResult.hasNext()) {
            if (!isFirst){
                msg.append(",");
            }else{
                isFirst = false;
            }
            RecordNumber num = checkResult.next();
            //msg.append(num.number).append(":").append(num.rank);
            msg.append(num.rank);
        }
        //msg.append("\t").append(winResult);
        System.out.println(msg);
    }
}
