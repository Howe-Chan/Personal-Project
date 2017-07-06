package com.lee.accountsecretary.model;

/**
 * 按月、所有类别查询
 * @author lee
 *
 */
public class QueryMonthAllType {
	
	private String date;	//日期
	private String name;	//名称
	private String type;	//类别
	private String incomeExpenses;//收入/支出
	private double money;	//金额
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getIncomeExpenses() {
		return incomeExpenses;
	}
	public void setIncomeExpenses(String incomeExpenses) {
		this.incomeExpenses = incomeExpenses;
	}
	public double getMoney() {
		return money;
	}
	public void setMoney(double money) {
		this.money = money;
	}
	
}
