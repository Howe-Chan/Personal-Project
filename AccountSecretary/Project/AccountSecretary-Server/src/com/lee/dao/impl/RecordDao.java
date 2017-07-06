package com.lee.dao.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.lee.dao.IRecordDao;
import com.lee.dbutil.DBUtilsTemplate;
import com.lee.model.Record;
import com.lee.query.model.QueryMonthAllType;
import com.lee.query.model.QuerySingleType;
import com.lee.query.model.QueryYearAllType;

/**
 * 账目数据层实现类
 * @author lee
 *
 */
@Repository
public class RecordDao implements IRecordDao {

	@Autowired
	private DBUtilsTemplate dbutilsTemplate;

	@Override
	public int updateTypeByUserId(int userId, int typeOldId, int typeNewId) {
		String sql = "update as_record set type_id=? where user_id=? and type_id=?";
		Object[] params = new Object[] { typeNewId, userId, typeOldId };
		return dbutilsTemplate.update(sql, params);
	}

	@Override
	public double findSurplusByDate(int userId, Date startDate, Date endDate) {
		String sql = "select ifnull("
				+ "(select ifnull(sum(money),0) as money from as_record where user_id=? and datetime>? and datetime<? and is_income = 1"
				+ ")-(select ifnull(sum(money),0) as money from as_record where user_id=? and datetime>? and datetime<? and is_income = 0"
				+ "),0) as surplus";
		Object[] params = new Object[] { userId, startDate, endDate, userId,
				startDate, endDate };
		return dbutilsTemplate.query(sql, new ScalarHandler<BigDecimal>(), params).doubleValue();
	}

	@Override
	public double findSurplusByTypeAndDate(int userId, int typeId,
			Date startDate, Date endDate) {
		String sql = "select ifnull("
				+ "(select ifnull(sum(money),0) as money from as_record where user_id=? and type_id=? and datetime>? and datetime<? and is_income = 1"
				+ ")-(select ifnull(sum(money),0) as money from as_record where user_id=? and type_id=? and datetime>? and datetime<? and is_income = 0"
				+ "),0) as surplus";
		Object[] params = new Object[] { userId, typeId, startDate, endDate,
				userId, typeId, startDate, endDate };
		return dbutilsTemplate.query(sql, new ScalarHandler<BigDecimal>(), params).doubleValue();
	}

	@Override
	public int addRecord(Record record) {
		String sql = "insert into as_record(name,datetime,is_income,money,type_id,user_id) values(?,?,?,?,?,?)";
		Object[] params = new Object[] { record.getName(),
				record.getDatetime(), record.isIncome(), record.getMoney(),
				record.getTypeId(), record.getUserId() };
		return dbutilsTemplate.update(sql, params);
	}

	@Override
	public List<QueryYearAllType> findYearAllType(int userId, Date startDate,
			Date endDate) {
		String sql = "select mond.months as date,ifnull(sr.money,0) as incomeMoney,ifnull(zc.money,0) as expensesMoney,ifnull(sr.money,0)-ifnull(zc.money,0) as monthSurplus from ("
				+ "select  date_format(datetime,'%Y/%m') as months from as_record where user_id=? and datetime>? and datetime<? group by months"
				+ ")as mond left join ("
				+ "select date_format(datetime,'%Y/%m') as months,is_income as type,sum(money) as money from as_record where user_id=? and datetime>? and datetime<? and is_income = 1 group by months"
				+ ")as sr on mond.months=sr.months left join ("
				+ "select date_format(datetime,'%Y/%m') as months,is_income as type,sum(money) as money from as_record where user_id=? and datetime>? and datetime<? and is_income = 0 group by months"
				+ ")as zc on mond.months=zc.months order by mond.months";
		Object[] params = new Object[] { userId, startDate, endDate, userId,
				startDate, endDate, userId, startDate, endDate };
		return dbutilsTemplate.query(sql, new BeanListHandler<QueryYearAllType>(QueryYearAllType.class), params);
	}

	@Override
	public List<QuerySingleType> findYearSingleType(int userId, int typeId,
			Date startDate, Date endDate) {
		String sql = "select mond.months as date,mond.tname as type,ifnull(sr.money,0)-ifnull(zc.money,0) as surplus from ("
				+ "select date_format(tr.datetime,'%Y/%m') as months,tt.name as tname from as_record tr,as_type tt where tr.type_id=tt.id and tr.user_id=? and tr.type_id=? and tr.datetime>? and tr.datetime<? group by months"
				+ ")as mond left join ("
				+ "select date_format(datetime,'%Y/%m') as months,is_income as type,sum(money) as money from as_record where user_id=? and type_id=? and datetime>? and datetime<? and is_income = 1 group by months"
				+ ")as sr on mond.months=sr.months left join ("
				+ "select date_format(datetime,'%Y/%m') as months,is_income as type,sum(money) as money from as_record where user_id=? and type_id=? and datetime>? and datetime<? and is_income = 0 group by months"
				+ ")as zc on mond.months=zc.months order by mond.months";
		Object[] params = new Object[] { userId, typeId, startDate, endDate, userId, typeId,
				startDate, endDate, userId, typeId, startDate, endDate };
		return dbutilsTemplate.query(sql, new BeanListHandler<QuerySingleType>(QuerySingleType.class), params);
	}

	@Override
	public List<QueryMonthAllType> findMonthAllType(int userId,
			Date startDate, Date endDate, int pageNum) {
		String sql = "select date_format(tr.datetime,'%Y/%m/%d %H:%i') as date,tr.name as name,tt.name as type,if(tr.is_income=0, '支出', '收入') as incomeExpenses,tr.money as money "
				+ "from as_record tr,as_type tt where tr.type_id=tt.id and tr.user_id=? and datetime>? and datetime<? order by tr.datetime limit ?,10";
		Object[] params = new Object[] { userId, startDate, endDate, pageNum };
		return dbutilsTemplate.query(sql, new BeanListHandler<QueryMonthAllType>(QueryMonthAllType.class), params);
	}

	@Override
	public List<QuerySingleType> findMonthSingleType(int userId, int typeId,
			Date startDate, Date endDate, int pageNum) {
		String sql = "select mond.months as date,mond.tname as type,ifnull(sr.money,0)-ifnull(zc.money,0) as surplus from ("
				+ "select  date_format(tr.datetime,'%Y/%m/%d') as months,tt.name as tname from as_record tr,as_type tt where tr.type_id=tt.id and tr.user_id=? and tr.type_id=? and tr.datetime>? and tr.datetime<? group by months"
				+ ")as mond left join ("
				+ "select date_format(datetime,'%Y/%m/%d') as months,is_income as type,sum(money) as money from as_record where user_id=? and type_id=? and datetime>? and datetime<? and is_income = 1 group by months"
				+ ")as sr on mond.months=sr.months left join ("
				+ "select date_format(datetime,'%Y/%m/%d') as months,is_income as type,sum(money) as money from as_record where user_id=? and type_id=? and datetime>? and datetime<? and is_income = 0 group by months"
				+ ")as zc on mond.months=zc.months order by mond.months limit ?,10";
		Object[] params = new Object[] { userId, typeId, startDate, endDate, userId, typeId,
				startDate, endDate, userId, typeId, startDate, endDate, pageNum };
		return dbutilsTemplate.query(sql, new BeanListHandler<QuerySingleType>(QuerySingleType.class), params);
	}

	@Override
	public long countMonthAllType(int userId, Date startDate, Date endDate) {
		String sql = "select count(*) "
				+ "from as_record tr,as_type tt where tr.type_id=tt.id and tr.user_id=? and datetime>? and datetime<?";
		Object[] params = new Object[] { userId, startDate, endDate };
		return dbutilsTemplate.query(sql, new ScalarHandler<Long>(), params);
	}

	@Override
	public long countMonthSingleType(int userId, int typeId, Date startDate,
			Date endDate) {
		String sql = "select count(*) from ("
				+ "select  date_format(tr.datetime,'%Y/%m/%d') as months,tt.name as tname from as_record tr,as_type tt where tr.type_id=tt.id and tr.user_id=? and tr.type_id=? and tr.datetime>? and tr.datetime<? group by months"
				+ ")as mond left join ("
				+ "select date_format(datetime,'%Y/%m/%d') as months,is_income as type,sum(money) as money from as_record where user_id=? and type_id=? and datetime>? and datetime<? and is_income = 1 group by months"
				+ ")as sr on mond.months=sr.months left join ("
				+ "select date_format(datetime,'%Y/%m/%d') as months,is_income as type,sum(money) as money from as_record where user_id=? and type_id=? and datetime>? and datetime<? and is_income = 0 group by months"
				+ ")as zc on mond.months=zc.months order by mond.months";
		Object[] params = new Object[] { userId, typeId, startDate, endDate, userId, typeId,
				startDate, endDate, userId, typeId, startDate, endDate };
		return dbutilsTemplate.query(sql, new ScalarHandler<Long>(), params);
	}

	@Override
	public String findTypeByDate(int userId, Date startDate, Date endDate) {
		String sql = "select tt.name as name from as_record tr,as_type tt where tr.type_id=tt.id and tr.user_id=? and tr.datetime>? and tr.datetime<? and tr.is_income = 0 group by tr.type_id order by money desc limit 1";
		Object[] params = new Object[] { userId, startDate, endDate };
		return dbutilsTemplate.query(sql, new ScalarHandler<String>(), params);
	}

}
