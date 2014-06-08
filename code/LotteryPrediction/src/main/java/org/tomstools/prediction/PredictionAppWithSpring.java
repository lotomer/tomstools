/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.prediction;

import org.tomstools.prediction.spring.ApplicationContext;

/**
 * 
 * @author admin
 * @date 2014年3月16日 
 * @time 下午10:55:14
 * @version 1.0
 */
public class PredictionAppWithSpring {
    public static void main(String[] args) {
        LotteryPrediction prediction = (LotteryPrediction) ApplicationContext.getInstance().getBean("targetCrawler");
        //targetCrawler.run();
    }
}
