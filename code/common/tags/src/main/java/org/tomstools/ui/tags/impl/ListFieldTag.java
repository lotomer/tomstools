/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.ui.tags.impl;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import org.tomstools.ui.tags.AbstractTag;

/**
 * 自定义标签：field标签
 *   列表字段定义
 * @author lotomer
 * @date 2011-12-14 
 * @time 下午09:55:31
 */
public class ListFieldTag extends AbstractTag {
    private static final long serialVersionUID = -8673361071131926898L;
    private String title;       // 表头显示值
    private boolean visible;    // 是否显示
    private String dicType;     // 是否翻译成字典值
    
    
    public ListFieldTag() {
        visible = true;
    }
    @Override
    public int doStartTag() throws JspException {
        if (checkDatas()) {
            Tag parent = getParent();
            if (parent instanceof ListTag){
                ListTag listTag = (ListTag) parent;
                listTag.addField(getId(), title, visible,dicType);
            }else{
                throw new JspException("The <field> tag's parent must be the <list> tag!");
            }
        }

        return SKIP_BODY;
    }
    private boolean checkDatas() {
        // TODO Auto-generated method stub
        return true;
    }
    public final String getTitle() {
        return title;
    }
    public final void setTitle(String title) {
        this.title = title;
    }
    public final boolean isVisible() {
        return visible;
    }
    public final void setVisible(boolean visible) {
        this.visible = visible;
    }
    public final String getDicType() {
        return dicType;
    }
    public final void setDicType(String dicType) {
        this.dicType = dicType;
    }
    
    
}
