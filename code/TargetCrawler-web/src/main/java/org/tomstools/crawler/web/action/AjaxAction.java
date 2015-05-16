/**
 * copyright (a) 2010-2015 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.web.action;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.tomstools.crawler.web.model.DataGrid;
import org.tomstools.crawler.web.service.HostService;
import org.tomstools.crawler.web.util.ContextUtil;

/**
 * ajax
 * @author admin
 * @date 2015年5月16日
 * @time 上午9:42:03
 * @version 1.0
 */
@Controller("ajaxAction")
public class AjaxAction {
    private static final String SUCCESS = "success";
    @Autowired
    private HostService hostService;
    private Object result;

    public String getStatus() throws Exception {
        String status = ContextUtil.getRequestParameter("status");
        result = hostService.selectStatus(status);
        return SUCCESS;
    }

    public String getDetail() throws Exception {
        String crawlId = ContextUtil.getRequestParameter("crawlId");
        String startTime = ContextUtil.getRequestParameter("startTime");
        String endTime = ContextUtil.getRequestParameter("endTime");
        String pageNum = ContextUtil.getRequestParameter("page");
        String pageSize = ContextUtil.getRequestParameter("rows");
        String sTotal = ContextUtil.getRequestParameter("total");
        int iPageNum = 1;
        int iPageSize = 100;
        if (null != pageNum) {
            iPageNum = Integer.parseInt(pageNum);
        }
        if (null != pageSize) {
            iPageSize = Integer.parseInt(pageSize);
        }
        RowBounds rowBounds = new RowBounds((iPageNum - 1) * iPageSize, iPageSize);
        int iCrawlId = -1;
        if (null != crawlId) {
            try {
                iCrawlId = Integer.parseInt(crawlId);
            } catch (NumberFormatException e) {
                iCrawlId = -1;
            }
        }
        int total = 0;
        if (-1 == iCrawlId){
            if (null != sTotal) {
                total = Integer.parseInt(sTotal);
            }else{
                total = hostService.countAllDetail(startTime, endTime);
            }
            result = new DataGrid().setTotal(total).setRows(hostService.selectAllDetail(startTime, endTime, rowBounds));
        }else{
            if (null != sTotal) {
                total = Integer.parseInt(sTotal);
            }else{
                total = hostService.countDetail(iCrawlId,startTime, endTime);
            }
            result = new DataGrid().setTotal(total).setRows(hostService.selectDetail(iCrawlId, startTime, endTime, rowBounds));
        }
        return SUCCESS;
    }

    public final Object getResult() {
        return result;
    }

    public final void setResult(Object result) {
        this.result = result;
    }

}
