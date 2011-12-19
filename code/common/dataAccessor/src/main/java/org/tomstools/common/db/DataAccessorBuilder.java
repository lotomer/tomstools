package org.tomstools.common.db;


/**  
 * 数据访问对象创建器
 * @author:     lotomer
 * @version:    1.0  
 * Copyright:   (c)2010 lotomer.org 
 * Create at:   2010-8-4 上午11:28:30  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * --------------------------------------------------------  
 * 2010-8-4      lotomer     1.0          1.0 Version  
 */
public interface DataAccessorBuilder
{
    /** 生成一个数据库访问对象 */
    public DataAccessor newDataAccessor();
}
