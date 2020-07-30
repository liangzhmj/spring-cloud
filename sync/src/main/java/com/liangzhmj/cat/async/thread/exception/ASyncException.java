package com.liangzhmj.cat.async.thread.exception;

import com.liangzhmj.cat.tools.exception.CatException;

public class ASyncException extends CatException {

    public ASyncException(){}
    public ASyncException(Throwable e){
        super(e);
    }
    public ASyncException(String message){
        super(message);
        this.message = message;
    }
}
