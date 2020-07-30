package com.liangzhmj.cat.api.servlet;


import com.liangzhmj.cat.api.engine.ClassLoaderCache;
import com.liangzhmj.cat.api.exception.APIException;
import com.liangzhmj.cat.api.service.SyncAdapter;
import com.liangzhmj.cat.dao.mysql.APIDao;
import com.liangzhmj.cat.dao.mysql.impl.BaseDao;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 动态servelt(在listener处注册)
 */
@SuppressWarnings("serial")
@Log4j2
public class DownloadAction extends HttpServlet {
	
	
	private String sname;
	private APIDao baseDao;
	private String logicClass;
	/** 0:测试。1:正式 **/
	private int type = 0;
	
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);//这个一定要放在getServletContext()的签名，不然调用getServeltContext会弹出空指针异常
		sname = config.getInitParameter("sname");
		try {
			WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
			baseDao = wac.getBean("baseDao", BaseDao.class);
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
		try {
			if(StringUtils.isEmpty(logicClass) || type == 0){
				//获取对应的处理逻辑
				Object[] infos = baseDao.getObjects("SELECT logicClass,type FROM t_inter_sync WHERE sname='"+sname+"'");
				if(infos == null || infos.length != 2){
					throw new APIException("RELOAD","-1:unknown error");
				}
				String clazz = StringUtils.getCleanString(infos[0]);
				if(StringUtils.isEmpty(clazz)){
					throw new APIException("RELOAD","001:this inter is free");
				}
				logicClass = clazz;
				type = StringUtils.getCleanInteger(infos[1]);
			}
			
			//加载逻辑类
			SyncAdapter syncAdapter = ClassLoaderCache.checkAdapter(logicClass);
			if(syncAdapter == null){
				type = 0;//再次可以读取数据库
				syncAdapter = ClassLoaderCache.loadSyncAdapter(baseDao, logicClass);
				if(syncAdapter == null){//还是没有
					throw new APIException("RELOAD","002:this inter is unvaliable");
				}
			}
			//执行同步逻辑
			syncAdapter.doAnalyse(request, response);
			//返回执行结果
		} catch (APIException e) {
			String code = e.getCode();
			if("RELOAD".equals(code)){
				logicClass = null;
			}
			log.error(e.getMessage());
		}catch (Exception e) {
			log.error(e);
		}finally{
		}
	}
	
	
}
