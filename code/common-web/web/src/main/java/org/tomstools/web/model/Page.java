/**
 * copyright (a) 2010-2015 tomstools.org. All rights reserved.
 */
package org.tomstools.web.model;

import java.util.List;

/**
 * 页面信息
 * 
 * @author admin
 * @date 2015年7月6日 
 * @time 下午3:56:14
 * @version 1.0
 */
public class Page {
    private int pageId;
    private String pageName;
    private List<Integer> subPageId;
    private String contentURL;
    private String params;
    private String useIframe;
    private int orderNum;
    private String width;
    private String height;
    private String iconCls;
    private int autoFreshTime;
    private int parentId;
    
    public final int getParentId() {
        return parentId;
    }
    public final void setParentId(int parentId) {
        this.parentId = parentId;
    }
    public final int getPageId() {
        return pageId;
    }
    public final void setPageId(int pageId) {
        this.pageId = pageId;
    }
    public final String getPageName() {
        return pageName;
    }
    public final void setPageName(String pageName) {
        this.pageName = pageName;
    }
    
    public final List<Integer> getSubPageId() {
        return subPageId;
    }
    public final void setSubPageId(List<Integer> subPageId) {
        this.subPageId = subPageId;
    }
    public final String getContentURL() {
        return contentURL;
    }
    public final void setContentURL(String contentURL) {
        this.contentURL = contentURL;
    }
    public final String getParams() {
        return params;
    }
    public final void setParams(String params) {
        this.params = params;
    }
    public final String getUseIframe() {
        return useIframe;
    }
    public final void setUseIframe(String useIframe) {
        this.useIframe = useIframe;
    }
    public final int getOrderNum() {
        return orderNum;
    }
    public final void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }
    public final String getWidth() {
        return width;
    }
    public final void setWidth(String width) {
        this.width = width;
    }
    public final String getHeight() {
        return height;
    }
    public final void setHeight(String height) {
        this.height = height;
    }
    public final String getIconCls() {
        return iconCls;
    }
    public final void setIconCls(String iconCls) {
        this.iconCls = iconCls;
    }
    public final int getAutoFreshTime() {
        return autoFreshTime;
    }
    public final void setAutoFreshTime(int autoFreshTime) {
        this.autoFreshTime = autoFreshTime;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Page:{pageId:").append(pageId).append(", pageName:").append(pageName)
                .append(", subPageId:").append(subPageId).append(", contentURL:").append(contentURL)
                .append(", params:").append(params).append(", useIframe:").append(useIframe)
                .append(", orderNum:").append(orderNum).append(", width:").append(width)
                .append(", height:").append(height).append(", iconCls:").append(iconCls)
                .append(", autoFreshTime:").append(autoFreshTime).append("}");
        return builder.toString();
    }
    
}
