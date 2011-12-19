/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.ui.tags.handle;

import java.util.List;
import java.util.Map;

/**
 * 查询结果
 * @author lotomer
 * @date 2011-12-14 
 * @time 下午04:25:44
 */
public class QueryResult {
    private int pageSize;   //页面大小
    private int pageNum;    //当前页码。从1开始计数，即第一页为1
    private long count;     //从记录数
    private List<Map<String, String>> result;   //结果集
    
    public final int getPageSize() {
        return pageSize;
    }
    public final void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    public final int getPageNum() {
        return pageNum;
    }
    public final void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }
    public final long getCount() {
        return count;
    }
    public final void setCount(long count) {
        this.count = count;
    }
    public final List<Map<String, String>> getResult() {
        return result;
    }
    public final void setResult(List<Map<String, String>> result) {
        this.result = result;
    }
}
