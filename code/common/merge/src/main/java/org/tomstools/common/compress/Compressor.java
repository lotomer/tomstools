/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.common.compress;

/**
 * 压缩工具
 * @author vaval
 * @date 2011-12-16 
 * @time 下午07:43:40
 */
public interface Compressor {
    /**
     * 将指定文件压缩后保存为压缩文件名
     * @param srcFileName           待压缩文件
     * @param compressedFileName    压缩后文件名
     */
    public void compress(String srcFileName, String compressedFileName);
}
