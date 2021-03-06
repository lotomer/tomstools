/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.common.merge.manager.datasource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;

import org.tomstools.common.log.Logger;
import org.tomstools.common.merge.manage.WebFileManager;
import org.tomstools.common.util.Utils;

/**
 * @author lotomer
 * @date 2011-12-20 
 * @time 下午05:08:28
 */
public class DefaultWebFileManager extends WebFileManager{
    private static final Logger logger = Logger.getLogger(DefaultWebFileManager.class);
    /** web前台页面文件配置之js */
    private static final String WEB_JS_PREFIX = "web." + FILE_TYPE_JS + ".";
    /** web前台页面文件配置之css */
    private static final String WEB_CSS_PREFIX = "web." + FILE_TYPE_CSS + ".";
    /** 默认配置文件 */
    private static final String DEFAULT_CONFIG = "webFileConfig.properties";
    /** 多个文件之间的分隔符 */
    private static final String SEPARATOR = ",";
    private static final String KEY_WEB_CONFIG_FILE = "WEB_CONFIG_FILE";

    public void init() throws Exception {
        logger.info("init");
        Properties prop = new Properties();
        String configFile = getVariableValue(KEY_WEB_CONFIG_FILE);
        if (Utils.isEmpty(configFile)){
            configFile = DEFAULT_CONFIG;
        }
        logger.info("config file:" + configFile);
        InputStream file = this.getClass().getClassLoader().getResourceAsStream(configFile);
        if (null == file){
            throw new Exception("The file " + configFile + " donnot exists!");
        }
        try {
            prop.load(file);
            for (Entry<Object, Object> config : prop.entrySet()) {
                String key = (String) config.getKey();
                // 根据配置项的前缀特征提取相应的前台配置文件信息
                if (key.startsWith(WEB_JS_PREFIX)) {
                    // js文件配置
                    key = key.substring(WEB_JS_PREFIX.length());
                    addFile(key, FILE_TYPE_JS, (String) config.getValue());
                } else if (key.startsWith(WEB_CSS_PREFIX)) {
                    // css文件配置
                    key = key.substring(WEB_CSS_PREFIX.length());
                    addFile(key, FILE_TYPE_CSS, (String) config.getValue());
                } else if ("debug".equals(key)){
                    if ("false".equals(config.getValue().toString())){
                        setDebug(false);
                    }
                } else {
                    // do nonthing
                }
            }
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    private void addFile(String fileId, String fileType, String fileNames) {
        if (!Utils.isEmpty(fileId)){
            //判断文件列表是否为空
            if (!Utils.isEmpty(fileNames)){
                String[] files = fileNames.split(SEPARATOR);
                // 第一个是输出路径，第二个开始是文件名
                int index = 0;
                String outputPath = null;
                if (0 < files.length){
                    outputPath = files[index++];
                }
                if (Utils.isEmpty(outputPath)){
                    logger.warn("not define output path!");
                    return;
                }
                for (int i = index; i < files.length; i++) {
                    fileMergeControler.addFile(fileId, files[i], fileType, outputPath);
                }

            }else{
                logger.warn("There has no files configed for " + fileId);
            }
        }
    }
}
