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
     * @param file 文件
     * @return 文件内容
     */
    public static String getFileContent(File file) {
        StringBuilder content = new StringBuilder();        
        if (file.isFile()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String lineContent = null;
                while ((lineContent = reader.readLine()) != null) {
                    content.append(lineContent);
                }
                reader.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            logger.warn("The file is not exists or is not a file!" + file.getAbsolutePath());
        }
        
        return content.toString();
    }

    /**
     * 获取源文件相对于基准文件的相对路径
     * @param baseFilePath  基准文件。必须是目录而不是文件。不允许为null
     * @param srcFilePath   源文件。必须是目录而不是文件。。不允许为null
     * @return 相对路径
     */
    public static String generateAbstractPath(File baseFilePath, File srcFilePath) {
        String baseFileName = getRealPath(baseFilePath);
        String srcFileName = getRealPath(srcFilePath);
        String[] baseNames = baseFileName.split("\\" + File.separator);
        String[] srcNames = srcFileName.split("\\" + File.separator);
        int baseLength = baseNames.length;
        //int srcLength = isFile ? srcNames.length - 1 : srcNames.length;
        int srcLength = srcNames.length;
        int length = baseLength < srcLength ? baseLength : srcLength;
        int index = 0;
        for (; index < length; index++){
            if (!baseNames[index].equals(srcNames[index])){
                break;
            }
        }
        StringBuilder abstractPath = new StringBuilder();
        for (int i = index; i < baseNames.length; i++) {
            abstractPath.append("../");
        }
        for (int i = index; i < srcNames.length; i++) {
            abstractPath.append(srcNames[i]);
            if (i != srcNames.length - 1){
                abstractPath.append("/");
            }
        }
        
        return abstractPath.toString();
    }
    private static String getRealPath(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return file.getAbsolutePath();
        }
    }
}
