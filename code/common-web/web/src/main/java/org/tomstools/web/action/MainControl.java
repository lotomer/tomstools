/**
 * copyright (a) 2010-2015 tomstools.org. All rights reserved.
 */
package org.tomstools.web.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.tomstools.common.URLUtil;
import org.tomstools.common.parse.TemplateParser;
import org.tomstools.web.model.Menu;
import org.tomstools.web.model.Page;
import org.tomstools.web.model.User;
import org.tomstools.web.model.WebMetricInfo;
import org.tomstools.web.service.CodeService;
import org.tomstools.web.service.UserService;
import org.tomstools.web.service.WebMetricService;

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
    @Autowired
    private WebMetricService webMetricService;

    @RequestMapping("/logout.do")
    public String logout(@RequestParam(value = "key", required = false) final String key,
            @RequestParam(value = "theme", defaultValue = THEME_DEFAULT) String theme, Model model) {
        LOG.info("[logout] theme:" + theme);
        userService.logout(getKey(key));
        model.addAttribute("theme", getTheme(theme));
        model.addAttribute("error", "");
        return "login";
    }
    private final static SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
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
            	String clientIp = req.getRemoteHost();
                User user = userService.login(userName, userPassword,clientIp);
                if (null != user) { // 认证通过
					// 获取用户特定的样式
					model.addAttribute("theme", getTheme(userService.getConfig(user.getUserId(), "THEME")));
					// 校验客户端IP，如果有限制的话
					if (StringUtils.isEmpty(user.getClientIp()) || check(user.getClientIp(), clientIp)) {
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
                    Date now = new Date();
                	model.addAttribute("LOGIN_TIME", DF.format(now));
                    Date lastLoginTime = userService.getLastLoginTime(user.getUserId(),user.getKey());
                    if (null != lastLoginTime){
                    	model.addAttribute("LAST_LOGIN_TIME", DF.format(lastLoginTime));
                    }else{
                    	model.addAttribute("LAST_LOGIN_TIME", DF.format(now));
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
						model.addAttribute("error", "客户端受限！");
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

	private boolean check(String clientIp, String realIp) {
		String[] cs = clientIp.split(",");
		for (int i = 0; i < cs.length; i++) {
			if (cs[i].equalsIgnoreCase(realIp)) {
				// 匹配上，则通过验证
				return true;
			}
		}
		return false;
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
            @RequestParam("pageId") int pageId, @RequestParam(value = "p", defaultValue = "") String param, 
            Model model, HttpServletRequest req, HttpServletResponse resp) {
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
                	Page page =userService.getUserPageByPageId(user.getUserId(), pageId);
                    List<Page> subPages = userService.getUserSubPagesByPageId(user.getUserId(), pageId);
                    if (null == subPages) {
                        subPages = Collections.emptyList();
                    }else{
                    	// 替换子页面的参数
                    	Map<String, Object> configs = new HashMap<String, Object>();
                        Map<String, String> params = generateParams(param);
                        configs.put("request", params);
                    	for (Page subPage : subPages) {
							subPage.setParams("");
							subPage.setContentURL(TemplateParser.parse(configs, subPage.getContentURL()));
						}
                    }
                    model.addAttribute("subPages", JSON.toJSONString(subPages));
                    model.addAttribute("p", param);
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
    @RequestMapping("/metricScript.do")
    public @ResponseBody String metricScript(@RequestParam(value = "key", defaultValue = "") String key,
    		@RequestParam("metricName") String metricName,@RequestParam(value = "p", defaultValue = "") String param,HttpServletRequest req, HttpServletResponse resp){
    	resp.setContentType("application/javascript;charset=UTF-8");
    	key = getKey(key);
        User user = userService.getUserByKey(key);
        String error = userService.check(user);
		if (!"".equals(error)) {
			return "NEED_LOGIN:" + error;
		}
		 Map<String, String> params = generateParams(param);
         //params.put("m", m);
         WebMetricInfo metricInfo = webMetricService.getWebMetric(metricName, params, user);
         if (null != metricInfo) {
             return "function tmpFun(){" + metricInfo.getTemplateScript() + "}; tmpFun();";
         }else{
        	 return "";
         }
    }
    @RequestMapping("/metric.do")
    public String metric(@RequestParam(value = "key", defaultValue = "") String key,
            @RequestParam(value = "theme", defaultValue = THEME_DEFAULT) String theme,
            @RequestParam("metricName") String metricName,@RequestParam(value="refresh",defaultValue="0") String refresh, @RequestParam(value = "p", defaultValue = "") String param,
            Model model, HttpServletRequest req, HttpServletResponse resp) {
        LOG.info("[metric] " + metricName + ",params:" + param);
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
                    Map<String, String> params = generateParams(param);
                    WebMetricInfo metricInfo = webMetricService.getWebMetric(metricName, params, user);
                    if (null != metricInfo) {
                        model.addAttribute("metricInfo", metricInfo);
                        model.addAttribute("metricInfoJson", JSON.toJSONString(metricInfo));
                        model.addAttribute("user", user);
                        model.addAttribute("refresh", refresh);
                        model.addAttribute("p", param);
                        return "metric";
                    } else {
                        // model.addAttribute("error", "没有找到对应的指标");
                        // return "error";
                        throw new RuntimeException("没有找到指标名称为\"" + metricName + "\"对应的指标数据");
                    }
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
			@RequestParam(value = "theme", defaultValue = THEME_DEFAULT) String theme,@RequestParam(value="refresh",defaultValue="0") String refresh,
			@RequestParam(value = "jspName") String jspName, @RequestParam(value = "p", required = false) String param,
			Model model, HttpServletRequest req, HttpServletResponse resp) {
		LOG.info("[redirect] " + jspName);
		model.addAttribute("theme", getTheme(theme));
		if (null == key || "".equals(key)) {
			model.addAttribute("error", "密钥不能为空！");
		} else {
			key = getKey(key);
			User user = userService.getUserByKey(key);
			if (null != user) {
				model.addAttribute("user", user);
				Map<String, String> params = generateParams(param);
				model.addAllAttributes(params);
                model.addAttribute("refresh", refresh);
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
                    	// 替换子页面的参数
                    	Map<String, Object> configs = new HashMap<String, Object>();
                        Map<String, String> params = generateParams(param);
                        configs.put("request", params);
                    	for (Page subPage : subPages) {
							subPage.setParams("");
							subPage.setContentURL(TemplateParser.parse(configs, subPage.getContentURL()));
						}
                        model.addAttribute("subPages", JSON.toJSONString(subPages));
                        model.addAttribute("p", param);
                        model.addAttribute("page", page);
                        model.addAttribute("user", user);
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
		if (!StringUtils.isEmpty(key)) {
			String[] keys = key.split(",");
			return keys[0];
		} else {
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
