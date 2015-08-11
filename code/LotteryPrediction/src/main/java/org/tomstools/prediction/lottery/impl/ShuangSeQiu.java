/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.prediction.lottery.impl;

import java.util.Arrays;

import org.tomstools.prediction.lottery.Lottery;
import org.tomstools.prediction.lottery.LotteryRecord;
import org.tomstools.prediction.lottery.LotteryWinResult;

/**
 * 彩票：双色球
 * 
 * @author admin
 * @date 2014年6月6日
 * @time 上午11:32:07
 * @version 1.0
 */
public class ShuangSeQiu extends Lottery {
    private static final int[] maxValue4types = new int[] { 33, 16 }; // 各种球的最大值
    private static final int[] startIndex4types = new int[] { 0, 6 }; // 各种类分隔线的索引号
    private static final int numberCount = 7; // 每期开球个数

    @Override
    public final int[] getMaxValue4types() {
        return maxValue4types;
    }

    @Override
    public final int[] getStartIndex4types() {
        return startIndex4types;
    }

    public int getNumberCount() {
        return numberCount;
    }

    /*
     * @since 1.0
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ShuangSeQiu [numberCount=").append(numberCount).append(", maxValue4types=")
                .append(Arrays.toString(maxValue4types)).append(", startIndex4types=")
                .append(Arrays.toString(startIndex4types)).append("]");
        return builder.toString();
    }

    public int[] getNumCount4types(){
        int[] numCount4type = new int[startIndex4types.length];
        int index = 0;
        boolean isFinished = false;
        while (!isFinished) {
            int iStart = startIndex4types[index++];
            int iEnd;
            if (index < startIndex4types.length) {
                iEnd = startIndex4types[index];
            } else {
                iEnd = getNumberCount();
                isFinished = true;
            }

            numCount4type[index - 1] = iEnd - iStart;
        }
        
        return numCount4type;
    }
    @Override
    public LotteryWinResult checkWinner(int[][] predResult, LotteryRecord lotteryRecord) {
        if (predResult.length == startIndex4types.length){
            int[] numCount4types = getNumCount4types();
            // 计算所选号码 的中奖情况
            int[][] record = new int[startIndex4types.length][];
            for (int i = 0; i < record.length; i++) {
                record[i] = new int[numCount4types[i]];
            }
            LotteryWinResult result = new LotteryWinResult();
            // XXX 需要实现
            return result ;
        }
        return null;
    }

}
