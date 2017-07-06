package com.lee.namecardmanager.ui;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lee.namecardmanager.NMApplication;
import com.lee.namecardmanager.R;
import com.lee.namecardmanager.util.AccountInfo;
import com.lee.namecardmanager.util.MD5Util;
import com.lee.namecardmanager.widget.LoadingDialog;
import com.lee.namecardmanager.widget.TextDialog;

/**
 * 登陆界面
 * @author lee
 *
 */
public class LoginActivity extends Activity implements OnClickListener {
	
	private ImageView mImageView_icon;	//头像
	private EditText mEditText_account;	//账号
	private EditText mEditText_password;//密码
	private Button mButton_login;		//登录
	private TextView mTextView_forget;	//忘记密码
	private TextView mTextView_sign;	//注册
	
	public static final String USER_NAME = "user_name";//传递用户名使用的名称

	private static final String VOLLEY_POST = "Str_Post_Login";//volley请求的标签
	/* 提示框 */
	private LoadingDialog mLoadingDialog;//加载提示

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		//创建提示框
		mLoadingDialog = new LoadingDialog.Builder(LoginActivity.this).setText("正在登录……").create();
		
		initView();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//取消某个网络请求
		NMApplication.getRequestQueue().cancelAll(VOLLEY_POST);
	}

	/**
	 * 初始化视图
	 */
	private void initView() {
		mImageView_icon = (ImageView) findViewById(R.id.login_user_icon);
		mEditText_account = (EditText) findViewById(R.id.login_user_account);
		mEditText_password = (EditText) findViewById(R.id.login_user_password);
		mButton_login = (Button) findViewById(R.id.login_btn_login);
		mTextView_forget = (TextView) findViewById(R.id.login_txt_forget);
		mTextView_sign = (TextView) findViewById(R.id.login_txt_sign);
		//listener
		mButton_login.setOnClickListener(this);
		mTextView_forget.setOnClickListener(this);
		mTextView_sign.setOnClickListener(this);
		
		getUserData();
	}

	/**
	 * 获取用户数据
	 */
	private void getUserData() {
		int userIconId = AccountInfo.getInstance().getUserIcon();
		String userName = AccountInfo.getInstance().getUserName();
		mEditText_account.setText(userName);
		switch (userIconId) {
			case 0:
				mImageView_icon.setImageResource(R.drawable.default_user_icon);
				break;
				
			case 1:
				mImageView_icon.setImageResource(R.drawable.user_icon_1);
				break;
	
			case 2:
				mImageView_icon.setImageResource(R.drawable.user_icon_2);
				break;
	
			case 3:
				mImageView_icon.setImageResource(R.drawable.user_icon_3);
				break;
	
			case 4:
				mImageView_icon.setImageResource(R.drawable.user_icon_4);
				break;
	
			case 5:
				mImageView_icon.setImageResource(R.drawable.user_icon_5);
				break;
	
			case 6:
				mImageView_icon.setImageResource(R.drawable.user_icon_6);
				break;
	
			case 7:
				mImageView_icon.setImageResource(R.drawable.user_icon_7);
				break;
	
			case 8:
				mImageView_icon.setImageResource(R.drawable.user_icon_8);
				break;
	
			case 9:
				mImageView_icon.setImageResource(R.drawable.user_icon_9);
				break;
	
			default:
				break;
		}
	}

	@Override
	public void onClick(View v) {
		Intent mIntent = new Intent();
		switch (v.getId()) {
			case R.id.login_btn_login:
				String userName = mEditText_account.getText().toString().trim();
				String userPs = mEditText_password.getText().toString().trim();
				if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(userPs)) {
					showTextDialog("账号或密码不能为空！");
				} else {
					//发送请求
					sendPostRuest(userName, userPs);
				}
				break;

			case R.id.login_txt_forget:
				String account = mEditText_account.getText().toString().trim();
				if (TextUtils.isEmpty(account)) {
					showTextDialog("账号不能为空！");
				} else {
					//跳转至忘记密码界面
					mIntent = new Intent(LoginActivity.this, ForgetPS1Activity.class);
					mIntent.putExtra(USER_NAME, account);
					startActivity(mIntent);
				}
				break;

			case R.id.login_txt_sign:
				//跳转至注册界面
				mIntent = new Intent(LoginActivity.this, SignActivity.class);
				startActivity(mIntent);
				break;
	
			default:
				break;
		}
	}

	/**
	 * 发送HTTP请求
	 * @param userName 用户名
	 * @param userPs 用户密码
	 */
	private void sendPostRuest(final String userName, final String userPs) {
		mLoadingDialog.show();
		//服务器请求地址
		String requestUrl = AccountInfo.getInstance().getServerAddress() + "/user/login";
		//建立不确定返回对象的请求
		StringRequest request = new StringRequest(Method.POST, requestUrl, new Listener<String>() {
			
			@Override
			public void onResponse(String arg0) {
				mLoadingDialog.dismiss();
				//数据解析
				JSONObject mJSONObject = JSONObject.parseObject(arg0);
				boolean success = JSONObject.parseObject(mJSONObject.getString("success"), Boolean.class);
				//请求成功
				if (success) {
					//存放配置
					AccountInfo mAccountInfo = AccountInfo.getInstance();
					mAccountInfo.setUserName(userName);
					mAccountInfo.setUserPs(MD5Util.GetMD5Code(userPs));
					mAccountInfo.setUserIcon(Integer.parseInt(mJSONObject.getString("iconId")));
					mAccountInfo.commit();
					//跳转界面
					Intent mIntent = new Intent(LoginActivity.this, MainActivity.class);
					startActivity(mIntent);
					finish();
				} else {
					showTextDialog(mJSONObject.getString("message"));
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				//请求失败
				mLoadingDialog.dismiss();
				showTextDialog("请求出错：\n" + arg0.toString());
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> hashMap = new HashMap<String, String>();
				hashMap.put("userName", userName);
				hashMap.put("userPs", MD5Util.GetMD5Code(userPs));
				return hashMap;
			}
		};
		//给请求对象添加TAG标签
		request.setTag(VOLLEY_POST);
		//将请求对象添加到请求队列中
		NMApplication.getRequestQueue().add(request);
	}
	
	/**
	 * 展示文本提示框
	 * @param msg 提示信息
	 */
	private void showTextDialog(String msg) {
		//创建dialog
		final TextDialog.Builder mBuilder = new TextDialog.Builder(
				LoginActivity.this).setTitle("提示").setContent(msg);
		TextDialog mDialog = mBuilder.setButton("确    定",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						mBuilder.closeDialog();
					}
					
				}).create();
		mDialog.show();
	}
}
