/**
 * copyright (a) 2010-2012 tomstools.org. All rights reserved.
 */
package org.tomstools.ui.tags.impl;

import org.tomstools.ui.tags.AbstractTag;

/**
 * @author lotomer
 * @date 2012-5-31 
 * @time 上午10:30:18
 */
public class InlineTag extends AbstractTag {
    private static final long serialVersionUID = 4314326387409241760L;
    private String inline;
    public final String getInline() {
        return inline;
    }
    public final void setInline(String inline) {
        this.inline = inline;
    }
    
}
