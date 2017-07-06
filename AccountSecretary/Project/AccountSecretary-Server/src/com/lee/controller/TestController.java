package com.lee.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lee.result.JsonResult;

/**
 * 测试操作控制器
 * @author lee
 *
 */
@Controller
@RequestMapping("/test")
public class TestController {

	@PostMapping("/server_test")
    @ResponseBody
	public JsonResult test() {
		return new JsonResult();
	}
}
