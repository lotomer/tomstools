/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.http;

import java.util.HashSet;
import java.util.Set;

/**
 * url管理器
 * @author admin
 * @date 2014年3月13日 
 * @time 上午9:53:56
 * @version 1.0
 */
public class UrlManager {
    private Set<String> finishedUrls;
    
    /**
     * 默认钩子函数
     * @since 1.0
     */
    public UrlManager() {
        super();
        finishedUrls = new HashSet<String>();
    }

    /**
     * 判断指定url是否已经被爬取过
     * @param url 待判断的url
     * @return true 已经爬取过；false 未爬取过
     * @since 1.0
     */
    public boolean isFinished(String url) {
        return finishedUrls.contains(url);
    }

    /**
     * 将指定url置为已爬取
     * @param url 指定url
     * @since 1.0
     */
    public void setFinished(String url) {
        finishedUrls.add(url);
    }
    
    public void clean(){
        finishedUrls.clear();
        finishedUrls = null;
        finishedUrls = new HashSet<String>();
    }
}
