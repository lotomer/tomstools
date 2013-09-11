/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.tomstools.common.log.Logger;

public class SysConfig {
    private final static Logger logger = Logger.getLogger(SysConfig.class);
    private final static String CONFIG_FILE_NAME = "config.properties";
    private final static String KEY_OTHER_FILES = "other.files";
    private static final String SEPARATOR_OTHER_FILES = ",";
    private static SysConfig instance = new SysConfig();
    private Map<String, String> configs = new HashMap<String, String>();
    /** 监视器每轮休息时间 */
    private long monitorSleepSeconds;
    /** 任务执行每轮休息时间 */
    private long taskSleepSeconds;
    /** 任务分配每轮休息时间 */
    private long dispatchSleepSeconds;
    /** 批量保存的大小 */
    private int batchSizeForSave;
    /** 最大失败次数 */
    private int maxFailCnt;
    /** 缓存服务器列表 */
    private String cacheServerList;
    /** 索引文件根目录 */
    private String indexRootPath;
    /** web服务器地址 */
    private String webServerAddress;
    /** 原始文件存储路径 */
    private String baseFilePath;
    /** 联想题诗词索引文件路径 */
    private String inputNotifyIndexPath;

    /** 相关搜索词索引文件路径 */
    private String relateIndexPath;

    /** 默认系统列表，多个系统名之间以英文逗号分隔 */
    private String defaultSystems;

    /** 跳转url */
    private String url;
    /** 用户信息有效时间 */
    private long userInfoSurvivalTimes;
    /** 标题长度 */
    private int titleLength;
    /** 每个节点能同时拥有的最大批次数 */
    private int maxNodeDispatchSize;
    /** 待移出的特殊字符 */
    private String specialCharactersToBeRemoved;
    /** 局点名称 */
    private String localName;
    /** 局点显示名称 */
    private String localDisplayName;
    /** 构建时间 */
    private String buildTime;
    /** web部署名 */
    private String webContextName;

//    private List<SystemBean> systems;
//    private List<SearchFieldBean> searchFields;
//    private List<ServerInfoBean> serverInfos;
    private boolean debug;
    private String webIndexPage;

    public static SysConfig getInstance() {
        return instance;
    }

//    public final List<SystemBean> getSystems() {
//        return systems;
//    }
//
//    public void loadDBDatas() {
//        DataAccessor dataAccessor = DataAccessorFactory.getInstance().getDataAccessorBuilder()
//                .newDataAccessor();
//        try {
//            dataAccessor.open();
//            // 加载数据库中的配置数据
//            loadFromDB(dataAccessor);
//
//            // 加载专业词库
//            loadProfessionalWords(dataAccessor);
//
//            // 加载其他全局数据
//            loadOtherDatas(dataAccessor);
//        }
//        catch (DAOException e) {
//            logger.error(e.getMessage(), e);
//        }
//        finally {
//            dataAccessor.close();
//        }
//    }

    private SysConfig() {
        // 加载文件中的配置数据
        loadFromFile();
    }

//    private void loadOtherDatas(DataAccessor dataAccessor) {
//        // 加载系统列表
//        try {
//            systems = new CommonDao(dataAccessor).getSystemList();
//        }
//        catch (DAOException e) {
//            logger.error(e.getMessage(), e);
//            systems = Collections.emptyList();
//        }
//
//        // 加载系统搜索字段信息
//        loadSearchFields(dataAccessor);
//
//        // 加载服务器信息
//        loadServerInfos(dataAccessor);
//        
//        //加载前台结果排序器
//        loadWebSort(dataAccessor);
//    }
    
//    private void loadWebSort(DataAccessor dataAccessor){
//        try {
//            String  sorts= new CommonDao(dataAccessor).getWebSort();
//            this.configs.put("sorter", sorts);
//        }
//        catch (DAOException e) {
//            logger.error(e.getMessage(), e);
//            serverInfos = Collections.emptyList();
//        }
//    }
//
//    /** 加载服务器信息 */
//    private void loadServerInfos(DataAccessor dataAccessor) {
//        try {
//            serverInfos = new CommonDao(dataAccessor).getServerInfoList();
//        }
//        catch (DAOException e) {
//            logger.error(e.getMessage(), e);
//            serverInfos = Collections.emptyList();
//        }
//    }
//
//    /** 加载系统搜索字段信息 */
//    private void loadSearchFields(DataAccessor dataAccessor) {
//        try {
//            searchFields = new CommonDao(dataAccessor).getSearchFields();
//        }
//        catch (DAOException e) {
//            logger.error(e.getMessage(), e);
//            searchFields = Collections.emptyList();
//        }
//
//        // 必须配置搜索字段，否则会报运行时异常
//        if (Util.isEmpty(searchFields)) {
//            throw new RuntimeException(
//                    "The search fields is empty! Please config it in the database! The table name is CORE_COMMON_SEARCH_FIELDS.");
//        }
//    }

    /**
     * 从配置文件中加载配置项 首先加载配置文件CONFIG_FILE_NAME中的配置项，再根据其中的KEY_OTHER_FILES配置项加载其他配置文件中的配置项
     * 
     * @see #CONFIG_FILE_NAME
     * @see #KEY_OTHER_FILES
     */
    private void loadFromFile() {
        InputStream file = this.getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
        if (null == file) {
            logger.error("The file named " + CONFIG_FILE_NAME + " don't exists!");
            return;
        }

        Properties prop = new Properties();
        try {
            prop.load(file);
            for (Entry<Object, Object> entry : prop.entrySet()) {
                configs.put((String) entry.getKey(), (String) entry.getValue());
            }

            // 加载其他配置文件中的配置项
            String otherFiles = configs.get(KEY_OTHER_FILES);
            if (!Utils.isEmpty(otherFiles)) {
                String[] files = otherFiles.split(SEPARATOR_OTHER_FILES);
                for (String fileName : files) {
                    doLoadFromOtherFile(fileName);
                }
            }
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        try {
            file.close();
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        // 校验文件配置项
        checkFileConfig();
    }

    // 校验文件配置项
    private void checkFileConfig() {
        if (null == configs) {
            logger.error("The config is null!");
        }

        // 局点名称
//        localName = getString(Constant.FILE_CONFIG_LOCAL_NAME, "");
//
//        // 局点显示名称
//        localDisplayName = getString(Constant.FILE_CONFIG_LOCAL_DISPLAYNAME, "");
//
//        // 构建时间
//        buildTime = getString(Constant.FILE_CONFIG_BUILD_TIME, "");

        // 调试模式
        String debugStr = getString("debug", "true");
        if ("true".equalsIgnoreCase(debugStr)) {
            debug = true;
        } else {
            debug = false;
        }
    }

    private String getString(String configName, String defaultValue) {
        String tmpValue = configs.get(configName);
        if (Utils.isEmpty(tmpValue)) {
            logger.warn("The item " + configName + " is not configed in file! Use default value:"
                    + defaultValue);
            return defaultValue;
        } else {
            return tmpValue;
        }
    }

    private long getLong(String configName, long defaultValue) {
        String tmpValue = configs.get(configName);
        if (Utils.isEmpty(tmpValue)) {
            logger.warn("The item " + configName
                    + " is not configed in database! Use default value:" + defaultValue);
            return defaultValue;
        } else {
            try {
                return Long.valueOf(tmpValue);
            }
            catch (NumberFormatException e) {
                logger.warn(e.getMessage(), e);
                return defaultValue;
            }
        }
    }

    private int getInt(String configName, int defaultValue) {
        String tmpValue = configs.get(configName);
        if (Utils.isEmpty(tmpValue)) {
            logger.warn("The item " + configName
                    + " is not configed in database! Use default value:" + defaultValue);
            return defaultValue;
        } else {
            try {
                return Integer.valueOf(tmpValue);
            }
            catch (NumberFormatException e) {
                logger.warn(e.getMessage(), e);
                return defaultValue;
            }
        }
    }

    private void doLoadFromOtherFile(String fileName) {
        InputStream file = this.getClass().getClassLoader().getResourceAsStream(fileName);
        if (null == file) {
            logger.error("The file named " + fileName + " don't exists!");
            return;
        }
        Properties prop = new Properties();
        try {
            prop.load(file);
            for (Entry<Object, Object> entry : prop.entrySet()) {
                configs.put((String) entry.getKey(), (String) entry.getValue());
            }
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        try {
            file.close();
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

//    private void loadProfessionalWords(DataAccessor dataAccessor) {
//        // 从数据库获取专业字典
//        List<String> keywords = new CommonBusi(dataAccessor).loadProfessionalWords();
//        // 将专业字典信息加载到分词字典中
//        // Util.loadDictionaryExtendWords(keywords);
//        if (!Util.isEmpty(keywords)) {
//            AnalyzerFactory.getDefaultAnalyzer().loadExtendWords(keywords);
//        }
//    }

    /**
     * 从数据库加载
     * 
     * @param dataAccessor
     */
//    private void loadFromDB(DataAccessor dataAccessor) {
//        logger.debug("load configs from database.");
//        Map<String, String> tmpConfigs = new CommonBusi(dataAccessor).getConfigFromDB();
//        configs.putAll(tmpConfigs);
//        // 数据库中的配置项校验
//        checkDBConfig();
//    }
//
//    // 数据库中的配置项校验
//    private void checkDBConfig() {
//        if (null == configs) {
//            logger.error("The config is null!");
//        }
//
//        // 监视器每轮休息时间
//        monitorSleepSeconds = getLong(Constant.DB_CONFIG_MONITOR_SLEEP_SECONDS,
//                Constant.DEFAULT_SLEEP_TIME_SECOND);
//
//        // 任务执行每轮休息时间
//        taskSleepSeconds = getLong(Constant.DB_CONFIG_TASK_SLEEP_SECONDS,
//                Constant.DEFAULT_SLEEP_TIME_SECOND);
//
//        // 任务分配每轮休息时间
//        dispatchSleepSeconds = getLong(Constant.DB_CONFIG_DISPATCH_SLEEP_SECONDS,
//                Constant.DEFAULT_SLEEP_TIME_SECOND);
//
//        // 最大失败次数
//        maxFailCnt = getInt(Constant.DB_CONFIG_MAX_FAIL_CNT, Constant.DEFAULT_MAX_FAIL_CNT);
//
//        // 批量保存的大小
//        batchSizeForSave = getInt(Constant.DB_CONFIG_BATCH_SIZE_FOR_SAVE,
//                Constant.DEFAULT_BATCH_SIZE);
//
//        // 缓存服务器列表
//        cacheServerList = getString(Constant.DB_CONFIG_CACHE_SERVER_LIST, "");
//
//        // 索引文件根目录
//        indexRootPath = getString(Constant.DB_CONFIG_INDEX_ROOT_PATH, "");
//
//        // web服务器地址
//        webServerAddress = getString(Constant.DB_CONFIG_WEB_SERVER_ADDRESS, "");
//
//        // 原始文件存储路径
//        baseFilePath = getString(Constant.DB_CONFIG_BASE_FILE_PATH, "");
//
//        // 联想题诗词索引文件路径
//        inputNotifyIndexPath = getString(Constant.DB_CONFIG_INPYUT_NOTIFY_INDEX_PATH, "");
//
//        // 相关搜索索引文件路径
//        relateIndexPath = getString(Constant.DB_CONFIG_RELATE_INDEX_PATH, "");
//
//        // 默认系统列表
//        defaultSystems = getString(Constant.DB_CONFIG_DEFAULT_SYSTEMS, "");
//
//        // 跳转URL
//        url = getString(Constant.DB_CONFIG_URL, "");
//
//        // 用户信息有效时长
//        userInfoSurvivalTimes = getLong(Constant.DB_CONFIG_USERINFO_SURVIVAL_TIMES,
//                Constant.DEFAULT_USERINFO_SURVIVAL_TIMES);
//
//        // 标题长度
//        titleLength = getInt(Constant.DB_CONFIG_TITLE_LENGTH, Constant.DEFAULT_TITLE_LENGTH);
//
//        // 节点最大任务批次数
//        maxNodeDispatchSize = getInt(Constant.DB_CONFIG_MAX_NODE_DISPATCH_SIZE,
//                Constant.DEFAULT_MAX_NODE_DISPATCH_SIZE);
//
//        // 缓存服务器列表
//        specialCharactersToBeRemoved = getString(
//                Constant.DB_CONFIG_SPECIAL_CHARACTERS_TO_BE_REMOVED, null);
//
//        // web应用部署名
//        webContextName = getString(Constant.DB_CONFIG_WEB_CONTEXT_NAME,
//                Constant.DEFAULT_WEB_CONTEXT_NAME);
//
//        // web应用首页
//        webIndexPage = getString(Constant.DB_CONFIG_WEB_INDEX_PAGE, Constant.DEFAULT_WEB_INDEX_PAGE);
//    }

    /**
     * @return the configs
     */
    public final Map<String, String> getConfigs() {
        return configs;
    }

    /**
     * @return the monitorSleepSeconds
     */
    public final long getMonitorSleepSeconds() {
        return monitorSleepSeconds;
    }

    /**
     * @return the taskSleepSeconds
     */
    public final long getTaskSleepSeconds() {
        return taskSleepSeconds;
    }

    /**
     * @return the dispatchSleepSeconds
     */
    public final long getDispatchSleepSeconds() {
        return dispatchSleepSeconds;
    }

    /**
     * @return the batchSizeForSave
     */
    public final int getBatchSizeForSave() {
        return batchSizeForSave;
    }

    /**
     * @return the maxFailCnt
     */
    public final int getMaxFailCnt() {
        return maxFailCnt;
    }

    /**
     * @return the cacheServerList
     */
    public final String getCacheServerList() {
        return cacheServerList;
    }

    /**
     * @return the indexRootPath
     */
    public final String getIndexRootPath() {
        return indexRootPath;
    }

    public String getRelateIndexPath() {
        return relateIndexPath;
    }

    /**
     * @return the webServerAddress
     */
    public final String getWebServerAddress() {
        return webServerAddress;
    }

    /**
     * 
     * @return the baseFilePath
     */
    public String getBaseFilePath() {
        return baseFilePath;
    }

    public String getInputNotifyIndexPath() {
        return inputNotifyIndexPath;
    }

    /** 获取默认系统列表，多个系统名之间以英文逗号分隔 */
    public final String getDefaultSystems() {
        return defaultSystems;
    }

    /** 获取跳转url */
    public final String getUrl() {
        return url;
    }

    /**
     * 获取指定KEY对应的值
     * 
     * @param key
     * @return 配置的值。没有配置则返回null
     */
    public String getOtherValue(String key) {
        return configs.get(key);
    }

    public long getUserInfoSurvivalTimes() {
        return userInfoSurvivalTimes;
    }

    public int getTitleLength() {
        return titleLength;
    }

    /**
     * 根据系统编号获取该系统的搜索字段信息
     * 
     * @param systemId 系统编号
     * @return 搜索字段列表。不会为null
     */
//    public List<SearchFieldBean> getSearchFields(int systemId) {
//        List<SearchFieldBean> result = new ArrayList<SearchFieldBean>();
//        for (SearchFieldBean searchField : searchFields) {
//            if (systemId == searchField.getSystem_id()) {
//                result.add(searchField);
//            }
//        }
//
//        return result;
//    }

    /**
     * 更新内存中的服务器信息
     * 
     * @param serverInfo 服务器信息
     */
//    public synchronized void updateServerInfo(ServerInfoBean serverInfo) {
//        for (int i = 0; i < serverInfos.size(); ++i) {
//            if (serverInfo.getServerID() == serverInfos.get(i).getServerID()) {
//                serverInfos.remove(i);
//                serverInfos.add(serverInfo);
//            }
//        }
//    }
//
//    /** 获取服务器列表 */
//    public final List<ServerInfoBean> getServerInfos() {
//        return serverInfos;
//    }

    public final int getMaxNodeDispatchSize() {
        return maxNodeDispatchSize;
    }

    /**
     * 获取待移出的特殊字符。多个以英文逗号分隔
     */
    public final String getSpecialCharactersToBeRemoved() {
        return specialCharactersToBeRemoved;
    }

    /**
     * 获取局点名
     * 
     * @return
     */
    public final String getLocalName() {
        return localName;
    }

    /**
     * 获取局点显示名称
     * 
     * @return
     */
    public final String getLocalDisplayName() {
        return localDisplayName;
    }

    /**
     * 获取构建时间
     * 
     * @return
     */
    public final String getBuildTime() {
        return buildTime;
    }

    /**
     * 获取WEB应用部署名
     * 
     * @return
     */
    public final String getWebContextName() {
        return webContextName;
    }

    /**
     * 是否是调试模式
     * 
     * @return true 是调试模式；false 不是调试模式
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * 获取WEB应用的首页
     * 
     */
    public String getWebIndexPage() {
        return webIndexPage;
    }
    /** 获取前台显示的HTML内容需要剔除的特殊字符 */
    public String getHTMLSpecialChars4remove() {
        String value = (String) get("html.special.chars");
        if (null == value) {
            logger.warn("html.special.chars not configed.");
            value = "";
        }
        return value;
    }
    /**
     * 根据配置项获取其对应的配置值
     * 
     * @param key 配置项
     * @return 配置值。如果没有配置则返回null
     */
    private Object get(String key) {
        return configs.get(key);
    }
}
