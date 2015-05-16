/**
 * copyright (a) 2010-2015 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.web.model;

import org.apache.ibatis.type.Alias;

/**
 * 爬取详情
 * @author admin
 * @date 2015年5月16日 
 * @time 下午1:26:29
 * @version 1.0
 */
@Alias("detail")
public class Detail {
    // CRAWL_ID,URL_PREFIX,URL,TITLE,IN_TIME
    private int crawl_id;
    private String url_prefix;
    private String url;
    private String title;
    private String in_time;
    private String site_name;
    private String channel_name;
    public final int getCrawl_id() {
        return crawl_id;
    }
    public final void setCrawl_id(int crawl_id) {
        this.crawl_id = crawl_id;
    }
    public final String getUrl_prefix() {
        return url_prefix;
    }
    public final void setUrl_prefix(String url_prefix) {
        this.url_prefix = url_prefix;
    }
    public final String getUrl() {
        return url;
    }
    public final void setUrl(String url) {
        this.url = url;
    }
    public final String getTitle() {
        return title;
    }
    public final void setTitle(String title) {
        this.title = title;
    }
    public final String getIn_time() {
        return in_time;
    }
    public final void setIn_time(String in_time) {
        this.in_time = in_time;
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
}
