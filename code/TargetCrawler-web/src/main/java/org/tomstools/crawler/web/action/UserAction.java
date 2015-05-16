/**
 * copyright (a) 2010-2015 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.web.action;

import org.apache.struts2.ServletActionContext;
import org.springframework.stereotype.Controller;
import org.tomstools.crawler.web.util.ContextUtil;

/**
 * 
 * @author admin
 * @date 2015年5月16日
 * @time 上午12:32:26
 * @version 1.0
 */
@Controller("userAction")
public class UserAction {
    public static final String USER_INFO = "USER_INFO";

    public String login() {
        String userName = ContextUtil.getRequestParameter("userName");
        String userPassword = ContextUtil.getRequestParameter("userPassword");
        if ("admin".equals(userName) && "1".equals(userPassword)) {
            ServletActionContext.getRequest().getSession(true).setAttribute(USER_INFO, "a");
            return "success";
        } else {
            return "fail";
        }
    }

}
