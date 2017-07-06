package com.lee.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lee.dao.IRecordDao;
import com.lee.model.Record;
import com.lee.query.model.QueryMonthAllType;
import com.lee.query.model.QuerySingleType;
import com.lee.query.model.QueryYearAllType;
import com.lee.service.IRecordService;

/**
 * 账目服务层实现类
 * @author lee
 *
 */
@Service
public class RecordService implements IRecordService {
	
	@Autowired
	private IRecordDao recordDao;

	@Override
	public double findSurplusByDate(int userId, Date startDate, Date endDate) {
		return recordDao.findSurplusByDate(userId, startDate, endDate);
	}

	@Override
	public double findSurplusByTypeAndDate(int userId, int typeId,
			Date startDate, Date endDate) {
		return recordDao.findSurplusByTypeAndDate(userId, typeId, startDate, endDate);
	}

	@Override
	public int addRecord(Record record) {
		return recordDao.addRecord(record);
	}

	@Override
	public List<QueryYearAllType> findYearAllType(int userId, Date startDate,
			Date endDate) {
		return recordDao.findYearAllType(userId, startDate, endDate);
	}

	@Override
	public List<QuerySingleType> findYearSingleType(int userId, int typeId,
			Date startDate, Date endDate) {
		return recordDao.findYearSingleType(userId, typeId, startDate, endDate);
	}

	@Override
	public List<QueryMonthAllType> findMonthAllType(int userId, Date startDate,
			Date endDate, int pageNum) {
		return recordDao.findMonthAllType(userId, startDate, endDate, pageNum);
	}

	@Override
	public List<QuerySingleType> findMonthSingleType(int userId, int typeId,
			Date startDate, Date endDate, int pageNum) {
		return recordDao.findMonthSingleType(userId, typeId, startDate, endDate, pageNum);
	}

	@Override
	public long countMonthAllType(int userId, Date startDate, Date endDate) {
		return recordDao.countMonthAllType(userId, startDate, endDate);
	}

	@Override
	public long countMonthSingleType(int userId, int typeId, Date startDate,
			Date endDate) {
		return recordDao.countMonthSingleType(userId, typeId, startDate, endDate);
	}

	@Override
	public String findTypeByDate(int userId, Date startDate, Date endDate) {
		return recordDao.findTypeByDate(userId, startDate, endDate);
	}
	
}
