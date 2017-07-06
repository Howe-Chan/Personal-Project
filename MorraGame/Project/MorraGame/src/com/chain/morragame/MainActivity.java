package com.chain.morragame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

/**
 * 主界面
 * @author chain
 *
 */
public class MainActivity extends Activity implements OnClickListener{
	
	//ImageButton
	private ImageButton mImageButton_startGame;//开始游戏
	private ImageButton mImageButton_top;//排行榜
	private ImageButton mImageButton_setting;//设置
	
	//Spinner
	private Spinner mSpinner_gameType;//游戏类型
	private static final String[] types={"无尽模式"};  
	private ArrayAdapter<String> mAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initview();
	}

	/**
	 * 初始化视图
	 */
	private void initview() {
		// TODO Auto-generated method stub
		//ImageButton
		mImageButton_startGame = (ImageButton) findViewById(R.id.start_game_btn);
		mImageButton_top = (ImageButton) findViewById(R.id.top_btn);
		mImageButton_setting = (ImageButton) findViewById(R.id.setting_btn);
		//listener
		mImageButton_startGame.setOnClickListener(this);
		mImageButton_top.setOnClickListener(this);
		mImageButton_setting.setOnClickListener(this);
		
		//Spinner
		initSpinner();
	}

	/**
	 * 初始化下拉框
	 */
	private void initSpinner() {
		// TODO Auto-generated method stub
		mSpinner_gameType = (Spinner) findViewById(R.id.game_type_spinner);
		//设置适配器mAdapter的类型
		mAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,types);
		mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//给spinner添加适配器mAdapter
		mSpinner_gameType.setAdapter(mAdapter);
		//添加监听
		mSpinner_gameType.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				switch(arg2){
					case 0 :
						//无尽模式
						
						break;
						
					default:
						break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	@Override
	public void onClick(View mview) {
		// TODO Auto-generated method stub
		//Android通讯
		Intent mIntent = new Intent();
		
		switch (mview.getId()) {
			case R.id.start_game_btn:
				//跳转至游戏界面
				mIntent.setClass(getApplicationContext(), GameMainActivity.class);
				startActivity(mIntent);
				break;

			case R.id.top_btn:
				//跳转至排行榜界面
				mIntent.setClass(getApplicationContext(), TopActivity.class);
				startActivity(mIntent);
				break;

			case R.id.setting_btn:
				//跳转至设置界面
				mIntent.setClass(getApplicationContext(), SettingActivity.class);
				startActivity(mIntent);
				break;
	
			default:
				break;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}
}
