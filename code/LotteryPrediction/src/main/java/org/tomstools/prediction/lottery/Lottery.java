/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.prediction.lottery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 彩票
 * @author admin
 * @date 2014年6月6日 
 * @time 下午1:03:41
 * @version 1.0
 */
public abstract class Lottery {
    private List<LotteryRecord> lotteryRecords;

    /**
     * 添加彩票开奖记录
     * @param lotteryRecords 彩票历史开奖记录
     * @since 1.0
     */
    public void initRecords(List<LotteryRecord> lotteryRecords) {
        this.lotteryRecords = new ArrayList<>(lotteryRecords);
        // 根据期数进行升序排序
        Collections.sort(this.lotteryRecords, new Comparator<LotteryRecord>() {
            @Override
            public int compare(LotteryRecord o1, LotteryRecord o2) {
                if (null == o1){
                    return 1;
                }else if (null == o2){
                    return -1;
                } else if (o1.getQishu() == o2.getQishu()){
                    return 0;
                }else{
                    return o1.getQishu() > o2.getQishu() ? 1 : -1;
                }
            }
        });
    }

    /**
     * 获取彩票历史开奖记录
     * @return 彩票历史开奖记录
     * @since 1.0
     */
    public List<LotteryRecord> getLotteryRecords() {
        return lotteryRecords;
    }

    /**
     * @return 返回 各种球的最大值
     * @since 1.0
     */
    public abstract int[] getMaxValue4types();

    /**
     * @return 返回 各种球的开始索引号
     * @since 1.0
     */
    public abstract int[] getStartIndex4types() ;

    /**
     * 获取总号码个数
     * @return 总号码个数
     * @since 1.0
     */
    public abstract int getNumberCount();

}
