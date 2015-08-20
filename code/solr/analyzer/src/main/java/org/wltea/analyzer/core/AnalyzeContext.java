/*     */ package org.wltea.analyzer.core;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Map;
/*     */ import java.util.Set;

/*     */ import org.wltea.analyzer.cfg.Configuration;
/*     */ import org.wltea.analyzer.dic.Dictionary;
/*     */ 
/*     */ class AnalyzeContext
/*     */ {
/*     */   private static final int BUFF_SIZE = 4096;
/*     */   private static final int BUFF_EXHAUST_CRITICAL = 100;
/*     */   private char[] segmentBuff;
/*     */   private int[] charTypes;
/*     */   private int buffOffset;
/*     */   private int cursor;
/*     */   private int available;
/*     */   private Set<String> buffLocker;
/*     */   private QuickSortSet orgLexemes;
/*     */   private Map<Integer, LexemePath> pathMap;
/*     */   private LinkedList<Lexeme> results;
/*     */   private Configuration cfg;
/*     */ 
/*     */   public AnalyzeContext(Configuration cfg)
/*     */   {
/*  81 */     this.cfg = cfg;
/*  82 */     this.segmentBuff = new char[BUFF_SIZE];
/*  83 */     this.charTypes = new int[BUFF_SIZE];
/*  84 */     this.buffLocker = new HashSet<String>();
/*  85 */     this.orgLexemes = new QuickSortSet();
/*  86 */     this.pathMap = new HashMap<Integer, LexemePath>();
/*  87 */     this.results = new LinkedList<Lexeme>();
/*     */   }
/*     */ 
/*     */   int getCursor() {
/*  91 */     return this.cursor;
/*     */   }
/*     */ 
/*     */   char[] getSegmentBuff()
/*     */   {
/*  99 */     return this.segmentBuff;
/*     */   }
/*     */ 
/*     */   char getCurrentChar() {
/* 103 */     return this.segmentBuff[this.cursor];
/*     */   }
/*     */ 
/*     */   int getCurrentCharType() {
/* 107 */     return this.charTypes[this.cursor];
/*     */   }
/*     */ 
/*     */   int getBufferOffset() {
/* 111 */     return this.buffOffset;
/*     */   }
/*     */ 
/*     */   int fillBuffer(Reader reader)
/*     */     throws IOException
/*     */   {
/* 121 */     int readCount = 0;
/* 122 */     if (this.buffOffset == 0)
/*     */     {
/* 124 */       readCount = reader.read(this.segmentBuff);
/*     */     } else {
/* 126 */       int offset = this.available - this.cursor;
/* 127 */       if (offset > 0)
/*     */       {
/* 129 */         System.arraycopy(this.segmentBuff, this.cursor, this.segmentBuff, 0, offset);
/* 130 */         readCount = offset;
/*     */       }
/*     */ 
/* 133 */       readCount += reader.read(this.segmentBuff, offset, BUFF_SIZE - offset);
/*     */     }
/*     */ 
/* 136 */     this.available = readCount;
/*     */ 
/* 138 */     this.cursor = 0;
/* 139 */     return readCount;
/*     */   }
/*     */ 
/*     */   void initCursor()
/*     */   {
/* 146 */     this.cursor = 0;
/* 147 */     this.segmentBuff[this.cursor] = CharacterUtil.regularize(this.segmentBuff[this.cursor]);
/* 148 */     this.charTypes[this.cursor] = CharacterUtil.identifyCharType(this.segmentBuff[this.cursor]);
/*     */   }
/*     */ 
/*     */   boolean moveCursor()
/*     */   {
/* 157 */     if (this.cursor < this.available - 1) {
/* 158 */       this.cursor += 1;
/* 159 */       this.segmentBuff[this.cursor] = CharacterUtil.regularize(this.segmentBuff[this.cursor]);
/* 160 */       this.charTypes[this.cursor] = CharacterUtil.identifyCharType(this.segmentBuff[this.cursor]);
/* 161 */       return true;
/*     */     }
/* 163 */     return false;
/*     */   }
/*     */ 
/*     */   void lockBuffer(String segmenterName)
/*     */   {
/* 173 */     this.buffLocker.add(segmenterName);
/*     */   }
/*     */ 
/*     */   void unlockBuffer(String segmenterName)
/*     */   {
/* 181 */     this.buffLocker.remove(segmenterName);
/*     */   }
/*     */ 
/*     */   boolean isBufferLocked()
/*     */   {
/* 190 */     return this.buffLocker.size() > 0;
/*     */   }
/*     */ 
/*     */   boolean isBufferConsumed()
/*     */   {
/* 199 */     return this.cursor == this.available - 1;
/*     */   }
/*     */ 
/*     */   boolean needRefillBuffer()
/*     */   {
/* 216 */     return (this.available == BUFF_SIZE) && 
/* 214 */       (this.cursor < this.available - 1) && 
/* 215 */       (this.cursor > this.available - BUFF_EXHAUST_CRITICAL) && 
/* 216 */       (!isBufferLocked());
/*     */   }
/*     */ 
/*     */   void markBufferOffset()
/*     */   {
/* 223 */     this.buffOffset += this.cursor;
/*     */   }
/*     */ 
/*     */   void addLexeme(Lexeme lexeme)
/*     */   {
/* 231 */     this.orgLexemes.addLexeme(lexeme);
/*     */   }
/*     */ 
/*     */   void addLexemePath(LexemePath path)
/*     */   {
/* 240 */     if (path != null)
/* 241 */       this.pathMap.put(Integer.valueOf(path.getPathBegin()), path);
/*     */   }
/*     */ 
/*     */   QuickSortSet getOrgLexemes()
/*     */   {
/* 251 */     return this.orgLexemes;
/*     */   }
/*     */ 
/*     */   void outputToResult()
/*     */   {
/* 258 */     int index = 0;
/* 259 */     while (index <= this.cursor)
/*     */     {
/* 261 */       if (this.charTypes[index] == 0) {
/* 262 */         index++;
/*     */       }
/*     */       else
/*     */       {
/* 266 */         LexemePath path = (LexemePath)this.pathMap.get(Integer.valueOf(index));
/* 267 */         if (path != null)
/*     */         {
/* 269 */           Lexeme l = path.pollFirst();
/* 270 */           while (l != null) {
/* 271 */             this.results.add(l);
/*     */ 
/* 273 */             index = l.getBegin() + l.getLength();
/* 274 */             l = path.pollFirst();
/* 275 */             if (l != null)
/*     */             {
/* 277 */               for (; index < l.getBegin(); index++)
/* 278 */                 outputSingleCJK(index);
/*     */             }
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 284 */           outputSingleCJK(index);
/* 285 */           index++;
/*     */         }
/*     */       }
/*     */     }
/* 289 */     this.pathMap.clear();
/*     */   }
/*     */ 
/*     */   private void outputSingleCJK(int index)
/*     */   {
/* 297 */     if (4 == this.charTypes[index]) {
/* 298 */       Lexeme singleCharLexeme = new Lexeme(this.buffOffset, index, 1, 64);
/* 299 */       this.results.add(singleCharLexeme);
/* 300 */     } else if (8 == this.charTypes[index]) {
/* 301 */       Lexeme singleCharLexeme = new Lexeme(this.buffOffset, index, 1, 8);
/* 302 */       this.results.add(singleCharLexeme);
/*     */     }
/*     */   }
/*     */ 
/*     */   Lexeme getNextLexeme()
/*     */   {
/* 314 */     Lexeme result = (Lexeme)this.results.pollFirst();
/* 315 */     while (result != null)
/*     */     {
/* 317 */       compound(result);
/* 318 */       if (Dictionary.getSingleton().isStopWord(this.segmentBuff, result.getBegin(), result.getLength()))
/*     */       {
/* 320 */         result = (Lexeme)this.results.pollFirst();
/*     */       }
/*     */       else {
/* 323 */         result.setLexemeText(String.valueOf(this.segmentBuff, result.getBegin(), result.getLength()));
/* 324 */         break;
/*     */       }
/*     */     }
/* 327 */     return result;
/*     */   }
/*     */ 
/*     */   void reset()
/*     */   {
/* 334 */     this.buffLocker.clear();
/* 335 */     this.orgLexemes = new QuickSortSet();
/* 336 */     this.available = 0;
/* 337 */     this.buffOffset = 0;
/* 338 */     this.charTypes = new int[BUFF_SIZE];
/* 339 */     this.cursor = 0;
/* 340 */     this.results.clear();
/* 341 */     this.segmentBuff = new char[BUFF_SIZE];
/* 342 */     this.pathMap.clear();
/*     */   }
/*     */ 
/*     */   private void compound(Lexeme result)
/*     */   {
/* 349 */     if (!this.cfg.useSmart()) {
/* 350 */       return;
/*     */     }
/*     */ 
/* 353 */     if (!this.results.isEmpty())
/*     */     {
/* 355 */       if (2 == result.getLexemeType()) {
/* 356 */         Lexeme nextLexeme = (Lexeme)this.results.peekFirst();
/* 357 */         boolean appendOk = false;
/* 358 */         if (16 == nextLexeme.getLexemeType())
/*     */         {
/* 360 */           appendOk = result.append(nextLexeme, 16);
/* 361 */         } else if (32 == nextLexeme.getLexemeType())
/*     */         {
/* 363 */           appendOk = result.append(nextLexeme, 48);
/*     */         }
/* 365 */         if (appendOk)
/*     */         {
/* 367 */           this.results.pollFirst();
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 372 */       if ((16 == result.getLexemeType()) && (!this.results.isEmpty())) {
/* 373 */         Lexeme nextLexeme = (Lexeme)this.results.peekFirst();
/* 374 */         boolean appendOk = false;
/* 375 */         if (32 == nextLexeme.getLexemeType())
/*     */         {
/* 377 */           appendOk = result.append(nextLexeme, 48);
/*     */         }
/* 379 */         if (appendOk)
/*     */         {
/* 381 */           this.results.pollFirst();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\临时目录\BaiduYunDownload\IKAnalyzer.jar
 * Qualified Name:     org.wltea.analyzer.core.AnalyzeContext
 * JD-Core Version:    0.6.2
 */