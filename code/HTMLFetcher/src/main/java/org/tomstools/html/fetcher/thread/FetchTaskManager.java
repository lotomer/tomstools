/**
 * copyright (a) 2010-2012 tomstools.org. All rights reserved.
 */
package org.tomstools.html.fetcher.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.tomstools.html.fetcher.HTMLFetcher;

/**
 * 抓取任务管理类
 * @author lotomer
 * @date 2012-6-11 
 * @time 下午03:49:00
 */
public class FetchTaskManager {
    //private static final Logger logger = Logger.getLogger(FetchTaskManager.class);
    private ThreadPoolExecutor pool;
    private int count;
    private HTMLFetcher fetcher;

    public FetchTaskManager(HTMLFetcher fetcher, int threadCount) {
        this.fetcher = fetcher;
        if (threadCount < 1){
            threadCount = 1;
        }
        count = 1;
        pool = new ThreadPoolExecutor(threadCount, threadCount, 3, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1000));
    }
    /**
     * 增加一个抓取任务
     * @param outputFileName        文件保存路径
     * @param url                   抓取网页的URL
     * @param regexpFilterInclude   抓取网页的内容需要包含哪些数据的过滤器。null 表示不使用该过滤器，即获取整个网页
     * @param regexpFilterExclude   不能包含哪些内容的过滤器。null 表示不使用该过滤器
     */
    public void addTask(String outputFileName, String url, String regexpFilterInclude, String regexpFilterExclude) {
        FetchTask task = new FetchTask(fetcher,outputFileName,url,regexpFilterInclude,regexpFilterExclude);
        pool.execute(new FetchThread(task, "#" + count++));
    }
    
    /**
     * 执行所有任务
     */
    public void runTasks() {        
        while(true){
            if (pool.getCompletedTaskCount() == pool.getTaskCount()){
                pool.shutdown();
                break;
            }
        }
    }
}
