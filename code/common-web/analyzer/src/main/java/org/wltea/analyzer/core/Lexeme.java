/*     */ package org.wltea.analyzer.core;
/*     */ 
/*     */ public class Lexeme
/*     */   implements Comparable<Lexeme>
/*     */ {
/*     */   public static final int TYPE_UNKNOWN = 0;
/*     */   public static final int TYPE_ENGLISH = 1;
/*     */   public static final int TYPE_ARABIC = 2;
/*     */   public static final int TYPE_LETTER = 3;
/*     */   public static final int TYPE_CNWORD = 4;
/*     */   public static final int TYPE_CNCHAR = 64;
/*     */   public static final int TYPE_OTHER_CJK = 8;
/*     */   public static final int TYPE_CNUM = 16;
/*     */   public static final int TYPE_COUNT = 32;
/*     */   public static final int TYPE_CQUAN = 48;
/*     */   private int offset;
/*     */   private int begin;
/*     */   private int length;
/*     */   private String lexemeText;
/*     */   private int lexemeType;
/*     */ 
/*     */   public Lexeme(int offset, int begin, int length, int lexemeType)
/*     */   {
/*  66 */     this.offset = offset;
/*  67 */     this.begin = begin;
/*  68 */     if (length < 0) {
/*  69 */       throw new IllegalArgumentException("length < 0");
/*     */     }
/*  71 */     this.length = length;
/*  72 */     this.lexemeType = lexemeType;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/*  81 */     if (o == null) {
/*  82 */       return false;
/*     */     }
/*     */ 
/*  85 */     if (this == o) {
/*  86 */       return true;
/*     */     }
/*     */ 
/*  89 */     if ((o instanceof Lexeme)) {
/*  90 */       Lexeme other = (Lexeme)o;
/*  91 */       if ((this.offset == other.getOffset()) && 
/*  92 */         (this.begin == other.getBegin()) && 
/*  93 */         (this.length == other.getLength())) {
/*  94 */         return true;
/*     */       }
/*  96 */       return false;
/*     */     }
/*     */ 
/*  99 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 108 */     int absBegin = getBeginPosition();
/* 109 */     int absEnd = getEndPosition();
/* 110 */     return absBegin * 37 + absEnd * 31 + absBegin * absEnd % getLength() * 11;
/*     */   }
/*     */ 
/*     */   public int compareTo(Lexeme other)
/*     */   {
/* 119 */     if (this.begin < other.getBegin())
/* 120 */       return -1;
/* 121 */     if (this.begin == other.getBegin())
/*     */     {
/* 123 */       if (this.length > other.getLength())
/* 124 */         return -1;
/* 125 */       if (this.length == other.getLength()) {
/* 126 */         return 0;
/*     */       }
/* 128 */       return 1;
/*     */     }
/*     */ 
/* 132 */     return 1;
/*     */   }
/*     */ 
/*     */   public int getOffset()
/*     */   {
/* 137 */     return this.offset;
/*     */   }
/*     */ 
/*     */   public void setOffset(int offset) {
/* 141 */     this.offset = offset;
/*     */   }
/*     */ 
/*     */   public int getBegin() {
/* 145 */     return this.begin;
/*     */   }
/*     */ 
/*     */   public int getBeginPosition()
/*     */   {
/* 152 */     return this.offset + this.begin;
/*     */   }
/*     */ 
/*     */   public void setBegin(int begin) {
/* 156 */     this.begin = begin;
/*     */   }
/*     */ 
/*     */   public int getEndPosition()
/*     */   {
/* 164 */     return this.offset + this.begin + this.length;
/*     */   }
/*     */ 
/*     */   public int getLength()
/*     */   {
/* 172 */     return this.length;
/*     */   }
/*     */ 
/*     */   public void setLength(int length) {
/* 176 */     if (this.length < 0) {
/* 177 */       throw new IllegalArgumentException("length < 0");
/*     */     }
/* 179 */     this.length = length;
/*     */   }
/*     */ 
/*     */   public String getLexemeText()
/*     */   {
/* 187 */     if (this.lexemeText == null) {
/* 188 */       return "";
/*     */     }
/* 190 */     return this.lexemeText;
/*     */   }
/*     */ 
/*     */   public void setLexemeText(String lexemeText) {
/* 194 */     if (lexemeText == null) {
/* 195 */       this.lexemeText = "";
/* 196 */       this.length = 0;
/*     */     } else {
/* 198 */       this.lexemeText = lexemeText;
/* 199 */       this.length = lexemeText.length();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getLexemeType()
/*     */   {
/* 208 */     return this.lexemeType;
/*     */   }
/*     */ 
/*     */   public void setLexemeType(int lexemeType) {
/* 212 */     this.lexemeType = lexemeType;
/*     */   }
/*     */ 
/*     */   public boolean append(Lexeme l, int lexemeType)
/*     */   {
/* 222 */     if ((l != null) && (getEndPosition() == l.getBeginPosition())) {
/* 223 */       this.length += l.getLength();
/* 224 */       this.lexemeType = lexemeType;
/* 225 */       return true;
/*     */     }
/* 227 */     return false;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 235 */     StringBuffer strbuf = new StringBuffer();
/* 236 */     strbuf.append(getBeginPosition()).append("-").append(getEndPosition());
/* 237 */     strbuf.append(" : ").append(this.lexemeText).append(" : \t");
/* 238 */     switch (this.lexemeType) {
/*     */     case 0:
/* 240 */       strbuf.append("UNKONW");
/* 241 */       break;
/*     */     case 1:
/* 243 */       strbuf.append("ENGLISH");
/* 244 */       break;
/*     */     case 2:
/* 246 */       strbuf.append("ARABIC");
/* 247 */       break;
/*     */     case 3:
/* 249 */       strbuf.append("LETTER");
/* 250 */       break;
/*     */     case 4:
/* 252 */       strbuf.append("CN_WORD");
/* 253 */       break;
/*     */     case 64:
/* 255 */       strbuf.append("CN_CHAR");
/* 256 */       break;
/*     */     case 8:
/* 258 */       strbuf.append("OTHER_CJK");
/* 259 */       break;
/*     */     case 32:
/* 261 */       strbuf.append("COUNT");
/* 262 */       break;
/*     */     case 16:
/* 264 */       strbuf.append("CN_NUM");
/* 265 */       break;
/*     */     case 48:
/* 267 */       strbuf.append("CN_QUAN");
/*     */     }
/*     */ 
/* 271 */     return strbuf.toString();
/*     */   }
/*     */ }

/* Location:           G:\临时目录\BaiduYunDownload\IKAnalyzer.jar
 * Qualified Name:     org.wltea.analyzer.core.Lexeme
 * JD-Core Version:    0.6.2
 */