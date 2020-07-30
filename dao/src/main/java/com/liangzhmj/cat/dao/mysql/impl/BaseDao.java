package com.liangzhmj.cat.dao.mysql.impl;

import com.liangzhmj.cat.dao.mysql.APIDao;
import com.liangzhmj.cat.dao.mysql.annotation.DBcolumn;
import com.liangzhmj.cat.dao.mysql.utils.DBUtils;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Log4j2
public class BaseDao implements APIDao {

	private JdbcTemplate jdbcTemplate;
	

	
	@Override
	public void saveEntity(Object entity, String table) throws Exception {
		String sql = genervateEntitySql(entity, table);
		if(!StringUtils.isEmpty(sql)){
			jdbcTemplate.execute(sql);
		}
	}

	@Override
	public int insertEntity(Object entity, String table) throws Exception {
		String sql = genervateEntitySql(entity, table);
		return insertSQL(sql);
	}

	@Override
	public void executeSQL(String sql) {
		log.debug("更新数据，sql--->"+sql);
		jdbcTemplate.execute(sql);
	}
	@Override
	public int updateSQL(String sql) {
		log.debug("更新数据，sql--->"+sql);
		return jdbcTemplate.update(sql);
	}

	@Override
	public int insertSQL(String sql) {
		if(!StringUtils.isEmpty(sql)){
			Connection cn = null;
			Statement stmt = null;
			ResultSet rs = null;
			try {
				cn = DataSourceUtils.getConnection(this.getDataDource());//从连接池获取连接
				stmt = cn.createStatement(); 
				stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
				rs = stmt.getGeneratedKeys();  
			     if (rs.next()) {  
			        return rs.getInt(1);  
			     } 
			}catch (Exception e) {
				log.error(e);
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
				if(cn!=null) {
					try {
						DataSourceUtils.releaseConnection(cn, this.getDataDource());//归还连接给连接池
					}catch(Exception ex){}
				}
			}
		}
		return 0;
	}

	@Override
	public void executeBatch(List<String> sqls) throws Exception {
		if (CollectionUtils.isEmpty(sqls)) {
			return ;
		}
		Connection cn = null;
		Statement stmt = null;
		try {
			cn = DataSourceUtils.getConnection(this.getDataDource());// 从连接池获取连接
			cn.setAutoCommit(false);
			stmt = cn.createStatement();
			for (String sql : sqls) {
				stmt.addBatch(sql);
			}
			stmt.executeBatch(); // 执行批处理
			cn.commit();
			stmt.clearBatch();
		} catch (Exception e) {
			try {
				if (cn != null)
					cn.rollback();
			} catch (Exception ee) {
			}
			throw e;
		} finally {
			try {
				cn.setAutoCommit(true);
			} catch (SQLException e) {
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception ex) {
				}
			}
			if (cn != null) {
				try {
					DataSourceUtils.releaseConnection(cn, this.getDataDource());// 归还连接给连接池
				} catch (Exception ex) {
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getEntity(final Class<T> clazz, String sql) throws Exception {
		log.debug("获取实体sql-->" + sql);
		T entity = jdbcTemplate.query(sql,new ResultSetExtractor<T>() {

			@Override
			public T extractData(ResultSet rs) throws SQLException,
					DataAccessException {
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
			}
			
		});
		return entity;
	 }
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> getEntities(final Class<T> clazz, String sql)
			throws Exception {
		log.debug("获取实体列表sql-->" + sql);
		List<T> entities = jdbcTemplate.query(sql,new RowMapper<T>() {
			@Override
			public T mapRow(ResultSet rs, int rowNum) throws SQLException {
				Object obj = null;
				try {
					obj = getT(clazz, rs);
				} catch (Exception e) {
					log.error(e);
					obj = null;
				}
				return obj == null ? null : (T)obj;
			}
			
		});
		return entities;
	}

	@Override
	public Object[] getObjects(String sql) throws Exception {
		
		log.debug("获取多字段单条记录sql-->" + sql);
		
		Object[] infos = jdbcTemplate.query(sql, new ResultSetExtractor<Object[]>(){
			@Override
			public Object[] extractData(ResultSet rs) throws SQLException,
					DataAccessException {
				ResultSetMetaData md = rs.getMetaData(); //得到结果集(rs)的结构信息，比如字段数、字段名等   
		        int columnCount = md.getColumnCount(); //返回此 ResultSet 对象中的列数   
	          	if(rs.next()) {   
	          		Object[] objs = new Object[columnCount];
		            for (int i = 1; i <= columnCount; i++) {   
		               objs[i-1] = rs.getObject(i);   
		            } 
		            return objs;
	          	}
	          	return null;
			}
			
		});
		return infos;
	}

	@Override
	public Map<String, Object> getMap(String sql) throws Exception {
		log.debug("获取多字段单条记录sql-->" + sql);
		try {
			return jdbcTemplate.queryForMap(sql);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public Object getObject(String sql) throws Exception {
		
		log.debug("获取单字段单条记录sql-->" + sql);
		try {
			return jdbcTemplate.queryForObject(sql, new RowMapper<Object>(){
				@Override
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs.getObject(1);
				}
			});
		} catch (Exception e) {
			log.error(e);
		}
		return null;
	}
	
	@Override
	public long getRecords(String sql) throws Exception {
		log.debug("获取统计记录sql-->" + sql);
		return jdbcTemplate.queryForObject(sql, Long.class);
	}
	
	@Override
	public <T> T getObject(String sql, Class<T> clazz) throws Exception {
		return jdbcTemplate.queryForObject(sql, clazz);
	}

	@Override
	public List<Map<String,Object>> getMapList(String sql) throws Exception {
		log.debug("获取多字段多条记录sql-->" + sql);
		try {
			return jdbcTemplate.queryForList(sql);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<Object[]> getObjectList(String sql) throws Exception {

		log.debug("获取多字段多条记录sql-->" + sql);

		List<Object[]> infos = jdbcTemplate.query(sql, new ResultSetExtractor<List<Object[]>>(){
			@Override
			public List<Object[]> extractData(ResultSet rs)
					throws SQLException, DataAccessException {
				ResultSetMetaData md = rs.getMetaData(); //得到结果集(rs)的结构信息，比如字段数、字段名等
				int columnCount = md.getColumnCount(); //返回此 ResultSet 对象中的列数
				List<Object[]> objList = new ArrayList<Object[]>();
				while(rs.next()) {
					Object[] objs = new Object[columnCount];
					for (int i = 1; i <= columnCount; i++) {
						objs[i-1] = rs.getObject(i);
					}
					objList.add(objs);
				}
				return objList;
			}
		});
		return infos;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getList(String sql) throws Exception {
		
		log.debug("获取多字段多条记录sql-->" + sql);
		
		
		List res = jdbcTemplate.query(sql, new RowMapper<Object>(){
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getObject(1);
			}
		});
		return res;
	}

 
	@Override
	public boolean createTable(String sql) {
		
		log.debug("创建数据表sql-->" + sql);
		jdbcTemplate.execute(sql);
		return true;
	}
	
	@Override
	public DataSource getDataDource() {
		return jdbcTemplate.getDataSource();
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
//	/**
//	 * 通过ResultSet给实体赋值
//	 * @param clazz
//	 * @param resultSet
//	 * @return
//	 * @throws Exception
//	 */
//	private Object getT(Class<?> clazz, ResultSet resultSet) throws Exception {
//		Object obj = null;
//		obj = clazz.newInstance();
//		PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(clazz);
//		for (PropertyDescriptor pd : pds) {
//			try {
//				if ("java.lang.String".equals(pd.getPropertyType().getCanonicalName())) {
//					pd.getWriteMethod().invoke(obj, new Object[] { resultSet.getString(pd.getName()) });
//				} else if (("int".equals(pd.getPropertyType().getSimpleName())) || ("java.lang.Integer".equals(pd.getPropertyType().getCanonicalName()))) {
//					pd.getWriteMethod().invoke(obj, new Object[] { Integer.valueOf(resultSet.getInt(pd.getName())) });
//				} else if (("boolean".equals(pd.getPropertyType().getSimpleName())) || ("java.lang.Boolean".equals(pd.getPropertyType().getCanonicalName()))) {
//					pd.getWriteMethod().invoke(obj, new Object[] { Boolean.valueOf(resultSet.getBoolean(pd.getName())) });
//				} else if ("java.util.Date".equals(pd.getPropertyType().getCanonicalName())) {
//					Timestamp time = resultSet.getTimestamp(pd.getName());
//					if (time != null)
//						pd.getWriteMethod().invoke(obj, new Object[] { new Date(time.getTime()) });
//				}
//				else if (("long".equals(pd.getPropertyType().getSimpleName())) || ("java.lang.Long".equals(pd.getPropertyType().getCanonicalName()))) {
//					pd.getWriteMethod().invoke(obj, new Object[] { Long.valueOf(resultSet.getLong(pd.getName())) });
//				} else if (("float".equals(pd.getPropertyType().getSimpleName())) || ("java.lang.Float".equals(pd.getPropertyType().getCanonicalName()))) {
//					pd.getWriteMethod().invoke(obj, new Object[] { Float.valueOf(resultSet.getFloat(pd.getName())) });
//				}
//			} catch (Exception e) {
//				log.error(e);
//			}
//		}
//		return obj;
//	}

}
