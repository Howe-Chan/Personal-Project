package com.lee.accountsecretary.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lee.accountsecretary.ASApplication;
import com.lee.accountsecretary.R;
import com.lee.accountsecretary.util.AccountInfo;

/**
 * 主界面
 * @author lee
 *
 */
public class MainActivity extends Activity implements OnClickListener {
	
	private ImageView mImageView_userIcon;	//用户头像
	private TextView mTextView_userName;	//用户名
	private ImageView mImageView_setting;	//设置
	private Button mButton_record;			//记账
	private Button mButton_query;			//查看账单

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initView();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//程序退出，更改状态
		ASApplication.getInstance().setLogin(false);
	}

	/**
	 * 初始化视图
	 */
	private void initView() {
		mImageView_userIcon = (ImageView) findViewById(R.id.main_user_icon);
		mTextView_userName = (TextView) findViewById(R.id.main_user_name);
		mImageView_setting = (ImageView) findViewById(R.id.main_setting);
		mButton_record = (Button) findViewById(R.id.main_btn_record);
		mButton_query = (Button) findViewById(R.id.main_btn_query);
		//listener
		mImageView_userIcon.setOnClickListener(this);
		mImageView_setting.setOnClickListener(this);
		mButton_record.setOnClickListener(this);
		mButton_query.setOnClickListener(this);
		
		getUserData();
	}

	/**
	 * 获取用户数据
	 */
	private void getUserData() {
		int userIconId = AccountInfo.getInstance().getUserIcon();
		String userName = AccountInfo.getInstance().getUserName();
		mTextView_userName.setText(userName);
		switch (userIconId) {
			case 0:
				mImageView_userIcon.setImageResource(R.drawable.default_user_icon);
				break;
				
			case 1:
				mImageView_userIcon.setImageResource(R.drawable.user_icon_1);
				break;
	
			case 2:
				mImageView_userIcon.setImageResource(R.drawable.user_icon_2);
				break;
	
			case 3:
				mImageView_userIcon.setImageResource(R.drawable.user_icon_3);
				break;
	
			case 4:
				mImageView_userIcon.setImageResource(R.drawable.user_icon_4);
				break;
	
			case 5:
				mImageView_userIcon.setImageResource(R.drawable.user_icon_5);
				break;
	
			case 6:
				mImageView_userIcon.setImageResource(R.drawable.user_icon_6);
				break;
	
			case 7:
				mImageView_userIcon.setImageResource(R.drawable.user_icon_7);
				break;
	
			case 8:
				mImageView_userIcon.setImageResource(R.drawable.user_icon_8);
				break;
	
			case 9:
				mImageView_userIcon.setImageResource(R.drawable.user_icon_9);
				break;
	
			default:
				break;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent mIntent = new Intent();
		switch (v.getId()) {
			case R.id.main_user_icon:
				//个人信息页
				mIntent = new Intent(this, SelfInfoActivity.class);
				startActivity(mIntent);
				finish();
				break;

			case R.id.main_setting:
				//服务器配置页
				mIntent = new Intent(this, ServerSettingActivity.class);
				startActivity(mIntent);
				break;

			case R.id.main_btn_record:
				//记账
				mIntent = new Intent(this, RecordActivity.class);
				startActivity(mIntent);
				break;

			case R.id.main_btn_query:
				//查看账单
				mIntent = new Intent(this, RecordQueryActivity.class);
				startActivity(mIntent);
				break;
	
			default:
				break;
		}
	}
}
