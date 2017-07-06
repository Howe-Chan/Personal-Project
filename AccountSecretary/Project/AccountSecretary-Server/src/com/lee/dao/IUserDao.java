package com.lee.dao;

import com.lee.model.User;

/**
 * 用户数据层接口
 * 
 * @author lee
 *
 */
public interface IUserDao {

	/**
	 * 根据某个字段值查找该用户
	 * 
	 * @param property
	 *            字段名称
	 * @param value
	 *            字段值
	 * @return {@link User}
	 */
	User findByExample(String property, Object value);

	/**
	 * 插入
	 * 
	 * @param user
	 *            {@link User}
	 * @return 受影响的行数
	 */
	int add(User user);
	
	/**
	 * 根据ID更新该用户的某个字段值
	 * 
	 * @param id
	 *            用户id
	 * @param property
	 *            字段名称
	 * @param value
	 *            字段值
	 * @return 受影响的行数
	 */
	int updateExampleById(int id, String property, Object value);
	
}
