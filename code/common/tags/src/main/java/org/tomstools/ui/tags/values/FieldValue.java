/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.ui.tags.values;

/**
 * 列表字段值
 * @author vaval
 * @date 2011-12-14 
 * @time 下午10:05:19
 */
public class FieldValue {
    private String id;          // 字段编码
    private String title;       // 表头显示值
    private boolean visible;    // 是否显示
    private String dicType;     // 是否翻译成字典值
    
    public FieldValue(String id, String title, boolean visible, String dicType) {
        super();
        this.id = id;
        this.title = title;
        this.visible = visible;
        this.dicType = dicType;
    }

    public final String getId() {
        return id;
    }

    public final void setId(String id) {
        this.id = id;
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
