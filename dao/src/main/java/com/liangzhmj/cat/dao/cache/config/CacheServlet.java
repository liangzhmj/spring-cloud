package com.liangzhmj.cat.dao.cache.config;


import com.liangzhmj.cat.dao.cache.EhcacheContext;
import com.liangzhmj.cat.dao.exception.DaoException;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * 对外发布DBConfig的管理服务
 */
@SuppressWarnings("serial")
@Log4j2
public class CacheServlet extends HttpServlet {

//	@Value("${cache.admin.allowips:#{null}}")
	private String allowIps;//这里通过@Value是获取不到的
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		try {
			String ip = getIp(request);
			//ip非法
			if((ip == null || allowIps == null || allowIps.indexOf(ip) < 0) && !"all".equals(allowIps)){
				throw new DaoException("["+ip+"]非法访问");
			}
			String method = request.getParameter("method");
			String name = request.getParameter("name");
			if("clear".equals(method)){
				EhcacheContext.clearCache(name);
				out.write("success");
			}else if("del".equals(method)){
				String key = request.getParameter("key");
				EhcacheContext.delCache(name,key);
				out.write("success");
			}else if("get".equals(method)){
				String key = request.getParameter("key");
				Object val = EhcacheContext.getCache(name,key);
				out.write("value:"+val);
			}
		} catch (Exception e) {
			log.error(e);
			out.write("fail");
		}finally{
			if(out != null){
				out.flush();
				out.close();
			}
		}
	}

	public void init() throws ServletException {
		super.init();
		allowIps = this.getInitParameter("allowIps");
	}

	public static String getIp(HttpServletRequest request) {
		if (request == null) {
			return null;
		}
		String ipaddress = null;
		if (request.getHeader("x-forwarded-for") == null) {
			ipaddress = request.getRemoteAddr();
		} else {
			ipaddress = request.getHeader("x-forwarded-for");
		}
		if (!StringUtils.isEmpty(ipaddress)) {
			String[] s = ipaddress.split(",");
			if (s.length == 1) {
				ipaddress = s[0].trim();
			} else if (s.length > 1) {
				ipaddress = s[s.length - 2].trim();
			}
		}
		return ipaddress;
	}
	
	
}
