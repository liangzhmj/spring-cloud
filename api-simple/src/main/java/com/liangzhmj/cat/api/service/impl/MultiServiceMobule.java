package com.liangzhmj.cat.api.service.impl;

import com.liangzhmj.cat.api.exception.APIException;
import com.liangzhmj.cat.api.model.InterMethodInfo;
import com.liangzhmj.cat.api.protocol.req.APIReq;
import com.liangzhmj.cat.api.service.ServiceModule;
import com.liangzhmj.cat.api.service.annotation.After;
import com.liangzhmj.cat.api.service.annotation.Before;
import com.liangzhmj.cat.api.service.annotation.InterMethod;
import com.liangzhmj.cat.tools.collection.CollectionUtils;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 多业务（method注解，参数注解）业务模块
 * 配合@InterMethod,@Before,@After使用
 * @author liangzhmj
 *
 */
@Log4j2
public class MultiServiceMobule implements ServiceModule {

    private Map<String, InterMethodInfo> cache = new HashMap<>();


    public Object defaultLogic(APIReq req) throws Exception {
        throw APIException.simpleException("接口尚未开通","接口尚未开通:"+req);
    }

    /**
     * 执行业务（统一不做异常处理，抛到外部做）
     *
     * @param req
     * @return 返回结果，可以是JSONObject,JSONArray等对象，在ServiceAgency中直接放到result.data中
     */
    @Override
    public Object doService(APIReq req) throws Exception {
        if(StringUtils.isEmpty(req.getMethod())){
            return defaultLogic(req);//没有定义方法
        }
        //从缓存里面取
        InterMethodInfo info = cache.get(req.getMethod());
        if(info == null){
            log.info("缓存中缓存["+req.getMethod()+"]对应的方法信息，利用反射机制获取");
            //获取实现的子类的方法集合
            Method[] methods = this.getClass().getDeclaredMethods();
            if(CollectionUtils.isEmpty(methods)){
                throw APIException.simpleException("接口未开通","该接口["+req.getInterId()+"]没有定义分支");
            }
            //遍历
            for (Method method : methods) {
                //获取注解
                InterMethod im = method.getAnnotation(InterMethod.class);
                if(im == null || !im.value().equals(req.getMethod())){
                    //方法没有添加@InterMethod注解或者method对不上
                    continue;
                }
                log.info("接口["+req.getInterId()+"]存在["+req.getMethod()+"]分支");
                info = new InterMethodInfo(req.getMethod(),method);
                //获取预处理
                Before b = method.getAnnotation(Before.class);
                if(b != null && !CollectionUtils.isEmpty(b.value())){
                    log.info("接口["+req.getInterId()+"]的分支["+req.getMethod()+"]存在预处理方法["+ Arrays.toString(b.value())+"]");
                    for(String bname : b.value()){
                        //获取方法
                        Method bm = this.getClass().getMethod(bname,APIReq.class);
                        info.addBefore(bm);
                    }
                }
                //获取后处理
                After a = method.getAnnotation(After.class);
                if(a != null && !CollectionUtils.isEmpty(a.value())){
                    log.info("接口["+req.getInterId()+"]的分支["+req.getMethod()+"]存在后处理方法["+ Arrays.toString(a.value())+"]");
                    for(String aname : a.value()){
                        //获取方法
                        Method am = this.getClass().getMethod(aname,APIReq.class,Object.class);
                        info.addAfter(am);
                    }
                }
                cache.put(req.getMethod(),info);//添加缓存
                break;
            }
        }
        if(info == null){
            throw APIException.simpleException("接口未开通","没有对应method["+req.getMethod()+"]的分支");
        }
        //处理预处理
        for (Method bm : info.getBefores()) {
            req = (APIReq)bm.invoke(this,req);
        }
        //处理主体逻辑
        Object res = info.getSelf().invoke(this,req);
        //处理后处理
        for (Method am : info.getAfters()) {
            res = am.invoke(this,req,res);
        }
        return res;//返回结果
    }
}
