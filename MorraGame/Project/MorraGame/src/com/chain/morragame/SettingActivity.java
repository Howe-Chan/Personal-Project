package com.chain.morragame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

import com.chain.morragame.util.InputCheck;
import com.chain.morragame.util.LoadingDialogUtil;
import com.chain.socket.code.Code101;
import com.chain.socket.code.Code101.OnReceiveSiginListener;
import com.chain.socket.model.ICode;

/**
 * 设置界面
 * @author chain
 *
 */
public class SettingActivity extends Activity implements OnClickListener,OnReceiveSiginListener{
	
	//Application
	private APP mApp;

	//EditText
	private EditText mEditText_user;//用户
	private EditText mEditText_server;//服务器IP
	private EditText mEditText_port;//端口
	//ImageButton
	private ImageButton mImageButton_ok;//确定
	//用户名
	private String usrName;
	
	//加载框
	private LoadingDialogUtil mLoadDialog;
	//获取数据失败
	private boolean isGetFail = true;
	
	//异步消息处理，管理者
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				mLoadDialog.dismiss();
				new AlertDialog.Builder(SettingActivity.this).setTitle("提示").setMessage("网络错误，请检查网络连接！").setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				}).show();
				break;

			case 2:
				mLoadDialog.dismiss();
				new AlertDialog.Builder(SettingActivity.this).setTitle("提示").setMessage("已存在该用户名，请重新输入用户名！").setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				}).show();
				break;
			case 3:
				//发送用户名
				if(usrName.equals("")){
					//初始化一个命令101
					ICode mCode101 = new Code101(SettingActivity.this);
					mCode101.send(mEditText_user.getText().toString().trim());
					//当发出消息后10秒没有任何操作则显示提示框
					mHandler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							if(isGetFail){
								mHandler.sendEmptyMessage(1);
							}
							isGetFail = true;
						}
					}, 10000);
				}
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		//Application
		mApp = (APP) getApplication();
		//加载框
		mLoadDialog = new LoadingDialogUtil(this);
		//获取android数据库中的username
		usrName = mApp.getUserName();
		initview();
	}

	/**
	 * 初始化视图
	 */
	private void initview() {
		// TODO Auto-generated method stub
		//EditText
		mEditText_user = (EditText) findViewById(R.id.setting_usr_edit);
		mEditText_server = (EditText) findViewById(R.id.setting_server_edit);
		mEditText_port = (EditText) findViewById(R.id.setting_port_edit);
		//判断android数据库中是否存在用户
		if(usrName.equals("")){
			mEditText_user.setEnabled(true);//设置文本框可用
		}
		mEditText_user.setText(mApp.getUserName());
		mEditText_server.setText(mApp.getIp());
		mEditText_port.setText(""+mApp.getPort());
		//ImageButton
		mImageButton_ok = (ImageButton) findViewById(R.id.setting_ok_btn);
		mImageButton_ok.setOnClickListener(this);
		
		/*mEditText_user.setText("2434");
		mEditText_server.setText("192.168.40.101");
		mEditText_port.setText(""+1314);*/
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

	@Override
	public void onClick(View mview) {
		// TODO Auto-generated method stub
		switch (mview.getId()) {
			case R.id.setting_ok_btn:
				//获取输入的值
				String userName = mEditText_user.getText().toString().trim();
				String ip = mEditText_server.getText().toString().trim();
				String port = mEditText_port.getText().toString().trim();
				//验证输入的字符
				InputCheck mInputCheck = new InputCheck();
				if(!userName.equals("") && mInputCheck.ipCheck(ip) && mInputCheck.portCheck(port)){
					//输入的字符符合要求,写入本地数据库
					int mPort = Integer.parseInt(port);
					mApp.setUserName(userName);
					mApp.setIp(ip);
					mApp.setPort(mPort);
					mApp.commit();
					
					//加载中
					mLoadDialog.show();
					//重新建立连接
					mApp.reConnection(ip, mPort,mHandler);
					//连接后关闭
					finish();
				}else{
					new AlertDialog.Builder(SettingActivity.this).setTitle("提示").setMessage("输入的字符不符合要求，请重新输入！").setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int arg1) {
							// TODO Auto-generated method stub
							dialog.cancel();
						}
					}).show();
				}
				break;
	
			default:
				break;
		}
	}

	@Override
	public void receiveSiginSuccess(boolean temp) {
		// TODO Auto-generated method stub
		isGetFail = false;
		mLoadDialog.dismiss();
		if(temp){
			//关闭页面
			finish();
		}else{
			mHandler.sendEmptyMessage(2);
		}
	}
}
