/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.prediction.lottery;

/**
 * 记录中的号码
 * @author admin
 * @date 2014年6月8日 
 * @time 上午11:36:59
 * @version 1.0
 */
public class RecordNumber{
    public final int number;
    public final int rank;
    public final float probability;
    /**
     * @param number
     * @param rank
     * @param probability
     * @since 1.0
     */
    public RecordNumber(int number, int rank, float probability) {
        super();
        this.number = number;
        this.rank = rank;
        this.probability = probability;
    }
    /*
     * @since 1.0
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[").append(number).append(", ").append(rank)
                .append(", ").append(probability).append("]");
        return builder.toString();
    }
    
    
}