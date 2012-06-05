/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.common.merge.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.List;

import org.tomstools.common.log.Logger;
import org.tomstools.common.merge.Merger;
import org.tomstools.common.util.FileUtil;

/**
 * 文件合并器
 * 
 * @author vaval
 * @date 2011-12-16
 * @time 下午07:49:16
 */
public class FileMerger implements Merger {
    private static final Logger logger = Logger.getLogger(FileMerger.class);
    // private static final int BUFFER_SIZE = 1024;
    private List<String> files = new ArrayList<String>();

    public void add(String srcFileName) {
        logger.info("add file " + srcFileName);
        files.add(srcFileName);
    }

    public void merge(String mergedFileName, String charset, Handle handle) {
        // 目标文件不能存在
        File destFile = new File(mergedFileName);
        if (destFile.exists()) {
            if (!destFile.delete()) {
                throw new RuntimeException("The dest file is exists, and delete failed! File name:"
                        + mergedFileName);
            }
        }
        logger.info("start merge " + mergedFileName);
        FileOutputStream os = null;
        FileChannel out = null;
        CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
        try {
            // 循环读入并输出
            for (String fileName : files) {
                File file = new File(fileName);
                if (!file.exists()) {
                    logger.warn("The file is not exists! File name:" + file.getAbsolutePath());
                    continue;
                }
                if (null == out) {
                    os = new FileOutputStream(destFile);
                    out = os.getChannel();
                    // 输出UTF8文件标准头
                    //out.write(FileUtil.FILE_HEAD_UTF8);
                }
                logger.info("file:" + fileName);
                String content = FileUtil.getFileContent(file, charset);
                if (null != handle) {
                    content = handle.process(file, content, destFile);
                }
                // 去除UTF8文件头
                if (content.startsWith(FileUtil.FILE_HEAD_UTF8_STR)) {
                    content = content.substring(FileUtil.FILE_HEAD_UTF8_STR.length());
                    out.write(encoder.encode(CharBuffer.wrap(content)));
                } else {
                    out.write(encoder.encode(CharBuffer.wrap(content)));
                }                
            }
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (null != out) {
                // 关闭输出流
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
                out = null;
            }
        }

        logger.info("merge finished.");
    }
}
