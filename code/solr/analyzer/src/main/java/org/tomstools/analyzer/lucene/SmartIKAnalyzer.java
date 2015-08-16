package org.tomstools.analyzer.lucene;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SmartIKAnalyzer extends IKAnalyzer {
	private static final Log LOG = LogFactory.getLog(SmartIKAnalyzer.class);
	public SmartIKAnalyzer(){
		super(true, true);
		LOG.info("Default construct");
	}
}
