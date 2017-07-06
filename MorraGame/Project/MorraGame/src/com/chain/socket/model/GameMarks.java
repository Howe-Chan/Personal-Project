package com.chain.socket.model;

/**
 * 游戏分数
 * @author chain
 *
 */
public class GameMarks {

	// id
	private long id;
	// 用户名
	private String userName;
	// 分数
	private long grade;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public long getGrade() {
		return grade;
	}

	public void setGrade(long grade) {
		this.grade = grade;
	}

}
