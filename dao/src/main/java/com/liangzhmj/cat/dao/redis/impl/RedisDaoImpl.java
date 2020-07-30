package com.liangzhmj.cat.dao.redis.impl;

import com.liangzhmj.cat.dao.redis.RedisDao;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class RedisDaoImpl implements RedisDao {

	private RedisTemplate<String, Object> redisTemplate;
	
	@Override
	public void add(String key, Object value) {
		redisTemplate.opsForValue().set(key, value); 
	}

	@Override
	public void add(String key, Object value, long expire) {
		redisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS); 
	}

	
	@Override
	public long addOrIncr(String key, long value) {
		return redisTemplate.opsForValue().increment(key, value);
	}

	@Override
	public long addOrDecr(String key, long value) {
		return redisTemplate.opsForValue().increment(key, -value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getT(String key) {
		Object obj = redisTemplate.opsForValue().get(key);
		if(obj == null){
			return null;
		}
		return (T)obj;
	}

	@Override
	public void delete(String key) {
		redisTemplate.delete(key);
	}

	@Override
	public void deletes(List<String> keys) {
		redisTemplate.delete(keys);
	}

	@Override
	public long rpush(String key, String value) {
		return redisTemplate.opsForList().rightPush(key, value);
	}

	@Override
	public long lpush(String key, String value) {
		return redisTemplate.opsForList().leftPush(key, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T rpop(String key) {
		Object obj = redisTemplate.opsForList().rightPop(key);
		 if(obj == null){
			 return null;
		 }
		 return (T)obj;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T lpop(String key) {
		 Object obj = redisTemplate.opsForList().leftPop(key);
		 if(obj == null){
			 return null;
		 }
		 return (T)obj;
	}

	@Override
	public long sadd(String key, String value) {
		return redisTemplate.opsForSet().add(key, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T spop(String key) {
		Object obj = redisTemplate.opsForSet().pop(key);
		 if(obj == null){
			 return null;
		 }
		 return (T)obj;
	}

	@Override
	public Set<Object> smembers(String key) {
		return redisTemplate.opsForSet().members(key);
	}

	@Override
	public boolean exists(String key) {
		return redisTemplate.hasKey(key);
	}

	@Override
	public long setSize(String key) {
		return redisTemplate.opsForSet().size(key);
	}

	@Override
	public long listSize(String key) {
		return redisTemplate.opsForList().size(key);
	}
	
}
