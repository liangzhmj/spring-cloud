package com.liangzhmj.cat.dao.mysql.config;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.util.StringUtils;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * 由于这里的数据源bean是通过import引入的，可能错过了spring对druid监控web的初始化，因此这里也要手动注册下
 * （其实是有的20200721）则跟DataSource（默认）的一样，DataSource了没有参数的Setter方法，确能够通过@ConfigurationProperties设置属性
 * 相信是spring从中作梗
 * 这个是不需要@ServletComponentScan的
 */
@Log4j2
@Setter
public class DruidMonitor {

    @Value("${monitor.druid.ips:#{null}}")
    private String ips;
    @Value("${monitor.druid.username:admin}")
    private String username;
    @Value("${monitor.druid.password:123456}")
    private String password;
    @Value("${monitor.druid.path:druid}")
    private String path;

    @Bean
    public ServletRegistrationBean DruidStatViewServlet(){
       log.info("实例化Druid监控Servlet...");
        //org.springframework.boot.context.embedded.ServletRegistrationBean提供类的进行注册.
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(),"/"+path+"/*");
        //添加初始化参数
        if(!StringUtils.isEmpty(ips)){
            servletRegistrationBean.addInitParameter("allow",ips);
        }
        servletRegistrationBean.addInitParameter("loginUsername",username);
        servletRegistrationBean.addInitParameter("loginPassword",password);
        log.info("druid-monitor-->ips["+ips+"]-username["+username+"]-password["+password+"]-path["+path+"]");
        //是否可以重置
        servletRegistrationBean.addInitParameter("resetEnable","false");
        return servletRegistrationBean;
    }

    /**
     * 注册一个：filterRegistrationBean
     * @return
     */
    @Bean
    public FilterRegistrationBean druidStatFilter(){
        log.info("实例化Druid监控Filter...");
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
        //添加过滤规则.
        filterRegistrationBean.addUrlPatterns("/*");
        //添加不需要忽略的格式信息.
        filterRegistrationBean.addInitParameter("exclusions","*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }

}
