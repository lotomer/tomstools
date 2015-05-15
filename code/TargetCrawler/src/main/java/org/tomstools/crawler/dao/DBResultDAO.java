/**
 * copyright (a) 2010-2015 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.tomstools.common.Utils;
import org.tomstools.crawler.config.Target;

/**
 * 
 * @author admin
 * @date 2015年5月11日
 * @time 下午2:19:08
 * @version 1.0
 */
public class DBResultDAO extends SqlSessionDaoSupport implements ResultDAO {
    private static final Logger LOGGER = Logger.getLogger(DBResultDAO.class);
    private Map<String, Boolean> isPrepareWrite4targets = new HashMap<String, Boolean>();; // 是否已经准备好写了，true表示准备好了，null或false表示没准备好
    private Map<String, Integer> targetIds = new HashMap<String, Integer>();;
    private Map<String, String[]> targetTitles = new HashMap<String, String[]>();;

    @Override
    public void save(Target target, String url, Map<String, String> datas) {
        if (!Utils.isEmpty(datas)) {
            String[] titles = target.getContentExtractor().getTitles();
            if (null != titles) {
                Boolean isPrepared = isPrepareWrite4targets.get(target.getName());
                // 是否已经尝试过输出。null表示没有尝试过输出；true 已经准备好输出；false 没有准备好输出
                if (null == isPrepared) {
                    // 首先假定没有准备好输出
                    isPrepareWrite4targets.put(target.getName(), false);
                    targetTitles.put(target.getName(), titles);
                    // 保存结果数据
                    Integer id = targetIds.get(target.getName());
                    if (null != id) {
                        // 准备好写数据了
                        isPrepareWrite4targets.put(target.getName(), true);
                        // 输出到文件的内容需要依据表头数据的顺序
                        if (null != id) {
                            saveRow(id, titles, datas);
                        }
                    }
                } else if (isPrepared) {
                    // 输出到文件的内容需要依据表头数据的顺序
                    Integer id = targetIds.get(target.getName());
                    if (null != id) {
                        saveRow(id, titles, datas);
                    }
                }
            } else {
                LOGGER.error("The content extractor need titles! " + target.getName());
            }
        }
    }

    private void saveRow(Integer id, String[] titles, Map<String, String> datas) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        if (null != titles && null != datas) {
            for (int i = 0; i < titles.length; i++) {
                params.put(titles[i], datas.get(titles[i]));
            }
        }

        getSqlSession().insert("insertData", params);
    }

    @Override
    public Collection<String> prepare(Target target) throws Exception {
        List<String> datas = new ArrayList<String>();
        Integer id = targetIds.get(target.getName());
        if (null == id) {
            Map<String, Object> result = getSqlSession().selectOne("getStatus", target.getUrl());
            if (null == result) {
                // 记录当前状态
                saveStatus(target.getUrl(),target.getName(),target.getSiteName(),target.getChannelName(), "");
                result = getSqlSession().selectOne("getStatus", target.getUrl());
            }
            
            if (null != result) {
                id = (Integer) result.get("ID");
                if (null != id) {
                    targetIds.put(target.getName(), id);
                    // 爬取当前状态
                    String status = (String) result.get("STATUS");
                    if (null != status) {
                        datas.add(status);

                        // 获取指定数据
                        List<Object> tops = getSqlSession().selectList("getTops", id);
                        if (null != tops) {
                            for (Object top : tops) {
                                if (top instanceof Map) {
                                    @SuppressWarnings("unchecked")
                                    Map<String, Object> row = (Map<String, Object>) top;
                                    datas.add((String) row.get("STATUS"));
                                }
                            }
                            
                            // 删除所有指定数据
                            int count = getSqlSession().delete("deleteTops", id);
                            LOGGER.info("delete top count: " + count);
                        }
                    }
                }
            }
        }
        return datas;
    }

    @Override
    public void finish(Target target, Collection<String> newFlagDatas) {
        Boolean isPrepared = isPrepareWrite4targets.get(target.getName());
        // 只有准备好了才继续后续的操作
        if (null != isPrepared && isPrepared) {
            // 保存最新处理标识
            saveProcessedFlagDatas(target, newFlagDatas);

            // 进行可能需要的扫尾工作
            if (null != target.getCompletedHandler()) {
                // target.getCompletedHandler().handle(outFileNames.get(target.getName()));
            }
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
        if (Utils.isEmpty(target.getName()) || Utils.isEmpty(flagDatas)) {
            return;
        }

        boolean isFirst = true;
        // 第一行是状态，其他事置顶数据
        for (String flagData : flagDatas) {
            if (isFirst) {
                isFirst = false;
                Integer id = targetIds.get(target.getName());
                if (null != id){
                    updateStatus(id,flagData);
                }else{
                    saveStatus(target.getUrl(),target.getName(),target.getSiteName(),target.getChannelName(), flagData);
                }
            } else {
                // 其余是置顶数据
                saveTop(targetIds.get(target.getName()), flagData);
            }
        }
    }

    private void updateStatus(Integer id, String status) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        params.put("status", status);
        int count = getSqlSession().update("updateStatus", params);
        LOGGER.info("update status count: " + count);
    }

    private void saveTop(Integer id, String status) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        params.put("status", status);
        int count = getSqlSession().insert("", params);
        LOGGER.debug("insert top count: " + count);
    }

    private void saveStatus(String url, String name,String siteName,String channelName,String status) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("url", url);
        params.put("name", name);
        params.put("siteName", siteName);
        params.put("channelName", channelName);
        params.put("status", status);

        int count = getSqlSession().insert("insertStatus", params);
        LOGGER.info("insert status count: " + count);
    }
}
