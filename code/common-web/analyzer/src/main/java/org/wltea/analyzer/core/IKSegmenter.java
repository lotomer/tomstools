package org.wltea.analyzer.core;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.dic.Dictionary;

public final class IKSegmenter {
	private Reader input;
	private Configuration cfg;
	private AnalyzeContext context;
	private List<ISegmenter> segmenters;
	private IKArbitrator arbitrator;

	public IKSegmenter(Reader input, boolean useSmart) {
		this.input = input;
		this.cfg = DefaultConfig.getInstance();
		this.cfg.setUseSmart(useSmart);
		init();
	}

	public IKSegmenter(Reader input, Configuration cfg) {
		this.input = input;
		this.cfg = cfg;
		init();
	}

	private void init() {
		//使用重新加载功能，方便中途重新加载数据
		//Dictionary.initial(this.cfg);
		Dictionary.reload(this.cfg);

		this.context = new AnalyzeContext(this.cfg);

		this.segmenters = loadSegmenters();

		this.arbitrator = new IKArbitrator();
	}

	private List<ISegmenter> loadSegmenters() {
		List<ISegmenter> segmenters = new ArrayList<ISegmenter>(4);

		segmenters.add(new LetterSegmenter());

		segmenters.add(new CN_QuantifierSegmenter());

		segmenters.add(new CJKSegmenter());
		return segmenters;
	}

	public synchronized Lexeme next() throws IOException {
		Lexeme l = null;

		while ((l = this.context.getNextLexeme()) == null) {
			int available = this.context.fillBuffer(this.input);
			if (available <= 0) {
				this.context.reset();
				return null;
			}

			this.context.initCursor();
			do {
				for (ISegmenter segmenter : this.segmenters) {
					segmenter.analyze(this.context);
				}
			} while ((!this.context.needRefillBuffer()) && (this.context.moveCursor()));

			for (ISegmenter segmenter : this.segmenters) {
				segmenter.reset();
			}

			this.arbitrator.process(this.context, this.cfg.useSmart());

			this.context.outputToResult();

			this.context.markBufferOffset();
		}

		return l;
	}

	public synchronized void reset(Reader input) {
		this.input = input;
		this.context.reset();
		for (ISegmenter segmenter : this.segmenters)
			segmenter.reset();
	}
}
