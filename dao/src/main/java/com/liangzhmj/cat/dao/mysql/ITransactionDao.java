package com.liangzhmj.cat.dao.mysql;

import java.sql.SQLException;

public interface ITransactionDao extends APIDao{

	void openTransation() throws SQLException;
	void submitTransation() throws SQLException;
	void setSavepoint() throws SQLException;
	void rollback() throws SQLException;
	void destroy();
}
