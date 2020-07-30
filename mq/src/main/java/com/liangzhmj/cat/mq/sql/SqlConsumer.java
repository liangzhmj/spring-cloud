package com.liangzhmj.cat.mq.sql;

import com.liangzhmj.cat.dao.mysql.APIDao;
import com.liangzhmj.cat.mq.exception.MqException;
import com.liangzhmj.cat.tools.collection.CollectionUtils;
import com.liangzhmj.cat.tools.date.DateUtils;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.jdbc.datasource.DataSourceUtils;

import java.io.File;
import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


/**
 * 读取处理批处理数据
 * 
 */
@Log4j2
public class SqlConsumer extends Thread {

	private static String SQL_LINE_END_TAG = "/r/n";
	private int max = 100;
	private int pool1 = 2; // 即时处理线程数
	private int pool2 = 1; // 第一次处理失败，继续处理线程数
	private int interval = 300;//每次获取队列最大时间间隔
	private String failFilePath;
	private boolean run = true;
	private APIDao apiDao;
	/** 即时批处理 **/
	private BlockingQueue<String> queue1;
	/** 即时处理失败，二次处理批处理  **/
	private BlockingQueue<String> queue2;
	/** 二次处理失败，单条处理  **/
	private BlockingQueue<String> queue3;
	private String name;

	public SqlConsumer(String name,APIDao apiDao,int max,int pool1,int pool2,int interval,String failFilePath,BlockingQueue<String> queue1,BlockingQueue<String> queue2,BlockingQueue<String> queue3) {
		this.apiDao = apiDao;
		this.max = max;
		this.pool1 = pool1;
		this.pool2 = pool2;
		this.interval = interval;
		this.failFilePath = failFilePath;
		this.queue1 = queue1;
		this.queue2 = queue2;
		this.queue3 = queue3;
		this.name = name;
	}

	@Override
	public void run() {
		//第一轮=================================================================
		for (int i = 0; i < pool1; i++) {
			new Thread(new Runnable() {//开启线程
				@Override
				public void run() {
					while(run || !queue1.isEmpty()){//满足一个条件，继续执行
						try {
							// 从队列读取数据，最多拿max个
							List<String> sqls = new ArrayList<String>();
							for (int j = 0; j < max; j++) {
								String sql = getPoll1(interval,TimeUnit.MILLISECONDS);
								if (StringUtils.isEmpty(sql)) {
									break;
								}
								sqls.add(sql);
							}
							// 批处理执行，失败添加到二次执行队列
							executeBatchSql(sqls);
							sqls.clear();
						} catch(MqException e){//执行出现可控异常
							log.warn(e.getMessage());
						} catch (Throwable e) {//执行出现未知异常
							log.error(e);
						}
					}
				}
			}).start();
		}
		//第一轮=================================================================

		//第二轮=================================================================
		for (int i = 0; i < pool2; i++) {
			new Thread(new Runnable() {//开启线程
				@Override
				public void run() {
					int interval2 = interval +200;
					while(run || !queue2.isEmpty()){//满足一个条件，继续执行
						try {
							// 从队列读取数据，最多拿max个
							List<String> sqls = new ArrayList<String>();
							for (int j = 0; j < max; j++) {
								String sql = getPoll2(interval2,TimeUnit.MILLISECONDS);
								if (StringUtils.isEmpty(sql)) {
									break;
								}
								sqls.add(sql);
							}
							// 批处理执行，失败添加到二次执行队列
							executeBatchSql2(sqls);
							sqls.clear();
						} catch(MqException e){//执行出现可控异常
							log.warn(e.getMessage());
						} catch (Throwable e) {//执行出现未知异常
							log.error(e);
						}
					}
				}
			}).start();
		}
		//第二轮=================================================================
		
		//第三轮=================================================================
		new Thread(new Runnable() {//开启线程
			@Override
			public void run() {
				int interval3 = interval + 500;
				while(run || !queue3.isEmpty()){//满足一个条件，继续执行
					try {
						// 从队列读取数据，最多拿max个
						String sql = getPoll3(interval3,TimeUnit.MILLISECONDS);
						if (!StringUtils.isEmpty(sql)) {
							executeBatchSql3(sql);
						}
					} catch(MqException e){//执行出现可控异常
						log.warn(e.getMessage());
					} catch (Throwable e) {//执行出现未知异常
						log.error(e);
					}
				}
			}
		}).start();
		//第三轮=================================================================

		log.info(">>>>>>>>>>>["+name+"]批处理执行SQL-即时线程" + pool1 + "个，二次处理线程" + pool2 + "个");
	}

	/**
	 * 批处理执行sql
	 * 
	 * @param lists
	 * @return 失败-false;成功-true
	 */
	private boolean executeBatchSql(List<String> lists) {
		int size = lists.size();
		if (size <= 0) {
			return true;
		}
		long startTime = System.currentTimeMillis();
		Connection cn = null;
		Statement stmt = null;
		boolean returnBoo = false;
		try {
			cn = DataSourceUtils.getConnection(apiDao.getDataDource());// 从连接池获取连接
			cn.setAutoCommit(false);
			stmt = cn.createStatement();
			for (int i = 0; i < size; i++) {
				stmt.addBatch(lists.get(i));
			}
			stmt.executeBatch(); // 执行批处理
			cn.commit();
			stmt.clearBatch();
			long endTime = System.currentTimeMillis();
			log.info(name+"批处理[第一次执行]更新成功条数：" + size + ", time:" + (endTime - startTime));
			returnBoo = true;
		} catch (Exception e) {
			log.error(name+"批处理[第一次执行]更新失败,回滚...", e.getMessage());
			try {
				if (cn != null)
					cn.rollback();
			} catch (Exception ee) {
			}

			// 添加到二次处理队列
			for (String sql2 : lists) {
				log.debug("队列1执行失败，转到队列2:"+sql2);
				offer2(sql2);
			}
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
					DataSourceUtils.releaseConnection(cn, apiDao.getDataDource());// 归还连接给连接池
				} catch (Exception ex) {
				}
			}
		}
		return returnBoo;
	}

	/**
	 * 批处理执行sql，失败找出失败的数据
	 * 
	 * @param lists
	 * @return 执行失败的数据
	 */
	private boolean executeBatchSql2(List<String> lists) {
		int size = lists.size();
		if (size <= 0) {
			return true;
		}
		Connection cn = null;
		Statement stmt = null;
		boolean returnBoo = false;
		try {
			cn = DataSourceUtils.getConnection(apiDao.getDataDource());// 从连接池获取连接
			cn.setAutoCommit(false);
			stmt = cn.createStatement();
			for (int i = 0; i < size; i++) {
				stmt.addBatch(lists.get(i));
			}
			stmt.executeBatch(); // 执行批处理
			cn.commit();
			stmt.clearBatch();
			returnBoo = true;
			log.info(name+"批处理[第二次执行]更新成功条数：" + size);
		} catch (BatchUpdateException bue) {
			log.error(name+"批处理[第二次执行]更新失败,回滚...", bue.getMessage());
			try {
				cn.rollback();
			} catch (SQLException e) {
				log.error(e.getMessage());
			}
			int[] r = bue.getUpdateCounts();
			int k = 0;
			for (int j = 0; j < r.length; j++) {
				if (r[j] > 0) {
					log.debug("队列2分析sql可执行，返回队列1："+lists.get(j));
					offer1(lists.get(j)); // 可执行sql，继续执行,添加到一号队列
				} else {
					k++;
					log.debug("队列2分析sql不可执行，入列队列3："+lists.get(j));
					offer3(lists.get(j)); // 不可执行sql，放入单条分析队列添加到三号队列
				}
			}
			log.info(name+"批处理[第二次执行]分析批处理sql，可执行条数：" + (size - k) + " ,不可执行条数：" + k);
		} catch (SQLException e) {
			log.error(e.getMessage());
			try {
				cn.rollback();
			} catch (SQLException e1) {
				log.error(e.getMessage());
			}
			log.error(e);
		} catch (Exception e) {
			log.error(name+"批处理[第二次执行]关闭数据链接异常", e);
			e.printStackTrace();
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
					DataSourceUtils.releaseConnection(cn, apiDao.getDataDource());// 归还连接给连接池
				} catch (Exception ex) {
				}
			}
		}
		return returnBoo;
	}

	/**
	 * 清除未执行数据
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	public void clearData() throws InterruptedException {
		List<String> elems = getAllElems();
		// 还有未执行完的
		if (!CollectionUtils.isEmpty(elems)) {
			elems = getAllElems();
			executeBatchSql(elems);
			// 睡两秒
			Thread.sleep(2000);
			if (!CollectionUtils.isEmpty(elems)) {
				elems = getAllElems();
				executeBatchSql(elems);
			}
		}
	}

	/**
	 * 单条sql执行，失败，写文件
	 * 
	 * @param sql
	 * @return 失败-false;成功-true
	 */
	private boolean executeBatchSql3(String sql) {
		log.info(name+"批处理[第三次执行]单条执行:-->" + sql);
		int count = 0;
		try {
			apiDao.executeSQL(sql);
		} catch (Throwable e) {
			count = -1;
			log.error(e.getMessage());
		}

		// 一天一个文件
		if (count == -1) {// 异常
			String currtime = DateUtils.getCurrentStr("yyyyMMdd");
			String realPath = failFilePath + currtime + ".txt";
			String failSql = sql + SQL_LINE_END_TAG;
			try {
				FileUtils.write(new File(realPath),failSql,"UTF-8",true);
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
		return true;
	}

	public void shutdown() {
		run = false;
	}
	
	
	
	/**
	 * 添加sql到队列
	 * @param sql
	 * @return true:成功，false:失败
	 */
	public boolean offer1(String sql) {
		return (queue1.offer(sql));
	}
	
	/**
	 * 添加sql到队列
	 * @param sql
	 * @return true:成功，false:失败
	 */
	public boolean offer2(String sql) {
		return (queue2.offer(sql));
	}
	
	/**
	 * 添加sql到队列
	 * @param sql
	 * @return true:成功，false:失败
	 */
	public boolean offer3(String sql) {
		return (queue3.offer(sql));
	}
	
	/** 获取并移除此队列的头 ，如果此队列为空，则返回 null */
	public String getPoll1(long timeout, TimeUnit unit) {
		try {
			return (queue1.poll(timeout,unit));
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}
		return null;
	}
	
	/** 获取并移除此队列的头 ，如果此队列为空，则返回 null */
	public String getPoll2(long timeout, TimeUnit unit) {
		try {
			return (queue2.poll(timeout,unit));
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}
		return null;
	}
	
	/** 获取并移除此队列的头 ，如果此队列为空，则返回 null */
	public String getPoll3(long timeout, TimeUnit unit) {
		try {
			return (queue3.poll(timeout,unit));
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}
		return null;
	}
	
	
	public List<String> getAllElems(){
		List<String> elems = new ArrayList<String>();
		if(queue1 != null && !queue1.isEmpty()){
			String sql = queue1.poll();
			elems.add(sql);
		}
		if(queue2 != null && !queue2.isEmpty()){
			String sql = queue2.poll();
			elems.add(sql);
		}
		if(queue2 != null && !queue2.isEmpty()){
			String sql = queue2.poll();
			elems.add(sql);
		}
		log.info(name+"未执行的sql总共有:"+elems.size()+"条");
		return elems;
	}
}
