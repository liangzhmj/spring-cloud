package com.liangzhmj.cat.mq.exception;

import com.liangzhmj.cat.tools.exception.CatException;

/**
 * mq模块的异常类，用于分类拦截
 */
public class MqException extends CatException {

    public MqException(){}
    public MqException(Throwable e){
        super(e);
    }
    public MqException(String message){
        super(message);
        this.message = message;
    }
}
