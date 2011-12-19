/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.ui.tags.impl;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;

import org.tomstools.common.log.Logger;
import org.tomstools.common.util.Utils;
import org.tomstools.ui.tags.AbstractTag;

/**
 * 自定义标签：js标签
 * @author lotomer
 * @date 2011-12-14 
 * @time 下午03:08:31
 */
public class JSTag extends AbstractTag implements BodyTag{
    private static final long serialVersionUID = 1165741742092023531L;
    private static final Logger logger = Logger.getLogger(JSTag.class);
    private BodyContent bodyContent;

    @Override
    public int doEndTag() throws JspException {
        JspWriter out = pageContext.getOut();
        try {
            out.println("</script>"); // 页面中显示的内容
        }
        catch (IOException e) {
            throw new JspException(e);
        }
        return super.doEndTag();
    }

    @Override
    public int doStartTag() throws JspException {
        if (null != getId()) {
            String id = getId().trim();
            if (!"".equals(id)) {
                JspWriter out = pageContext.getOut();
                // TODO 未完成
                String code = null;//WebFileMgr.getInstance().getJsCode(getId());
                StringBuilder js = new StringBuilder();
                try {
                    if (!Utils.isEmpty(code)) {
                        js.append(code);
                    }
   
                    out.print(js.toString());
                }
                catch (IOException e) {
                    throw new JspException(e);
                }
            }
        }
        return EVAL_BODY_BUFFERED;
    }

    @Override
    public int doAfterBody() throws JspException {
        if (null != bodyContent) {
            try {
                bodyContent.writeOut(bodyContent.getEnclosingWriter());
            }
            catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return SKIP_BODY;
    }

    public void doInitBody() throws JspException {
        // empty
    }

    public void setBodyContent(BodyContent bodyContent) {
        this.bodyContent = bodyContent;
    }
}
