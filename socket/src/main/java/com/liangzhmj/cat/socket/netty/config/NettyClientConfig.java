package com.liangzhmj.cat.socket.netty.config;

import com.liangzhmj.cat.socket.netty.config.vo.Config;
import com.liangzhmj.cat.socket.netty.service.NettyClient;
import com.liangzhmj.cat.tools.collection.CollectionUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletContextInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.List;

@Log4j2
@Setter
@Getter
@ConfigurationProperties(prefix = "socket.netty.client")//这里报错不用管，应该是说@ConfigurationProperties要在bean类上，这里通过@Import注入，而编辑并不知道
public class NettyClientConfig implements ServletContextInitializer {

    private List<Config> configs;


    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        log.info("初始化netty客户端");
        if(CollectionUtils.isEmpty(configs)){
            log.info("没有netty.client配置");
            return;
        }
        for (Config config : configs) {
            try {
                log.info("准备初始化netty客户端->"+config);
                NettyClient client = new NettyClient(config.getHost(),config.getPort(),config.getHandler());
                Thread t = new Thread(client);
                t.start();
            }  catch (Exception e) {
                log.error("初始化netty客户端异常:"+config,e);
            }
        }
    }
}
