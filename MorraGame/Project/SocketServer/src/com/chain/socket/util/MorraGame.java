package com.chain.socket.util;

import com.chain.socket.model.GameResult;

/**
 * 划拳游戏
 * 
 * @author chain
 *
 */
public class MorraGame {

	// 用户出拳
	private int userShot;
	// 电脑出拳
	private int computerShot;
	// 游戏结果
	private int result;

	public MorraGame() {

	}

	/**
	 * 带参数的构造方法
	 * 
	 * @param userShot
	 */
	public MorraGame(int userShot) {
		this.userShot = userShot;
		this.computerShot = (int) (Math.random() * 3) + 1;
	}

	/**
	 * 获取游戏结果
	 * @return
	 */
	public GameResult getRuselt() {
		if (userShot == computerShot) {
			result = 0;
		} else if ((userShot == 1 & computerShot == 3) | (userShot == 2 & computerShot == 1) | (userShot == 3 & computerShot == 2)) {
			result = 1;
		} else {
			result = 2;
		}
		//初始化一个GameResult
		GameResult gameResult = new GameResult();
		gameResult.setUserShot(userShot);
		gameResult.setComputerShot(computerShot);
		gameResult.setResult(result);
		
		return gameResult;
	}
}
