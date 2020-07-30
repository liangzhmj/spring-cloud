package com.liangzhmj.cat.api.service.impl;

import com.liangzhmj.cat.api.engine.ClassLoaderCache;
import com.liangzhmj.cat.api.enums.APIExceptionEnum;
import com.liangzhmj.cat.api.exception.APIException;
import com.liangzhmj.cat.api.model.InterInfo;
import com.liangzhmj.cat.api.protocol.req.APIReq;
import com.liangzhmj.cat.api.service.ServiceEngine;
import com.liangzhmj.cat.api.service.ServiceModule;
import com.liangzhmj.cat.dao.mysql.APIDao;
import com.liangzhmj.cat.dao.mysql.utils.DBUtils;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务引擎实现类
 * 
 * @author liangzhmj
 *
 */
@Service("serviceEngine")
@Setter
@Log4j2
public class ServiceEngineImpl implements ServiceEngine {

	@Resource
	private APIDao baseDao;
	@Value("${api.projectId:-1}")
	private int projectId;
	private static Map<String,InterInfo> cache = new HashMap<>();

	/**
	 * 异常统一抛到ServiceAgent层处理
	 */
	@Override
	public Object doService(APIReq req) throws Exception {
		InterInfo interInfo = getInterInfo(req);
		if (interInfo == null || !interInfo.isValid()) {
			throw new APIException(APIExceptionEnum.FAIL_PROTOCOL_NOBANGD,"接口【" + req.getInterId() + "】");
		}
		if (interInfo.getProjectId() != projectId) {// 跨项目访问，禁止
			throw new APIException(APIExceptionEnum.FAIL_ILLEGAL_REQ,
					"跨项目，禁止访问，[" + projectId + "]-[" + interInfo.getProjectId() + "]");
		}
		// 判断是否为当前项目
		req.setInterInfo(interInfo);
		// 反射生成实例
		ServiceModule sm = ClassLoaderCache.loadServiceModule(baseDao, interInfo.getExeClass());
		if (sm == null) {
			throw new APIException("接口【" + interInfo.getInterId() + "】找不到入口执行类:" + interInfo.getExeClass());
		}
		// 执行业务
		Object data = sm.doService(req);
		return data;
	}

	@Override
	public InterInfo getInterInfo(APIReq req) {
		String interId = req.getInterId();
		String key = "interInfo_" + interId + "V" + req.getVersion();
		InterInfo interInfo = cache.get(key);
		if(interInfo != null){
			return interInfo;
		}
		log.warn("缓存拿不到接口["+interId+"]的配置信息，从数据库中获取");
		try {
			StringBuilder sql = new StringBuilder(
					"SELECT t1.interId,t1.name,t2.fullpackage,t1.projectId FROM t_inter t1 LEFT JOIN t_inter_class t2 ON(t1.interId=t2.interId AND t2.type=0 AND t2.isUse=1 AND t2.version=")
							.append(req.getVersion()).append(") WHERE t1.interId='")
							.append(DBUtils.mysql_varchar_escape(interId)).append("' AND t1.isUse=1");
			Object[] infos = baseDao.getObjects(sql.toString());
			if (infos == null) {
				throw new APIException("查找不到该接口的配置信息:" + req);
			}
			// 数据库数据合法
			interInfo = new InterInfo(infos);
			interInfo.setVesion(req.getVersion());
			cache.put(key,interInfo);
			return interInfo;
		} catch (APIException e) {
			log.error(e.getMessage());
		} catch (Exception e) {
			log.error(e);
		}
		//添加缓存,数据没有也要添加缓存，防止错误的interId多次查询数据库
		interInfo = new InterInfo();
		cache.put(key,interInfo);
		return interInfo;
	}

	public static void delInterInfoCache(String key){
		cache.remove(key);
	}
	public static void clearInterInfoCache(){
		cache.clear();
	}

	@Override
	public void registerSyncInters(ServletContext context) {
		try {
			log.info("注册Servlet Listener启动");
			// 查找对应的接口服务信息
			List<Object[]> infos = baseDao
					.getObjectList("SELECT sname,url,servletClass FROM t_inter_sync WHERE isUse=1 AND projectId="+projectId);
			if (CollectionUtils.isEmpty(infos)) {
				log.info("没有服务接口需要注册");
				return;
			}
			int counter = 0;
			for (Object[] obj : infos) {
				String sname = StringUtils.getCleanString(obj[0]);
				String url = StringUtils.getCleanString(obj[1]);
				String sclazz = StringUtils.getCleanString(obj[2]);
				if (StringUtils.isEmpty(sname) || StringUtils.isEmpty(url) || StringUtils.isEmpty(sclazz)) {
					log.info("服务配置信息不合法：sname=" + sname + " url=" + url);
					continue;
				}
				// 注册服务
				ServletRegistration register = context.addServlet(sname, sclazz);
				// 为动态注册的Servlet设定访问URL(可设定多个)
				register.addMapping(url);
				// 设置初始化参数
				register.setInitParameter("sname", sname);
				counter++;
				log.info("注册接口【" + sname + "】-servletClass:" + sclazz + "-" + url);
			}
			log.info("注册了" + counter + "个 servlet 服务");

		} catch (Exception e) {
			log.error(e);
		}
	}

}
