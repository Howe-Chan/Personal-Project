package com.lee.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.lee.model.Record;
import com.lee.model.User;
import com.lee.query.model.QueryMonthAllType;
import com.lee.query.model.QuerySingleType;
import com.lee.query.model.QueryYearAllType;
import com.lee.result.JsonResult;
import com.lee.service.IRecordService;
import com.lee.service.IUserService;
import com.lee.util.DateConvertUtil;
import com.lee.util.QueryDate;

/**
 * 账目操作控制器
 * @author lee
 *
 */
@Controller
@RequestMapping("/record")
public class RecordController {

	/** 日志 **/
    private final static Logger LOGGER = LoggerFactory.getLogger(RecordController.class);

    @Autowired
    private IUserService userService;
    
    @Autowired
    private IRecordService recordService;

    /**
     * 新增账目记录
     * @param userName
     * @param record
     * @return {@link JsonResult}
     */
    @PostMapping("/add")
    @ResponseBody
    public JsonResult addRecord(String userName, Record record) {
    	JsonResult result = new JsonResult();
    	if (StringUtils.isEmpty(userName) || record == null) {
    		result.putSuccess(false, "用户名或账目不能为空！");
		} else {
			User user = null;
			try {
				user = userService.findByExample("name", userName);
			} catch (Exception e) {
				result.putSuccess(false, e.getMessage());
				return result;
			}
			if (user != null) {
				try {
					LOGGER.info(JSONObject.toJSONString(record));
					record.setUserId(user.getId());
					recordService.addRecord(record);
				} catch (Exception e) {
					result.putSuccess(false, e.getMessage());
				}
			} else {
				result.putSuccess(false, "该用户不存在！");
			}
		}
    	return result;
    }

    /**
     * 按年查询所有类型
     * @param userName 用户名
     * @param date 日期
     * @return {@link JsonResult}
     */
    @PostMapping("/query_year_alltype")
    @ResponseBody
    public JsonResult queryYearAllType(String userName, String date) {
    	JsonResult result = new JsonResult();
    	if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(date)) {
    		result.putSuccess(false, "用户名或日期不能为空！");
		} else {
			User user = null;
			try {
				user = userService.findByExample("name", userName);
			} catch (Exception e) {
				result.putSuccess(false, e.getMessage());
				return result;
			}
			if (user != null) {
				QueryDate mDate = DateConvertUtil.getYearByDate(date);
				List<QueryYearAllType> mList = new ArrayList<QueryYearAllType>();
				try {
					mList = recordService.findYearAllType(user.getId(), mDate.getStartDate(), mDate.getEndDate());
				} catch (Exception e) {
					result.putSuccess(false, e.getMessage());
					return result;
				}
				if (mList.size() > 0) {
					double yearSurplus = 0d;
					String yearExpensesMaxType = "";
					try {
						yearSurplus = recordService.findSurplusByDate(user.getId(), mDate.getStartDate(), mDate.getEndDate());
						yearExpensesMaxType = recordService.findTypeByDate(user.getId(), mDate.getStartDate(), mDate.getEndDate());
					} catch (Exception e) {
						result.putSuccess(false, e.getMessage());
						return result;
					}
					result.putRows(mList);
					result.put("yearSurplus", yearSurplus);
					result.put("yearExpensesMaxType", yearExpensesMaxType);
				} else {
					result.putSuccess(false, "不存在该时间段的账目记录！");
				}
			} else {
				result.putSuccess(false, "该用户不存在！");
			}
		}
    	return result;
    }

    /**
     * 按年查询单个类型
     * @param userName 用户名
     * @param date 日期
     * @param typeId 类别id
     * @return {@link JsonResult}
     */
    @PostMapping("/query_year_singletype")
    @ResponseBody
    public JsonResult queryYearSingleType(String userName, String date, int typeId) {
    	JsonResult result = new JsonResult();
    	if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(date)) {
    		result.putSuccess(false, "用户名或日期不能为空！");
		} else {
			User user = null;
			try {
				user = userService.findByExample("name", userName);
			} catch (Exception e) {
				result.putSuccess(false, e.getMessage());
				return result;
			}
			if (user != null) {
				QueryDate mDate = DateConvertUtil.getYearByDate(date);
				List<QuerySingleType> mList = new ArrayList<QuerySingleType>();
				try {
					mList = recordService.findYearSingleType(user.getId(), typeId, mDate.getStartDate(), mDate.getEndDate());
				} catch (Exception e) {
					result.putSuccess(false, e.getMessage());
					return result;
				}
				if (mList.size() > 0) {
					double typeYearSurplus = 0d;
					try {
						typeYearSurplus = recordService.findSurplusByTypeAndDate(user.getId(), typeId, mDate.getStartDate(), mDate.getEndDate());
					} catch (Exception e) {
						result.putSuccess(false, e.getMessage());
						return result;
					}
					result.putRows(mList);
					result.put("typeYearSurplus", typeYearSurplus);
				} else {
					result.putSuccess(false, "不存在该时间段的账目记录！");
				}
			} else {
				result.putSuccess(false, "该用户不存在！");
			}
		}
    	return result;
    }

    /**
     * 按月查询所有类别
     * @param userName 用户名
     * @param date 日期
     * @param pageNum 页码
     * @return {@link JsonResult}
     */
    @PostMapping("/query_month_alltype")
    @ResponseBody
    public JsonResult queryMonthAllType(String userName, String date, int pageNum) {
    	JsonResult result = new JsonResult();
    	if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(date)) {
    		result.putSuccess(false, "用户名或日期不能为空！");
		} else {
			User user = null;
			try {
				user = userService.findByExample("name", userName);
			} catch (Exception e) {
				result.putSuccess(false, e.getMessage());
				return result;
			}
			if (user != null) {
				QueryDate mDate = DateConvertUtil.getMonthByDate(date);
				long count = 0;
				try {
					count = recordService.countMonthAllType(user.getId(), mDate.getStartDate(), mDate.getEndDate());
				} catch (Exception e) {
					result.putSuccess(false, e.getMessage());
					return result;
				}
				if (count > 0) {
					try {
						List<QueryMonthAllType> mList = recordService.findMonthAllType(user.getId(), mDate.getStartDate(), mDate.getEndDate(), (pageNum - 1) * 10);
						double monthSurplus = recordService.findSurplusByDate(user.getId(), mDate.getStartDate(), mDate.getEndDate());
						String monthExpensesMaxType = recordService.findTypeByDate(user.getId(), mDate.getStartDate(), mDate.getEndDate());
						//存放
						result.putRows(mList, (long)Math.ceil(count/10d));
						result.put("monthSurplus", monthSurplus);
						result.put("monthExpensesMaxType", monthExpensesMaxType);
					} catch (Exception e) {
						result.putSuccess(false, e.getMessage());
						return result;
					}
				} else {
					result.putSuccess(false, "不存在该时间段的账目记录！");
				}
			} else {
				result.putSuccess(false, "该用户不存在！");
			}
		}
    	return result;
    }
    
    /**
     * 按月查询单个类别
     * @param userName 用户名
     * @param date 日期
     * @param typeId 类别id
     * @param pageNum 页码
     * @return
     */
    @PostMapping("/query_month_singletype")
    @ResponseBody
    public JsonResult queryMonthSingleType(String userName, String date, int typeId, int pageNum) {
    	JsonResult result = new JsonResult();
    	if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(date)) {
    		result.putSuccess(false, "用户名或日期不能为空！");
		} else {
			User user = null;
			try {
				user = userService.findByExample("name", userName);
			} catch (Exception e) {
				result.putSuccess(false, e.getMessage());
				return result;
			}
			if (user != null) {
				QueryDate mDate = DateConvertUtil.getMonthByDate(date);
				long count = 0;
				try {
					count = recordService.countMonthAllType(user.getId(), mDate.getStartDate(), mDate.getEndDate());
				} catch (Exception e) {
					result.putSuccess(false, e.getMessage());
					return result;
				}
				if (count > 0) {
					try {
						List<QuerySingleType> mList = recordService.findMonthSingleType(user.getId(), typeId, mDate.getStartDate(), mDate.getEndDate(), (pageNum - 1) * 10);
						double typeMonthSurplus = recordService.findSurplusByTypeAndDate(user.getId(), typeId, mDate.getStartDate(), mDate.getEndDate());
						//存放
						result.putRows(mList, (long)Math.ceil(count/10d));
						result.put("typeMonthSurplus", typeMonthSurplus);
					} catch (Exception e) {
						result.putSuccess(false, e.getMessage());
						return result;
					}
				} else {
					result.putSuccess(false, "不存在该时间段的账目记录！");
				}
			} else {
				result.putSuccess(false, "该用户不存在！");
			}
		}
    	return result;
    }
}
