package com.lee.namecardmanager.ui;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout.LayoutParams;
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
import com.lee.namecardmanager.model.Namecard;
import com.lee.namecardmanager.util.AccountInfo;
import com.lee.namecardmanager.widget.ButtonDialog;
import com.lee.namecardmanager.widget.LoadingDialog;
import com.lee.namecardmanager.widget.TextDialog;
import com.lee.namecardocr.config.NameToPinyin;

/**
 * 名片信息界面
 * @author lee
 *
 */
public class NamecardInfoActivity extends Activity implements OnClickListener{
	
	private ImageButton mImgBtn_back;		// 返回
	private Button mButton_delete;			// 删除
	private Button mButton_update;			// 更改
	private EditText mEditText_name;		// 姓名
	private EditText mEditText_jobTitle;	// 职称
	private EditText mEditText_companyName;	// 公司名称
	private EditText mEditText_companyAddress;// 公司地址
	private EditText mEditText_mobile;		// 手机
	private EditText mEditText_tel;			// 电话
	private EditText mEditText_fax;			// 传真
	private EditText mEditText_email;		// 邮件
	private EditText mEditText_web;			// 网页
	
	private boolean isUpdate = false;
	
	private Namecard namecardRes = null;

	/* 操作类型 */
	private static final int UPDATE_NAMECARD = 1;	//更改名片
	private static final int DELETE_NAMECARD = 2;	//删除名片
	/* 提示框 */
	private LoadingDialog mLoadingDialog;

	private static final String VOLLEY_POST = "Str_Post_Namecard_Info";//volley请求的标签
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_namecard_info);
		Intent mIntent = getIntent();
		namecardRes = (Namecard) mIntent.getSerializableExtra(NamecardQueryActivity.NAMECARD_INFO);
		initView();
		// 加载名片信息
		showNamecard(namecardRes);
		setUpdateState(isUpdate);
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
		mImgBtn_back = (ImageButton) findViewById(R.id.info_namecard_btn_back);
		mButton_delete = (Button) findViewById(R.id.info_namecard_delete);
		mButton_update = (Button) findViewById(R.id.info_namecard_update);
		mEditText_name = (EditText) findViewById(R.id.info_namecard_name);
		mEditText_jobTitle = (EditText) findViewById(R.id.info_namecard_jobtitle);
		mEditText_companyName = (EditText) findViewById(R.id.info_namecard_companyname);
		mEditText_companyAddress = (EditText) findViewById(R.id.info_namecard_companyaddress);
		mEditText_mobile = (EditText) findViewById(R.id.info_namecard_mobile);
		mEditText_tel = (EditText) findViewById(R.id.info_namecard_tel);
		mEditText_fax = (EditText) findViewById(R.id.info_namecard_fax);
		mEditText_email = (EditText) findViewById(R.id.info_namecard_email);
		mEditText_web = (EditText) findViewById(R.id.info_namecard_web);
		// listener
		mImgBtn_back.setOnClickListener(this);
		mButton_delete.setOnClickListener(this);
		mButton_update.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.info_namecard_btn_back:
				// 返回
				finish();
				break;

			case R.id.info_namecard_delete:
				// 删除
				if (isUpdate) {
					setUpdateState(false);
				} else {
					showNamecardDeleteDialog(namecardRes);
				}
				break;

			case R.id.info_namecard_update:
				// 更改
				if (isUpdate) {
					String name = mEditText_name.getText().toString().trim();
					if (!TextUtils.isEmpty(name)) {
						Namecard recordNamecard = new Namecard();
						recordNamecard.setId(namecardRes.getId());
						recordNamecard.setName(name);
						recordNamecard.setNamePinyin(NameToPinyin.convertNameToPinyin(name));
						recordNamecard.setJobTitle(mEditText_jobTitle.getText().toString().trim());
						recordNamecard.setCompanyName(mEditText_companyName.getText().toString().trim());
						recordNamecard.setCompanyAddress(mEditText_companyAddress.getText().toString().trim());
						recordNamecard.setMobile(mEditText_mobile.getText().toString().trim());
						recordNamecard.setTel(mEditText_tel.getText().toString().trim());
						recordNamecard.setFax(mEditText_fax.getText().toString().trim());
						recordNamecard.setEmail(mEditText_email.getText().toString().trim());
						recordNamecard.setWeb(mEditText_web.getText().toString().trim());
						showNamecardUpdateDialog(recordNamecard);
					} else {
						showTextDialog(false, "请确认姓名是否填写完整");
					}
				} else {
					setUpdateState(true);
				}
				break;
	
			default:
				break;
		}
	}
	
	/**
	 * 更改状态
	 * @param state
	 */
	@SuppressWarnings("deprecation")
	private void setUpdateState(boolean state) {
		if (state) {
			mButton_delete.setText("取    消");
			mButton_delete.setTextColor(getResources().getColor(R.drawable.button_text_color));
			mButton_delete.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_bg));
			mButton_update.setText("提    交");
		} else {
			mButton_delete.setText("删    除");
			mButton_delete.setTextColor(getResources().getColor(R.drawable.red_btn_text_color));
			mButton_delete.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_btn_bg));
			mButton_update.setText("更    改");
		}
		isUpdate = state;
		setEditTextEnabled(isUpdate);
	}
	
	/**
	 * 设置文本框是否可用
	 * @param isEnabled
	 */
	private void setEditTextEnabled(boolean isEnabled) {
		mEditText_name.setEnabled(isEnabled);
		mEditText_jobTitle.setEnabled(isEnabled);
		mEditText_companyName.setEnabled(isEnabled);
		mEditText_companyAddress.setEnabled(isEnabled);
		mEditText_mobile.setEnabled(isEnabled);
		mEditText_tel.setEnabled(isEnabled);
		mEditText_fax.setEnabled(isEnabled);
		mEditText_email.setEnabled(isEnabled);
		mEditText_web.setEnabled(isEnabled);
	}

	/**
	 * 展示名片信息
	 * @param namecard
	 */
	private void showNamecard(Namecard namecard) {
		mEditText_name.setText(namecard == null ? "" : namecard.getName());
		mEditText_jobTitle.setText(namecard == null ? "" : namecard.getJobTitle());
		mEditText_companyName.setText(namecard == null ? "" : namecard.getCompanyName());
		mEditText_companyAddress.setText(namecard == null ? "" : namecard.getCompanyAddress());
		mEditText_mobile.setText(namecard == null ? "" : namecard.getMobile());
		mEditText_tel.setText(namecard == null ? "" : namecard.getTel());
		mEditText_fax.setText(namecard == null ? "" : namecard.getFax());
		mEditText_email.setText(namecard == null ? "" : namecard.getEmail());
		mEditText_web.setText(namecard == null ? "" : namecard.getWeb());
	}

	/**
	 * 展示文本提示框
	 * @param msg 提示信息
	 */
	private void showTextDialog(final boolean isFinish, String msg) {
		//创建dialog
		final TextDialog.Builder mBuilder = new TextDialog.Builder(
				NamecardInfoActivity.this).setTitle("提示").setContent(msg);
		TextDialog mDialog = mBuilder.setButton("确    定",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						mBuilder.closeDialog();
						if (isFinish) {
							NamecardInfoActivity.this.finish();
						}
					}
					
				}).create();
		mDialog.show();
	}

	/**
	 * 展示名片更改提示框
	 * @param namecard {@link Namecard}
	 */
	private void showNamecardUpdateDialog(final Namecard namecard) {
		//内容布局
		TextView mTextView_custom = new TextView(NamecardInfoActivity.this);
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mTextView_custom.setLayoutParams(lp);
		mTextView_custom.setTextSize(16f);
		mTextView_custom.setTextColor(getResources().getColor(R.color.light_grey_cc));
		//拼装显示的名片信息
		StringBuffer buffer = new StringBuffer();
		buffer.append("姓名：" + namecard.getName());
		buffer.append("\n姓名全拼：" + namecard.getNamePinyin());
		buffer.append("\n头衔：" + namecard.getJobTitle());
		buffer.append("\n公司名称：" + namecard.getCompanyName());
		buffer.append("\n公司地址：" + namecard.getCompanyAddress());
		buffer.append("\n手机：" + namecard.getMobile());
		buffer.append("\n电话：" + namecard.getTel());
		buffer.append("\n传真：" + namecard.getFax());
		buffer.append("\n邮件：" + namecard.getEmail());
		buffer.append("\n网址：" + namecard.getWeb());
		mTextView_custom.setText(buffer);
        //创建dialog
		final ButtonDialog.Builder mBuilder = new ButtonDialog.Builder(
				NamecardInfoActivity.this).setTitle("名片信息确认").setView(mTextView_custom);
		ButtonDialog mDialog = mBuilder.setLeftButton("取    消",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						mBuilder.closeDialog();
					}
				}).setRightButton("确 认 更 改", new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// 更改状态
						setUpdateState(false);
						mBuilder.closeDialog();
						//发起请求
						sendPostRuest(UPDATE_NAMECARD, namecard);
					}
				}).create();
		mDialog.show();
	}

	/**
	 * 展示名片删除提示框
	 * @param namecard {@link Namecard}
	 */
	@SuppressLint("InflateParams")
	private void showNamecardDeleteDialog(final Namecard namecard) {
		//内容布局
		//内容布局
		View mView = getLayoutInflater().inflate(R.layout.namecard_info_delete, null);
		TextView mTextView_delete = (TextView) mView.findViewById(R.id.namecard_info_delete);
		mTextView_delete.setText(namecard.getName());
        //创建dialog
		final ButtonDialog.Builder mBuilder = new ButtonDialog.Builder(
				NamecardInfoActivity.this).setTitle("提示").setView(mView);
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
						sendPostRuest(DELETE_NAMECARD, namecard);
					}
				}).create();
		mDialog.show();
	}
	

	/**
	 * 发送HTTP请求
	 * @param operateType 操作类型
	 * @param namecard 名片信息
	 */
	private void sendPostRuest(final int operateType, final Namecard namecard) {
		//服务器请求地址
		String requestUrl = AccountInfo.getInstance().getServerAddress();
		//拼装请求url
		switch (operateType) {
			case UPDATE_NAMECARD:
				requestUrl += "/namecard/update_namecard";
				mLoadingDialog= new LoadingDialog.Builder(NamecardInfoActivity.this).setText("正在更新……").create();
				break;
				
			case DELETE_NAMECARD:
				requestUrl += "/namecard/delete_namecard";
				mLoadingDialog= new LoadingDialog.Builder(NamecardInfoActivity.this).setText("正在删除……").create();
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
					case UPDATE_NAMECARD:
						if (success) {
							//刷新列表
							showTextDialog(true, "名片更改成功！");
						} else {
							showTextDialog(false, mJSONObject.getString("message"));
						}
						break;

					case DELETE_NAMECARD:
						if (success) {
							//刷新列表
							showTextDialog(true, "删除名片成功！");
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
				showTextDialog(false, "请求出错：\n" + arg0.toString());
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> hashMap = new HashMap<String, String>();
				hashMap.put("userName", AccountInfo.getInstance().getUserName());
				hashMap.put("userPs", AccountInfo.getInstance().getUserPs());
				switch (operateType) {
					case UPDATE_NAMECARD:
						hashMap.put("id", JSONObject.toJSONString(namecard.getId()));
						hashMap.put("name", namecard.getName());
						hashMap.put("namePinyin", namecard.getNamePinyin());
						hashMap.put("jobTitle", namecard.getJobTitle());
						hashMap.put("companyName", namecard.getCompanyName());
						hashMap.put("companyAddress", namecard.getCompanyAddress());
						hashMap.put("mobile", namecard.getMobile());
						hashMap.put("tel", namecard.getTel());
						hashMap.put("fax", namecard.getFax());
						hashMap.put("email", namecard.getEmail());
						hashMap.put("web", namecard.getWeb());
						break;

					case DELETE_NAMECARD:
						hashMap.put("id", JSONObject.toJSONString(namecard.getId()));
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

}
