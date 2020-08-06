package com.liangzhmj.cat.api.controller;

import com.liangzhmj.cat.api.enums.APIExceptionEnum;
import com.liangzhmj.cat.api.exception.APIException;
import com.liangzhmj.cat.api.protocol.ServiceAgency;
import com.liangzhmj.cat.api.protocol.req.APIReq;
import com.liangzhmj.cat.api.protocol.resp.Result;
import com.liangzhmj.cat.api.utils.ServletUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller//不设全局变量，单例就行不用@Scope("prototype")
@Log4j2
public class InterfaceController {

    @Autowired
    private ServiceAgency serviceAgency;

    @RequestMapping("/interfaceAction")
    public void inter(HttpServletRequest request, HttpServletResponse response){
        doService(request,response);
    }

    @RequestMapping("/interSyncAction")//服务入口类(同步,逻辑完全和InterfaceAction一样，考虑项目规模，一些锁操作主要是基于java内部的实现，
                            // 方便nginx路径区分，做单点同步逻辑（不支持分布式）)
    public void interSync(HttpServletRequest request, HttpServletResponse response){
        doService(request,response);
    }

    public void doService(HttpServletRequest request, HttpServletResponse response){
        long start = System.currentTimeMillis();
        APIReq rqs = null;
        try {
            rqs = new APIReq(request,response);  //实例化协议
            rqs.reciveRequest();  //接收协议内容
            serviceAgency.start(rqs);
        } catch(APIException e){
            long end = System.currentTimeMillis();
            Result apie = Result.fail(e);
            ServletUtils.returnRes(apie,rqs,(end-start));
        } catch (Exception e) {
            long end = System.currentTimeMillis();
            Result unke = Result.fail(new APIException(APIExceptionEnum.FAIL_UNKNOWN,e.getMessage()));
            ServletUtils.returnRes(unke,rqs,(end-start));
            log.error(e);
        }
    }
}
