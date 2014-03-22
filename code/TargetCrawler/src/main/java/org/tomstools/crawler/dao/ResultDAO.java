/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.dao;

import java.util.Collection;
import java.util.Map;

import org.tomstools.crawler.config.Target;

/**
 * 结果数据操作对象
 * 
 * @author admin
 * @date 2014年3月13日
 * @time 上午9:53:21
 * @version 1.0
 */
public interface ResultDAO {
    /**
     * 保存结果数据
     * 
     * @param target 目标对象
     * @param name 名称
     * @param url 对应的url
     * @param datas 提取出来的结果数据
     * @since 1.0
     */
    public void save(Target target, String url, Map<String, String> datas);

    /**
     * 获取指定主页面最后处理过的页面的url
     * 
     * @param targetName 目标名
     * @return 该主页面对应的最后处理过的url
     * @since 1.0
     */
    //public String getLatestProcessedUrl(String targetName);

    /**
     * 保存主页面最后处理过的标识数据
     * 
     * @param target 目标对象
     * @param flagDatas 处理过的标识数据
     * @since 1.0
     */
    public void saveProcessedFlagDatas(Target target, Collection<String> flagDatas);

    /**
     * 开始处理
     * 
     * @param target 目标对象
     * @return 处理标记数据。不为null，可能为空
     * @throws Exception
     * @since 1.0
     */
    public Collection<String> prepare(Target target) throws Exception;

    /**
     * 结束处理
     * 
     * @param target 目标对象
     * @since 1.0
     */
    public void finish(Target target);
}
