package com.lee.accountsecretary.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.lee.accountsecretary.model.Type;
import com.lee.accountsecretary.util.AccountInfo;
import com.lee.accountsecretary.widget.ButtonDialog;
import com.lee.accountsecretary.widget.LoadingDialog;
import com.lee.accountsecretary.widget.TextDialog;

/**
 * 个人信息
 * @author lee
 *
 */
public class SelfInfoActivity extends Activity implements OnClickListener{
	
	private ImageButton mImgBtn_back;		//返回
	private ImageView mImg_userIcon;		//头像
	private TextView mTextView_userAccount;	//账号
	private Spinner mSpinner_type;			//类别
	private Button mButton_typeDelete;		//删除类别
	private Button mButton_typeAdd;			//新增类别
	private TextView mTextView_monthSurplus;//月结余
	private TextView mTextView_yearSurplus;	//年结余
	private Button mButton_logout;			//注销

	List<Type> typeList = new ArrayList<Type>();//账目类别列表
	private int typeId = 0;					//被选择的类别id
	private String typeName = "";			//被选择的类别名称
	private boolean isSystemType = false;	//是否为系统类别

	/* 操作类型 */
	private static final int SELF_INFO = 1;			//用户信息
	private static final int DELETE_TYPE = 2;		//删除类别
	private static final int ADD_TYPE = 3;			//新增类别
	
	private static final String VOLLEY_POST = "Str_Post_SelfInfo";//volley请求的标签

	/* 提示框 */
	private LoadingDialog mLoadingDialog;//加载提示
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selfinfo);
		
		initView();
		initSpinner();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//发送请求
		sendPostRuest(SELF_INFO, "");
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
		mImgBtn_back = (ImageButton) findViewById(R.id.selfinfo_btn_back);
		mImg_userIcon = (ImageView) findViewById(R.id.selfinfo_user_icon);
		mTextView_userAccount = (TextView) findViewById(R.id.selfinfo_user_account);
		mSpinner_type = (Spinner) findViewById(R.id.selfinfo_type_manage);
		mButton_typeDelete = (Button) findViewById(R.id.selfinfo_type_delete);
		mButton_typeAdd = (Button) findViewById(R.id.selfinfo_type_add);
		mTextView_monthSurplus = (TextView) findViewById(R.id.selfinfo_month_surplus);
		mTextView_yearSurplus = (TextView) findViewById(R.id.selfinfo_year_surplus);
		mButton_logout = (Button) findViewById(R.id.selfinfo_logout);
		//listener
		mImgBtn_back.setOnClickListener(this);
		mButton_typeDelete.setOnClickListener(this);
		mButton_typeAdd.setOnClickListener(this);
		mButton_logout.setOnClickListener(this);
		
		getUserData();
	}

	/**
	 * 获取用户数据
	 */
	private void getUserData() {
		int userIconId = AccountInfo.getInstance().getUserIcon();
		String userName = AccountInfo.getInstance().getUserName();
		mTextView_userAccount.setText(userName);
		switch (userIconId) {
			case 0:
				mImg_userIcon.setImageResource(R.drawable.default_user_icon);
				break;
				
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
	}

	/**
	 * 初始化下拉框
	 */
	private void initSpinner() {
		//设置监听
		mSpinner_type.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				mButton_typeDelete.setEnabled(true);
				//赋值类别id
				Type mType = typeList.get(position);
				typeId = mType.getId();
				typeName = mType.getName();
				if (mType.getUserId() == 0) {
					isSystemType = true;
				} else {
					isSystemType = false;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				mButton_typeDelete.setEnabled(false);
			}
		});
	}
	
	/**
	 * 解析数据
	 */
	private void parseData() {
		//将数据重置
		typeId = 0;
		typeName = "";
		isSystemType = false;
		List<String> data = new ArrayList<String>();
		for (int i = 0; i < typeList.size(); i++) {
			data.add(typeList.get(i).getName());
		}
		refershTypeList(data);
	}
	
	/**
	 * 刷新类别列表
	 * @param data {@link List<String>}
	 */
	private void refershTypeList(final List<String> data){
		//设置下拉框的布局样式
		ViewGroup.LayoutParams vlp = mSpinner_type.getLayoutParams();
		int width = vlp.width;
		final AbsListView.LayoutParams lp = new AbsListView.LayoutParams(width, 50);
		//设置适配器
		ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.sign_spinner_checked_text, data){
			@Override
			public View getDropDownView(int position, View convertView,
					ViewGroup parent) {
				//获取
				View view = View.inflate(SelfInfoActivity.this, R.layout.sign_spinner_layout, null);
				view.setLayoutParams(lp);
				//设置对应的数据
				TextView mTextView_text = (TextView) view.findViewById(R.id.spinner_text);
				mTextView_text.setText(data.get(position));
				//选中背景颜色
				if (mSpinner_type.getSelectedItemPosition() == position) {
					view.setBackgroundColor(getResources().getColor(R.color.light_grey_b1));
				} else {
					view.setBackgroundColor(getResources().getColor(R.color.white));
				}
				return view;
			}
		};
		mAdapter.setDropDownViewResource(R.layout.sign_spinner_layout);
		mSpinner_type.setAdapter(mAdapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.selfinfo_btn_back:
				Intent mIntent = new Intent(SelfInfoActivity.this, MainActivity.class);
				startActivity(mIntent);
				finish();
				break;

			case R.id.selfinfo_type_delete:
				//删除某类别
				if (isSystemType) {
					showSystemTypeDialog();
				} else {
					showDeleteTypeDialog();
				}
				break;

			case R.id.selfinfo_type_add:
				//新增类别
				showAddTypeDialog();
				break;

			case R.id.selfinfo_logout:
				//注销
				showLogoutDialog();
				break;
	
			default:
				break;
		}
	}

	/**
	 * 删除类别
	 */
	@SuppressLint("InflateParams")
	private void showDeleteTypeDialog() {
		//内容布局
		View mView = getLayoutInflater().inflate(R.layout.content_record_type_delete, null);
		TextView mTextView_delete = (TextView) mView.findViewById(R.id.content_record_type_delete);
		mTextView_delete.setText(typeName);
        //创建dialog
		final ButtonDialog.Builder mBuilder = new ButtonDialog.Builder(
				SelfInfoActivity.this).setTitle("删除类别").setView(mView);
		ButtonDialog mDialog = mBuilder.setLeftButton("取    消",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						mBuilder.closeDialog();
					}
				}).setRightButton("确 认 删 除", new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						mBuilder.closeDialog();
						//发起请求
						sendPostRuest(DELETE_TYPE, "");
					}
				}).create();
		mDialog.show();
	}

	/**
	 * 新增类别提示
	 */
	@SuppressLint("InflateParams")
	private void showAddTypeDialog() {
		//内容布局
		View mView = getLayoutInflater().inflate(R.layout.content_record_type_add, null);
		final EditText mEditText_typeAdd = (EditText) mView.findViewById(R.id.content_record_type_add);
        //创建dialog
		final ButtonDialog.Builder mBuilder = new ButtonDialog.Builder(
				SelfInfoActivity.this).setTitle("新增类别").setView(mView);
		ButtonDialog mDialog = mBuilder.setLeftButton("取    消",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						mBuilder.closeDialog();
					}
				}).setRightButton("确 认 新 增", new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//获取输入框中的值
						String addType = mEditText_typeAdd.getText().toString().trim();
						mBuilder.closeDialog();
						//发送请求
						if (TextUtils.isEmpty(addType)) {
							showTextDialog("类别名称不能为空！");
						} else {
							sendPostRuest(ADD_TYPE, addType);
						}
					}
				}).create();
		mDialog.show();
	}
	
	/**
	 * 系统默认类别提示
	 */
	private void showSystemTypeDialog(){
		//创建dialog
		final TextDialog.Builder mBuilder = new TextDialog.Builder(
				SelfInfoActivity.this).setTitle("提示").setContent(
				"该类别为【系统默认类别】，不能被删除");
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
	 * 注销提示
	 */
	@SuppressLint("InflateParams")
	private void showLogoutDialog() {
		//内容布局
		View mView = getLayoutInflater().inflate(R.layout.content_user_logout, null);
		TextView mTextView_user = (TextView) mView.findViewById(R.id.content_user_logout);
		mTextView_user.setText(AccountInfo.getInstance().getUserName());
        //创建dialog
		final ButtonDialog.Builder mBuilder = new ButtonDialog.Builder(
				SelfInfoActivity.this).setTitle("提示").setView(mView);
		ButtonDialog mDialog = mBuilder.setLeftButton("取    消",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						mBuilder.closeDialog();
					}
				}).setRightButton("注    销", new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//注销时更改状态值
						mBuilder.closeDialog();
						ASApplication.getInstance().setLogin(false);
						//先删除密码，然后进入登陆页面
						AccountInfo mAccountInfo = AccountInfo.getInstance();
						mAccountInfo.setUserPs("");
						mAccountInfo.commit();
						Intent mIntent = new Intent(SelfInfoActivity.this, LoginActivity.class);
						startActivity(mIntent);
						finish();
					}
				}).create();
		mDialog.show();
	}

	/**
	 * 发送HTTP请求
	 * @param operateType 操作类型
	 * @param addTypeName 新增类别名称
	 */
	private void sendPostRuest(final int operateType, final String addTypeName) {
		//服务器请求地址
		String requestUrl = AccountInfo.getInstance().getServerAddress();
		//拼装请求url
		switch (operateType) {
			case SELF_INFO:
				requestUrl += "/user/self_info";
				mLoadingDialog= new LoadingDialog.Builder(SelfInfoActivity.this).setText("正在加载……").create();
				break;
				
			case DELETE_TYPE:
				requestUrl += "/user/delete_user_type";
				mLoadingDialog= new LoadingDialog.Builder(SelfInfoActivity.this).setText("正在删除……").create();
				break;
				
			case ADD_TYPE:
				requestUrl += "/user/add_user_type";
				mLoadingDialog= new LoadingDialog.Builder(SelfInfoActivity.this).setText("正在新增……").create();
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
					case SELF_INFO:
						if (success) {
							//刷新列表
							mTextView_monthSurplus.setText(mJSONObject.getString("monthSurplus"));
							mTextView_yearSurplus.setText(mJSONObject.getString("yearSurplus"));
							typeList = JSONObject.parseArray(mJSONObject.getString("rows"), Type.class);
							parseData();
						} else {
							showAllTypeErrDialog(mJSONObject.getString("message"));
						}
						break;

					case DELETE_TYPE:
						if (success) {
							//刷新列表
							typeList = JSONObject.parseArray(mJSONObject.getString("rows"), Type.class);
							parseData();
							showTextDialog("删除类别成功！");
						} else {
							showTextDialog(mJSONObject.getString("message"));
						}
						break;

					case ADD_TYPE:
						if (success) {
							//刷新列表
							typeList = JSONObject.parseArray(mJSONObject.getString("rows"), Type.class);
							parseData();
							showTextDialog("新增类别成功！");
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
				if (operateType == SELF_INFO) {
					showAllTypeErrDialog("请求出错：\n" + arg0.toString());
				} else {
					showTextDialog("请求出错：\n" + arg0.toString());
				}
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> hashMap = null;
				switch (operateType) {
					case SELF_INFO:
						hashMap = new HashMap<String, String>();
						hashMap.put("userName", AccountInfo.getInstance().getUserName());
						break;

					case DELETE_TYPE:
						hashMap = new HashMap<String, String>();
						hashMap.put("userName", AccountInfo.getInstance().getUserName());
						hashMap.put("typeId", JSONObject.toJSONString(typeId));
						break;

					case ADD_TYPE:
						hashMap = new HashMap<String, String>();
						hashMap.put("userName", AccountInfo.getInstance().getUserName());
						hashMap.put("typeName", addTypeName);
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
	 * @param msg 提示信息
	 */
	private void showTextDialog(String msg) {
		//创建dialog
		final TextDialog.Builder mBuilder = new TextDialog.Builder(
				SelfInfoActivity.this).setTitle("提示").setContent(msg);
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
	 * 获取账目类别失败
	 * @param msg 提示信息
	 */
	private void showAllTypeErrDialog(String msg) {
		//创建dialog
		final TextDialog.Builder mBuilder = new TextDialog.Builder(
				SelfInfoActivity.this).setTitle("提示").setContent(msg);
		TextDialog mDialog = mBuilder.setButton("确    定",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						//加载出错更改状态值
						ASApplication.getInstance().setLogin(false);
						mBuilder.closeDialog();
						finish();
					}
					
				}).create();
		mDialog.show();
	}
}
