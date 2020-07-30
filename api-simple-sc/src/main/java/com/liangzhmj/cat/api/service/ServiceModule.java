package com.liangzhmj.cat.api.service;

import com.liangzhmj.cat.api.protocol.req.APIReq;

/**
 * 业务模块
 * @author liangzhmj
 *
 */
public interface ServiceModule {

	/**
	 * 执行业务（统一不做异常处理，抛到外部做）
	 * @param req
	 * @return 返回结果，可以是JSONObject,JSONArray等对象，在ServiceAgency中直接放到result.data中
	 */
	Object doService(APIReq req) throws Exception;
}
