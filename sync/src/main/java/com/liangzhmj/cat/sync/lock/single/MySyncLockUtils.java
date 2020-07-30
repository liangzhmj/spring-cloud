package com.liangzhmj.cat.sync.lock.single;

import com.liangzhmj.cat.sync.lock.single.model.MyLock;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 同步锁工具
 * @author liangzhmj
 *
 */
@Log4j2
public class MySyncLockUtils {

	private static Map<String, MyLock> myLocks = new ConcurrentHashMap<>();
	private static Map<String, MyFreqLock> frLocks = new ConcurrentHashMap<>();

	/**
	 * 获取MyFreqLock
	 * @param key
	 * @return
	 */
	public static MyFreqLock getFrLock(String key){
		if(StringUtils.isEmpty(key)){
			return null;
		}
		MyFreqLock frLock = frLocks.get(key);
		if(frLock != null){
			frLock.lock();//计数器+1
			return frLock;//保证同一个对象,达到锁的目的
		}
		synchronized (frLocks) {
			frLock = frLocks.get(key);//二次获取，防止覆盖
			if(frLock == null){//二次判断，防止覆盖
				frLock = new MyFreqLock(key);//简单已key作为实体内容
				frLocks.put(key, frLock);
			}
			frLock.lock();//计数器+1
		}
		return frLock;
	}
	

	
	
	/**
	 * 获取简单的同步锁(锁实体，不附带参数信息)
	 * @param key
	 * @return
	 */
	public static MyLock getSimpleLock(String key){
		if(StringUtils.isEmpty(key)){
			return null;
		}
		MyLock myLock = myLocks.get(key);
		if(myLock != null){
			return myLock;//保证同一个对象,达到锁的目的
		}
		synchronized (myLocks) {
			myLock = myLocks.get(key);//二次获取，防止覆盖
			if(myLock == null){//二次判断，防止覆盖
				myLock = new MyLock(key);//简单已key作为实体内容
				myLocks.put(key, myLock);
			}
		}
		return myLock;
	}
	
	/**
	 * 设置带参数的同步锁
	 * @param key
	 * @param obj
	 * @return true:设置成功，false:设置失败
	 */
	public static boolean setSimpleValueLock(String key,Object obj){
		if(StringUtils.isEmpty(key) || obj == null){
			return false;
		}
		if(myLocks.containsKey(key)){
			log.error("key:"+key+" 的同步锁已经存在，不允许替换0");
			return false;
		}
		synchronized (myLocks) {
			if(myLocks.containsKey(key)){//二次获取，防止覆盖
				log.error("key:"+key+" 的同步锁已经存在，不允许替换1");
				return false;
			}
			myLocks.put(key, new MyLock(obj));
		}
		return true;
	}
	
	/**
	 * 除非废除该key的锁不然不要废除
	 * @param key
	 */
	public static void removeMyLock(String key){
		myLocks.remove(key);
	}
	protected static void removeFrLock(String key){
		frLocks.remove(key);
	}
	
}
