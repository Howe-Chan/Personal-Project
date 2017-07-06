package com.lee.dao.impl;

import java.util.List;

import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.lee.dao.ITypeDao;
import com.lee.dbutil.DBUtilsTemplate;
import com.lee.model.Type;

/**
 * 类别数据层实现类
 * @author lee
 *
 */
@Repository
public class TypeDao implements ITypeDao {

	@Autowired
	private DBUtilsTemplate dbutilsTemplate;

	@Override
	public List<Type> findByUserId(int userId) {
		String sql = "select id,name,user_id as userId from as_type where user_id=? or user_id is null";
		return dbutilsTemplate.query(sql, new BeanListHandler<Type>(Type.class), userId);
	}

	@Override
	public int deleteByIdAndUserId(int id, int userId) {
		String sql = "delete from as_type where id=? and user_id=?";
		Object[] params = new Object[] { id, userId };
		return dbutilsTemplate.update(sql, params);
	}

	@Override
	public int add(Type type) {
		String sql = "insert into as_type(name,user_id) values(?,?)";
		Object[] params = new Object[] { type.getName(), type.getUserId() };
		return dbutilsTemplate.update(sql, params);
	}

	@Override
	public Type findByUserIdIsNull(String property, Object value) {
		String sql = "select id,name,user_id as userId from as_type where user_id is null and " + property + "=?";
		return dbutilsTemplate.query(sql, new BeanHandler<Type>(Type.class), value);
	}

	@Override
	public Type findByUserId(int userId, String property, Object value) {
		String sql = "select a.id,a.name,a.user_id as userId from (select * from as_type where user_id is null or user_id=?) a where a." + property + "=?";
		Object[] params = new Object[] { userId, value };
		return dbutilsTemplate.query(sql, new BeanHandler<Type>(Type.class), params);
	}

}
