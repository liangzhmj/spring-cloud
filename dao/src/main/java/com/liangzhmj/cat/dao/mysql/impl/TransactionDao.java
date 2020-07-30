package com.liangzhmj.cat.dao.mysql.impl;

import com.liangzhmj.cat.dao.mysql.APIDao;
import com.liangzhmj.cat.dao.mysql.ITransactionDao;
import com.liangzhmj.cat.dao.mysql.annotation.DBcolumn;
import com.liangzhmj.cat.dao.mysql.utils.DBUtils;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.Date;
import java.util.*;

/**
 * 处理事务Dao(数据库因此要求是InnerDB)
 * 
 * @author liangzhmj
 *
 */
@Log4j2
public class TransactionDao implements ITransactionDao {

	private Connection cn;
	private APIDao apiDao;
	private Savepoint savepoint;

	public TransactionDao(APIDao apiDao) {
		this.apiDao = apiDao;
		cn = DataSourceUtils.getConnection(apiDao.getDataDource());// 从连接池获取连接
	}

	/**
	 * 打开事务
	 * 
	 * @throws SQLException
	 */
	@Override
	public void openTransation() throws SQLException {
		cn.setAutoCommit(false);
	}

	/**
	 * 设置保存点(保存点之前的数据都会被保存)
	 * 
	 * @throws SQLException
	 */
	@Override
	public void setSavepoint() throws SQLException {
		savepoint = cn.setSavepoint();
	}
	
	/**
	 * 提交事务
	 * @throws SQLException
	 */
	@Override
	public void submitTransation() throws SQLException{
		cn.commit();
	}

	/**
	 * 回滚数据
	 * @throws SQLException
	 */
	@Override
	public void rollback() throws SQLException {
		if (savepoint != null) { // CD异常
			cn.rollback(savepoint);
			cn.commit();
		} else { // AB异常
			cn.rollback();
		}

	}

	/**
	 * 销毁该事务dao
	 */
	@Override
	public void destroy() {
		if (cn != null) {
			try {
				cn.setAutoCommit(true);// 还原该链接的自动提交
			} catch (SQLException e) {
				log.debug(e.getMessage());
				try {
					if (cn.getAutoCommit()) {
						log.warn("该连接已经是autoCommit=true无需重复设置");
					} else {
						log.fatal("该连接设置autoCommit=true失败");
					}
				} catch (SQLException e1) {
					log.error("该连接检查autoCommit失败：" + e.getMessage());
				}
			}
			try {
				DataSourceUtils.releaseConnection(cn, apiDao.getDataDource());// 归还连接给连接池
			} catch (Exception ex) {
				log.error("连接归还连接池失败:" + ex.getMessage());
			}
		}
	}

	@Override
	public void saveEntity(Object entity, String table) throws Exception {
		String sql = genervateEntitySql(entity, table);
		if(StringUtils.isEmpty(sql)){
			return;
		}
		updateSQL(sql);
	}

	@Override
	public int insertEntity(Object entity, String table) throws Exception {
		String sql = genervateEntitySql(entity, table);
		return insertSQL(sql);
	}

	@Override
	public void executeSQL(String sql) {
		Statement stmt = null;
		try {
			stmt = cn.createStatement();
			stmt.execute(sql);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());//抛到外面处理
		} finally{
			if(stmt != null){
				try {
					stmt.close();
				} catch (SQLException e) {
					log.error(e);
				}
			}
		}
	}

	@Override
	public int updateSQL(String sql) {
		Statement stmt = null;
		int res = 0;
		try {
			stmt = cn.createStatement();
			res = stmt.executeUpdate(sql);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());//抛到外面处理
		} finally{
			if(stmt != null){
				try {
					stmt.close();
				} catch (SQLException e) {
					log.error(e);
				}
			}
		}
		return res;
	}

	@Override
	public int insertSQL(String sql) {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = cn.createStatement(); 
			stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			rs = stmt.getGeneratedKeys();  
		     if (rs.next()) {  
		        return rs.getInt(1);  
		     } 
		}catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		} finally{
			if(rs!=null) {
				try {
					rs.close();
				}catch(Exception ex){}
			}
			if(stmt!=null) {
				try {
					stmt.close();
				}catch(Exception ex){}
			}
		}
		return 0;
	}

	@Override
	public List<Object[]> getObjectList(String sql) throws Exception {
		PreparedStatement ps = null;
	    ResultSet rs = null;
	    List<Object[]> objList = null;
		try {
			ps = cn.prepareStatement(sql);
			rs = ps.executeQuery();
			ResultSetMetaData md = rs.getMetaData(); //得到结果集(rs)的结构信息，比如字段数、字段名等   
			int columnCount = md.getColumnCount(); //返回此 ResultSet 对象中的列数
			objList = new ArrayList<Object[]>();
			while(rs.next()) {
				Object[] objs = new Object[columnCount];
				for (int i = 1; i <= columnCount; i++) {   
					objs[i-1] = rs.getObject(i);   
				}
				objList.add(objs);
			}
		} catch (Exception e) {
			log.error(e);
		} finally{
			try {
				rs.close();
			} catch (Exception e) {
				log.error(e);
			}
			try {
				ps.close();
			} catch (Exception e) {
				log.error(e);
			}
		}
	    return objList;
	}

	@Override
	public List<Map<String,Object>> getMapList(String sql) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Map<String,Object>> mapList = null;
		try {
			ps = cn.prepareStatement(sql);
			rs = ps.executeQuery();
			ResultSetMetaData md = rs.getMetaData(); //得到结果集(rs)的结构信息，比如字段数、字段名等
			int columnCount = md.getColumnCount(); //返回此 ResultSet 对象中的列数
			mapList = new ArrayList<Map<String,Object>>();
			while(rs.next()) {
				Map<String,Object> map = new HashMap<>();
				for (int i = 1; i <= columnCount; i++) {
					Object val = rs.getObject(i);
					String key = md.getCatalogName(i);
					map.put(key,val);
				}
				mapList.add(map);
			}
		} catch (Exception e) {
			log.error(e);
		} finally{
			try {
				rs.close();
			} catch (Exception e) {
				log.error(e);
			}
			try {
				ps.close();
			} catch (Exception e) {
				log.error(e);
			}
		}
		return mapList;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List getList(String sql) throws Exception {
	    List list = new ArrayList();
	    PreparedStatement ps = null;
	    ResultSet rs = null;
		try {
			ps = cn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()) {   
				list.add(rs.getObject(1));
			}
		} catch (Exception e) {
			log.error(e);
		} finally{
			try {
				rs.close();
			} catch (Exception e) {
				log.error(e);
			}
			try {
				ps.close();
			} catch (Exception e) {
				log.error(e);
			}
		}
	    return list;
	}

	@Override
	public Object[] getObjects(String sql) throws Exception {
		PreparedStatement ps = null;
	    ResultSet rs = null;
	    Object[] objs = null;
		try {
			ps = cn.prepareStatement(sql);
			rs = ps.executeQuery();
			ResultSetMetaData md = rs.getMetaData(); //得到结果集(rs)的结构信息，比如字段数、字段名等   
			int columnCount = md.getColumnCount(); //返回此 ResultSet 对象中的列数   
			if(rs.next()) {   
				objs = new Object[columnCount];
				for (int i = 1; i <= columnCount; i++) {   
					objs[i-1] = rs.getObject(i);   
				}
			}
		} catch (Exception e) {
			log.error(e);
		} finally{
			try {
				rs.close();
			} catch (Exception e) {
				log.error(e);
			}
			try {
				ps.close();
			} catch (Exception e) {
				log.error(e);
			}
		}
		return objs;
	}

	@Override
	public Map<String,Object> getMap(String sql) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map<String,Object> map = null;
		try {
			ps = cn.prepareStatement(sql);
			rs = ps.executeQuery();
			ResultSetMetaData md = rs.getMetaData(); //得到结果集(rs)的结构信息，比如字段数、字段名等
			int columnCount = md.getColumnCount(); //返回此 ResultSet 对象中的列数
			if(rs.next()) {
				map = new HashMap<>();
				for (int i = 1; i <= columnCount; i++) {
					String key = md.getCatalogName(i);
					Object val = rs.getObject(i);
					map.put(key,val);
				}
			}
		} catch (Exception e) {
			log.error(e);
		} finally{
			try {
				rs.close();
			} catch (Exception e) {
				log.error(e);
			}
			try {
				ps.close();
			} catch (Exception e) {
				log.error(e);
			}
		}
		return map;
	}

	@Override
	public Object getObject(String sql) throws Exception {
		PreparedStatement ps = null;
	    ResultSet rs = null;
		try {
			ps = cn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()) {   
				return rs.getObject(1);   
			}
		} catch (Exception e) {
			log.error(e);
		} finally{
			try {
				rs.close();
			} catch (Exception e) {
				log.error(e);
			}
			try {
				ps.close();
			} catch (Exception e) {
				log.error(e);
			}
		}
		return null;
	}

	@Override
	public <T> T getObject(String sql, Class<T> clazz) throws Exception {
		return getEntity(clazz, sql);
	}

	@Override
	public long getRecords(String sql) throws Exception {
		Object obj = getObject(sql);
		if(obj == null){
			return 0;
		}
		return (Long)obj;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getEntity(Class<T> clazz, String sql) throws Exception {
		PreparedStatement ps = null;
	    ResultSet rs = null;
		try {
			ps = cn.prepareStatement(sql);
			rs = ps.executeQuery();
			Object obj = null;
			if (rs.next()) {
				try {
					obj = getT(clazz, rs);
				} catch (Exception e) {
					log.error(e);
					obj = null;
				}
			}
			return obj == null ? null : (T)obj;
		} catch (Exception e) {
			log.error(e);
		} finally{
			try {
				rs.close();
			} catch (Exception e) {
				log.error(e);
			}
			try {
				ps.close();
			} catch (Exception e) {
				log.error(e);
			}
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> List<T> getEntities(Class<T> clazz, String sql) throws Exception {
		List list = new ArrayList();
	    PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = cn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
			  Object obj = getT(clazz, rs);
			  list.add(obj);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally{
			try {
				rs.close();
			} catch (Exception e) {
				log.error(e);
			}
			try {
				ps.close();
			} catch (Exception e) {
				log.error(e);
			}
		}
	    return list;
	}

	
	@Override
	public void executeBatch(List<String> sqls) throws Exception {
		if (CollectionUtils.isEmpty(sqls)) {
			return ;
		}
		Statement stmt = null;
		try {
			stmt = cn.createStatement();
			for (String sql : sqls) {
				stmt.addBatch(sql);
			}
			stmt.executeBatch(); // 执行批处理
			stmt.clearBatch();
		} catch (Exception e) {
			try {
				if (cn != null)
					cn.rollback();
			} catch (Exception ee) {
			}
			throw e;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception ex) {
				}
			}
		}
	}
	
	@Override
	public boolean createTable(String sql) throws Exception {
		executeSQL(sql);
		return true;
	}

	@Override
	public DataSource getDataDource() {
		throw new RuntimeException("暂不支持该方法");
	}

	
	/**
	 * 构建实体sql
	 * @param entity
	 * @param table
	 * @return
	 */
	private String genervateEntitySql(Object entity, String table){
		if(entity == null || StringUtils.isEmpty(table)){
			log.error("数据不合法,entity = " + entity + " , table = " + table);
			return null;
		}
		Field[] fs = entity.getClass().getDeclaredFields();
		if(fs == null || fs.length == 0){
			log.error("entity没有属性"+entity);
			return null;
		}
		
		StringBuilder names = new StringBuilder();
		StringBuilder values = new StringBuilder();
		for (Field field : fs) {
			DBcolumn cf = field.getAnnotation(DBcolumn.class);
			//主键自动生成
			if(cf == null || cf.pk()){continue;}
			String name = cf.name();
			name = !StringUtils.isEmpty(name)?name:field.getName();
			try {
				String value = BeanUtils.getProperty(entity, name);
				if(!StringUtils.isEmpty(value)){
					names.append(name).append(",");
					values.append("'"+ DBUtils.mysql_varchar_escape(value)+"'").append(",");
				}
			} catch (Exception e) {
				log.error("获取属性name["+name+"]失败,values = " + values,e);
			}
		}
		String nameStr = null;
		String valueStr = null;
		if(names.length() > 0 && values.length() > 0){
			nameStr = names.substring(0, names.length()-1);
			valueStr = values.substring(0, values.length()-1);
		}
		if(StringUtils.isEmpty(nameStr) || StringUtils.isEmpty(valueStr)){
			log.error("没有属性要记录的"+entity);
			return null;
		}
		String temp = "INSERT INTO "+table+"("+nameStr+") VALUES("+valueStr+")"; 
		log.debug("entity-->sql:"+temp);
		return temp;
	}
	
	/**
	 * 通过ResultSet给实体赋值
	 * @param clazz
	 * @param resultSet
	 * @return
	 * @throws Exception
	 */
	private Object getT(Class<?> clazz, ResultSet resultSet) throws Exception {
		Object obj = clazz.newInstance();
		Field[] fs = clazz.getDeclaredFields();
		if(fs == null || fs.length == 0){
			log.error("entity没有属性");
			return obj;
		}
		
		for (Field field : fs) {
			try {
				DBcolumn cf = field.getAnnotation(DBcolumn.class);
				if(cf == null){continue;}
				String name = cf.name();
				name = !StringUtils.isEmpty(name)?name:field.getName();
				if ("java.lang.String".equals(field.getType().getCanonicalName())) {
					BeanUtils.setProperty(obj, field.getName(), resultSet.getString(name));
				} else if (("int".equals(field.getType().getCanonicalName()))
						|| ("java.lang.Integer".equals(field.getType().getCanonicalName()))) {
					BeanUtils.setProperty(obj, field.getName(), resultSet.getInt(name));
				} else if (("boolean".equals(field.getType().getCanonicalName()))
						|| ("java.lang.Boolean".equals(field.getType().getCanonicalName()))) {
					BeanUtils.setProperty(obj, field.getName(), resultSet.getBoolean(name));
				} else if ("java.util.Date".equals(field.getType().getCanonicalName())) {
					Timestamp time = resultSet.getTimestamp(name);
					if (time != null)
					BeanUtils.setProperty(obj, field.getName(), new Date(time.getTime()));
				} else if (("long".equals(field.getType().getCanonicalName()))
						|| ("java.lang.Long".equals(field.getType().getCanonicalName()))) {
					BeanUtils.setProperty(obj, field.getName(), resultSet.getLong(name));
				} else if (("float".equals(field.getType().getCanonicalName()))
						|| ("java.lang.Float".equals(field.getType().getCanonicalName()))) {
					BeanUtils.setProperty(obj, field.getName(), resultSet.getFloat(name));
				}
			} catch (Exception e) {
			}
		}
	    return obj;
    }
}
