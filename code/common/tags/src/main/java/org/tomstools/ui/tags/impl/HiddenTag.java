/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.ui.tags.impl;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.tomstools.common.util.Utils;
import org.tomstools.ui.tags.AbstractTag;

/**
 * 自定义标签：hidden标签 隐藏域
 * 
 * @author lotomer
 * @date 2011-12-14
 * @time 下午03:29:55
 */
public class HiddenTag extends AbstractTag {
    private static final long serialVersionUID = -9157178478750456596L;
    private static final int SCOPE_PAGE = 1;
    private static final int SCOPE_REQUEST = 2;
    private static final int SCOPE_SESSION = 3;
    private static final int SCOPE_APPLICATION = 4;
    private static final int SCOPE_PARAMETER = 5;
    private static final int SCOPE_INVALID = 0;
    /**
     * 取值范围
     */
    private int scope;
    private String value;

    public final int getScope() {
        return scope;
    }

    public final void setScope(int scope) {
        this.scope = scope;
    }

    public final String getValue() {
        return value;
    }

    public final void setValue(String value) {
        this.value = value;
    }

    /**
     * 根据作用于获取属性值
     * 
     * @param name
     *            属性名
     * @param scope
     *            作用域。
     * @see SCOPE
     * @return
     */
    protected Object getAttribute(String name, int scope) {
        switch (scope) {
        case SCOPE_REQUEST:
            return pageContext.getAttribute(name, PageContext.REQUEST_SCOPE);
        case SCOPE_PAGE:
            return pageContext.getAttribute(name, PageContext.PAGE_SCOPE);
        case SCOPE_SESSION:
            return pageContext.getAttribute(name, PageContext.SESSION_SCOPE);
        case SCOPE_APPLICATION:
            return pageContext.getAttribute(name, PageContext.APPLICATION_SCOPE);
        case SCOPE_PARAMETER:
            return pageContext.getRequest().getParameter(name);
        default:
            return null;
        }
    }

    /**
     * 判断作用域是否有效。如果有效，则可以从作用域中取值
     */
    protected boolean isValidScope() {
        return SCOPE_INVALID != scope;
    }

    @Override
    public int doStartTag() throws JspException {
        String id = getId();
        if (!Utils.isEmpty(id)) {
            id = id.trim();
            StringBuilder html = new StringBuilder();
            html.append("<input type=\"hidden\" id=\"");
            html.append(id);
            html.append("\" name=\"");
            html.append(id);

            // 根据name从请求参数中获取值
            if (isValidScope()) {
                setValue((String) getAttribute(id, getScope()));
            }
            if (null != getValue()) {
                html.append("\" value=\"");
                html.append(getValue());
            }
            html.append("\"></input>");
            JspWriter out = pageContext.getOut();

            try {
                out.print(html.toString());
            } catch (IOException e) {
                throw new JspException(e);
            }
        }

        return SKIP_BODY;
    }
}
