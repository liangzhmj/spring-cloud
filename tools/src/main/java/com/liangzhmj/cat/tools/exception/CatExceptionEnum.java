package com.liangzhmj.cat.tools.exception;

/**
 * 异常的枚举接口，被枚举继承（因为枚举之间不能继承），在调用的时候使用枚举接口实现多态
 * @author liangzhmj
 */
public interface CatExceptionEnum {

    //客户端万能提示
    public static String SERVER_BUSY = "服务器忙,请稍后再试";

    String getCode();
    String getMessage();
}
