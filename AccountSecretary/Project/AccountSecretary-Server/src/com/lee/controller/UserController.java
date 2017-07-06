package com.lee.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.lee.model.Type;
import com.lee.model.User;
import com.lee.result.JsonResult;
import com.lee.service.IRecordService;
import com.lee.service.ISecurityService;
import com.lee.service.ITypeService;
import com.lee.service.IUserService;
import com.lee.util.DateConvertUtil;
import com.lee.util.MD5Util;
import com.lee.util.QueryDate;

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

    @Autowired
    private ITypeService typeService;

    @Autowired
    private IRecordService recordService;
    
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
					userService.updateExampleById(user.getId(), "password", userPs);
				} catch (Exception e) {
					result.putSuccess(false, e.getMessage());
				}
			} else {
				result.putSuccess(false, "该用户不存在！");
			}
		}
    	return result;
    }
    
    /**
     * 获取个人信息
     * @param userName 用户名
     * @return {@link JsonResult}
     */
    @PostMapping("/self_info")
    @ResponseBody
    private JsonResult getSelfInfo(String userName) {
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
				try {
					//用户所有类别
					List<Type> mList = typeService.findByUserId(user.getId());
					//获取当前日期
					SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
					SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
					QueryDate nowMonth = DateConvertUtil.getMonthByDate(monthFormat.format(new Date()));
					QueryDate nowYear = DateConvertUtil.getYearByDate(yearFormat.format(new Date()));
					//月结余、年结余
					double monthSurplus = recordService.findSurplusByDate(user.getId(), nowMonth.getStartDate(), nowMonth.getEndDate());
					double yearSurplus = recordService.findSurplusByDate(user.getId(), nowYear.getStartDate(), nowYear.getEndDate());
					//设定参数值
					result.putRows(mList);
					result.put("monthSurplus", monthSurplus);
					result.put("yearSurplus", yearSurplus);
				} catch (Exception e) {
					result.putSuccess(false, e.getMessage());
				}
			} else {
				result.putSuccess(false, "该用户不存在！");
			}
		}
    	return result;
    }

    /**
     * 获取用户所有类别
     * @param userName 用户名
     * @return {@link JsonResult}
     */
    @PostMapping("/self_all_type")
    @ResponseBody
    public JsonResult getSelfAllType(String userName) {
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
				List<Type> mList = new ArrayList<Type>();
				try {
					mList = typeService.findByUserId(user.getId());
				} catch (Exception e) {
					result.putSuccess(false, e.getMessage());
					return result;
				}
				if (mList.size() > 0) {
					result.putRows(mList);
				} else {
					result.putSuccess(false, "系统类别、个人类别尚未添加！");
				}
			} else {
				result.putSuccess(false, "该用户不存在！");
			}
		}
    	return result;
    }

    /**
     * 删除用户类别
     * @param userName 用户名
     * @param typeId 类别id
     * @return {@link JsonResult}
     */
    @PostMapping("/delete_user_type")
    @ResponseBody
    public JsonResult deleteUserType(String userName, int typeId) {
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
				List<Type> mList = new ArrayList<Type>();
				try {
					typeService.deleteByIdAndUserId(typeId, user.getId());
					mList = typeService.findByUserId(user.getId());
				} catch (Exception e) {
					result.putSuccess(false, e.getMessage());
					return result;
				}
				if (mList.size() > 0) {
					result.putRows(mList);
				} else {
					result.putSuccess(false, "系统类别、个人类别尚未添加！");
				}
			} else {
				result.putSuccess(false, "该用户不存在！");
			}
		}
    	return result;
    }

    /**
     * 新增用户类别
     * @param userName 用户名
     * @param typeName 类别名
     * @return {@link JsonResult}
     */
    @PostMapping("/add_user_type")
    @ResponseBody
    public JsonResult addUserType(String userName, String typeName) {
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
				Type type = null;
				try {
					type = typeService.findByUserId(user.getId(), "name", typeName);
				} catch (Exception e) {
					result.putSuccess(false, e.getMessage());
					return result;
				}
				if (type != null) {
					result.putSuccess(false, "该类别已存在！");
				} else {
					List<Type> mList = new ArrayList<Type>();
					try {
						Type addType = new Type();
						addType.setName(typeName);
						addType.setUserId(user.getId());
						typeService.add(addType);
						mList = typeService.findByUserId(user.getId());
					} catch (Exception e) {
						result.putSuccess(false, e.getMessage());
						return result;
					}
					if (mList.size() > 0) {
						result.putRows(mList);
					} else {
						result.putSuccess(false, "系统类别、个人类别尚未添加！");
					}
				}
			} else {
				result.putSuccess(false, "该用户不存在！");
			}
		}
    	return result;
    }
}
