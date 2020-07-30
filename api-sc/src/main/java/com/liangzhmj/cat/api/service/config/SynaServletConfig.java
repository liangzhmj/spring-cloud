package com.liangzhmj.cat.api.service.config;

import com.liangzhmj.cat.api.exception.APIException;
import com.liangzhmj.cat.api.service.ServiceEngine;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@Log4j2
public class SynaServletConfig implements ServletContextInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        long start = System.currentTimeMillis();
        //动态注册servlet
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        ServiceEngine serviceEngine = (ServiceEngine) wac.getBean("serviceEngine", ServiceEngine.class);
        if(serviceEngine == null){
            throw new APIException("获取serviceEngine失败");
        }
        serviceEngine.registerSyncInters(servletContext);
        long end = System.currentTimeMillis();
        log.info("注册动态接口执行完毕:"+(end-start)+"ms");

    }
}
