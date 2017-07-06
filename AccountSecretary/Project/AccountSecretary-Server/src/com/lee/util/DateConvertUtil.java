package com.lee.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * 日期转换工具类
 * @author lee
 *
 */
public class DateConvertUtil {

	/**
	 * 获取改日期的所在月
	 * @param month 传入的月
	 * @return {@link QueryDate}
	 */
	public static QueryDate getMonthByDate(String month){
		QueryDate queryDate = null;
		try {
			queryDate = new QueryDate();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date startDate = format.parse(month+"-01 00:00:00");
			Calendar monthCalendar = Calendar.getInstance();
			monthCalendar.setTime(startDate);
			monthCalendar.add(Calendar.MONTH,+2);//加一月
			Date endDate = monthCalendar.getTime();
			queryDate.setStartDate(startDate);
			queryDate.setEndDate(endDate);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		return queryDate;
	}
	
	/**
	 * 获取改日期的所在年
	 * @param year 传入的年
	 * @return {@link QueryDate}
	 */
	public static QueryDate getYearByDate(String year){
		QueryDate queryDate = null;
		try {
			queryDate = new QueryDate();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date startDate = format.parse(year+"-01-01 00:00:00");
			Calendar monthCalendar = Calendar.getInstance();
			monthCalendar.setTime(startDate);
			monthCalendar.add(Calendar.YEAR,+1);//加一年
			Date endDate = monthCalendar.getTime();
			queryDate.setStartDate(startDate);
			queryDate.setEndDate(endDate);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		return queryDate;
	}
}
