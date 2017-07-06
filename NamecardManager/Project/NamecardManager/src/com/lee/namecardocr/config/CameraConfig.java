package com.lee.namecardocr.config;

/**
 * 相机配置
 * @author chan
 *
 */
public class CameraConfig {
	
	/** 自动适配相机 */
	public static final boolean CAMERA_AUTO_ADAPTIVE = false;
	
	/** 相机分辨率支持的宽高比例的宽<自动适配> */
	public static int CAMERA_PIXEL_SCALE_W = 4;// <CAMERA_ADAPTIVE为true时生效，手动配置无效>
	
	/** 相机分辨率支持的宽高比例的高<自动适配> */
	public static int CAMERA_PIXEL_SCALE_H = 3;// <CAMERA_ADAPTIVE为true时生效，手动配置无效>
	
	/** 相机分辨率支持的宽<自动适配> */
	public static int CAMERA_PIXEL_WIDTH = 640;// <CAMERA_ADAPTIVE为true时生效，手动配置无效>
	
	/** 相机分辨率支持的高<自动适配> */
	public static int CAMERA_PIXEL_HEIGHT = 480;// <CAMERA_ADAPTIVE为true时生效，手动配置无效>
	
	/** 相机分辨率支持的宽<手动配置> */
	public static final int CAMERA_SET_SUPPORT_WIDTH = 1280;// <CAMERA_ADAPTIVE为false时生效>,预览和相片同时支持的宽

	/** 相机分辨率支持的高<手动配置> */
	public static final int CAMERA_SET_SUPPORT_HEIGHT = 720;// <CAMERA_ADAPTIVE为false时生效>,预览和相片同时支持的高
	
	/** 屏幕偏移量 */
	public static final int SCREEN_OFFSET = 90;// <CAMERA_ADAPTIVE为false时生效>,针对不同的屏幕做出的偏移
	
	/** 是否拍摄名片 */
	public static final boolean IS_NAMECARD = true;// true:拍摄名片	false:拍摄身份证
	
	/** 名片偏移量 */
	public static final int NAMECARD_OFFSET = 40;// <CAMERA_ADAPTIVE为true时生效>,作为身份证拍摄时偏移量为0

}
