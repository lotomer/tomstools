/**
 * copyright (a) 2010-2015 tomstools.org. All rights reserved.
 */
package org.tomstools.web.action;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.tomstools.common.URLUtil;
import org.tomstools.common.parse.TemplateParser;
import org.tomstools.web.model.Menu;
import org.tomstools.web.model.Page;
import org.tomstools.web.model.User;
import org.tomstools.web.service.CodeService;
import org.tomstools.web.service.UserService;

import com.alibaba.fastjson.JSON;

/**
 * 
 * @author admin
 * @date 2015年7月6日
 * @time 下午8:45:45
 * @version 1.0
 */
@Controller
public class MainControl {
    private static final Log LOG = LogFactory.getLog(MainControl.class);
    private static final String THEME_DEFAULT = "default";
    @Autowired
    private UserService userService;

    @RequestMapping("/logout.do")
    public String logout(@RequestParam(value = "key", required = false) final String key,
            @RequestParam(value = "theme", defaultValue = THEME_DEFAULT) String theme, Model model) {
        LOG.info("[logout] theme:" + theme);
        userService.setInvalid(getKey(key));
        model.addAttribute("theme", getTheme(theme));
        model.addAttribute("error", "");
        return "login";
    }

    @RequestMapping(value="/login.do")
    public String login(@RequestParam(value = "key", required = false) final String key,
            @RequestParam(value = "userName", required = false) final String userName,
            @RequestParam(value = "userPassword", required = false) final String userPassword,
            @RequestParam(value = "verifyCode", required = false) String verifyCode,
            @RequestParam(value = "theme", required = false, defaultValue = THEME_DEFAULT) String theme,
            @RequestParam(value = "referer", required = false) String referer, Model model, HttpSession session,
            HttpServletRequest req, HttpServletResponse resp) {
        LOG.info("[login] theme:" + theme);
        model.addAttribute("theme", getTheme(theme));
        if (null != key && !"".equals(key)) {
            //return index(key, theme, model, req, resp);
        }
        model.addAttribute("userName", userName);
        model.addAttribute("userPassword", userPassword);
        if (null == userName || null == userPassword) {
            // 没有指定用户名和密码，则不提示任何错误
        } else if ("".equals(userName) || "".equals(userPassword)) {
            model.addAttribute("error", "用户名或密码不能为空！");
        } else {
            // 先进行验证码校验
            String realCode = (String) session.getAttribute(CodeService.KEY_CODE);
            if (null == verifyCode || !verifyCode.equalsIgnoreCase(realCode)) {
                model.addAttribute("error", "验证码错误！");
                model.addAttribute("msg", "用${error}");
            } else {
                User user = userService.getUser(userName, userPassword);
                if (null != user) { // 认证通过
                    List<Page> pages = userService.getUserPages(user.getUserId());
                    if (null == pages) {
                        pages = Collections.emptyList();
                    }
                    String realKey = user.getKey();
                    model.addAttribute("pages", JSON.toJSONString(pages));

                    List<Menu> menus = userService.getUserMenus(user.getUserId());
                    if (null == menus) {
                        menus = Collections.emptyList();
                    }
                    model.addAttribute("menus", JSON.toJSONString(menus));
                    model.addAttribute("user", user);
                    if (!StringUtils.isEmpty(referer)) {
                        String url = referer;
                        if (!url.contains("?")){
                            url = url + "?key=" + realKey;
                        }else if (url.endsWith("?")) {
                            url = url + "key=" + realKey;
                        }else{
                            url = URLUtil.removeURLQueryAttibute(url, "key");
                            if (url.endsWith("&")){
                                url = url + "key=" + realKey;
                            }else{
                                url = url +  "&key=" + realKey;
                            }
                        }
                        LOG.info("redirect to " + url);
                        try {
                            resp.sendRedirect(url);
                        } catch (IOException e) {
                            LOG.error(e.getMessage(),e);
                        }
                        return null;
                    } else {
                        return "index";
                    }
                } else {
                    model.addAttribute("error", "用户名/密码错误");
                }
            }
        }
        if (!StringUtils.isEmpty(referer)) {
            model.addAttribute("referer", referer);
        }
        return "login";
    }

    @RequestMapping("/index.do")
    public String index(@RequestParam(value = "key", defaultValue = "") String key,
            @RequestParam(value = "theme", defaultValue = THEME_DEFAULT) String theme, Model model,
            HttpServletRequest req, HttpServletResponse resp) {
        LOG.info("[index] theme:" + theme);
        model.addAttribute("theme", getTheme(theme));
        if (null == key || "".equals(key)) {
            model.addAttribute("error", "请先登录！");
        } else {
            key = getKey(key);
            User user = userService.getUserByKey(key);
            if (null != user) {
                if ("".equals(user.getKey())) {
                    model.addAttribute("error", "密钥已失效！");
                } else {
                    List<Page> pages = userService.getUserPages(user.getUserId());
                    if (null == pages) {
                        pages = Collections.emptyList();
                    }
                    model.addAttribute("pages", JSON.toJSONString(pages));

                    List<Menu> menus = userService.getUserMenus(user.getUserId());
                    if (null == menus) {
                        menus = Collections.emptyList();
                    }
                    model.addAttribute("menus", JSON.toJSONString(menus));

                    model.addAttribute("user", user);
                    return "index";
                }
            } else {
                model.addAttribute("error", "没有找到密钥对应的用户。密钥：" + key);
            }
        }
        model.addAttribute("referer", req.getRequestURI() + "?" + req.getQueryString());
        return "login";
    }

    private String getTheme(String theme) {
        if (null == theme || "".equals(theme)) {
            return THEME_DEFAULT;
        }
        String[] themes = theme.split(",");
        for (int i = 0; i < themes.length; i++) {
            if (!"".equals(themes[i])) {
                return themes[i];
            }
        }
        return THEME_DEFAULT;
    }

    @RequestMapping("/container.do")
    public String container(@RequestParam(value = "key", defaultValue = "") String key,
            @RequestParam(value = "theme", defaultValue = THEME_DEFAULT) String theme,
            @RequestParam("pageId") int pageId, Model model, HttpServletRequest req, HttpServletResponse resp) {
        LOG.info("[container] pageId:" + pageId);
        model.addAttribute("theme", getTheme(theme));
        if (null == key || "".equals(key)) {
            model.addAttribute("error", "密钥不能为空！");
        } else {
            key = getKey(key);
            User user = userService.getUserByKey(key);
            if (null != user) {
                if ("".equals(user.getKey())) {
                    model.addAttribute("error", "密钥已失效！");
                } else {
                    List<Page> subPages = userService.getUserSubPagesByPageId(user.getUserId(), pageId);
                    Page page = null;
                    if (null == subPages) {
                        subPages = Collections.emptyList();
                    }
                    model.addAttribute("subPages", JSON.toJSONString(subPages));

                    model.addAttribute("page", page);
                    model.addAttribute("user", user);
                    return "container";
                }
            } else {
                model.addAttribute("error", "没有找到密钥对应的用户。密钥：" + key);
            }
        }
        model.addAttribute("referer", req.getRequestURI() + "?" + req.getQueryString());
        return "login";
    }

    private Map<String, String> generateParams(String param) {
        Map<String, String> params = new HashMap<String, String>();
        if (!StringUtils.isEmpty(param)) {
            String[] array = param.split(",");
            for (String s : array) {
                String ss[] = s.split("\\:", 2);
                if (2 == ss.length) {
                    params.put(ss[0], ss[1]);
                }
            }
        }
        return params;
    }

    @RequestMapping("/redirect.do")
    public String redirect(@RequestParam(value = "key", defaultValue = "") String key,
            @RequestParam(value = "theme", defaultValue = THEME_DEFAULT) String theme,
            @RequestParam(value = "jspName") String jspName, Model model, HttpServletRequest req,
            HttpServletResponse resp) {
        LOG.info("[redirect] " + jspName);
        model.addAttribute("theme", getTheme(theme));
        if (null == key || "".equals(key)) {
            model.addAttribute("error", "密钥不能为空！");
        } else {
            key = getKey(key);
            User user = userService.getUserByKey(key);
            if (null != user) {
                model.addAttribute("user", user);
                return jspName;
            } else {
                model.addAttribute("error", "没有找到密钥对应的用户。密钥：" + key);
            }
        }
        model.addAttribute("referer", req.getRequestURI() + "?" + req.getQueryString());
        return "login";
    }

    @RequestMapping("/page.do")
    public String page(@RequestParam(value = "key", defaultValue = "") String key,
            @RequestParam(value = "theme", defaultValue = THEME_DEFAULT) String theme,
            @RequestParam(value = "id") String id, @RequestParam(value = "p", required = false) String param,
            Model model, HttpServletRequest req, HttpServletResponse resp) {
        LOG.info("[page] " + id);
        model.addAttribute("theme", getTheme(theme));
        if (null == key || "".equals(key)) {
            model.addAttribute("error", "密钥不能为空！");
        } else {
            key = getKey(key);
            User user = userService.getUserByKey(key);
            if (null != user) {
                model.addAttribute("user", user);
                // Map<String, String> params = new HashMap<String, String>();
                Page page = userService.getUserPageByPageId(user.getUserId(), Integer.parseInt(id));
                if (null != page) {
                    // 判断是否包含子页面，如果包含子页面，则直接跳转到使用容器的页面
                    List<Page> subPages = userService.getUserSubPagesByPageId(user.getUserId(), page.getPageId());
                    if (null != subPages && !subPages.isEmpty()) {
                        // 包含子页面
                        model.addAttribute("subPages", JSON.toJSONString(subPages));
                        model.addAttribute("page", page);
                        return "container";
                    }
                    Map<String, Object> configs = new HashMap<String, Object>();
                    configs.put("config", userService.getUserConfigs(user.getUserId()));
                    Map<String, String> params = generateParams(param);
                    configs.put("request", params);
                    String url = page.getContentURL();
                    if (null != url && (url.startsWith("http://") || url.startsWith("https://"))) {// 外部链接
                        if (!params.isEmpty()) {
                            StringBuilder msg = new StringBuilder();
                            boolean isFirst = true;
                            for (Entry<String, String> entry : params.entrySet()) {
                                if (isFirst) {
                                    isFirst = false;
                                } else {
                                    msg.append("&");
                                }
                                msg.append(entry.getKey()).append("=")
                                        .append(TemplateParser.parse(configs, entry.getValue()));
                            }
                            if (!url.contains("?")) {
                                url += "?" + msg.toString();
                            } else if (url.endsWith("&")) {
                                url += msg.toString();
                            } else {
                                url += "&" + msg.toString();
                            }
                        }
                        page.setContentURL(TemplateParser.parse(configs, url.replaceAll("'", "\\\\'")));
                        page.setParams(null);
                    } else {
                        // 内部链接
                        if (!StringUtils.isEmpty(param)) {
                            page.setParams("p=" + TemplateParser.parse(configs, param));
                        }
                    }
                }
                model.addAttribute("page", page);
                return "page";
            } else {
                model.addAttribute("error", "没有找到密钥对应的用户。密钥：" + key);
            }
        }
        model.addAttribute("referer", req.getRequestURI() + "?" + req.getQueryString());
        return "login";
    }

    private String getKey(String key) {
        if (!StringUtils.isEmpty(key)){
            String[] keys = key.split(",");
            return keys[0];
        }else{
            return key;
        }
    }

    @RequestMapping("/proxy.do")
    public String proxy(@RequestParam(value = "theme", defaultValue = THEME_DEFAULT) String theme,
            @RequestParam(value = "u", required = true) String url,
            @RequestParam(value = "pid", required = true) String pid,
            @RequestParam(value = "w", required = true) String width,
            @RequestParam(value = "h", required = true) String height,
            @RequestParam(value = "p", required = false) String params,
            @RequestParam(value = "r", required = false) String queryParam, Model model) {
        LOG.info("[ proxy] " + url);
        model.addAttribute("theme", getTheme(theme));
        model.addAttribute("u", url);
        model.addAttribute("pid", pid);
        model.addAttribute("w", width);
        model.addAttribute("h", height);
        model.addAttribute("p", params);
        model.addAttribute("r", queryParam);

        return "proxy";
    }
}
