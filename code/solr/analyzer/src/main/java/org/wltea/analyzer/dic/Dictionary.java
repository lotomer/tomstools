package org.wltea.analyzer.dic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;

import org.wltea.analyzer.cfg.Configuration;

public class Dictionary {
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
	public static void reload(Configuration cfg) {
		synchronized (Dictionary.class) {
			singleton = new Dictionary(cfg);
		}
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

		InputStream is = getClass().getClassLoader().getResourceAsStream(this.cfg.getMainDictionary());
		if (is == null) {
			throw new RuntimeException("Main Dictionary not found!!!");
		}
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"), 512);
			String theWord = null;
			do {
				theWord = br.readLine();
				if ((theWord != null) && (!"".equals(theWord.trim())))
					this._MainDict.fillSegment(theWord.trim().toLowerCase().toCharArray());
			} while (theWord != null);
		} catch (IOException ioe) {
			System.err.println("Main Dictionary loading exception.");
			ioe.printStackTrace();
			try {
				if (is != null) {
					is.close();
					is = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} finally {
			try {
				if (is != null) {
					is.close();
					is = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		loadExtDict();
	}

	private void loadExtDict() {
		List<String> extDictFiles = this.cfg.getExtDictionarys();
		if (extDictFiles != null) {
			InputStream is = null;
			for (String extDictName : extDictFiles) {
				System.out.println("加载扩展词典：" + extDictName);
				is = getClass().getClassLoader().getResourceAsStream(extDictName);

				if (is != null) {
					try {
						BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"), 512);
						String theWord = null;
						do {
							theWord = br.readLine();
							if ((theWord != null) && (!"".equals(theWord.trim()))) {
								this._MainDict.fillSegment(theWord.trim().toLowerCase().toCharArray());
							}
						} while (theWord != null);
					} catch (IOException ioe) {
						System.err.println("Extension Dictionary loading exception.");
						ioe.printStackTrace();
						try {
							if (is != null) {
								is.close();
								is = null;
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					} finally {
						try {
							if (is != null) {
								is.close();
								is = null;
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	private void loadStopWordDict() {
		this._StopWordDict = new DictSegment(Character.valueOf('\000'));

		List<String> extStopWordDictFiles = this.cfg.getExtStopWordDictionarys();
		if (extStopWordDictFiles != null) {
			InputStream is = null;
			for (String extStopWordDictName : extStopWordDictFiles) {
				System.out.println("加载扩展停止词典：" + extStopWordDictName);

				is = getClass().getClassLoader().getResourceAsStream(extStopWordDictName);

				if (is != null) {
					try {
						BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"), 512);
						String theWord = null;
						do {
							theWord = br.readLine();
							if ((theWord != null) && (!"".equals(theWord.trim()))) {
								this._StopWordDict.fillSegment(theWord.trim().toLowerCase().toCharArray());
							}
						} while (theWord != null);
					} catch (IOException ioe) {
						System.err.println("Extension Stop word Dictionary loading exception.");
						ioe.printStackTrace();
						try {
							if (is != null) {
								is.close();
								is = null;
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					} finally {
						try {
							if (is != null) {
								is.close();
								is = null;
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	private void loadQuantifierDict() {
		this._QuantifierDict = new DictSegment(Character.valueOf('\000'));

		InputStream is = getClass().getClassLoader().getResourceAsStream(this.cfg.getQuantifierDicionary());
		if (is == null)
			throw new RuntimeException("Quantifier Dictionary not found!!!");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"), 512);
			String theWord = null;
			do {
				theWord = br.readLine();
				if ((theWord != null) && (!"".equals(theWord.trim())))
					this._QuantifierDict.fillSegment(theWord.trim().toLowerCase().toCharArray());
			} while (theWord != null);
		} catch (IOException ioe) {
			System.err.println("Quantifier Dictionary loading exception.");
			ioe.printStackTrace();
			try {
				if (is != null) {
					is.close();
					is = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} finally {
			try {
				if (is != null) {
					is.close();
					is = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
