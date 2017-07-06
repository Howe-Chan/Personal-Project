package com.lee.namecardmanager.ui;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hanvon.HWCloudManager;
import com.hanvon.utils.ConnectionDetector;
import com.lee.namecardmanager.NMApplication;
import com.lee.namecardmanager.R;
import com.lee.namecardmanager.model.Namecard;
import com.lee.namecardmanager.util.AccountInfo;
import com.lee.namecardmanager.widget.ButtonDialog;
import com.lee.namecardmanager.widget.LoadingDialog;
import com.lee.namecardmanager.widget.TextDialog;
import com.lee.namecardocr.CameraActivity;
import com.lee.namecardocr.config.NameToPinyin;

/**
 * 名片录入界面
 * @author lee
 *
 */
public class NamecardRecordActivity extends Activity implements OnClickListener{
	
	private ImageButton mImgBtn_back;		// 返回
	private ImageButton mImgBtn_camera;		// 拍照识别
	private ImageButton mImgBtn_cropImg;	// 截图识别
	private Button mButton_ok;				// 确认录入
	private EditText mEditText_name;		// 姓名
	private EditText mEditText_jobTitle;	// 职称
	private EditText mEditText_companyName;	// 公司名称
	private EditText mEditText_companyAddress;// 公司地址
	private EditText mEditText_mobile;		// 手机
	private EditText mEditText_tel;			// 电话
	private EditText mEditText_fax;			// 传真
	private EditText mEditText_email;		// 邮件
	private EditText mEditText_web;			// 网页
	
	public static final String IMG_PATH = "img_path";
	public static final int CAMERA_REQUEST_CODE = 1;// 相机 
	public static final int CROPIMG_SYSTEM_REQUEST_CODE_1 = 2;// 系统裁剪步骤1
	public static final int CROPIMG_SYSTEM_REQUEST_CODE_2 = 3;// 系统裁剪步骤2
	
	private static final String VOLLEY_POST = "Str_Post_Namecard_Record";//volley请求的标签

	private String imgPath = "";
	private String savePath = "/sdcard/";
	private String saveFileName = "temp.jpg";
	
	/* 提示框 */
	private LoadingDialog mLoadingDialog;//加载提示

	private HWCloudManager mHWCloudManager;// 名片
	private String hanvon_ocr_key = "5979f164-c1d3-41b6-89c5-e979f18cd957";
	private static final String NAMECARD_DISCERN = "namecard_discern";
	// 解析线程
	private Runnable discernRunnable = new Runnable() {
		
		@Override
		public void run() {
			/**
			 * 调用汉王云名片识别方法
			 */
			String result = mHWCloudManager.cardLanguage("auto", imgPath);
			// 调用UI线程
			Bundle mBundle = new Bundle();
			mBundle.putString(NAMECARD_DISCERN, result);
			Message msg = new Message();
			msg.setData(mBundle);
			discernHandler.sendMessage(msg);
		}
	};
	// UI线程
	@SuppressLint("HandlerLeak")
	private Handler discernHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			mLoadingDialog.dismiss();
			Bundle bundle = msg.getData();
			String result = bundle.getString(NAMECARD_DISCERN);
			parseData(result);
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_namecard_record);
		/**
		 * hanvon_ocr_key 是您在开发者中心申请的android_key 并 申请了云名片识别服务
		 * 开发者中心：http://developer.hanvon.com/
		 */
		mHWCloudManager = new HWCloudManager(this, hanvon_ocr_key);
		// 获取存放路径
		savePath = getExternalFilesDir("/namecard/").getAbsolutePath();
		
		initView();
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
		mImgBtn_back = (ImageButton) findViewById(R.id.record_btn_back);
		mImgBtn_camera = (ImageButton) findViewById(R.id.record_camera);
		mImgBtn_cropImg = (ImageButton) findViewById(R.id.record_crop);
		mButton_ok = (Button) findViewById(R.id.record_ok);
		mEditText_name = (EditText) findViewById(R.id.record_namecard_name);
		mEditText_jobTitle = (EditText) findViewById(R.id.record_namecard_jobtitle);
		mEditText_companyName = (EditText) findViewById(R.id.record_namecard_companyname);
		mEditText_companyAddress = (EditText) findViewById(R.id.record_namecard_companyaddress);
		mEditText_mobile = (EditText) findViewById(R.id.record_namecard_mobile);
		mEditText_tel = (EditText) findViewById(R.id.record_namecard_tel);
		mEditText_fax = (EditText) findViewById(R.id.record_namecard_fax);
		mEditText_email = (EditText) findViewById(R.id.record_namecard_email);
		mEditText_web = (EditText) findViewById(R.id.record_namecard_web);
		// listener
		mImgBtn_back.setOnClickListener(this);
		mImgBtn_camera.setOnClickListener(this);
		mImgBtn_cropImg.setOnClickListener(this);
		mButton_ok.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.record_btn_back:
				// 返回
				finish();
				break;

			case R.id.record_camera:
				// 拍照识别
				Intent mCameraIntent = new Intent(NamecardRecordActivity.this, CameraActivity.class);
				startActivityForResult(mCameraIntent, CAMERA_REQUEST_CODE);
				break;

			case R.id.record_crop:
				// 截图识别
				Intent mSystemCropIntent = new Intent(Intent.ACTION_PICK);
				mSystemCropIntent.setType("image/*");
				startActivityForResult(mSystemCropIntent, CROPIMG_SYSTEM_REQUEST_CODE_1);
				break;

			case R.id.record_ok:
				// 录入
				String name = mEditText_name.getText().toString().trim();
				if (!TextUtils.isEmpty(name)) {
					Namecard recordNamecard = new Namecard();
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
					showNamecardRecordDialog(recordNamecard);
				} else {
					showTextDialog("请确认姓名是否填写完整");
				}
				break;
	
			default:
				break;
		}
	}

	/**
	 * 名片识别
	 */
	private void namecardOCR() {
		showNamecard(null);
		ConnectionDetector mConnectionDetector = new ConnectionDetector(NamecardRecordActivity.this);
		if (mConnectionDetector.isConnectingTOInternet()) {
			if (!"".equals(imgPath)) {
				// 创建Dialog
				mLoadingDialog = new LoadingDialog.Builder(NamecardRecordActivity.this).setText("正在识别请稍后......").create();
				mLoadingDialog.show();
				new Thread(discernRunnable).start();
			} else {
				Toast.makeText(NamecardRecordActivity.this, "请选择名片后再试", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(NamecardRecordActivity.this, "网络连接失败，请检查网络后重试！", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 解析数据
	 * @param result 数据源
	 */
	private void parseData(String result) {
		Log.i("lee", result);
		//数据解析
		JSONObject mJSONObject = JSONObject.parseObject(result);
		int code = JSONObject.parseObject(mJSONObject.getString("code"), Integer.class);
		if (code == 0) {
			// 识别成功
			String name = getSingleParameter(mJSONObject, "name");
			String jobTitle = getSingleParameter(mJSONObject, "title");
			String tel = getSingleParameter(mJSONObject, "tel");
			String mobile = getSingleParameter(mJSONObject, "mobile");
			String fax = getSingleParameter(mJSONObject, "fax");
			String email = getSingleParameter(mJSONObject, "email");
			String companyName = getSingleParameter(mJSONObject, "comp");
			String companyAddress = getSingleParameter(mJSONObject, "addr");
			String web = getSingleParameter(mJSONObject, "web");
			// 拼装数据
			Namecard namecard = new Namecard();
			namecard.setName(name);
			namecard.setJobTitle(jobTitle);
			namecard.setCompanyName(companyName);
			namecard.setCompanyAddress(companyAddress);
			namecard.setMobile(mobile);
			namecard.setTel(tel);
			namecard.setFax(fax);
			namecard.setEmail(email);
			namecard.setWeb(web);
			showNamecard(namecard);
		} else {
			// 识别失败
			showTextDialog("名片识别失败：\n" + result);
		}
	}
	
	/**
	 * 获取json中的某个字段值
	 * @param jsonObject {@link JSONObject}
	 * @param name 字段名称
	 * @return
	 */
	private String getSingleParameter(JSONObject jsonObject, String name) {
		String temp = "";
		List<String> parameter = JSONObject.parseArray(jsonObject.getString(name), String.class);
		if (parameter.size() > 0) {
			temp = parameter.get(0);
		}
		return temp;
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
	 * 展示名片录入提示框
	 * @param namecard {@link Namecard}
	 */
	private void showNamecardRecordDialog(final Namecard namecard) {
		//内容布局
		TextView mTextView_custom = new TextView(NamecardRecordActivity.this);
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mTextView_custom.setLayoutParams(lp);
//		mTextView_custom.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));
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
				NamecardRecordActivity.this).setTitle("名片信息确认").setView(mTextView_custom);
		ButtonDialog mDialog = mBuilder.setLeftButton("取    消",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						mBuilder.closeDialog();
					}
				}).setRightButton("确 认 录 入", new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						mBuilder.closeDialog();
						//发起请求
						sendPostRequest(namecard);
					}
				}).create();
		mDialog.show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				imgPath = data.getStringExtra(IMG_PATH);
				namecardOCR();
			} else {
				imgPath = "";
			}
		} else if (requestCode == CROPIMG_SYSTEM_REQUEST_CODE_1) {
			if (resultCode == RESULT_OK) {
				// 创建存放文件的uri
				File saveFile = new File(savePath, saveFileName);
				Uri saveUri = Uri.fromFile(saveFile);
				// 跳转至裁剪工具
				Intent mCropIntent = new Intent("com.android.camera.action.CROP");
				mCropIntent.setDataAndType(data.getData(), "image/*");
				mCropIntent.putExtra("return-data", "false");
				mCropIntent.putExtra(MediaStore.EXTRA_OUTPUT, saveUri);
				startActivityForResult(mCropIntent, CROPIMG_SYSTEM_REQUEST_CODE_2);
			} else {
				imgPath = "";
			}
		} else if (requestCode == CROPIMG_SYSTEM_REQUEST_CODE_2) {
			if (resultCode == RESULT_OK) {
				imgPath = savePath + "/" + saveFileName;
				namecardOCR();
			} else {
				imgPath = "";
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 展示文本提示框
	 * @param msg 提示信息
	 */
	private void showTextDialog(String msg) {
		//创建dialog
		final TextDialog.Builder mBuilder = new TextDialog.Builder(
				NamecardRecordActivity.this).setTitle("提示").setContent(msg);
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
	 * 发送HTTP请求
	 * @param userPs 需更改的密码
	 */
	private void sendPostRequest(final Namecard namecard) {
		mLoadingDialog = new LoadingDialog.Builder(NamecardRecordActivity.this).setText("正在录入名片.....").create();
		mLoadingDialog.show();
		//服务器请求地址
		String requestUrl = AccountInfo.getInstance().getServerAddress() + "/namecard/add_namecard";
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
					showTextDialog("名片录入成功！");
				} else {
					showTextDialog(mJSONObject.getString("message"));
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				mLoadingDialog.dismiss();
				//请求失败,提示信息
				showTextDialog("请求出错：\n" + arg0.toString());
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> hashMap = new HashMap<String, String>();
				hashMap.put("userName", AccountInfo.getInstance().getUserName());
				hashMap.put("userPs", AccountInfo.getInstance().getUserPs());
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
				return hashMap;
			}
		};
		//给请求对象添加TAG标签
		request.setTag(VOLLEY_POST);
		//将请求对象添加到请求队列中
		NMApplication.getRequestQueue().add(request);
	}

}
