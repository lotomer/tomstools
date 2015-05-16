/**
 * copyright (a) 2010-2015 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.web.action;

import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * 
 * @author admin
 * @date 2015年5月16日 
 * @time 上午10:17:25
 * @version 1.0
 */
public class LoginInteceptor extends AbstractInterceptor {
    private static final long serialVersionUID = -4559618107729288255L;

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        ActionContext ctx = invocation.getInvocationContext();
        Map<String,Object> session = ctx.getSession();
         
        Object user = session.get(UserAction.USER_INFO);
        if(user != null){
            return invocation.invoke();
        }
        ctx.put("tip", "您还没有登录，请登录或注册先亲。。");    
        return "login";
    }

}
