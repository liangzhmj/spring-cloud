package com.liangzhmj.cat.sync.lock.exception;

import com.liangzhmj.cat.tools.exception.CatException;

public class SyncException extends CatException {

    public SyncException(){}
    public SyncException(Throwable e){
        super(e);
    }
    public SyncException(String message){
        super(message);
        this.message = message;
    }
}
