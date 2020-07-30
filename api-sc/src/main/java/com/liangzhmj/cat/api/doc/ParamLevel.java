package com.liangzhmj.cat.api.doc;

/**
 * api参数层级
 * @author liangzhmj
 */
public enum ParamLevel {
    TOP,//顶层参数，也就是APIReq里面的第一层属性
    PARAMS;//params层参数，也就是APIReq.param中的参数
}
