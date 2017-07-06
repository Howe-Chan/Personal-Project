package com.lee.dao.impl;

import org.apache.commons.dbutils.handlers.BeanHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.lee.dao.IUserDao;
import com.lee.dbutil.DBUtilsTemplate;
import com.lee.model.User;
import com.lee.util.MD5Util;

/**
 * 用户数据层实现类
 * @author lee
 *
 */
@Repository
public class UserDao implements IUserDao {

	@Autowired
	private DBUtilsTemplate dbutilsTemplate;

	@Override
	public User findByExample(String property, Object value) {
		String sql = "select id,name,icon_id as iconId,password,security_answer as securityAnswer,security_id as securityId from as_user where " + property + "=?";
		return dbutilsTemplate.query(sql, new BeanHandler<User>(User.class), value);
	}

	@Override
	public int add(User user) {
		String sql = "insert into as_user(name,icon_id,password,security_answer,security_id) values(?,?,?,?,?)";
		Object[] params = new Object[] { user.getName(), user.getIconId(),
				MD5Util.GetMD5Code(user.getPassword()),
				user.getSecurityAnswer(), user.getSecurityId() };
		return dbutilsTemplate.update(sql, params);
	}

	@Override
	public int updateExampleById(int id, String property, Object value) {
		String sql = "update as_user set " + property + "=? where id=?";
		Object[] params = new Object[] { id, value };
		return dbutilsTemplate.update(sql, params);
	}
}
