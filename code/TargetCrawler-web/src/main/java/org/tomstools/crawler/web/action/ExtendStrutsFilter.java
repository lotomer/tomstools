/**
 * copyright (a) 2010-2015 tomstools.org. All rights reserved.
 */
package org.tomstools.crawler.web.action;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter;
import org.tomstools.crawler.web.util.JspMatcher;

/**
 * 过滤器。过滤所有非action请求
 * @author admin
 * @date 2015年5月16日 
 * @time 上午10:34:38
 * @version 1.0
 */
public class ExtendStrutsFilter extends StrutsPrepareAndExecuteFilter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        if (req.getRequestURI().endsWith("/") || JspMatcher.isMatched(req.getRequestURI())){
            resp.sendRedirect("user_login.action");
        }else{
            super.doFilter(request, response, chain);
        }
    }

}
