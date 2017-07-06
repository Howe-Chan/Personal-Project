package com.chain.socket.service;

import java.util.List;

import com.chain.socket.model.GameMarks;

/**
 * 用户分数Service
 * @author chain
 *
 */
public interface IGameMarksService {

	/**
	 * 添加一条分数记录
	 * 
	 * @param bean
	 * @return
	 */
	public boolean add(GameMarks bean);

	/**
	 * 更新某个分数记录（更新该用户的分数）
	 * 
	 * @param bean
	 * @return
	 */
	public boolean update(GameMarks bean);

	/**
	 * 查看排行榜
	 * 
	 * @return
	 */
	public List<GameMarks> findAll();
}
