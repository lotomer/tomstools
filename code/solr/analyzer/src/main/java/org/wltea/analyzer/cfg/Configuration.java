package org.wltea.analyzer.cfg;

import java.util.List;

public abstract interface Configuration
{
  public abstract boolean useSmart();

  public abstract void setUseSmart(boolean paramBoolean);

  public abstract List<String> getMainDictionary();

  public abstract List<String> getQuantifierDicionary();

  /**
   * 获取具体的字典列表
   * @return 具体的字典列表。而不是文件名列表
   */
  public abstract List<String> getExtDictionarys();

  /**
   * 获取扩展停用词列表
   * @return 具体的停用词列表，而不是文件名列表
   */
  public abstract List<String> getExtStopWordDictionarys();
}
