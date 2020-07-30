package com.liangzhmj.cat.ext.wechat.config;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("wechatConfigContext")
public class ConfigContext {

    private static WechatConfig wechatConfig;

    public static WechatConfig getWechatConfig() {
        return wechatConfig;
    }


    @Resource
    public void setWechatConfig(WechatConfig wechatConfig) {
        ConfigContext.wechatConfig = wechatConfig;
    }

}
