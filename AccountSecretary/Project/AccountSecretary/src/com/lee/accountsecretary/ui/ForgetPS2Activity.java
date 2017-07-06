package com.lee.accountsecretary.ui;

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
import android.widget.ImageButton;

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
import com.lee.accountsecretary.util.MD5Util;
import com.lee.accountsecretary.widget.LoadingDialog;
import com.lee.accountsecretary.widget.TextDialog;

/**
 * 忘记密码2界面
 * @author lee
 *
 */
public class ForgetPS2Activity extends Activity implements OnClickListener {

	private ImageButton mImgBtn_back;		//返回
	private EditText mEditText_password;	//密码
	private EditText mEditText_passwordAgain;//确认密码
//	private ImageView mImageView_psAgainResult;//密码确认结果
	private Button mButton_ok;				//确认更改

	private String userName;//用户名

	private static final String VOLLEY_POST = "Str_Post_ForgetPs2";//volley请求的标签

	/* 提示框 */
	private LoadingDialog mLoadingDialog;//加载提示
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_ps_2);
		//获取穿过来的数据
		Intent mIntent = getIntent();
		userName = mIntent.getStringExtra(LoginActivity.USER_NAME);
		//创建提示框
		mLoadingDialog = new LoadingDialog.Builder(ForgetPS2Activity.this).setText("正在更改……").create();
		
		initView();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//取消某个网络请求
		ASApplication.getRequestQueue().cancelAll(VOLLEY_POST);
	}

	/**
	 * 初始化视图
	 */
	private void initView() {
		mImgBtn_back = (ImageButton) findViewById(R.id.forget_ps2_btn_back);
		mEditText_password = (EditText) findViewById(R.id.forget_ps2_password);
		mEditText_passwordAgain = (EditText) findViewById(R.id.forget_ps2_password_again);
		mButton_ok = (Button) findViewById(R.id.forget_ps2_ok);
		//listener
		mImgBtn_back.setOnClickListener(this);
		mButton_ok.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.forget_ps2_btn_back:
				finish();
				break;

			case R.id.forget_ps2_ok:
				String password = mEditText_password.getText().toString().trim();
				String psAgain = mEditText_passwordAgain.getText().toString().trim();
				if (password.equals(psAgain)) {
					if (!TextUtils.isEmpty(psAgain)) {
						//发送请求
						sendPostRequest(psAgain);
					} else {
						showTextDialog(false, "密码不能为空！");
					}
				} else {
					showTextDialog(false, "两次输入的密码不一致！");
				}
				
				break;
	
			default:
				break;
		}
	}

	/**
	 * 展示文本提示框
	 * @param isRequestSuccess 是否为请求成功
	 * @param msg 提示信息
	 */
	private void showTextDialog(final boolean isRequestSuccess, String msg) {
		//创建dialog
		final TextDialog.Builder mBuilder = new TextDialog.Builder(
				ForgetPS2Activity.this).setTitle("提示").setContent(msg);
		TextDialog mDialog = mBuilder.setButton("确    定",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						mBuilder.closeDialog();
						if (isRequestSuccess) {
							//跳转界面
							Intent mIntent = new Intent(ForgetPS2Activity.this, LoginActivity.class);
							startActivity(mIntent);
							finish();
						}
					}
					
				}).create();
		mDialog.show();
	}

	/**
	 * 发送HTTP请求
	 * @param userPs 需更改的密码
	 */
	private void sendPostRequest(final String userPs) {
		mLoadingDialog.show();
		//服务器请求地址
		String requestUrl = AccountInfo.getInstance().getServerAddress() + "/user/change_userps";
		//建立不确定返回对象的请求
		StringRequest request = new StringRequest(Method.POST, requestUrl, new Listener<String>() {
			
			@Override
			public void onResponse(String arg0) {
				mLoadingDialog.dismiss();
				//数据解析
				JSONObject mJSONObject = JSONObject.parseObject(arg0);
				boolean success = JSONObject.parseObject(mJSONObject.getString("success"), Boolean.class);
				if (success) {
					//提示
					showTextDialog(true, "密码更改成功！");
				} else {
					showTextDialog(false, mJSONObject.getString("message"));
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				mLoadingDialog.dismiss();
				//请求失败,提示信息
				showTextDialog(false, "请求出错：\n" + arg0.toString());
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
		ASApplication.getRequestQueue().add(request);
	}

}
