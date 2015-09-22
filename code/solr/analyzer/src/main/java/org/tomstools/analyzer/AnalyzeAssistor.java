package org.tomstools.analyzer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tomstools.analyzer.cfg.DBConfig;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

public class AnalyzeAssistor {
	private static final Log LOG = LogFactory.getLog(AnalyzeAssistor.class);
	private IKSegmenter ik;

	public AnalyzeAssistor(boolean useSmart,boolean useDataBase){
		ik = new IKSegmenter(useSmart, useDataBase?new DBConfig() : new DefaultConfig());
	}
	
	public List<String> getWords(String str) {
		List<String> result = new ArrayList<String>();
		// 独立Lucene实现
		StringReader re = new StringReader(str);
		ik.setInput(re);
		Lexeme lex = null;
		try {
			while ((lex = ik.next()) != null) {
				result.add(lex.getLexemeText());
			}
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}

		return result;
	}
	
	public static void main(String[] args) {
		String str = "中海油 成品油 市场 ";
		AnalyzeAssistor ik = new AnalyzeAssistor(true, true);
		System.out.println(ik.getWords(str));
		System.out.println(ik.getWords(str));
	}
}
