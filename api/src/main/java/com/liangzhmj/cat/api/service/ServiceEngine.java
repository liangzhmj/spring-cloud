package com.liangzhmj.cat.api.service;

import com.liangzhmj.cat.api.model.InterInfo;
import com.liangzhmj.cat.api.protocol.req.APIReq;

import javax.servlet.ServletContext;

/**
 * 服务引擎接口
 * @author liangzhmj
 *
 */
public interface ServiceEngine {

	/**
	 * 执行业务
	 * @param req
	 * @return 返回ServiceModule.doService()返回的对象，在ServiceAgency中直接放到result.data中
	 * @throws Exception
	 */
	Object doService(APIReq req) throws Exception;
	/**
	 * 获取app基础信息
	 * @param req
	 * @return
	 */
	InterInfo getInterInfo(APIReq req);
	/**
	 * 注册同步接口
	 * @param context
	 */
	void registerSyncInters(ServletContext context);
}
