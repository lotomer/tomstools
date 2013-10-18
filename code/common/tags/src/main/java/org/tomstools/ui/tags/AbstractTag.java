/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.ui.tags;

import javax.servlet.jsp.tagext.TagSupport;

/**
 * 自定义标签基类
 * @author lotomer
 * @date 2011-12-14 
 * @time 下午03:07:04
 */
public abstract class AbstractTag extends TagSupport {
    private static final long serialVersionUID = -2661718471626614028L;
    
    /**
     * 获取隐藏域的html内容
     * @param id    隐藏域唯一编号
     * @param value 隐藏域值
     * @return  html内容
     */
    protected final String getHiddenHTML(String id, Object value){
        StringBuilder html = new StringBuilder();
        html.append("<input type=\"hidden\" id=\"");
        html.append(id);
        html.append("\" name=\"");
        html.append(id);
        html.append("\" value=\"");
        html.append(value);
        html.append("\"/>");
        
        return html.toString();
    }
}
