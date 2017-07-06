package com.lee.controller;

import java.util.List;

import javax.servlet.ServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.lee.model.Namecard;
import com.lee.result.JsonResult;
import com.lee.service.INamecardService;
import com.lee.servlet.UserAuthenticationFilter;

/**
 * 名片操作控制器
 * 
 * @author lee
 * 
 */
@Controller
@RequestMapping("/namecard")
public class NamecardController {

	/** 日志 **/
	private final static Logger LOGGER = LoggerFactory.getLogger(NamecardController.class); 
	
	@Autowired
    private INamecardService namecardService;
	
	/**
	 * 查询所有名片
	 * 
	 * @param request
	 *            {@link ServletRequest}
	 * @param pageNum
	 *            页码
	 * @return {@link List<Namecard>}
	 */
	@PostMapping("/all_namecard")
    @ResponseBody
	public JsonResult queryAllNamecard(ServletRequest request, int pageNum){
		JsonResult result = new JsonResult();
		int userId = (int) request.getAttribute(UserAuthenticationFilter.USER_ID);
		// 总记录数
		long count = 0;
		try {
			count = namecardService.countFindAll(userId);
		} catch (Exception e) {
			result.putSuccess(false, e.getMessage());
			return result;
		}
		if (count <= 0) {
			result.putSuccess(false, "还没有收录的名片哟！");
			return result;
		}
		// 结果集
		try {
			List<Namecard> mList = namecardService.findAll((pageNum - 1) * 10, userId);
			result.putRows(mList, (long)Math.ceil(count/10d));
			System.out.println(JSONObject.toJSONString(result));
		} catch (Exception e) {
			result.putSuccess(false, e.getMessage());
			return result;
		}
		return result;
	}

	/**
	 * 新增名片
	 * 
	 * @param request
	 *            {@link ServletRequest}
	 * @param namecard
	 *            {@link Namecard}
	 * @return {@link JsonResult}
	 */
	@PostMapping("/add_namecard")
    @ResponseBody
	public JsonResult addNamecard(ServletRequest request, Namecard namecard) {
		JsonResult result = new JsonResult();
		int userId = (int) request.getAttribute(UserAuthenticationFilter.USER_ID);
		namecard.setUserId(userId);
		// 执行新增操作
		try {
			LOGGER.info(JSONObject.toJSONString(namecard));
			namecardService.add(namecard);
		} catch (Exception e) {
			result.putSuccess(false, e.getMessage());
			return result;
		}
		return result;
	}
	
	/**
	 * 根据名片ID删除该名片
	 * 
	 * @param id
	 *            名片ID
	 * @return {@link JsonResult}
	 */
	@PostMapping("/delete_namecard")
    @ResponseBody
	public JsonResult deleteNamecard(int id) {
		JsonResult result = new JsonResult();
		// 执行新增操作
		try {
			namecardService.delete(id);
		} catch (Exception e) {
			result.putSuccess(false, e.getMessage());
			return result;
		}
		return result;
	}
	
	/**
	 * 更新名片
	 * 
	 * @param request
	 *            {@link ServletRequest}
	 * @param namecard
	 *            {@link Namecard}
	 * @return {@link JsonResult}
	 */
	@PostMapping("/update_namecard")
    @ResponseBody
	public JsonResult updateNamecard(ServletRequest request, Namecard namecard) {
		JsonResult result = new JsonResult();
		int userId = (int) request.getAttribute(UserAuthenticationFilter.USER_ID);
		namecard.setUserId(userId);
		// 执行更新操作
		try {
			LOGGER.info(JSONObject.toJSONString(namecard));
			namecardService.update(namecard);
		} catch (Exception e) {
			result.putSuccess(false, e.getMessage());
			return result;
		}
		return result;
	}
	
	/**
	 * 模糊查询
	 * 
	 * @param request
	 *            {@link ServletRequest}
	 * @param value
	 *            检索值
	 * @param pageNum
	 *            页码
	 * @return {@link JsonResult}
	 */
	@PostMapping("/fuzzy_query_namecard")
    @ResponseBody
	public JsonResult queryBySearchedValue(ServletRequest request, String value, int pageNum) {
		JsonResult result = new JsonResult();
		int userId = (int) request.getAttribute(UserAuthenticationFilter.USER_ID);
		// 总记录数
		long count = 0;
		try {
			count = namecardService.countFindBySearchedValue(value, userId);
		} catch (Exception e) {
			result.putSuccess(false, e.getMessage());
			return result;
		}
		if (count <= 0) {
			result.putSuccess(false, "没有找到相关的名片哟！");
			return result;
		}
		// 结果集
		try {
			List<Namecard> mList = namecardService.findBySearchedValue(value, (pageNum - 1) * 10, userId);
			result.putRows(mList, (long)Math.ceil(count/10d));
		} catch (Exception e) {
			result.putSuccess(false, e.getMessage());
			return result;
		}
		return result;
	}
}
