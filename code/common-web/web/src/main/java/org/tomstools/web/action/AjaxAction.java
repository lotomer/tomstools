/**
 * copyright (a) 2010-2015 tomstools.org. All rights reserved.
 */
package org.tomstools.web.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tomstools.web.crawler.PageFetcher;
import org.tomstools.web.model.User;
import org.tomstools.web.service.CodeService;
import org.tomstools.web.service.UserService;

/**
 * ajax
 * 
 * @author admin
 * @date 2015年5月16日
 * @time 上午9:42:03
 * @version 1.0
 */
@Controller
@RequestMapping("/ajax")
public class AjaxAction {
    @Autowired
    private UserService userService;
    @Autowired
    private CodeService codeService;

    @RequestMapping("/getUser.do")
    public @ResponseBody User getUser(@RequestParam("key") String key, HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        resp.setContentType("application/json;charset=UTF-8");
        return userService.getUserByKey(key);
    }

    @RequestMapping("/code.do")
    public @ResponseBody void getCode(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        codeService.getCode(req, resp);
    }

    @RequestMapping("/remote.do")
    public @ResponseBody String remote(@RequestParam("key") String key, @RequestParam("url") String url,
            @RequestParam(value = "pageEncoding", defaultValue = "UTF-8") String pageEncoding, HttpServletRequest req,
            HttpServletResponse resp) throws IOException {
        PageFetcher pageFetcher = new PageFetcher(pageEncoding);
        return pageFetcher.fetchPageContent(url);
    }
}
