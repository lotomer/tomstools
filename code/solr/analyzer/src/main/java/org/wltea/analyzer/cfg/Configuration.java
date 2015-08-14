package org.wltea.analyzer.cfg;

import java.util.List;

public abstract interface Configuration
{
  public abstract boolean useSmart();

  public abstract void setUseSmart(boolean paramBoolean);

  public abstract String getMainDictionary();

  public abstract String getQuantifierDicionary();

  public abstract List<String> getExtDictionarys();

  public abstract List<String> getExtStopWordDictionarys();
}

/* Location:           G:\临时目录\BaiduYunDownload\IKAnalyzer.jar
 * Qualified Name:     org.wltea.analyzer.cfg.Configuration
 * JD-Core Version:    0.6.2
 */