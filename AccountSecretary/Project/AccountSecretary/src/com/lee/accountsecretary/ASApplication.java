package com.lee.accountsecretary;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * 全局
 * @author lee
 *
 */
public class ASApplication extends Application {

	private static ASApplication mInstance;

	private static RequestQueue mRequestQueue;//volley请求队列
	
	private boolean isLogin;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mInstance = this;
		//创建一个volley请求队列
		mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		//每次启动程序默认没有登录
		isLogin = false;
	}
	
	/**
	 * 获取Application
	 * @return {@link ASApplication}
	 */
	public static ASApplication getInstance() {
		return mInstance;
	}
	
	/**
	 * 获取volley请求队列
	 * @return {@link RequestQueue}
	 */
	public static RequestQueue getRequestQueue(){
		return mRequestQueue;
	}

	public boolean isLogin() {
		return isLogin;
	}

	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}
	
}
