/**
 * copyright (a) 2010-2012 tomstools.org. All rights reserved.
 */
package org.tomstools.common.db;

import java.sql.Connection;
import java.util.List;

import org.tomstools.common.db.exception.DAOException;
import org.tomstools.common.db.result.PageInfo;

/**
 * 数据访问接口
 * @author lotomer
 * @date 2012-3-22 
 * @time 上午11:05:09
 */
public interface DataAccessor {
    /**
     * 获取数据库连接 正常使用时不需要单独调用此接口，只有在特殊情况下（如：兼容以前数据库操作代码）才需要调用此接口
     * 
     * @return 数据库连接
     */
    public Connection getConnection();

    /**
     * 打开数据库连接
     * 
     * @param autoCommit
     *            是否自动提交修改
     * @throws DAOException
     *             打开失败
     */
    public void open(boolean autoCommit) throws DAOException;

    /**
     * 打开数据库连接。自动提交修改
     */
    public void open() throws DAOException;

    /**
     * 关闭数据库连接
     */
    public void close();

    /**
     * 主动提交数据库操作中的数据修改
     * 
     * @throws DAOException
     */
    public void commit() throws DAOException;

    /**
     * 数据库操作回退
     * 
     * @param force
     *            是否强制回退
     */
    public void rollback(boolean force);

    /**
     * 从数据库查询一条记录
     * 
     * @param sqlName
     *            SQL语句对应的名称
     * @return 一条结果记录对应的java对象
     * @throws DAOException
     *             查询失败
     */
    public Object query(String sqlName) throws DAOException;

    /**
     * 根据查询条件从数据库查询一条记录
     * 
     * @param sqlName
     *            SQL语句对应的名称
     * @param parameter
     *            查询条件
     * @return 一条结果记录对应的java对象
     * @throws DAOException
     *             查询失败
     */
    public Object query(String sqlName, Object parameter) throws DAOException;

    /**
     * 从数据库查询一个结果集合
     * 
     * @param sqlName
     *            SQL语句对应的名称
     * @return 结果集
     * @throws DAOException
     */
    public List<?> query4list(String sqlName) throws DAOException;

    /**
     * 根据查询条件从数据库查询一个结果集合
     * 
     * @param sqlName
     *            查询语句名称
     * @param parameter
     *            查询条件
     * @return 结果集
     * @throws DAOException
     */
    public List<?> query4list(String sqlName, Object parameter) throws DAOException;

    /**
     * 根据查询条件从数据库查询一个分页结果集合
     * 
     * @param sqlName
     *            SQL语句对应的名称
     * @param parameter
     *            查询条件
     * @param pageInfo
     *            分页信息
     * @return 分页结果集合
     * @throws DAOException
     */
    public List<?> query4list(String sqlName, Object parameter, PageInfo pageInfo)
            throws DAOException;

    /**
     * 执行数据库插入操作
     * 
     * @param sqlName
     *            SQL语句对应的名称
     * @return 影响的记录数
     * @throws DAOException
     */
    public int insert(String sqlName) throws DAOException;

    /**
     * 根据参数执行数据库插入操作
     * 
     * @param sqlName
     *            SQL语句对应的名称
     * @param parameter
     *            参数信息
     * @return 影响的记录数
     * @throws DAOException
     */
    public int insert(String sqlName, Object parameter) throws DAOException;

    /**
     * 执行数据库更新操作
     * 
     * @param sqlName
     *            SQL语句对应的名称
     * @return 影响的记录数
     * @throws DAOException
     */
    public int update(String sqlName) throws DAOException;

    /**
     * 根据参数执行数据库更新操作
     * 
     * @param sqlName
     *            SQL语句对应的名称
     * @param parameter
     *            参数信息
     * @return 影响的记录数
     * @throws DAOException
     */
    public int update(String sqlName, Object parameter) throws DAOException;

    /**
     * 执行数据库删除操作
     * 
     * @param sqlName
     *            SQL语句对应的名称
     * @return 影响的记录数
     * @throws DAOException
     */
    public int delete(String sqlName) throws DAOException;

    /**
     * 根据参数执行数据库删除操作
     * 
     * @param sqlName
     *            SQL语句对应的名称
     * @param parameter
     *            参数信息
     * @return 影响的记录数
     * @throws DAOException
     */
    public int delete(String sqlName, Object parameter) throws DAOException;

    /** 清除缓存 */
    public void clearCache();
}
