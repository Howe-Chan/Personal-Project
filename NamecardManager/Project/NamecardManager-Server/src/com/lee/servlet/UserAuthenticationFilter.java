package com.lee.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.alibaba.fastjson.JSONObject;
import com.lee.model.User;
import com.lee.result.JsonResult;
import com.lee.service.IUserService;
import com.lee.util.MD5Util;

/**
 * 用户认证过滤
 * @author lee
 *
 */
public class UserAuthenticationFilter implements Filter{
	
	public static final String USER_ID = "user_id";

    private IUserService userService;
    
	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) arg0;
		HttpServletResponse response = (HttpServletResponse) arg1;
		// 认证用户
		String userName = request.getParameter("userName");
		String userPs = request.getParameter("userPs");
    	JsonResult result = new JsonResult();
    	
    	if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(userPs)) {
    		result.putSuccess(false, "用户认证失败！");
			response.addHeader("content-type", "application/json;charset=utf-8");
			response.getWriter().write(JSONObject.toJSONString(result));
    		return;
		}
    	
    	User user = null;
		try {
			user = userService.findByExample("name", userName);
		} catch (Exception e) {
			result.putSuccess(false, e.getMessage());
			response.addHeader("content-type", "application/json;charset=utf-8");
			response.getWriter().write(JSONObject.toJSONString(result));
			return;
		}
		
		if (user == null || !MD5Util.GetMD5Code(userPs).equals(user.getPassword())) {
	    	result.putSuccess(false, "用户认证失败");
			response.addHeader("content-type", "application/json;charset=utf-8");
			response.getWriter().write(JSONObject.toJSONString(result));
			return;
		} 

		//认证成功
		arg0.setAttribute(USER_ID, user.getId());
		arg2.doFilter(arg0, arg1);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		ApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(arg0.getServletContext());
		userService = (IUserService) appContext.getBean("userService");
	}

}
