/**
 * copyright (a) 2010-2015 tomstools.org. All rights reserved.
 */
package org.tomstools.web.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.tomstools.common.DES;
import org.tomstools.common.Encryptable;
import org.tomstools.common.MD5;
import org.tomstools.web.model.Config;
import org.tomstools.web.model.Menu;
import org.tomstools.web.model.Page;
import org.tomstools.web.model.User;
import org.tomstools.web.persistence.UserMapper;

/**
 * 用户服务
 * 
 * @author admin
 * @date 2015年7月6日
 * @time 上午10:49:30
 * @version 1.0
 */
@Service("userService")
public class UserService {
    private final static Log LOG = LogFactory.getLog(UserService.class);
    @Autowired
    private UserMapper userMapper;
    private Encryptable keyEncryptor = new MD5(new DES());
    private Encryptable passwordEncryptor = new MD5();
    private Map<String, String> commonConfigs;
    private Map<Integer, Map<String, String>> userConfigs;

    public List<Map<String, Object>> getUserList(){
    	return userMapper.selectUserList();
    }
    /**
     * 根据用户密钥获取用户信息
     * 
     * @param key
     *            密钥
     * @return 用户信息。可能为null
     * @since 1.0
     */
    @Transactional(readOnly=true)
    public User getUserByKey(String key) {
    	if (StringUtils.isEmpty(key)){
    		return null;
    	}
        User user = userMapper.selectUserByKey(key);
        if (null != user) {
            user.setConfigs(getUserConfigs(user.getUserId()));
            // 更新用户状态
            if (!StringUtils.isEmpty(user.getKey())) {
                userMapper.updateKey(user.getUserId(), key);
            }
        }
        return user;
    }

    public List<Menu> getUserMenus(int userId) {
        return userMapper.selectUserMenus(userId);
    }

    public List<Page> getUserSubPagesByPageId(int userId, int pageId) {
        List<Page> subpages = getUserSubPages(userId);
        List<Page> result = new ArrayList<Page>();
        if (null != subpages) {
            for (Page page : subpages) {
                if (pageId == page.getParentId()) {
                    result.add(page);
                }
            }
        }

        return result;
    }

    public List<Page> getUserSubPages(int userId) {
        return userMapper.selectUserSubPages(userId);
    }

    public Page getUserPageByPageId(int userId, int pageId) {
        List<Page> pages = getUserPageList(userId);
        if (null != pages) {
            for (Page page : pages) {
                if (pageId == page.getPageId()) {
                    return page;
                }
            }
        }

        return null;
    }

    public List<Page> getUserPages(int userId) {
        List<Page> result = getUserPageList(userId);
        List<Page> subPages = getUserSubPages(userId);
        Map<Integer, List<Integer>> subpageMap = new HashMap<Integer, List<Integer>>();
        for (Page page : subPages) {
            List<Integer> pageIds = subpageMap.get(page.getParentId());
            if (null == pageIds) {
                pageIds = new ArrayList<Integer>();
                subpageMap.put(page.getParentId(), pageIds);
            }

            pageIds.add(page.getPageId());
        }

        for (Page page : result) {
            page.setSubPageId(subpageMap.get(page.getPageId()));
        }
        return result;
    }

    private List<Page> getUserPageList(int userId) {
        List<Page> pages = userMapper.selectUserPages(userId);
//        for (Page page : pages) {
//            String url = page.getContentURL();
//            String param = page.getParams();
//            if (!StringUtils.isEmpty(param)) {
//                Map<String, Object> configs = new HashMap<String, Object>();
//                HashMap<String, String> config = new HashMap<String, String>();
//                configs.put("request", config);
//                String[] params = param.split("&");
//                for (int i = 0; i < params.length; i++) {
//                    String[] kv = params[i].split("=", 2);
//                    if (2 == kv.length){
//                        config.put(kv[0], kv[1]);
//                    }
//                }
//                page.setContentURL(TemplateParser.parse(configs, url));
//            }
//            page.setParams(null);
//        }
        return pages;
    }

    /**
     * 获取用户密码
     * 
     * @param userName
     *            用户名
     * @param userPassword
     *            密码
     * @return 用户密钥。密钥不存在则返回null
     * @since 1.0
     */
    public User getUser(String userName, String userPassword) {
        String pwd = encryptPassword(userName,userPassword);
        // 先认证，获取用户信息
        User user = userMapper.selectUser(userName, pwd);
        if (null != user) {
            String key = user.getKey();
            // 认证通过
            if (null == key) {
                LOG.info("The key is not saved! Will save it.");
                // 没有保存密钥，生成新密钥
                key = generateKey(userName, userPassword);
                user.setKey(key);
                // 保存密钥
                userMapper.insertKey(user.getUserId(), key);
            } else if ("".equals(key)) {
                LOG.info("The key is invalid! Will generate and update the key.");
                // 密钥已失效。重新生成密钥
                key = generateKey(userName, userPassword);
                user.setKey(key);
                // 更新密钥
                userMapper.updateKey(user.getUserId(), key);
            } else {
                // 有有效的密钥
            }
            user.setConfigs(getUserConfigs(user.getUserId()));
        }

        return user;
    }

    public Map<String, String> getUserConfigs(int userId) {
        loadConfigs();
        if (null != userConfigs) {
            HashMap<String, String> configs = new HashMap<String, String>(commonConfigs);
            Map<String, String> userConfig = userConfigs.get(userId);
            if (null != userConfig) {
                for (Entry<String, String> e : userConfig.entrySet()) {
                    configs.put(e.getKey(), e.getValue());
                }
            }
            // 添加用户配置信息
            return configs;
        }
        return null;
    }

    private String generateKey(String userName, String userPassword) {
        try {
            return new String(keyEncryptor.encrypt((userName + "$$" + System.currentTimeMillis()).getBytes()));
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return "";
        }
    }

    private String encryptPassword(String userName,String password) {
        try {
            return new String(passwordEncryptor.encrypt((userName + "$$$" + password).getBytes()));
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return password;
        }
    }

    public void setInvalid(String key) {
        userMapper.deleteKey(key);
    }

    public String getConfig(int userId, String configName) {
        String value = null;
        loadConfigs();

        if (null != userConfigs) {
            Map<String, String> userConfig = userConfigs.get(userId);
            if (null != userConfig) {
                value = userConfig.get(configName);
            }
        }
        // 没有找到则在通用配置中找
        if (null == value && null != commonConfigs) {
            value = commonConfigs.get(configName);
        }

        return value;
    }

    private void loadConfigs() {
        if (null == userConfigs) {
            commonConfigs = new HashMap<String, String>();
            userConfigs = new HashMap<Integer, Map<String, String>>();
            List<Config> configs = userMapper.selectAllConfigs();
            for (Config config : configs) {
                if (-1 != config.getUserId()) {
                    Map<String, String> userConfig = userConfigs.get(config.getUserId());
                    if (null == userConfig) {
                        userConfig = new HashMap<String, String>();
                        userConfigs.put(config.getUserId(), userConfig);
                    }
                    userConfig.put(config.getName(), config.getValue());
                } else {
                    commonConfigs.put(config.getName(), config.getValue());
                }
            }
        }
    }

    /**
     * 校验密钥
     * @param key	用户密钥
     * @return   校验通过，则返回空字符串； 校验不通过，则返回错误信息。不返回null
     */
	public String check(String key) {
		User user = getUserByKey(key);
		if (null != user) {
			if ("".equals(user.getKey())) {
				return "密钥已失效！";
			} else {
				return "";
			}
		} else {
			return "没有找到密钥对应的用户。密钥：" + key;
		}
	}
	
	/**
     * 校验用户
     * @param user	用户
     * @return   校验通过，则返回空字符串； 校验不通过，则返回错误信息。不返回null
     */
	public String check(User user) {
		if (null != user) {
			if ("".equals(user.getKey())) {
				return "密钥已失效！";
			} else {
				return "";
			}
		} else {
			return "用户不能为null！";
		}
	}
	public static void main(String[] args) {
		UserService userService = new UserService();
		System.out.println(userService.encryptPassword("user1","123"));
	}
}
