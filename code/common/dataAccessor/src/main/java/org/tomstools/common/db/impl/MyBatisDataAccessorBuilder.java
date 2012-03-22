/**
 * copyright (a) 2010-2012 tomstools.org. All rights reserved.
 */
package org.tomstools.common.db.impl;

import java.io.IOException;
import java.io.Reader;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.tomstools.common.db.DataAccessor;
import org.tomstools.common.db.DataAccessorBuilder;
import org.tomstools.common.log.Logger;
import org.tomstools.common.util.Utils;

/**
 * MyBatis数据库访问生成器
 * 
 * @author lotomer
 * @date 2012-3-22
 * @time 上午11:02:29
 */
public class MyBatisDataAccessorBuilder implements DataAccessorBuilder {
    private static final Logger logger = Logger.getLogger(MyBatisDataAccessorBuilder.class);
    private static final String DEFAULT_DB_CONFIG_FILE_NAME = "SqlMapConfig.xml";
    /** 设置数据库配置文件名是的KEY，用于系统属性设置 */
    public static final String KEY_DB_CONFIG_FILE_NAME = "DB_CONFIG_FILE_NAME";
    private SqlSessionFactory sqlSessionFactory;

   

    public  MyBatisDataAccessorBuilder(){
        this(null);
    }
    public  MyBatisDataAccessorBuilder(String environment) {
        String resource = System.getProperty(KEY_DB_CONFIG_FILE_NAME);
        if (Utils.isEmpty(resource)) {
            resource = DEFAULT_DB_CONFIG_FILE_NAME;
        }
        try {
            Reader reader = Resources.getResourceAsReader(resource);
            logger.info("connect to database start.");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader, environment);
            logger.info("connect to database success.");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
    /**
     * 根据数据库连接信息创建数据库访问生成器。基于XML配置文件的配置进行扩展
     * @param id        唯一标识
     * @param driver    驱动程序
     * @param url       数据库连接url
     * @param user      数据库用户名
     * @param password  数据库用户密码
     */
    public MyBatisDataAccessorBuilder(String id, String driver, String url,
            String user, String password) {
        this();
        Configuration config = sqlSessionFactory.getConfiguration();
        DataSource dataSource = new PooledDataSource(driver, url, user, password);
        TransactionFactory transactionFactory = config.getEnvironment().getTransactionFactory();
        Environment environment = new Environment(id, transactionFactory, dataSource);
        config.setEnvironment(environment);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(config);
    }
    public DataAccessor newDataAccessor() {
        DataAccessor dataAccerror = new MyBatisDataAccessor(sqlSessionFactory);
        return dataAccerror;
    }

}
