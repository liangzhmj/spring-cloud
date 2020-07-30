package com.liangzhmj.cat.dao.exception;

import com.liangzhmj.cat.tools.exception.CatException;

public class DaoException extends CatException {

    public DaoException(){}
    public DaoException(Throwable e){
        super(e);
    }
    public DaoException(String message){
        super(message);
        this.message = message;
    }
}
