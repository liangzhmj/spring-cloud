package com.liangzhmj.cat.mq.sql.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * 公共的sql执行队列
 * 
 */
public class SqlQueue {
	
	/** 即时批处理 **/
	private BlockingQueue<String> queue1 = new LinkedBlockingQueue<String>();
	/** 即时处理失败，二次处理批处理  **/
	private BlockingQueue<String> queue2 = new LinkedBlockingQueue<String>();
	/** 二次处理失败，单条处理  **/
	private BlockingQueue<String> queue3 = new LinkedBlockingQueue<String>();


	/**
	 * 添加sql到队列
	 * @param sql
	 * @return true:成功，false:失败
	 */
	public boolean offer(String sql) {
		return queue1.offer(sql);
	}


	public BlockingQueue<String> getQueue1() {
		return queue1;
	}


	public BlockingQueue<String> getQueue2() {
		return queue2;
	}


	public BlockingQueue<String> getQueue3() {
		return queue3;
	}
	
	
}
