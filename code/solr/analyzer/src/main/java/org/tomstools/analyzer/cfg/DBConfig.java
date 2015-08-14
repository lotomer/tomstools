/**
 * 
 */
package org.tomstools.analyzer.cfg;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wltea.analyzer.cfg.Configuration;

/**
 * @author Administrator
 *
 */
public class DBConfig implements Configuration {
	private static final Log LOG = LogFactory.getLog(DBConfig.class);
	private static final String PATH_DIC_MAIN = "org/wltea/analyzer/dic/main2012.dic";
	private static final String PATH_DIC_QUANTIFIER = "org/wltea/analyzer/dic/quantifier.dic";
	private static final String FILE_NAME = "IKAnalyzer.cfg.xml";
	// private static final String EXT_DICT = "ext_dict";
	private static final String EXT_STOP = "ext_stopwords";
	private Properties props;
	private boolean useSmart;

	public DBConfig() {
		useSmart = true;
		this.props = new Properties();

		InputStream input = getClass().getClassLoader().getResourceAsStream(FILE_NAME);
		if (input != null) {
			try {
				this.props.loadFromXML(input);
			} catch (InvalidPropertiesFormatException e) {
				LOG.error("Load config file failed: " + e.getMessage(), e);
			} catch (IOException e) {
				LOG.error("Load config file failed: " + e.getMessage(), e);
			}
		}

	}

	public boolean useSmart() {
		return this.useSmart;
	}

	public void setUseSmart(boolean useSmart) {
		this.useSmart = useSmart;
	}

	public String getMainDictionary() {
		return PATH_DIC_MAIN;
	}

	public String getQuantifierDicionary() {
		return PATH_DIC_QUANTIFIER;
	}

	public List<String> getExtDictionarys() {
		LOG.info("getExtDictionarys.");
		// 获取数据库配置
		String jdbc_classname = props.getProperty("jdbc_classname", "com.mysql.jdbc.Driver");
		String jdbc_url = props.getProperty("jdbc_url",
				"jdbc:mysql://127.0.0.1:3306/common?useUnicode=true&amp;characterEncoding=UTF-8");
		String jdbc_user = props.getProperty("jdbc_user", "root");
		String jdbc_password = props.getProperty("jdbc_password", "root123");
		String selectSql = props.getProperty("selectSql", "select WORD_CONTENT from T_C_WORD");
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(jdbc_classname);
		ds.setUrl(jdbc_url);
		ds.setUsername(jdbc_user);
		ds.setPassword(jdbc_password);
		ds.setMaxActive(2);
		QueryRunner runner = new QueryRunner(ds);
		try {
			List<String> result = runner.query(selectSql, new ColumnListHandler<String>());
			if (null != result) {
				LOG.info("Load ext words: " + result.size());
				return result;
			}
		} catch (SQLException e) {
			LOG.error("Load ext words from database failed: " + e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
		
		return Collections.emptyList();
	}

	public List<String> getExtStopWordDictionarys() {
		List<String> extStopWordDictFiles = new ArrayList<String>(2);
		String extStopWordDictCfg = this.props.getProperty(EXT_STOP);
		if (extStopWordDictCfg != null) {
			String[] filePaths = extStopWordDictCfg.split(";");
			if (filePaths != null) {
				for (String filePath : filePaths) {
					if ((filePath != null) && (!"".equals(filePath.trim()))) {
						extStopWordDictFiles.add(filePath.trim());
					}
				}
			}
		}
		return extStopWordDictFiles;
	}
}