/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.common.concurrent.masterWorker;

import java.lang.Thread.State;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * master-worker模式中的master
 * @author admin
 * @date 2014年4月1日 
 * @time 下午10:59:39
 * @version 1.0
 */
public class Master<TASK,OUT> {
    // 任务队列
    private Queue<TASK> taskQueue = new ConcurrentLinkedQueue<TASK>();
    // 进行队列
    private Map<String, Thread> threadMap = new HashMap<String, Thread>();
    // 处理结果
    private Map<String,OUT> resultMap = new ConcurrentHashMap<String, OUT>();
    
    /**
     * 构造函数
     * @param worker    业务处理器原型
     * @param workerCount 业务处理器个数
     * @since 1.0
     */
    public Master(Worker<TASK, OUT> worker, int workerCount){
        worker.setResultMap(resultMap);
        worker.setTaskQueue(taskQueue);
        // 生成业务处理器
        for (int i = 0; i < workerCount; i++) {
            threadMap.put(Integer.toString(i), new Thread(worker, Integer.toString(i)));
        }
    }
    
    /**
     * 开始执行任务
     * 
     * @since 1.0
     */
    public void execute(){
        for (Entry<String, Thread> entry : threadMap.entrySet()) {
            entry.getValue().start();
        }
    }
    
    /**
     * 提交一个待处理的对象 
     * @param task 待处理的对象
     * @since 1.0
     */
    public void submit(TASK task){
        taskQueue.add(task);
    }
    
    /**
     * 判断是否已经处理完毕
     * @return true 已经全部处理完毕；false 没有全部处理完毕
     * @since 1.0
     */
    public boolean isComplete(){
        for (Entry<String, Thread> entry : threadMap.entrySet()) {
            if (entry.getValue().getState() != State.TERMINATED){
                // 还未结束
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 获取处理结果
     * @return 处理结果
     * @since 1.0
     */
    public Map<String, OUT> getResult(){
        return resultMap;
    }
}
