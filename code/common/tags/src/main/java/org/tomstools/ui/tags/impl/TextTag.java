/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.ui.tags.impl;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.tomstools.common.util.Utils;

/**
 * 自定义标签：text标签 文本域
 * 
 * @author lotomer
 * @date 2011-12-14
 * @time 下午03:31:53
 */
public class TextTag extends HiddenTag {
    private static final long serialVersionUID = -6999852013642682668L;
    /** 样式类名 */
    private String className;
    /** 标注 */
    private String label;

    @Override
    public int doStartTag() throws JspException {
        String id = getId();
        if (!Utils.isEmpty(id)) {
            id = id.trim();
            StringBuilder html = new StringBuilder();
            if (!Utils.isEmpty(label)){
                html.append("<label>");
                html.append(label);
                html.append("</label>");
            }
            html.append("<input type=\"text\" autocomplete=\"off\" id=\"");
            html.append(id);
            html.append("\" name=\"");
            html.append(id);
            // 样式类
            if (null != className) {
                html.append("\" class=\"");
                html.append(className);
            }
            // 值
            if (isValidScope()) {
                Object value = getAttribute(id, getScope());
                if (null != value){
                    setValue(String.valueOf(value));
                }else{
                    // 如果没有获取到值，则不进行主动设置
                    //setValue(null);
                }
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
        
        release();
        return SKIP_BODY;
    }

    public final String getClassName() {
        return className;
    }

    public final void setClassName(String className) {
        this.className = className;
    }

    public final String getLabel() {
        return label;
    }

    public final void setLabel(String label) {
        this.label = label;
    }
    
    public void release(){
        super.release();
        this.className = null;
        this.label = null;
    }
}
