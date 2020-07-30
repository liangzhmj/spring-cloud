package com.liangzhmj.cat.api.enums;

import lombok.Getter;

/**
 * 协议字段枚举
 * @author liangzhmj
 */
@Getter
public enum ProtocolEnum {


    ADMIN_REQ_METHOD("method"),//暴露给后台管理接口的方法的key
    ADMIN_METHOD_RELOADCLASS("reloadClass"),//后台管理接口重载class的key
    ADMIN_METHOD_CLEARCLASS("clearClass"),//后台管理接口清空class缓存的key
    ADMIN_METHOD_CACHE_CLERNBYNAME("clearCacheByName"),//后台管理接口清空缓存的key
    ADMIN_METHOD_CACHE_DELBYKEY("delByKey"),//后台管理接口删除缓存的key
    ADMIN_METHOD_APIDOC("apidoc");//api文档




    ProtocolEnum(String fielName, Object defaultVal) {
        this.fielName = fielName;
        this.defaultVal = defaultVal;
    }
    ProtocolEnum(String fielName) {
        this.fielName = fielName;
    }

    private String fielName;
    private Object defaultVal;


}
