/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.ui.tags.impl;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.tomstools.common.merge.manage.WebFileManagerFactory;
import org.tomstools.common.util.Utils;
import org.tomstools.ui.tags.AbstractTag;

/**
 * 自定义标签：js标签
 * @author lotomer
 * @date 2011-12-14 
 * @time 下午03:08:31
 */
public class JSTag extends AbstractTag{
    private static final long serialVersionUID = 1165741742092023531L;

    @Override
    public int doStartTag() throws JspException {
        if (null != getId()) {
            String id = getId().trim();
            if (!"".equals(id)) {
                String code = WebFileManagerFactory.getInstance().getWebFileManager().getHTMLCode(id, "js");
                if (Utils.isEmpty(code)) {
                    return SKIP_BODY;
                }
                
                JspWriter out = pageContext.getOut();
                try {
                    out.print(code);
                }
                catch (IOException e) {
                    throw new JspException(e);
                }
            }
        }
        return SKIP_BODY;
    }
}
