package com.liangzhmj.cat.api.protocol;

import com.liangzhmj.cat.api.aop.AspectChain;
import com.liangzhmj.cat.api.enums.APIExceptionEnum;
import com.liangzhmj.cat.api.exception.APIException;
import com.liangzhmj.cat.api.protocol.req.APIReq;
import com.liangzhmj.cat.api.protocol.resp.Result;
import com.liangzhmj.cat.api.service.ServiceEngine;
import com.liangzhmj.cat.api.utils.ServletUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 服务代理类
 * @author liangzhmj
 *
 */
@Component("serviceAgency")
@Log4j2
public class ServiceAgency{

	@Resource
	private ServiceEngine serviceEngine;
	@Resource
	private AspectChain serviceAspectChain;

	public void start(APIReq req) {
		try {
			//调用doAction方法执行业务逻辑
			Result res = null;
			long start = System.currentTimeMillis();
			//======================== 执行切面和业务，统一异常处理---start ================================
			try {
				//执行业务之前的切面
				req = serviceAspectChain.doPrepares(req);
				//执行业务逻辑
				Object data = serviceEngine.doService(req);
				//执行业务之后的切面
				data = serviceAspectChain.doAfters(req, data);
				//执行成功
				res = Result.success(data);
			} catch(APIException e){//业务类内不做异常处理,统一在这里做
				res = Result.fail(e);//业务异常（不是异常的异常）
				log.error(e.getMessage());
			} catch (Throwable e) {
				log.error(e);
				res = Result.fail(new APIException(APIExceptionEnum.FAIL_UNKNOWN,e.getMessage()));//未知异常
			} finally {
				//最终执行的
				try {
					//res肯定不为空的
					res = serviceAspectChain.doFinally(req, res);
				} catch (Exception e) {
					log.info(e);
				}
			}
			//======================== 执行切面和业务，统一异常处理---end   ================================
			long end = System.currentTimeMillis();
			//返回结果
			ServletUtils.returnRes(res,req,(end-start));
		} catch (IOException e) {
			log.error(e);
		}
	}
	
	


	
}
