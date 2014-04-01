/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.common.concurrent.masterWorker;

import java.util.Map;
import java.util.Queue;

/**
 * master-worker模式中的worker
 * @author admin
 * @date 2014年4月1日 
 * @time 下午11:06:33
 * @version 1.0
 */
public abstract class Worker<TASK,OUT> implements Runnable {
    private Queue<TASK> taskQueue;
    private Map<String,OUT> resultMap;
    
    /**
     * @param tasks 设置 任务队列
     * @since 1.0
     */
    public final void setTaskQueue(Queue<TASK> taskQueue) {
        this.taskQueue = taskQueue;
    }


    /**
     * @param resultMap 设置 结果收集器
     * @since 1.0
     */
    public final void setResultMap(Map<String, OUT> resultMap) {
        this.resultMap = resultMap;
    }

    

    /**
     * 返回结果集
     * @since 1.0
     */
    public final Map<String, OUT> getResultMap() {
        return resultMap;
    }

    public final void run() {
        TASK task = null;
        while(true){
            task = taskQueue.poll();
            if (null == task){
                // 已经处理完了，直接退出
                break;
            }else{
                OUT out = handle(task);
                resultMap.put(Integer.toString(task.hashCode()), out);
            }
        }
    }


    /**
     * 任务处理。由子类实现
     * @param task 待处理的任务
     * @return 处理后的结果
     * @since 1.0
     */
    protected abstract OUT handle(TASK task);
    
}
