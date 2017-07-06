package com.lee.dao;

import java.util.List;

import com.lee.model.Security;

/**
 * 密保数据层接口
 * @author lee
 *
 */
public interface ISecurityDao {

	/**
	 * 获取所有密保问题
	 * @return {@link List<Security>}
	 */
	List<Security> findAll();
	
	/**
	 * 根据id获取对应的密保实体
	 * @param id id
	 * @return {@link Security}
	 */
	Security findById(int id);
}
