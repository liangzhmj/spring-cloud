package com.liangzhmj.cat.dao.redis;

import java.util.List;
import java.util.Set;


public interface RedisDao {

	void add(String key, Object Object);
	void add(String key, Object Object, long expire);
	long addOrIncr(String key, long value);
	long addOrDecr(String key, long value);
	<T> T getT(String key);
	void delete(String key);
	void deletes(List<String> keys);
	long rpush(String key, String value);
	long lpush(String key, String value);
	<T> T rpop(String key);
	<T> T lpop(String key);
	long sadd(String key, String value);
	<T> T spop(String key);
	Set<Object> smembers(String key);
	boolean exists(String key);
	long setSize(String key);
	long listSize(String key);
	
}
