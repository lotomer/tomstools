/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.prediction.lottery.impl;

import java.util.Arrays;

import org.tomstools.prediction.lottery.Lottery;

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

}
