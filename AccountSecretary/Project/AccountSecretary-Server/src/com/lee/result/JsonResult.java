package com.lee.result;

import java.util.HashMap;

/**
 * 
  @Title: JsonResult
  @Description: 统一JSON返回格式
  @author: lee
 */
public class JsonResult extends HashMap<String, Object>{
	private static final long serialVersionUID = 1L;
	/** 成功标记关键字 **/
    private final static String SUCCESS_KEY = "success";
    /** 返回信息关键字 **/
    private final static String MESSAGE_KEY = "message";
    /** 返回数据关键字 **/
    private final static String ROWS_KEY = "rows";
    /** 总记录数，只在返回数据为Array或List时设定，总记录数包括数据查询的所有记录总数，一般在分页时使用 **/
    private final static String TOTAL_KEY = "total";

    /** 构造并初始化 **/
    public JsonResult() {
        this.putSuccess(true);
    }

    /**
     * 写入成功标记
     * @param success 成功标记
     * @return {@link JsonResult} 返回该对象
     */
    public JsonResult putSuccess(boolean success) {
        this.put(SUCCESS_KEY, success);
        return this;
    }

    /**
     * 写入成功标记和返回信息，一般该方法在失败时调用
     * @param success 成功标记
     * @param message 返回信息
     * @return {@link JsonResult} 返回该对象
     */
    public JsonResult putSuccess(boolean success, String message) {
        this.put(SUCCESS_KEY, success);
        this.put(MESSAGE_KEY, message);
        return this;
    }

    /**
     * 写入返回信息，一般该方法在失败时调用
     * @param message 返回信息
     * @return {@link JsonResult} 返回该对象
     */
    public JsonResult putMessage(String message) {
        this.put(MESSAGE_KEY, message);
        return this;
    }

    /**
     * 写入返回数据
     * @param rows 返回数据
     * @return {@link JsonResult} 返回该对象
     */
    public JsonResult putRows(Object rows) {
        this.put(ROWS_KEY, rows);
        return this;
    }

    /**
     * 写入返回数据和总记录数，总记录数包括数据查询的所有记录总数，一般在分页时使用。
     * @param rows 返回数据
     * @param total 总记录数
     * @return {@link JsonResult} 返回该对象
     */
    public JsonResult putRows(Object rows, long total) {
        this.put(ROWS_KEY, rows);
        this.put(TOTAL_KEY, total);
        return this;
    }

    /**
     * 写入总记录数
     * @param total 总记录数
     * @return {@link JsonResult} 返回该对象
     */
    public JsonResult putTotal(long total) {
        this.put(TOTAL_KEY, total);
        return this;
    }
    
    public JsonResult put(String key, Object value) {
    	super.put(key, value);
		return this;
    }
}
