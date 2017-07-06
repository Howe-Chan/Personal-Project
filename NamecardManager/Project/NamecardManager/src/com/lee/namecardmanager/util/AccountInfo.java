package com.lee.namecardmanager.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.lee.namecardmanager.NMApplication;

/**
 * 账户信息
 * @author lee
 *
 */
public class AccountInfo {

	private static final String USER_NAME = "user_name";			//用户名
	private static final String USER_PS = "user_ps";				//用户密码
	private static final String USER_ICON_ID = "user_icon_id";		//用户头像id
	private static final String SERVER_ADDRESS = "server_address";	//服务器地址

	/* 用于存储 */
	private static final String PREF_FILE_NAME = "account_info";
	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor editor;

	private static AccountInfo mInstance;
	
	/**
	 * 单例
	 * @return
	 */
	public static AccountInfo getInstance() {
		if (mInstance == null) {
			synchronized (AccountInfo.class) {
				if (mInstance == null) {
					mInstance = new AccountInfo(NMApplication.getInstance().getApplicationContext());
				}
			}
		}
		return mInstance;
	}
	
	private AccountInfo(Context context){
		/* 创建一个存储的方式 */
		// 实例化SharedPreferences对象（第一步）
		mSharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
		// 实例化SharedPreferences.Editor对象（第二步）
		editor = mSharedPreferences.edit();
		// 用putString的方法保存数据 (第三步)
	}

	/**
	 * 提交数据
	 */
	public void commit() {
		editor.commit();
	}

	public String getUserName() {
		return mSharedPreferences.getString(USER_NAME, "");
	}

	public void setUserName(String userName) {
		editor.putString(USER_NAME, userName);
	}

	public String getUserPs() {
		return mSharedPreferences.getString(USER_PS, "");
	}

	public void setUserPs(String userPs) {
		editor.putString(USER_PS, userPs);
	}

	public int getUserIcon() {
		return mSharedPreferences.getInt(USER_ICON_ID, 0);
	}

	public void setUserIcon(int userIcon) {
		editor.putInt(USER_ICON_ID, userIcon);
	}

	public String getServerAddress() {
		return mSharedPreferences.getString(SERVER_ADDRESS, "");
	}

	public void setServerAddress(String serverAddress) {
		editor.putString(SERVER_ADDRESS, serverAddress);
	}
	
}
