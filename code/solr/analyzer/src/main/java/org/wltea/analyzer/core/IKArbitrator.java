/*     */ package org.wltea.analyzer.core;
/*     */ 
/*     */ import java.util.Stack;
/*     */ import java.util.TreeSet;
/*     */ 
/*     */ class IKArbitrator
/*     */ {
/*     */   void process(AnalyzeContext context, boolean useSmart)
/*     */   {
/*  45 */     QuickSortSet orgLexemes = context.getOrgLexemes();
/*  46 */     Lexeme orgLexeme = orgLexemes.pollFirst();
/*     */ 
/*  48 */     LexemePath crossPath = new LexemePath();
/*  49 */     while (orgLexeme != null) {
/*  50 */       if (!crossPath.addCrossLexeme(orgLexeme))
/*     */       {
/*  52 */         if ((crossPath.size() == 1) || (!useSmart))
/*     */         {
/*  55 */           context.addLexemePath(crossPath);
/*     */         }
/*     */         else {
/*  58 */           QuickSortSet.Cell headCell = crossPath.getHead();
/*  59 */           LexemePath judgeResult = judge(headCell, crossPath.getPathLength());
/*     */ 
/*  61 */           context.addLexemePath(judgeResult);
/*     */         }
/*     */ 
/*  65 */         crossPath = new LexemePath();
/*  66 */         crossPath.addCrossLexeme(orgLexeme);
/*     */       }
/*  68 */       orgLexeme = orgLexemes.pollFirst();
/*     */     }
/*     */ 
/*  73 */     if ((crossPath.size() == 1) || (!useSmart))
/*     */     {
/*  76 */       context.addLexemePath(crossPath);
/*     */     }
/*     */     else {
/*  79 */       QuickSortSet.Cell headCell = crossPath.getHead();
/*  80 */       LexemePath judgeResult = judge(headCell, crossPath.getPathLength());
/*     */ 
/*  82 */       context.addLexemePath(judgeResult);
/*     */     }
/*     */   }
/*     */ 
/*     */   private LexemePath judge(QuickSortSet.Cell lexemeCell, int fullTextLength)
/*     */   {
/*  95 */     TreeSet pathOptions = new TreeSet();
/*     */ 
/*  97 */     LexemePath option = new LexemePath();
/*     */ 
/* 100 */     Stack lexemeStack = forwardPath(lexemeCell, option);
/*     */ 
/* 103 */     pathOptions.add(option.copy());
/*     */ 
/* 106 */     QuickSortSet.Cell c = null;
/* 107 */     while (!lexemeStack.isEmpty()) {
/* 108 */       c = (QuickSortSet.Cell)lexemeStack.pop();
/*     */ 
/* 110 */       backPath(c.getLexeme(), option);
/*     */ 
/* 112 */       forwardPath(c, option);
/* 113 */       pathOptions.add(option.copy());
/*     */     }
/*     */ 
/* 117 */     return (LexemePath)pathOptions.first();
/*     */   }
/*     */ 
/*     */   private Stack<QuickSortSet.Cell> forwardPath(QuickSortSet.Cell lexemeCell, LexemePath option)
/*     */   {
/* 128 */     Stack conflictStack = new Stack();
/* 129 */     QuickSortSet.Cell c = lexemeCell;
/*     */ 
/* 131 */     while ((c != null) && (c.getLexeme() != null)) {
/* 132 */       if (!option.addNotCrossLexeme(c.getLexeme()))
/*     */       {
/* 134 */         conflictStack.push(c);
/*     */       }
/* 136 */       c = c.getNext();
/*     */     }
/* 138 */     return conflictStack;
/*     */   }
/*     */ 
/*     */   private void backPath(Lexeme l, LexemePath option)
/*     */   {
/* 147 */     while (option.checkCross(l))
/* 148 */       option.removeTail();
/*     */   }
/*     */ }

/* Location:           G:\临时目录\BaiduYunDownload\IKAnalyzer.jar
 * Qualified Name:     org.wltea.analyzer.core.IKArbitrator
 * JD-Core Version:    0.6.2
 */