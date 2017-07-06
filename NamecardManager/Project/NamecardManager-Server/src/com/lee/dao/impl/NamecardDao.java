package com.lee.dao.impl;

import java.util.List;

import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.lee.dao.INamecardDao;
import com.lee.dbutil.DBUtilsTemplate;
import com.lee.model.Namecard;

/**
 * 名片数据层实现类
 * @author lee
 *
 */
@Repository
public class NamecardDao implements INamecardDao {

	@Autowired
	private DBUtilsTemplate dbutilsTemplate;
	
	@Override
	public List<Namecard> findAll(int pageNum, int userId) {
		String sql = "select id,name_pinyin as namePinyin,name,job_title as jobTitle,company_name as companyName,company_address as companyAddress,mobile,tel,fax,email,web,user_id as userId from nm_namecard where user_id=? order by name_pinyin asc limit ?,10";
		Object[] params = new Object[] {userId, pageNum};
		return dbutilsTemplate.query(sql, new BeanListHandler<Namecard>(Namecard.class), params);
	}

	@Override
	public int add(Namecard namecard) {
		String sql = "insert into nm_namecard(name_pinyin,name,job_title,company_name,company_address,mobile,tel,fax,email,web,user_id) values(?,?,?,?,?,?,?,?,?,?,?)";
		Object[] params = new Object[] { namecard.getNamePinyin(),
				namecard.getName(), namecard.getJobTitle(),
				namecard.getCompanyName(), namecard.getCompanyAddress(),
				namecard.getMobile(), namecard.getTel(), namecard.getFax(),
				namecard.getEmail(), namecard.getWeb(), namecard.getUserId() };
		return dbutilsTemplate.update(sql, params);
	}

	@Override
	public int delete(int id) {
		String sql = "delete from nm_namecard where id=" + id;
		return dbutilsTemplate.update(sql);
	}

	@Override
	public int update(Namecard namecard) {
		String sql = "update nm_namecard set name_pinyin=?,name=?,job_title=?,company_name=?,company_address=?,mobile=?,tel=?,fax=?,email=?,web=?,user_id=? where id=?";
		Object[] params = new Object[] { namecard.getNamePinyin(),
				namecard.getName(), namecard.getJobTitle(),
				namecard.getCompanyName(), namecard.getCompanyAddress(),
				namecard.getMobile(), namecard.getTel(), namecard.getFax(),
				namecard.getEmail(), namecard.getWeb(), namecard.getUserId(),
				namecard.getId() };
		return dbutilsTemplate.update(sql, params);
	}

	@Override
	public List<Namecard> findBySearchedValue(String value, int pageNum, int userId) {
		String sql = "select id,name_pinyin as namePinyin,name,job_title as jobTitle,company_name as companyName,company_address as companyAddress,mobile,tel,fax,email,web,user_id as userId from (select * from nm_namecard where user_id=?) as a where a.name_pinyin like ? or a.name like ? or a.job_title like ? or a.company_name like ? or a.company_address like ? or a.mobile like ? or a.tel like ? or a.fax like ? or a.email like ? or a.web like ? order by a.name_pinyin asc limit ?,10";
		Object[] params = new Object[] { userId, toFuzzyQuery(value),
				"%" + value + "%", "%" + value + "%", "%" + value + "%",
				"%" + value + "%", "%" + value + "%", "%" + value + "%",
				"%" + value + "%", "%" + value + "%", "%" + value + "%",
				pageNum };
		return dbutilsTemplate.query(sql, new BeanListHandler<Namecard>(Namecard.class), params);
	}

	@Override
	public long countFindAll(int userId) {
		String sql = "select count(*) from nm_namecard where user_id=" + userId;
		return dbutilsTemplate.query(sql, new ScalarHandler<Long>());
	}

	@Override
	public long countFindBySearchedValue(String value, int userId) {
		String sql = "select count(*) from (select * from nm_namecard where user_id=?) as a where a.name_pinyin like ? or a.name like ? or a.job_title like ? or a.company_name like ? or a.company_address like ? or a.mobile like ? or a.tel like ? or a.fax like ? or a.email like ? or a.web like ?";
		Object[] params = new Object[] { userId, toFuzzyQuery(value),
				"%" + value + "%", "%" + value + "%", "%" + value + "%",
				"%" + value + "%", "%" + value + "%", "%" + value + "%",
				"%" + value + "%", "%" + value + "%", "%" + value + "%" };
		return dbutilsTemplate.query(sql, new ScalarHandler<Long>(), params);
	}

	/**
	 * 将检索值转换成模糊查询特殊值
	 * 
	 * @param value
	 *            检索值
	 * @return 模糊查询所需要的值
	 */
	private String toFuzzyQuery(String value) {
		String temp = "%";
		int length = value.length();
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				temp = temp + value.substring(i, i + 1) + "%";
			}
		} else {
			temp = temp + value + "%";
		}
		return temp;
	}

}
