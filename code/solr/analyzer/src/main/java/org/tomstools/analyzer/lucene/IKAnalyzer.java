/**
 * 
 */
package org.tomstools.analyzer.lucene;

import java.io.Reader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.tomstools.analyzer.cfg.DBConfig;
import org.wltea.analyzer.cfg.Configuration;

/**
 * @author Administrator
 *
 */
public class IKAnalyzer extends Analyzer {
	private static final Log LOG = LogFactory.getLog(IKAnalyzer.class);
	private boolean useSmart;
	private boolean useDataBase;
	private Configuration conf;

	public IKAnalyzer() {
		LOG.info("Default construct.");
		conf = new DBConfig();
	}

	public IKAnalyzer(boolean useSmart,boolean useDataBase) {
		LOG.info("useSmart construct");
		this.useSmart = useSmart;
		this.useDataBase = useDataBase;
		if (this.useDataBase){
			conf = new DBConfig();
			conf.setUseSmart(useSmart);
		}else{
			conf = null;
		}
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
		LOG.info("createComponents. useSmart: " + useSmart);
		Tokenizer _IKTokenizer = new IKTokenizer(reader,useSmart, conf);
		TokenStreamComponents comp = new TokenStreamComponents(_IKTokenizer);
		return comp;
	}
	

	public boolean isUseDataBase() {
		return useDataBase;
	}

	public void setUseDataBase(boolean useDataBase) {
		LOG.info("set useDataBase:" + useDataBase);
		this.useDataBase = useDataBase;
	}

	public boolean useSmart() {
		return this.useSmart;
	}

	public void setUseSmart(boolean useSmart) {
		LOG.info("set useSmart:" + useSmart);
		this.useSmart = useSmart;
	}
}