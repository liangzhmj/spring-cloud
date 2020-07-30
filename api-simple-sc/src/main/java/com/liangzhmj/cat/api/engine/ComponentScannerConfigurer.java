package com.liangzhmj.cat.api.engine;

import com.liangzhmj.cat.api.aop.aspect.APIAspect;
import com.liangzhmj.cat.api.engine.annotation.InterAop;
import com.liangzhmj.cat.api.engine.annotation.InterService;
import com.liangzhmj.cat.api.engine.model.EngineContext;
import com.liangzhmj.cat.api.service.ServiceModule;
import com.liangzhmj.cat.tools.collection.CollectionUtils;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 扫描指定包下面的类
 * 并对@InterAop，和@InterService进行加载处理
 */
@Log4j2
@Component
@DependsOn("daoBeanUtils")//该模块本来就依赖cat-dao模块
public class ComponentScannerConfigurer {

    @Value("${api.scanprefix:#{null}}")
    private String scanPrefix;

    @PostConstruct
    public void initConfig() throws Exception{
        if(StringUtils.isEmpty(scanPrefix)){
            log.info("项目中没有自定义注解配置");
            return;
        }
        //入参 要扫描的包名(这家伙会扫描指定类下的所有class，包括依赖，因此很多jar依赖可能存在ClassNotFound(不影响程序，只是启动的时候不好看)，因此建议精准扫描)
        Reflections f = new Reflections(scanPrefix);
        //入参 目标注解类
        Set<Class<?>> aops = f.getTypesAnnotatedWith(InterAop.class);
        if(!CollectionUtils.isEmpty(aops)){
            TreeMap<Integer, APIAspect> temp = new TreeMap<>();
            for (Class<?> aop : aops) {
                APIAspect obj = (APIAspect) aop.newInstance();
                //获取当前类的注解
                InterAop cur = aop.getAnnotation(InterAop.class);
                log.info("实例化aop对象:"+aop);
                temp.put(cur.value(),obj);
            }
            for (Map.Entry<Integer, APIAspect> en : temp.entrySet()) {
                EngineContext.aops.add(en.getValue());
            }
        }
        Set<Class<?>> services = f.getTypesAnnotatedWith(InterService.class);
        if(!CollectionUtils.isEmpty(services)){
            for (Class<?> service : services) {
                ServiceModule obj = (ServiceModule) service.newInstance();
                log.info("实例化service对象:"+service);
                //获取当前类的注解
                InterService cur = service.getAnnotation(InterService.class);
                EngineContext.services.put(cur.value(),obj);
            }
        }
    }
}
