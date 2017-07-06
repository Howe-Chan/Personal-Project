package com.lee.service;

import java.util.Date;
import java.util.List;

import com.lee.model.Record;
import com.lee.query.model.QueryMonthAllType;
import com.lee.query.model.QuerySingleType;
import com.lee.query.model.QueryYearAllType;

/**
 * 账目服务层接口
 * @author lee
 *
 */
public interface IRecordService {

	/**
	 * 根据日期查询用户结余
	 * 
	 * @param userId
	 *            用户id
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @return 结余
	 */
	double findSurplusByDate(int userId, Date startDate, Date endDate);

	/**
	 * 根据日期、类别id查询用户结余
	 * 
	 * @param userId
	 *            用户id
	 * @param typeId
	 *            类别id
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @return 结余
	 */
	double findSurplusByTypeAndDate(int userId, int typeId, Date startDate,
			Date endDate);

	/**
	 * 新增账目记录
	 * 
	 * @param record
	 *            {@link Record}
	 * @return 受影响的行数
	 */
	int addRecord(Record record);

	/**
	 * 按年查询所有类别
	 * 
	 * @param userId
	 *            用户id
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @return {@link List<QueryYearAllType>}
	 */
	List<QueryYearAllType> findYearAllType(int userId, Date startDate,
			Date endDate);

	/**
	 * 按年查询单个类别
	 * 
	 * @param userId
	 *            用户id
	 * @param typeId
	 *            类别id
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @return {@link List<QuerySingleType>}
	 */
	List<QuerySingleType> findYearSingleType(int userId, int typeId,
			Date startDate, Date endDate);

	/**
	 * 按月查询所有类别
	 * 
	 * @param userId
	 *            用户id
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @param pageNum
	 *            页码<按每页10行分页>
	 * @return {@link List<QueryMonthAllType>}
	 */
	List<QueryMonthAllType> findMonthAllType(int userId, Date startDate,
			Date endDate, int pageNum);

	/**
	 * 按月查询单个类别
	 * 
	 * @param userId
	 *            用户id
	 * @param typeId
	 *            类别id
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @param pageNum
	 *            页码<按每页10行分页>
	 * @return {@link List<QuerySingleType>}
	 */
	List<QuerySingleType> findMonthSingleType(int userId, int typeId,
			Date startDate, Date endDate, int pageNum);

	/**
	 * 按月查询所有类别的总记录数
	 * 
	 * @param userId
	 *            用户id
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @return 总记录数
	 */
	long countMonthAllType(int userId, Date startDate, Date endDate);

	/**
	 * 按月查询单个类别的总记录数
	 * 
	 * @param userId
	 *            用户id
	 * @param typeId
	 *            类别id
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @return 总记录数
	 */
	long countMonthSingleType(int userId, int typeId, Date startDate,
			Date endDate);

	/**
	 * 根据日期查询用户支出最多的类别
	 * 
	 * @param userId
	 *            用户id
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * @return 类别名称
	 */
	String findTypeByDate(int userId, Date startDate, Date endDate);
}
