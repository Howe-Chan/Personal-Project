package com.lee.accountsecretary.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lee.accountsecretary.ASApplication;
import com.lee.accountsecretary.R;
import com.lee.accountsecretary.model.Security;
import com.lee.accountsecretary.model.User;
import com.lee.accountsecretary.util.AccountInfo;
import com.lee.accountsecretary.util.MD5Util;
import com.lee.accountsecretary.widget.LoadingDialog;
import com.lee.accountsecretary.widget.TextDialog;

/**
 * 注册界面
 * @author lee
 *
 */
public class SignActivity extends Activity implements OnClickListener {
	
	private ImageButton mImgBtn_back;		//返回键
	private ImageView mImg_userIcon;		//用户头像
	private LinearLayout mLayout_scrollUserIcon;//用户头像列表
	private EditText mEdit_userAccount;		//用户账号
	private EditText mEdit_userPassword;	//用户密码
	private EditText mEdit_userPsAgain;		//用户确认密码
	private ImageView mImg_psAgainResult;	//密码确认结果
	private Spinner mSpinner_securityQ;		//密保问题
	private EditText mEdit_securityA;		//密保答案
	private Button mBtn_sign;				//注册

	List<Security> securityList = new ArrayList<Security>();//密保问题列表
	
	/* 操作类型 */
	private static final int ALL_SECURITY = 1;		//获取所有密保问题
	private static final int REGISTER = 2;			//注册用户
	private static final String VOLLEY_POST = "Str_Post_Sign";//volley请求的标签

	/* 提示框 */
	private LoadingDialog mLoadingDialog;//加载提示
	
	/* 被选择的字段 */
	private int iconId = 0;		//头像id
	private int securityId = 0;	//密保问题id

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign);
		
		initView();
		initSpinner();
		initScrollView();
		userPsAgain();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//发送请求，获取所有密保问题
		sendPostRuest(ALL_SECURITY, null);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//取消某个网络请求
		ASApplication.getRequestQueue().cancelAll(VOLLEY_POST);
	}

	/**
	 * 初始化视图
	 */
	private void initView() {
		mImgBtn_back = (ImageButton) findViewById(R.id.sign_btn_back);
		mImg_userIcon = (ImageView) findViewById(R.id.sign_user_icon);
		mLayout_scrollUserIcon = (LinearLayout) findViewById(R.id.sign_scroll_user_icon);
		mEdit_userAccount = (EditText) findViewById(R.id.sign_user_account);
		mEdit_userPassword = (EditText) findViewById(R.id.sign_user_password);
		mEdit_userPsAgain = (EditText) findViewById(R.id.sign_user_password_again);
		mImg_psAgainResult = (ImageView) findViewById(R.id.sign_ps_again_result);
		mSpinner_securityQ = (Spinner) findViewById(R.id.sign_security_question);
		mEdit_securityA = (EditText) findViewById(R.id.sign_security_answer);
		mBtn_sign = (Button) findViewById(R.id.sign_btn_sign);
		//listener
		mImgBtn_back.setOnClickListener(this);
		mBtn_sign.setOnClickListener(this);
	}

	/**
	 * 初始化下拉框
	 */
	private void initSpinner() {
		//设置监听
		mSpinner_securityQ.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (position == 0) {
					mEdit_securityA.setEnabled(false);
					mEdit_securityA.setText("");
				} else {
					mEdit_securityA.setEnabled(true);
					//赋值密保问题id
					securityId = securityList.get(position - 1).getId();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				mEdit_securityA.setEnabled(false);
			}
		});
	}
	
	/**
	 * 解析数据
	 */
	private void parseData() {
		List<String> data = new ArrayList<String>();
		data.add("请选择密保问题");
		for (int i = 0; i < securityList.size(); i++) {
			data.add(securityList.get(i).getName());
		}
		refershSecurityList(data);
	}
	
	/**
	 * 刷新密保问题列表
	 * @param data {@link List<String>}
	 */
	private void refershSecurityList(final List<String> data) {
		//设置下拉框的布局样式
		ViewGroup.LayoutParams vlp = mSpinner_securityQ.getLayoutParams();
		int width = vlp.width;
		final AbsListView.LayoutParams lp = new AbsListView.LayoutParams(width, 50);
		//设置适配器
		ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.sign_spinner_checked_text, data){
			@Override
			public View getDropDownView(int position, View convertView,
					ViewGroup parent) {
				// TODO Auto-generated method stub
				//获取
				View view = View.inflate(SignActivity.this, R.layout.sign_spinner_layout, null);
				view.setLayoutParams(lp);
				//设置对应的数据
				TextView mTextView_text = (TextView) view.findViewById(R.id.spinner_text);
				mTextView_text.setText(data.get(position));
				//选中背景颜色
				if (mSpinner_securityQ.getSelectedItemPosition() == position) {
					view.setBackgroundColor(getResources().getColor(R.color.light_grey_b1));
				} else {
					view.setBackgroundColor(getResources().getColor(R.color.white));
				}
				return view;
			}
		};
		mAdapter.setDropDownViewResource(R.layout.sign_spinner_layout);
		mSpinner_securityQ.setAdapter(mAdapter);
	}
	
	/**
	 * 初始化SrollView
	 */
	private void initScrollView() {
		//获取高度
		LinearLayout mLayout_scrollLayout = (LinearLayout) findViewById(R.id.sign_scroll_layout);
		ViewGroup.LayoutParams vlp = mLayout_scrollLayout.getLayoutParams();
		int width = vlp.width;
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
		//获取页面
		View view = View.inflate(this, R.layout.sign_srollview_layout, null);
		view.setLayoutParams(lp);
		setUserIconListener(view);
		mLayout_scrollUserIcon.addView(view);
	}

	/**
	 * 给所有Icon设置监听
	 * @param view
	 */
	private void setUserIconListener(View view) {
		for(int i=1; i<=9; i++){
			//获取该组件
			View mView = getViewForIndex(view, i);
			//设置监听
			setViewOnClickListener(mView, i);
		}
	}

	/**
	 * 按照索引获取具体的某个视图
	 * @param view 父控件
	 * @param index 索引
	 * @return
	 */
	private View getViewForIndex(View view, int index) {
		View mView = null;
		switch (index) {
			case 1:
				mView = view.findViewById(R.id.srollview_icon_1);
				break;

			case 2:
				mView = view.findViewById(R.id.srollview_icon_2);
				break;

			case 3:
				mView = view.findViewById(R.id.srollview_icon_3);
				break;

			case 4:
				mView = view.findViewById(R.id.srollview_icon_4);
				break;

			case 5:
				mView = view.findViewById(R.id.srollview_icon_5);
				break;

			case 6:
				mView = view.findViewById(R.id.srollview_icon_6);
				break;

			case 7:
				mView = view.findViewById(R.id.srollview_icon_7);
				break;

			case 8:
				mView = view.findViewById(R.id.srollview_icon_8);
				break;

			case 9:
				mView = view.findViewById(R.id.srollview_icon_9);
				break;
	
			default:
				break;
		}
		return mView;
	}

	/**
	 * 给view视图设置OnClick监听
	 * @param view
	 * @param index
	 */
	private void setViewOnClickListener(View view, final int index) {
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (index) {
					case 1:
						mImg_userIcon.setImageResource(R.drawable.user_icon_1);
						break;

					case 2:
						mImg_userIcon.setImageResource(R.drawable.user_icon_2);
						break;

					case 3:
						mImg_userIcon.setImageResource(R.drawable.user_icon_3);
						break;

					case 4:
						mImg_userIcon.setImageResource(R.drawable.user_icon_4);
						break;

					case 5:
						mImg_userIcon.setImageResource(R.drawable.user_icon_5);
						break;

					case 6:
						mImg_userIcon.setImageResource(R.drawable.user_icon_6);
						break;

					case 7:
						mImg_userIcon.setImageResource(R.drawable.user_icon_7);
						break;

					case 8:
						mImg_userIcon.setImageResource(R.drawable.user_icon_8);
						break;

					case 9:
						mImg_userIcon.setImageResource(R.drawable.user_icon_9);
						break;
			
					default:
						break;
				}
				//赋值用户头像id
				iconId = index;
			}
		});
	}

	/**
	 * 确认密码
	 */
	private void userPsAgain() {
		mEdit_userPsAgain.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					String ps1 = mEdit_userPassword.getText().toString().trim();
					String ps2 = mEdit_userPsAgain.getText().toString().trim();
					mImg_psAgainResult.setVisibility(View.VISIBLE);
					if(ps1.equals(ps2)){
						mImg_psAgainResult.setImageResource(R.drawable.sign_right);
					}else{
						mImg_psAgainResult.setImageResource(R.drawable.sign_error);
					}
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sign_btn_back:
			//返回
			finish();
			break;

		case R.id.sign_btn_sign:
			String userName = mEdit_userAccount.getText().toString().trim();
			String password = mEdit_userPassword.getText().toString().trim();
			String psAgain = mEdit_userPsAgain.getText().toString().trim();
			String securityAnswer = mEdit_securityA.getText().toString().trim();
			//注册
			if (iconId != 0 && !TextUtils.isEmpty(userName)
					&& password.equals(psAgain) && !TextUtils.isEmpty(psAgain)
					&& securityId != 0 && !TextUtils.isEmpty(securityAnswer)) {
				User user = new User();
				user.setName(userName);
				user.setPassword(psAgain);
				user.setSecurityAnswer(securityAnswer);
				user.setIconId(iconId);
				user.setSecurityId(securityId);
				sendPostRuest(REGISTER, user);
			} else {
				showTextDialog(false, "请确认所有内容都填写完整！");
			}
			break;

		default:
			break;
		}
	}
	
	/**
	 * 发送HTTP请求
	 * @param operateType 操作类型
	 * @param user {@link User}
	 */
	private void sendPostRuest(final int operateType, final User user) {
		//服务器请求地址
		String requestUrl = AccountInfo.getInstance().getServerAddress();
		//拼装请求url
		switch (operateType) {
			case ALL_SECURITY:
				requestUrl += "/user/all_security";
				mLoadingDialog= new LoadingDialog.Builder(SignActivity.this).setText("正在加载……").create();
				break;
				
			case REGISTER:
				requestUrl += "/user/register";
				mLoadingDialog= new LoadingDialog.Builder(SignActivity.this).setText("正在注册……").create();
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
					case ALL_SECURITY:
						if (success) {
							//刷新列表
							securityList = JSONObject.parseArray(mJSONObject.getString("rows"), Security.class);
							parseData();
						} else {
							showAllSecurityErrDialog(mJSONObject.getString("message"));
						}
						break;

					case REGISTER:
						if (success) {
							showTextDialog(true, "注册成功！");
						} else {
							showTextDialog(false, mJSONObject.getString("message"));
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
					case ALL_SECURITY:
						showAllSecurityErrDialog("请求出错：\n" + arg0.toString());
						break;

					case REGISTER:
						showTextDialog(false, "请求出错：\n" + arg0.toString());
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
					case REGISTER:
						hashMap = new HashMap<String, String>();
						hashMap.put("name", user.getName());
						hashMap.put("iconId", JSONObject.toJSONString(user.getIconId()));
						hashMap.put("password", MD5Util.GetMD5Code(user.getPassword()));
						hashMap.put("securityAnswer", user.getSecurityAnswer());
						hashMap.put("securityId", JSONObject.toJSONString(user.getSecurityId()));
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
	 * 展示文本提示框
	 * @param isSignSuccess 是否为注册成功
	 * @param msg 提示信息
	 */
	private void showTextDialog(final boolean isSignSuccess, String msg) {
		//创建dialog
		final TextDialog.Builder mBuilder = new TextDialog.Builder(
				SignActivity.this).setTitle("提示").setContent(msg);
		TextDialog mDialog = mBuilder.setButton("确    定",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						mBuilder.closeDialog();
						if (isSignSuccess) {
							//跳转界面
							Intent mIntent = new Intent(SignActivity.this, LoginActivity.class);
							startActivity(mIntent);
							finish();
						}
					}
					
				}).create();
		mDialog.show();
	}
	
	/**
	 * 获取密保问题失败
	 * @param msg 提示信息
	 */
	private void showAllSecurityErrDialog(String msg) {
		//创建dialog
		final TextDialog.Builder mBuilder = new TextDialog.Builder(
				SignActivity.this).setTitle("提示").setContent(msg);
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
