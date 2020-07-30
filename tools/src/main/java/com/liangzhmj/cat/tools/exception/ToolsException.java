package com.liangzhmj.cat.tools.exception;

public class ToolsException extends CatException {

    public ToolsException(){}
    public ToolsException(Throwable e){
        super(e);
    }
    public ToolsException(String message){
        super(message);
        this.message = message;
    }
}
