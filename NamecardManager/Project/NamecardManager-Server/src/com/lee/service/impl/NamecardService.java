package com.lee.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lee.dao.INamecardDao;
import com.lee.model.Namecard;
import com.lee.service.INamecardService;

/**
 * 名片服务层实现类
 * @author lee
 *
 */
@Service
public class NamecardService implements INamecardService {
	
	@Autowired
	private INamecardDao namecardDao;

	@Override
	public List<Namecard> findAll(int pageNum, int userId) {
		return namecardDao.findAll(pageNum, userId);
	}

	@Override
	public long countFindAll(int userId) {
		return namecardDao.countFindAll(userId);
	}

	@Override
	public int add(Namecard namecard) {
		return namecardDao.add(namecard);
	}

	@Override
	public int delete(int id) {
		return namecardDao.delete(id);
	}

	@Override
	public int update(Namecard namecard) {
		return namecardDao.update(namecard);
	}

	@Override
	public List<Namecard> findBySearchedValue(String value, int pageNum,
			int userId) {
		return namecardDao.findBySearchedValue(value, pageNum, userId);
	}

	@Override
	public long countFindBySearchedValue(String value, int userId) {
		return namecardDao.countFindBySearchedValue(value, userId);
	}

}
