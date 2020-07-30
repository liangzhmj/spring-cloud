package com.liangzhmj.cat.mq.action;


import com.liangzhmj.cat.mq.action.vo.AbstractAction;
import com.liangzhmj.cat.mq.exception.MqException;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;



/**
 * 读取批量执行AbstractAction队列
 * @author liangzhmj
 */
@Log4j2
public class ActionConsumer extends Thread {

	private int size = 2;
	private boolean run = true;
	private String name;
	private int interval = 500;
	/** 即时批处理 **/
	private BlockingQueue<AbstractAction> queue1;
	private BlockingQueue<AbstractAction> queue2;

	/**
	 * 实例化动作队列消费者程序，需要调用start()启动
	 * @param name 消费者名称
	 * @param size  消费者执行线程数
	 * @param interval 每次获取队列元素的最大时间间隔
	 * @param queue1 第一轮执行队列（外币元素统一入列该队列）
	 * @param queue2 第二轮执行队列（第一轮失败时会入列第二轮）
	 */
	public ActionConsumer(String name,int size,int interval,BlockingQueue<AbstractAction> queue1,BlockingQueue<AbstractAction> queue2) {
		this.name = name;
		this.size = size;
		if(interval > 10){
			this.interval = interval;
		}
		this.queue1 = queue1;
		this.queue2 = queue2;
	}
	
	
	@Override
	public void run() {
		log.info(name+"启动"+size+"个线程处理...");
		//第一轮=================================================================
		for (int i = 0; i < size; i++) {
			new Thread(new Runnable() {//开启线程
				@Override
				public void run() {
					while(run || !queue1.isEmpty()){//满足一个条件，继续执行
						try {
							AbstractAction sa =  getPoll1(interval, TimeUnit.MILLISECONDS);//等待500毫秒
							if(sa == null){
								continue;
							}
							//需要执行的
							if(!sa.isValid()){
								throw new MqException(name+"该动作已失效1_1，并未成功执行--->"+sa);
							}
							//执行准备工作
							sa.prepareForAction();
							try {
								//开启线程执行动作
								boolean isSuc = sa.doAction();
								//成功
								if(isSuc){
									//判断是否需要回调
									try {
										sa.onSuccess();
									} catch (Exception e) {
									}
								}
								//失败
								else{
									//重新添加到队列等着（内部判断是否有效）
									offer1(sa);
									try {
										sa.onFail();
									} catch (Exception e) {
									}
								}
							} catch (Exception e) {
								//添加到队列
								offer1(sa);
								log.error(name+"第一轮("+sa.getTime()+")处理"+sa.getName()+"出现异常--->"+sa,e);
							}
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
		new Thread(new Runnable() {//第二轮开启线程
			@Override
			public void run() {
				int interval2 = interval*2;
				while(run || !queue2.isEmpty()){//满足一个条件，继续执行
					try {
						AbstractAction sa =  getPoll2(interval2, TimeUnit.MILLISECONDS);//等待10秒
						if(sa == null){
							continue;
						}
						//需要执行的
						if(!sa.isValid()){
							throw new MqException(name+"该动作已失效1_2，并未成功执行--->"+sa);
						}
						//执行准备工作
						sa.prepareForAction();
						try {
							//开启线程执行动作
							boolean isSuc = sa.doAction();
							//成功
							if(isSuc){
								//判断是否需要回调
								try {
									sa.onSuccess();
								} catch (Exception e) {
								}
							}
							//失败
							else{
								//重新添加到队列等着（内部判断是否有效）
								offer2(sa);
								try {
									sa.onFail();
								} catch (Exception e) {
								}
							}
									
						} catch (Exception e) {
							//添加到队列
							offer2(sa);
							log.error(name+"第二轮("+sa.getTime()+")处理"+sa.getName()+"出现异常--->"+sa,e);
						}
					} catch(MqException e){//执行出现可控异常
						log.warn(e.getMessage());
					} catch (Throwable e) {//执行出现未知异常
						log.error(e);
					}
				}
			}
		}).start();
	}
	
	public void shutdown(){
		run = false;
	}
	
	
	/**
	 * 添加到队列方法，将指定元素插入此队列的尾部
	 * @param sa
	 * @return 成功：true,失败：false
	 */
	public boolean offer1(AbstractAction sa) {
		//无效
		if(!sa.isValid()){
			//time复位，转到第二个队列
			sa.reset();
			offer2(sa);
			log.info(name+"动作"+sa.getName()+"第一轮执行失败，复位进入第二轮队列");
			return false;
		}
		return (queue1.offer(sa));
	}
	
	/** 获取并移除此队列的头 ，如果此队列为空，则返回 null 
	 * @throws InterruptedException */
	public AbstractAction getPoll1(long timeout, TimeUnit unit){
		try {
			return (queue1.poll(timeout,unit));
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}
		return null;
	}
	
	/**
	 * 添加到队列方法，将指定元素插入此队列的尾部
	 * @param sa
	 * @return 成功：true,失败：false
	 */
	public boolean offer2(AbstractAction sa) {
		//无效
		if(!sa.isValid()){
			log.info(name+"动作"+sa.getName()+"第二轮执行失败，放弃执行:"+sa);
			return false;
		}
		return (queue2.offer(sa));
	}
	
	/** 获取并移除此队列的头 ，如果此队列为空，则返回 null 
	 * @throws InterruptedException */
	public AbstractAction getPoll2(long timeout, TimeUnit unit){
		try {
			return (queue2.poll(timeout,unit));
		} catch (InterruptedException e) {
			log.error(e);
		}
		return null;
	}
	
}
