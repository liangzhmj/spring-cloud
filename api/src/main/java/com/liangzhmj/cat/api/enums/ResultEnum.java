package com.liangzhmj.cat.api.enums;

import lombok.Getter;

/**
 * 返回客户端result非异常枚举
 * @author liangzhmj
 */
@Getter
public enum ResultEnum {



    /**
     * 返回给客户端
     */
    SUCCESS("00", "处理成功");


    ResultEnum(String code, String message, String showMsg) {
        this.code = code;
        this.message = message;
        this.showMsg = showMsg;
    }
    ResultEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private String code;
    private String message;//debugMsg真实调试的信息
    private String showMsg;//给用户看的信息


}
