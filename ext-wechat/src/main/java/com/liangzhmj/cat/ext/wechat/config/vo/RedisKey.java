package com.liangzhmj.cat.ext.wechat.config.vo;

import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.Getter;

/**
 * 项目中redis的相关key
 */
@Getter
public class RedisKey {

    //前缀
    public String prefix;
    //----------------------第三方----------------------------start
    //第三方预授权码+第三方appid
    public String thirdPreCode;
    //第三方平台token+第三方appid
    public String componentToken;
    //第三方平台验证ticket+第三方appid
    public String verifyTicket;
    //授权跟第三方平台的授权号的token+授权号appid
    public String authToken;
    //授权跟第三方平台的授权号的刷新token+授权号appid
    public String authRefreshToken;
    //----------------------第三方----------------------------end

    //----------------------单个----------------------------end
    public String token;


    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix(){
        if(this.prefix == null){
            return "WECHAT_MODULE_";
        }
        return prefix;
    }

    public void setThirdPreCode(String thirdPreCode) {
        if(StringUtils.isEmpty(thirdPreCode)){
            thirdPreCode = "THIRD_PRECODE_";
        }
        this.thirdPreCode = getPrefix()+thirdPreCode;
    }

    public void setComponentToken(String componentToken) {
        if(StringUtils.isEmpty(componentToken)){
            componentToken = "COMPONENT_TOKEN_";
        }
        this.componentToken = getPrefix() + componentToken;
    }

    public void setVerifyTicket(String verifyTicket) {
        if(StringUtils.isEmpty(verifyTicket)){
            verifyTicket = "VERIFY_TICKET_";
        }
        this.verifyTicket = getPrefix() + verifyTicket;
    }

    public void setAuthToken(String authToken) {
        if(StringUtils.isEmpty(authToken)){
            authToken = "AUTH_TOKEN_";
        }
        this.authToken = getPrefix() + authToken;
    }

    public void setAuthRefreshToken(String authRefreshToken) {
        if(StringUtils.isEmpty(authRefreshToken)){
            authRefreshToken = "AUTH_REFRESH_TOKEN_";
        }
        this.authRefreshToken = getPrefix() + authRefreshToken;
    }

    public void setToken(String token) {
        if(StringUtils.isEmpty(token)){
            token = "TOKEN_";
        }
        this.token = getPrefix() + token;
    }
}
