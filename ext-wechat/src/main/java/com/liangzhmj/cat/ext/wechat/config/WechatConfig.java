package com.liangzhmj.cat.ext.wechat.config;

import com.liangzhmj.cat.ext.wechat.config.vo.RedisKey;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component("wechatConfig")
@ConfigurationProperties(prefix = "wechat")
@Data
@Log4j2
public class WechatConfig {

    private RedisKey redisKey;
    //第三方平台获取token路径
    private String ctokenUrl;
    //第三方平台获取授权号token路径
    private String mtokenUrl;
    //证书路径前缀
    private String certPathPrefix;
    //退款订单后缀
    private String rorderSuffix;

    public WechatConfig() {
        log.info("初始化微信模块配置...");
    }


}
