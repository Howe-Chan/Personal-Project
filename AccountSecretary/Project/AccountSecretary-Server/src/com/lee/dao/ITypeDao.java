package com.lee.dao;

import java.util.List;

import com.lee.model.Type;

/**
 * 类别数据层接口
 * 
 * @author lee
 *
 */
public interface ITypeDao {

	/**
	 * 查询该用户id的所有类别
	 * 
	 * @param userId
	 *            用户id
	 * @return {@link List<Type>}
	 */
	List<Type> findByUserId(int userId);

	/**
	 * 删除用户的某个类别
	 * 
	 * @param id
	 *            id
	 * @param userId
	 *            用户id
	 * @return 受影响的行数
	 */
	int deleteByIdAndUserId(int id, int userId);

	/**
	 * 新增类别
	 * 
	 * @param type
	 *            {@link Type}
	 * @return 受影响的行数
	 */
	int add(Type type);

	/**
	 * 根据某个字段值查询该系统类别
	 * 
	 * @param property
	 *            字段名称
	 * @param value
	 *            字段值
	 * @return {@link Type}
	 */
	Type findByUserIdIsNull(String property, Object value);

	/**
	 * 根据某个字段值查询系统和该用户的某个类别
	 * 
	 * @param userId
	 *            用户id
	 * @param property
	 *            字段名称
	 * @param value
	 *            字段值
	 * @return {@link Type}
	 */
	Type findByUserId(int userId, String property, Object value);
}
