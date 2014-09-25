/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tomstools.common.Logger;
import org.tomstools.common.Utils;
import org.tomstools.crawler.config.Target;

/**
 * 数据结果操作对象：文件方式。一个记录一个文件
 * 
 * @author admin
 * @date 2014年9月25日
 * @time 下午10:08:08
 * @version 1.0
 */
public class SingleRecordFileResultDAO implements ResultDAO {
    private final static Logger LOGGER = Logger.getLogger(SingleRecordFileResultDAO.class);
    private static final String FILE_LATEST_URL = ".latest";
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(
            "yyyyMMddHHmmss");
    private String rootDir;
    private Map<String, Writer> writers;
    private Map<String, String> outFileNames;
    private String separator;
    private Set<String> specialWords;
    private String newLine;
    private String fileCharset;
    private int fileCount;

    /**
     * 默认构造函数
     * 
     * @param rootDir 保存数据的根目录
     * @throws IOException
     * @since 1.0
     */
    public SingleRecordFileResultDAO(String rootDir, String separator, String[] specialWords)
            throws IOException {
        super();
        this.rootDir = rootDir;
        this.separator = separator;
        this.specialWords = new HashSet<String>();
        if (null != specialWords && 0 < specialWords.length) {
            for (int i = 0; i < specialWords.length; i++) {
                this.specialWords.add(specialWords[i]);
            }
        }
        this.newLine = "\n";
//        this.specialWords.add("\r");
//        this.specialWords.add("\n");
//        this.specialWords.add(newLine);
        this.specialWords.add(separator);
        writers = new HashMap<String, Writer>();
        outFileNames = new HashMap<>();
        // targetTitles = new HashMap<>();
        // isPrepareWrite4targets = new HashMap<>();
    }

    public void save(Target target, String url, Map<String, String> datas) {
        if (!Utils.isEmpty(datas)) {
            String[] titles = target.getContentExtractor().getTitles();
            if (null != titles && 0 < titles.length) {
                // 还没有写过文件，则先输出表头
                String filePath = this.outFileNames.get(target.getName());
                if (null == fileCharset) {
                    fileCharset = Charset.defaultCharset().displayName();
                }
                String fileName = String.valueOf(fileCount);
                String content = null;
                if (1 < titles.length){
                    // 第一个是标题
                    fileName = datas.get(titles[0]);
                    content = datas.get(titles[1]);
                }else{
                    content = datas.get(titles[0]);
                }
                FileOutputStream fos = null;
                Writer w = null;
                try {
                    fos = new FileOutputStream(filePath+ File.separator + fileName + ".dat", false);
                    w = new OutputStreamWriter(fos, fileCharset);
                    if ("UTF-8".equalsIgnoreCase(fileCharset)) {
                        // UTF-8的csv用excel打开时出现乱码，需要显示输出BOM
                        w.write(new String(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF }));
                    }
                    
                    w.write(content);
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }finally{
                    Utils.close(w);
                    Utils.close(fos);
                }
            } else {
                LOGGER.error("The content extractor need titles! " + target.getName());
            }
        }
    }

    /**
     * 剔除特殊字符。如换行符等
     * 
     * @param value 待剔除特殊字符的字符串
     * @return 剔除特殊字符之后的字符串
     * @since 1.0
     */
    protected String trimSpecial(String value) {
        if (!Utils.isEmpty(value)) {
            for (String word : this.specialWords) {
                value = value.replaceAll(word, " ");
            }

            return value.trim();
        } else {
            return "";
        }
    }

    /**
     * 保存主页面最后处理过的标识数据
     * 
     * @param target 目标对象
     * @param flagDatas 处理过的标识数据
     * @since 1.0
     */
    private void saveProcessedFlagDatas(Target target, Collection<String> flagDatas) {
        // LOGGER.info("saveProcessedFlagDatas. " + targetName + "," +
        // flagDatas);
        if (Utils.isEmpty(target.getName()) || Utils.isEmpty(flagDatas)) {
            return;
        }
        File file = new File(getFlagFileName(target.getName(), ""));
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String flagData : flagDatas) {
                if (null == flagData) {
                    writer.write("");
                } else {
                    writer.write(flagData);
                }
                writer.write(newLine);
            }

            writer.close();
            writer = null;
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public Collection<String> prepare(Target target) throws Exception {
        String flag = SIMPLE_DATE_FORMAT.format(new Date());
        if (null == writers.get(target.getName())) {
            // 结果输出
            File file = new File(getFilePath(target.getName()));
            if (!file.exists()) {
                file.mkdirs();
            }
            outFileNames.put(target.getName(), file.getAbsolutePath());
        }
        // 标识文件
        List<String> datas = new ArrayList<String>();
        File file = new File(getFlagFileName(target.getName(), ""));
        if (file.exists()) {
            // 将标识文件数据备份
            BufferedWriter writer = new BufferedWriter(new FileWriter(getFlagFileName(
                    target.getName(), flag)));
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while (null != (line = reader.readLine())) {
                datas.add(line);
                writer.write(line);
                writer.write(newLine);
            }
            Utils.close(reader);
            Utils.close(writer);
        }

        return datas;
    }

    private String getFilePath(String targetName) {
        return rootDir + File.separator + targetName + File.separator;
    }

    private String getFlagFileName(String targetName, String flag) {
        return getFilePath(targetName) + flag + FILE_LATEST_URL;
    }

    public void finish(Target target, Collection<String> newFlagDatas) {
        Writer writer = writers.get(target.getName());
        if (null != writer) {
            try {
                writer.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
            writers.remove(target.getName());
            writer = null;
        }
        // 保存最新处理标识
        saveProcessedFlagDatas(target, newFlagDatas);

        // 进行可能需要的扫尾工作
        if (null != target.getCompletedHandler()) {
            target.getCompletedHandler().handle(outFileNames.get(target.getName()));
        }
    }

    /**
     * @return 返回 separator
     * @since 1.0
     */
    public final String getSeparator() {
        return separator;
    }

    /**
     * @param newLine 设置 newLine
     * @since 1.0
     */
    public final void setNewLine(String newLine) {
        this.newLine = newLine;
        this.specialWords.add(newLine);
    }

    /**
     * @param fileCharset 设置 输出文件编码格式。默认GBK
     * @since 1.0
     */
    public final void setFileCharset(String fileCharset) {
        this.fileCharset = fileCharset;
    }

    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        System.out.println(sdf.format(new Date()));
    }
}
