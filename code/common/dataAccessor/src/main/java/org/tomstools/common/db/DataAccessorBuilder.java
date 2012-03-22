/**
 * copyright (a) 2010-2012 tomstools.org. All rights reserved.
 */
package org.tomstools.common.db;


/**  
 * 数据访问对象创建器
 * @author lotomer
 * @date 2012-3-22 
 * @time 上午11:04:58
 */
public interface DataAccessorBuilder
{
    /** 生成一个数据库访问对象 */
    public DataAccessor newDataAccessor();
}
