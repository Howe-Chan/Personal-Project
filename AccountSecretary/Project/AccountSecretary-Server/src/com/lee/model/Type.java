package com.lee.model;

/**
 * 类别实体
 * @author lee
 *
 */
public class Type {

	private int id;		//id
	private String name;//类别名称
	private int userId;	//用户id

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

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
}
