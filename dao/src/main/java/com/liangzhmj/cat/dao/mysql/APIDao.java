package com.liangzhmj.cat.dao.mysql;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * 对dao层简单封装
 * @author liangzh
 *
 */
public interface APIDao {

	/**
	 * 保存entity中的非空属性到table表中（entity中的属性名和table中的字段名和类型必须对应，暂时只支持数字和字符串）
	 * @param entity
	 * @param table
	 * @throws Exception
	 */
	void saveEntity(Object entity, String table)throws Exception ;
	int insertEntity(Object entity, String table)throws Exception ;

	/**
	 * 执行sql返回更新记录数(一般执行DDL)
	 * @param sql
	 * @return
	 */
	void executeSQL(String sql);
	/**
	 * 执行sql返回更新记录数
	 * @param sql
	 * @return
	 */
	int updateSQL(String sql);
	/**
	 * 插入sql并返回自增ID
	 * @param sql
	 * @return
	 */
	int insertSQL(String sql);
	/**
	 * 批量执行sql
	 * @param sqls
	 * @throws Exception
	 */
	void executeBatch(List<String> sqls) throws Exception;
	/**
	 * 查询多个字段和多条记录返回对象数组列表
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	List<Map<String,Object>> getMapList(String sql) throws Exception;
	/**
	 * 查询多个字段和多条记录返回对象数组列表
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	List<Object[]> getObjectList(String sql) throws Exception;
	@SuppressWarnings("rawtypes")
	List getList(String sql) throws Exception;
	/**
	 * 查询多个字段单条记录返回对象数组
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	Object[] getObjects(String sql) throws Exception;
	/**
	 * 查询多个字段单条记录返回对象数组
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> getMap(String sql) throws Exception;
	/**
	 * 单个字段单条记录返回对象数组
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	Object getObject(String sql) throws Exception;
	<T> T getObject(String sql, Class<T> clazz) throws Exception;
	/**
	 * 获取对象
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	long getRecords(String sql) throws Exception;
	/**
	 * 获取实体(属性类型名称必须要和字段类型名称对应)
	 * @param clazz
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	<T> T getEntity(Class<T> clazz, String sql) throws Exception;
	/**
	 * 获取实体列表(属性类型名称必须要和字段类型名称对应)
	 * @param <T>
	 * @param clazz
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	<T> List<T> getEntities(Class<T> clazz, String sql) throws Exception;
	/**
	 * 根据sql建表
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	boolean createTable(String sql)throws Exception;
	DataSource getDataDource();
}
