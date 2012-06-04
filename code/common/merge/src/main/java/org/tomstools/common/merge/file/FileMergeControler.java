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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tomstools.common.compress.CompressorFactory;
import org.tomstools.common.log.Logger;
import org.tomstools.common.merge.Merger;
import org.tomstools.common.merge.manage.WebFileManager;
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
    private static final String EXT_MERGED = ".merged";
    private Map<String, String> variables = new HashMap<String, String>();
    private Map<String, FileInfo> fileInfos = new HashMap<String, FileInfo>();
    private boolean needCompress;
    private boolean needDeleteSourceFileForCompress;
    private boolean isDebug;

    public FileMergeControler() {
        needCompress = false;
        needDeleteSourceFileForCompress = false;
        isDebug = true;
    }

    /**
     * 设置是否开启调试模式。如果开启调试模式，则不会合并文件。默认开启。
     * 
     * @param isDebug 是否开启调试模式。true 开启调试模式；false 不开启调试模式
     */
    public final void setDebug(boolean isDebug) {
        logger.info("set debug :" + isDebug);
        this.isDebug = isDebug;
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
            fileInfo = new FileInfo(fileId, fileType, replaceVariable(outputPath));
            fileInfos.put(key, fileInfo);
        }
        fileInfo.addFile(fileName);
    }

    private String getKey(String fileId, String fileType) {
        return fileId + SEPARATOR_KEY + fileType.toLowerCase();
    }

    /**
     * 执行合并
     * 
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
            final String mergedFileName = getMergedFileName(fileId, fileType);

            // 解决相对路径问题
            if ("css".equalsIgnoreCase(fileType)) {
                merger.merge(fileInfo.outputPath + File.separator + mergedFileName, charset,
                        new Merger.Handle() {
                            public String process(File file, String content, File destFile) {
                                return replaceCss(content, file.getParent(),
                                        destFile.getParentFile());
                            }
                        });
            } else if ("js".equalsIgnoreCase(fileType)) {
                merger.merge(fileInfo.outputPath + File.separator + mergedFileName, charset,
                        new Merger.Handle() {
                            public String process(File file, String content, File destFile) {
                                return replaceJs(content, file.getParent(),
                                        destFile.getParentFile());
                            }
                        });
            }
            if (needCompress) {
                // 执行压缩
                CompressorFactory.createFileCompressor()
                        .compress(
                                fileInfo.outputPath + File.separator + mergedFileName,
                                getCompressedFileName(fileInfo.outputPath, mergedFileName,
                                        fileType, File.separator), charset,
                                needDeleteSourceFileForCompress, fileType);
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
            return getCompressedFileName(fileInfo.outputPath, outputFileName, fileType, "/");
        } else {
            return getCompressedFileName(".", outputFileName, fileType, "/");
        }
    }

    /**
     * 获取文件压缩后的文件名
     * 
     * @param outputPath 输出目录
     * @param mergedFileName 压缩压缩的文件的文件名
     * @param fileType 文件类型
     * @param fileSeparator 文件路径分隔符
     * @return 文件压缩后的文件名
     */
    private String getCompressedFileName(String outputPath, String mergedFileName, String fileType,
            String fileSeparator) {
        StringBuilder out = new StringBuilder();
        out.append(outputPath);
        out.append(fileSeparator);
        String fileName = mergedFileName
                .substring(0, mergedFileName.length() - EXT_MERGED.length());
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
        // logger.info(out.toString());
        return out.toString();
    }

    /**
     * 获取合并后的文件路径
     * 
     * @param fileId 文件标识
     * @param fileType 文件类型
     * @return 合并后的完整文件名
     */
    String getMergedFileName(String fileId, String fileType) {
        StringBuilder fileName = new StringBuilder();
        fileName.append(fileId);
        fileName.append(".");
        fileName.append(fileType);
        fileName.append(EXT_MERGED);
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
        String htmlOutlineCode; // 外联html内容
        private Map<String, String> inlineCodes;// 内联html内容

        FileInfo(String fileId, String fileType, String outputPath) {
            super();
            this.fileId = fileId;
            this.fileType = fileType;
            this.outputPath = outputPath;
            files = new ArrayList<String>();
            inlineCodes = new HashMap<String, String>();
        }

        void addInlineCode(String inlinePath, String code) {
            inlineCodes.put(inlinePath, code);
        }

        String getInlineCode(String inlinePath) {
            return inlineCodes.get(inlinePath);
        }

        void addFile(String fileName) {
            files.add(fileName);
        }

        @Override
        public String toString() {
            StringBuilder msg = new StringBuilder();
            msg.append("fileInfo:{id:");
            msg.append(fileId);
            msg.append(",type:");
            msg.append(fileType);
            msg.append(",outPath:");
            msg.append(outputPath);
            msg.append(",outlineCode:");
            msg.append(htmlOutlineCode);
            msg.append(",inlineCodes:");
            msg.append(inlineCodes);
            msg.append("}");
            return msg.toString();
        }
    }

    /**
     * 获取html脚本
     * 
     * @param id 文件标识
     * @param type 文件类型
     * @param inlinePath 内联时该标签所在目录。为空时不进行内联。默认为空。
     * @param webRootPath web应用物理路径
     * 
     * @return html脚本
     */
    public final String getHTMLCode(String id, String type, String inlinePath, String webRootPath) {
        if (isDebug) {
            // 调试模式，不合并
            return getOutlineHTMLCodes(id, type);
        } else {
            // 非调试模式，返回合并后结果
            if (!Utils.isEmpty(inlinePath)) {
                return combileInlineHTMLCode(id, type, inlinePath, webRootPath);
            } else {
                return combileOutlineHTMLCode(id, type);
            }
        }
    }

    private String getOutlineHTMLCodes(String id, String type) {
        FileInfo fileInfo = fileInfos.get(getKey(id, type));
        String content = "";
        if (null != fileInfo) {
            content = fileInfo.htmlOutlineCode;
            if (Utils.isEmpty(content)) {
                // 获取数据并保存
                List<String> files = fileInfo.files;
                StringBuilder html = new StringBuilder();
                if (!Utils.isEmpty(files)) {
                    for (String file : files) {
                        html.append(getOutlineCode(file, type));
                        html.append("\n");
                    }
                }
                content = html.toString();
                fileInfo.htmlOutlineCode = content;
                logger.info(fileInfo);
            }
        }

        return content;
    }

    private String combileOutlineHTMLCode(String id, String type) {
        FileInfo fileInfo = fileInfos.get(getKey(id, type));
        String content = "";
        if (null != fileInfo) {
            content = fileInfo.htmlOutlineCode;
            if (Utils.isEmpty(content)) {
                // 获取数据并保存
                content = getOutlineCode(getOutputFileName4web(id, type), type);
                fileInfo.htmlOutlineCode = content;
                logger.info(fileInfo);
            }
        }

        return content;
    }

    private String combileInlineHTMLCode(String id, String type, String inlinePath,
            String webRootPath) {
        FileInfo fileInfo = fileInfos.get(getKey(id, type));
        String content = "";
        if (null != fileInfo) {
            content = fileInfo.getInlineCode(inlinePath);
            if (Utils.isEmpty(content)) {
                // 获取数据并保存
                content = getInlineCode(id, type, webRootPath, new File(webRootPath + inlinePath));
                fileInfo.addInlineCode(inlinePath, content);
                logger.info(fileInfo);
            }
        }

        return content;
    }

    private String getOutlineCode(String outputFileName, String type) {
        StringBuilder html = new StringBuilder();
        if (WebFileManager.FILE_TYPE_JS.equalsIgnoreCase(type)) {
            // js文件
            // 文件方式外联
            html.append("<script type=\"text/javascript\" src=\"");
            html.append(outputFileName);
            html.append("\"></script>");

        } else if (WebFileManager.FILE_TYPE_CSS.equalsIgnoreCase(type)) {
            // css文件
            // 文件方式外联
            html.append("<link type=\"text/css\" rel=\"stylesheet\" href=\"");
            html.append(outputFileName);
            html.append("\"/>");

        } else {
            logger.warn("The file type is not expected(expected:js/css)! type:" + type);
        }
        return html.toString();
    }

    private String getInlineCode(String id, String type, String webRootPath, File pagePath) {
        StringBuilder html = new StringBuilder();
        String outputFileName = getOutputFileName4web(id, type);
        if (outputFileName.startsWith("/")) {
            outputFileName = outputFileName.substring(1);
            int index = outputFileName.indexOf("/");
            if (-1 < index) {
                outputFileName = webRootPath + outputFileName.substring(index);
            }
        } else {
            outputFileName = webRootPath + File.separator + outputFileName;
        }

        if (WebFileManager.FILE_TYPE_JS.equalsIgnoreCase(type)) {
            // js文件
            // 字符串方式内联
            html.append("<script type=\"text/javascript\">");
            String content = replaceJs(new File(outputFileName), pagePath);
            if (content.startsWith(FileUtil.FILE_HEAD_UTF8_STR)) {
                html.append(content.substring(FileUtil.FILE_HEAD_UTF8_STR.length()).getBytes());
            } else {
                html.append(content);
            }

            html.append("</script>");
        } else if (WebFileManager.FILE_TYPE_CSS.equalsIgnoreCase(type)) {
            // css文件
            // 字符串方式内联
            html.append("<style type=\"text/css\">");
            String content = replaceCss(new File(outputFileName), pagePath);
            if (content.startsWith(FileUtil.FILE_HEAD_UTF8_STR)) {
                html.append(content.substring(FileUtil.FILE_HEAD_UTF8_STR.length()).getBytes());
            } else {
                html.append(content);
            }
            html.append("</style>");
        } else {
            logger.warn("The file type is not expected(expected:js/css)! type:" + type);
        }

        return html.toString();
    }

    String replaceJs(String content, String contentParentPath, File pagePath) {       
        if (!Utils.isEmpty(content)) {
            // 约定：js中只允许以下方式使用相对路径
            // 1、对象的src属性中可以包含相对路径。如 
            //      .src="a.gif";
            //      .src='../a.gif';
            Pattern pattern = Pattern.compile("(\\S+?\\.src\\s*?=\\s*?['\"])(.*?)(['\"]\\s*?;)",
                    Pattern.DOTALL);
            Matcher matcher = pattern.matcher(content);
            int iPosStart = 0;
            int iPosEnd = content.length();
            StringBuffer result = new StringBuffer();
            while (matcher.find()) {
                result.append(content.substring(iPosStart, matcher.start()));
                result.append(matcher.group(1));
                File contentFile = new File(contentParentPath + File.separator + matcher.group(2));
                String abstractPath = FileUtil.generateAbstractPath(pagePath, contentFile);
                result.append(abstractPath);
                result.append(matcher.group(3));
                iPosStart = matcher.end();
            }
            result.append(content.substring(iPosStart, iPosEnd));

            return result.toString();
        } else {
            return "";
        }
    }

    private String replaceJs(File srcFile, File pagePath) {
        String content = FileUtil.getFileContent(srcFile);
        String contentPath = srcFile.getParent();
        return replaceJs(content, contentPath, pagePath);
    }

    String replaceCss(String content, String contentParentPath, File pagePath) {
        if (!Utils.isEmpty(content)) {
            // 将所有相对路径进行转换
            // 对图片的引用格式可能为：url(a.gif)、url(i/a.gif)、url(./i/a.gif)、url(../i/a.gif)，路径可能由单引号(')、双引号(")包裹
            Pattern pattern = Pattern.compile("([\\W]url\\(['\"]*)(.*?)(['\"]*\\))",
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher matcher = pattern.matcher(content);
            int iPosStart = 0;
            int iPosEnd = content.length();
            StringBuffer result = new StringBuffer();
            String pre = null;
            String url = null;
            String tail = null;
            while (matcher.find()) {
                result.append(content.substring(iPosStart, matcher.start()));
                pre = matcher.group(1);
                url = matcher.group(2);
                tail = matcher.group(3);
                result.append(pre);
                File contentFile = new File(contentParentPath + File.separator + url);
                String abstractPath = FileUtil.generateAbstractPath(pagePath, contentFile);
                result.append(abstractPath);
                result.append(tail);
                iPosStart = matcher.end();
            }
            result.append(content.substring(iPosStart, iPosEnd));

            return result.toString();
        } else {
            return "";
        }
    }

    private String replaceCss(File srcFile, File pagePath) {
        String content = FileUtil.getFileContent(srcFile, "UTF-8");
        String contentPath = srcFile.getParent();
        return replaceCss(content, contentPath, pagePath);
    }

}
