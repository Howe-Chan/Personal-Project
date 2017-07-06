package com.chain.socket.model;

/**
 * 消息类
 * 
 * @author chain
 *
 */
public class CMessage {
	// 命令
	private int code;
	// 用户名
	private String userName;
	// 内容
	private String content;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
