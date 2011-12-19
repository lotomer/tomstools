package org.tomstools.common.db.impl;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.tomstools.common.db.DataAccessor;
import org.tomstools.common.db.DataAccessorBuilder;
import org.tomstools.common.log.Logger;
/**
 * MyBatis数据访问工厂
 * 
 * @author:     lotomer
 * @version:    1.0  
 * Copyright:   (c)2010 lotomer.org 
 * Create at:   2010-11-12 上午09:28:20  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * --------------------------------------------------------  
 * 2010-11-12      lotomer     1.0          1.0 Version
 */
public class MyBatisDataAccessorBuilder implements DataAccessorBuilder
{
    private static final Logger logger = Logger.getLogger(MyBatisDataAccessorBuilder.class);
    private SqlSessionFactory sqlSessionFactory;
    
    public MyBatisDataAccessorBuilder()
    {
        String resource = "SqlMapConfig.xml";
        
        Reader reader = null;
        try
        {
            reader = Resources.getResourceAsReader(resource);
        }
        catch (IOException e)
        {
            logger.error(e.getMessage(),e);
        }
        logger.info("connect to database start.");
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        logger.info("connect to database success.");
    }
    
    public DataAccessor newDataAccessor()
    {
        DataAccessor dataAccerror = new MyBatisDataAccessor(sqlSessionFactory);
        return dataAccerror;
    }
    
}
