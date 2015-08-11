/**
 * copyright (a) 2010-2015 tomstools.org. All rights reserved.
 */
package org.tomstools.web.model;

/**
 * 菜单
 * @author admin
 * @date 2015年7月6日 
 * @time 下午3:43:55
 * @version 1.0
 */
public class Menu {
    private int menuId;
    private int parentId;
    private String menuName;
    private Integer pageId;
    private int orderNum;
    private String iconClass;
    private String isShow;
    public final int getMenuId() {
        return menuId;
    }
    public final void setMenuId(int menuId) {
        this.menuId = menuId;
    }
    public final int getParentId() {
        return parentId;
    }
    public final void setParentId(int parentId) {
        this.parentId = parentId;
    }
    public final String getMenuName() {
        return menuName;
    }
    public final void setMenuName(String menuName) {
        this.menuName = menuName;
    }
    public final Integer getPageId() {
        return pageId;
    }
    public final void setPageId(Integer pageId) {
        this.pageId = pageId;
    }
    public final int getOrderNum() {
        return orderNum;
    }
    public final void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }
    public final String getIconClass() {
        return iconClass;
    }
    public final void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }
    public final String getIsShow() {
        return isShow;
    }
    public final void setIsShow(String isShow) {
        this.isShow = isShow;
    }
}
