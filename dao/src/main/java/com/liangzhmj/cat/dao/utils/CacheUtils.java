package com.liangzhmj.cat.dao.utils;

import com.liangzhmj.cat.dao.cache.EhcacheContext;
import com.liangzhmj.cat.dao.redis.RedisDao;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;

import java.io.*;

/**
 * 该类利用LocalCache缓存本地数据，解决以下问题
 * 不能在热部署类里面使用成员属性缓存，因此不同的入口有不同的classloader，
 * 不同的classloader会有不同的类对象，不同的类对象有不同的成员属性，因此可能会出现问题
 * 并且热部署的类不能直接存储在LocalCache类里面，因为获取强转的时候回抛类型转换异常（不同的classloader）
 * 因此需要对缓存的对象序列化，然后获取的时候再返序列化成对象（序列化也要对应在热部署的classloader中）
 * @author liangzhmj
 *
 */
@Log4j2
public class CacheUtils {

	private static RedisDao redisDao = DaoBeanUtils.getRedisDao();

	/**
	 * 添加序列化对象到redis中
	 * @param key
	 * @param val
	 */
	public static void addSeriaRedis(String key,Object val){
		if(StringUtils.isEmpty(key) || val == null){
			return;
		}
		redisDao.add(key, serializeToByte(val));
	}
	
	/**
	 * 添加序列化对象到redis中
	 * @param key
	 * @param val
	 * @param expire 秒
	 */
	public static void addSeriaRedis(String key,Object val,long expire){
		if(StringUtils.isEmpty(key) || val == null){
			return;
		}
		redisDao.add(key, serializeToByte(val),expire);
	}
	
	/**
	 * 获取序列化对象
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getSeriaRedis(String key){
		try {
			byte[] b = redisDao.getT(key);
			if(b == null){
				return null;
			}
			return (T)unserializeFromByte(b);
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
	}

	/**
	 * 添加序列化对象到ehcache中
	 * @param name
	 * @param key
	 * @param val
	 */
	public static void addSeriaEhcache(String name,String key,Object val){
		if(StringUtils.isEmpty(key) || val == null){
			return;
		}
		EhcacheContext.putCache(name,key,serializeToByte(val));
	}

	/**
	 * 获取序列化对象
	 * @param name
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getSeriaEhcache(String name,String key){
		try {
			byte[] b = EhcacheContext.getCache(name,key);
			if(b == null){
				return null;
			}
			return (T)unserializeFromByte(b);
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
	}

	
	/**
	 * 序列化对象
	 * @param object
	 * @return
	 */
	public static byte[] serializeToByte(Object object) {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			// 序列化
			if (object != null) {
				baos = new ByteArrayOutputStream();
				oos = new ObjectOutputStream(baos);
				oos.writeObject(object);
				byte[] bytes = baos.toByteArray();
				return bytes;
			}
		} catch (Exception e) {
			log.error(e);
		} finally {
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 反序列化对象
	 * @param bytes
	 * @return
	 */
	public static Object unserializeFromByte(byte[] bytes) {
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		try {
			// 反序列化
			if (bytes != null) {
				bais = new ByteArrayInputStream(bytes);
				ois = new ObjectInputStream(bais);
				Object obj = ois.readObject();
				return obj;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(ois != null){
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(bais != null){
				try {
					bais.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
