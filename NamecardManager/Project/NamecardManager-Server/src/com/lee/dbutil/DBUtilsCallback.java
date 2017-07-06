package com.lee.dbutil;

import java.sql.Connection;
import java.sql.SQLException;

public interface DBUtilsCallback<T> {
	T execute(Connection connection) throws SQLException;
}
