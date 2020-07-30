package com.liangzhmj.cat.api.servlet;

import com.liangzhmj.cat.api.aop.AspectChain;
import com.liangzhmj.cat.api.engine.ClassLoaderCache;
import com.liangzhmj.cat.api.engine.ClassLoaderEngine;
import com.liangzhmj.cat.api.enums.APIExceptionEnum;
import com.liangzhmj.cat.api.enums.ProtocolEnum;
import com.liangzhmj.cat.api.exception.APIException;
import com.liangzhmj.cat.api.job.SynaJobFactory;
import com.liangzhmj.cat.api.service.APIDocService;
import com.liangzhmj.cat.api.service.impl.ServiceEngineImpl;
import com.liangzhmj.cat.dao.mysql.APIDao;
import com.liangzhmj.cat.tools.http.IPUtil;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

/**
 * 开放给管理后台的接口
 */
@SuppressWarnings("serial")
@WebServlet(name="adminInterfaceAction",urlPatterns={"/adminInterface"})
@Log4j2
public class AdminInterfaceAction extends HttpServlet {

	@Value("${api.admin.manager.ips:127.0.0.1}")
	private String adminManagerIps;
	private APIDao baseDao;
	private AspectChain serviceAspectChain;
	private SynaJobFactory synaJobFactory;
	private APIDocService apiDocService;

	@Override
	public void init() throws ServletException {
		try {
			WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
			baseDao = wac.getBean("baseDao",APIDao.class);
			serviceAspectChain = wac.getBean("serviceAspectChain",AspectChain.class);
			synaJobFactory = wac.getBean("synaJobFactory",SynaJobFactory.class);
			apiDocService = wac.getBean("apiDocService",APIDocService.class);
			if(baseDao == null){throw new RuntimeException("实例化baseDao失败");}
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

		String method = request.getParameter(ProtocolEnum.ADMIN_REQ_METHOD.getFielName());
		Writer writer = response.getWriter();
		try {
			if(StringUtils.isEmpty(method)){
				throw new APIException("缺失method参数");
			}
			String ip = IPUtil.getIp(request);
			//ip非法
			if((ip == null || adminManagerIps == null || adminManagerIps.indexOf(ip) < 0) && !"all".equals(adminManagerIps)){
				throw new APIException("["+ip+"]非法访问");
			}
			//热部署
			if(ProtocolEnum.ADMIN_METHOD_RELOADCLASS.getFielName().equalsIgnoreCase(method)){
				String name = request.getParameter("name");
				log.info("["+ip+"]访问管理员接口-method["+method+"]-热部署name["+name+"]");
				Class<?> clazz = ClassLoaderEngine.defineClass(baseDao, name);
				//清除对象缓存
				ClassLoaderCache.clearCache(name);
				if(clazz != null){
					writer.write("success");
				}else{
					writer.write("fail");
				}
				return;
			}
			//热部署
			if(ProtocolEnum.ADMIN_METHOD_CLEARCLASS.getFielName().equalsIgnoreCase(method)){
				log.info("["+ip+"]访问管理员接口-method["+method+"]-清空热部署缓存");
				//清除对象缓存
				ClassLoaderCache.clearCache();
				//初始化切面
				serviceAspectChain.init();
				//初始化任务队列
				synaJobFactory.init();
				writer.write("success");
				return;
			}

			if(ProtocolEnum.ADMIN_METHOD_CACHE_DELBYKEY.getFielName().equalsIgnoreCase(method)){//method为了适配之前的项目
				String key = request.getParameter("key");
				log.info("["+ip+"]访问管理员接口-method["+method+"]-根据key清除interInfo缓存["+request.getParameter("fieldName")+"]");
				ServiceEngineImpl.delInterInfoCache(key);
				//初始化文档
				apiDocService.initDoc();
				writer.write("success");
				return;
			}
			if(ProtocolEnum.ADMIN_METHOD_CACHE_CLERNBYNAME.getFielName().equalsIgnoreCase(method)){//method为了适配之前的项目
				log.info("["+ip+"]访问管理员接口-method["+method+"]-清空interInfo缓存["+ request.getParameter("fieldName")+"]");
				ServiceEngineImpl.clearInterInfoCache();
				//初始化文档
				apiDocService.initDoc();
				writer.write("success");
				return;
			}
			if(ProtocolEnum.ADMIN_METHOD_APIDOC.getFielName().equalsIgnoreCase(method)){//api文档
				log.info("["+ip+"]访问管理员接口-method["+method+"]-获取api文档");
				JSONObject doc = apiDocService.apiDoc();
				if(doc == null){
					throw new APIException("没有文档");
				}
				log.info(doc);
				writer.write(doc.toString());
				return;
			}
		} catch(APIException e){
			log.warn(e.getMessage());
			writer.write(e.getMessage());
		}catch (Exception e) {
			writer.write(APIExceptionEnum.FAIL_UNKNOWN.getMessage());
			log.error(e);
		}finally{
			if(writer != null){
				writer.flush();
				writer.close();
			}
		}
		
	}

	public String getAdminManagerIps() {
		return adminManagerIps;
	}

	public void setAdminManagerIps(String adminManagerIps) {
		this.adminManagerIps = adminManagerIps;
	}
}
