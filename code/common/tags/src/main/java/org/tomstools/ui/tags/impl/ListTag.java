/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.ui.tags.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.tomstools.common.util.Utils;
import org.tomstools.ui.tags.AbstractTag;
import org.tomstools.ui.tags.handle.ListHandleManager;
import org.tomstools.ui.tags.handle.QueryResult;
import org.tomstools.ui.tags.values.FieldValue;

/**
 * 自定义标签：list标签 结果集
 * 
 * @author lotomer
 * @date 2011-12-14
 * @time 下午03:43:37
 */
public class ListTag extends AbstractTag {

    private static final long serialVersionUID = -3685141937373685926L;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_PAGE_NUM = 1;
    private static final int DEFAULT_MAX_PAGE_COUNT = 10;
    private static final String DEFAULT_TARGET = "_self";
    private static final String QUERY_PARAM_PAGE_NUM = "PAGE_NUM";

    /** 数据来源 */
    private String from;

    /** 结果字段列表 */
    private List<FieldValue> fields;

    private String action; // 翻页时的action
    private String target; // 翻页时页面打开方式
    private int pageSize; // 页面大小
    private int pageNum; // 当前页码。从1开始计数。
    private int maxPageCount; // 导航条中最大页码个数

    public ListTag() {
        super();
        fields = new ArrayList<FieldValue>();
    }

    @Override
    public int doStartTag() throws JspException {
        return EVAL_BODY_INCLUDE;
    }

    @Override
    public int doEndTag() throws JspException {
        if (checkDatas()) {
            String id = getId().trim();
            StringBuilder html = new StringBuilder();
            html.append("<div class=\"list\" id=\"");
            html.append(id);
            html.append("\">");

            // 输出页面大小,页码
            html.append(getHiddenHTML("pageSize", pageSize));
            html.append(getHiddenHTML("pageNum", pageNum));

            // 正文
            html.append("<div class=\"list-content\">");
            QueryResult result = ListHandleManager.getHandle().queryDatas(id, pageSize, pageNum);
            if (null != result && null != result.getResult()) {
                // 以列为单位
                for (FieldValue field : fields) {
                    if (field.isVisible()) {
                        html.append("<div style='float:left'>");
                    } else {
                        html.append("<div style='display:none'>");
                    }
                    // 表头
                    html.append(createHeadCell(field.getTitle()));
                    // 逐条取值
                    int index = 1;
                    for (Map<String, String> record : result.getResult()) {
                        html.append(createCell(field.getId(), index++, record.get(field.getId()),
                                field.getDicType()));
                    }
                    html.append("</div>");
                }
            }
            html.append("</div>");
            // 导航条
            html.append("<div class=\"list-navigate\">");
            html.append("</div>");

            // 输出到页面
            JspWriter out = pageContext.getOut();

            try {
                out.print(html.toString());
            } catch (IOException e) {
                throw new JspException(e);
            }
        }
        return super.doEndTag();
    }

    private String createCell(String id, int index, String value, String dicType) {
        StringBuilder html = new StringBuilder();
        html.append("<div id='");
        html.append(getId());
        html.append("_");
        html.append(id);
        html.append("_");
        html.append(index);
        html.append("'>");
        html.append(ListHandleManager.getHandle().transfer(dicType, value));
        html.append("</div>");
        return html.toString();
    }

    private String createHeadCell(String title) {
        StringBuilder html = new StringBuilder();
        html.append("<div>");
        html.append(title);
        html.append("</div>");
        return html.toString();
    }

    private boolean checkDatas() {
        // 唯一编号不能为空
        if (Utils.isEmpty(getId())) {
            return false;
        }
        // 字段列表不能为空
        if (Utils.isEmpty(fields)) {
            return false;
        }

        if (Utils.isEmpty(target)) {
            target = DEFAULT_TARGET;
        }
        if (pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        // 判断参数中是否包含页码信息
        String qryPageNum = pageContext.getRequest().getParameter(QUERY_PARAM_PAGE_NUM);
        if (!Utils.isEmpty(qryPageNum)) {
            pageNum = Integer.parseInt(qryPageNum);
        }
        if (pageNum < 1) {
            pageNum = DEFAULT_PAGE_NUM;
        }
        if (maxPageCount < 1) {
            maxPageCount = DEFAULT_MAX_PAGE_COUNT;
        }

        return true;
    }

    public final String getFrom() {
        return from;
    }

    public final void setFrom(String from) {
        this.from = from;
    }

    public final String getAction() {
        return action;
    }

    public final void setAction(String action) {
        this.action = action;
    }

    public final String getTarget() {
        return target;
    }

    public final void setTarget(String target) {
        this.target = target;
    }

    public final int getMaxPageCount() {
        return maxPageCount;
    }

    public final void setMaxPageCount(int maxPageCount) {
        this.maxPageCount = maxPageCount;
    }

    public final int getPageSize() {
        return pageSize;
    }

    public final void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public final int getPageNum() {
        return pageNum;
    }

    public final void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public void addField(String id, String title, boolean visible, String dicType) {
        fields.add(new FieldValue(id, title, visible, dicType));
    }

}
