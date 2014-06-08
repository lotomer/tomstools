/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.prediction.lottery;

import java.util.Arrays;

/**
 * 开奖记录测试结果
 * @author admin
 * @date 2014年6月8日 
 * @time 上午12:33:01
 * @version 1.0
 */
public class LotteryRecordCheckResult implements java.util.Iterator<RecordNumber> {
    //private LotteryRecord lotteryRecord;   // 开奖结果
    private int[] numbers;    // 名次
    private float[] probabilities; // 概率
    private int[] ranks;    // 名次
    private int index;
    /**
     * @param lotteryRecord
     * @param probabilities
     * @param ranks
     * @since 1.0
     */
    public LotteryRecordCheckResult(LotteryRecord lotteryRecord, float[] probabilities, int[] ranks) {
        super();
        //this.lotteryRecord = lotteryRecord;
        this.numbers = lotteryRecord.getNumbers();
        this.probabilities = probabilities;
        this.ranks = ranks;
        index = 0;
    }
    /*
     * @since 1.0
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LotteryRecordTestResult [numbers=").append(Arrays.toString(numbers))
                .append(", probabilities=").append(Arrays.toString(probabilities))
                .append(", ranks=").append(Arrays.toString(ranks)).append("]");
        return builder.toString();
    }
    

    @Override
    public boolean hasNext() {
        return index < this.numbers.length;
    }
    @Override
    public RecordNumber next() {
        RecordNumber num = new RecordNumber(this.numbers[index],this.ranks[index],this.probabilities[index]);
        ++index;
        return num;
    }
    @Override
    public void remove() {
        throw new RuntimeException("Not yet implementation!");
    }
}
