package com.lee.namecardmanager.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
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
import com.lee.namecardmanager.NMApplication;
import com.lee.namecardmanager.R;
import com.lee.namecardmanager.model.Namecard;
import com.lee.namecardmanager.util.AccountInfo;
import com.lee.namecardmanager.widget.LoadingDialog;
import com.lee.namecardmanager.widget.TextDialog;

/**
 * 名片查询界面
 * @author lee
 *
 */
public class NamecardQueryActivity extends Activity implements OnClickListener{
	
	public static final String NAMECARD_INFO = "namecard_info";

	private ImageButton mImgBtn_back;			//返回
	private EditText mEditText_searchValue;		//检索值
	private Button mButton_search;				//检索
	private Button mButton_previousPage;		//上一页
	private Button mButton_nextPage;			//下一页
	private TextView mTextView_currentPage;		//当前页码
	private TextView mTextView_totalPage;		//总共页数
	private ListView mListView_result;			//结果列表

	private int totalPage = 1;		//总页码
	private int currentPage = 1;	//当前页码
	List<Namecard> queryResult = null;
	private String searchValue = "";

	/* 提示框 */
	private LoadingDialog mLoadingDialog;

	private static final String VOLLEY_POST = "Str_Post_Namecard_Query";//volley请求的标签
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_namecard_query);
		//初始化视图
		mLoadingDialog = new LoadingDialog.Builder(NamecardQueryActivity.this).setText("正在查询……").create();
		initView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// 查出所有
		sendPostRuest("", currentPage);
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
		mImgBtn_back = (ImageButton) findViewById(R.id.query_btn_back);
		mEditText_searchValue = (EditText) findViewById(R.id.query_edit_search);
		mButton_search = (Button) findViewById(R.id.query_btn_search);
		mButton_previousPage = (Button) findViewById(R.id.query_previous_page);
		mButton_nextPage = (Button) findViewById(R.id.query_next_page);
		mTextView_currentPage = (TextView) findViewById(R.id.query_current_page);
		mTextView_totalPage = (TextView) findViewById(R.id.query_total_page);
		mListView_result = (ListView) findViewById(R.id.query_listview);
		// listener
		mImgBtn_back.setOnClickListener(this);
		mButton_search.setOnClickListener(this);
		mButton_previousPage.setOnClickListener(this);
		mButton_nextPage.setOnClickListener(this);
		mListView_result.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 跳转至名片信息界面
				Namecard namecard = queryResult.get(position);
				Intent mIntent = new Intent(NamecardQueryActivity.this, NamecardInfoActivity.class);
				Bundle mBundle = new Bundle();
				mBundle.putSerializable(NAMECARD_INFO, namecard);
				mIntent.putExtras(mBundle);
				startActivity(mIntent);
			}
		});
	}

	@Override
	public void onClick(View v) {
		searchValue = mEditText_searchValue.getText().toString().trim();
		switch (v.getId()) {
			case R.id.query_btn_back:
				// 返回
				finish();
				break;

			case R.id.query_btn_search:
				// 检索
				currentPage = 1;
				sendPostRuest(searchValue, currentPage);
				break;

			case R.id.query_previous_page:
				// 上一页
				currentPage --;
				sendPostRuest(searchValue, currentPage);
				break;

			case R.id.query_next_page:
				// 下一页
				currentPage ++;
				sendPostRuest(searchValue, currentPage);
				break;
	
			default:
				break;
		}
	}

	/**
	 * 解析数据
	 * @param jsonData 解析数据
	 */
	private void refreshListData(String jsonData){
		//解析数据
		if (jsonData == null || TextUtils.isEmpty(jsonData)) {
			return;
		}
		//先将数据转成Array
		JSONArray mJSONArray = JSON.parseArray(jsonData);
		//存放数据
		queryResult = new ArrayList<Namecard>();
		for (int i = 0; i < mJSONArray.size(); i++) {
			Namecard namecard = JSONObject.parseObject(mJSONArray.getString(i), Namecard.class);
			queryResult.add(namecard);
		}
		//创建适配器
		CustomAdapter adapter = new CustomAdapter(NamecardQueryActivity.this, queryResult);
		mListView_result.setAdapter(adapter);
	}
	
	/**
	 * 用于Adapter的ViewHolder
	 * @author lee
	 *
	 */
	private static class ViewHolder {
		private TextView mTextView_text1;
		private TextView mTextView_text2;
	}
	
	/**
	 * 用于listview的自定义适配器
	 * @author lee
	 *
	 */
	private class CustomAdapter extends BaseAdapter {
		
		private LayoutInflater mInflater = null;
		private List<Namecard> dataList = null;
		
		public CustomAdapter(Context context, List<Namecard> dataList) {
			// 根据context上下文加载布局
			this.mInflater = LayoutInflater.from(context);
			this.dataList = dataList;
		}
		
		@Override
		public int getCount() {
			// 在此适配器中所代表的数据集中的条目数
			return dataList.size();
		}

		@Override
		public Object getItem(int position) {
			// 获取数据集中与指定索引对应的数据项
			return dataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// 获取在列表中与指定索引对应的行id
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 获取一个在数据集中指定索引的视图来显示数据
			ViewHolder holder = null;
			// 如果缓存convertView为空，则需要创建View
			if (convertView == null) {
				holder = new ViewHolder();
				// 根据自定义的Item布局加载布局
				convertView = mInflater.inflate(R.layout.query_result_list_item, null);
				holder.mTextView_text1 = (TextView) convertView.findViewById(R.id.listview_text_1);
				holder.mTextView_text2 = (TextView) convertView.findViewById(R.id.listview_text_2);
				// 将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// 赋值
			holder.mTextView_text1.setText((CharSequence) dataList.get(position).getName());
			String resultRes = SearchResultRes(dataList.get(position), searchValue);
			if ("".equals(resultRes)) {
				holder.mTextView_text2.setVisibility(View.GONE);
			} else {
				holder.mTextView_text2.setVisibility(View.VISIBLE);
				holder.mTextView_text2.setText(resultRes);
			}
			return convertView;
		}
		
	}
	
	/**
	 * 从名片中检索被检索值
	 * @param namecard {@link Namecard}
	 * @return 
	 */
	private String SearchResultRes(Namecard namecard, String search) {
		// 姓名全拼
		if (Pattern.matches(convertToRegExp(search), namecard.getNamePinyin())) {
			return "";
		}
		// 姓名
		if (namecard.getName().contains(search)) {
			return "";
		}
		// 职称
		if (namecard.getJobTitle().contains(search)) {
			return namecard.getJobTitle();
		}
		// 公司名称
		if (namecard.getCompanyName().contains(search)) {
			return namecard.getCompanyName();
		}
		// 公司地址
		if (namecard.getCompanyAddress().contains(search)) {
			return namecard.getCompanyAddress();
		}
		// 手机
		if (namecard.getMobile().contains(search)) {
			return namecard.getMobile();
		}
		// 电话
		if (namecard.getTel().contains(search)) {
			return namecard.getTel();
		}
		// 传真
		if (namecard.getFax().contains(search)) {
			return namecard.getFax();
		}
		// 邮件
		if (namecard.getEmail().contains(search)) {
			return namecard.getEmail();
		}
		// 网址
		if (namecard.getWeb().contains(search)) {
			return namecard.getWeb();
		}
		return "";
	}
	
	/**
	 * 将值转换成正则表达式
	 * @param value 被转换的值
	 * @return
	 */
	private String convertToRegExp(String value) {
		String temp = "\\S";
		int length = value.length();
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				temp = temp + value.substring(i, i + 1) + "\\S";
			}
		} else {
			temp = temp + value + "\\S";
		}
		return temp;
	}

	/**
	 * 发送HTTP请求
	 * @param page 页码
	 */
	private void sendPostRuest(final String search, final int page) {
		//服务器请求地址
		String requestUrl = AccountInfo.getInstance().getServerAddress() + "/namecard/fuzzy_query_namecard";
		mLoadingDialog.show();
		//建立不确定返回对象的请求
		StringRequest request = new StringRequest(Method.POST, requestUrl, new Listener<String>() {
			
			@Override
			public void onResponse(String arg0) {
				mLoadingDialog.dismiss();
				//数据解析
				JSONObject mJSONObject = JSONObject.parseObject(arg0);
				boolean success = JSONObject.parseObject(mJSONObject.getString("success"), Boolean.class);
				if (success) {
					//刷新列表
					totalPage = Integer.parseInt(mJSONObject.getString("total"));
					changeButton(page, totalPage);
					mTextView_currentPage.setText("" + page);
					mTextView_totalPage.setText("" + totalPage);
					refreshListData(mJSONObject.getString("rows"));
				} else {
					showTextDialog(mJSONObject.getString("message"));
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
				Map<String, String> hashMap =  new HashMap<String, String>();
				hashMap.put("userName", AccountInfo.getInstance().getUserName());
				hashMap.put("userPs", AccountInfo.getInstance().getUserPs());
				hashMap.put("value", search);
				hashMap.put("pageNum", JSONObject.toJSONString(page));
				return hashMap;
			}
		};
		//给请求对象添加TAG标签
		request.setTag(VOLLEY_POST);
		//将请求对象添加到请求队列中
		NMApplication.getRequestQueue().add(request);
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
				NamecardQueryActivity.this).setTitle("提示").setContent(msg);
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
	 * 获取名片失败
	 * @param msg 提示信息
	 */
	private void showRequestErrDialog(String msg) {
		//创建dialog
		final TextDialog.Builder mBuilder = new TextDialog.Builder(
				NamecardQueryActivity.this).setTitle("提示").setContent(msg);
		TextDialog mDialog = mBuilder.setButton("确    定",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						mBuilder.closeDialog();
						NamecardQueryActivity.this.finish();
					}
					
				}).create();
		mDialog.show();
	}

}
