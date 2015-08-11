/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.prediction.lottery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 预测结果
 * 
 * @author admin
 * @date 2014年6月6日
 * @time 上午10:49:13
 * @version 1.0
 */
public class PredictResult {
    private NumberPredict[][] numberPredict4types;
    private List<Float[]> rank4types;

    /**
     * 
     * @param value4types 各种球的数值
     * @param probability4types 各种球的概率
     * @since 1.0
     */
    public PredictResult(int[][] value4types, float[][] probability4types) {
        int typeCount = probability4types.length;
        numberPredict4types = new NumberPredict[typeCount][];
        rank4types = new ArrayList<>(typeCount);
        // 组织结果
        for (int i = 0; i < typeCount; i++) {
            int numCount = value4types[i].length;
            numberPredict4types[i] = new NumberPredict[numCount];
            Set<Float> ranks = new HashSet<>();
            for (int j = 0; j < numCount; j++) {
                numberPredict4types[i][j] = new NumberPredict(value4types[i][j],
                        probability4types[i][j]);
                ranks.add(probability4types[i][j]);
            }

            // 预测结果排序：降序
            Arrays.sort(numberPredict4types[i], new Comparator<NumberPredict>() {
                @Override
                public int compare(NumberPredict o1, NumberPredict o2) {
                    return -Float.compare(o1.probability, o2.probability);
                }
            });

            // 排名进行排序：降序
            Float[] rs = ranks.toArray(new Float[0]);
            Arrays.sort(rs, new Comparator<Float>() {
                @Override
                public int compare(Float o1, Float o2) {
                    return o2.compareTo(o1);
                }
            });

            rank4types.add(rs);
        }
    }

    /*
     * @since 1.0
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(Arrays.deepToString(numberPredict4types));
        return builder.toString();
    }

    public static class NumberPredict {
        public final int number;
        public final float probability;

        /**
         * @param number
         * @param probability
         * @since 1.0
         */
        public NumberPredict(int number, float probability) {
            super();
            this.number = number;
            this.probability = probability;
        }

        /*
         * @since 1.0
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(number).append("=").append(probability);
            return builder.toString();
        }
    }

    public LotteryRecordCheckResult check(LotteryRecord lotteryRecord) {
        int[] numbers = lotteryRecord.getNumbers();
        Lottery lottery = lotteryRecord.getLottery();
        // 开奖结果号码对应的比重
        float[] probabilities = new float[numbers.length];
        // 开奖结果号码对应的排名
        int[] ranks = new int[numbers.length];

        int[] startIndex4types = lottery.getStartIndex4types();
        int typeCount = startIndex4types.length;

        // 生成某球在所有球中的索引对应的是某种球的索引
        HashMap<Integer, Integer> indexMap = new HashMap<Integer, Integer>();
        for (int i = 0; i < typeCount; i++) {
            int startNum = startIndex4types[i];
            int endNum = (i + 1 < typeCount) ? startIndex4types[i + 1] : lottery.getNumberCount();
            for (; startNum < endNum; startNum++) {
                indexMap.put(startNum, i);
            }
        }

        // 评估号码
        for (int i = 0; i < numbers.length; i++) {
            // 获取号码球所在组
            int typeIndex = indexMap.get(i);
            Float[] rank4type = rank4types.get(typeIndex);
            NumberPredict[] nums = this.numberPredict4types[typeIndex];
            // 找到对应的号码
            for (int j = 0; j < nums.length; j++) {
                if (numbers[i] == nums[j].number) {
                    probabilities[i] = nums[j].probability; // 权重
                    // 根据权重获取排名
                    int index = Arrays.binarySearch(rank4type, 0, rank4type.length, probabilities[i],new Comparator<Float>() {
                        @Override
                        public int compare(Float o1, Float o2) {
                            // 使用降序
                            return o2.compareTo(o1);
                        }
                    });
                    ranks[i] =  index + 1; // 排名
                    break;
                }
            }
        }

        return new LotteryRecordCheckResult(lotteryRecord, probabilities, ranks);
    }

    public LotteryWinResult testPrediction(LotteryRecord lotteryRecord, int[] numSelect4type) {
        //int[] numbers = lotteryRecord.getNumbers();
        Lottery lottery = lotteryRecord.getLottery();
        // 号码球种类
        int typeCount = numSelect4type.length;
        // 取出TopN的预测结果
        int[][] predResult = new int[typeCount][];
        for (int i = 0; i < typeCount; i++) {
            predResult[i] = new int[numSelect4type[i]];
            for (int j = 0; j < predResult[i].length; j++) {
                predResult[i][j] = this.numberPredict4types[i][j].number;
            }
        }
        
        LotteryWinResult result = lottery.checkWinner(predResult,lotteryRecord);
        return result ;
    }
}
