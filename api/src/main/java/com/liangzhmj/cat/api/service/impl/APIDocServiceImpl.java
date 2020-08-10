package com.liangzhmj.cat.api.service.impl;

import com.liangzhmj.cat.api.doc.*;
import com.liangzhmj.cat.api.engine.ClassLoaderEngine;
import com.liangzhmj.cat.api.exception.APIException;
import com.liangzhmj.cat.api.protocol.req.APIReq;
import com.liangzhmj.cat.api.service.APIDocService;
import com.liangzhmj.cat.dao.mysql.APIDao;
import com.liangzhmj.cat.tools.collection.CollectionUtils;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("apiDocService")
@Log4j2
public class APIDocServiceImpl implements APIDocService {

    @Autowired
    private APIDao baseDao;
    @Value("${api.projectId:-1}")
    private int projectId;
    private JSONObject doc = null;

    @Override
    @PostConstruct
    public synchronized void initDoc() throws Exception{
        log.info("初始化接口文档:"+projectId);
        try {
            if(projectId < 1){
                throw new APIException("projectId配置错误，请检查,api.projectId");
            }
            List<Object[]> infos = baseDao.getObjectList("SELECT t1.interId,t1.name,t2.version,t2.fullpackage,t1.remark FROM t_inter t1 JOIN t_inter_class t2 ON(t1.interId=t2.interId) WHERE t2.type=0 AND t2.isUse=1 AND t1.projectId="+projectId+" ORDER BY interId ASC,version ASC");
            if(CollectionUtils.isEmpty(infos)){
                throw new APIException("获取不到到接口class");
            }
            doc = new JSONObject();
            //全局属性
            doc.put("size",infos.size());
            doc.put("projectId",projectId);
            JSONArray apis = new JSONArray();
            for (Object[] info : infos) {
                String interId = StringUtils.getCleanString(info[0]);
                String descr = StringUtils.getCleanString(info[1]);
                int version = StringUtils.getCleanInteger(info[2]);
                String fpackage = StringUtils.getCleanString(info[3]);
                String remark = StringUtils.getCleanString(info[4]);
                try {
                    Class<?> clazz = ClassLoaderEngine.loadClass(baseDao,fpackage);
                    if(clazz == null){
                        throw new APIException("class-["+fpackage+"]获取不到数据库字节码");
                    }
                    int annotationType = 0;//0:注解在类上，1:注解在doServicce方法上
                    Method codeMethod = clazz.getMethod("doService", APIReq.class);
                    ApiDoc apiDoc = clazz.getAnnotation(ApiDoc.class);
                    if(apiDoc == null){
                        log.info("接口["+interId+"V"+version+"]-["+descr+"]在类名上没有声明文档");
                        apiDoc = codeMethod.getAnnotation(ApiDoc.class);
                        if(apiDoc == null){
                            throw new APIException("接口["+interId+"V"+version+"]-["+descr+"]在方法上也没有声明文档");
                        }
                        annotationType = 1;
                    }
                    //接口属性
                    JSONObject api = new JSONObject();
                    api.put("id",interId);
                    api.put("version",version);
                    api.put("descr",descr);
                    api.put("remark",remark);
                    //参数属性
                    ApiParams paramArray = getAnnotation(clazz,codeMethod,ApiParams.class,annotationType);
                    JSONObject methodJson = new JSONObject();//method-param
                    if(paramArray != null){
                        ApiParam[] params = paramArray.value();
                        Map<String, JSONArray> cache = new HashMap<>();
                        JSONArray common = new JSONArray();
                        String commonKey = "commonMethod";
                        for (ApiParam param : params) {//组装method-param
                            JSONObject mparam = new JSONObject();
                            mparam.put("name",param.name());
                            mparam.put("level",param.level());
                            mparam.put("required",param.required());
                            mparam.put("descr",param.descr());
                            mparam.put("dataType",param.dataType());
                            String[] methods = param.method();
                            if(methods.length == 0) {//没有方法名(表示共用参数)
                                common.add(mparam);
                                continue;
                            }
                            //有对应的方法
                            for (String method : methods) {
                                JSONArray mparams = cache.get(method);
                                if(mparams == null){
                                    mparams = new JSONArray();
                                }
                                mparams.add(mparam);
                                cache.put(method,mparams);
                            }
                        }
                        if(!CollectionUtils.isEmpty(common)){
                            methodJson.put(commonKey,common);//默认方法，这个必须有
                        }
                        methodJson.putAll(cache);
                    }
                    //没有私有参数的方法
                    ApiEmptyMethod emptyMethod = getAnnotation(clazz,codeMethod,ApiEmptyMethod.class,annotationType);
                    if(emptyMethod != null){
                        String[] vals = emptyMethod.value();
                        for (String val : vals) {
                            methodJson.put(val,new JSONArray());
                        }
                    }
                    api.put("methods",methodJson);
                    //响应
                    ApiResps respsArray = getAnnotation(clazz,codeMethod,ApiResps.class,annotationType);
                    if(respsArray != null){
                        ApiResp[] resps = respsArray.value();
                        JSONArray cache = new JSONArray();
                        for (ApiResp resp : resps) {//组装method-param
                            JSONObject mresp = new JSONObject();
                            mresp.put("name",resp.name());
                            mresp.put("descr",resp.descr());
                            cache.add(mresp);
                        }
                        api.put("resp",cache);
                    }
                    apis.add(api);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
            doc.put("apis",apis);
        } catch (Exception e) {
            log.error("初始化文档异常",e);
        }
    }

    /**
     * 根据注解类型，获取annotation
     * @param clazz
     * @param method
     * @param anno
     * @param type
     * @param <T>
     * @return
     */
    public <T extends Annotation> T getAnnotation(Class<?> clazz, Method method, Class<T> anno, int type){
        if(type == 0){//类
            return clazz.getAnnotation(anno);
        }
        //方法
        return method.getAnnotation(anno);
    }

    @Override
    public JSONObject apiDoc() {
        return doc;
    }
}
