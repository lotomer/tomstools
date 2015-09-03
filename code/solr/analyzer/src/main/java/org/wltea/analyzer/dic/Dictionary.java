package org.wltea.analyzer.dic;

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wltea.analyzer.cfg.Configuration;

public class Dictionary {
	private static final Log LOG = LogFactory.getLog(Dictionary.class);
	private static Dictionary singleton;
	private DictSegment _MainDict;
	private DictSegment _StopWordDict;
	private DictSegment _QuantifierDict;
	private Configuration cfg;

	private Dictionary(Configuration cfg) {
		this.cfg = cfg;
		loadMainDict();
		loadStopWordDict();
		loadQuantifierDict();
	}

	/**
	 * 新增重新加载功能，方便中途加入新词
	 * 
	 * @param cfg
	 */
	public static void reloadDictionary(Configuration cfg) {
		LOG.warn("Reload dictionary!");
		Dictionary self = initial(cfg);
		// 重新加载扩展词库
		self.loadExtDict();
		// 重新加载停用词
		self.loadStopWordDict();
	}

	public static Dictionary initial(Configuration cfg) {
		if (singleton == null) {
			synchronized (Dictionary.class) {
				if (singleton == null) {
					singleton = new Dictionary(cfg);
					return singleton;
				}
			}
		}
		return singleton;
	}

	public static Dictionary getSingleton() {
		if (singleton == null) {
			throw new IllegalStateException("词典尚未初始化，请先调用initial方法");
		}
		return singleton;
	}

	public void addWords(Collection<String> words) {
		if (words != null)
			for (String word : words)
				if (word != null) {
					singleton._MainDict.fillSegment(word.trim().toLowerCase().toCharArray());
				}
	}

	public void disableWords(Collection<String> words) {
		if (words != null)
			for (String word : words)
				if (word != null) {
					singleton._MainDict.disableSegment(word.trim().toLowerCase().toCharArray());
				}
	}

	public Hit matchInMainDict(char[] charArray) {
		return singleton._MainDict.match(charArray);
	}

	public Hit matchInMainDict(char[] charArray, int begin, int length) {
		return singleton._MainDict.match(charArray, begin, length);
	}

	public Hit matchInQuantifierDict(char[] charArray, int begin, int length) {
		return singleton._QuantifierDict.match(charArray, begin, length);
	}

	public Hit matchWithHit(char[] charArray, int currentIndex, Hit matchedHit) {
		DictSegment ds = matchedHit.getMatchedDictSegment();
		return ds.match(charArray, currentIndex, 1, matchedHit);
	}

	public boolean isStopWord(char[] charArray, int begin, int length) {
		return singleton._StopWordDict.match(charArray, begin, length).isMatch();
	}

	private void loadMainDict() {
		this._MainDict = new DictSegment(Character.valueOf('\000'));
		List<String> dictWords = this.cfg.getMainDictionary();
		LOG.warn("Main dicts: " + dictWords.size());
		if (null != dictWords && 0 != dictWords.size()){
			for (int i = 0; i < dictWords.size(); i++) {
				this._MainDict.fillSegment(dictWords.get(i).trim().toLowerCase().toCharArray());
			}
		}
		
		loadExtDict();
	}

	public void loadExtDict() {
		List<String> extDictWords = this.cfg.getExtDictionarys();
		LOG.warn("Ext dicts: " + extDictWords.size());
		if (null != extDictWords && 0 != extDictWords.size()){
			for (int i = 0; i < extDictWords.size(); i++) {
				this._MainDict.fillSegment(extDictWords.get(i).trim().toLowerCase().toCharArray());
			}
		}
	}

	private void loadStopWordDict() {
		this._StopWordDict = new DictSegment(Character.valueOf('\000'));

		List<String> extStopWords = this.cfg.getExtStopWordDictionarys();
		LOG.warn("Ext stopwords: " + extStopWords.size());
		if (null != extStopWords && 0 != extStopWords.size()){
			for (int i = 0; i < extStopWords.size(); i++) {
				this._StopWordDict.fillSegment(extStopWords.get(i).trim().toLowerCase().toCharArray());
			}
		}
	}
 
	private void loadQuantifierDict() {
		this._QuantifierDict = new DictSegment(Character.valueOf('\000'));
		List<String> quantifierDictWords = this.cfg.getQuantifierDicionary();
		LOG.warn("Quantifier dicts: " + quantifierDictWords.size());
		if (null != quantifierDictWords && 0 != quantifierDictWords.size()){
			for (int i = 0; i < quantifierDictWords.size(); i++) {
				this._QuantifierDict.fillSegment(quantifierDictWords.get(i).trim().toLowerCase().toCharArray());
			}
		}
	}
}
