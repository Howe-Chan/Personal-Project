package com.lee.accountsecretary.model;

import java.util.Date;

/**
 * 账目实体
 * @author lee
 *
 */
public class Record {
	
	private int id;			//id
	private String name;	//账目名称
	private Date datetime;	//账目日期
	private boolean isInconme;//收入/支出
	private double money;	//账目金额
	private int typeId;		//类别id
	private int userId;		//用户id

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	public boolean isInconme() {
		return isInconme;
	}

	public void setInconme(boolean isInconme) {
		this.isInconme = isInconme;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}
