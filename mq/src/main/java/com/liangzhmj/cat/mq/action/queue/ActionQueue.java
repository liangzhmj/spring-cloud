package com.liangzhmj.cat.mq.action.queue;

import com.liangzhmj.cat.mq.action.vo.AbstractAction;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * 此队批量执行某些动作
 * 
 * @author liangzhmj
 */
public class ActionQueue {
	
	/** 即时批处理 **/
	private BlockingQueue<AbstractAction> queue1 = new LinkedBlockingQueue<AbstractAction>();
	private BlockingQueue<AbstractAction> queue2 = new LinkedBlockingQueue<AbstractAction>();

	/**
	 * 添加到队列方法，将指定元素插入此队列的尾部
	 * @param sa
	 * @return 成功：true,失败：false
	 */
	public boolean offer(AbstractAction sa) {
		return queue1.offer(sa);
	}

	public BlockingQueue<AbstractAction> getQueue1() {
		return queue1;
	}

	public BlockingQueue<AbstractAction> getQueue2() {
		return queue2;
	}
	
}
