package com.lee.namecardocr.manager;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import com.lee.namecardocr.config.CameraConfig;

/**
 * 相机管理器
 * 
 * @author chan
 *
 */
public class CameraManager {

	private static final String TAG = CameraManager.class.getName();
	private Camera camera;
	private Camera.Parameters parameters;
	private AutoFocusManager autoFocusManager;
	private int requestedCameraId = -1;// 请求相机ID

	private boolean initialized;// 是否初始化
	private boolean previewing;// 是否正在预览

	// 相机所支持的像素
	private int pixelWidth = 640;
	private int pixelHeight = 480;

	/**
	 * 打开摄像头
	 *
	 * @param cameraId 摄像头id
	 * @return Camera {@link Camera}
	 */
	public Camera open(int cameraId) {
		// 获取该设备上可用的物理摄像机的数量
		int numCameras = Camera.getNumberOfCameras();
		if (numCameras == 0) {
			Log.e(TAG, "No cameras!");
			return null;
		}
		// 用户是否明确某个相机
		boolean explicitRequest = cameraId >= 0;
		if (!explicitRequest) {
			// Select a camera if no explicit camera requested
			int index = 0;
			while (index < numCameras) {
				// 获取index所对应的相机信息
				Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
				Camera.getCameraInfo(index, cameraInfo);
				// 是否为后置摄像头
				if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
					break;
				}
				index++;
			}
			// 后置摄像头ID
			cameraId = index;
		}
		// 打开相机
		Camera camera;
		if (cameraId < numCameras) {
			Log.e(TAG, "Opening camera #" + cameraId);
			camera = Camera.open(cameraId);
		} else {
			if (explicitRequest) {
				Log.e(TAG, "Requested camera does not exist: " + cameraId);
				camera = null;
			} else {
				Log.e(TAG, "No camera facing back; returning camera #0");
				camera = Camera.open(0);
			}
		}
		return camera;
	}

	/**
	 * 打开camera
	 *
	 * @param holder {@link SurfaceHolder}
	 * @throws IOException {@link IOException}
	 */
	public synchronized void openDriver(SurfaceHolder holder) throws IOException {
		Log.e(TAG, "openDriver");
		// 打开相机
		Camera theCamera = camera;
		if (theCamera == null) {
			theCamera = open(requestedCameraId);
			if (theCamera == null) {
				throw new IOException();
			}
			camera = theCamera;
		}
		// 设置Surface用于实时预览
		theCamera.setPreviewDisplay(holder);

		if (!initialized) {
			initialized = true;
			// 在Application中获取过，则不再获取
			if (CameraConfig.CAMERA_AUTO_ADAPTIVE) {
				pixelWidth = CameraConfig.CAMERA_PIXEL_WIDTH;
				pixelHeight = CameraConfig.CAMERA_PIXEL_HEIGHT;
			} else {
				pixelWidth = CameraConfig.CAMERA_SET_SUPPORT_WIDTH;
				pixelHeight = CameraConfig.CAMERA_SET_SUPPORT_HEIGHT;
			}
			// 设置相机的参数
			parameters = camera.getParameters();
			parameters.setPreviewSize(pixelWidth, pixelHeight);// 设置预览的尺寸
			parameters.setPictureFormat(ImageFormat.JPEG);// 设置照片存储格式
			parameters.setJpegQuality(100);// 设置捕获的图片质量，参数为1-100，100为最好
			parameters.setPictureSize(pixelWidth, pixelHeight);// 设置照片的尺寸
			theCamera.setParameters(parameters);
		}
	}
	
	/**
	 * 获取相机所支持的最大尺寸
	 * 
	 * @param camera {@link Camera}
	 */
	public Camera.Size getCameraSupportedMaxSizes(Camera camera) {
		//获取相机所支持的所有预览尺寸
		List<Camera.Size> previewSizes = camera.getParameters().getSupportedPreviewSizes();
		List<Camera.Size> pictureSizes = camera.getParameters().getSupportedPictureSizes();
		// 对相机尺寸排序
		Collections.sort(previewSizes, new Comparator<Camera.Size>() {

			@Override
			public int compare(Camera.Size lhs, Camera.Size rhs) {
				if (lhs.width == rhs.width) {
					return lhs.height > rhs.height ? -1 : lhs.height < rhs.height ? 1 : 0;
				} else {
					return lhs.width > rhs.width ? -1 : lhs.width < rhs.width ? 1 : 0;
				}
			}
		});
		Collections.sort(pictureSizes, new Comparator<Camera.Size>() {

			@Override
			public int compare(Camera.Size lhs, Camera.Size rhs) {
				if (lhs.width == rhs.width) {
					return lhs.height > rhs.height ? -1 : lhs.height < rhs.height ? 1 : 0;
				} else {
					return lhs.width > rhs.width ? -1 : lhs.width < rhs.width ? 1 : 0;
				}
			}
		});
		// 获取相机的预览和照片同时支持的最大分辨率
		Camera.Size supportedSize = null;
		boolean isFinish = false;
		for (int i = 0; i < previewSizes.size(); i++) {
			for (int j = 0; j < pictureSizes.size(); j++) {
				if (previewSizes.get(i).width == pictureSizes.get(j).width && previewSizes.get(i).height == pictureSizes.get(j).height) {
					supportedSize = previewSizes.get(i);
					isFinish = true;
					break;
				}
			}
			if (isFinish) {
				break;
			}
		}
		return supportedSize;
	}
	
	/**
	 * 获取最大公约数<辗转相除法>
	 * @param m <非0>
	 * @param n <非0>
	 * @return 最大公约数
	 */
	public int getGCD(int m, int n) {
		while (true) {
			if ((m = m % n) == 0)
				return n;
			if ((n = n % m) == 0)
				return m;
		}
	}

	/**
	 * camera是否打开
	 *
	 * @return camera是否打开
	 */
	public synchronized boolean isOpen() {
		return camera != null;
	}

	/**
	 * 关闭camera
	 */
	public synchronized void closeDriver() {
		Log.e(TAG, "closeDriver");
		if (camera != null) {
			camera.release();
			camera = null;
		}
	}

	/**
	 * 开始预览
	 */
	public synchronized void startPreview() {
		Log.d(TAG, "startPreview");
		Camera theCamera = camera;
		if (theCamera != null && !previewing) {
			theCamera.startPreview();
			previewing = true;
			autoFocusManager = new AutoFocusManager(camera);
		}
	}

	/**
	 * 关闭预览
	 */
	public synchronized void stopPreview() {
		Log.d(TAG, "stopPreview");
		if (autoFocusManager != null) {
			autoFocusManager.stop();
			autoFocusManager = null;
		}
		if (camera != null && previewing) {
			camera.stopPreview();
			previewing = false;
		}
	}

	/**
	 * 打开闪光灯
	 */
	public synchronized void openLight() {
		Log.d(TAG, "openLight");
		if (camera != null) {
			parameters = camera.getParameters();
			parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
			camera.setParameters(parameters);
		}
	}

	/**
	 * 关闭闪光灯
	 */
	public synchronized void offLight() {
		Log.d(TAG, "offLight");
		if (camera != null) {
			parameters = camera.getParameters();
			parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
			camera.setParameters(parameters);
		}
	}

	/**
	 * 拍照
	 *
	 * @param shutter {@link Camera.ShutterCallback}
	 * @param raw {@link Camera.PictureCallback}
	 * @param jpeg {@link Camera.PictureCallback}
	 */
	public synchronized void takePicture(final Camera.ShutterCallback shutter, final Camera.PictureCallback raw, final Camera.PictureCallback jpeg) {
		camera.takePicture(shutter, raw, jpeg);
	}
}
