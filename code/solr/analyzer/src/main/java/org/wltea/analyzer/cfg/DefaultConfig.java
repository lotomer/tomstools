/*     */ package org.wltea.analyzer.cfg;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.InvalidPropertiesFormatException;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class DefaultConfig
/*     */   implements Configuration
/*     */ {
/*     */   private static final String PATH_DIC_MAIN = "org/wltea/analyzer/dic/main2012.dic";
/*     */   private static final String PATH_DIC_QUANTIFIER = "org/wltea/analyzer/dic/quantifier.dic";
/*     */   private static final String FILE_NAME = "IKAnalyzer.cfg.xml";
/*     */   private static final String EXT_DICT = "ext_dict";
/*     */   private static final String EXT_STOP = "ext_stopwords";
/*     */   private Properties props;
/*     */   private boolean useSmart;
/*     */ 
/*     */   public static Configuration getInstance()
/*     */   {
/*  67 */     return new DefaultConfig();
/*     */   }
/*     */ 
/*     */   private DefaultConfig()
/*     */   {
/*  74 */     this.props = new Properties();
/*     */ 
/*  76 */     InputStream input = getClass().getClassLoader().getResourceAsStream("IKAnalyzer.cfg.xml");
/*  77 */     if (input != null)
/*     */       try {
/*  79 */         this.props.loadFromXML(input);
/*     */       } catch (InvalidPropertiesFormatException e) {
/*  81 */         e.printStackTrace();
/*     */       } catch (IOException e) {
/*  83 */         e.printStackTrace();
/*     */       }
/*     */   }
/*     */ 
/*     */   public boolean useSmart()
/*     */   {
/*  95 */     return this.useSmart;
/*     */   }
/*     */ 
/*     */   public void setUseSmart(boolean useSmart)
/*     */   {
/* 104 */     this.useSmart = useSmart;
/*     */   }
/*     */ 
/*     */   public String getMainDictionary()
/*     */   {
/* 113 */     return "org/wltea/analyzer/dic/main2012.dic";
/*     */   }
/*     */ 
/*     */   public String getQuantifierDicionary()
/*     */   {
/* 121 */     return "org/wltea/analyzer/dic/quantifier.dic";
/*     */   }
/*     */ 
/*     */   public List<String> getExtDictionarys()
/*     */   {
/* 129 */     List extDictFiles = new ArrayList(2);
/* 130 */     String extDictCfg = this.props.getProperty("ext_dict");
/* 131 */     if (extDictCfg != null)
/*     */     {
/* 133 */       String[] filePaths = extDictCfg.split(";");
/* 134 */       if (filePaths != null) {
/* 135 */         for (String filePath : filePaths) {
/* 136 */           if ((filePath != null) && (!"".equals(filePath.trim()))) {
/* 137 */             extDictFiles.add(filePath.trim());
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 142 */     return extDictFiles;
/*     */   }
/*     */ 
/*     */   public List<String> getExtStopWordDictionarys()
/*     */   {
/* 151 */     List extStopWordDictFiles = new ArrayList(2);
/* 152 */     String extStopWordDictCfg = this.props.getProperty("ext_stopwords");
/* 153 */     if (extStopWordDictCfg != null)
/*     */     {
/* 155 */       String[] filePaths = extStopWordDictCfg.split(";");
/* 156 */       if (filePaths != null) {
/* 157 */         for (String filePath : filePaths) {
/* 158 */           if ((filePath != null) && (!"".equals(filePath.trim()))) {
/* 159 */             extStopWordDictFiles.add(filePath.trim());
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 164 */     return extStopWordDictFiles;
/*     */   }
/*     */ }

/* Location:           G:\临时目录\BaiduYunDownload\IKAnalyzer.jar
 * Qualified Name:     org.wltea.analyzer.cfg.DefaultConfig
 * JD-Core Version:    0.6.2
 */