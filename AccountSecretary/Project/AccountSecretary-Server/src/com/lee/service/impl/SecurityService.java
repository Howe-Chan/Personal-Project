package com.lee.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lee.dao.ISecurityDao;
import com.lee.model.Security;
import com.lee.service.ISecurityService;

/**
 * 密保数据层实现类
 * @author lee
 *
 */
@Service
public class SecurityService implements ISecurityService{
	
	@Autowired
	private ISecurityDao securityDao;

	@Override
	public List<Security> findAll() {
		return securityDao.findAll();
	}

	@Override
	public Security findById(int id) {
		return securityDao.findById(id);
	}

}
