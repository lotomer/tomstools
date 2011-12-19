package org.tomstools.common.db.impl;

import java.sql.Connection;
import java.util.List;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.tomstools.common.db.DataAccessor;
import org.tomstools.common.db.exception.DAOException;
import org.tomstools.common.db.result.PageInfo;
import org.tomstools.common.log.Logger;

/**
 * 数据库操作
 * 
 * @author: lotomer
 * @version: 1.0 Copyright: (c)2010 lotomer.org Create at: 2010-7-30 龙昌茂02:38:11
 * 
 *           Modification History: Date Author Version Description
 *           -------------------------------------------------------- 2010-7-30
 *           lotomer 1.0 1.0 Version
 */
public class MyBatisDataAccessor implements DataAccessor
{
    private static Logger     logger            = Logger.getLogger(MyBatisDataAccessor.class);
    private SqlSession        sqlSession;
    private SqlSessionFactory sqlSessionFactory;
    
    public MyBatisDataAccessor(SqlSessionFactory sqlSessionFactory)
    {
        this.sqlSessionFactory = sqlSessionFactory;
    }
    
    public void open() throws DAOException
    {
        open(true);
    }
    
    public void open(boolean autoCommit) throws DAOException
    {
        // 先关闭
        close();
        
        logger.info("open()");
        // 打开数据库连接
        try
        {
            sqlSession = sqlSessionFactory.openSession(autoCommit);
        }
        catch (PersistenceException e)
        {
            throw new DAOException(e);
        }
        clearCache();
    }
    
    public void commit() throws DAOException
    {
        logger.info("--------------------commit");
        try
        {
            sqlSession.commit();
        }
        catch (PersistenceException e)
        {
            throw new DAOException(e);
        }
    }
    
    public void rollback(boolean force)
    {
        logger.info("===================rollback===force: " + force);
        sqlSession.rollback(force);
    }
    
    public void close()
    {        
        if (null != sqlSession)
        {
            logger.info("close()");
            sqlSession.close();
            sqlSession = null;
        }
    }
    
    public int delete(String sqlName, Object parameter) throws DAOException
    {
        checkSession();
        try
        {
            return sqlSession.delete(sqlName, parameter);
        }
        catch (PersistenceException e)
        {
            throw new DAOException(e);
        }
    }
    
    public int delete(String sqlName) throws DAOException
    {
        return delete(sqlName, null);
    }
    
    public int insert(String sqlName, Object parameter) throws DAOException
    {
        checkSession();
        try
        {
            return sqlSession.insert(sqlName, parameter);
        }
        catch (PersistenceException e)
        {
            throw new DAOException(e);
        }
    }
    
    public int insert(String sqlName) throws DAOException
    {
        return insert(sqlName, null);
    }
    
    public Object query(String sqlName, Object parameter) throws DAOException
    {
        checkSession();
        try
        {
            return sqlSession.selectOne(sqlName, parameter);
        }
        catch (PersistenceException e)
        {
            throw new DAOException(e);
        }
    }
    
    public Object query(String sqlName) throws DAOException
    {
        return query(sqlName, null);
    }
    
    @SuppressWarnings("rawtypes")
    public List query4list(String sqlName, Object parameter, PageInfo pageInfo) throws DAOException
    {
        checkSession();
        int offset = (pageInfo.getPageNum() - 1) * pageInfo.getPageSize();
        int limit = pageInfo.getPageSize();
        RowBounds rowBounds = new RowBounds(offset, limit);
        try
        {
            return sqlSession.selectList(sqlName, parameter, rowBounds);
        }
        catch (PersistenceException e)
        {
            throw new DAOException(e);
        }
    }
    
    @SuppressWarnings("rawtypes")
    public List query4list(String sqlName, Object parameter) throws DAOException
    {
        return query4list(sqlName, parameter, PageInfo.DEFAULT);
    }
    
    @SuppressWarnings("rawtypes")
    public List query4list(String sqlName) throws DAOException
    {
        return query4list(sqlName, null, PageInfo.DEFAULT);
    }
    
    public int update(String sqlName, Object parameter) throws DAOException
    {
        checkSession();
        try
        {
            return sqlSession.update(sqlName, parameter);
        }
        catch (PersistenceException e)
        {
            throw new DAOException(e);
        }
    }
    
    public int update(String sqlName) throws DAOException
    {
        return update(sqlName, null);
    }
    
    private void checkSession() throws DAOException
    {
        if (null == sqlSession)
        {
            throw new DAOException("The sqlSession if null, please use method open() before operate the database.");
        }
    }

    public Connection getConnection()
    {
        return sqlSession.getConnection();
    }

    public void clearCache()
    {
        if (null != sqlSession)
        {
            sqlSession.clearCache();
        }
    }
}
