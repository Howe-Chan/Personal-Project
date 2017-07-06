package com.chain.morragame;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import com.chain.socket.SocketClient;

/**
 * 全局通用的类
 * @author chain
 *
 */
public class APP extends Application{
	//存储类
	private SharedPreferences mSharedPreferences;
	//存储编辑器
	private SharedPreferences.Editor editor;
	
	//用户名
	private String userName = "userName";
	//服务器IP
	private String ip;
	//端口
	private int port;
	//socket客户端
	private SocketClient mSocketClient;
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		//存放对象
		// 实例化SharedPreferences对象（第一步）
		mSharedPreferences = getSharedPreferences("device",MODE_PRIVATE);//创建一个私有存储
		// 实例化SharedPreferences.Editor对象（第二步）
		editor = mSharedPreferences.edit();
		// 用putString的方法保存数据 (第三步)
		
		mSocketClient = new SocketClient();
		
		Intent mIntent = new Intent();
		//判断用户名、服务器ip、端口是否不为空
		if(!getUserName().equals("") && !getIp().equals("") && getPort()!=0){
			//开启连接
			String mIp = getIp();
			int mPort = getPort();
			connection(getIp(), getPort());
		}else{
			//跳转至设置界面
			mIntent.setClass(getApplicationContext(), SettingActivity.class);
			mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(mIntent);
		}
	}
	
	/**
	 * 连接服务器
	 * @param ip
	 * @param port
	 * @return
	 */
	public void connection(final String ip, final int port){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mSocketClient.open(ip, port);
				//System.out.println("连接结果："+mSocketClient.socket.isConnected());
			}
		}).start();
	}
	
	/**
	 * 设置界面重新连接
	 * @param ip
	 * @param port
	 * @param mHandler
	 */
	public void reConnection(final String ip, final int port,final Handler mHandler){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mSocketClient.open(ip, port);
				//System.out.println("连接结果："+mSocketClient.socket.isConnected());
				mHandler.sendEmptyMessage(3);
			}
		}).start();
	}
	
	/**
	 * 提交数据
	 */
	public void commit(){
		//提交当前数据 
		editor.commit(); 
	}
	
	
	public String getIp() {
		return mSharedPreferences.getString("ip", "");
	}

	public void setIp(String ip) {
		editor.putString("ip", ip);
	}

	public int getPort() {
		return mSharedPreferences.getInt("port", 0);
	}

	public void setPort(int port) {
		editor.putInt("port", port);
	}

	public String getUserName() {
		return mSharedPreferences.getString("userName", "");
	}

	public void setUserName(String userName) {
		editor.putString("userName", userName);
	}
}
