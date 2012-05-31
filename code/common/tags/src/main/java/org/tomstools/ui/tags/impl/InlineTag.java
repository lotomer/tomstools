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
    protected static final String MODE_FILE = "file";
    protected static final String MODE_INLINE = "inline";
    
    private String mode = MODE_FILE;
    public final String getMode() {
        return mode;
    }
    public final void setMode(String mode) {
        this.mode = mode;
    }
}
