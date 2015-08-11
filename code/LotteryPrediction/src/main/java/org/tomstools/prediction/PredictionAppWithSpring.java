/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.prediction;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 
 * @author admin
 * @date 2014年3月16日 
 * @time 下午10:55:14
 * @version 1.0
 */
public class PredictionAppWithSpring {
    public static void main(String[] args) throws IOException {
        InputStream inStream = PredictionAppWithSpring.class.getClassLoader().getResourceAsStream("config.properties");
        Properties p = new Properties();
        p.load(inStream);
        System.out.println(p);
        //LotteryPrediction prediction = (LotteryPrediction) ApplicationContext.getInstance().getBean("targetCrawler");
        //targetCrawler.run();
    }
}
