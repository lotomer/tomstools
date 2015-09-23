/**
 * copyright (a) 2010-2015 tomstools.org. All rights reserved.
 */
package org.tomstools.web.service;

import java.util.ArrayList;
import java.util.Date;
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
@Transactional()
public class UserService {
    private final static Log LOG = LogFactory.getLog(UserService.class);
    private static final String DEFAULT_PASSWORD = "123";
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
    public List<Map<String, Object>> selectAllMenus(){
        return userMapper.selectAllMenus();
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

    public void logout(String key) {
    	User user = getUserByKey(key);
    	if (null != user){
    		// 删除密钥
    		userMapper.deleteKey(user.getUserId(),key);
    		// 记录登出日志
    		userMapper.saveLogoutLog(user.getUserId(),key);
    	}
    }

    /**
     * 根据配置名获取用户配置
     * @param userId	用户编号
     * @param configName	配置名
     * @return 配置值。没有匹配上则返回null
     */
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
	public void saveByOwner(int userId, String userName,String nickName, String newPassword, String email, String phoneNumber, String clientIp) {
		String pwd = null;
		if (!StringUtils.isEmpty(newPassword)){
			pwd = encryptPassword(userName, newPassword);
		}
		userMapper.saveUser(userId,nickName,pwd,email,phoneNumber,clientIp);
	}
	public void save(int userId, String nickName, String email, String phoneNumber, String clientIp) {
	    userMapper.saveUser(userId,nickName,null,email,phoneNumber,clientIp);
	}
	public List<Map<String, Object>> selectRoleList() {
        return userMapper.selectRoleList();
    }
	public List<Map<String, Object>> selectSubPages() {
        return userMapper.selectSubPages();
    }
	public List<Map<String, Object>> selectPageList() {
        return userMapper.selectPageList();
    }
    public void deleteRole(int id) {
        userMapper.deleteRole(id);
    }
    public void addRole(String roleName) {
        userMapper.addRole(roleName);
    }
    public void saveRole(int id, String roleName) {
        userMapper.saveRole(id,roleName);
    }
    public void saveRoleUsers(int roleId, String userIds) {
        // 首先清除原有角色的关联数据
        userMapper.deleteRoleUsers(roleId);
        if (!StringUtils.isEmpty(userIds)){
            String[] ids = userIds.split(",");
            for (int i = 0; i < ids.length; i++) {
                if (!StringUtils.isEmpty(ids[i])){
                    userMapper.saveRoleUser(roleId,Integer.parseInt(ids[i]));
                }
            }
        }
    }
    public void saveRoleMenus(int roleId, String menuIds) {
        // 首先清除原有角色的关联数据
        userMapper.deleteRoleMenus(roleId);
        if (!StringUtils.isEmpty(menuIds)){
            String[] ids = menuIds.split(",");
            for (int i = 0; i < ids.length; i++) {
                if (!StringUtils.isEmpty(ids[i])){
                    userMapper.saveRoleMenu(roleId,Integer.parseInt(ids[i]));
                    // 给菜单默认的页面授权
                    //userMapper.saveRolePageByMenuId(roleId,Integer.parseInt(ids[i]));
                }
            }
        }
    }
    public void saveRolePages(int roleId, String pageIds) {
        // 首先清除原有角色的关联数据
        userMapper.deleteRolePages(roleId);
        if (!StringUtils.isEmpty(pageIds)){
            String[] ids = pageIds.split(",");
            for (int i = 0; i < ids.length; i++) {
                if (!StringUtils.isEmpty(ids[i])){
                    userMapper.saveRolePage(roleId,Integer.parseInt(ids[i]));
                }
            }
        }
    }
    public List<Map<String, Object>> selectRoleUserList() {
        return userMapper.selectRoleUserList();
    }
    public List<Map<String, Object>> selectRoleMenuList() {
        return userMapper.selectRoleMenuList();
    }
    public List<Map<String, Object>> selectRolePageList() {
        return userMapper.selectRolePageList();
    }
    public void deleteUser(int id) {
        userMapper.deleteUser(id);
    }
    public void addUser(String userName, String nickName, String email, String phoneNumber, String clientIp) {
        userMapper.addUser(userName,encryptPassword(userName, DEFAULT_PASSWORD),nickName,email,phoneNumber,clientIp);
    }
	public void deleteMenu(int id) {
		userMapper.deleteMenu(id);
	}
	public void saveMenu(int id, String menuName, int parentId, Integer pageId, Integer orderNum,
			String iconClass, String isShow) {
		userMapper.saveMenu(id, menuName,parentId,pageId,orderNum,iconClass,isShow);
	}
	public void addMenu(String menuName, Integer parentId, Integer pageId, Integer orderNum, String iconClass,
			String isShow) {
		userMapper.addMenu(menuName,parentId,pageId,orderNum,iconClass,isShow);
	}
	public void deletePage(int id) {
		userMapper.deletePage(id);
	}
	public void savePage(int id, String pageName, String contentUrl, String params, String width, String height,
			String iconClass, String autoFreshTime) {
		userMapper.savePage(id,pageName,contentUrl,params,width,height,iconClass,autoFreshTime);
	}
	public void addPage(String pageName, String contentUrl, String params, String width, String height,
			String iconClass, String autoFreshTime) {
		userMapper.addPage(pageName,contentUrl,params,width,height,iconClass,autoFreshTime);
	}
	public void deleteSubPage(int id, int subId) {
		userMapper.deleteSubPage(id,subId);
	}
	public void saveSubPage(int id, int subId, String orderNum, String width, String height) {
		userMapper.saveSubPage(id, subId,orderNum,width,height);
	}
	public void addSubPage(int id, int subId, String orderNum, String width, String height) {
		userMapper.addSubPage(id, subId,orderNum,width,height);
	}
	public User getUserById(int userId) {
		return userMapper.selectUserById(userId);
	}
	public void deleteAllSubPage(int id) {
		userMapper.deleteAllSubPage(id);
	}
	public User login(String userName, String userPassword, String clientIp) {
		User user = getUser(userName, userPassword);
		if (null != user && !StringUtils.isEmpty(user.getKey())){
			// 记录登陆日志
			userMapper.saveLoginLog(user.getUserId(),user.getKey(),clientIp);
		}
		return user;
	}
	public Date getLastLoginTime(int userId, String key) {
		return userMapper.getLastLoginTime(userId,key);
	}
}
