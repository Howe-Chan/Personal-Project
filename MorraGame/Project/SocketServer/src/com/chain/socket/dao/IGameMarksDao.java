package com.chain.socket.dao;

import java.util.List;

import com.chain.socket.model.GameMarks;

/**
 * 游戏分数DAO
 * 
 * @author chain
 *
 */
public interface IGameMarksDao {

	/**
	 * 添加一条分数记录
	 * 
	 * @param bean
	 * @return
	 */
	public long add(GameMarks bean);

	/**
	 * 删除一条分数记录
	 * 
	 * @param id
	 */
	public void delete(long id);

	/**
	 * 更新某条分数记录
	 * 
	 * @param bean
	 */
	public long update(GameMarks bean);

	/**
	 * 根据索引去查该条分数记录
	 * 
	 * @param id
	 * @return
	 */
	public GameMarks findById(long id);

	/**
	 * 查询所有分数记录（前十）
	 * 
	 * @return
	 */
	public List<GameMarks> findAll();

	/**
	 * 根据某个属性值去查该条分数记录
	 * 
	 * @param property
	 * @param value
	 * @return
	 */
	public GameMarks findByExample(String property, Object value);
}
