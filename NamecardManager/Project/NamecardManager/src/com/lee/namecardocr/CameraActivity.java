package com.lee.namecardocr;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lee.namecardmanager.R;
import com.lee.namecardmanager.ui.NamecardRecordActivity;
import com.lee.namecardocr.config.CameraConfig;
import com.lee.namecardocr.manager.CameraManager;
import com.lee.namecardocr.view.PreviewBorderView;

/**
 * 相机界面
 * @author chan
 *
 */
public class CameraActivity extends Activity implements OnClickListener, SurfaceHolder.Callback {
	
	private String savePath = "/sdcard/";
	private String saveFileName = "temp.jpg";
	
	private SurfaceView mSurfaceView_camera;
	private PreviewBorderView mPreviewBorderView_camera;
	private ImageView mImg_flash;
	private ImageButton mImgBtn_capture;
	
	private CameraManager mCameraManager;
    private boolean hasSurface;
    private boolean toggleLight;// 闪光灯状态
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		// 获取存放路径
		savePath = getExternalFilesDir("/namecard/").getAbsolutePath();
		
		initView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		/**
		 * 初始化camera
		 */
		mCameraManager = new CameraManager();
		// 获取SurfaceHolder
        SurfaceHolder surfaceHolder = mSurfaceView_camera.getHolder();

		if (hasSurface) {
			// activity在paused时但不会stopped,因此surface仍旧存在；
			// surfaceCreated()不会调用，因此在这里初始化camera
			initCamera(surfaceHolder);
		} else {
			// 重置callback，等待surfaceCreated()来初始化camera
			surfaceHolder.addCallback(this);
		}
	}
	
	@Override
	protected void onPause() {
		/**
		 * 停止camera，是否资源操作
		 */
		mCameraManager.stopPreview();
		mCameraManager.closeDriver();
		if (!hasSurface) {
			// 获取SurfaceHolder
	        SurfaceHolder surfaceHolder = mSurfaceView_camera.getHolder();
			surfaceHolder.removeCallback(this);
		}
		super.onPause();
	}

	/**
	 * 初始化视图
	 */
	private void initView() {
		mSurfaceView_camera = (SurfaceView) findViewById(R.id.camera_surfaceview);
		mPreviewBorderView_camera = (PreviewBorderView) findViewById(R.id.camera_previewborderview);
		mImg_flash = (ImageView) findViewById(R.id.camera_flash);
		mImgBtn_capture = (ImageButton) findViewById(R.id.camera_capture);
		//判断是否自动适应
		if (CameraConfig.CAMERA_AUTO_ADAPTIVE) {
//			int widthPixels = getScreenWidth(this);
			int heightPixels = getScreenHeight(this);
			// 重置SurfaceView的宽高
			RelativeLayout.LayoutParams surfaceviewParams = (RelativeLayout.LayoutParams) mSurfaceView_camera.getLayoutParams();
			surfaceviewParams.width = heightPixels * CameraConfig.CAMERA_PIXEL_SCALE_W / CameraConfig.CAMERA_PIXEL_SCALE_H;
			surfaceviewParams.height = heightPixels;
			mSurfaceView_camera.setLayoutParams(surfaceviewParams);
			// 重置PreviewBorderView的宽高
			RelativeLayout.LayoutParams borderViewParams = (RelativeLayout.LayoutParams) mPreviewBorderView_camera.getLayoutParams();
			borderViewParams.width = heightPixels * CameraConfig.CAMERA_PIXEL_SCALE_W / CameraConfig.CAMERA_PIXEL_SCALE_H;
			borderViewParams.height = heightPixels;
			mPreviewBorderView_camera.setLayoutParams(borderViewParams);
		}
		// listener
		mImg_flash.setOnClickListener(this);
		mImgBtn_capture.setOnClickListener(this);
	}
	
	/**
	 * 初始camera
	 *
	 * @param surfaceHolder {@link SurfaceHolder}
	 */
	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (mCameraManager.isOpen()) {
			return;
		}
		try {
			// 打开Camera硬件设备
			mCameraManager.openDriver(surfaceHolder);
			// 创建一个handler来打开预览，并抛出一个运行时异常
			mCameraManager.startPreview();
		} catch (Exception ioe) {
			
		}
	}

	/**
	 * 获得屏幕宽度，单位px
	 *
	 * @param context {@link Context}
	 * @return 屏幕宽度
	 */
	public int getScreenWidth(Context context) {
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		return displayMetrics.widthPixels;
	}

	/**
	 * 获得屏幕高度
	 *
	 * @param context {@link Context}
	 * @return 屏幕除去通知栏的高度
	 */
	public int getScreenHeight(Context context) {
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		return displayMetrics.heightPixels - getStatusBarHeight(context);
	}

	/**
	 * 获取通知栏高度
	 *
	 * @param context {@link Context}
	 * @return 通知栏高度
	 */
	public int getStatusBarHeight(Context context) {
		int statusBarHeight = 0;
		try {
			// 通过反射R文件中的status_bar_height的ID，然后根据ID获取高度
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object obj = clazz.newInstance();
			Field field = clazz.getField("status_bar_height");
			int temp = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(temp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusBarHeight;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.camera_flash:
				//闪光灯开关
				if (!toggleLight) {
					toggleLight = true;
					mImg_flash.setImageDrawable(getResources().getDrawable(R.drawable.camera_flash_open));
					mCameraManager.openLight();
				} else {
					toggleLight = false;
					mImg_flash.setImageDrawable(getResources().getDrawable(R.drawable.camera_flash_close));
					mCameraManager.offLight();
				}
				break;

			case R.id.camera_capture:
				//拍照
				mCameraManager.takePicture(null, null, mJpegCallback);
				break;
	
			default:
				break;
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		/*
		 * SurfaceHolder.Callback中的方法
		 * 当SurfaceView创建时调用
		 */
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		/*
		 * SurfaceHolder.Callback中的方法
		 * 当SurfaceView创建好后需要对SurfaceView操作时调用
		 */
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		/*
		 * SurfaceHolder.Callback中的方法
		 * 当SurfaceView销毁时调用
		 */
		hasSurface = false;
	}

	/**
	 * 拍照回调
	 */
	Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {
		
		@Override
		public void onPictureTaken(final byte[] data, Camera camera) {
			// 根据拍照所得的数据创建位图
			final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			int height = bitmap.getHeight();
			int width = bitmap.getWidth();
			// 截取框线所对应的那部分位图
			int namecardOffset = 0;
			if (CameraConfig.IS_NAMECARD) {
				namecardOffset = CameraConfig.NAMECARD_OFFSET;
			}
			int screenshotBitmapX = (width - height) / 2 - namecardOffset / 2;
			int screenshotBitmapY = height / 6;
			int screenshotBitmapWidth = height + namecardOffset;
			int screenshotBitmapXHeight = height * 2 / 3;
			final Bitmap screenshotBitmap = Bitmap.createBitmap(bitmap, screenshotBitmapX, screenshotBitmapY, screenshotBitmapWidth, screenshotBitmapXHeight);
			Log.d("TAG", "width:" + width + " height:" + height);
			Log.d("TAG", "x:" + screenshotBitmapX + " y:" + screenshotBitmapY + " width:" + screenshotBitmapWidth + " height:" + screenshotBitmapXHeight);
			
			// 创建一个位于SD卡上的文件
			File path = new File(savePath);
			if (!path.exists()) {
				path.mkdirs();
			}
			File file = new File(path, saveFileName);
			FileOutputStream outStream = null;
			try {
				// 打开指定文件对应的输出流
				outStream = new FileOutputStream(file);
				// 把位图输出到指定文件中
				screenshotBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
				outStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 返回图片路径
			Intent mIntent = new Intent();
			mIntent.putExtra(NamecardRecordActivity.IMG_PATH, file.getAbsolutePath());
			setResult(RESULT_OK, mIntent);

			CameraActivity.this.finish();

		}
	};

}
