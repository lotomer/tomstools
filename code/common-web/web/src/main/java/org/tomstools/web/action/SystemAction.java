package org.tomstools.web.action;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tomstools.web.service.UserService;

import com.alibaba.fastjson.JSON;

@Controller
@RequestMapping("/sys")
public class SystemAction {
	// private static Log LOG = LogFactory.getLog(SystemAction.class);
	@Autowired
	private UserService userService;

	@RequestMapping("/user/list.do")
	public @ResponseBody String getUserList(@RequestParam("key") String key, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		resp.setContentType("application/json;charset=UTF-8");
		String error = userService.check(key);
		if (!"".equals(error)) {
			return error;
		}
		List<Map<String, Object>> s = userService.getUserList();

		return JSON.toJSONString(s);
	}
}
