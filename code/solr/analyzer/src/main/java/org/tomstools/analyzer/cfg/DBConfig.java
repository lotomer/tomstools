/**
 * 
 */
package org.tomstools.analyzer.cfg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wltea.analyzer.cfg.DefaultConfig;

/**
 * @author Administrator
 *
 */
public class DBConfig extends DefaultConfig {
	private static final Log LOG = LogFactory.getLog(DBConfig.class);
		
	private static final String JDBC_CLASSNAME_DEFAULT = "com.mysql.jdbc.Driver";
	private static final String JDBC_URL_DEFAULT = "jdbc:mysql://127.0.0.1:3306/common?useUnicode=true&amp;characterEncoding=UTF-8";
	private static final String JDBC_USER_DEFAULT = "root";
	private static final String JDBC_PASSWORD_DEFAULT = "root123";
	private static final String JDBC_SQL_SELECT_WORDS = "select WORD from V_WORDS";
	private static final String JDBC_SQL_SELECT_STOPWORDS = "select WORD from V_STOPWORDS";
	//private Properties props;
	//private boolean useSmart;

	public DBConfig() {
		super();
		this.setUseSmart(true);
	}
	public List<String> getExtDictionarys() {
		LOG.info("getExtDictionarys.");
		List<String> dicts = super.getExtDictionarys();
		LOG.info("Load ext dicts from file: " + dicts.size());
		// 获取数据库配置
		String jdbc_classname = getProperty("jdbc_classname", JDBC_CLASSNAME_DEFAULT);
		String jdbc_url = getProperty("jdbc_url", JDBC_URL_DEFAULT);
		String jdbc_user = getProperty("jdbc_user", JDBC_USER_DEFAULT);
		String jdbc_password = getProperty("jdbc_password", JDBC_PASSWORD_DEFAULT);
		String selectSql = getProperty("selectWordSql", JDBC_SQL_SELECT_WORDS);
//		BasicDataSource ds = new BasicDataSource();
//		ds.setDriverClassName(jdbc_classname);
//		ds.setUrl(jdbc_url);
//		ds.setUsername(jdbc_user);
//		ds.setPassword(jdbc_password);
//		ds.setMaxActive(2);
//		QueryRunner runner = new QueryRunner(ds);
		try {
			Class.forName(jdbc_classname);
		} catch (ClassNotFoundException e1) {
			LOG.error(e1.getMessage(),e1);
			return Collections.emptyList();
		}
		Connection connection = null;
		QueryRunner runner = new QueryRunner();
		try {
			connection = DriverManager.getConnection(jdbc_url, jdbc_user, jdbc_password);
			List<String> result = runner.query(connection,selectSql, new ColumnListHandler<String>());
			if (null != result) {
				LOG.info("Load ext dicts from DB: " + result.size());
				dicts.addAll(result);
				return dicts;
			}
		} catch (SQLException e) {
			LOG.error("Load ext words from database failed: " + e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}finally{
			DbUtils.closeQuietly(connection);
			connection= null;
			runner = null;
		}
		
		return dicts;
	}

	public List<String> getExtStopWordDictionarys() {
		LOG.info("getExtStopWords.");
		List<String> dicts = super.getExtStopWordDictionarys();
		LOG.info("Load ext stopwords from file: " + dicts.size());
		// 获取数据库配置
		String jdbc_classname = getProperty("jdbc_classname", JDBC_CLASSNAME_DEFAULT);
		String jdbc_url = getProperty("jdbc_url", JDBC_URL_DEFAULT);
		String jdbc_user = getProperty("jdbc_user", JDBC_USER_DEFAULT);
		String jdbc_password = getProperty("jdbc_password", JDBC_PASSWORD_DEFAULT);
		String selectSql = getProperty("selectStopWordSql", JDBC_SQL_SELECT_STOPWORDS);
//		BasicDataSource ds = new BasicDataSource();
//		ds.setDriverClassName(jdbc_classname);
//		ds.setUrl(jdbc_url);
//		ds.setUsername(jdbc_user);
//		ds.setPassword(jdbc_password);
//		ds.setMaxActive(2);
//		QueryRunner runner = new QueryRunner(ds);
		try {
			Class.forName(jdbc_classname);
		} catch (ClassNotFoundException e1) {
			LOG.error(e1.getMessage(),e1);
			return Collections.emptyList();
		}
		Connection connection = null;
		QueryRunner runner = new QueryRunner();
		try {
			connection = DriverManager.getConnection(jdbc_url, jdbc_user, jdbc_password);
			List<String> result = runner.query(connection,selectSql, new ColumnListHandler<String>());
			if (null != result) {
				LOG.info("Load ext stopwords from DB: " + result.size());
				dicts.addAll(result);
				return dicts;
			}
		} catch (SQLException e) {
			LOG.error("Load ext stopwords from database failed: " + e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}finally {
			DbUtils.closeQuietly(connection);
			connection = null;
			runner = null;
		}

		return dicts;

	}
	public static void main(String[] args) {
		DBConfig config = new DBConfig();
		System.out.println(config.getExtStopWordDictionarys());
	}
}