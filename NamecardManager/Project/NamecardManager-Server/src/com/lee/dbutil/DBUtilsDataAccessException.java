package com.lee.dbutil;

import org.springframework.dao.DataAccessException;

public class DBUtilsDataAccessException extends DataAccessException {
	
	private static final long serialVersionUID = 1L;

	public DBUtilsDataAccessException(String msg) {
		super(msg);
	}
	
	public DBUtilsDataAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
