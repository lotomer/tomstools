/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tomstools.common.Logger;
import org.tomstools.common.Utils;
import org.tomstools.crawler.busi.TargetBusi;
import org.tomstools.crawler.common.Element;
import org.tomstools.crawler.config.CrawlingRule;
import org.tomstools.crawler.config.Target;
import org.tomstools.crawler.dao.ResultDAO;
import org.tomstools.crawler.extractor.ContentPageExtractor;
import org.tomstools.crawler.http.PageFetcher;
import org.tomstools.crawler.http.RequestInfo;
import org.tomstools.crawler.http.UrlManager;
import org.tomstools.crawler.parser.Parser;
import org.tomstools.crawler.util.HTMLUtil;

/**
 * 定向爬虫
 * 
 * @author admin
 * @date 2014年3月12日
 * @time 下午7:18:51
 * @version 1.0
 */
public class TargetCrawler implements Runnable {
    /**
     * 
     * @author admin
     * @date 2014年8月14日
     * @time 下午7:12:38
     * @version 1.0
     */
    public static class Page {
        private String url;
        private Map<String, String> constantValues;

        public Page(String url, Map<String, String> constantValues) {
            this.url = url;
            this.constantValues = constantValues;
        }

        public final String getUrl() {
            return url;
        }

        public final Map<String, String> getConstantValues() {
            return constantValues;
        }

        
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Page [url=").append(url).append(", constantValues=")
                    .append(constantValues).append("]");
            return builder.toString();
        }
    }

    private final static Logger LOGGER = Logger.getLogger(TargetCrawler.class);
    private List<TargetBusi> targetBusis; // 定向爬取目标列表
    private int totalPage;
    private UrlManager urlManager;
    private int totalSubPage;
    private int totalRecords;

    /**
     * 构造函数
     * 
     * @param targetBusis 定向爬取目标业务对象列表
     * @since 1.0
     */
    public TargetCrawler(List<TargetBusi> targetBusis) {
        super();
        this.urlManager = new UrlManager();
        this.targetBusis = targetBusis;
    }

    /**
     * 构造函数
     * 
     * @param targets 定向爬取目标列表
     * @param resultDAO 结果数据操作对象
     * @since 1.0
     */
    public TargetCrawler(List<Target> targets, ResultDAO resultDAO) {
        super();
        this.urlManager = new UrlManager();
        this.targetBusis = new ArrayList<TargetBusi>(targets.size());
        for (Target target : targets) {
            targetBusis.add(new TargetBusi(target, resultDAO));
        }
    }

    public void run() {
        long startTime = System.currentTimeMillis();
        if (null != targetBusis && 0 != targetBusis.size()) {
            if(!HTMLUtil.check()) {
                LOGGER.info("check failed!");
                return;
            }
            // XXX 暂时采用串行方式执行，以后根据需要改为并行
            for (final TargetBusi targetBusi : targetBusis) {
                // LOGGER.info(target);
                try {
                    if (targetBusi.prepare()) {
                        PageFetcher fetcher = new PageFetcher(targetBusi.getTarget()
                                .getDefaultCharsetName());
                        fetcher.setConnectionTimeOut(targetBusi.getTarget().getCrawlingRule()
                                .getConnectionTimeOut());
                        fetcher.setSocketTimeOut(targetBusi.getTarget().getCrawlingRule()
                                .getSocketTimeOut());
                        processMainPage(targetBusi, fetcher);
                        targetBusi.finish();
                        // 清理已经处理过的url
                        urlManager.clean();
                    }
                } catch (Throwable e) {
                    LOGGER.error("run target failed! Target name: "
                            + targetBusi.getTarget().getName() + ", errors: " + e.getMessage(), e);
                }
            }
        }

        LOGGER.warn("Process finished. Total page: " + this.totalPage + ", total sub page: "
                + this.totalSubPage + ", total records: " + totalRecords + ", total cost "
                + (System.currentTimeMillis() - startTime) / 1000 + "s.");
    }

    /**
     * 处理主页面
     */
    private void processMainPage(final TargetBusi targetBusi, PageFetcher fetcher) {
        // 如果分页导航是根据模板自动生成的，而不是从页面中提取出来的，则先将所有分页页面生成
        if (targetBusi.getTarget().getNavigationExtractor().useExpression()) {
            // 爬取主页面
            processPageWithoutNavigation(targetBusi, fetcher);
            if (!targetBusi.isFinished()) {
                // 生成其他主页面url列表
                List<RequestInfo> requestInfos = targetBusi.getTarget().getNavigationExtractor()
                        .getNextPageRequestInfos(null);
                for (RequestInfo requestInfo : requestInfos) {
                    if (!Utils.isEmpty(requestInfo.getUrl())) {
                        // 如果设置了url，则使用新的url
                        // 如果新url以&开头，则直接追加到主页面url中
                        if (requestInfo.getUrl().startsWith("&")){
                            if (targetBusi.getTarget().getBaseUrl() == null) targetBusi.getTarget().setBaseUrl(targetBusi.getTarget().getUrl());
                            targetBusi.getTarget().setUrl(targetBusi.getTarget().getBaseUrl()+requestInfo.getUrl());
                        }else{
                            targetBusi.getTarget().setUrl(requestInfo.getUrl());
                        }
                    }
                    // 复制有效配置数据给页面爬取器
                    if (null != fetcher.getRequestInfo()) {
                        fetcher.getRequestInfo().copyValidDatas(requestInfo);
                    } else {
                        fetcher.setRequestInfo(requestInfo);
                    }
                    processPageWithoutNavigation(targetBusi, fetcher);
                    if (targetBusi.isFinished()) {
                        break;
                    }
                }
            }
        } else {
            // 爬取主页面并返回下一页url集合
            List<RequestInfo> pageRequestInfos = processPageWithNavigation(targetBusi, fetcher);
            if (!targetBusi.isFinished()) {
                for (int i = 0; i < pageRequestInfos.size(); i++) {
                    targetBusi.getTarget().setUrl(pageRequestInfos.get(i).getUrl());
                    // 复制有效配置数据给页面爬取器
                    RequestInfo requestInfo = fetcher.getRequestInfo();
                    if (null != requestInfo) {
                        requestInfo.copyValidDatas(pageRequestInfos.get(i));
                    } else {
                        fetcher.setRequestInfo(pageRequestInfos.get(i));
                    }
                    pageRequestInfos.addAll(processPageWithNavigation(targetBusi, fetcher));
                    if (targetBusi.isFinished()) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * 处理页面。不需要处理分页导航信息
     * 
     * @param targetBusi 目标页面
     * @param fetcher 爬取器
     * @since 1.0
     */
    private void processPageWithoutNavigation(final TargetBusi targetBusi, PageFetcher fetcher) {
        LOGGER.info("process page with navigation: " + targetBusi.getTarget().getUrl());
        if (Utils.isEmpty(targetBusi.getTarget().getUrl())) {
            return;
        }

        URL url = null;
        try {
            url = new URL(targetBusi.getTarget().getUrl());
        } catch (MalformedURLException e) {
            LOGGER.error(e.getMessage(), e);
            return;
        }

        String webRoot = HTMLUtil.getWebRoot(url);
        String parentPath = url.getPath();
        doProcess(targetBusi, fetcher, webRoot, parentPath);
    }

    /**
     * 处理页面以及分页导航信息
     * 
     * @param targetBusi 目标页面
     * @param fetcher 爬取器
     * @return 下一页url列表，不为null
     * @since 1.0
     */
    private List<RequestInfo> processPageWithNavigation(final TargetBusi targetBusi,
            PageFetcher fetcher) {
        LOGGER.info("process page with navigation: " + targetBusi.getTarget().getUrl());
        List<RequestInfo> pageRequestInfos = new ArrayList<RequestInfo>(0);
        if (Utils.isEmpty(targetBusi.getTarget().getUrl())) {
            return pageRequestInfos;
        }

        URL url = null;
        try {
            url = new URL(targetBusi.getTarget().getUrl());
        } catch (MalformedURLException e) {
            LOGGER.error(e.getMessage(), e);
            return pageRequestInfos;
        }

        String webRoot = HTMLUtil.getWebRoot(url);
        String parentPath = url.getPath();
        Element document = doProcess(targetBusi, fetcher, webRoot, parentPath);
        if (null == document) {
            return pageRequestInfos;
        }
        // 解析出下一页url
        List<RequestInfo> requestInfos = targetBusi.getTarget().getNavigationExtractor()
                .getNextPageRequestInfos(document);
        for (RequestInfo requestInfo : requestInfos) {
            RequestInfo aRequest = new RequestInfo();
            aRequest.setUrl(targetBusi.getTarget().getUrl());
            aRequest.copyValidDatas(requestInfo);
            String nextPageUrl = HTMLUtil.getRealUrl(aRequest.getUrl(), webRoot, parentPath);
            LOGGER.info("The next page: " + nextPageUrl);
            // LOGGER.warn("next page request info: "+aRequest);
            if (null != nextPageUrl && !isProcessed(nextPageUrl)) {
                setCompleted(nextPageUrl);
                aRequest.setUrl(nextPageUrl);
                pageRequestInfos.add(aRequest);
            }
        }

        return pageRequestInfos;
    }

    private Element doProcess(final TargetBusi targetBusi, PageFetcher fetcher,
            final String webRoot, final String parentPath) {
        // LOGGER.info("do process: " + target.getUrl());
        if (isProcessed(targetBusi.getTarget().getUrl())) {

        }
        // 判断这一批是否已经处理完成，如果处理完成了就要休息一下
        if (!targetBusi.checkBatchInfo()) {
            LOGGER.info(targetBusi.getTarget().getName() + " check failed!");
            return null;
        }

        PageFetcher pageFetcher = targetBusi.getTarget().getPageFetcher();
        if (null == pageFetcher) {
            pageFetcher = fetcher;
        } else {
            // 需要及时更新请求信息
            if (null != pageFetcher.getRequestInfo()) {
                pageFetcher.getRequestInfo().copyValidDatas(fetcher.getRequestInfo());
            } else {
                pageFetcher.setRequestInfo(fetcher.getRequestInfo());
            }
        }
        CrawlingRule rule = targetBusi.getTarget().getCrawlingRule();
        if (null != rule) {
            pageFetcher.setConnectionTimeOut(rule.getConnectionTimeOut());
            pageFetcher.setSocketTimeOut(rule.getSocketTimeOut());
        }
        String content = pageFetcher.fetchPageContent(targetBusi.getTarget().getUrl());
        if (null == content) {
            LOGGER.warn("There is no content in url: " + targetBusi.getTarget().getUrl());
            return null;
        }
        // 将该页面置为已处理，避免重复处理
        setCompleted(targetBusi.getTarget().getUrl());
        Parser parser = targetBusi.getTarget().getParser();
        if (null == parser) {
            return null;
        }
        // 解析文档
        Element document = parser.parse(content, null);
        ++totalPage;
        targetBusi.incFetchPageCount();
        ContentPageExtractor subpageExtractor = targetBusi.getTarget().getContentPageExtractor();
        if (null != subpageExtractor) {
            // 内容页面抽取器对应的固定属性
            Map<String, String> constantValues4contentPageExtractor = new HashMap<String, String>();
            List<String> urls = subpageExtractor.getContentPageUrls(document);
            // 添加固定属性
            addConstantValues(constantValues4contentPageExtractor,
                    subpageExtractor.getConstantValues(document));

            // 有子页面抽取器，则使用子页面抽取器抽取子页面url，并逐个处理
            List<Page> contentPages = new ArrayList<Page>();
            for (String url : urls) {
                contentPages.add(new Page(url, constantValues4contentPageExtractor));
            }
            // 判断是否还包含子页面抽取器，如果包含，则需要继续处理
            while (null != (subpageExtractor = subpageExtractor.getContentPageExtractor())) {
                contentPages = getContentPages(subpageExtractor, fetcher, parser, contentPages,
                        webRoot, parentPath);
            }
            // List<String> nextPages = new ArrayList<String>();
            for (Page page : contentPages) {
                // nextPages.addAll(processSubPage(targetBusi,fetcher,
                // HTMLUtil.getRealUrl(aUrl, webRoot, parentPath)));
                processSubPage(targetBusi, fetcher, HTMLUtil.getRealUrl(page.url, webRoot, parentPath),page.constantValues);
                if (targetBusi.isFinished()) {
                    LOGGER.warn("is finished!");
                    return null;
                }
            }
            // 处理下一页
            // for (String nextPage : nextPages) {
            // targetBusi.getTarget().setUrl(HTMLUtil.getRealUrl(nextPage,
            // webRoot, parentPath));
            // processPageWithNavigation(targetBusi, fetcher);
            // }
        } else {
            // 没有子页面时，则直接使用内容抽取器处理页面，最近处理的标记已数据内容为准
            List<Map<String, String>> records = targetBusi.getTarget().getContentExtractor()
                    .extractContent(document);
            int count = 0;
            // 保存页面内容
            for (Map<String, String> record : records) {
                if (record.isEmpty()) {
                    continue;
                }
                // 判断该内容是否已经保存过，如果已经保存过，则表示已经处理完成了
                if (targetBusi.willFinish(record.toString())) {
                    break;
                }
                targetBusi.incRecordCount();
                targetBusi.saveRecord(targetBusi.getTarget().getUrl(), record);
                ++count;
            }
            LOGGER.info("records count: " + count);
            totalRecords += count;
            if (targetBusi.isFinished()) {
                LOGGER.warn("is finished!");
                return null;
            }
        }

        return document;
    }

    private void addConstantValues(Map<String, String> constantValues4contentPageExtractor,
            Map<String, String> constantValues) {
        if (null != constantValues && null != constantValues4contentPageExtractor) {
            constantValues4contentPageExtractor.putAll(constantValues);
        }
    }

    private List<Page> getContentPages(ContentPageExtractor subpageExtractor, PageFetcher fetcher,
            Parser parser, List<Page> pages, String webRoot, String parentPath
            ) {
        PageFetcher pageFetcher = subpageExtractor.getPageFetcher();
        if (null == pageFetcher)
            pageFetcher = fetcher;
        List<Page> subpages = new ArrayList<Page>();
        for (Page page : pages) {
            String content = pageFetcher.fetchPageContent(HTMLUtil.getRealUrl(page.url, webRoot,
                    parentPath));
            if (null != content) {
                Element doc = parser.parse(content, null);
                // 添加固定属性
                Map<String, String> tmpConstantValues = subpageExtractor.getConstantValues(doc);
                if (null != tmpConstantValues) {
                    addConstantValues(tmpConstantValues, page.constantValues);
                } else {
                    tmpConstantValues = page.constantValues;
                }
                List<String> tmpUrls = subpageExtractor.getContentPageUrls(doc);
                for (String tmpUrl : tmpUrls) {
                    subpages.add(new Page(tmpUrl, tmpConstantValues));
                }
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("urls: " + pages);
            LOGGER.debug("subpages: " + subpages);
        }
        return subpages;
    }

    /**
     * 处理单个子页面
     * 
     * @param targetBusi
     * @param fetcher
     * @param subUrl
     * @param constantValues4contentPageExtractor
     * @return 返回下一页的url集合，不为null
     * @since 1.0
     */
    private List<String> processSubPage(TargetBusi targetBusi, PageFetcher fetcher, String subUrl,
            Map<String, String> constantValues4contentPageExtractor) {
        List<String> nextPages = Collections.emptyList();
        // 判断这一批是否已经处理完成，如果处理完成了就要休息一下
        if (!targetBusi.checkBatchInfo()) {
            LOGGER.warn(targetBusi.getTarget().getName() + " check failed!");
            return nextPages;
        }
        LOGGER.info("process sub page: " + subUrl);
        // 1、判断页面是否已经处理过
        if (Utils.isEmpty(subUrl)) {
            return nextPages;
        }
        if (isProcessed(subUrl)) {
            LOGGER.warn("The url is processed this time: " + subUrl);
            return nextPages;
        }
        // 判断是否是最后一条
        if (targetBusi.willFinish(subUrl)) {
            return nextPages;
        }
        ++totalSubPage;
        // 2、抓取页面
        PageFetcher pageFetcher = targetBusi.getTarget().getPageFetcher();
        if (null == pageFetcher)
            pageFetcher = fetcher;
        String content = pageFetcher.fetchPageContent(subUrl);
        if (null == content) {
            // 内容抓取失败，则返回true，表示已经处理完毕了
            return nextPages;
        }
        targetBusi.incFetchPageCount();
        int count = 0;
        // 3、解析页面内容
        // 3.1、获取解析器
        Parser parser = targetBusi.getTarget().getParser();
        // 3.2、开始解析
        if (null != parser) {
            // 3.2.1、解析页面内容
            Element doc = parser.parse(content, null);
            List<Map<String, String>> records = targetBusi.getTarget().getContentExtractor()
                    .extractContent(doc, constantValues4contentPageExtractor);

            // 保存页面内容
            for (Map<String, String> record : records) {
                targetBusi.incRecordCount();
                targetBusi.saveRecord(subUrl, record);
                ++count;
            }

            // XXX 貌似没必要这样处理：获取下一页url集合
            // nextPages =
            // targetBusi.getTarget().getNavigationExtractor().getPageUrls(doc);
        }
        if (0 < count) {
            LOGGER.info("get records count: " + count);
        } else {
            LOGGER.warn(new StringBuilder().append("get record failed from ").append(subUrl)
                    .toString());
        }
        totalRecords += count;
        setCompleted(subUrl);

        return nextPages;
    }

    /**
     * 将该url置为已爬取过
     * 
     * @since 1.0
     */
    private void setCompleted(String url) {
        urlManager.setFinished(url);
    }

    /**
     * 判断该url是否已经被爬取过
     * 
     * @param url 待判断的url
     * @return true 已经爬取过；false 未爬取过
     * @since 1.0
     */
    private boolean isProcessed(String url) {
        if (Utils.isEmpty(url)) {
            return true;
        }
        return urlManager.isFinished(url);
    }

    /**
     * @param urlManager 设置 url管理器
     * @since 1.0
     */
    // public final void setUrlManager(UrlManager urlManager) {
    // this.urlManager = urlManager;
    // }

}
