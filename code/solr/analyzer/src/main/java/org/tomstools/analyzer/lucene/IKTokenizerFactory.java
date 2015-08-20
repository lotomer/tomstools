package org.tomstools.analyzer.lucene;

import java.io.Reader;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;
import org.tomstools.analyzer.cfg.DBConfig;
import org.wltea.analyzer.cfg.Configuration;

public class IKTokenizerFactory extends TokenizerFactory {
	private static final Log LOG = LogFactory.getLog(IKTokenizerFactory.class);
	private boolean useSmart;
	private Configuration conf;

	public IKTokenizerFactory(Map<String, String> params) {
		super(params);
		String useSmartParam = (String) params.get("useSmart");
		String useDataBaseParam = (String) params.get("useDataBase");
		this.useSmart = (useSmartParam != null ? Boolean.parseBoolean(useSmartParam) : false);
		boolean useDataBase = (useDataBaseParam != null ? Boolean.parseBoolean(useDataBaseParam) : false);
		if (useDataBase) {
			this.conf = new DBConfig();
		} else {
			this.conf = null;
		}
	}

	@Override
	public Tokenizer create(AttributeFactory attributeFactory, Reader in) {
		LOG.info(new StringBuilder().append("useSmart:").append(useSmart).append(" conf:").append(conf)
				.append(" attributeFactory:").append(attributeFactory).toString());
		return new IKTokenizer(in, this.useSmart, this.conf);
	}
}