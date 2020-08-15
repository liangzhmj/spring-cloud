package com.liangzhmj.cat.api.model;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 配合MultiServiceModule和InterMethod使用
 * 缓存java反射的信息
 * @author liangzhmj
 */
@Data
public class InterMethodInfo {
    private String method;
    private Method self;
    private List<Method> befores;
    private List<Method> afters;

    public InterMethodInfo(String method,Method self){
        this.method = method;
        this.self = self;
        befores = new ArrayList<>();
        afters = new ArrayList<>();
    }
    public void addBefore(Method method){
        this.befores.add(method);
    }
    public void addAfter(Method method){
        this.afters.add(method);
    }
}
