package com.lee.accountsecretary.ui;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import android.widget.DatePicker;
import android.widget.ImageButton;
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
import com.lee.accountsecretary.model.Type;
import com.lee.accountsecretary.util.AccountInfo;
import com.lee.accountsecretary.widget.ButtonDialog;
import com.lee.accountsecretary.widget.LoadingDialog;
import com.lee.accountsecretary.widget.TextDialog;

/**
 * 账单查询
 * @author lee
 *
 */
public class RecordQueryActivity extends Activity implements OnClickListener{
	
	private ImageButton mImgBtn_back;	//返回
	private Spinner mSpinner_condition;	//查询条件
	private LinearLayout mLayout_year;	//年
	private TextView mTextView_year;
	private LinearLayout mLayout_month;	//月
	private TextView mTextView_month;
	private Spinner mSpinner_type;		//账目类别
	private Button mButton_query;		//查询
	
	private boolean isYear = false;		//是否按年查询
	private boolean isAllType = false;	//是否所有类别
	private String date = "";			//日期
	
	List<Type> typeList = new ArrayList<Type>();//账目类别列表
	private int typeId = 0;					//被选择的类别id
	
	public static final String CONDITION_QUERTYPE = "queryType";
	public static final String CONDITION_DATE = "date";
	public static final String CONDITION_TYPE = "type";

	private static final String VOLLEY_POST = "Str_Post_RecordQuery";//volley请求的标签

	/* 提示框 */
	private LoadingDialog mLoadingDialog;//加载提示

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_query);
		//创建提示框
		mLoadingDialog = new LoadingDialog.Builder(RecordQueryActivity.this).setText("正在加载……").create();
		
		initView();
		initConditionSpinner();
		initTypeSpinner();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//发送请求
		sendPostRuest();
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
		mImgBtn_back = (ImageButton) findViewById(R.id.recordquery_btn_back);
		mSpinner_condition = (Spinner) findViewById(R.id.recordquery_condition);
		mLayout_year = (LinearLayout) findViewById(R.id.recordquery_year_layout);
		mTextView_year = (TextView) findViewById(R.id.recordquery_year_input);
		mLayout_month = (LinearLayout) findViewById(R.id.recordquery_month_layout);
		mTextView_month = (TextView) findViewById(R.id.recordquery_month_input);
		mSpinner_type = (Spinner) findViewById(R.id.recordquery_type);
		mButton_query = (Button) findViewById(R.id.recordquery_btn_query);
		//listener
		mImgBtn_back.setOnClickListener(this);
		mLayout_year.setOnClickListener(this);
		mLayout_month.setOnClickListener(this);
		mButton_query.setOnClickListener(this);
	}
	
	/**
	 * 初始化查询条件下拉框
	 */
	private void initConditionSpinner() {
		//获取数据
		final List<String> data = new ArrayList<String>();
		data.add("按年查询");
		data.add("按月查询");
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
				View view = View.inflate(RecordQueryActivity.this, R.layout.sign_spinner_layout, null);
				view.setLayoutParams(lp);
				//设置对应的数据
				TextView mTextView_text = (TextView) view.findViewById(R.id.spinner_text);
				mTextView_text.setText(data.get(position));
				//选中背景颜色
				if (mSpinner_condition.getSelectedItemPosition() == position) {
					view.setBackgroundColor(getResources().getColor(R.color.light_grey_b1));
				} else {
					view.setBackgroundColor(getResources().getColor(R.color.white));
				}
				return view;
			}
		};
		mAdapter.setDropDownViewResource(R.layout.sign_spinner_layout);
		mSpinner_condition.setAdapter(mAdapter);
		//设置监听
		mSpinner_condition.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				mTextView_year.setText("- - - -");
				mTextView_month.setText("- -");
				date = "";
				if (position == 0) {
					isYear = true;
				} else if (position == 1) {
					isYear = false;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		
	}

	/**
	 * 初始化类别下拉框
	 */
	private void initTypeSpinner() {
		//设置监听
		mSpinner_type.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					isAllType = true;
				} else {
					isAllType = false;
					typeId = typeList.get(position - 1).getId();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				typeId = 0;
			}
		});
	}

	/**
	 * 解析数据
	 */
	private void parseData() {
		//重置数据
		typeId = 0;
		List<String> data = new ArrayList<String>();
		data.add("所有");
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
				View view = View.inflate(RecordQueryActivity.this, R.layout.sign_spinner_layout, null);
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
			case R.id.recordquery_btn_back:
				finish();
				break;

			case R.id.recordquery_year_layout:
				if (isYear) {
					showDateDialog(isYear);
				}
				break;

			case R.id.recordquery_month_layout:
				if (!isYear) {
					showDateDialog(isYear);
				}
				break;

			case R.id.recordquery_btn_query:
				if (TextUtils.isEmpty(date)) {
					showTextDialog("未选择日期！");
				} else {
					//跳转至账目明细界面
					Intent mIntent = new Intent(RecordQueryActivity.this, RecordDetailActivity.class);
					if (isYear) {
						if (isAllType) {
							mIntent.putExtra(CONDITION_QUERTYPE, RecordDetailActivity.QUERY_YEAR_ALL);
						} else {
							mIntent.putExtra(CONDITION_QUERTYPE, RecordDetailActivity.QUERY_YEAR_SINGLE);
						}
					} else {
						if (isAllType) {
							mIntent.putExtra(CONDITION_QUERTYPE, RecordDetailActivity.QUERY_MONTH_ALL);
						} else {
							mIntent.putExtra(CONDITION_QUERTYPE, RecordDetailActivity.QUERY_MONTH_SINGLE);
						}
					}
					mIntent.putExtra(CONDITION_DATE, date);
					mIntent.putExtra(CONDITION_TYPE, typeId);
					startActivity(mIntent);
				}
				break;
	
			default:
				break;
		}
	}

	/**
	 * 展示日期选择器
	 * @param isYear 是否只选择年
	 */
	@SuppressLint({ "SimpleDateFormat", "InflateParams" })
	private void showDateDialog(final boolean isYear) {
		//内容布局
		View mView = getLayoutInflater().inflate(R.layout.content_date, null);
		final DatePicker mPicker_date = (DatePicker) mView.findViewById(R.id.content_date_datepicker);
        //加载日期控件
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        mPicker_date.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
        mPicker_date.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);//阻止控件获得焦点
        //限制起始日期
        try {
    		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			mPicker_date.setMinDate(format.parse("2013-08-13").getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
        mPicker_date.setMaxDate(System.currentTimeMillis());
        //隐藏日期
        hideDay(mPicker_date, isYear);
        
        //创建dialog
        String title = "选择月份";
        if (isYear) {
        	title = "选择年份";
		}
		final ButtonDialog.Builder mBuilder = new ButtonDialog.Builder(
				RecordQueryActivity.this).setTitle(title).setView(mView);
		ButtonDialog mDialog = mBuilder.setLeftButton("确    定",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
                    	//填充日期
						mTextView_year.setText("" + mPicker_date.getYear());
						if (isYear) {
							mTextView_month.setText("- -");
							date = "" + mPicker_date.getYear();
						} else {
							mTextView_month.setText(String.format("%02d",mPicker_date.getMonth() + 1));
							date = String.format("%d-%02d", mPicker_date.getYear(), mPicker_date.getMonth() + 1);
						}
						mBuilder.closeDialog();
					}
				}).create();
		mDialog.show();
	}
	
	/**
	 * 隐藏DatePicker的日期
	 * @param mDatePicker 日期控件
	 * @param isYear 是否只选择年
	 */
	private void hideDay(DatePicker mDatePicker, boolean isYear){
		Field[] datePickerfFields = mDatePicker.getClass().getDeclaredFields();
		for (Field datePickerField : datePickerfFields) {
			//隐藏日
			if ("mDaySpinner".equals(datePickerField.getName())) {
				datePickerField.setAccessible(true);
				Object dayPicker = new Object();
				try {
					dayPicker = datePickerField.get(mDatePicker);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
				((View) dayPicker).setVisibility(View.GONE);
			}
			//隐藏月
			if (isYear && "mMonthSpinner".equals(datePickerField.getName())) {
				datePickerField.setAccessible(true);
				Object dayPicker = new Object();
				try {
					dayPicker = datePickerField.get(mDatePicker);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
				((View) dayPicker).setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 发送HTTP请求
	 */
	private void sendPostRuest() {
		//服务器请求地址
		String requestUrl = AccountInfo.getInstance().getServerAddress() + "/user/self_all_type";
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
				if (success) {
					//刷新列表
					typeList = JSONObject.parseArray(mJSONObject.getString("rows"), Type.class);
					parseData();
				} else {
					showUserTypeErrDialog(mJSONObject.getString("message"));
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				mLoadingDialog.dismiss();
				showUserTypeErrDialog("请求出错：\n" + arg0.toString());
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> hashMap = new HashMap<String, String>();
				hashMap.put("userName", AccountInfo.getInstance().getUserName());
				return hashMap;
			}
		};
		//给请求对象添加TAG标签
		request.setTag(VOLLEY_POST);
		//将请求对象添加到请求队列中
		ASApplication.getRequestQueue().add(request);
	}

	/**
	 * 获取账目类别失败
	 * @param msg 提示信息
	 */
	private void showUserTypeErrDialog(String msg) {
		//创建dialog
		final TextDialog.Builder mBuilder = new TextDialog.Builder(
				RecordQueryActivity.this).setTitle("提示").setContent(msg);
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

	/**
	 * 展示文本提示框
	 * @param msg 提示信息
	 */
	private void showTextDialog(String msg) {
		//创建dialog
		final TextDialog.Builder mBuilder = new TextDialog.Builder(
				RecordQueryActivity.this).setTitle("提示").setContent(msg);
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
