package com.liangzhmj.cat.dao.utils;

import com.liangzhmj.cat.dao.mysql.APIDao;
import com.liangzhmj.cat.dao.redis.RedisDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 通过静态注入获取spring bean
 * @author liangzhmj
 *
 */
@Component("daoBeanUtils")
public class DaoBeanUtils {

	private static APIDao baseDao;
	private static APIDao serviceDao;
	private static RedisDao redisDao;
	
	public static APIDao getBaseDao() {
		return baseDao;
	}
	
	public static APIDao getServiceDao(){
		return serviceDao;
	}

	public static RedisDao getRedisDao(){
		return redisDao;
	}
	
	

	@Autowired(required = false)
	@Qualifier("baseDao")
	public void setBaseDao(APIDao baseDao) {
		DaoBeanUtils.baseDao = baseDao;
	}
	
	@Autowired(required = false)
	@Qualifier("serviceDao")
	public void setServiceDao(APIDao serviceDao) {
		DaoBeanUtils.serviceDao = serviceDao;
	}

	@Autowired(required = false)
	@Qualifier("redisDao")
	public void setRedisDao(RedisDao redisDao) {
		DaoBeanUtils.redisDao = redisDao;
	}
	

	
}
