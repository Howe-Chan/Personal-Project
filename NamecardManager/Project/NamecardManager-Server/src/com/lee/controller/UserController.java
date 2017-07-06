package com.lee.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lee.model.Security;
import com.lee.model.User;
import com.lee.result.JsonResult;
import com.lee.service.ISecurityService;
import com.lee.service.IUserService;
import com.lee.util.MD5Util;

/**
 * 用户操作控制器
 * @author lee
 *
 */
@Controller
@RequestMapping("/user")
public class UserController {

	/** 日志 **/
    private final static Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private IUserService userService;

    @Autowired
    private ISecurityService securityService;
    
    /**
     * 登录
     * @param userName 用户名
     * @param userPs 用户密码
     * @return {@link JsonResult}
     */
    @PostMapping("/login")
    @ResponseBody
    public JsonResult login(String userName, String userPs) {
    	JsonResult result = new JsonResult();
    	if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(userPs)) {
    		result.putSuccess(false, "用户名或密码不能为空！");
		} else {
			User user = null;
			try {
				user = userService.findByExample("name", userName);
			} catch (Exception e) {
				result.putSuccess(false, e.getMessage());
				return result;
			}
			if (user != null) {
				if (MD5Util.GetMD5Code(userPs).equals(user.getPassword())) {
					result.put("iconId", user.getIconId());
				} else {
					result.putSuccess(false, "密码错误！");
				}
			} else {
				result.putSuccess(false, "该用户不存在！");
			}
		}
    	return result;
    }

    /**
     * 获取所有密保问题
     * @return {@link JsonResult}
     */
    @PostMapping("/all_security")
    @ResponseBody
    public JsonResult getAllSecurity() {
    	JsonResult result = new JsonResult();
		List<Security> mList = new ArrayList<Security>();
		try {
			mList = securityService.findAll();
		} catch (Exception e) {
			result.putSuccess(false, e.getMessage());
			return result;
		}
		if (mList.size() > 0) {
			result.putRows(mList);
		} else {
			result.putSuccess(false, "系统未设置密保问题！");
		}
    	return result;
    }
    
    /**
     * 注册用户
     * @param user {@link User}
     * @return {@link JsonResult}
     */
    @PostMapping("/register")
    @ResponseBody
    public JsonResult register(User user) {
    	JsonResult result = new JsonResult();
		User findUser = null;
		try {
			findUser = userService.findByExample("name", user.getName());
		} catch (Exception e) {
			result.putSuccess(false, e.getMessage());
			return result;
		}
		if (findUser != null) {
			result.putSuccess(false, "该用户已存在！");
		} else {
			try {
				userService.add(user);
			} catch (Exception e) {
				result.putSuccess(false, e.getMessage());
			}
		}
    	return result;
    }
    
    /**
     * 获取用户密保
     * @param userName 用户名
     * @return {@link JsonResult}
     */
    @PostMapping("/user_security")
    @ResponseBody
    public JsonResult getUserSecurity(String userName) {
    	JsonResult result = new JsonResult();
    	if (StringUtils.isEmpty(userName)) {
			result.putSuccess(false, "用户名不能为空！");
		} else {
			User user = null;
			try {
				user = userService.findByExample("name", userName);
			} catch (Exception e) {
				result.putSuccess(false, e.getMessage());
				return result;
			}
			if (user != null) {
				Security security = null;
				try {
					security = securityService.findById(user.getSecurityId());
				} catch (Exception e) {
					result.putSuccess(false, e.getMessage());
					return result;
				}
				if (security != null) {
					result.put("securityName", security.getName());
				} else {
					result.putSuccess(false, "密保问题不存在！");
				}
			} else {
				result.putSuccess(false, "该用户不存在！");
			}
		}
    	return result;
    }
    
    /**
     * 验证密保问题
     * @param userName 用户名
     * @param securityAnswer 密保答案
     * @return {@link JsonResult}
     */
    @PostMapping("/check_security")
    @ResponseBody
    public JsonResult checkUserSecurityAnswer(String userName, String securityAnswer) {
    	JsonResult result = new JsonResult();
    	if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(securityAnswer)) {
			result.putSuccess(false, "用户名或密保答案不能为空！");
		} else {
			User user = null;
			try {
				user = userService.findByExample("name", userName);
			} catch (Exception e) {
				result.putSuccess(false, e.getMessage());
				return result;
			}
			if (user != null) {
				if (!securityAnswer.equals(user.getSecurityAnswer())) {
					result.putSuccess(false, "密保验证失败！");
				}
			} else {
				result.putSuccess(false, "该用户不存在！");
			}
		}
    	return result;
    }

    /**
     * 更改用户密码
     * @param userName 用户名
     * @param userPs 用户密码
     * @return {@link JsonResult}
     */
    @PostMapping("/change_userps")
    @ResponseBody
    public JsonResult changeUserPs(String userName, String userPs) {
    	JsonResult result = new JsonResult();
    	if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(userPs)) {
    		result.putSuccess(false, "用户名或密码不能为空！");
		} else {
			User user = null;
			try {
				user = userService.findByExample("name", userName);
			} catch (Exception e) {
				result.putSuccess(false, e.getMessage());
				return result;
			}
			if (user != null) {
				try {
					userService.updateExampleById(user.getId(), "password", MD5Util.GetMD5Code(userPs));
				} catch (Exception e) {
					result.putSuccess(false, e.getMessage());
				}
			} else {
				result.putSuccess(false, "该用户不存在！");
			}
		}
    	return result;
    }
    
}
