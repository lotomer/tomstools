/**
 * copyright (a) 2010-2015 tomstools.org. All rights reserved.
 */
package org.tomstools.web.model;

import java.util.Map;

/**
 * 用户信息
 * 
 * @author admin
 * @date 2015年7月6日
 * @time 上午10:11:46
 * @version 1.0
 */
public class User {
    private int userId;
    private String userName;
    private String userPassword;
    private String nickName;
    private String key;
    private String email;
    private String phoneNumber;
    private String clientIp;
    private Map<String, String> configs;
    public final Map<String, String> getConfigs() {
        return configs;
    }
    public final void setConfigs(Map<String, String> configs) {
        this.configs = configs;
    }
    public final int getUserId() {
        return userId;
    }
    public final void setUserId(int userId) {
        this.userId = userId;
    }
    public final String getUserName() {
        return userName;
    }
    public final void setUserName(String userName) {
        this.userName = userName;
    }
    
    public final String getUserPassword() {
		return userPassword;
	}
	public final void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public final String getNickName() {
        return nickName;
    }
    public final void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public final String getKey() {
        return key;
    }
    public final void setKey(String key) {
        this.key = key;
    }
    public final String getEmail() {
        return email;
    }
    public final void setEmail(String email) {
        this.email = email;
    }
    public final String getPhoneNumber() {
        return phoneNumber;
    }
    public final void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getClientIp() {
		return clientIp;
	}
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	@Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("User:{userId:").append(userId).append(", userName:").append(userName)
                .append(", nickName:").append(nickName).append(", key:").append(key)
                .append(", email:").append(email).append(", phoneNumber:").append(phoneNumber)
                .append("}");
        return builder.toString();
    }
}
