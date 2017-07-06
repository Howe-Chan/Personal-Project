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
import android.widget.ImageButton;
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
import com.lee.namecardmanager.widget.LoadingDialog;
import com.lee.namecardmanager.widget.TextDialog;

/**
 * 忘记密码1界面
 * @author lee
 *
 */
public class ForgetPS1Activity extends Activity implements OnClickListener {
	
	private ImageButton mImgBtn_back;	//返回
	private TextView mTextView_question;//密保问题
	private EditText mEditText_answer;	//密保答案
	private Button mButton_next;		//下一步
	
	private String userName;//用户名

	/* 操作类型 */
	private static final int GET_SECURITY = 1;		//获取用户密保问题
	private static final int CHECK_SECURITY = 2;	//验证用户密保答案
	private static final String VOLLEY_POST = "Str_Post_ForgetPs1";//volley请求的标签

	/* 提示框 */
	private LoadingDialog mLoadingDialog;//加载提示
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_ps_1);
		//获取穿过来的数据
		Intent mIntent = getIntent();
		userName = mIntent.getStringExtra(LoginActivity.USER_NAME);
		
		initView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//获取密保问题
		sendPostRuest(GET_SECURITY, "");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//取消某个网络请求
		NMApplication.getRequestQueue().cancelAll(VOLLEY_POST);
	}

	/**
	 * 初始化视图
	 */
	private void initView() {
		mImgBtn_back = (ImageButton) findViewById(R.id.forget_ps1_btn_back);
		mTextView_question = (TextView) findViewById(R.id.forget_ps1_question);
		mEditText_answer = (EditText) findViewById(R.id.forget_ps1_answer);
		mButton_next = (Button) findViewById(R.id.forget_ps1_next);
		//listener
		mImgBtn_back.setOnClickListener(this);
		mButton_next.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.forget_ps1_btn_back:
				finish();
				break;

			case R.id.forget_ps1_next:
				String answer = mEditText_answer.getText().toString().trim();
				if (TextUtils.isEmpty(answer)) {
					showTextDialog("密保答案不能为空！");
				} else {
					//发送请求
					sendPostRuest(CHECK_SECURITY, answer);
				}
				break;
	
			default:
				break;
		}
	}

	/**
	 * 发送HTTP请求
	 * @param operateType 操作类型
	 * @param securityAnswer 密保答案
	 */
	private void sendPostRuest(final int operateType, final String securityAnswer) {
		//服务器请求地址
		String requestUrl = AccountInfo.getInstance().getServerAddress();
		//拼装请求url
		switch (operateType) {
			case GET_SECURITY:
				requestUrl += "/user/user_security";
				mLoadingDialog= new LoadingDialog.Builder(ForgetPS1Activity.this).setText("正在加载……").create();
				break;
				
			case CHECK_SECURITY:
				requestUrl += "/user/check_security";
				mLoadingDialog= new LoadingDialog.Builder(ForgetPS1Activity.this).setText("正在验证……").create();
				break;
	
			default:
				break;
		}
		mLoadingDialog.show();
		//建立不确定返回对象的请求
		StringRequest request = new StringRequest(Method.POST, requestUrl, new Listener<String>() {
			
			@Override
			public void onResponse(String arg0) {
				mLoadingDialog.dismiss();
				//数据解析
				JSONObject mJSONObject = JSONObject.parseObject(arg0);
				boolean success = JSONObject.parseObject(mJSONObject.getString("success"), Boolean.class);
				//请求成功
				switch (operateType) {
					case GET_SECURITY:
						if (success) {
							mTextView_question.setText(mJSONObject.getString("securityName"));
						} else {
							showGetSecurityErrDialog(mJSONObject.getString("message"));
						}
						break;

					case CHECK_SECURITY:
						if (success) {
							//跳转界面
							Intent mIntent = new Intent(ForgetPS1Activity.this, ForgetPS2Activity.class);
							mIntent.putExtra(LoginActivity.USER_NAME, userName);
							startActivity(mIntent);
							finish();
						} else {
							showTextDialog(mJSONObject.getString("message"));
						}
						break;

					default:
						break;
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				mLoadingDialog.dismiss();
				switch (operateType) {
					case GET_SECURITY:
						showGetSecurityErrDialog("请求出错：\n" + arg0.toString());
						break;

					case CHECK_SECURITY:
						showTextDialog("请求出错：\n" + arg0.toString());
						break;
	
					default:
						break;
				}
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> hashMap = null;
				switch (operateType) {
					case GET_SECURITY:
						hashMap = new HashMap<String, String>();
						hashMap.put("userName", userName);
						break;

					case CHECK_SECURITY:
						hashMap = new HashMap<String, String>();
						hashMap.put("userName", userName);
						hashMap.put("securityAnswer", securityAnswer);
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
		NMApplication.getRequestQueue().add(request);
	}
	
	/**
	 * 展示文本提示框
	 * @param msg 提示信息
	 */
	private void showTextDialog(String msg) {
		//创建dialog
		final TextDialog.Builder mBuilder = new TextDialog.Builder(
				ForgetPS1Activity.this).setTitle("提示").setContent(msg);
		TextDialog mDialog = mBuilder.setButton("确    定",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						mBuilder.closeDialog();
					}
					
				}).create();
		mDialog.show();
	}
	
	/**
	 * 获取密保问题失败
	 * @param msg 提示信息
	 */
	private void showGetSecurityErrDialog(String msg) {
		//创建dialog
		final TextDialog.Builder mBuilder = new TextDialog.Builder(
				ForgetPS1Activity.this).setTitle("提示").setContent(msg);
		TextDialog mDialog = mBuilder.setButton("确    定",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						mBuilder.closeDialog();
						finish();
					}
					
				}).create();
		mDialog.show();
	}
}
