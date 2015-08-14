/*     */ package org.wltea.analyzer.core;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ 
/*     */ class LetterSegmenter
/*     */   implements ISegmenter
/*     */ {
/*     */   static final String SEGMENTER_NAME = "LETTER_SEGMENTER";
/*  38 */   private static final char[] Letter_Connector = { '#', '&', '+', '-', '.', '@', '_' };
/*     */ 
/*  41 */   private static final char[] Num_Connector = { ',', '.' };
/*     */   private int start;
/*     */   private int end;
/*     */   private int englishStart;
/*     */   private int englishEnd;
/*     */   private int arabicStart;
/*     */   private int arabicEnd;
/*     */ 
/*     */   LetterSegmenter()
/*     */   {
/*  76 */     Arrays.sort(Letter_Connector);
/*  77 */     Arrays.sort(Num_Connector);
/*  78 */     this.start = -1;
/*  79 */     this.end = -1;
/*  80 */     this.englishStart = -1;
/*  81 */     this.englishEnd = -1;
/*  82 */     this.arabicStart = -1;
/*  83 */     this.arabicEnd = -1;
/*     */   }
/*     */ 
/*     */   public void analyze(AnalyzeContext context)
/*     */   {
/*  91 */     boolean bufferLockFlag = false;
/*     */ 
/*  93 */     bufferLockFlag = (processEnglishLetter(context)) || (bufferLockFlag);
/*     */ 
/*  95 */     bufferLockFlag = (processArabicLetter(context)) || (bufferLockFlag);
/*     */ 
/*  97 */     bufferLockFlag = (processMixLetter(context)) || (bufferLockFlag);
/*     */ 
/* 100 */     if (bufferLockFlag) {
/* 101 */       context.lockBuffer("LETTER_SEGMENTER");
/*     */     }
/*     */     else
/* 104 */       context.unlockBuffer("LETTER_SEGMENTER");
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/* 112 */     this.start = -1;
/* 113 */     this.end = -1;
/* 114 */     this.englishStart = -1;
/* 115 */     this.englishEnd = -1;
/* 116 */     this.arabicStart = -1;
/* 117 */     this.arabicEnd = -1;
/*     */   }
/*     */ 
/*     */   private boolean processMixLetter(AnalyzeContext context)
/*     */   {
/* 128 */     boolean needLock = false;
/*     */ 
/* 130 */     if (this.start == -1) {
/* 131 */       if ((1 == context.getCurrentCharType()) || 
/* 132 */         (2 == context.getCurrentCharType()))
/*     */       {
/* 134 */         this.start = context.getCursor();
/* 135 */         this.end = this.start;
/*     */       }
/*     */ 
/*     */     }
/* 139 */     else if ((1 == context.getCurrentCharType()) || 
/* 140 */       (2 == context.getCurrentCharType()))
/*     */     {
/* 142 */       this.end = context.getCursor();
/*     */     }
/* 144 */     else if ((context.getCurrentCharType() == 0) && 
/* 145 */       (isLetterConnector(context.getCurrentChar())))
/*     */     {
/* 147 */       this.end = context.getCursor();
/*     */     }
/*     */     else {
/* 150 */       Lexeme newLexeme = new Lexeme(context.getBufferOffset(), this.start, this.end - this.start + 1, 3);
/* 151 */       context.addLexeme(newLexeme);
/* 152 */       this.start = -1;
/* 153 */       this.end = -1;
/*     */     }
/*     */ 
/* 158 */     if ((context.isBufferConsumed()) && 
/* 159 */       (this.start != -1) && (this.end != -1))
/*     */     {
/* 161 */       Lexeme newLexeme = new Lexeme(context.getBufferOffset(), this.start, this.end - this.start + 1, 3);
/* 162 */       context.addLexeme(newLexeme);
/* 163 */       this.start = -1;
/* 164 */       this.end = -1;
/*     */     }
/*     */ 
/* 169 */     if ((this.start == -1) && (this.end == -1))
/*     */     {
/* 171 */       needLock = false;
/*     */     }
/* 173 */     else needLock = true;
/*     */ 
/* 175 */     return needLock;
/*     */   }
/*     */ 
/*     */   private boolean processEnglishLetter(AnalyzeContext context)
/*     */   {
/* 184 */     boolean needLock = false;
/*     */ 
/* 186 */     if (this.englishStart == -1) {
/* 187 */       if (2 == context.getCurrentCharType())
/*     */       {
/* 189 */         this.englishStart = context.getCursor();
/* 190 */         this.englishEnd = this.englishStart;
/*     */       }
/*     */     }
/* 193 */     else if (2 == context.getCurrentCharType())
/*     */     {
/* 195 */       this.englishEnd = context.getCursor();
/*     */     }
/*     */     else {
/* 198 */       Lexeme newLexeme = new Lexeme(context.getBufferOffset(), this.englishStart, this.englishEnd - this.englishStart + 1, 1);
/* 199 */       context.addLexeme(newLexeme);
/* 200 */       this.englishStart = -1;
/* 201 */       this.englishEnd = -1;
/*     */     }
/*     */ 
/* 206 */     if ((context.isBufferConsumed()) && 
/* 207 */       (this.englishStart != -1) && (this.englishEnd != -1))
/*     */     {
/* 209 */       Lexeme newLexeme = new Lexeme(context.getBufferOffset(), this.englishStart, this.englishEnd - this.englishStart + 1, 1);
/* 210 */       context.addLexeme(newLexeme);
/* 211 */       this.englishStart = -1;
/* 212 */       this.englishEnd = -1;
/*     */     }
/*     */ 
/* 217 */     if ((this.englishStart == -1) && (this.englishEnd == -1))
/*     */     {
/* 219 */       needLock = false;
/*     */     }
/* 221 */     else needLock = true;
/*     */ 
/* 223 */     return needLock;
/*     */   }
/*     */ 
/*     */   private boolean processArabicLetter(AnalyzeContext context)
/*     */   {
/* 232 */     boolean needLock = false;
/*     */ 
/* 234 */     if (this.arabicStart == -1) {
/* 235 */       if (1 == context.getCurrentCharType())
/*     */       {
/* 237 */         this.arabicStart = context.getCursor();
/* 238 */         this.arabicEnd = this.arabicStart;
/*     */       }
/*     */     }
/* 241 */     else if (1 == context.getCurrentCharType())
/*     */     {
/* 243 */       this.arabicEnd = context.getCursor();
/* 244 */     } else if ((context.getCurrentCharType() != 0) || 
/* 245 */       (!isNumConnector(context.getCurrentChar())))
/*     */     {
/* 249 */       Lexeme newLexeme = new Lexeme(context.getBufferOffset(), this.arabicStart, this.arabicEnd - this.arabicStart + 1, 2);
/* 250 */       context.addLexeme(newLexeme);
/* 251 */       this.arabicStart = -1;
/* 252 */       this.arabicEnd = -1;
/*     */     }
/*     */ 
/* 257 */     if ((context.isBufferConsumed()) && 
/* 258 */       (this.arabicStart != -1) && (this.arabicEnd != -1))
/*     */     {
/* 260 */       Lexeme newLexeme = new Lexeme(context.getBufferOffset(), this.arabicStart, this.arabicEnd - this.arabicStart + 1, 2);
/* 261 */       context.addLexeme(newLexeme);
/* 262 */       this.arabicStart = -1;
/* 263 */       this.arabicEnd = -1;
/*     */     }
/*     */ 
/* 268 */     if ((this.arabicStart == -1) && (this.arabicEnd == -1))
/*     */     {
/* 270 */       needLock = false;
/*     */     }
/* 272 */     else needLock = true;
/*     */ 
/* 274 */     return needLock;
/*     */   }
/*     */ 
/*     */   private boolean isLetterConnector(char input)
/*     */   {
/* 283 */     int index = Arrays.binarySearch(Letter_Connector, input);
/* 284 */     return index >= 0;
/*     */   }
/*     */ 
/*     */   private boolean isNumConnector(char input)
/*     */   {
/* 293 */     int index = Arrays.binarySearch(Num_Connector, input);
/* 294 */     return index >= 0;
/*     */   }
/*     */ }

/* Location:           G:\临时目录\BaiduYunDownload\IKAnalyzer.jar
 * Qualified Name:     org.wltea.analyzer.core.LetterSegmenter
 * JD-Core Version:    0.6.2
 */