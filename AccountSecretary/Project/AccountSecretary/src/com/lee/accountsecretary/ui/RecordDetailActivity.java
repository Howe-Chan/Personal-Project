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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lee.accountsecretary.ASApplication;
import com.lee.accountsecretary.R;
import com.lee.accountsecretary.model.QueryMonthAllType;
import com.lee.accountsecretary.model.QuerySingleType;
import com.lee.accountsecretary.model.QueryYearAllType;
import com.lee.accountsecretary.util.AccountInfo;
import com.lee.accountsecretary.widget.LoadingDialog;
import com.lee.accountsecretary.widget.TextDialog;

/**
 * 账单明细
 * @author lee
 *
 */
public class RecordDetailActivity extends Activity implements OnClickListener{
	
	private ImageButton mImgBtn_back;			//返回
	private RelativeLayout mLayout_titleTools;	//标题工具栏
	private Button mButton_previousPage;		//上一页
	private Button mButton_nextPage;			//下一页
	private TextView mTextView_currentPage;		//当前页码
	private TextView mTextView_totalPage;		//总共页数
	private TextView mTextView_listTitle1;		//标题1
	private TextView mTextView_listTitle2;		//标题2
	private TextView mTextView_listTitle3;		//标题3
	private TextView mTextView_listTitle4;		//标题4
	private TextView mTextView_listTitle5;		//标题5
	private ListView mListView_detail;			//明细列表
	private TextView mTextView_clearingTitle1;	//结算结果1标题
	private TextView mTextView_clearing1;		//结算结果1
	private LinearLayout mLayout_clearing2;		//结算布局2
	private TextView mTextView_clearingTitle2;	//结算结果2标题
	private TextView mTextView_clearing2;		//结算结果2
	
	private int queryType = 0;		//查询条件类型
	private String date = "";		//日期
	private int typeId = 0;			//账目类别id
	private int totalPage = 1;		//总页码
	private int currentPage = 1;	//当前页码
	
	/* 查询条件 */
	public static final int QUERY_YEAR_ALL = 1;		//按年查询所有类别
	public static final int QUERY_YEAR_SINGLE = 2;	//按年查询单个类别
	public static final int QUERY_MONTH_ALL = 3;	//按月查询所有类别
	public static final int QUERY_MONTH_SINGLE = 4;	//按月查询单个类别
	
	/* 提示框 */
	private LoadingDialog mLoadingDialog;

	private static final String VOLLEY_POST = "Str_Post_RecordDetail";//volley请求的标签

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_detail);
		//获取查询条件
		Intent mIntent = getIntent();
		queryType = mIntent.getIntExtra(RecordQueryActivity.CONDITION_QUERTYPE, 0);
		date = mIntent.getStringExtra(RecordQueryActivity.CONDITION_DATE);
		typeId = mIntent.getIntExtra(RecordQueryActivity.CONDITION_TYPE, 0);
		//初始化视图
		mLoadingDialog = new LoadingDialog.Builder(RecordDetailActivity.this).setText("正在查询……").create();
		initView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//发送请求
		sendPostRuest(totalPage);
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
		mImgBtn_back = (ImageButton) findViewById(R.id.recorddetail_btn_back);
		mLayout_titleTools = (RelativeLayout) findViewById(R.id.recorddetail_title_tools);
		mButton_previousPage = (Button) findViewById(R.id.recorddetail_previous_page);
		mButton_nextPage = (Button) findViewById(R.id.recorddetail_next_page);
		mTextView_currentPage = (TextView) findViewById(R.id.recorddetail_current_page);
		mTextView_totalPage = (TextView) findViewById(R.id.recorddetail_total_page);
		mTextView_listTitle1 = (TextView) findViewById(R.id.recorddetail_listview_title_1);
		mTextView_listTitle2 = (TextView) findViewById(R.id.recorddetail_listview_title_2);
		mTextView_listTitle3 = (TextView) findViewById(R.id.recorddetail_listview_title_3);
		mTextView_listTitle4 = (TextView) findViewById(R.id.recorddetail_listview_title_4);
		mTextView_listTitle5 = (TextView) findViewById(R.id.recorddetail_listview_title_5);
		mListView_detail = (ListView) findViewById(R.id.recorddetail_listview);
		mTextView_clearingTitle1 = (TextView) findViewById(R.id.recorddetail_clearing_1_title);
		mTextView_clearing1 = (TextView) findViewById(R.id.recorddetail_clearing_1);
		mLayout_clearing2 = (LinearLayout) findViewById(R.id.recorddetail_clearing_2_layout);
		mTextView_clearingTitle2 = (TextView) findViewById(R.id.recorddetail_clearing_2_title);
		mTextView_clearing2 = (TextView) findViewById(R.id.recorddetail_clearing_2);
		//listener
		mImgBtn_back.setOnClickListener(this);
		mButton_previousPage.setOnClickListener(this);
		mButton_nextPage.setOnClickListener(this);
		//界面变化
		viewChange();
	}

	/**
	 * 更改表头
	 */
	private void viewChange() {
		switch (queryType) {
			case QUERY_YEAR_ALL:
				mTextView_listTitle1.setText("日期");
				mTextView_listTitle2.setText("收入");
				mTextView_listTitle3.setText("支出");
				mTextView_listTitle4.setText("月结余");
				mTextView_listTitle5.setVisibility(View.INVISIBLE);
				mLayout_titleTools.setVisibility(View.GONE);
				mTextView_clearingTitle1.setText("年结余：");
				mTextView_clearingTitle2.setText("该年支出最多的类别：");
				break;

			case QUERY_YEAR_SINGLE:
				mTextView_listTitle1.setText("日期");
				mTextView_listTitle2.setText("类别");
				mTextView_listTitle3.setText("类别月结余");
				mTextView_listTitle4.setVisibility(View.INVISIBLE);
				mTextView_listTitle5.setVisibility(View.INVISIBLE);
				mLayout_titleTools.setVisibility(View.GONE);
				mTextView_clearingTitle1.setText("该类别年结余：");
				mLayout_clearing2.setVisibility(View.GONE);
				break;

			case QUERY_MONTH_ALL:
				mTextView_listTitle1.setText("日期");
				mTextView_listTitle2.setText("名称");
				mTextView_listTitle3.setText("类别");
				mTextView_listTitle4.setText("收/支");
				mTextView_listTitle5.setText("金额");
				mLayout_titleTools.setVisibility(View.VISIBLE);
				mTextView_clearingTitle1.setText("月结余：");
				mTextView_clearingTitle2.setText("该月支出最多的类别：");
				break;

			case QUERY_MONTH_SINGLE:
				mTextView_listTitle1.setText("日期");
				mTextView_listTitle2.setText("类别");
				mTextView_listTitle3.setText("金额");
				mTextView_listTitle4.setVisibility(View.INVISIBLE);
				mTextView_listTitle5.setVisibility(View.INVISIBLE);
				mLayout_titleTools.setVisibility(View.VISIBLE);
				mTextView_clearingTitle1.setText("该类别月结余：");
				mLayout_clearing2.setVisibility(View.GONE);
				break;
	
			default:
				break;
		}
	}

	/**
	 * 解析数据
	 * @param jsonData 解析数据
	 */
	private void parseData(String jsonData){
		//解析数据
		if (jsonData == null || TextUtils.isEmpty(jsonData)) {
			return;
		}
		//先将数据转成Array
		JSONArray mJSONArray = JSON.parseArray(jsonData);
		//存放数据
		List<Map<String, Object>> mDataList = new ArrayList<Map<String,Object>>();
		for (int i = 0; i < mJSONArray.size(); i++) {
	        Map<String, Object> map = new HashMap<String, Object>();
			switch (queryType) {
				case QUERY_YEAR_ALL:
					QueryYearAllType mQueryYearAllType = JSON.parseObject(mJSONArray.getString(i), QueryYearAllType.class);
					map.put("text1", mQueryYearAllType.getDate());
					map.put("text2", mQueryYearAllType.getIncomeMoney());
					map.put("text3", mQueryYearAllType.getExpensesMoney());
					map.put("text4", mQueryYearAllType.getMonthSurplus());
					map.put("text5", "");
					break;

				case QUERY_YEAR_SINGLE:
					QuerySingleType mQueryYearSingleType = JSON.parseObject(mJSONArray.getString(i), QuerySingleType.class);
					map.put("text1", mQueryYearSingleType.getDate());
					map.put("text2", mQueryYearSingleType.getType());
					map.put("text3", mQueryYearSingleType.getSurplus());
					map.put("text4", "");
					map.put("text5", "");
					break;

				case QUERY_MONTH_ALL:
					QueryMonthAllType mQueryMonthAllType = JSON.parseObject(mJSONArray.getString(i), QueryMonthAllType.class);
					map.put("text1", mQueryMonthAllType.getDate());
					map.put("text2", mQueryMonthAllType.getName());
					map.put("text3", mQueryMonthAllType.getType());
					map.put("text4", mQueryMonthAllType.getIncomeExpenses());
					map.put("text5", mQueryMonthAllType.getMoney());
					break;

				case QUERY_MONTH_SINGLE:
					QuerySingleType mQueryMonthSingleType = JSON.parseObject(mJSONArray.getString(i), QuerySingleType.class);
					map.put("text1", mQueryMonthSingleType.getDate());
					map.put("text2", mQueryMonthSingleType.getType());
					map.put("text3", mQueryMonthSingleType.getSurplus());
					map.put("text4", "");
					map.put("text5", "");
					break;
	
				default:
					break;
			}
			mDataList.add(map);
		}
		refreshListData(mDataList);
	}
	
	/**
	 * 刷新列表
	 */
	private void refreshListData(List<Map<String,Object>> dataList){
		//创建适配器
		SimpleAdapter adapter = new SimpleAdapter(this, dataList,
				R.layout.record_detail_list, new String[] { "text1", "text2",
						"text3", "text4", "text5" }, new int[] {
						R.id.listview_text_1, R.id.listview_text_2,
						R.id.listview_text_3, R.id.listview_text_4,
						R.id.listview_text_5 });
		mListView_detail.setAdapter(adapter);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.recorddetail_btn_back:
				finish();
				break;

			case R.id.recorddetail_previous_page:
				//上一页
				currentPage --;
				sendPostRuest(currentPage);
				break;

			case R.id.recorddetail_next_page:
				//下一页
				currentPage ++;
				sendPostRuest(currentPage);
				break;
	
			default:
				break;
		}
	}

	/**
	 * 发送HTTP请求
	 * @param page 页码
	 */
	private void sendPostRuest(final int page) {
		//服务器请求地址
		String requestUrl = AccountInfo.getInstance().getServerAddress();
		//拼装请求url
		switch (queryType) {
			case QUERY_YEAR_ALL:
				requestUrl += "/record/query_year_alltype";
				break;
				
			case QUERY_YEAR_SINGLE:
				requestUrl += "/record/query_year_singletype";
				break;
				
			case QUERY_MONTH_ALL:
				requestUrl += "/record/query_month_alltype";
				break;
				
			case QUERY_MONTH_SINGLE:
				requestUrl += "/record/query_month_singletype";
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
				switch (queryType) {
					case QUERY_YEAR_ALL:
						if (success) {
							//刷新列表
							mTextView_clearing1.setText(mJSONObject.getString("yearSurplus"));
							mTextView_clearing2.setText(mJSONObject.getString("yearExpensesMaxType"));
							parseData(mJSONObject.getString("rows"));
						} else {
							showTextDialog(mJSONObject.getString("message"));
						}
						break;

					case QUERY_YEAR_SINGLE:
						if (success) {
							//刷新列表
							mTextView_clearing1.setText(mJSONObject.getString("typeYearSurplus"));
							parseData(mJSONObject.getString("rows"));
						} else {
							showTextDialog(mJSONObject.getString("message"));
						}
						break;

					case QUERY_MONTH_ALL:
						if (success) {
							//刷新列表
							totalPage = Integer.parseInt(mJSONObject.getString("total"));
							changeButton(page, totalPage);
							mTextView_currentPage.setText("" + page);
							mTextView_totalPage.setText("" + totalPage);
							mTextView_clearing1.setText(mJSONObject.getString("monthSurplus"));
							mTextView_clearing2.setText(mJSONObject.getString("monthExpensesMaxType"));
							parseData(mJSONObject.getString("rows"));
						} else {
							showTextDialog(mJSONObject.getString("message"));
						}
						break;

					case QUERY_MONTH_SINGLE:
						if (success) {
							//刷新列表
							totalPage = Integer.parseInt(mJSONObject.getString("total"));
							changeButton(page, totalPage);
							mTextView_currentPage.setText("" + page);
							mTextView_totalPage.setText("" + totalPage);
							mTextView_clearing1.setText(mJSONObject.getString("typeMonthSurplus"));
							parseData(mJSONObject.getString("rows"));
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
				showRequestErrDialog("请求出错：\n" + arg0.toString());
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> hashMap = null;
				switch (queryType) {
					case QUERY_YEAR_ALL:
						hashMap = new HashMap<String, String>();
						hashMap.put("userName", AccountInfo.getInstance().getUserName());
						hashMap.put("date", date);
						break;
						
					case QUERY_YEAR_SINGLE:
						hashMap = new HashMap<String, String>();
						hashMap.put("userName", AccountInfo.getInstance().getUserName());
						hashMap.put("date", date);
						hashMap.put("typeId", JSONObject.toJSONString(typeId));
						break;
						
					case QUERY_MONTH_ALL:
						hashMap = new HashMap<String, String>();
						hashMap.put("userName", AccountInfo.getInstance().getUserName());
						hashMap.put("date", date);
						hashMap.put("pageNum", JSONObject.toJSONString(page));
						break;
						
					case QUERY_MONTH_SINGLE:
						hashMap = new HashMap<String, String>();
						hashMap.put("userName", AccountInfo.getInstance().getUserName());
						hashMap.put("date", date);
						hashMap.put("typeId", JSONObject.toJSONString(typeId));
						hashMap.put("pageNum", JSONObject.toJSONString(page));
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
	 * 改变上下页的状态
	 * @param cPage 当前页
	 * @param tPage 总页数
	 */
	private void changeButton(int cPage, int tPage) {
		if (cPage == tPage && tPage == 1) {
			mButton_previousPage.setEnabled(false);
			mButton_nextPage.setEnabled(false);
		} else if (cPage == 1) {
			mButton_previousPage.setEnabled(false);
			mButton_nextPage.setEnabled(true);
		} else if (cPage == tPage) {
			mButton_previousPage.setEnabled(true);
			mButton_nextPage.setEnabled(false);
		} else {
			mButton_previousPage.setEnabled(true);
			mButton_nextPage.setEnabled(true);
		}
	}
	
	/**
	 * 展示文本提示框
	 * @param msg 提示信息
	 */
	private void showTextDialog(String msg) {
		//创建dialog
		final TextDialog.Builder mBuilder = new TextDialog.Builder(
				RecordDetailActivity.this).setTitle("提示").setContent(msg);
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
	private void showRequestErrDialog(String msg) {
		//创建dialog
		final TextDialog.Builder mBuilder = new TextDialog.Builder(
				RecordDetailActivity.this).setTitle("提示").setContent(msg);
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
