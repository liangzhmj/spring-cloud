package com.liangzhmj.cat.api.aop.aspect;


import com.liangzhmj.cat.api.protocol.req.APIReq;
import com.liangzhmj.cat.api.protocol.resp.Result;

/**
 * 切面接口
 * @author liangzhmj
 *
 */
public interface APIAspect {

	/**
	 * 在执行业务之前调用，可以做鉴权，修改APIReq参数等
	 * @param req
	 * @return
	 */
	public APIReq doPrepare(APIReq req) throws Exception;
	/**
	 * 在执行之后调用（没有抛异常），可以做data的修改，做日志等一些切面操作(如果业务抛异常，有可能不执行)
	 * @param req
	 * @param data
	 * @return
	 */
	public Object doAfter(APIReq req, Object data) throws Exception;
	/**
	 * 在执行之后finally调用（包括抛异常），可以做一下资源回收，日志，甚至是修改result.code
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public Result doFinally(APIReq req, Result res) throws Exception;
}
