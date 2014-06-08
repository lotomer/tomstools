/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.prediction.algorithm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.tomstools.prediction.lottery.Lottery;
import org.tomstools.prediction.lottery.LotteryRecord;
import org.tomstools.prediction.lottery.PredictResult;

/**
 * 简单预测模型算法
 * 
 * @author admin
 * @date 2014年6月6日
 * @time 下午12:47:19
 * @version 1.0
 */
public class SimpleLotteryPredictionModelAlgorithm extends LotteryPredictionModelAlgorithm {
    private Map<Integer, Integer> indexMap;

    @Override
    public PredictResult predict(Lottery lottery) throws Exception {
        // 生成所有数字及初始权重
        int[] startIndex4types = lottery.getStartIndex4types();
        int[] maxValue4types = lottery.getMaxValue4types();
        
        // 校验数据
        if (startIndex4types.length != maxValue4types.length) {
            throw new Exception("The lottery's startIndex4types' length["
                    + startIndex4types.length + "] must equal maxValue4types's length["
                    + maxValue4types.length + "]");
        }
        if (startIndex4types.length < 0 || lottery.getNumberCount() < startIndex4types.length){
            throw new Exception("Invalid lottery's config! " + lottery);
        }
        int numberCount = lottery.getNumberCount();
        for (int i = 0; i < startIndex4types.length; i++) {
            // 索引值不能非法
            if (startIndex4types[i] < 0 || numberCount <= startIndex4types[i]){
                throw new Exception("All index config must less than lottery's number count! " + lottery );
            }
        }
        
        int typeCount = startIndex4types.length;
        // 保存每种球中所有球的数值
        int[][] value4types = new int[typeCount][];
        // 保存每种球中所有球的概率值
        float[][] probability4types = new float[typeCount][];
        // 保持每种球中所有球已经出现的次数
        int[][] count4types = new int[startIndex4types.length][];
        for (int i = 0; i < typeCount; i++) {
            // 初始化各种球的数值
            value4types[i] = new int[maxValue4types[i]];
            for (int j = 0; j < maxValue4types[i]; j++) {
                value4types[i][j] = j + 1;
            }
            
            // 初始化统计值
            count4types[i] = new int[maxValue4types[i]];
            Arrays.fill(count4types[i], 0);
            // 初始化各种球的概率
            probability4types[i] = new float[maxValue4types[i]];
            Arrays.fill(probability4types[i], 0);
        }
        
        // 生成某球在所有球中的索引对应的是某种球的索引
        indexMap = new HashMap<Integer, Integer>();
        for (int i = 0; i < typeCount; i++) {
            int startNum = startIndex4types[i];
            int endNum = (i + 1 < typeCount) ? startIndex4types[i + 1] : numberCount;
            for (; startNum < endNum; startNum++) {
                indexMap.put(startNum, i);
            }
        }
        // 统计所有球出现的次数
        for (LotteryRecord record : this.records) {
            int[] nums = record.getNumbers();
            for (int i = 0; i < nums.length; i++) {
                // 获取号码球所在组
                int typeIndex = indexMap.get(i);
                // 号码球出现次数加1
                int[] counts = count4types[typeIndex];
                counts[nums[i] - 1] = counts[nums[i] - 1] + 1;
            }
        }
        
        // 开始计算概率值
        float recordCount = this.records.size() + 1;
        // 遍历每种号码球
        for (int i = 0; i < typeCount; i++) {
            // 遍历同种号码球中的所有号码球
            for (int j = 0; j < count4types[i].length; j++) {
                probability4types[i][j] = probability4types[i][j] + (recordCount - count4types[i][j]) / (1 + recordCount);
            }
        }
        
        // 返回结果
        return new PredictResult(value4types,probability4types);
    }

}
