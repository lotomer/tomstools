/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.common.merge.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.tomstools.common.compress.CompressorFactory;
import org.tomstools.common.log.Logger;
import org.tomstools.common.util.FileUtil;
import org.tomstools.common.util.Utils;

/**
 * 文件合并控制器
 * 
 * @author lotomer
 * @date 2011-12-20
 * @time 上午10:58:12
 */
public class FileMergeControler {
    private static final Logger logger = Logger.getLogger(FileMergeControler.class);
    private static final String SEPARATOR_KEY = "_";
    private Map<String, String> variables = new HashMap<String, String>();
    private Map<String, FileInfo> fileInfos = new HashMap<String, FileInfo>();
    private boolean needCompress;
    private boolean needDeleteSourceFileForCompress;

    public FileMergeControler() {
        needCompress = false;
        needDeleteSourceFileForCompress = false;
    }

    /**
     * 设置是否需要压缩
     * 
     * @param needCompress 是否需要压缩
     */
    public final void setNeedCompress(boolean needCompress) {
        this.needCompress = needCompress;
    }

    /**
     * 设置是否需要在压缩文件后删除源文件 默认不删除
     */
    public final void setNeedDeleteSourceFileForCompress(boolean needDeleteSourceFileForCompress) {
        this.needDeleteSourceFileForCompress = needDeleteSourceFileForCompress;
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
        variables.put(getVariableName(variableName), variableValue);
    }

    /**
     * 通过变量名获取变量值
     * 
     * @param variableName 变量名
     * @return 变量值。可能为null或空字符串
     */
    public String getVariableValue(String variableName) {
        return variables.get(getVariableName(variableName));
    }

    private String getVariableName(String variableName) {
        return "${" + variableName + "}";
    }

    /**
     * 添加待合并部分
     * 
     * <pre>
     * FileMergeManager manager = new FileMergeManager();
     * manager.addFile(&quot;index&quot;, &quot;/opt/a.js&quot;, &quot;js&quot;, &quot;./js&quot;);
     * manager.addFile(&quot;index&quot;, &quot;/opt/b.js&quot;, &quot;js&quot;, &quot;./js&quot;);
     * </pre>
     * 
     * @param fileId 文件标识，同一文件标识可对应多个文件名
     * @param fileName 文件名
     * @param fileType 文件类型
     * @param outputPath 输出路径
     */
    public void addFile(String fileId, String fileName, String fileType, String outputPath) {
        // 文件标识不能为空
        if (Utils.isEmpty(fileId)) {
            logger.warn("The fileId cannot be empty!");
            return;
        }
        if (!Utils.isEmpty(fileName)) {
            fileName = replaceVariable(fileName);
            if (Utils.isEmpty(fileType)) {
                fileType = FileUtil.getFileExt(fileName);
            }

            // 准备合并扩展信息
            addFileInfo(fileId, fileName, fileType, outputPath);
        }
    }

    private void addFileInfo(String fileId, String fileName, String fileType, String outputPath) {
        String key = getKey(fileId, fileType);
        FileInfo fileInfo = fileInfos.get(key);
        if (null == fileInfo) {
            fileInfo = new FileInfo(fileId, fileType, outputPath);
            fileInfos.put(key, fileInfo);
        }
        fileInfo.addFile(fileName);
    }

    private String getKey(String fileId, String fileType) {
        return fileId + SEPARATOR_KEY + fileType.toLowerCase();
    }

    /**
     * 执行合并
     * @throws IOException 
     */
    public void merge(String charset) throws IOException {
        logger.info("start merge...");
        for (Entry<String, FileInfo> file : fileInfos.entrySet()) {
            FileInfo fileInfo = file.getValue();
            String fileId = fileInfo.fileId;
            String fileType = fileInfo.fileType;
            FileMerger merger = new FileMerger();
            List<String> fileList = fileInfo.files;
            for (String fileName : fileList) {
                merger.add(fileName);
            }
            // 执行合并
            String mergedFileName = getMergedFileName(fileId, fileType);
            merger.merge(mergedFileName);
            if (needCompress) {
                // 执行压缩
                CompressorFactory.createFileCompressor().compress(
                        mergedFileName,
                        getCompressedFileName(fileInfo.outputPath, mergedFileName, fileType,
                                File.separator), charset, needDeleteSourceFileForCompress);
            }
        }
    }

    /**
     * 获取输出文件路径。供web前台页面使用
     * 
     * @param fileId 文件标识
     * @param fileType 文件类型
     * @return 完整文件名
     */
    public String getOutputFileName4web(String fileId, String fileType) {
        String outputFileName = getMergedFileName(fileId, fileType);
        String key = getKey(fileId, fileType);
        FileInfo fileInfo = fileInfos.get(key);
        if (null != fileInfo) {
            return getCompressedFileName(fileInfo.outputPath, outputFileName, fileType,
                    "/");
        } else {
            return getCompressedFileName(".", outputFileName, fileType, "/");
        }
    }

    /**
     * 获取文件压缩后的文件名
     * 
     * @param outputPath 输出目录
     * @param fileName 压缩压缩的文件的文件名
     * @param fileType 文件类型
     * @param fileSeparator 文件路径分隔符
     * @return 文件压缩后的文件名
     */
    private String getCompressedFileName(String outputPath, String fileName, String fileType,
            String fileSeparator) {
        StringBuilder out = new StringBuilder();
        out.append(replaceVariable(outputPath));
        out.append(fileSeparator);
        if (needCompress) {
            int index = fileName.lastIndexOf(".");
            if (-1 < index) {
                out.append(fileName.substring(0, index));
                out.append("-min");
                out.append(fileName.substring(index));
            } else {
                out.append(fileName);
                out.append("-min");
            }
        } else {
            out.append(fileName);
        }
        logger.info(out.toString());
        return out.toString();
    }

    /**
     * 获取合并后的文件路径
     * 
     * @param fileId 文件标识
     * @param fileType 文件类型
     * @return 合并后的完整文件名
     */
    private String getMergedFileName(String fileId, String fileType) {
        StringBuilder fileName = new StringBuilder();
        fileName.append(fileId);
        fileName.append(".");
        fileName.append(fileType);
        return fileName.toString();
    }

    /**
     * 根据文件标识和文件类型获取文件名列表
     * 
     * @param fileId 文件标识
     * @param fileType 文件类型
     * @return 文件列表。不会为null
     */
    public List<String> getFileList(String fileId, String fileType) {
        List<String> result = fileInfos.get(getKey(fileId, fileType)).files;
        if (null == result) {
            return Collections.emptyList();
        } else {
            return result;
        }
    }

    /**
     * 替换part中的变量
     * 
     * @param part 待替换内容
     * @return 替换后的结果
     */
    private String replaceVariable(String part) {
        for (Entry<String, String> variable : variables.entrySet()) {
            part = part.replace(variable.getKey(), variable.getValue());
        }
        return part;
    }

    private static class FileInfo {
        String fileId;
        String fileType;
        List<String> files;
        String outputPath;

        FileInfo(String fileId, String fileType, String outputPath) {
            super();
            this.fileId = fileId;
            this.fileType = fileType;
            this.outputPath = outputPath;
            files = new ArrayList<String>();
        }

        void addFile(String fileName) {
            files.add(fileName);
        }
    }
}
