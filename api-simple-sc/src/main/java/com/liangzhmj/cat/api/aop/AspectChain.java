package com.liangzhmj.cat.api.aop;


import com.liangzhmj.cat.api.protocol.req.APIReq;
import com.liangzhmj.cat.api.protocol.resp.Result;

public interface AspectChain {

	public void init();

	/**
	 * 切面链在执行业务之前调用，可以做鉴权，修改APIReq参数等
	 * @param req
	 * @return
	 * @throws Exception
	 */
	public APIReq doPrepares(APIReq req) throws Exception;

	/**
	 * 切面链在执行之后调用（没有抛异常），可以做data的修改，做日志等一些切面操作(如果业务抛异常，有可能不执行)
	 * @param req
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public Object doAfters(APIReq req, Object data) throws Exception;

	/**
	 * 切面链在执行之后finally调用（包括抛异常），可以做一下资源回收，日志，甚至是修改result.code
	 * @param req
	 * @param res
	 * @return 返回最终的result
	 * @throws Exception
	 */
	public Result doFinally(APIReq req, Result res) throws Exception;
}
