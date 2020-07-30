package com.liangzhmj.cat.api.enums;

import com.liangzhmj.cat.tools.exception.CatExceptionEnum;
import lombok.Getter;

/**
 * 异常枚举
 * @author liangzhmj
 */
@Getter
public enum APIExceptionEnum implements CatExceptionEnum {



    /**
     * 返回给客户端
     */
    FAIL_UNKNOWN("-1", "未知异常"),
    FAIL_ILLEGAL_REQ("10", "非法访问",SERVER_BUSY),
    FAIL_ILLEGAL_DATA("11", "非法数据",SERVER_BUSY),
    FAIL_ILLEGAL_BUSINESS("12", "非法业务",SERVER_BUSY),
    FAIL_PROTOCOL_ERROR("20", "协议错误",SERVER_BUSY),
    FAIL_PROTOCOL_CODE("21", "操作码异常",SERVER_BUSY),
    FAIL_PROTOCOL_PARAM("22", "参数错误",SERVER_BUSY),
    FAIL_PROTOCOL_NOBANGD("23", "应用没有绑定服务",SERVER_BUSY),
    FAIL_SERVICE_NO_AUTH("30","没有权限","抱歉，您所在用户组没有权限"),
    FAIL_SERVICE_NO_RESUORCE("31","没有资源","抱歉，暂时没有查询到对应的资源的信息"),
    FAIL_SERVICE_SESSION_TIMEOUT("32","会话超时","抱歉，您的登录状态已失效，请重新登录"),
    FAIL_SERVICE_ACCESS_LIMIT("33","访问上限","抱歉，您已到达最大次数"),
    FAIL_SERVICE_DAY_LIMIT("34","访问日限","抱歉，您当天已达次数上限，请明天再试"),
    FAIL_SERVICE_MONTH_LIMIT("35","访问月限","抱歉，您当月已达次数上限，请下月再试"),
    FAIL_SERVICE_SIGH("36","签名错误",SERVER_BUSY),
    FAIL_SERVICE_OPERATION("50","操作异常");//在代码里自定义showMsg

    APIExceptionEnum(String code, String message, String showMsg) {
        this.code = code;
        this.message = message;
        this.showMsg = showMsg;
    }
    APIExceptionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private String code;
    private String message;//debugMsg真实调试的信息
    private String showMsg;//给用户看的信息


}
