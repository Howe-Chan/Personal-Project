package com.lee.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lee.dao.IUserDao;
import com.lee.model.User;
import com.lee.service.IUserService;

/**
 * 用户服务层实现类
 * @author lee
 *
 */
@Service
public class UserService implements IUserService {
	
	@Autowired
	private IUserDao userDao;

	@Override
	public User findByExample(String property, Object value) {
		return userDao.findByExample(property, value);
	}

	@Override
	public int add(User user) {
		return userDao.add(user);
	}

	@Override
	public int updateExampleById(int id, String property, Object value) {
		return userDao.updateExampleById(id, property, value);
	}

}
