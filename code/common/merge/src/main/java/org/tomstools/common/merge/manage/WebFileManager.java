/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.common.merge.manage;

import java.util.List;

import org.tomstools.common.log.Logger;
import org.tomstools.common.merge.file.FileMergeControler;

/**
 * web文件管理器 提供对文本文件的合并、压缩、web页面js/css脚本的引用串
 * @author lotomer
 * @date 2011-12-20
 * @time 上午11:02:34
 */
public abstract class WebFileManager {
    private static final Logger logger = Logger.getLogger(WebFileManager.class);
    protected static final String FILE_TYPE_JS = "js";
    protected static final String FILE_TYPE_CSS = "css";
    protected FileMergeControler fileMergeControler;
    private boolean isDebug;

    protected WebFileManager() {
        fileMergeControler = new FileMergeControler();
        isDebug = true;
    }

    /**
     * 初始化工作，负责数据源的载入。 子类可重写
     */
    public abstract void init();

    public final void setDebug(boolean isDebug) {
        this.isDebug = isDebug;
    }

    /**
     * 获取html脚本
     * @param id 文件标识
     * @param type 文件类型
     * @return html脚本
     */
    public final String getHTMLCode(String id, String type) {
        if (isDebug) {
            // 调试模式，不合并
            StringBuilder html = new StringBuilder();
            List<String> files = fileMergeControler.getFileList(id, type);
            for (String file : files) {
                html.append(combileHTMLCode(file, type));
                html.append("\n");
            }

            return html.toString();
        } else {
            // 非调试模式，返回合并后结果
            return combileHTMLCode(fileMergeControler.getOutputFileName(id, type), type);
        }
    }

    /**
     * 添加可能的变量。用于替换可能的变量值 。 <br/>
     * 合并后的输出路径变量名格式为：OUTPUT_ + 类型名(JS/CSS)。<br/>
     * 如果没有设置此变量，则合并输出到当前目录
     * @param variableName 变量名
     * @param variableValue 变量值
     */
    public void addVariable(String variableName, String variableValue) {
        fileMergeControler.addVariable(variableName, variableValue);
    }
    /**
     * 通过变量名获取变量值
     * @param variableName  变量名
     * @return 变量值。可能为null或空字符串
     */
    protected String getVariableValue(String variableName){
        return fileMergeControler.getVariableValue(variableName);
    }
    private String combileHTMLCode(String outputFileName, String type) {
        StringBuilder html = new StringBuilder();
        if (FILE_TYPE_JS.equalsIgnoreCase(type)) {
            // js文件
            html.append("<script type=\"text/javascript\" src=\"");
            html.append(outputFileName);
            html.append("\"></script>");
        } else if (FILE_TYPE_CSS.equalsIgnoreCase(type)) {
            // css文件
            html.append("<link type=\"text/css\" rel=\"stylesheet\" href=\"");
            html.append(outputFileName);
            html.append("\"/>");
        } else {
            logger.warn("The file type is not expected(expected:js/css)! type:" + type);
        }

        return html.toString();
    }

    /**
     * 执行合并
     */
    public void merge(String charset) {
        fileMergeControler.merge(charset);
    }
    /**
     * 设置是否需要压缩
     * @param needCompress 是否需要压缩
     */
    public void setNeedCompress(boolean needCompress) {
        fileMergeControler.setNeedCompress(needCompress);
    }
    /**
     * 设置是否需要在压缩文件后删除源文件
     * 默认不删除
     */
    public final void setNeedDeleteSourceFileForCompress(boolean needDeleteSourceFileForCompress) {
        fileMergeControler.setNeedDeleteSourceFileForCompress(needDeleteSourceFileForCompress);
    }
}
