/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.handle;

/**
 * 钩子接口，用于完成任务之后执行
 * @author admin
 * @date 2014年6月21日 
 * @time 下午9:52:26
 * @version 1.0
 */
public interface CompletedHandleable {
    /**
     * 执行完成后的操作
     * @param flag  标识
     * @since 1.0
     */
    void handle(String flag);
}
