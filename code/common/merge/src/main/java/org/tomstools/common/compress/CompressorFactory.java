/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.common.compress;

import org.tomstools.common.compress.impl.FileCompressor;

/**
 * 压缩工具工厂类
 * @author lotomer
 * @date 2011-12-20 
 * @time 上午11:06:07
 */
public class CompressorFactory {
    private CompressorFactory(){
        
    }
    
    /**
     * 获取文件压缩工具
     * @return 文件压缩工具
     */
    public static Compressor createFileCompressor(){
        return new FileCompressor();
    }
}
