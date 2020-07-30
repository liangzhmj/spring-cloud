package com.liangzhmj.cat.dao.cache;

import com.liangzhmj.cat.dao.exception.DaoException;
import org.springframework.cache.Cache;
import org.springframework.cache.jcache.JCacheCacheManager;

import javax.annotation.Resource;

/**
 * 代码层面使用ehcache,突破注解的局pringbean,非静态方法等局限性
 * @author liangzhmj
 */
public class EhcacheContext {

    private static JCacheCacheManager cacheManager;

    @Resource
    public void setCacheManager(JCacheCacheManager cacheManager) {
        EhcacheContext.cacheManager = cacheManager;
    }

    public static <T> T getCache(String name, String key) throws DaoException {
        Cache cache = cacheManager.getCache(name);
        if(cache == null){
            throw new DaoException("不存在内存空间："+name);
        }
        Object val = cache.get(key,Object.class);
        if(val == null){
            return null;
        }
        return (T)val;
    }

    public static void putCache(String name,String key,Object value){
        Cache cache = cacheManager.getCache(name);
        if(cache == null){
            throw new DaoException("不存在内存空间："+name);
        }
        cache.put(key,value);
    }

    public static void delCache(String name,String key){
        Cache cache = cacheManager.getCache(name);
        if(cache == null){
            throw new DaoException("不存在内存空间："+name);
        }
        cache.evict(key);
    }

    public static void clearCache(String name){
        Cache cache = cacheManager.getCache(name);
        if(cache == null){
            throw new DaoException("不存在内存空间："+name);
        }
        cache.clear();
    }
}
