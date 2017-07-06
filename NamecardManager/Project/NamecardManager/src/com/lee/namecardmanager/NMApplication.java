package com.lee.namecardmanager;

import android.app.Application;
import android.hardware.Camera;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.lee.namecardocr.config.CameraConfig;
import com.lee.namecardocr.manager.CameraManager;

/**
 * 全局
 * @author lee
 *
 */
public class NMApplication extends Application{

	private static NMApplication mInstance;

	private static RequestQueue mRequestQueue;//volley请求队列
	
	private boolean isLogin;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		//创建一个volley请求队列
		mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		//每次启动程序默认没有登录
		isLogin = false;
		
		// 判断是否自动适配
		if (CameraConfig.CAMERA_AUTO_ADAPTIVE) {
			cameraAutoAdptive();
		}
	}

	/**
	 * 相机自动适配
	 */
	private void cameraAutoAdptive() {
		CameraManager mCameraManager = new CameraManager();
		Camera mCamera = mCameraManager.open(-1);
		Camera.Size supportedSize = mCameraManager.getCameraSupportedMaxSizes(mCamera);
		if (supportedSize != null) {
			CameraConfig.CAMERA_PIXEL_WIDTH = supportedSize.width;
			CameraConfig.CAMERA_PIXEL_HEIGHT = supportedSize.height;
			// 换算出分辨率比例
			int maxCommonisor = mCameraManager.getGCD(CameraConfig.CAMERA_PIXEL_WIDTH, CameraConfig.CAMERA_PIXEL_HEIGHT);
			CameraConfig.CAMERA_PIXEL_SCALE_W = CameraConfig.CAMERA_PIXEL_WIDTH / maxCommonisor;
			CameraConfig.CAMERA_PIXEL_SCALE_H = CameraConfig.CAMERA_PIXEL_HEIGHT / maxCommonisor;
		} else {
			CameraConfig.CAMERA_PIXEL_WIDTH = CameraConfig.CAMERA_SET_SUPPORT_WIDTH;
			CameraConfig.CAMERA_PIXEL_HEIGHT = CameraConfig.CAMERA_SET_SUPPORT_HEIGHT;
		}
		// 释放相机
		mCamera.release();
	}

	/**
	 * 获取Application
	 * @return {@link NMApplication}
	 */
	public static NMApplication getInstance() {
		return mInstance;
	}
	
	/**
	 * 获取volley请求队列
	 * @return {@link RequestQueue}
	 */
	public static RequestQueue getRequestQueue(){
		return mRequestQueue;
	}

	public boolean isLogin() {
		return isLogin;
	}

	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}

}
