/**
 * copyright (a) 2010-2015 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.web.model;

import org.apache.ibatis.type.Alias;

/**
 * 爬取状态
 * @author admin
 * @date 2015年5月16日 
 * @time 上午2:56:31
 * @version 1.0
 */
@Alias("status")
public class Status {
    private int id;
    private String name;
    private String site_name;
    private String channel_name;
    private String url;
    private String update_time;
    private String status;
    private int cnt;
    public final int getId() {
        return id;
    }
    public final void setId(int id) {
        this.id = id;
    }
    public final String getName() {
        return name;
    }
    public final void setName(String name) {
        this.name = name;
    }
    public final String getSite_name() {
        return site_name;
    }
    public final void setSite_name(String site_name) {
        this.site_name = site_name;
    }
    public final String getChannel_name() {
        return channel_name;
    }
    public final void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }
    public final String getUrl() {
        return url;
    }
    public final void setUrl(String url) {
        this.url = url;
    }
    public final String getUpdate_time() {
        return update_time;
    }
    public final void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }
    
    public final String getStatus() {
        return status;
    }
    public final void setStatus(String status) {
        this.status = status;
    }
    public final int getCnt() {
        return cnt;
    }
    public final void setCnt(int cnt) {
        this.cnt = cnt;
    }
}
