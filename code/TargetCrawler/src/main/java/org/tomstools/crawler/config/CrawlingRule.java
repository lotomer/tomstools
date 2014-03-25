/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.config;

/**
 * 爬取规则
 * 
 * @author admin
 * @date 2014年3月12日
 * @time 下午7:22:17
 * @version 1.0
 */
public class CrawlingRule {
    /** 开始批次大小 */
    private int batchSizeInit;
    /** 最终批次大小 */
    private int batchSizeFinal;
    /** 批次增加步长 */
    private int batchSizeStep;
    /** 批次开始间隔时间（单位：毫秒） */
    private int batchIntervalInit;
    /** 批次最终间隔时间（单位：毫秒） */
    private int batchIntervalFinal;
    /** 批次间隔时间步长（单位：毫秒） */
    private int batchIntervalStep;
    /** 最大数目 */
    private int maxSize;
    /** 连接超时时间。单位毫秒 */
    private int connectionTimeOut = 8000;
    /** socket超时时间。单位毫秒 */
    private int socketTimeOut = 10000;

    /**
     * @since 1.0
     */
    public CrawlingRule() {
        super();
        this.batchSizeInit = 10;
        this.batchSizeFinal = 10;
        this.batchSizeStep = 0;
        this.batchIntervalInit = 0;
        this.batchIntervalFinal = 0;
        this.batchIntervalStep = 0;
        this.connectionTimeOut = 8000;
        this.socketTimeOut = 10000;
    }

    /**
     * @return 返回 开始批次大小
     * @since 1.0
     */
    public final int getBatchSizeInit() {
        return batchSizeInit;
    }

    /**
     * @param batchSizeInit 设置 开始批次大小
     * @since 1.0
     */
    public final void setBatchSizeInit(int batchSizeInit) {
        this.batchSizeInit = batchSizeInit;
    }

    /**
     * @return 返回 最终批次大小
     * @since 1.0
     */
    public final int getBatchSizeFinal() {
        return batchSizeFinal;
    }

    /**
     * @param batchSizeFinal 设置 最终批次大小
     * @since 1.0
     */
    public final void setBatchSizeFinal(int batchSizeFinal) {
        this.batchSizeFinal = batchSizeFinal;
    }

    /**
     * @return 返回 批次增加步长
     * @since 1.0
     */
    public final int getBatchSizeStep() {
        return batchSizeStep;
    }

    /**
     * @param batchSizeStep 设置 批次增加步长
     * @since 1.0
     */
    public final void setBatchSizeStep(int batchSizeStep) {
        this.batchSizeStep = Math.abs(batchSizeStep);
    }

    /**
     * @return 返回 批次开始间隔时间（单位：毫秒）
     * @since 1.0
     */
    public final int getBatchIntervalInit() {
        return batchIntervalInit;
    }

    /**
     * @param batchIntervalInit 设置 批次开始间隔时间（单位：毫秒）
     * @since 1.0
     */
    public final void setBatchIntervalInit(int batchIntervalInit) {
        this.batchIntervalInit = batchIntervalInit;
    }

    /**
     * @return 返回 批次最终间隔时间（单位：毫秒）
     * @since 1.0
     */
    public final int getBatchIntervalFinal() {
        return batchIntervalFinal;
    }

    /**
     * @param batchIntervalFinal 设置 批次最终间隔时间（单位：毫秒）
     * @since 1.0
     */
    public final void setBatchIntervalFinal(int batchIntervalFinal) {
        this.batchIntervalFinal = batchIntervalFinal;
    }

    /**
     * @return 返回 批次间隔时间步长（单位：毫秒）
     * @since 1.0
     */
    public final int getBatchIntervalStep() {
        return batchIntervalStep;
    }

    /**
     * @param batchIntervalStep 设置 批次间隔时间步长（单位：毫秒）
     * @since 1.0
     */
    public final void setBatchIntervalStep(int batchIntervalStep) {
        this.batchIntervalStep = Math.abs(batchIntervalStep);
    }

    /**
     * @return 返回 maxSize
     * @since 1.0
     */
    public final int getMaxSize() {
        return maxSize;
    }

    /**
     * @param maxSize 设置 maxSize
     * @since 1.0
     */
    public final void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * @return 返回 连接超时时间。单位毫秒
     * @since 1.0
     */
    public final int getConnectionTimeOut() {
        return connectionTimeOut;
    }

    /**
     * @param connectionTimeOut 设置 连接超时时间。单位毫秒
     * @since 1.0
     */
    public final void setConnectionTimeOut(int connectionTimeOut) {
        this.connectionTimeOut = connectionTimeOut;
    }

    /**
     * @return 返回 socket超时时间。单位毫秒
     * @since 1.0
     */
    public final int getSocketTimeOut() {
        return socketTimeOut;
    }

    /**
     * @param socketTimeOut 设置 socket超时时间。单位毫秒
     * @since 1.0
     */
    public final void setSocketTimeOut(int socketTimeOut) {
        this.socketTimeOut = socketTimeOut;
    }

    /*
     * @since 1.0
     */
    @Override
    public String toString() {
        return new StringBuilder().append("{batchSizeInit=").append(batchSizeInit)
                .append(", batchSizeFinal=").append(batchSizeFinal).append(", batchSizeStep=")
                .append(batchSizeStep).append(", batchIntervalInit=").append(batchIntervalInit)
                .append(", batchIntervalFinal=").append(batchIntervalFinal)
                .append(", batchIntervalStep=").append(batchIntervalStep)
                .append(", connectionTimeOut=").append(connectionTimeOut)
                .append(", socketTimeOut=").append(socketTimeOut).append(", maxSize=")
                .append(maxSize).append("}").toString();
    }
}
