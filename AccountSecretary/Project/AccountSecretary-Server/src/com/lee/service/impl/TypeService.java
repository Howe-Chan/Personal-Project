package com.lee.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lee.dao.IRecordDao;
import com.lee.dao.ITypeDao;
import com.lee.model.Type;
import com.lee.service.ITypeService;

/**
 * 类别服务层接口
 * @author lee
 *
 */
@Service
public class TypeService implements ITypeService {
	
	@Autowired
	private ITypeDao typeDao;

	@Autowired
	private IRecordDao recordDao;

	@Override
	public List<Type> findByUserId(int userId) {
		return typeDao.findByUserId(userId);
	}

	@Transactional
	@Override
	public int deleteByIdAndUserId(int id, int userId) {
		Type type = typeDao.findByUserIdIsNull("name", "其他");
		recordDao.updateTypeByUserId(userId, id, type.getId());
		return typeDao.deleteByIdAndUserId(id, userId);
	}

	@Override
	public int add(Type type) {
		return typeDao.add(type);
	}

	@Override
	public Type findByUserId(int userId, String property, Object value) {
		return typeDao.findByUserId(userId, property, value);
	}

}
