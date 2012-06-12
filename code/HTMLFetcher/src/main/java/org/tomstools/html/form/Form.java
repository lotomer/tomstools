/**
 * copyright (a) 2010-2012 tomstools.org. All rights reserved.
 */
package org.tomstools.html.form;

import java.util.HashMap;
import java.util.Map;

import org.tomstools.common.util.Utils;

/**
 * @author lotomer
 * @date 2012-6-12 
 * @time 下午02:57:44
 */
public class Form {
    private String userName;
    private String password;
    private Map<String, String> attrs;
    public Form(String userName, String password) {
        super();
        this.userName = userName;
        this.password = password;
        attrs = new HashMap<String, String>();
    }
    public final String getUserName() {
        return userName;
    }
    public final void setUserName(String userName) {
        this.userName = userName;
    }
    public final String getPassword() {
        return password;
    }
    public final void setPassword(String password) {
        this.password = password;
    }
    /**
     * 设置表单属性
     * @param name  名称
     * @param value 值
     */
    public final void setAttribute(String name, String value){
        attrs.put(name, value);
    }
    /**
     * 判断是否需要登录
     * @return
     */
    public boolean needLogin(){
        return !Utils.isEmpty(userName);
    }
    /**
     * 获取表单属性列表
     * @return 表单属性列表。不会为null
     */
    public final Map<String, String> getAttributes() {
        return attrs;
    }
    
}
