package com.liangzhmj.cat.api.aop.impl;

import com.liangzhmj.cat.api.aop.AspectChain;
import com.liangzhmj.cat.api.aop.aspect.APIAspect;
import com.liangzhmj.cat.api.engine.ClassLoaderEngine;
import com.liangzhmj.cat.api.exception.APIException;
import com.liangzhmj.cat.api.protocol.req.APIReq;
import com.liangzhmj.cat.api.protocol.resp.Result;
import com.liangzhmj.cat.dao.mysql.APIDao;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 业务切面链
 * @author liangzhmj
 *
 */
@Component("serviceAspectChain")
@Log4j2
public class ServiceAspectChain implements AspectChain {
	
	/** 缓存切面更新的时间戳 **/
	private Map<String,String> cache = new HashMap<String,String>();
	@Resource(name="baseDao")
	private APIDao baseDao;
	private APIAspect aspect;//切面实体，应该是列表的，暂时先只支持一个切面;
	@Value("${api.aop.regexp:#{null}}")
	private String classRegexp;

	/**
	 * 初始化切面
	 */
	@Override
	@PostConstruct
	public synchronized void init(){
		//获取匹配的aop类
		try {
			if(StringUtils.isEmpty(classRegexp)){
				throw new APIException("没有配置切面");
			}
			List<Object[]> asps = baseDao.getObjectList("SELECT fullpackage,updatetime FROM t_inter_class WHERE isUse=1 AND type=1 AND fullpackage REGEXP '"+classRegexp+"'");
			if(CollectionUtils.isEmpty(asps) || asps.size() != 1){
				throw new APIException("数据库中对应的切面class不是一个");
			}
			Object[] res = asps.get(0);
			String fp = StringUtils.getCleanString(res[0]);
			String updatetime = StringUtils.getCleanString(res[1]);
			if(StringUtils.isEmpty(fp) || StringUtils.isEmpty(updatetime)){
				throw new APIException("加载aop["+fp+"]-["+updatetime+"]失败");
			}
			//获取这个类最近更新的时间戳
			String lasttime = cache.get(fp);
			if(!StringUtils.isEmpty(lasttime) && lasttime.compareTo(updatetime)>=0){//没有更新
				throw new APIException("aop["+fp+"]-src["+lasttime+"]-dest["+updatetime+"]-不用更新");
			}
			Class<?> clazz = ClassLoaderEngine.loadClass(baseDao,fp);
			if(clazz == null){
				throw new APIException("加载aop["+fp+"]失败");
			}
			aspect = (APIAspect)clazz.newInstance();
			//更新时间
			cache.put(fp, updatetime);
		} catch(APIException e){
			log.warn("业务AOP链初始化异常,此次初始化无效:"+e.getMessage());
		} catch (Exception e) {
			log.warn("业务AOP链初始化异常,此次初始化无效:",e);
		}
	}
	/**
	 * 业务逻辑之前执行的代码
	 * @param req
	 * @return
	 */
	@Override
	public APIReq doPrepares(APIReq req) throws Exception{//异常跑到ServiceAgent统一异常处理
		if(aspect == null){//没有切面
			return req;
		}
		return aspect.doPrepare(req);
	}
	/**
	 * 业务逻辑之后执行的代码
	 * @param req
	 * @param data
	 * @return
	 */
	@Override
	public Object doAfters(APIReq req,Object data) throws Exception{//异常跑到ServiceAgent统一异常处理
		if(aspect == null){//没有切面
			return data;
		}
		return aspect.doAfter(req, data);
	}
	@Override
	public Result doFinally(APIReq req, Result res) throws Exception {
		if(aspect == null){//没有切面
			return res;
		}
		return aspect.doFinally(req, res);
	}

	public String getClassRegexp() {
		return classRegexp;
	}
}
