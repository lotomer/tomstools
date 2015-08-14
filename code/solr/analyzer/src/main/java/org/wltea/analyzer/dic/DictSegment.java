/*     */ package org.wltea.analyzer.dic;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ class DictSegment
/*     */   implements Comparable<DictSegment>
/*     */ {
/*  38 */   private static final Map<Character, Character> charMap = new HashMap(16, 0.95F);
/*     */   private static final int ARRAY_LENGTH_LIMIT = 3;
/*     */   private Map<Character, DictSegment> childrenMap;
/*     */   private DictSegment[] childrenArray;
/*     */   private Character nodeChar;
/*  53 */   private int storeSize = 0;
/*     */ 
/*  55 */   private int nodeState = 0;
/*     */ 
/*     */   DictSegment(Character nodeChar)
/*     */   {
/*  59 */     if (nodeChar == null) {
/*  60 */       throw new IllegalArgumentException("参数为空异常，字符不能为空");
/*     */     }
/*  62 */     this.nodeChar = nodeChar;
/*     */   }
/*     */ 
/*     */   Character getNodeChar() {
/*  66 */     return this.nodeChar;
/*     */   }
/*     */ 
/*     */   boolean hasNextNode()
/*     */   {
/*  73 */     return this.storeSize > 0;
/*     */   }
/*     */ 
/*     */   Hit match(char[] charArray)
/*     */   {
/*  82 */     return match(charArray, 0, charArray.length, null);
/*     */   }
/*     */ 
/*     */   Hit match(char[] charArray, int begin, int length)
/*     */   {
/*  93 */     return match(charArray, begin, length, null);
/*     */   }
/*     */ 
/*     */   Hit match(char[] charArray, int begin, int length, Hit searchHit)
/*     */   {
/* 106 */     if (searchHit == null)
/*     */     {
/* 108 */       searchHit = new Hit();
/*     */ 
/* 110 */       searchHit.setBegin(begin);
/*     */     }
/*     */     else {
/* 113 */       searchHit.setUnmatch();
/*     */     }
/*     */ 
/* 116 */     searchHit.setEnd(begin);
/*     */ 
/* 118 */     Character keyChar = new Character(charArray[begin]);
/* 119 */     DictSegment ds = null;
/*     */ 
/* 122 */     DictSegment[] segmentArray = this.childrenArray;
/* 123 */     Map segmentMap = this.childrenMap;
/*     */ 
/* 126 */     if (segmentArray != null)
/*     */     {
/* 128 */       DictSegment keySegment = new DictSegment(keyChar);
/* 129 */       int position = Arrays.binarySearch(segmentArray, 0, this.storeSize, keySegment);
/* 130 */       if (position >= 0) {
/* 131 */         ds = segmentArray[position];
/*     */       }
/*     */     }
/* 134 */     else if (segmentMap != null)
/*     */     {
/* 136 */       ds = (DictSegment)segmentMap.get(keyChar);
/*     */     }
/*     */ 
/* 140 */     if (ds != null) {
/* 141 */       if (length > 1)
/*     */       {
/* 143 */         return ds.match(charArray, begin + 1, length - 1, searchHit);
/* 144 */       }if (length == 1)
/*     */       {
/* 147 */         if (ds.nodeState == 1)
/*     */         {
/* 149 */           searchHit.setMatch();
/*     */         }
/* 151 */         if (ds.hasNextNode())
/*     */         {
/* 153 */           searchHit.setPrefix();
/*     */ 
/* 155 */           searchHit.setMatchedDictSegment(ds);
/*     */         }
/* 157 */         return searchHit;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 162 */     return searchHit;
/*     */   }
/*     */ 
/*     */   void fillSegment(char[] charArray)
/*     */   {
/* 170 */     fillSegment(charArray, 0, charArray.length, 1);
/*     */   }
/*     */ 
/*     */   void disableSegment(char[] charArray)
/*     */   {
/* 178 */     fillSegment(charArray, 0, charArray.length, 0);
/*     */   }
/*     */ 
/*     */   private synchronized void fillSegment(char[] charArray, int begin, int length, int enabled)
/*     */   {
/* 190 */     Character beginChar = new Character(charArray[begin]);
/* 191 */     Character keyChar = (Character)charMap.get(beginChar);
/*     */ 
/* 193 */     if (keyChar == null) {
/* 194 */       charMap.put(beginChar, beginChar);
/* 195 */       keyChar = beginChar;
/*     */     }
/*     */ 
/* 199 */     DictSegment ds = lookforSegment(keyChar, enabled);
/* 200 */     if (ds != null)
/*     */     {
/* 202 */       if (length > 1)
/*     */       {
/* 204 */         ds.fillSegment(charArray, begin + 1, length - 1, enabled);
/* 205 */       } else if (length == 1)
/*     */       {
/* 208 */         ds.nodeState = enabled;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private DictSegment lookforSegment(Character keyChar, int create)
/*     */   {
/* 222 */     DictSegment ds = null;
/*     */ 
/* 224 */     if (this.storeSize <= 3)
/*     */     {
/* 226 */       DictSegment[] segmentArray = getChildrenArray();
/*     */ 
/* 228 */       DictSegment keySegment = new DictSegment(keyChar);
/* 229 */       int position = Arrays.binarySearch(segmentArray, 0, this.storeSize, keySegment);
/* 230 */       if (position >= 0) {
/* 231 */         ds = segmentArray[position];
/*     */       }
/*     */ 
/* 235 */       if ((ds == null) && (create == 1)) {
/* 236 */         ds = keySegment;
/* 237 */         if (this.storeSize < 3)
/*     */         {
/* 239 */           segmentArray[this.storeSize] = ds;
/*     */ 
/* 241 */           this.storeSize += 1;
/* 242 */           Arrays.sort(segmentArray, 0, this.storeSize);
/*     */         }
/*     */         else
/*     */         {
/* 247 */           Map segmentMap = getChildrenMap();
/*     */ 
/* 249 */           migrate(segmentArray, segmentMap);
/*     */ 
/* 251 */           segmentMap.put(keyChar, ds);
/*     */ 
/* 253 */           this.storeSize += 1;
/*     */ 
/* 255 */           this.childrenArray = null;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 262 */       Map segmentMap = getChildrenMap();
/*     */ 
/* 264 */       ds = (DictSegment)segmentMap.get(keyChar);
/* 265 */       if ((ds == null) && (create == 1))
/*     */       {
/* 267 */         ds = new DictSegment(keyChar);
/* 268 */         segmentMap.put(keyChar, ds);
/*     */ 
/* 270 */         this.storeSize += 1;
/*     */       }
/*     */     }
/*     */ 
/* 274 */     return ds;
/*     */   }
/*     */ 
/*     */   private DictSegment[] getChildrenArray()
/*     */   {
/* 283 */     if (this.childrenArray == null) {
/* 284 */       synchronized (this) {
/* 285 */         if (this.childrenArray == null) {
/* 286 */           this.childrenArray = new DictSegment[3];
/*     */         }
/*     */       }
/*     */     }
/* 290 */     return this.childrenArray;
/*     */   }
/*     */ 
/*     */   private Map<Character, DictSegment> getChildrenMap()
/*     */   {
/* 298 */     if (this.childrenMap == null) {
/* 299 */       synchronized (this) {
/* 300 */         if (this.childrenMap == null) {
/* 301 */           this.childrenMap = new HashMap(6, 0.8F);
/*     */         }
/*     */       }
/*     */     }
/* 305 */     return this.childrenMap;
/*     */   }
/*     */ 
/*     */   private void migrate(DictSegment[] segmentArray, Map<Character, DictSegment> segmentMap)
/*     */   {
/* 313 */     for (DictSegment segment : segmentArray)
/* 314 */       if (segment != null)
/* 315 */         segmentMap.put(segment.nodeChar, segment);
/*     */   }
/*     */ 
/*     */   public int compareTo(DictSegment o)
/*     */   {
/* 327 */     return this.nodeChar.compareTo(o.nodeChar);
/*     */   }
/*     */ }

/* Location:           G:\临时目录\BaiduYunDownload\IKAnalyzer.jar
 * Qualified Name:     org.wltea.analyzer.dic.DictSegment
 * JD-Core Version:    0.6.2
 */