package com.liangzhmj.cat.api.exception;

import com.liangzhmj.cat.api.enums.APIExceptionEnum;
import com.liangzhmj.cat.tools.exception.CatException;
import com.liangzhmj.cat.tools.exception.CatExceptionEnum;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.Setter;

/**
 * api模块统一异常处理
 * @author liangzhmj
 */
public class APIException extends CatException {
    //message为debugMsg
    private String showMsg;//返回给客户端的提示
    @Setter
    private boolean copyDebugMsg = false;//如果showMsg为空，则设置是否copy debugMsg,在客户端只看到showMsg看不到debugMsg（节省传送数据）

    /**
     * 返回直接给用户看的提示信息
     * @param showMsg
     * @return
     */
    public static APIException showMsg(String showMsg){//由于构造方法单个string参数被占了，因此用静态方法
        return APIException.showMsg(null,showMsg);
    }

    /**
     * 返回直接给用户看的提示信息
     * @param code
     * @param showMsg
     * @return
     */
    public static APIException showMsg(String code,String showMsg){//由于构造方法单个string参数被占了，因此用静态方法
        APIException e = new APIException(code,showMsg);//实际上是设置了message，但是把copy设置回来就好了
        e.setCopyDebugMsg(true);
        return e;
    }
    public APIException(){}
    public APIException(String message){
        super(message);
        this.message = message;
    }
    public APIException(String code,String message){
        super(message);
        this.code = code;
        this.message = message;
    }

    public APIException(CatExceptionEnum cee){
        super(cee);
        APIExceptionEnum aee = (APIExceptionEnum)cee;
        if(StringUtils.isEmpty(aee.getShowMsg())){
            copyDebugMsg = true;
        }
        this.showMsg = aee.getShowMsg();
    }

    public APIException(CatExceptionEnum cee, String ex){//常用，带上原异常信息
        super(cee,ex);
        APIExceptionEnum aee = (APIExceptionEnum)cee;
//        if(StringUtils.isEmpty(aee.getShowMsg())){
//            copyDebugMsg = true;
//        }
        this.showMsg = aee.getShowMsg();
    }
    public APIException(CatExceptionEnum cee, String ex,String showMsg){
        super(cee,ex);
        this.showMsg = showMsg;
    }
    public APIException(String code,String showMsg,String debugMsg){
        super(debugMsg);
        this.code = code;
        this.showMsg = showMsg;
        this.message = debugMsg;
    }

    /**
     * 获取接口提示信息（直接展示给用户）
     * @return
     */
    public String getHint(){
        if(this.showMsg != null){//优先级最高
            return this.showMsg;
        }
        //如果为空，看是否复制信息
        if(copyDebugMsg){
            return this.message;//返回错误信息
        }
        return APIExceptionEnum.SERVER_BUSY;//如果既没值，有没复制，则返回默认信息
    }

    /**
     * 获取debug信息
     * @return
     */
    public String getMessage(){
        if(copyDebugMsg){//如果是复制属性，则提示信息就是debug信息，不用返回
            return null;
        }
        return this.message;//返回debug信息
    }

}
