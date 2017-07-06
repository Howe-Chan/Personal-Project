package com.lee.dao.impl;

import java.util.List;

import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.lee.dao.ISecurityDao;
import com.lee.dbutil.DBUtilsTemplate;
import com.lee.model.Security;

/**
 * 密保数据层实现类
 * @author lee
 *
 */
@Repository
public class SecurityDao implements ISecurityDao {

	@Autowired
	private DBUtilsTemplate dbutilsTemplate;
	
	@Override
	public List<Security> findAll() {
		String sql = "select * from nm_security";
		return dbutilsTemplate.query(sql, new BeanListHandler<Security>(Security.class));
	}

	@Override
	public Security findById(int id) {
		String sql = "select * from nm_security where id=?";
		return dbutilsTemplate.query(sql, new BeanHandler<Security>(Security.class), id);
	}

}
