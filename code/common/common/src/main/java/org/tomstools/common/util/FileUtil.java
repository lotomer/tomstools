/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.tomstools.common.log.Logger;

/**
 * 文件操作工具类
 * 
 * @author lotomer
 * @date 2011-12-20
 * @time 下午03:26:24
 */
public final class FileUtil {
    private static final Logger logger = Logger.getLogger(FileUtil.class);

    private FileUtil() {
    }

    /**
     * 获取文件名的扩展名
     * 
     * @param fileName 文件名
     * @return 文件名的扩展名。如果文件名为空，则范围空字符串。不会返回null
     */
    public static String getFileExt(String fileName) {
        String fileExt = "";
        if (!Utils.isEmpty(fileName)) {
            int index = fileName.lastIndexOf(".");
            if (-1 < index) {
                fileExt = fileName.substring(index + 1);
            }
        }

        return fileExt;
    }

    /**
     * 根据文件名获取文件内容
     * XXX 读取性能需要优化
     * @param fileName 文件名
     * @return 文件内容
     */
    public static String getFileContent(String fileName) {
        StringBuilder content = new StringBuilder();
        File file = new File(fileName);
        if (file.isFile()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(fileName));
                String lineContent = null;
                while ((lineContent = reader.readLine()) != null) {
                    content.append(lineContent);
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            logger.warn("The file is not exists or is not a file!" + file.getAbsolutePath());
        }
        
        return content.toString();
    }
}
