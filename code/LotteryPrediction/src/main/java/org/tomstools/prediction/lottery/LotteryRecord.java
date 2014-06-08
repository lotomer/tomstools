/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.prediction.lottery;

import java.util.Arrays;

import org.tomstools.prediction.common.Logger;

/**
 * 彩票记录
 * @author admin
 * @date 2014年6月6日 
 * @time 上午10:46:10
 * @version 1.0
 */
public class LotteryRecord {
    private final static Logger LOGGER = Logger.getLogger(LotteryRecord.class);
    private long qishu;
    private String date;
    private int[] numbers;
    private Lottery lottery;
    private int index;
    /**
     * @param qishu     开奖期数
     * @param date  开奖日期
     * @param numberCount 号码个数
     * @since 1.0
     */
    public LotteryRecord(long qishu, String date, Lottery lottery) {
        super();
        this.qishu = qishu;
        this.date = date;
        this.lottery = lottery;
        numbers = new int[lottery.getNumberCount()];
        index = 0;
    }

    /**
     * 获取开奖期数
     * @return 期数
     * @since 1.0
     */
    public long getQishu() {
        return qishu;
    }

    /**
     * 按顺序添加开奖号码
     * @param number 开奖号码
     * @return 自身
     * @since 1.0
     */
    public LotteryRecord addNumber(int number) {
        if (index < lottery.getNumberCount()){
            numbers[index++] = number;
        }else{
            LOGGER.warn("Enough numbers! " + lottery);
        }
        return this;
    }

    
    /**
     * @return 返回 号码球列表，注意保持顺序
     * @since 1.0
     */
    public final int[] getNumbers() {
        return numbers;
    }

    /**
     * @param numbers 设置 号码球列表，注意保持顺序
     * @since 1.0
     */
    public final void setNumbers(int[] numbers) {
        this.numbers = numbers;
    }

    /**
     * @return 返回 彩票信息
     * @since 1.0
     */
    public final Lottery getLottery() {
        return lottery;
    }

    /*
     * @since 1.0
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[").append(qishu).append(", ").append(date)
                .append(", ").append(Arrays.toString(numbers)).append(", ")
                .append(lottery);
        return builder.toString();
    }
}
