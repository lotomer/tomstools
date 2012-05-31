/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.common.merge.manage;

import java.io.IOException;

import org.tomstools.common.log.Logger;
import org.tomstools.common.merge.file.FileMergeControler;

/**
 * web文件管理器 提供对文本文件的合并、压缩、web页面js/css脚本的引用串
 * 
 * @author lotomer
 * @date 2011-12-20
 * @time 上午11:02:34
 */
public abstract class WebFileManager {
    private static final Logger logger = Logger.getLogger(WebFileManager.class);
    public static final String FILE_TYPE_JS = "js";
    public static final String FILE_TYPE_CSS = "css";
    protected FileMergeControler fileMergeControler;


    protected WebFileManager() {
        fileMergeControler = new FileMergeControler();
    }

    /**
     * 初始化工作，负责数据源的载入。 子类可重写
     * 
     * @throws Exception
     */
    public abstract void init() throws Exception;

    /**
     * 设置是否开启调试模式。如果开启调试模式，则不会合并文件。默认开启。
     * 
     * @param isDebug 是否开启调试模式。true 开启调试模式；false 不开启调试模式
     */
    public final void setDebug(boolean isDebug) {
        fileMergeControler.setDebug(isDebug);
    }

    /**
     * 获取html脚本
     * 
     * @param id 文件标识
     * @param type 文件类型
     * @param isInline 是否开启内联模式。debug模式下总是以外联方式连接 true 以内联方式直接将内容输出到引用文件中； false
     *            以外联文件的方式连接到引用文件中
     * @param webRootPath web应用物理路径
     * 
     * @return html脚本
     */
    public final String getHTMLCode(String id, String type, boolean isInline, String webRootPath) {
        return fileMergeControler.getHTMLCode(id, type, isInline, webRootPath);
    }

    /**
     * 添加可能的变量。用于替换可能的变量值 。 <br/>
     * 合并后的输出路径变量名格式为：OUTPUT_ + 类型名(JS/CSS)。<br/>
     * 如果没有设置此变量，则合并输出到当前目录
     * 
     * @param variableName 变量名
     * @param variableValue 变量值
     */
    public void addVariable(String variableName, String variableValue) {
        logger.info("addVariable:" + variableName + "=" + variableValue);
        fileMergeControler.addVariable(variableName, variableValue);
    }

    /**
     * 通过变量名获取变量值
     * 
     * @param variableName 变量名
     * @return 变量值。可能为null或空字符串
     */
    protected String getVariableValue(String variableName) {
        return fileMergeControler.getVariableValue(variableName);
    }

    /**
     * 执行合并
     * 
     * @throws IOException
     */
    public void merge(String charset) throws IOException {
        fileMergeControler.merge(charset);
    }

    /**
     * 设置是否需要压缩
     * 
     * @param needCompress 是否需要压缩
     */
    public void setNeedCompress(boolean needCompress) {
        fileMergeControler.setNeedCompress(needCompress);
    }

    /**
     * 设置是否需要在压缩文件后删除原合并文件 默认不删除
     */
    public final void setNeedDeleteSourceFileForCompress(boolean needDeleteSourceFileForCompress) {
        fileMergeControler.setNeedDeleteSourceFileForCompress(needDeleteSourceFileForCompress);
    }
}
