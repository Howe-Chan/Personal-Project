package com.lee.accountsecretary.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lee.accountsecretary.ASApplication;
import com.lee.accountsecretary.R;
import com.lee.accountsecretary.model.Record;
import com.lee.accountsecretary.model.Type;
import com.lee.accountsecretary.util.AccountInfo;
import com.lee.accountsecretary.widget.ButtonDialog;
import com.lee.accountsecretary.widget.LoadingDialog;
import com.lee.accountsecretary.widget.TextDialog;

/**
 * 记账界面
 * @author lee
 *
 */
@SuppressLint({ "SimpleDateFormat", "DefaultLocale" })
public class RecordActivity extends Activity implements OnClickListener{
	
	private ImageButton mImgBtn_back;		//返回
	private EditText mEditText_name;	//账目名称
	private Spinner mSpinner_type;		//账目类型
	private LinearLayout mLayout_date;	//账目日期
	private TextView mTextView_date;
	private RadioGroup mRadioGroup_incomeExpenses;//账目收/支
//	private RadioButton mRadioButton_income;//收入
//	private RadioButton mRadioButton_expenses;//支出
	private EditText mEditText_money;	//金额
	private Button mButton_ok;			//记录

	List<Type> typeList = new ArrayList<Type>();//账目类别列表
	private int typeId = 0;					//被选择的类别id
	private boolean isIncome = false;		//收入/支出

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");//日期转换器
	
	/* 操作类型 */
	private static final int USER_TYPE = 1;			//用户所有账目类别
	private static final int ADD_RECORD = 2;		//新增账目记录
	
	private static final String VOLLEY_POST = "Str_Post_Record";//volley请求的标签

	/* 提示框 */
	private LoadingDialog mLoadingDialog;//加载提示
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);
		
		initView();
		initSpinner();
	}

	@Override
	protected void onResume() {
		super.onResume();
		//默认当前时间
		mTextView_date.setText(dateFormat.format(System.currentTimeMillis()));
		//发送请求
		sendPostRuest(USER_TYPE, null);
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
		mImgBtn_back = (ImageButton) findViewById(R.id.record_btn_back);
		mEditText_name = (EditText) findViewById(R.id.record_name);
		mSpinner_type = (Spinner) findViewById(R.id.record_type);
		mLayout_date = (LinearLayout) findViewById(R.id.record_date_layout);
		mTextView_date = (TextView) findViewById(R.id.record_date_text);
		mRadioGroup_incomeExpenses = (RadioGroup) findViewById(R.id.record_income_expenses_radiogroup);
//		mRadioButton_income = (RadioButton) findViewById(R.id.record_income_radiobutton);
//		mRadioButton_expenses = (RadioButton) findViewById(R.id.record_expenses_radiobutton);
		mEditText_money = (EditText) findViewById(R.id.record_money);
		mButton_ok = (Button) findViewById(R.id.record_ok);
		//listener
		mImgBtn_back.setOnClickListener(this);
		mLayout_date.setOnClickListener(this);
		mButton_ok.setOnClickListener(this);
		mRadioGroup_incomeExpenses.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
					case R.id.record_income_radiobutton:
						//收入
						isIncome = true;
						break;
	
					case R.id.record_expenses_radiobutton:
						//支出
						isIncome = false;
						break;
	
					default:
						break;
				}
			}
		});
		mEditText_money.setFilters(new InputFilter[] { mInputFilter });//设置输入类型
	}
	
	/* 重写输入框类型 */
	private InputFilter mInputFilter = new InputFilter() {
		
		private static final int MAX_VALUE = 100000000;	//最大金额
		private static final int PONTINT_LENGTH = 2;	//小数位数
		Pattern p = Pattern.compile("[0-9]*");		//除数字外的其他的字符串
		
		/**
		 * arg0		新输入的字符串
		 * arg1		新输入的字符串起始下标，一般为0
		 * arg2		新输入的字符串终点下标，一般为arg0长度-1
		 * arg3		输入之前文本框内容
		 * arg4		原内容起始坐标，一般为0
		 * arg5		原内容终点坐标，一般为arg3长度-1
		 */
		@Override
		public CharSequence filter(CharSequence arg0, int arg1, int arg2,
				Spanned arg3, int arg4, int arg5) {
			String inputtext = arg0.toString().trim();//新字符串
			String oldtext = arg3.toString().trim();//之前的字符串
			//验证删除等按键
			if("".equals(inputtext)){
				return null;
			}
            //判断第一次输入0第二次又输入0-9
			Matcher in = p.matcher(inputtext);
			if(oldtext.equals("0") && in.matches() && !inputtext.equals(".")){
				return "";
			}
            //判断是否第一次直接输入小数点
            if(inputtext.equals(".") && oldtext.equals("")){
            	return "0" + inputtext;
            }
			//验证非数字或者小数点的情况
            Matcher m = p.matcher(arg0);
            if(oldtext.contains(".")){
                //已经存在小数点的情况下，只能输入数字
                if(!m.matches()){
                    return null;
                }
            }else{
                //未输入小数点的情况下，可以输入小数点和数字
                if(!m.matches() && !arg0.equals(".") ){
                    return null;
                }
            }
            //验证输入金额的大小
            if(!arg0.toString().equals("")){
                double dold = Double.parseDouble(oldtext+inputtext);
                if(dold > MAX_VALUE){
                	Toast.makeText(RecordActivity.this, "输入的账目金额不能大于"+MAX_VALUE, Toast.LENGTH_SHORT).show();
                    return arg3.subSequence(arg4, arg5);
                }else if(dold == MAX_VALUE){
                    if(inputtext.equals(".")){
                    	Toast.makeText(RecordActivity.this, "输入的账目金额不能大于"+MAX_VALUE, Toast.LENGTH_SHORT).show();
                        return arg3.subSequence(arg4, arg5);
                    }
                }
            }
            //验证小数位精度是否正确
            if(oldtext.contains(".")){
                int index = oldtext.indexOf(".");
                int len = arg5 - index;
                //小数位只能2位
                if(len > PONTINT_LENGTH){
                    CharSequence newText = arg3.subSequence(arg4, arg5);
                    return newText;
                }
            }
            return arg3.subSequence(arg4, arg5) +inputtext;
		}
	};
	
	/**
	 * 初始化下拉框
	 */
	private void initSpinner() {
		//设置监听
		mSpinner_type.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				//复制类别id
				typeId = typeList.get(position).getId();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
	}

	/**
	 * 解析数据
	 */
	private void parseData() {
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
				View view = View.inflate(RecordActivity.this, R.layout.sign_spinner_layout, null);
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
			case R.id.record_btn_back:
				finish();
				break;

			case R.id.record_date_layout:
				//日期选择
				showDateTimeDialog();
				break;

			case R.id.record_ok:
				String recordName = mEditText_name.getText().toString().trim();
				String strDatetime = mTextView_date.getText().toString().trim();
				String strMoney = mEditText_money.getText().toString().trim();
				//记录
				if (TextUtils.isEmpty(recordName) || TextUtils.isEmpty(strDatetime) || TextUtils.isEmpty(strMoney)) {
					showTextDialog("请确认所有内容都填写完整！");
				} else {
					try {
						Date datetime = dateFormat.parse(strDatetime);
						double money = Double.parseDouble(strMoney);
						Record record = new Record();
						record.setName(recordName);
						record.setDatetime(datetime);
						record.setInconme(isIncome);
						record.setTypeId(typeId);
						record.setMoney(money);
						//发送请求
						sendPostRuest(ADD_RECORD, record);
					} catch (ParseException e) {
						showTextDialog("数据转换出错：\n" + e.getMessage());
					}
				}
				
				break;
	
			default:
				break;
		}
	}

	/**
	 * 显示日期选择
	 */
	@SuppressLint("InflateParams")
	private void showDateTimeDialog() {
		//内容布局
		View mView = getLayoutInflater().inflate(R.layout.content_datetime, null);
		final DatePicker mPicker_date = (DatePicker) mView.findViewById(R.id.content_datetime_date);
		final TimePicker mPicker_time = (TimePicker) mView.findViewById(R.id.content_datetime_time);
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
        //加载时间控件
        mPicker_time.setIs24HourView(true);
        mPicker_time.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
        mPicker_time.setCurrentMinute(cal.get(Calendar.MINUTE));
        mPicker_time.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        //创建dialog
		final ButtonDialog.Builder mBuilder = new ButtonDialog.Builder(
				RecordActivity.this).setTitle("选择账目日期").setView(mView);
		ButtonDialog mDialog = mBuilder.setLeftButton("确    定",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
                        //将日期填充
						String datetime = String.format(
								"%d-%02d-%02d %02d:%02d",
								mPicker_date.getYear(),
								mPicker_date.getMonth() + 1,
								mPicker_date.getDayOfMonth(),
								mPicker_time.getCurrentHour(),
								mPicker_time.getCurrentMinute());
                        mTextView_date.setText(datetime);
						mBuilder.closeDialog();
					}
				}).create();
		mDialog.show();
	}

	/**
	 * 发送HTTP请求
	 * @param operateType 操作类型
	 * @param record {@link Record}
	 */
	private void sendPostRuest(final int operateType, final Record record) {
		//服务器请求地址
		String requestUrl = AccountInfo.getInstance().getServerAddress();
		//拼装请求url
		switch (operateType) {
			case USER_TYPE:
				requestUrl += "/user/self_all_type";
				mLoadingDialog= new LoadingDialog.Builder(RecordActivity.this).setText("正在加载……").create();
				break;
				
			case ADD_RECORD:
				requestUrl += "/record/add";
				mLoadingDialog= new LoadingDialog.Builder(RecordActivity.this).setText("正在记录……").create();
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
					case USER_TYPE:
						if (success) {
							//刷新列表
							typeList = JSONObject.parseArray(mJSONObject.getString("rows"), Type.class);
							parseData();
						} else {
							showUserTypeErrDialog(mJSONObject.getString("message"));
						}
						break;

					case ADD_RECORD:
						if (success) {
							showTextDialog("记录账目成功！");
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
				if (operateType == USER_TYPE) {
					showUserTypeErrDialog("请求出错：\n" + arg0.toString());
				} else {
					showTextDialog("请求出错：\n" + arg0.toString());
				}
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> hashMap = null;
				switch (operateType) {
					case USER_TYPE:
						hashMap = new HashMap<String, String>();
						hashMap.put("userName", AccountInfo.getInstance().getUserName());
						break;

					case ADD_RECORD:
						hashMap = new HashMap<String, String>();
						hashMap.put("userName", AccountInfo.getInstance().getUserName());
						hashMap.put("name", record.getName());
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						hashMap.put("datetime", format.format(record.getDatetime()));
						hashMap.put("income", JSONObject.toJSONString(record.isInconme()));
						hashMap.put("money", JSONObject.toJSONString(record.getMoney()));
						hashMap.put("typeId", JSONObject.toJSONString(typeId));
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
	 * 获取账目类别失败
	 * @param msg 提示信息
	 */
	private void showUserTypeErrDialog(String msg) {
		//创建dialog
		final TextDialog.Builder mBuilder = new TextDialog.Builder(
				RecordActivity.this).setTitle("提示").setContent(msg);
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
				RecordActivity.this).setTitle("提示").setContent(msg);
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
