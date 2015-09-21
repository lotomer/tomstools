package org.tomstools.web.persistence;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.tomstools.web.model.Config;
import org.tomstools.web.model.Menu;
import org.tomstools.web.model.Page;
import org.tomstools.web.model.User;

/**
 * 用户数据
 * 
 * @author admin
 * @date 2015年7月6日
 * @time 上午10:50:51
 * @version 1.0
 */
public interface UserMapper {
    public List<Map<String, Object>> selectUserList();

    /**
     * 根据用户名和密码获取用户信息
     * 
     * @param userName
     *            用户名
     * @param userPassword
     *            密码
     * @return 用户信息
     * @since 1.0
     */
    // @Select("SELECT u.USER_ID userId,u.USER_NAME userName,u.NICK_NAME
    // nickName,u.EMAIL,u.PHONE_NUMBER phoneNumber,CASE WHEN k.`KEY` IS NULL
    // THEN NULL WHEN now() < k.INVALID_TIME THEN k.`KEY` ELSE '' END `key` FROM
    // T_M_USERS u LEFT JOIN T_U_KEY k ON u.USER_ID=k.USER_ID WHERE
    // u.USER_NAME=#{userName} AND u.USER_PASSWD=#{userPassword} AND
    // u.IS_VALID='1'")
    public User selectUser(@Param("userName") String userName, @Param("userPassword") String userPassword);
	public User selectUserById(@Param("USER_ID")  int userId);

    /**
     * 根据密钥获取用户编号
     * 
     * @param key
     *            密钥
     * @return 用户编号
     * @since 1.0
     */
    // @Select("SELECT k.USER_ID userId,CASE WHEN now() < k.INVALID_TIME THEN k.`KEY` ELSE '' END `key`,u.USER_NAME userName,
    // u.NICK_NAME nickName,u.CLIENT_IP clientIp,u.EMAIL email,u.PHONE_NUMBER phoneNumber 
    // FROM T_U_KEY k,T_M_USERS u where k.USER_ID=u.USER_ID AND u.IS_VALID='1'  AND `KEY`= #{key} AND now() < k.INVALID_TIME")
    public User selectUserByKey(@Param("key") String key);

	public void saveLoginLog(@Param("USER_ID") int userId, @Param("KEY") String key,@Param("CLIENT_IP") String clientIp);

	public void saveLogoutLog(@Param("USER_ID") int userId, @Param("KEY") String key);
    /**
     * 根据用户编号获取用户的菜单列表
     * 
     * @param userId
     *            用户编号
     * @return 用户菜单列表
     * @since 1.0
     */
    // @Select("SELECT m.MENU_ID menuId,m.MENU_NAME menuName,m.PAGE_ID
    // pageId,m.PARENT_ID parentId,m.IS_SHOW isShow,m.ORDER_NUM
    // orderNum,m.ICON_CLASS iconClass FROM T_M_MENUS m ,T_PRI_ROLE_MENUS rm,
    // T_REL_ROLE_USER ru WHERE m.MENU_ID=rm.MENU_ID AND rm.ROLE_ID=ru.ROLE_ID
    // AND ru.USER_ID=#{userId} AND m.IS_VALID='1';")
    public List<Menu> selectUserMenus(@Param("userId") int userId);

	public List<Map<String, Object>> selectAllMenus();

	public void deleteMenu(@Param("MENU_ID") int id);

	public void saveMenu(@Param("MENU_ID") int id, @Param("MENU_NAME") String menuName,
			@Param("PARENT_ID") int parentId, @Param("PAGE_ID") Integer pageId, @Param("ORDER_NUM") Integer orderNum,
			@Param("ICON_CLASS") String iconClass, @Param("IS_SHOW") String isShow);

	public void addMenu(@Param("MENU_NAME") String menuName, @Param("PARENT_ID") int parentId,
			@Param("PAGE_ID") Integer pageId, @Param("ORDER_NUM") Integer orderNum,
			@Param("ICON_CLASS") String iconClass, @Param("IS_SHOW") String isShow);

	public List<Map<String, Object>> selectPageList();

	public List<Map<String, Object>> selectSubPages();

	public void deletePage(@Param("PAGE_ID") int id);

	public void savePage(@Param("PAGE_ID") int id, @Param("PAGE_NAME") String pageName,
			@Param("CONTENT_URL") String contentUrl, @Param("PARAMS") String params, @Param("WIDTH") String width,
			@Param("HEIGHT") String height, @Param("ICON_CLASS") String iconClass,
			@Param("AUTO_FRESH_TIME") String autoFreshTime);

	public void addPage(@Param("PAGE_NAME") String pageName, @Param("CONTENT_URL") String contentUrl,
			@Param("PARAMS") String params, @Param("WIDTH") String width, @Param("HEIGHT") String height,
			@Param("ICON_CLASS") String iconClass, @Param("AUTO_FRESH_TIME") String autoFreshTime);

	/**
	 * 根据用户编号获取用户的页面列表
	 * 
	 * @param userId
	 *            用户编号
	 * @return 用户页面列表
	 * @since 1.0
	 */
	// @Select("SELECT p.PAGE_ID pageId,p.PAGE_NAME pageName,p.PARENT_ID
	// parentId,p.CONTENT_URL contentUrl,p.PARAMS,p.USE_IFRAME
	// useIframe,p.ICON_CLASS iconClass,p.AUTO_FRESH_TIME
	// autoFreshTime,rp.IS_SHOW isShow,rp.ORDER_NUM orderNum,rp.WIDTH,rp.HEIGHT
	// FROM T_M_PAGES p,T_PRI_ROLE_PAGES rp,T_REL_ROLE_USER ru WHERE p.PAGE_ID =
	// rp.PAGE_ID AND rp.ROLE_ID=ru.ROLE_ID AND ru.USER_ID=#{userId}")
	public List<Page> selectUserPages(@Param("userId") int userId);

	// @Select("SELECT rp.PAGE_ID parentId,p.PAGE_ID pageId,p.PAGE_NAME
	// pageName,p.CONTENT_URL contentUrl,p.PARAMS,p.ICON_CLASS
	// iconClass,p.AUTO_FRESH_TIME autoFreshTime,rp.ORDER_NUM
	// orderNum,rp.WIDTH,rp.HEIGHT FROM T_REL_PAGES rp LEFT JOIN T_M_PAGES p ON
	// rp.SUB_PAGE_ID=p.PAGE_ID LEFT JOIN T_PRI_ROLE_PAGES prp ON
	// p.PAGE_ID=prp.PAGE_ID LEFT JOIN T_REL_ROLE_USER ru ON
	// prp.ROLE_ID=ru.ROLE_ID WHERE rp.PAGE_ID=#{pageId} AND
	// ru.USER_ID=#{userId} AND rp.IS_VALID=1 AND p.IS_VALID=1 ")
	public List<Page> selectUserSubPages(@Param("userId") int userId);

	/**
	 * 保存用户密钥（原来不存在）
	 * 
	 * @param userId
	 *            用户编号
	 * @param key
	 *            密钥
	 * @since 1.0
	 */
	public void insertKey(@Param("userId") int userId, @Param("key") String key);

	/**
	 * 更新用户密钥（原来存在）
	 * 
	 * @param userId
	 *            用户编号
	 * @param key
	 *            密钥
	 * @since 1.0
	 */
	public void updateKey(@Param("userId") int userId, @Param("key") String key);

    public void deleteKey(@Param("USER_ID") int userId,@Param("key") String key);

	/**
	 * 加载配置项
	 * 
	 * @return 配置项列表
	 */
	public List<Config> selectAllConfigs();

	public void saveUser(@Param("USER_ID") int userId, @Param("NICK_NAME") String nickName,
			@Param("USER_PASSWD") String newPassword, @Param("EMAIL") String email,
			@Param("PHONE_NUMBER") String phoneNumber, @Param("CLIENT_IP") String clientIp);

	public void deleteUser(@Param("USER_ID") int id);

	public void addUser(@Param("USER_NAME") String userName, @Param("USER_PASSWD") String password,
			@Param("NICK_NAME") String nickName, @Param("EMAIL") String email,
			@Param("PHONE_NUMBER") String phoneNumber, @Param("CLIENT_IP") String clientIp);

	public List<Map<String, Object>> selectRoleList();

	public void deleteRole(@Param("ROLE_ID") int id);

	public void addRole(@Param("ROLE_NAME") String roleName);

	public void saveRole(@Param("ROLE_ID") int id, @Param("ROLE_NAME") String roleName);

	public void deleteRoleUsers(@Param("ROLE_ID") int roleId);

	public void saveRoleUser(@Param("ROLE_ID") int roleId, @Param("USER_ID") int userId);

	public void deleteRoleMenus(@Param("ROLE_ID") int roleId);

	public void saveRoleMenu(@Param("ROLE_ID") int roleId, @Param("MENU_ID") int menuId);

	public List<Map<String, Object>> selectRoleUserList();

	public List<Map<String, Object>> selectRoleMenuList();

	public List<Map<String, Object>> selectRolePageList();

	public void saveRolePage(@Param("ROLE_ID") int roleId, @Param("PAGE_ID") int pageId);

	public void deleteRolePages(@Param("ROLE_ID") int roleId);

	public void saveRolePageByMenuId(@Param("ROLE_ID") int roleId, @Param("MENU_ID") int menuId);

	public void deleteSubPage(@Param("PAGE_ID") int id, @Param("SUB_PAGE_ID") int subId);

	public void saveSubPage(@Param("PAGE_ID") int id, @Param("SUB_PAGE_ID") int subId,
			@Param("ORDER_NUM") String orderNum, @Param("WIDTH") String width, @Param("HEIGHT") String height);

	public void addSubPage(@Param("PAGE_ID") int id, @Param("SUB_PAGE_ID") int subId,
			@Param("ORDER_NUM") String orderNum, @Param("WIDTH") String width, @Param("HEIGHT") String height);

	public void deleteAllSubPage(@Param("PAGE_ID")  int id);

	public Date getLastLoginTime(@Param("USER_ID") int userId, @Param("KEY") String key);

}
