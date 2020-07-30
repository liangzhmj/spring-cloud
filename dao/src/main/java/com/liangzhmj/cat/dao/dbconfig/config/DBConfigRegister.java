package com.liangzhmj.cat.dao.dbconfig.config;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * 注册一个DBConfig的管理接口
 * 这跟DataSource（默认）的一样，DataSource了没有参数的Setter方法，确能够通过@ConfigurationProperties设置属性
 * 相信是spring从中作梗
 * 这个是不需要@ServletComponentScan的
 */
@Log4j2
@Setter
public class DBConfigRegister {

    @Value("${dbconfig.admin.allowips:}")
    private String allowIps;

    @Bean
    public ServletRegistrationBean DBConvigViewServlet(){
       log.info("实例化DBConfig管理Servlet...");
        //org.springframework.boot.context.embedded.ServletRegistrationBean提供类的进行注册.
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new DBConfigServlet(),"/dbconfig");
        //添加初始化参数
        servletRegistrationBean.addInitParameter("allowIps",allowIps);
        return servletRegistrationBean;
    }

}
