/*     */ package org.wltea.analyzer.core;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.wltea.analyzer.dic.Dictionary;
/*     */ import org.wltea.analyzer.dic.Hit;
/*     */ 
/*     */ class CN_QuantifierSegmenter
/*     */   implements ISegmenter
/*     */ {
/*     */   static final String SEGMENTER_NAME = "QUAN_SEGMENTER";
/*  45 */   private static String Chn_Num = "一二两三四五六七八九十零壹贰叁肆伍陆柒捌玖拾百千万亿拾佰仟萬億兆卅廿";
/*  46 */   private static Set<Character> ChnNumberChars = new HashSet();
/*     */   private int nStart;
/*     */   private int nEnd;
/*     */   private List<Hit> countHits;
/*     */ 
/*     */   static
/*     */   {
/*  48 */     char[] ca = Chn_Num.toCharArray();
/*  49 */     char[] arrayOfChar1 = ca; int j = ca.length; for (int i = 0; i < j; i++) { char nChar = arrayOfChar1[i];
/*  50 */       ChnNumberChars.add(Character.valueOf(nChar));
/*     */     }
/*     */   }
/*     */ 
/*     */   CN_QuantifierSegmenter()
/*     */   {
/*  71 */     this.nStart = -1;
/*  72 */     this.nEnd = -1;
/*  73 */     this.countHits = new LinkedList();
/*     */   }
/*     */ 
/*     */   public void analyze(AnalyzeContext context)
/*     */   {
/*  81 */     processCNumber(context);
/*     */ 
/*  83 */     processCount(context);
/*     */ 
/*  86 */     if ((this.nStart == -1) && (this.nEnd == -1) && (this.countHits.isEmpty()))
/*     */     {
/*  88 */       context.unlockBuffer("QUAN_SEGMENTER");
/*     */     }
/*  90 */     else context.lockBuffer("QUAN_SEGMENTER");
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/*  99 */     this.nStart = -1;
/* 100 */     this.nEnd = -1;
/* 101 */     this.countHits.clear();
/*     */   }
/*     */ 
/*     */   private void processCNumber(AnalyzeContext context)
/*     */   {
/* 108 */     if ((this.nStart == -1) && (this.nEnd == -1)) {
/* 109 */       if ((4 == context.getCurrentCharType()) && 
/* 110 */         (ChnNumberChars.contains(Character.valueOf(context.getCurrentChar()))))
/*     */       {
/* 112 */         this.nStart = context.getCursor();
/* 113 */         this.nEnd = context.getCursor();
/*     */       }
/*     */     }
/* 116 */     else if ((4 == context.getCurrentCharType()) && 
/* 117 */       (ChnNumberChars.contains(Character.valueOf(context.getCurrentChar()))))
/*     */     {
/* 119 */       this.nEnd = context.getCursor();
/*     */     }
/*     */     else {
/* 122 */       outputNumLexeme(context);
/*     */ 
/* 124 */       this.nStart = -1;
/* 125 */       this.nEnd = -1;
/*     */     }
/*     */ 
/* 130 */     if ((context.isBufferConsumed()) && 
/* 131 */       (this.nStart != -1) && (this.nEnd != -1))
/*     */     {
/* 133 */       outputNumLexeme(context);
/*     */ 
/* 135 */       this.nStart = -1;
/* 136 */       this.nEnd = -1;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void processCount(AnalyzeContext context)
/*     */   {
/* 147 */     if (!needCountScan(context)) {
/* 148 */       return;
/*     */     }
/*     */ 
/* 151 */     if (4 == context.getCurrentCharType())
/*     */     {
/* 154 */       if (!this.countHits.isEmpty())
/*     */       {
/* 156 */         Hit[] tmpArray = (Hit[])this.countHits.toArray(new Hit[this.countHits.size()]);
/* 157 */         for (Hit hit : tmpArray) {
/* 158 */           hit = Dictionary.getSingleton().matchWithHit(context.getSegmentBuff(), context.getCursor(), hit);
/* 159 */           if (hit.isMatch())
/*     */           {
/* 161 */             Lexeme newLexeme = new Lexeme(context.getBufferOffset(), hit.getBegin(), context.getCursor() - hit.getBegin() + 1, 32);
/* 162 */             context.addLexeme(newLexeme);
/*     */ 
/* 164 */             if (!hit.isPrefix()) {
/* 165 */               this.countHits.remove(hit);
/*     */             }
/*     */           }
/* 168 */           else if (hit.isUnmatch())
/*     */           {
/* 170 */             this.countHits.remove(hit);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 177 */       Hit singleCharHit = Dictionary.getSingleton().matchInQuantifierDict(context.getSegmentBuff(), context.getCursor(), 1);
/* 178 */       if (singleCharHit.isMatch())
/*     */       {
/* 180 */         Lexeme newLexeme = new Lexeme(context.getBufferOffset(), context.getCursor(), 1, 32);
/* 181 */         context.addLexeme(newLexeme);
/*     */ 
/* 184 */         if (singleCharHit.isPrefix())
/*     */         {
/* 186 */           this.countHits.add(singleCharHit);
/*     */         }
/* 188 */       } else if (singleCharHit.isPrefix())
/*     */       {
/* 190 */         this.countHits.add(singleCharHit);
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 197 */       this.countHits.clear();
/*     */     }
/*     */ 
/* 201 */     if (context.isBufferConsumed())
/*     */     {
/* 203 */       this.countHits.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean needCountScan(AnalyzeContext context)
/*     */   {
/* 212 */     if (((this.nStart != -1) && (this.nEnd != -1)) || (!this.countHits.isEmpty()))
/*     */     {
/* 214 */       return true;
/*     */     }
/*     */ 
/* 217 */     if (!context.getOrgLexemes().isEmpty()) {
/* 218 */       Lexeme l = context.getOrgLexemes().peekLast();
/* 219 */       if (((16 == l.getLexemeType()) || (2 == l.getLexemeType())) && 
/* 220 */         (l.getBegin() + l.getLength() == context.getCursor())) {
/* 221 */         return true;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 226 */     return false;
/*     */   }
/*     */ 
/*     */   private void outputNumLexeme(AnalyzeContext context)
/*     */   {
/* 234 */     if ((this.nStart > -1) && (this.nEnd > -1))
/*     */     {
/* 236 */       Lexeme newLexeme = new Lexeme(context.getBufferOffset(), this.nStart, this.nEnd - this.nStart + 1, 16);
/* 237 */       context.addLexeme(newLexeme);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\临时目录\BaiduYunDownload\IKAnalyzer.jar
 * Qualified Name:     org.wltea.analyzer.core.CN_QuantifierSegmenter
 * JD-Core Version:    0.6.2
 */