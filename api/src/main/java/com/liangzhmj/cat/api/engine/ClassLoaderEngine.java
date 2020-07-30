package com.liangzhmj.cat.api.engine;

import com.liangzhmj.cat.dao.mysql.APIDao;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;


/**
 * 类加载器引擎
 * @author liangzhmj
 *
 */
@Log4j2
public class ClassLoaderEngine {
	
	/**
	 * 根据模块标识和全类名获取对应类实例
	 * @param baseDao
	 * @param name
	 * @return
	 */
	public static Class<?> loadClass(APIDao baseDao, String name){
		if(StringUtils.isEmpty(name)){return null;}
		String sn = name;
		MyClassLoader my = ClassLoaderCache.getClassLoader(sn);
		if(my == null){
			//对curLoader上锁
			synchronized (ClassLoaderCache.getCurLoader()) {
				my = ClassLoaderCache.getClassLoader(sn);
				if(my == null){//二次判断
					log.info("模块【"+sn+"】获取不到对应的classloader,新建一个");
					my = new MyClassLoader(baseDao);
					//如果是新建的,先加载一下以免读了项目目录里面的
					try {
						my.findClass(name);
					} catch (ClassNotFoundException e) {
						log.error(e);
					}catch (Throwable e){
						log.error("加载类"+name+" 异常，该次添加无效",e);
					}
					ClassLoaderCache.putClassLoader(sn, my);
				}
			}
		}
		try {
			return my.loadClass(name);
		} catch (ClassNotFoundException e) {
			log.error("没有发现对应的类文件【"+name+"】");
		} catch (Throwable e){
			log.error(e);
		}
		return null;
	}


	/**
	 * 加载或更新类到ClassLoader中（不存在则加载，存在则更新）
	 * @param baseDao
	 * @param name
	 * @param name
	 * @return
	 */
	public synchronized static Class<?> defineClass(APIDao baseDao,String name){
		if(StringUtils.isEmpty(name)){return null;}
		String sn = name;
		MyClassLoader my = ClassLoaderCache.getClassLoader(sn);
		Class<?> clazz = null;
		if(my == null){
			log.info("模块【"+sn+"】获取不到对应的classloader,新建一个");
			my = new MyClassLoader(baseDao);
			ClassLoaderCache.putClassLoader(sn, my);
		}
		try {
			log.info("定义模块【"+sn+"】class:"+name);
			return my.findClass(name);
		} catch(LinkageError e){
			String message = e.getMessage();
			if(!StringUtils.isEmpty(message) && message.indexOf("duplicate class")>0){//如果这个类已经在
				//重新new一个ClassLoader,不用显示调用这个loader的全部类，因为要用到的时候会根据findClass的实现规则来加载对应的class
				log.info("类【"+name+"】已经存在于"+sn+"--ClassLoader中，执行更新操作");
				MyClassLoader loader = new MyClassLoader(baseDao);
				try {
					clazz = loader.findClass(name);//这里不能用loadClass要用findClass,因为loadClass会双亲委托,如果父层已经加载项目中的class则这里不更新
					log.info("使用新建的"+sn+"--ClassLoader 加载类【"+name+"】成功，替换ClassLoader");
					ClassLoaderCache.putClassLoader(sn, loader);
					my = null;
				} catch (ClassNotFoundException e1) {
					log.info("使用新建的"+sn+"--ClassLoader 加载类【"+name+"】失败不执行替换ClassLoader类更新失败:\n"+e1.getMessage());
				}catch (Throwable e2){
					log.error("加载类"+name+" 异常，该次添加无效");
				}
			}else{
				log.error("未知原因类【"+name+"】加载失败",e);
			}
		} catch (ClassNotFoundException e) {
			log.error("没有发现对应的类文件【"+name+"】");
		} catch (Exception e){
			log.error("未知原因类【"+name+"】加载失败",e);
		}
		return clazz;
	}
	
	
}
