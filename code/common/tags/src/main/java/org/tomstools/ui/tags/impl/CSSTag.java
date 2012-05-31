/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.ui.tags.impl;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.tomstools.common.merge.manage.WebFileManagerFactory;
import org.tomstools.common.util.Utils;

/**
 * 自定义标签：css标签
 * @author lotomer
 * @date 2011-12-14 
 * @time 下午03:27:17
 */
public class CSSTag extends InlineTag {
    private static final long serialVersionUID = -2628245647795440967L;
    @Override
    public int doStartTag() throws JspException {        
        if (null != getId()) {
            String id = getId().trim();
            if (!"".equals(id)) {
                String code = WebFileManagerFactory.getInstance().getWebFileManager().getHTMLCode(id, "css", MODE_INLINE.equalsIgnoreCase(getMode()),pageContext.getServletContext().getRealPath("/"));
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
