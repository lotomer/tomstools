/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler;

import org.tomstools.crawler.spring.ApplicationContext;

/**
 * 
 * @author admin
 * @date 2014年3月16日 
 * @time 下午10:55:14
 * @version 1.0
 */
public class TargetCrawlerAppWithSpring {
    public static void main(String[] args) {
        TargetCrawler targetCrawler = (TargetCrawler) ApplicationContext.getInstance().getBean("targetCrawler");
        targetCrawler.run();
    }
}
