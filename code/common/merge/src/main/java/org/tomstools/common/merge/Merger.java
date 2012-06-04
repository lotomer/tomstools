/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.common.merge;

import java.io.File;

/**
 * 合并工具
 * @author vaval
 * @date 2011-12-16 
 * @time 下午07:37:29
 */
public interface Merger {
    /**
     * 添加源数据
     * @param source  源数据
     */
    public void add(String source);
    
    /**
     * 执行合并
     * @param dest    执行合并时的辅助信息
     * @param charset 字符集
     * @param handle  内容处理钩子
     */
    public void merge(String dest, String charset, Handle handle);
    
    public interface Handle{
        String process(File file,String fileContent,File destFile);
    }
}
