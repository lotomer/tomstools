package org.tomstools.web.action;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tomstools.web.model.User;
import org.tomstools.web.service.UserService;

import com.alibaba.fastjson.JSON;

@Controller
@RequestMapping("/sys")
public class SystemAction {
    private static Log LOG = LogFactory.getLog(SystemAction.class);
    @Autowired
    private UserService userService;
    // ------------------------------------------------------------
    // -- 用户管理
    // ------------------------------------------------------------

    @RequestMapping("/user/list.do")
    public @ResponseBody String getUserList(@RequestParam("key") String key, HttpServletRequest req,
            HttpServletResponse resp) throws Exception {
        resp.setContentType("application/json;charset=UTF-8");
        String error = userService.check(key);
        if (!"".equals(error)) {
            return "NEED_LOGIN:" + error;
        }
        List<Map<String, Object>> s = userService.getUserList();

        return JSON.toJSONString(s);
    }

    @RequestMapping("/user/save.do")
    public @ResponseBody String saveUser(@RequestParam("key") String key,
            @RequestParam(value = "id", required = false) Integer userId,
            @RequestParam(value = "OLD_PASSWORD", required = false) String oldPassword,
            @RequestParam(value = "NEW_PASSWORD", required = false) String newPassword,
            @RequestParam(value = "NICK_NAME", required = false) String nickName,
            @RequestParam(value = "EMAIL", required = false) String email,
            @RequestParam(value = "PHONE_NUMBER", required = false) String phoneNumber,
            @RequestParam(value = "CLIENT_IP", required = false) String clientIp, HttpServletRequest req,
            HttpServletResponse resp) throws Exception {
        resp.setContentType("application/json;charset=UTF-8");
        User user = userService.getUserByKey(key);
        String error = userService.check(user);
        if (!"".equals(error)) {
            return "NEED_LOGIN:" + error;
        }
        if (null != userId){
            // 旧密码为空，则只允许修改密码之外的字段
            userService.save(userId,nickName, email,phoneNumber,clientIp);
        }else{
            // 判断密码是否正确
            if (null == userService.getUser(user.getUserName(), oldPassword)) {
                return "原密码错误！";
            }
            if (StringUtils.isEmpty(nickName) && StringUtils.isEmpty(newPassword)) {
                return "没有任何改动！";
            }
            userService.saveByOwner(user.getUserId(), user.getUserName(), nickName, newPassword,email,phoneNumber,clientIp);
        }
        return "";
    }
    @RequestMapping("/user/delete.do")
    public @ResponseBody String deleteUser(@RequestParam("key") String key,
            @RequestParam(value = "id", required = true) Integer id, HttpServletRequest req, HttpServletResponse resp) {
        String error = userService.check(key);
        if (!"".equals(error)) {
            return "NEED_LOGIN:" + error;
        }
        try {
            userService.deleteUser(id);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return e.getMessage();
        }
        return "";
    }
    @RequestMapping("/user/add.do")
    public @ResponseBody String addUser(@RequestParam("key") String key,
            @RequestParam(value = "USER_NAME", required = true) String userName,
            @RequestParam(value = "NICK_NAME", required = true) String nickName,
            @RequestParam(value = "EMAIL", required = false) String email,
            @RequestParam(value = "PHONE_NUMBER", required = false) String phoneNumber,
            @RequestParam(value = "CLIENT_IP", required = false) String clientIp, HttpServletRequest req,
            HttpServletResponse resp) {
        String error = userService.check(key);
        if (!"".equals(error)) {
            return "NEED_LOGIN:" + error;
        }
        try {
            userService.addUser(userName,nickName,email,phoneNumber,clientIp);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return e.getMessage();
        }
        return "";
    }
    // ------------------------------------------------------------
    // -- 角色管理
    // ------------------------------------------------------------

    @RequestMapping("/role/select.do")
    public @ResponseBody String selectRoleList() {
        List<Map<String, Object>> result = userService.selectRoleList();
        return JSON.toJSONString(result);
    }

    @RequestMapping("/role/delete.do")
    public @ResponseBody String deleteRole(@RequestParam("key") String key,
            @RequestParam(value = "id", required = true) Integer id, HttpServletRequest req, HttpServletResponse resp) {
        String error = userService.check(key);
        if (!"".equals(error)) {
            return "NEED_LOGIN:" + error;
        }
        try {
            userService.deleteRole(id);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return e.getMessage();
        }
        return "";
    }

    @RequestMapping("/role/save.do")
    public @ResponseBody String saveRole(@RequestParam("key") String key,
            @RequestParam(value = "id", required = true) Integer id,
            @RequestParam(value = "ROLE_NAME", required = true) String roleName, HttpServletRequest req,
            HttpServletResponse resp) {
        String error = userService.check(key);
        if (!"".equals(error)) {
            return "NEED_LOGIN:" + error;
        }
        try {
            userService.saveRole(id, roleName);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return e.getMessage();
        }
        return "";
    }

    @RequestMapping("/role/add.do")
    public @ResponseBody String addRole(@RequestParam("key") String key,
            @RequestParam(value = "ROLE_NAME", required = true) String roleName, HttpServletRequest req,
            HttpServletResponse resp) {
        String error = userService.check(key);
        if (!"".equals(error)) {
            return "NEED_LOGIN:" + error;
        }
        try {
            userService.addRole(roleName);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return e.getMessage();
        }
        return "";
    }

    @RequestMapping("/role/selectUsers.do")
    public @ResponseBody String selectRoleUserList(@RequestParam("key") String key) {
        List<Map<String, Object>> result = userService.selectRoleUserList();
        return JSON.toJSONString(result);
    }

    @RequestMapping("/role/selectMenus.do")
    public @ResponseBody String selectRoleMenuList(@RequestParam("key") String key) {
        List<Map<String, Object>> result = userService.selectRoleMenuList();
        return JSON.toJSONString(result);
    }

    @RequestMapping("/role/selectPages.do")
    public @ResponseBody String selectRolePageList(@RequestParam("key") String key) {
        List<Map<String, Object>> result = userService.selectRolePageList();
        return JSON.toJSONString(result);
    }

    @RequestMapping("/role/saveUsers.do")
    public @ResponseBody String saveRoleUsers(@RequestParam("key") String key,
            @RequestParam(value = "id", required = true) Integer id,
            @RequestParam(value = "USER_IDS", required = true) String userIds, HttpServletRequest req,
            HttpServletResponse resp) {
        String error = userService.check(key);
        if (!"".equals(error)) {
            return "NEED_LOGIN:" + error;
        }
        try {
            userService.saveRoleUsers(id, userIds);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return e.getMessage();
        }
        return "";
    }

    @RequestMapping("/role/saveMenus.do")
    public @ResponseBody String saveRoleMenus(@RequestParam("key") String key,
            @RequestParam(value = "id", required = true) Integer id,
            @RequestParam(value = "MENU_IDS", required = true) String menuIds, HttpServletRequest req,
            HttpServletResponse resp) {
        String error = userService.check(key);
        if (!"".equals(error)) {
            return "NEED_LOGIN:" + error;
        }
        try {
            userService.saveRoleMenus(id, menuIds);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return e.getMessage();
        }
        return "";
    }

    @RequestMapping("/role/savePages.do")
    public @ResponseBody String saveRolePages(@RequestParam("key") String key,
            @RequestParam(value = "id", required = true) Integer id,
            @RequestParam(value = "PAGE_IDS", required = true) String pageIds, HttpServletRequest req,
            HttpServletResponse resp) {
        String error = userService.check(key);
        if (!"".equals(error)) {
            return "NEED_LOGIN:" + error;
        }
        try {
            userService.saveRolePages(id, pageIds);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return e.getMessage();
        }
        return "";
    }

    // ------------------------------------------------------------
    // -- 菜单管理
    // ------------------------------------------------------------
    @RequestMapping("/menu/select.do")
    public @ResponseBody String selectMenuList() {
        List<Map<String, Object>> result = userService.selectAllMenus();
        return JSON.toJSONString(result);
    }

    @RequestMapping("/menu/delete.do")
    public @ResponseBody String deleteMenu(@RequestParam("key") String key,
            @RequestParam(value = "id", required = true) Integer id, HttpServletRequest req, HttpServletResponse resp) {
        String error = userService.check(key);
        if (!"".equals(error)) {
            return "NEED_LOGIN:" + error;
        }
        try {
            userService.deleteMenu(id);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return e.getMessage();
        }
        return "";
    }

    @RequestMapping("/menu/save.do")
    public @ResponseBody String saveMenu(@RequestParam("key") String key,
            @RequestParam(value = "id", required = true) Integer id,
            @RequestParam(value = "MENU_NAME", required = true) String menuName,
            @RequestParam(value = "PARENT_ID", required = true) String parentId,
            @RequestParam(value = "PAGE_ID", required = false) String pageId,
            @RequestParam(value = "ORDER_NUM", required = false) String orderNum,
            @RequestParam(value = "ICON_CLASS", required = false) String iconClass,
            @RequestParam(value = "IS_SHOW", required = false) String isShow, HttpServletRequest req,
            HttpServletResponse resp) {
        String error = userService.check(key);
        if (!"".equals(error)) {
            return "NEED_LOGIN:" + error;
        }
        Integer pid = -1;
        if (!"*".equals(parentId)){
	        try{
	        	pid = Integer.parseInt(parentId);
	        }catch(NumberFormatException e){
	        }
        }
        Integer pageid = null;
        if (!StringUtils.isEmpty(pageId) && !"*".equals(pageId)){
	        try{
	        	pageid = Integer.parseInt(pageId);
	        }catch(NumberFormatException e){
	        }
        }
        Integer order = 0;
        if (!StringUtils.isEmpty(orderNum)){
	        try{
	        	order = Integer.parseInt(orderNum);
	        }catch(NumberFormatException e){
	        }
        }
        try {
            userService.saveMenu(id, menuName,pid,pageid,order,iconClass,isShow);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return e.getMessage();
        }
        return "";
    }

    @RequestMapping("/menu/add.do")
    public @ResponseBody String addMenu(@RequestParam("key") String key,
            @RequestParam(value = "MENU_NAME", required = true) String menuName,
            @RequestParam(value = "PARENT_ID", required = true) String parentId,
            @RequestParam(value = "PAGE_ID", required = false) String pageId,
            @RequestParam(value = "ORDER_NUM", required = false) String orderNum,
            @RequestParam(value = "ICON_CLASS", required = false) String iconClass,
            @RequestParam(value = "IS_SHOW", required = false) String isShow, HttpServletRequest req,
            HttpServletResponse resp) {
        String error = userService.check(key);
        if (!"".equals(error)) {
            return "NEED_LOGIN:" + error;
        }
        Integer pid = -1;
        if (!"*".equals(parentId)){
	        try{
	        	pid = Integer.parseInt(parentId);
	        }catch(NumberFormatException e){
	        }
        }
        Integer pageid = null;
        if (!StringUtils.isEmpty(pageId) && !"*".equals(pageId)){
	        try{
	        	pageid = Integer.parseInt(pageId);
	        }catch(NumberFormatException e){
	        }
        }
        Integer order = 0;
        if (!StringUtils.isEmpty(orderNum)){
	        try{
	        	order = Integer.parseInt(orderNum);
	        }catch(NumberFormatException e){
	        }
        }
        try {
            userService.addMenu(menuName,pid,pageid,order,iconClass,isShow);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return e.getMessage();
        }
        return "";
    }
    // ------------------------------------------------------------
    // -- 页面管理
    // ------------------------------------------------------------
    @RequestMapping("/page/select.do")
    public @ResponseBody String selectPageList() {
        List<Map<String, Object>> result = userService.selectPageList();
        return JSON.toJSONString(result);
    }
    @RequestMapping("/page/delete.do")
    public @ResponseBody String deletePage(@RequestParam("key") String key,
            @RequestParam(value = "id", required = true) Integer id, HttpServletRequest req, HttpServletResponse resp) {
        String error = userService.check(key);
        if (!"".equals(error)) {
            return "NEED_LOGIN:" + error;
        }
        try {
            userService.deletePage(id);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return e.getMessage();
        }
        return "";
    }
    @RequestMapping("/page/save.do")
    public @ResponseBody String savePage(@RequestParam("key") String key,
            @RequestParam(value = "id", required = true) Integer id,
            @RequestParam(value = "PAGE_NAME", required = true) String pageName,
            @RequestParam(value = "CONTENT_URL", required = false) String contentUrl,
            @RequestParam(value = "PARAMS", required = false) String params,
            @RequestParam(value = "WIDTH", required = false) String width,
            @RequestParam(value = "HEIGHT", required = false) String height,
            @RequestParam(value = "ICON_CLASS", required = false) String iconClass,
            @RequestParam(value = "AUTO_FRESH_TIME", required = false) String autoFreshTime, HttpServletRequest req,
            HttpServletResponse resp) {
        String error = userService.check(key);
        if (!"".equals(error)) {
            return "NEED_LOGIN:" + error;
        }
        if (StringUtils.isEmpty(autoFreshTime)){
        	autoFreshTime = "0";
        }
        if (StringUtils.isEmpty(width)){
        	width = "1200";
        }
        if (StringUtils.isEmpty(height)){
        	height = "500";
        }
        try {
            userService.savePage(id, pageName,contentUrl,params,width,height,iconClass,autoFreshTime);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return e.getMessage();
        }
        return "";
    }

    @RequestMapping("/page/add.do")
    public @ResponseBody String addPage(@RequestParam("key") String key,
            @RequestParam(value = "PAGE_NAME", required = true) String pageName,
            @RequestParam(value = "CONTENT_URL", required = false) String contentUrl,
            @RequestParam(value = "PARAMS", required = false) String params,
            @RequestParam(value = "WIDTH", required = false) String width,
            @RequestParam(value = "HEIGHT", required = false) String height,
            @RequestParam(value = "ICON_CLASS", required = false) String iconClass,
            @RequestParam(value = "AUTO_FRESH_TIME", required = false) String autoFreshTime, HttpServletRequest req,
            HttpServletResponse resp) {
        String error = userService.check(key);
        if (!"".equals(error)) {
            return "NEED_LOGIN:" + error;
        }
        if (StringUtils.isEmpty(autoFreshTime)){
        	autoFreshTime = "0";
        }
        if (StringUtils.isEmpty(width)){
        	width = "1200";
        }
        if (StringUtils.isEmpty(height)){
        	height = "500";
        }
        try {
            userService.addPage(pageName,contentUrl,params,width,height,iconClass,autoFreshTime);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return e.getMessage();
        }
        return "";
    }
    // ------------------------------------------------------------
    // -- 子页面管理
    // ------------------------------------------------------------
    @RequestMapping("/subpage/select.do")
    public @ResponseBody String selectSubPageList() {
        List<Map<String, Object>> result = userService.selectSubPages();
        return JSON.toJSONString(result);
    }
    @RequestMapping("/subpage/deleteAll.do")
    public @ResponseBody String deleteAllSubPage(@RequestParam("key") String key,
            @RequestParam(value = "PAGE_ID", required = true) Integer pageId, HttpServletRequest req, HttpServletResponse resp) {
        String error = userService.check(key);
        if (!"".equals(error)) {
            return "NEED_LOGIN:" + error;
        }
        try {
            userService.deleteAllSubPage(pageId);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return e.getMessage();
        }
        return "";
    }
    @RequestMapping("/subpage/delete.do")
    public @ResponseBody String deleteSubPage(@RequestParam("key") String key,
            @RequestParam(value = "PAGE_ID", required = true) Integer pageId,
            @RequestParam(value = "SUB_PAGE_ID", required = true) Integer subPageId, HttpServletRequest req, HttpServletResponse resp) {
        String error = userService.check(key);
        if (!"".equals(error)) {
            return "NEED_LOGIN:" + error;
        }
        try {
            userService.deleteSubPage(pageId,subPageId);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return e.getMessage();
        }
        return "";
    }
    @RequestMapping("/subpage/save.do")
    public @ResponseBody String saveSubPage(@RequestParam("key") String key,
            @RequestParam(value = "PAGE_ID", required = true) Integer pageId,
            @RequestParam(value = "SUB_PAGE_ID", required = true) Integer subPageId,
            @RequestParam(value = "ORDER_NUM", required = false) String orderNum,
            @RequestParam(value = "WIDTH", required = false) String width,
            @RequestParam(value = "HEIGHT", required = false) String height, HttpServletRequest req,
            HttpServletResponse resp) {
        String error = userService.check(key);
        if (!"".equals(error)) {
            return "NEED_LOGIN:" + error;
        }
        if (StringUtils.isEmpty(orderNum)){
        	orderNum = "0";
        }
        if (StringUtils.isEmpty(width)){
        	width = "1200";
        }
        if (StringUtils.isEmpty(height)){
        	height = "500";
        }
        try {
            userService.saveSubPage(pageId, subPageId,orderNum,width,height);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return e.getMessage();
        }
        return "";
    }

    @RequestMapping("/subpage/add.do")
    public @ResponseBody String addSubPage(@RequestParam("key") String key,
            @RequestParam(value = "PAGE_ID", required = true) Integer id,
            @RequestParam(value = "SUB_PAGE_ID", required = true) Integer subId,
            @RequestParam(value = "ORDER_NUM", required = false) String orderNum,
            @RequestParam(value = "WIDTH", required = false) String width,
            @RequestParam(value = "HEIGHT", required = false) String height, HttpServletRequest req,
            HttpServletResponse resp) {
        String error = userService.check(key);
        if (!"".equals(error)) {
            return "NEED_LOGIN:" + error;
        }
        if (StringUtils.isEmpty(orderNum)){
        	orderNum = "0";
        }
        if (StringUtils.isEmpty(width)){
        	width = "1200";
        }
        if (StringUtils.isEmpty(height)){
        	height = "500";
        }
        try {
            userService.addSubPage(id, subId,orderNum,width,height);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return e.getMessage();
        }
        return "";
    }
}
