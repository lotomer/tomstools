package org.wltea.analyzer.cfg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 修改记录：将扩展词和停用词从文件中读取
 * 
 * @author Administrator
 *
 */
public class DefaultConfig implements Configuration {
	private static final Log LOG = LogFactory.getLog(DefaultConfig.class);
	private static final String PATH_DIC_MAIN = "org/wltea/analyzer/dic/main2012.dic";
	private static final String PATH_DIC_QUANTIFIER = "org/wltea/analyzer/dic/quantifier.dic";
	private static final String FILE_NAME = "IKAnalyzer.cfg.xml";
	private static final String EXT_DICT = "ext_dict";
	private static final String EXT_STOP = "ext_stopwords";
	private Properties props;
	private boolean useSmart;

//	public static Configuration getInstance() {
//		return new DefaultConfig();
//	}

	public DefaultConfig() {
		this.props = new Properties();

		InputStream input = getClass().getClassLoader().getResourceAsStream(FILE_NAME);
		if (input != null){
			LOG.info("Load config file success:" + FILE_NAME);
			try {
				this.props.loadFromXML(input);
			} catch (InvalidPropertiesFormatException e) {
				LOG.error(e.getMessage(),e);
			} catch (IOException e) {
				LOG.error(e.getMessage(),e);
			}
		}else{
			LOG.warn("Load config file failed:" + FILE_NAME);
		}
	}

	public boolean useSmart() {
		return this.useSmart;
	}

	public void setUseSmart(boolean useSmart) {
		this.useSmart = useSmart;
	}

	public List<String> getMainDictionary() {
		return loadWordsFromFile(PATH_DIC_MAIN);
	}

	public List<String> getQuantifierDicionary() {
		return loadWordsFromFile(PATH_DIC_QUANTIFIER);
	}

	public List<String> getExtDictionarys() {
		LOG.info("getExtDictionarys from file.");
		List<String> extDictFiles = new ArrayList<String>(2);
		String extDictCfg = getProperty(EXT_DICT);
		if (extDictCfg != null) {
			String[] filePaths = extDictCfg.split(";");
			if (filePaths != null) {
				for (String filePath : filePaths) {
					if ((filePath != null) && (!"".equals(filePath.trim()))) {
						extDictFiles.add(filePath.trim());
					}
				}
			}
		}

		return loadWordsFromFile(extDictFiles);
	}

	public List<String> getExtStopWordDictionarys() {
		LOG.info("getExtStopWordDictionarys from file.");
		List<String> extStopWordDictFiles = new ArrayList<String>(2);
		String extStopWordDictCfg = getProperty(EXT_STOP);
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

		return loadWordsFromFile(extStopWordDictFiles);
	}

	private List<String> loadWordsFromFile(String wordDictFile) {
		LOG.info("Load words from file：" + wordDictFile);
		InputStream is = getClass().getClassLoader().getResourceAsStream(wordDictFile);
		
		if (is != null) {
			List<String> words = new ArrayList<String>();
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"), 512);
				String theWord = null;
				do {
					theWord = br.readLine();
					if ((theWord != null) && (!"".equals(theWord.trim()))) {
						words.add(theWord.trim().toLowerCase());
					}
				} while (theWord != null);
			} catch (IOException ioe) {
				LOG.error("Load words from file failed: " + ioe.getMessage());
			} finally {
				try {
					if (is != null) {
						is.close();
						is = null;
					}
				} catch (IOException e) {
					LOG.error(e.getMessage(),e);
				}
			}
			
			return words;
		}
		
		return Collections.emptyList();
	}
	private List<String> loadWordsFromFile(List<String> wordDictFiles) {
		if (wordDictFiles != null) {
			List<String> words = new ArrayList<String>();
			
			for (String wordDictFile : wordDictFiles) {
				words.addAll(loadWordsFromFile(wordDictFile));
			}
			
			return words;
		}else{
			return Collections.emptyList();
		}
	}
	
	protected String getProperty(String key,String defaultValue){
		return props.getProperty(key,defaultValue);
	}
	protected String getProperty(String key){
		return props.getProperty(key,null);
	}
}
