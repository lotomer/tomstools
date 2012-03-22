/**
 * copyright (a) 2010-2012 tomstools.org. All rights reserved.
 */
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
 * 基于mybatis的数据库操作
 * @author lotomer
 * @date 2012-3-22 
 * @time 上午11:03:54
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
    
    public List<?> query4list(String sqlName, Object parameter, PageInfo pageInfo) throws DAOException
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
    
    public List<?> query4list(String sqlName, Object parameter) throws DAOException
    {
        return query4list(sqlName, parameter, PageInfo.DEFAULT);
    }
    
    public List<?> query4list(String sqlName) throws DAOException
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
        try {
            checkSession();
            return sqlSession.getConnection();
        } catch (DAOException e) {
            logger.error(e.getMessage(),e);
        }
        
        return null;
    }

    public void clearCache()
    {
        if (null != sqlSession)
        {
            sqlSession.clearCache();
        }
    }
}
