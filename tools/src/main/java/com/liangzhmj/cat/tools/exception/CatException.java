package com.liangzhmj.cat.tools.exception;

import lombok.Getter;

@Getter
public class CatException extends RuntimeException{

    protected String code;
    protected String message;

    public CatException(){}

    public CatException(String message){
        super(message);
        this.message = message;
    }

    public CatException(Throwable e){
        super(e);
    }

    public CatException(CatExceptionEnum bee){
        super(bee.getMessage());
        this.code = bee.getCode();
        this.message = bee.getMessage();
    }

    public CatException(CatExceptionEnum bee, String ex){
        super(bee.getMessage()+":"+ex);
        this.code = bee.getCode();
        this.message = bee.getMessage()+":"+ex;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
