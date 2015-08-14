/*     */ package org.wltea.analyzer.core;
/*     */ 
/*     */ class QuickSortSet
/*     */ {
/*     */   private Cell head;
/*     */   private Cell tail;
/*     */   private int size;
/*     */ 
/*     */   QuickSortSet()
/*     */   {
/*  39 */     this.size = 0;
/*     */   }
/*     */ 
/*     */   boolean addLexeme(Lexeme lexeme)
/*     */   {
/*  47 */     Cell newCell = new Cell(lexeme);
/*  48 */     if (this.size == 0) {
/*  49 */       this.head = newCell;
/*  50 */       this.tail = newCell;
/*  51 */       this.size += 1;
/*  52 */       return true;
/*     */     }
/*     */ 
/*  55 */     if (this.tail.compareTo(newCell) == 0) {
/*  56 */       return false;
/*     */     }
/*  58 */     if (this.tail.compareTo(newCell) < 0) {
/*  59 */       this.tail.next = newCell;
/*  60 */       newCell.prev = this.tail;
/*  61 */       this.tail = newCell;
/*  62 */       this.size += 1;
/*  63 */       return true;
/*     */     }
/*  65 */     if (this.head.compareTo(newCell) > 0) {
/*  66 */       this.head.prev = newCell;
/*  67 */       newCell.next = this.head;
/*  68 */       this.head = newCell;
/*  69 */       this.size += 1;
/*  70 */       return true;
/*     */     }
/*     */ 
/*  74 */     Cell index = this.tail;
/*  75 */     while ((index != null) && (index.compareTo(newCell) > 0)) {
/*  76 */       index = index.prev;
/*     */     }
/*  78 */     if (index.compareTo(newCell) == 0) {
/*  79 */       return false;
/*     */     }
/*  81 */     if (index.compareTo(newCell) < 0) {
/*  82 */       newCell.prev = index;
/*  83 */       newCell.next = index.next;
/*  84 */       index.next.prev = newCell;
/*  85 */       index.next = newCell;
/*  86 */       this.size += 1;
/*  87 */       return true;
/*     */     }
/*     */ 
/*  91 */     return false;
/*     */   }
/*     */ 
/*     */   Lexeme peekFirst()
/*     */   {
/*  99 */     if (this.head != null) {
/* 100 */       return this.head.lexeme;
/*     */     }
/* 102 */     return null;
/*     */   }
/*     */ 
/*     */   Lexeme pollFirst()
/*     */   {
/* 110 */     if (this.size == 1) {
/* 111 */       Lexeme first = this.head.lexeme;
/* 112 */       this.head = null;
/* 113 */       this.tail = null;
/* 114 */       this.size -= 1;
/* 115 */       return first;
/* 116 */     }if (this.size > 1) {
/* 117 */       Lexeme first = this.head.lexeme;
/* 118 */       this.head = this.head.next;
/* 119 */       this.size -= 1;
/* 120 */       return first;
/*     */     }
/* 122 */     return null;
/*     */   }
/*     */ 
/*     */   Lexeme peekLast()
/*     */   {
/* 131 */     if (this.tail != null) {
/* 132 */       return this.tail.lexeme;
/*     */     }
/* 134 */     return null;
/*     */   }
/*     */ 
/*     */   Lexeme pollLast()
/*     */   {
/* 142 */     if (this.size == 1) {
/* 143 */       Lexeme last = this.head.lexeme;
/* 144 */       this.head = null;
/* 145 */       this.tail = null;
/* 146 */       this.size -= 1;
/* 147 */       return last;
/*     */     }
/* 149 */     if (this.size > 1) {
/* 150 */       Lexeme last = this.tail.lexeme;
/* 151 */       this.tail = this.tail.prev;
/* 152 */       this.size -= 1;
/* 153 */       return last;
/*     */     }
/*     */ 
/* 156 */     return null;
/*     */   }
/*     */ 
/*     */   int size()
/*     */   {
/* 165 */     return this.size;
/*     */   }
/*     */ 
/*     */   boolean isEmpty()
/*     */   {
/* 173 */     return this.size == 0;
/*     */   }
/*     */ 
/*     */   Cell getHead()
/*     */   {
/* 181 */     return this.head;
/*     */   }
/*     */ 
/*     */   class Cell
/*     */     implements Comparable<Cell>
/*     */   {
/*     */     private Cell prev;
/*     */     private Cell next;
/*     */     private Lexeme lexeme;
/*     */ 
/*     */     Cell(Lexeme lexeme)
/*     */     {
/* 217 */       if (lexeme == null) {
/* 218 */         throw new IllegalArgumentException("lexeme must not be null");
/*     */       }
/* 220 */       this.lexeme = lexeme;
/*     */     }
/*     */ 
/*     */     public int compareTo(Cell o) {
/* 224 */       return this.lexeme.compareTo(o.lexeme);
/*     */     }
/*     */ 
/*     */     public Cell getPrev() {
/* 228 */       return this.prev;
/*     */     }
/*     */ 
/*     */     public Cell getNext() {
/* 232 */       return this.next;
/*     */     }
/*     */ 
/*     */     public Lexeme getLexeme() {
/* 236 */       return this.lexeme;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\临时目录\BaiduYunDownload\IKAnalyzer.jar
 * Qualified Name:     org.wltea.analyzer.core.QuickSortSet
 * JD-Core Version:    0.6.2
 */