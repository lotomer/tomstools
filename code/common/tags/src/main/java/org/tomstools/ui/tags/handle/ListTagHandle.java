/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.ui.tags.handle;


/**
 * list标签的数据来源钩子
 * @author lotomer
 * @date 2011-12-14 
 * @time 下午04:10:43
 */
public interface ListTagHandle {
    /**
     * 根据来源字符串获取结果集
     * @param from 来源字符串
     * @param pageSize 页面大小
     * @param pageNum 当前页码。从1开始计数，即第一页为1
     * @return 查询结果
     */
    public QueryResult queryDatas(String from, int pageSize, int pageNum);
    
    /**
     * 将字典数据转换为显示名称
     * @param dicType 字典类型
     * @param dicValue 字典数据
     * @return 字典数据对应的显示名称。如果没有匹配上，则返回原值
     */
    public String transfer(String dicType, String dicValue);
}
