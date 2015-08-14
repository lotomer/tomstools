/*     */ package org.wltea.analyzer.core;
/*     */ 
/*     */ class LexemePath extends QuickSortSet
/*     */   implements Comparable<LexemePath>
/*     */ {
/*     */   private int pathBegin;
/*     */   private int pathEnd;
/*     */   private int payloadLength;
/*     */ 
/*     */   LexemePath()
/*     */   {
/*  41 */     this.pathBegin = -1;
/*  42 */     this.pathEnd = -1;
/*  43 */     this.payloadLength = 0;
/*     */   }
/*     */ 
/*     */   boolean addCrossLexeme(Lexeme lexeme)
/*     */   {
/*  52 */     if (isEmpty()) {
/*  53 */       addLexeme(lexeme);
/*  54 */       this.pathBegin = lexeme.getBegin();
/*  55 */       this.pathEnd = (lexeme.getBegin() + lexeme.getLength());
/*  56 */       this.payloadLength += lexeme.getLength();
/*  57 */       return true;
/*     */     }
/*  59 */     if (checkCross(lexeme)) {
/*  60 */       addLexeme(lexeme);
/*  61 */       if (lexeme.getBegin() + lexeme.getLength() > this.pathEnd) {
/*  62 */         this.pathEnd = (lexeme.getBegin() + lexeme.getLength());
/*     */       }
/*  64 */       this.payloadLength = (this.pathEnd - this.pathBegin);
/*  65 */       return true;
/*     */     }
/*     */ 
/*  68 */     return false;
/*     */   }
/*     */ 
/*     */   boolean addNotCrossLexeme(Lexeme lexeme)
/*     */   {
/*  79 */     if (isEmpty()) {
/*  80 */       addLexeme(lexeme);
/*  81 */       this.pathBegin = lexeme.getBegin();
/*  82 */       this.pathEnd = (lexeme.getBegin() + lexeme.getLength());
/*  83 */       this.payloadLength += lexeme.getLength();
/*  84 */       return true;
/*     */     }
/*  86 */     if (checkCross(lexeme)) {
/*  87 */       return false;
/*     */     }
/*     */ 
/*  90 */     addLexeme(lexeme);
/*  91 */     this.payloadLength += lexeme.getLength();
/*  92 */     Lexeme head = peekFirst();
/*  93 */     this.pathBegin = head.getBegin();
/*  94 */     Lexeme tail = peekLast();
/*  95 */     this.pathEnd = (tail.getBegin() + tail.getLength());
/*  96 */     return true;
/*     */   }
/*     */ 
/*     */   Lexeme removeTail()
/*     */   {
/* 106 */     Lexeme tail = pollLast();
/* 107 */     if (isEmpty()) {
/* 108 */       this.pathBegin = -1;
/* 109 */       this.pathEnd = -1;
/* 110 */       this.payloadLength = 0;
/*     */     } else {
/* 112 */       this.payloadLength -= tail.getLength();
/* 113 */       Lexeme newTail = peekLast();
/* 114 */       this.pathEnd = (newTail.getBegin() + newTail.getLength());
/*     */     }
/* 116 */     return tail;
/*     */   }
/*     */ 
/*     */   boolean checkCross(Lexeme lexeme)
/*     */   {
/* 126 */     return ((lexeme.getBegin() >= this.pathBegin) && (lexeme.getBegin() < this.pathEnd)) || (
/* 126 */       (this.pathBegin >= lexeme.getBegin()) && (this.pathBegin < lexeme.getBegin() + lexeme.getLength()));
/*     */   }
/*     */ 
/*     */   int getPathBegin() {
/* 130 */     return this.pathBegin;
/*     */   }
/*     */ 
/*     */   int getPathEnd() {
/* 134 */     return this.pathEnd;
/*     */   }
/*     */ 
/*     */   int getPayloadLength()
/*     */   {
/* 142 */     return this.payloadLength;
/*     */   }
/*     */ 
/*     */   int getPathLength()
/*     */   {
/* 150 */     return this.pathEnd - this.pathBegin;
/*     */   }
/*     */ 
/*     */   int getXWeight()
/*     */   {
/* 159 */     int product = 1;
/* 160 */     QuickSortSet.Cell c = getHead();
/* 161 */     while ((c != null) && (c.getLexeme() != null)) {
/* 162 */       product *= c.getLexeme().getLength();
/* 163 */       c = c.getNext();
/*     */     }
/* 165 */     return product;
/*     */   }
/*     */ 
/*     */   int getPWeight()
/*     */   {
/* 173 */     int pWeight = 0;
/* 174 */     int p = 0;
/* 175 */     QuickSortSet.Cell c = getHead();
/* 176 */     while ((c != null) && (c.getLexeme() != null)) {
/* 177 */       p++;
/* 178 */       pWeight += p * c.getLexeme().getLength();
/* 179 */       c = c.getNext();
/*     */     }
/* 181 */     return pWeight;
/*     */   }
/*     */ 
/*     */   LexemePath copy() {
/* 185 */     LexemePath theCopy = new LexemePath();
/* 186 */     theCopy.pathBegin = this.pathBegin;
/* 187 */     theCopy.pathEnd = this.pathEnd;
/* 188 */     theCopy.payloadLength = this.payloadLength;
/* 189 */     QuickSortSet.Cell c = getHead();
/* 190 */     while ((c != null) && (c.getLexeme() != null)) {
/* 191 */       theCopy.addLexeme(c.getLexeme());
/* 192 */       c = c.getNext();
/*     */     }
/* 194 */     return theCopy;
/*     */   }
/*     */ 
/*     */   public int compareTo(LexemePath o)
/*     */   {
/* 199 */     if (this.payloadLength > o.payloadLength)
/* 200 */       return -1;
/* 201 */     if (this.payloadLength < o.payloadLength) {
/* 202 */       return 1;
/*     */     }
/*     */ 
/* 205 */     if (size() < o.size())
/* 206 */       return -1;
/* 207 */     if (size() > o.size()) {
/* 208 */       return 1;
/*     */     }
/*     */ 
/* 211 */     if (getPathLength() > o.getPathLength())
/* 212 */       return -1;
/* 213 */     if (getPathLength() < o.getPathLength()) {
/* 214 */       return 1;
/*     */     }
/*     */ 
/* 217 */     if (this.pathEnd > o.pathEnd)
/* 218 */       return -1;
/* 219 */     if (this.pathEnd < o.pathEnd) {
/* 220 */       return 1;
/*     */     }
/*     */ 
/* 223 */     if (getXWeight() > o.getXWeight())
/* 224 */       return -1;
/* 225 */     if (getXWeight() < o.getXWeight()) {
/* 226 */       return 1;
/*     */     }
/*     */ 
/* 229 */     if (getPWeight() > o.getPWeight())
/* 230 */       return -1;
/* 231 */     if (getPWeight() < o.getPWeight()) {
/* 232 */       return 1;
/*     */     }
/*     */ 
/* 240 */     return 0;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 244 */     StringBuffer sb = new StringBuffer();
/* 245 */     sb.append("pathBegin  : ").append(this.pathBegin).append("\r\n");
/* 246 */     sb.append("pathEnd  : ").append(this.pathEnd).append("\r\n");
/* 247 */     sb.append("payloadLength  : ").append(this.payloadLength).append("\r\n");
/* 248 */     QuickSortSet.Cell head = getHead();
/* 249 */     while (head != null) {
/* 250 */       sb.append("lexeme : ").append(head.getLexeme()).append("\r\n");
/* 251 */       head = head.getNext();
/*     */     }
/* 253 */     return sb.toString();
/*     */   }
/*     */ }

/* Location:           G:\临时目录\BaiduYunDownload\IKAnalyzer.jar
 * Qualified Name:     org.wltea.analyzer.core.LexemePath
 * JD-Core Version:    0.6.2
 */