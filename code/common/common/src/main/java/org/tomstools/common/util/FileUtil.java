/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.common.util;

/**
 * 文件操作工具类
 * @author lotomer
 * @date 2011-12-20
 * @time 下午03:26:24
 */
public final class FileUtil {
    private FileUtil() {
    }

    /**
     * 获取文件名的扩展名
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
}
