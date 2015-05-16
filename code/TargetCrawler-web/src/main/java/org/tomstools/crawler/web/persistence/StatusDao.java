package org.tomstools.crawler.web.persistence;

import java.util.List;

/**
 * 爬取状态
 * @param <T>
 * @author admin
 * @date 2015年5月16日 
 * @time 上午11:10:37
 * @version 1.0
 */
public interface  StatusDao<T> {
    public List<T> select(String status);
}
