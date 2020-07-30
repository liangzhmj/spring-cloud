package com.liangzhmj.cat.api.engine.model;

import com.liangzhmj.cat.api.aop.aspect.APIAspect;
import com.liangzhmj.cat.api.service.ServiceModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 存储注解配置信息
 * @author liangzhmj
 */
public class EngineContext {

    /** 存储逻辑类的配置,key:interId,value:逻辑对象 **/
    public static Map<String, ServiceModule> services = new HashMap<>();
    /** 存储切面类的配置:aop对象 **/
    public static List<APIAspect> aops = new ArrayList<>();


}
