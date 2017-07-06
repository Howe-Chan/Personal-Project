package com.lee.namecardocr.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.RejectedExecutionException;

import android.hardware.Camera;
import android.os.AsyncTask;
import android.util.Log;

/**
 * 自动对焦
 * 
 * @author chan
 *
 */
public class AutoFocusManager implements Camera.AutoFocusCallback {
	
	private static final String TAG = AutoFocusManager.class.getSimpleName();
	private static final long AUTO_FOCUS_INTERVAL_MS = 2000L;// 自动对焦间隔时间
	private static final Collection<String> FOCUS_MODES_CALLING_AF;// 对焦模式集
	// 先预加载两种对焦模式
	static {
		FOCUS_MODES_CALLING_AF = new ArrayList<String>(2);
		FOCUS_MODES_CALLING_AF.add(Camera.Parameters.FOCUS_MODE_AUTO);
		FOCUS_MODES_CALLING_AF.add(Camera.Parameters.FOCUS_MODE_MACRO);
	}

	private boolean stopped;// 是否停止对焦
	private boolean focusing;// 是否正在对焦
	private final boolean useAutoFocus;// 是否使用自动对焦
	private final Camera camera;
	private AsyncTask<?, ?, ?> outstandingTask;// 待执行线程

	public AutoFocusManager(Camera camera) {
		this.camera = camera;
		// 获取相机的对焦模式
		String currentFocusMode = camera.getParameters().getFocusMode();
		// 是否使用自动对焦
		useAutoFocus = FOCUS_MODES_CALLING_AF.contains(currentFocusMode);
		Log.e(TAG, "Current focus mode '" + currentFocusMode + "'; use auto focus? " + useAutoFocus);
		// 开始自动对焦
		start();
	}

	@Override
	public synchronized void onAutoFocus(boolean success, Camera theCamera) {
		// 相机对焦回调后执行
		focusing = false;
		autoFocusAgainLater();
	}

	/**
	 * 稍后再次自动对焦
	 */
	private synchronized void autoFocusAgainLater() {
		if (!stopped && outstandingTask == null) {
			AutoFocusTask newTask = new AutoFocusTask();
			try {
				// 设置“并行执行”方式执行线程
				newTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				outstandingTask = newTask;
			} catch (RejectedExecutionException ree) {
				Log.e(TAG, "Could not request auto focus", ree);
			}
		}
	}

	/**
	 * 开始自动对焦
	 */
	public synchronized void start() {
		if (useAutoFocus) {
			outstandingTask = null;
			if (!stopped && !focusing) {
				try {
					// 开启相机自动对焦，注册相机对焦回调
					camera.autoFocus(this);
					focusing = true;
				} catch (RuntimeException re) {
					// Have heard RuntimeException reported in Android 4.0.x+;
					// continue?
					Log.e(TAG, "Unexpected exception while focusing", re);
					// Try again later to keep cycle going
					autoFocusAgainLater();
				}
			}
		}
	}

	/**
	 * 清除待执行线程
	 */
	private synchronized void cancelOutstandingTask() {
		if (outstandingTask != null) {
			// 如果该线程还未结束，直接取消
			if (outstandingTask.getStatus() != AsyncTask.Status.FINISHED) {
				outstandingTask.cancel(true);
			}
			outstandingTask = null;
		}
	}

	/**
	 * 停止自动对焦
	 */
	public synchronized void stop() {
		stopped = true;
		if (useAutoFocus) {
			cancelOutstandingTask();
			// Doesn't hurt to call this even if not focusing
			try {
				camera.cancelAutoFocus();
			} catch (RuntimeException re) {
				// Have heard RuntimeException reported in Android 4.0.x+;
				// continue?
				Log.e(TAG, "Unexpected exception while cancelling focusing", re);
			}
		}
	}

	/**
	 * 自动对焦异步线程
	 * 
	 * @author chan
	 *
	 */
	private final class AutoFocusTask extends AsyncTask<Object, Object, Object> {
		@Override
		protected Object doInBackground(Object... voids) {
			try {
				Thread.sleep(AUTO_FOCUS_INTERVAL_MS);
			} catch (InterruptedException e) {
				// continue
			}
			start();
			return null;
		}
	}
}
