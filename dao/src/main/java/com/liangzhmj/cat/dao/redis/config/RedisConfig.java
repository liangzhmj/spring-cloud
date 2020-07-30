package com.liangzhmj.cat.dao.redis.config;

import com.liangzhmj.cat.dao.redis.RedisDao;
import com.liangzhmj.cat.dao.redis.impl.RedisDaoImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;

public class RedisConfig {

    @Resource
    private RedisTemplate redisTemplate;
	
	@Bean
    public RedisTemplate redisTemplateInit() {//重置redis的序列化工具
    	RedisSerializer stringSerializer = new StringRedisSerializer();
        //设置序列化Key的实例化对象
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
//        //设置序列化Value的实例化对象
//        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }

    @Bean
    public RedisDao redisDao(){
        return new RedisDaoImpl(redisTemplate);
    }
}
