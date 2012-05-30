/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.common.compress.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.tomstools.common.compress.Compressor;
import org.tomstools.common.log.Logger;
import org.tomstools.common.util.FileUtil;

import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

/**
 * 文件压缩器
 * @author vaval
 * @date 2011-12-16
 * @time 下午07:50:31
 */
public class FileCompressor implements Compressor {
    private static final Logger logger = Logger.getLogger(FileCompressor.class);
    private static final String FILE_EXT_CSS = "css";
    private static final String FILE_EXT_JS = "js";

    public void compress(String srcFileName, String compressedFileName, String charset,
            boolean deleteSourceFile) throws IOException {
        logger.info("srcFileName:" + srcFileName);
        logger.info("compressedFileName:" + compressedFileName);
        
        compress(new File(srcFileName), new File(compressedFileName), charset, deleteSourceFile);
    }

    private void compress(File srcFile, File compressedFile, String charset,
            boolean deleteSourceFile) throws IOException {
        if (null == srcFile) {
            throw new RuntimeException("The input file cannot be empty!");
        }
        if (null == compressedFile) {
            throw new RuntimeException("The dest file cannot be empty!");
        }
        // 区分是JS文件还是CSS文件，二者需要使用各自的方法进行压缩
        if (FILE_EXT_CSS.equalsIgnoreCase(FileUtil.getFileExt(srcFile.getName()))) {
            compressCSS(srcFile, compressedFile, charset, deleteSourceFile);
        } else if (FILE_EXT_JS.equalsIgnoreCase(FileUtil.getFileExt(srcFile.getName()))) {
            compressJS(srcFile, compressedFile, charset, deleteSourceFile);
        } else {
            throw new RuntimeException("The input file must be css/js file. "
                    + srcFile.getAbsolutePath());
        }
    }

    private void compressCSS(File inFile, File outFile, String charset, boolean deleteSourceFile) throws IOException {
        logger.info("start compress css file. in file:" + inFile.getAbsolutePath() + " out file:"
                + outFile.getAbsolutePath());
        Reader in = null;
        Writer out = null;
        int linebreakpos = -1;

        try {
            in = new InputStreamReader(new FileInputStream(inFile), charset);
            CssCompressor compressor = new CssCompressor(in);
            out = new OutputStreamWriter(new FileOutputStream(outFile), charset);
            compressor.compress(out, linebreakpos);
        } 
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(),e);
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(),e);
                }
            }
        }

        // 如果需要删除源文件，则删除
        if (deleteSourceFile) {
            if (inFile.delete()) {
                logger.info("delete in file success! file:" + inFile.getAbsolutePath());
            } else {
                logger.info("delete in file failed! file:" + inFile.getAbsolutePath());
            }
        }
        logger.info("compress css file finished.");
    }

    private void compressJS(File inFile, File outFile, String charset, boolean deleteSourceFile) throws IOException {
        logger.info("start compress js file. in file:" + inFile.getAbsolutePath() + " out file:"
                + outFile.getAbsolutePath());
        Reader in = null;
        Writer out = null;
        int linebreakpos = -1;
        boolean verbose = false;
        try {
            in = new InputStreamReader(new FileInputStream(inFile), charset);
            JavaScriptCompressor compressor = new JavaScriptCompressor(in, new ErrorReporter() {
                public void warning(String message, String sourceName, int line, String lineSource,
                        int lineOffset) {
                    if (line < 0)
                        System.err.println("\n[WARNING] " + message);
                    else
                        System.err
                                .println("\n[WARNING] " + line + ':' + lineOffset + ':' + message);
                }

                public void error(String message, String sourceName, int line, String lineSource,
                        int lineOffset) {
                    if (line < 0)
                        System.err.println("\n[ERROR] " + message);
                    else
                        System.err.println("\n[ERROR] " + line + ':' + lineOffset + ':' + message);
                }

                public EvaluatorException runtimeError(String message, String sourceName, int line,
                        String lineSource, int lineOffset) {
                    error(message, sourceName, line, lineSource, lineOffset);
                    return new EvaluatorException(message);
                }
            });
            out = new OutputStreamWriter(new FileOutputStream(outFile), charset);

            boolean munge = true;
            boolean preserveAllSemiColons = false;
            boolean disableOptimizations = false;

            compressor.compress(out, linebreakpos, munge, verbose, preserveAllSemiColons,
                    disableOptimizations);
            in.close();
            in = null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(),e);
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(),e);
                }
            }
        }

        // 如果需要删除源文件，则删除
        if (deleteSourceFile) {
            if (inFile.delete()) {
                logger.info("delete in file success! file:" + inFile.getAbsolutePath());
            } else {
                logger.info("delete in file failed! file:" + inFile.getAbsolutePath());
            }
        }
        logger.info("compress js file finished.");
    }

}
