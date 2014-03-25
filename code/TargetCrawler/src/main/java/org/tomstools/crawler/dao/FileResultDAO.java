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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.tomstools.crawler.common.Logger;
import org.tomstools.crawler.common.Utils;
import org.tomstools.crawler.config.Target;

/**
 * 数据结果操作对象：文件方式
 * 
 * @author admin
 * @date 2014年3月13日
 * @time 上午10:28:08
 * @version 1.0
 */
public class FileResultDAO implements ResultDAO {
    private final static Logger LOGGER = Logger.getLogger(FileResultDAO.class);
    private static final String FILE_LATEST_URL = ".latest";
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
    private String rootDir;
    private Map<String, Writer> writers;
    private String separator;
    private Set<String> specialWords;
    private String newLine;

    /**
     * 默认构造函数
     * 
     * @param rootDir 保存数据的根目录
     * @throws IOException
     * @since 1.0
     */
    public FileResultDAO(String rootDir, String separator, String[] specialWords)
            throws IOException {
        super();
        this.rootDir = rootDir;
        this.separator = separator;
        this.specialWords = new HashSet<String>();
        if (null != specialWords && 0 < specialWords.length){
            for (int i = 0; i < specialWords.length; i++) {
                this.specialWords.add(specialWords[i]);
            }
        }
        this.newLine = "\n";
        this.specialWords.add("\r");
        this.specialWords.add("\n");
        this.specialWords.add(newLine);
        this.specialWords.add(separator);
        writers = new HashMap<String, Writer>();
    }

    public void save(Target target, String url, Map<String, String> datas) {
        // 保存结果数据
        Writer writer = writers.get(target.getName());
        if (null != writer) {
            try {
                writer.write(getLineValue(url, datas));
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 获取一行数据
     * 
     * @param name 名称
     * @param url 对应的url
     * @param datas 数据内容
     * @return 整合成一行数据
     * @since 1.0
     */
    protected String getLineValue(String url, Map<String, String> datas) {
        if (Utils.isEmpty(datas.keySet())) {
            return "";
        }
        StringBuilder line = new StringBuilder();
        boolean isFirst = true;
        for (Entry<String, String> entry : datas.entrySet()) {
            if (!isFirst) {
                line.append(getSeparator());
            }else{
                isFirst = false;
            }
            line.append(trimSpecial(entry.getValue()));
        }
        line.append("\n");
        return line.toString();
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
        }else{
            return "";
        }
    }

    public void saveProcessedFlagDatas(Target target, Collection<String> flagDatas) {
        //LOGGER.info("saveProcessedFlagDatas. " + targetName + "," + flagDatas);
        if (Utils.isEmpty(target.getName()) || Utils.isEmpty(flagDatas)) {
            return;
        }
        File file = new File(getFlagFileName(target.getName()));
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String flagData : flagDatas) {
                if (null == flagData){
                    writer.write("");
                }else{
                    writer.write(flagData);
                }
                writer.write("\n");
            }
            
            writer.close();
            writer = null;
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public Collection<String> prepare(Target target) throws Exception {
        if (null == writers.get(target.getName())) {
            File file = new File(getDataFileName(target.getName()));
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(file, false);
            OutputStreamWriter w = new OutputStreamWriter(fos, "GB2312");
            // 输出表头
            String[] titles = target.getContentExtractor().getTitles();
            if (null != titles){
                boolean isFirst = true;
                for (String title : titles) {
                    if (!isFirst){
                        w.write(separator);
                    }
                    if (!Utils.isEmpty(title)){
                        w.write(title);
                        isFirst = false;
                    }
                }
            }
            w.write("\n");
            //BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            writers.put(target.getName(), w);
        }
        List<String> datas = new ArrayList<String>();
        File file = new File(getFlagFileName(target.getName()));
        if (file.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while (null != (line = reader.readLine())){
                datas.add(line);
            }
            reader.close();
            reader = null;
        }
        
        return datas;
    }

    private String getFlagFileName(String targetName) {
        return rootDir + File.separator + targetName+ File.separator + FILE_LATEST_URL;
    }

    private String getDataFileName(String targetName) {
        return rootDir + File.separator + targetName+ File.separator+targetName + "_" + SIMPLE_DATE_FORMAT.format(new Date()) + ".csv";
    }

    public void finish(Target target) {
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

    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        System.out.println(sdf.format(new Date()));
    }
}