package com.liangzhmj.cat.api.service.impl;

import com.liangzhmj.cat.api.engine.model.EngineContext;
import com.liangzhmj.cat.api.exception.APIException;
import com.liangzhmj.cat.api.protocol.req.APIReq;
import com.liangzhmj.cat.api.service.ServiceEngine;
import com.liangzhmj.cat.api.service.ServiceModule;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

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


	/**
	 * 异常统一抛到ServiceAgent层处理
	 */
	@Override
	public Object doService(APIReq req) throws Exception {
		// 反射生成实例
		ServiceModule sm = EngineContext.services.get(req.getInterId());
		if (sm == null) {
			throw new APIException("接口【" + req.getInterId() + "】找不到入口执行类");
		}
		// 执行业务
		Object data = sm.doService(req);
		return data;
	}

}
