package com.lee.namecardocr.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.lee.namecardmanager.R;
import com.lee.namecardocr.config.CameraConfig;

/**
 * 预览边框组件
 * 
 * @author chan
 *
 */
public class PreviewBorderView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
	
	private int mScreenH;// SurfaceView高
	private int mScreenW;// SurfaceView宽
	private Canvas mCanvas;
	private Paint mPaint;
	private Paint mPaintLine;
	private SurfaceHolder mHolder;
	private Thread mThread;// 绘制线程
	
	/* 默认属性值 */
	private static final String DEFAULT_TIPS_TEXT = "请将方框对准证件拍摄";
	private static final int DEFAULT_TIPS_TEXT_SIZE = 16;
	private static final int DEFAULT_TIPS_TEXT_COLOR = Color.GREEN;
	
	/* 自定义属性 */
	private float tipTextSize;
	private int tipTextColor;
	private String tipText;

	public PreviewBorderView(Context context) {
		this(context, null);
	}

	public PreviewBorderView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PreviewBorderView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initAttrs(context, attrs);
		init();
	}

	/**
	 * 初始化自定义属性
	 *
	 * @param context {@link Context}
	 * @param attrs {@link AttributeSet}
	 */
	private void initAttrs(Context context, AttributeSet attrs) {
		// 获取自定义属性类型数组
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PreviewBorderView);
		try {
			/* 获取属性值，根据index获取页面中的值 ，default为默认值*/
			/*
			 * TypedValue.applyDimension(int unit, float value, DisplayMetrics metrics)	属性值类型转换
			 * unit		转换单位（按照这个类型进行转换）
			 * value	被转换的值
			 * metrics	当前设备的像素值
			 * */
			tipTextSize = typedArray.getDimension(
					R.styleable.PreviewBorderView_tipTextSize, 
					TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TIPS_TEXT_SIZE, getResources().getDisplayMetrics()));
			tipTextColor = typedArray.getColor(R.styleable.PreviewBorderView_tipTextColor, DEFAULT_TIPS_TEXT_COLOR);
			tipText = typedArray.getString(R.styleable.PreviewBorderView_tipText);
			if (tipText == null) {
				tipText = DEFAULT_TIPS_TEXT;
			}
		} finally {
			// 回收
			typedArray.recycle();
		}

	}

	/**
	 * 初始化绘图变量
	 */
	private void init() {
		/* 获取当前SurfaceView的控制器 */
		this.mHolder = getHolder();
		// 给控制器添加回调
		this.mHolder.addCallback(this);
		// 设置SurfaceView的像素格式<PixelFormat.TRANSPARENT 透明>
		this.mHolder.setFormat(PixelFormat.TRANSPARENT);
		/* 
		 * SurfaceView.setZOrderOnTop(boolean onTop)	设置SurfaceView置顶
		 * */ 
		setZOrderOnTop(true);
		
		/* 实例一个画笔 */
		this.mPaint = new Paint();
		// 设置抗锯齿，false:加载位图边界出现锯齿 true:使位图边界平滑
		this.mPaint.setAntiAlias(true);
		// 设置画笔颜色
		this.mPaint.setColor(Color.WHITE);
		/*
		 * Paint.setStyle(Style style)	设置画笔样式
		 * Paint.Style.FILL				填充内部
		 * Paint.Style.FILL_AND_STROKE	填充内部和描边
		 * Paint.Style.STROKE			描边
		 * */
		this.mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		/*
		 * 设置图形重叠时的显示方式
		 * PorterDuff.Mode.CLEAR	清除被重叠的不分
		 * */
		this.mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		
		/* 实例一个画笔，用于绘制框线 */
		this.mPaintLine = new Paint();
		this.mPaintLine.setColor(tipTextColor);
		// 设置画笔的宽
		this.mPaintLine.setStrokeWidth(3.0F);
		
		/*
		 * View.setKeepScreenOn(boolean keepScreenOn)	设置屏幕保持唤醒状态
		 * */
		setKeepScreenOn(true);
	}

	/**
	 * 绘制取景框
	 */
	private void draw() {
		try {
			// 获取SurfaceHolder的画布，并锁定
			this.mCanvas = this.mHolder.lockCanvas();
			// 使用指定的ARGB填充整个画布
			this.mCanvas.drawARGB(100, 0, 0, 0);
			// 宽高比例，根据View的宽高比例变化而变化
//			this.mScreenW = (this.mScreenH * 4 / 3);
			Log.d("TAG", "mScreenW:" + mScreenW + " mScreenH:" + mScreenH);
			/*
			 * Canvas.drawRect(RectF rect, Paint paint)	绘制矩形
			 * new RectF(float left, float top, float right, float bottom)	按照指定的坐标创建矩形
			 * ____________________________________
			 * |                  ^        ^
			 * |                  |        |
			 * |                  |top     |bottom
			 * |             _____|________|___
			 * |     right  |              |   |
			 * |<-----------|--------------|---|
			 * |     left   |              |   |
			 * |<-----------|              |   |
			 * |            |______________|___|
			 * |            
			 * |
			 */ 
			int screenOffset = 0;
			if (!CameraConfig.CAMERA_AUTO_ADAPTIVE) {
				screenOffset = CameraConfig.SCREEN_OFFSET;
			}
			int namecardOffset = 0;
			if (CameraConfig.IS_NAMECARD) {
				namecardOffset = CameraConfig.NAMECARD_OFFSET;
			}
			// 绘制矩形框
			float left = this.mScreenW / 2 - this.mScreenH / 2 - screenOffset / 2 - namecardOffset / 2;
			float top = this.mScreenH / 6;
			float right = this.mScreenW / 2 + this.mScreenH / 2 + screenOffset / 2 + namecardOffset / 2;
			float bottom = this.mScreenH * 5 / 6;
			this.mCanvas.drawRect(new RectF(left, top, right, bottom), this.mPaint);
			
			// 框线长
			int lineLength = 50;
			/*
			 * 下面8个都是在绘制框线
			 */
			this.mCanvas.drawLine(left, top, left, top + lineLength, this.mPaintLine);
			this.mCanvas.drawLine(left, top, left + lineLength, top, this.mPaintLine);
			this.mCanvas.drawLine(right, top, right, top + lineLength, this.mPaintLine);
			this.mCanvas.drawLine(right, top, right - lineLength, top, this.mPaintLine);
			this.mCanvas.drawLine(left, bottom, left, bottom - lineLength, this.mPaintLine);
			this.mCanvas.drawLine(left, bottom, left + lineLength, bottom, this.mPaintLine);
			this.mCanvas.drawLine(right, bottom, right, bottom - lineLength, this.mPaintLine);
			this.mCanvas.drawLine(right, bottom, right - lineLength, bottom, this.mPaintLine);
			// 设置字体大小
			mPaintLine.setTextSize(tipTextSize);
			// 设置抗锯齿
			mPaintLine.setAntiAlias(true);
			// 设置抗抖动，true:柔和的线条 false:僵硬的线条
			mPaintLine.setDither(true);
			// 测量文字的长度
			float length = mPaintLine.measureText(tipText);
			// 绘制提示文字
			this.mCanvas.drawText(tipText, this.mScreenW / 2 - length / 2, this.mScreenH / 6 - tipTextSize, mPaintLine);
			Log.d("TAG", "left:" + left);
			Log.d("TAG", "top:" + top);
			Log.d("TAG", "right:" + right);
			Log.d("TAG", "bottom:" + bottom);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (this.mCanvas != null) {
				// 解锁画布，同时将该画布贴至SurfaceView
				this.mHolder.unlockCanvasAndPost(this.mCanvas);
			}
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		/*
		 * SurfaceHolder.Callback中的方法
		 * 当SurfaceView创建时调用
		 */
		// 获得SurfaceView组件的宽高
		this.mScreenW = getWidth();
		this.mScreenH = getHeight();
		// 开启子线程绘图
		this.mThread = new Thread(this);
		this.mThread.start();
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
		// 停止线程
		try {
			mThread.interrupt();// 终端线程
			mThread = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		/*
		 * Runnable方法
		 * 子线程绘图
		 */
		draw();
	}
}
