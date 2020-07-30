package com.liangzhmj.cat.sync.lock.distributed.zookeeper.aop;

import com.liangzhmj.cat.sync.lock.distributed.zookeeper.ZKLock;
import com.liangzhmj.cat.sync.lock.distributed.zookeeper.annotation.DLock;
import com.liangzhmj.cat.tools.spel.SpelUtils;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.Cleanup;
import lombok.extern.log4j.Log4j2;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;


@Aspect
@Log4j2
public class DLockAop {

    @Around(value = "@annotation(com.liangzhmj.cat.sync.lock.distributed.zookeeper.annotation.DLock)")
    public Object lock(ProceedingJoinPoint point) throws Throwable {
        //获取拦截的方法名
        Signature sig = point.getSignature();
        MethodSignature msig = (MethodSignature) sig;
        Method currentMethod = msig.getMethod();
        //获取拦截方法的参数
        DLock dlock = currentMethod.getAnnotation(DLock.class);
        long ms = dlock.ms();
        String path = StringUtils.isEmpty(dlock.value())?"/commondlock":dlock.value();
        log.debug("分布式锁对应zookeeper-src路径为:"+path);
        if(path.indexOf("#{") != -1){//有动态值，要获取参数
            String[] names = msig.getParameterNames();
            Object[] args = point.getArgs();
            EvaluationContext context = new StandardEvaluationContext();
            for (int i = 0; i < names.length; i++) {//封装SpEL表达式上下文
                context.setVariable(names[i],args[i]);
            }
            path = SpelUtils.parseTemplateExpression(context,path,String.class);
        }
        log.debug("分布式锁对应zookeeper-final路径为:"+path);
        //获取分布式锁,该注解是用来保证分配的资源被释放。在本地变量上使用该注解，任何后续代码（同级别，例如这里的log.error(e)就不会被包括）都将封装在try/finally中
        @Cleanup("release") InterProcessMutex lock = ZKLock.getDLock(path);
        if(ms > 0){
            lock.acquire(ms, TimeUnit.MILLISECONDS);
        }else{
            lock.acquire();
        }
        //执行方法体
        Object result = point.proceed();
        return result;
    }
}
