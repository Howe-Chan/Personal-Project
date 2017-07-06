package com.chain.socket.model;

/**
 * 游戏结果
 * 
 * shot: 1【剪刀】 2【石头】 3【布】 result: 0【平局】 1【用户赢】 2【电脑赢】
 * 
 * @author chain
 *
 */
public class GameResult {

	// 用户出拳
	private int userShot;
	// 电脑出拳
	private int computerShot;
	// 游戏结果
	private int result;

	public int getUserShot() {
		return userShot;
	}

	public void setUserShot(int userShot) {
		this.userShot = userShot;
	}

	public int getComputerShot() {
		return computerShot;
	}

	public void setComputerShot(int computerShot) {
		this.computerShot = computerShot;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

}
