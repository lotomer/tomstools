/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.common.merge.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.tomstools.common.log.Logger;
import org.tomstools.common.merge.Merger;

/**
 * 文件合并器
 * 
 * @author vaval
 * @date 2011-12-16
 * @time 下午07:49:16
 */
public class FileMerger implements Merger {
    private static final Logger logger = Logger.getLogger(FileMerger.class);
    private static final int BUFFER_SIZE = 1024;
    private List<String> files = new ArrayList<String>();

    public void add(String srcFileName) {
        logger.info("add file " + srcFileName);
        files.add(srcFileName);
    }

    public void merge(String mergedFileName) {
        // 目标文件不能存在
        File destFile = new File(mergedFileName);
        if (destFile.exists()){
            if (!destFile.delete()){
                throw new RuntimeException("The dest file is exists, and delete failed! File name:" + mergedFileName);
            }
        }
        logger.info("start merge " + mergedFileName);
        OutputStream out = null;
        try {
            byte[] b = new byte[BUFFER_SIZE];
            //循环读入并输出
            for (String file : files) {
                File srcFile = new File(file);
                if (!srcFile.exists()){
                    logger.warn("The file is not exists! File name:" + file);
                    continue;
                }
                if (null == out){
                    out = new FileOutputStream(destFile);
                }
                InputStream in = new FileInputStream(srcFile);
                int count = 0;
                do{
                    count = in.read(b);
                    out.write(b,0,count);
                }while(count == BUFFER_SIZE);
                out.write('\n');
                // 关闭输入流
                in.close();
                in = null;
            }
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(),e);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
        finally{
            if (null != out){
                //关闭输出流
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(),e);
                }
                out = null;
            }
        }
        
        logger.info("merge finished.");
    }
}
