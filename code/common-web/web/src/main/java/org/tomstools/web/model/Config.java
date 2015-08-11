/**
 * 
 */
package org.tomstools.web.model;

/**
 * 配置
 * @author lotomer
 * @date 2015年8月4日
 * @version 1.0
 */
public class Config {
    private int userId;
    private String name;
    private String value;
    public final int getUserId() {
        return userId;
    }
    public final void setUserId(int userId) {
        this.userId = userId;
    }
    public final String getName() {
        return name;
    }
    public final void setName(String name) {
        this.name = name;
    }
    public final String getValue() {
        return value;
    }
    public final void setValue(String value) {
        this.value = value;
    }
}
