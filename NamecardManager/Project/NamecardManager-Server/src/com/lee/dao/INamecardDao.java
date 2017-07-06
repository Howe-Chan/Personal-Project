package com.lee.dao;

import java.util.List;

import com.lee.model.Namecard;

/**
 * 名片数据层接口
 * 
 * @author lee
 * 
 */
public interface INamecardDao {

	/**
	 * 获取所有名片
	 * 
	 * @param pageNum
	 *            页码<按每页10行分页>
	 * @param userId
	 *            用户ID
	 * @return {@link List<Namecard>}
	 */
	List<Namecard> findAll(int pageNum, int userId);

	/**
	 * 获取所有名片的总记录数
	 * 
	 * @param userId
	 *            用户ID
	 * @return 总记录数
	 */
	long countFindAll(int userId);

	/**
	 * 新增名片
	 * 
	 * @param namecard
	 *            {@link Namecard}
	 * @return 受影响的行数
	 */
	int add(Namecard namecard);

	/**
	 * 根据名片ID删除改名片
	 * 
	 * @param id
	 *            名片ID
	 * @return 受影响的行数
	 */
	int delete(int id);

	/**
	 * 修改名片
	 * 
	 * @param namecard
	 *            {@link Namecard}
	 * @return 受影响的行数
	 */
	int update(Namecard namecard);

	/**
	 * 获取符合搜索值的所有名片
	 * 
	 * @param value
	 *            搜索值
	 * @param pageNum
	 *            页码<按每页10行分页>
	 * @param userId
	 *            用户ID
	 * @return {@link List<Namecard>}
	 */
	List<Namecard> findBySearchedValue(String value, int pageNum, int userId);

	/**
	 * 获取符合搜索值的所有名片的总记录数
	 * 
	 * @param value
	 *            搜索值
	 * @param userId
	 *            用户ID
	 * @return 总记录数
	 */
	long countFindBySearchedValue(String value, int userId);
}
