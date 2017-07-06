package com.lee.namecardmanager.ui;

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
import com.lee.namecardmanager.NMApplication;
import com.lee.namecardmanager.R;
import com.lee.namecardmanager.util.AccountInfo;
import com.lee.namecardmanager.widget.LoadingDialog;
import com.lee.namecardmanager.widget.TextDialog;

/**
 * 服务器配置界面
 * @author lee
 *
 */
public class ServerSettingActivity extends Activity implements OnClickListener {
	
	private ImageButton mImgBtn_back;			//返回
	private EditText mEditText_serverAddress;	//服务器地址
	private Button mButton_ok;					//确认配置
	
	private boolean isLoading;	//是否为加载界面跳转过来
	
	private static final String VOLLEY_POST = "Str_Post_Setting";//volley请求的标签
	/* 提示框 */
	private LoadingDialog mLoadingDialog;//加载提示
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server_setting);
		//获取穿过来的数据
		Intent mIntent = getIntent();
		isLoading = mIntent.getBooleanExtra(LoadingActivity.IS_LOADING, false);
		//创建提示框
		mLoadingDialog = new LoadingDialog.Builder(ServerSettingActivity.this).setText("正在配置……").create();
		
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
		mImgBtn_back = (ImageButton) findViewById(R.id.server_setting_btn_back);
		mEditText_serverAddress = (EditText) findViewById(R.id.server_setting_ip);
		mButton_ok = (Button) findViewById(R.id.server_setting_ok);
		if (isLoading) {
			mImgBtn_back.setVisibility(View.INVISIBLE);
		}
		mEditText_serverAddress.setText(AccountInfo.getInstance().getServerAddress());
		//listener
		mImgBtn_back.setOnClickListener(this);
		mButton_ok.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.server_setting_btn_back:
				finish();
				break;

			case R.id.server_setting_ok:
				String serverUrl = mEditText_serverAddress.getText().toString().trim();
				if (TextUtils.isEmpty(serverUrl)) {
					showTextDialog(false, "服务器地址不能为空！");
				} else {
					sendPostRequest(serverUrl);
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
				ServerSettingActivity.this).setTitle("提示").setContent(msg);
		TextDialog mDialog = mBuilder.setButton("确    定",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						mBuilder.closeDialog();
						if (isRequestSuccess) {
							if (isLoading) {
								//跳转界面
								Intent mIntent = new Intent(ServerSettingActivity.this, LoginActivity.class);
								startActivity(mIntent);
							}
							finish();
						}
					}
					
				}).create();
		mDialog.show();
	}

	/**
	 * 发送HTTP请求
	 * @param url 测试地址
	 */
	private void sendPostRequest(final String url) {
		mLoadingDialog.show();
		//服务器请求地址
		final String requestUrl = url + "/test/server_test";
		//建立不确定返回对象的请求
		StringRequest request = new StringRequest(Method.POST, requestUrl, new Listener<String>() {
			
			@Override
			public void onResponse(String arg0) {
				mLoadingDialog.dismiss();
				//数据解析
				JSONObject mJSONObject = JSONObject.parseObject(arg0);
				boolean success = JSONObject.parseObject(mJSONObject.getString("success"), Boolean.class);
				if (success) {
					//存放配置
					AccountInfo mAccountInfo = AccountInfo.getInstance();
					mAccountInfo.setServerAddress(url);
					mAccountInfo.commit();
					//提示
					showTextDialog(true, "配置成功！");
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
				return null;
			}
		};
		//给请求对象添加TAG标签
		request.setTag(VOLLEY_POST);
		//将请求对象添加到请求队列中
		NMApplication.getRequestQueue().add(request);
	}
	
}
