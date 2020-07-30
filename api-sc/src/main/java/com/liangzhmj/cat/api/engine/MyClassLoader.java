package com.liangzhmj.cat.api.engine;

import com.liangzhmj.cat.dao.mysql.APIDao;
import lombok.extern.log4j.Log4j2;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * 自定义ClassLoader
 * 从数据库获取字节码，加载到ClassLoader中
 * @author liangzhmj
 *
 */
@Log4j2
public class MyClassLoader extends ClassLoader {

	private String baseDir = System.getProperty("user.dir");
	
	private APIDao baseDao;
	
	public MyClassLoader(APIDao baseDao){
		super(Thread.currentThread().getContextClassLoader());  
		this.baseDao = baseDao;
	}
	
	//构造自定义Classloader, 并指定父Classloader  
	public MyClassLoader() {  
		super(Thread.currentThread().getContextClassLoader());  
	}  
	
	public Class<?> findLoadedClass1(String name) throws ClassNotFoundException{
		return findLoadedClass(name);
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		byte[] b = loadClassBytes(name);
		return defineClass(name, b, 0, b.length);
	}

	@Override
	protected URL findResource(String name) {
		try {
			String dirName = name.replaceAll("\\.", "/");
			String str = "file:///"+baseDir+dirName+".class";
			log.info("调用findResource-url:"+str);
			return new URL(str);
		} catch (MalformedURLException e) {
			log.error(e);
		}
		return super.findResource(name);
	}

	/**
	 * 获取class文件的byte[]
	 * @param className 全类名
	 * @return
	 * @throws ClassNotFoundException
	 */
	private byte[] loadClassBytes(String className)
			throws ClassNotFoundException {
		log.info("从数据库获取字节码【"+className+"】信息");
		try {
			byte[] b = (byte[])baseDao.getObject("SELECT content FROM t_inter_class WHERE fullpackage='"+className+"' AND isUse=1");
			if(b == null){
				throw new ClassNotFoundException(className);
			}
			return b;
		} catch (Exception fnfe) {
			throw new ClassNotFoundException(className);
		}
	}

}
