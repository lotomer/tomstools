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
	private Configuration conf;

	public boolean useSmart() {
		return this.useSmart;
	}

	public void setUseSmart(boolean useSmart) {
		this.useSmart = useSmart;
	}

	public IKAnalyzer() {
		conf = new DBConfig();
	}

	public IKAnalyzer(boolean useSmart) {
		this.useSmart = useSmart;
		conf = null;
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
		LOG.info("createComponents.");
		System.out.println("tom createComponents.");
		Tokenizer _IKTokenizer = null != conf ? new IKTokenizer(reader, conf) : new IKTokenizer(reader, useSmart());
		TokenStreamComponents comp = new TokenStreamComponents(_IKTokenizer);
		return comp;
	}
}