package com.liangzhmj.cat.ext.wechat.exception;

import com.liangzhmj.cat.tools.exception.CatException;

/**
 * 处理微信模块的异常
 * @author liangzhmj
 */
public class WechatException extends CatException {

    public WechatException(){}
    public WechatException(Throwable e){
        super(e);
    }
    public WechatException(String message){
        super(message);
        this.message = message;
    }
}
