package com.lee.query.model;

/**
 * 按年、所有类别查询
 * @author lee
 *
 */
public class QueryYearAllType {

	private String date;			//日期
	private double incomeMoney;		//收入金额
	private double expensesMoney;	//支出金额
	private double monthSurplus;	//月结余
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public double getIncomeMoney() {
		return incomeMoney;
	}
	public void setIncomeMoney(double incomeMoney) {
		this.incomeMoney = incomeMoney;
	}
	public double getExpensesMoney() {
		return expensesMoney;
	}
	public void setExpensesMoney(double expensesMoney) {
		this.expensesMoney = expensesMoney;
	}
	public double getMonthSurplus() {
		return monthSurplus;
	}
	public void setMonthSurplus(double monthSurplus) {
		this.monthSurplus = monthSurplus;
	}
	
}
