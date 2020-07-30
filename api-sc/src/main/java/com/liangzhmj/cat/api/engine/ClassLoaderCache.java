package com.liangzhmj.cat.api.engine;

import com.liangzhmj.cat.api.service.ServiceModule;
import com.liangzhmj.cat.api.service.SyncAdapter;
import com.liangzhmj.cat.dao.mysql.APIDao;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;

/**
 * 缓存ClassLoaderEngine中加载的类对象(由于缓存对象必须要在热部署之后清理掉，因此统一管理)
 */
@Log4j2
public class ClassLoaderCache {


	/** 当前的业务模块的实例 Map key:模块标识**/
	private static Map<String, SyncAdapter> adapterContext = new HashMap<String,SyncAdapter>();
	/** 当前的业务模块的实例 Map key:模块标识**/
	private static Map<String, ServiceModule> smContext = new HashMap<String,ServiceModule>();
	/** classloader缓存，一个入口一个classLoader，暂时有serviceModule，adapter，缺点入口增多classload增多，每一个classloader可能加载重复的类，例如工具类，造成浪费。优点：更新部分接口不用整个classloader替换 **/
	private static Map<String,Object> commonContext = new HashMap<String,Object>();
	/** 当前的业务classloader Map key:模块标识**/
	private static Map<String,MyClassLoader> curLoader = new HashMap<String,MyClassLoader>(); 
	
	
	public static MyClassLoader getClassLoader(String name){
		return curLoader.get(name);
	}
	public static void putClassLoader(String name,MyClassLoader loader){
		curLoader.put(name,loader);
	}
	public static Map<String, MyClassLoader> getCurLoader() {
		return curLoader;
	}
	
	
	public static SyncAdapter checkAdapter(String name){
		SyncAdapter sm = adapterContext.get(name);
		//缓存有
		if(sm != null){
			return sm;
		}
		return null;
	}
	/**
	 * 根据模块标识和全类名获取对应对象实例
	 * @param baseDao
	 * @param name
	 * @return
	 */
	public static SyncAdapter loadSyncAdapter(APIDao baseDao, String name){
		//缓存没有
		Class<?> clazz = ClassLoaderEngine.loadClass(baseDao,name);
		//获取不到class
		if(clazz == null){
			log.info("获取不到适配器Class【"+name+"】");
			return null;
		}
		//生成实例
		try {
			SyncAdapter sm;
			synchronized (adapterContext) {
				sm = (SyncAdapter)clazz.newInstance();
				//添加缓存
				adapterContext.put(name, sm);
			}
			log.info("从数据库加载类生成对象【"+name+"】");
			return sm;
		} catch (Exception e) {
			log.error(e);
		}
		return null;
	}

	/**
	 * 根据模块标识和全类名获取对应对象实例
	 * @param baseDao
	 * @param name
	 * @return
	 */
	public static Object loadObject(APIDao baseDao,String name){
		Object obj = commonContext.get(name);
		//缓存有
		if(obj != null){
			log.info("从缓存里面获取到对象【"+name+"】");
			return obj;
		}
		//缓存没有
		Class<?> clazz = ClassLoaderEngine.loadClass(baseDao, name);
		//获取不到class
		if(clazz == null){
			log.info("获取不到对象Class【"+name+"】");
			return null;
		}
		//生成实例
		try {
			synchronized (commonContext) {
				obj = clazz.newInstance();
				//添加缓存
				commonContext.put(name, obj);
			}
			log.info("从数据库加载类生成对象【"+name+"】");
			return obj;
		} catch (Exception e) {
			log.error(e);
		}
		return null;
	}
	
	/**
	 * 根据模块标识和全类名获取对应对象实例
	 * @param baseDao
	 * @param name
	 * @return
	 */
	public static ServiceModule loadServiceModule(APIDao baseDao,String name){
		String sn = name;
		ServiceModule sm = smContext.get(sn);
		//缓存有
		if(sm != null){
			return sm;
		}
		//缓存没有
		Class<?> clazz = ClassLoaderEngine.loadClass(baseDao,name);
		//获取不到class
		if(clazz == null){
			return null;
		}
		//生成实例
		try {
			synchronized (smContext) {
				sm = smContext.get(sn);//再次获取
				if(sm == null){//再次判断
					sm = (ServiceModule)clazz.newInstance();
					//添加缓存
					smContext.put(sn, sm);
				}
			}
			return sm;
		} catch (Exception e) {
			log.error(e);
		}
		return null;
	}
	
	/**
	 * 清理对象实例缓存
	 * @param name
	 */
	public static void clearCache(String name){
		log.info("触发清理对象缓存方法-start");
		if(curLoader.containsKey(name)){
			curLoader.remove(name);
			log.info("curLoader清除了适配器对象实例缓存,class="+name);
		}
		if(adapterContext.containsKey(name)){
			adapterContext.remove(name);
			log.info("adapter清除了适配器对象实例缓存,class="+name);
		}
		if(commonContext.containsKey(name)){
			commonContext.remove(name);
			log.info("common清除了适配器对象实例缓存,class="+name);
		}
		if(smContext.containsKey(name)){
			smContext.remove(name);
			log.info("smContext清除了适配器对象实例缓存,class="+name);
		}
		log.info("触发清理对象缓存方法-end");
	}

	public static void clearCache(){
		log.info("触发清理对象缓存方法-start2");
		curLoader.clear();
		adapterContext.clear();
		commonContext.clear();
		smContext.clear();
		log.info("触发清理对象缓存方法-end2");
	}
}
