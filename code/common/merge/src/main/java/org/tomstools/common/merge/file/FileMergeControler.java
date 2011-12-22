/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.common.merge.file;

import java.io.File;
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
 * @author lotomer
 * @date 2011-12-20
 * @time 上午10:58:12
 */
public class FileMergeControler {
    private static final Logger logger = Logger.getLogger(FileMergeControler.class);
    private static final String SEPARATOR_KEY = "_";
    private static final String VIRIABLE_OUTPUT = "OUTPUT";
    private Map<String, String> variables = new HashMap<String, String>();
    private Map<String, List<String>> fileInfos = new HashMap<String, List<String>>();
    private boolean needCompress;
    private boolean needDeleteSourceFileForCompress;
    public FileMergeControler() {
        needCompress = false;
        needDeleteSourceFileForCompress = false;
    }
    /**
     * 设置是否需要压缩
     * @param needCompress 是否需要压缩
     */
    public final void setNeedCompress(boolean needCompress) {
        this.needCompress = needCompress;
    }

    /**
     * 设置是否需要在压缩文件后删除源文件
     * 默认不删除
     */
    public final void setNeedDeleteSourceFileForCompress(boolean needDeleteSourceFileForCompress) {
        this.needDeleteSourceFileForCompress = needDeleteSourceFileForCompress;
    }
    /**
     * 添加可能的变量。用于替换可能的变量值 。 <br/>
     * 合并后的输出路径变量名格式为：OUTPUT_ + 类型名(JS/CSS)。<br/>
     * 如果没有设置此变量，则合并输出到当前目录
     * @param variableName 变量名
     * @param variableValue 变量值
     */
    public void addVariable(String variableName, String variableValue) {
        variables.put(getVariableName(variableName), variableValue);
    }

    /**
     * 通过变量名获取变量值
     * @param variableName  变量名
     * @return 变量值。可能为null或空字符串
     */
    public String getVariableValue(String variableName){
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
     * manager.addFile(&quot;index&quot;, &quot;/opt/a.js&quot;, &quot;js&quot;);
     * manager.addFile(&quot;index&quot;, &quot;/opt/b.js&quot;, &quot;js&quot;);
     * </pre>
     * @param fileId 文件标识，同一文件标识可对应多个文件名
     * @param fileName 文件名
     */
    public void addFile(String fileId, String fileName, String fileType) {
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
            addFileInfo(fileId, fileName, fileType);
        }
    }

    private void addFileInfo(String fileId, String fileName, String fileType) {
        String key = getKey(fileId, fileType);
        List<String> fileInfo = fileInfos.get(key);
        if (null == fileInfo) {
            fileInfo = new ArrayList<String>();
            fileInfos.put(key, fileInfo);
        }
        fileInfo.add(fileName);
    }

    private String getKey(String fileId, String fileType) {
        return fileId + SEPARATOR_KEY + fileType.toLowerCase();
    }

    /**
     * 执行合并
     */
    public void merge(String charset) {
        for (Entry<String, List<String>> fileInfo : fileInfos.entrySet()) {
            String[] file = fileInfo.getKey().split(SEPARATOR_KEY);
            if (2 != file.length) {
                logger.warn("The key is invalid! key:" + fileInfo.getKey());
                continue;
            }
            String fileId = file[0];
            String fileType = file[1];
            FileMerger merger = new FileMerger();
            List<String> fileList = fileInfo.getValue();
            for (String fileName : fileList) {
                merger.add(fileName);
            }
            // 执行合并
            String mergedFileName = getMergedFileName(fileId, fileType);
            merger.merge(mergedFileName);
            if (needCompress){
                // 执行压缩
                CompressorFactory.createFileCompressor().compress(mergedFileName,
                        getCompressedFileName(mergedFileName), charset, needDeleteSourceFileForCompress);
            }
        }
    }
    /**
     * 获取输出文件路径
     * @param fileId 文件标识
     * @param fileType 文件类型
     * @return 完整文件名
     */
    public String getOutputFileName(String fileId, String fileType) {
        String outputFileName = getMergedFileName(fileId, fileType);
        return getCompressedFileName(outputFileName);
    }
    
    /**
     * 获取文件压缩后的文件名
     * @param fileName  压缩压缩的文件的文件名   
     * @return  文件压缩后的文件名
     */
    private String getCompressedFileName(String fileName) {
        if (needCompress) {
            int index = fileName.lastIndexOf(".");
            if (-1 < index){
            return fileName.substring(0, index) + "-min" + fileName.substring(index);
            }else{
                return fileName + "-min";
            }
        } else {
            return fileName;
        }
    }
    
    /**
     * 获取合并后的文件路径
     * @param fileId 文件标识
     * @param fileType 文件类型
     * @return 合并后的完整文件名
     */
    private String getMergedFileName(String fileId, String fileType) {
        String path = variables.get(getVariableName(VIRIABLE_OUTPUT + SEPARATOR_KEY + fileType));
        StringBuilder fileName = new StringBuilder();
        if (!Utils.isEmpty(path)) {
            fileName.append(path);
            fileName.append(File.separator);
        }
        fileName.append(fileId);
        fileName.append(".");
        fileName.append(fileType);
        return fileName.toString();
    }

    /**
     * 根据文件标识和文件类型获取文件名列表
     * @param fileId 文件标识
     * @param fileType 文件类型
     * @return 文件列表。不会为null
     */
    public List<String> getFileList(String fileId, String fileType) {
        List<String> result = fileInfos.get(getKey(fileId, fileType));
        if (null == result) {
            return Collections.emptyList();
        } else {
            return result;
        }
    }

    /**
     * 替换part中的变量
     * @param part 待替换内容
     * @return 替换后的结果
     */
    private String replaceVariable(String part) {
        for (Entry<String, String> variable : variables.entrySet()) {
            part = part.replace(variable.getKey(), variable.getValue());
        }
        return part;
    }
}
