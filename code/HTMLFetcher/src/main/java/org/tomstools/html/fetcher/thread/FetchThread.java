/**
 * copyright (a) 2010-2012 tomstools.org. All rights reserved.
 */
package org.tomstools.html.fetcher.thread;

import org.tomstools.common.log.Logger;


/**
 * @author lotomer
 * @date 2012-6-11
 * @time 下午03:59:39
 */
public class FetchThread implements Runnable {
    private static final Logger logger = Logger.getLogger(FetchThread.class);
    private FetchTask task;
    private String name;


    /**
     * 初始化任务
     * @param task 任务信息
     */
    public FetchThread(FetchTask task, String name) {
        this.task = task;
        this.name = name;
    }

    /**
     * 任务执行
     */
    public void run() {
        logger.info("start run thread " + name);
        long startTime = System.currentTimeMillis();
        try {
            task.run();
        } finally {
            logger.info("stop thread " + name + ". Cost " + (System.currentTimeMillis() - startTime) + " ms.");
        }
    }
}
