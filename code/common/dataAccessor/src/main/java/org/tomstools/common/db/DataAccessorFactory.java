package org.tomstools.common.db;

import org.tomstools.common.db.impl.MyBatisDataAccessorBuilder;

/**
 * 数据库访问工厂单例
 * 
 * @author: lotomer
 * @version: 1.0 Copyright: (c)2010 lotomer.org Create at: 2010-11-12 上午09:15:05
 * 
 *           Modification History: Date Author Version Description
 *           -------------------------------------------------------- 2010-11-12
 *           lotomer 1.0 1.0 Version
 */
public final class DataAccessorFactory
{
    private static DataAccessorFactory  instance            = new DataAccessorFactory();
    private DataAccessorBuilder dataAccessorFactory = null;
    
    /**
     * 获取单例
     */
    public static DataAccessorFactory getInstance()
    {
        return instance;
    }
    
    private DataAccessorFactory()
    {
        dataAccessorFactory = new MyBatisDataAccessorBuilder();
    }
    
    /**
     * 获取数据库访问对象创建器
     * @return 数据库访问工厂
     */
    public final DataAccessorBuilder getDataAccessorBuilder()
    {
        return dataAccessorFactory;
    }
    
}
