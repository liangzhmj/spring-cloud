package com.liangzhmj.cat.sync.lock.single;

/**
 * 我的频繁同步锁(要显式移除)
 * @author liangzhmj
 *
 */
public class MyFreqLock{

	private int count = 0;
	private String key;
	
	public MyFreqLock(String key){
		this.key = key;
	}
	
	protected boolean isOccupied(){
		return count>0?true:false;
	}
	
	protected synchronized void lock(){
		count++;//count++不是原子操作，可能会有线程安全问题，因此加上synchronized关键字
	}


	protected int getCount() {
		return count;
	}
	
	public synchronized int unlock(){
		count--;
		if(isOccupied()){//被占用
			return count;
		}
		//没被占用
		MySyncLockUtils.removeFrLock(key);
		return 0;
	}
}
