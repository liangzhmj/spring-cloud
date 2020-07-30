package com.liangzhmj.cat.api.aop.impl;

import com.liangzhmj.cat.api.aop.AspectChain;
import com.liangzhmj.cat.api.aop.aspect.APIAspect;
import com.liangzhmj.cat.api.engine.model.EngineContext;
import com.liangzhmj.cat.api.protocol.req.APIReq;
import com.liangzhmj.cat.api.protocol.resp.Result;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 业务切面链
 * @author liangzhmj
 *
 */
@Component("serviceAspectChain")
@Log4j2
public class ServiceAspectChain implements AspectChain {

	private List<APIAspect> aops;

	/**
	 * 初始化切面
	 */
	@Override
	@PostConstruct
	public synchronized void init(){
		this.aops = EngineContext.aops;
	}
	/**
	 * 业务逻辑之前执行的代码
	 * @param req
	 * @return
	 */
	@Override
	public APIReq doPrepares(APIReq req) throws Exception{//异常跑到ServiceAgent统一异常处理
		if(CollectionUtils.isEmpty(aops)){//没有切面
			return req;
		}
		for (APIAspect aop : aops) {//正序访问
			req = aop.doPrepare(req);
		}
		return req;
	}
	/**
	 * 业务逻辑之后执行的代码
	 * @param req
	 * @param data
	 * @return
	 */
	@Override
	public Object doAfters(APIReq req,Object data) throws Exception{//异常跑到ServiceAgent统一异常处理
		if(CollectionUtils.isEmpty(aops)){//没有切面
			return data;
		}
		for (int i = aops.size()-1; i > -1; i--) {//倒序访问
			APIAspect aop = aops.get(i);
			data = aop.doAfter(req,data);
		}
		return data;
	}
	@Override
	public Result doFinally(APIReq req, Result res) throws Exception {
		if(CollectionUtils.isEmpty(aops)){//没有切面
			return res;
		}
		for (int i = aops.size()-1; i > -1; i--) {//倒序访问
			APIAspect aop = aops.get(i);
			res = aop.doFinally(req,res);
		}
		return res;
	}

}
