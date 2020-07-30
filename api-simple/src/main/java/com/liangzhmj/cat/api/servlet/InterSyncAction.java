package com.liangzhmj.cat.api.servlet;

import com.liangzhmj.cat.api.enums.APIExceptionEnum;
import com.liangzhmj.cat.api.exception.APIException;
import com.liangzhmj.cat.api.protocol.ServiceAgency;
import com.liangzhmj.cat.api.protocol.req.APIReq;
import com.liangzhmj.cat.api.protocol.resp.Result;
import com.liangzhmj.cat.api.utils.ServletUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 服务入口类(同步,逻辑完全和InterfaceAction一样，考虑项目规模，一些锁操作主要是基于java内部的实现，方便nginx路径区分，做单点同步逻辑（不支持分布式）)
 */
@SuppressWarnings("serial")
@Log4j2
@WebServlet(name="InterSyncAction",urlPatterns="/interSyncAction")
public class InterSyncAction extends HttpServlet {

	private ServiceAgency serviceAgency;
//	private AuthManager authManager;
	@Override
	public void init() throws ServletException {
		try {
			WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
			serviceAgency = wac.getBean("serviceAgency", ServiceAgency.class);
//			authManager = wac.getBean("authManager", AuthManagerImpl.class);
			if(serviceAgency == null){throw new RuntimeException("实例化serviceAgent失败");}
		} catch (Exception e) {
			log.fatal(e);
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		long start = System.currentTimeMillis();
		APIReq rqs = null;
		try {
			rqs = new APIReq(request,response);  //实例化协议
			rqs.reciveRequest();  //接收协议内容
			serviceAgency.start(rqs);
		} catch(APIException e){
			long end = System.currentTimeMillis();
			Result apie = Result.fail(e);
			ServletUtils.returnRes(apie,rqs,(end-start));
		} catch (Exception e) {
			long end = System.currentTimeMillis();
			Result unke = Result.fail(new APIException(APIExceptionEnum.FAIL_UNKNOWN,e.getMessage()));
			ServletUtils.returnRes(unke,rqs,(end-start));
			log.error(e);
		}
	}
}
