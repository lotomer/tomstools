/*     */ package org.wltea.analyzer.core;
/*     */ 
/*     */ class CharacterUtil
/*     */ {
/*     */   public static final int CHAR_USELESS = 0;
/*     */   public static final int CHAR_ARABIC = 1;
/*     */   public static final int CHAR_ENGLISH = 2;
/*     */   public static final int CHAR_CHINESE = 4;
/*     */   public static final int CHAR_OTHER_CJK = 8;
/*     */ 
/*     */   static int identifyCharType(char input)
/*     */   {
/*  51 */     if ((input >= '0') && (input <= '9')) {
/*  52 */       return 1;
/*     */     }
/*  54 */     if (((input >= 'a') && (input <= 'z')) || (
/*  55 */       (input >= 'A') && (input <= 'Z'))) {
/*  56 */       return 2;
/*     */     }
/*     */ 
/*  59 */     Character.UnicodeBlock ub = Character.UnicodeBlock.of(input);
/*     */ 
/*  61 */     if ((ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) || 
/*  62 */       (ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS) || 
/*  63 */       (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A))
/*     */     {
/*  65 */       return 4;
/*     */     }
/*  67 */     if ((ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) || 
/*  69 */       (ub == Character.UnicodeBlock.HANGUL_SYLLABLES) || 
/*  70 */       (ub == Character.UnicodeBlock.HANGUL_JAMO) || 
/*  71 */       (ub == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO) || 
/*  73 */       (ub == Character.UnicodeBlock.HIRAGANA) || 
/*  74 */       (ub == Character.UnicodeBlock.KATAKANA) || 
/*  75 */       (ub == Character.UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS)) {
/*  76 */       return 8;
/*     */     }
/*     */ 
/*  81 */     return 0;
/*     */   }
/*     */ 
/*     */   static char regularize(char input)
/*     */   {
/*  90 */     if (input == '　') {
/*  91 */       input = ' ';
/*     */     }
/*  93 */     else if ((input > 65280) && (input < 65375)) {
/*  94 */       input = (char)(input - 65248);
/*     */     }
/*  96 */     else if ((input >= 'A') && (input <= 'Z')) {
/*  97 */       input = (char)(input + ' ');
/*     */     }
/*     */ 
/* 100 */     return input;
/*     */   }
/*     */ }

/* Location:           G:\临时目录\BaiduYunDownload\IKAnalyzer.jar
 * Qualified Name:     org.wltea.analyzer.core.CharacterUtil
 * JD-Core Version:    0.6.2
 */