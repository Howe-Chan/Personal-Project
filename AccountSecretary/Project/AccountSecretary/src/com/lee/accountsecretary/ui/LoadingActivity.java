package com.lee.accountsecretary.ui;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lee.accountsecretary.ASApplication;
import com.lee.accountsecretary.R;
import com.lee.accountsecretary.util.AccountInfo;

/**
 * 加载界面
 * @author lee
 *
 */
public class LoadingActivity extends Activity {
	
	public static final String IS_LOADING = "is_loading";//是否为加载界面跳转
	
	/* 操作类型 */
	private static final int SERVER_TEST = 1;	//服务测试
	private static final int USER_LOGIN = 2;	//用户登录

	private static final String VOLLEY_POST = "Str_Post_Loading";//volley请求的标签
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		
		loading();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//取消某个网络请求
		ASApplication.getRequestQueue().cancelAll(VOLLEY_POST);
	}

	/**
	 * 加载
	 */
	private void loading() {
		//获取是否登录的状态
		boolean isLogin = ASApplication.getInstance().isLogin();
		if (isLogin) {
			//已登录
			gotoMain();
		} else {
			if ("".equals(AccountInfo.getInstance().getServerAddress())) {
				//未配置服务器
				gotoServerSetting();
			} else {
				sendPostRuest(SERVER_TEST);
			}
		}
		
	}

	/**
	 * 发送HTTP请求
	 * @param operateType 操作类型
	 */
	private void sendPostRuest(final int operateType) {
		//服务器请求地址
		String requestUrl = AccountInfo.getInstance().getServerAddress();
		//拼装请求url
		switch (operateType) {
			case SERVER_TEST:
				requestUrl += "/test/server_test";
				break;
				
			case USER_LOGIN:
				requestUrl += "/user/login";
				break;
	
			default:
				break;
		}
		//建立不确定返回对象的请求
		StringRequest request = new StringRequest(Method.POST, requestUrl, new Listener<String>() {
			
			@Override
			public void onResponse(String arg0) {
				JSONObject mJSONObject = JSONObject.parseObject(arg0);
				boolean success = JSONObject.parseObject(mJSONObject.getString("success"), Boolean.class);
				//请求成功
				switch (operateType) {
					case SERVER_TEST:
						if (success) {
							if ("".equals(AccountInfo.getInstance().getUserName()) || "".equals(AccountInfo.getInstance().getUserName())) {
								//账号、密码为空
								gotoLogin();
							} else {
								//发送登陆请求
								sendPostRuest(USER_LOGIN);
							}
						} else {
							gotoServerSetting();
						}
						break;

					case USER_LOGIN:
						if (success) {
							//登陆成功，更改状态值、跳转主界面
							ASApplication.getInstance().setLogin(true);
							gotoMain();
						} else {
							//登陆失败跳转登陆界面
							gotoLogin();
						}
						break;

					default:
						break;
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				//请求失败,直接跳转至服务器配置页面
				gotoServerSetting();
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> hashMap = null;
				switch (operateType) {
					case USER_LOGIN:
						hashMap = new HashMap<String, String>();
						hashMap.put("userName", AccountInfo.getInstance().getUserName());
						hashMap.put("userPs", AccountInfo.getInstance().getUserPs());
						break;
			
					default:
						break;
				}
				return hashMap;
			}
		};
		//给请求对象添加TAG标签
		request.setTag(VOLLEY_POST);
		//将请求对象添加到请求队列中
		ASApplication.getRequestQueue().add(request);
	}
	
	/**
	 * 跳转至主界面
	 */
	private void gotoMain() {
		Intent mIntent = new Intent(LoadingActivity.this, MainActivity.class);
		startActivity(mIntent);
		finish();
	}
	
	/**
	 * 跳转至服务器配置界面
	 */
	private void gotoServerSetting() {
		Intent mIntent = new Intent(LoadingActivity.this, ServerSettingActivity.class);
		mIntent.putExtra(IS_LOADING, true);
		startActivity(mIntent);
		finish();
	}
	
	/**
	 * 跳转至登陆界面
	 */
	private void gotoLogin() {
		Intent mIntent = new Intent(LoadingActivity.this, LoginActivity.class);
		startActivity(mIntent);
		finish();
	}

}
