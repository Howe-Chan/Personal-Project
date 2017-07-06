package com.lee.accountsecretary.model;

/**
 * 按年/月、单个类别查询
 * @author lee
 *
 */
public class QuerySingleType {

	private String date;	//日期
	private String type;	//类别
	private double surplus;	//类别结余
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public double getSurplus() {
		return surplus;
	}
	public void setSurplus(double surplus) {
		this.surplus = surplus;
	}
	
}
