package com.liangzhmj.cat.api.utils;

import com.liangzhmj.cat.api.protocol.req.APIReq;
import com.liangzhmj.cat.api.protocol.resp.Result;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.Cleanup;
import lombok.extern.log4j.Log4j2;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * servlet的工具类
 * @author liangzhmj
 */
@Log4j2
public class ServletUtils {

    /**
     * 返回api结果
     * @param result
     * @param req
     * @param spendtime
     * @throws IOException
     */
    public static void returnRes(Result result, APIReq req,long spendtime) throws IOException {
        try {
            HttpServletResponse response = req.getResponse();
            String resultStr = result.toString();
            log.info("返回码【"+req.getInterId()+"】->耗时:"+spendtime+"ms-->返回数据:"+ StringUtils.substring(1000,resultStr));
            //普通的协议
            if(req.getCrossDomain() == 1){//跨域访问
                String jsonpCallback = req.getRequest().getParameter("callback");//客户端请求参数
                write(response,jsonpCallback+"("+resultStr+")");//返回jsonp格式数据
                return;
            }
            write(response,resultStr.getBytes("UTF-8"));
        } catch (Exception e) {
            log.error(e);
        }
    }

    /**
     * 返回字节数组数据
     * @param response
     * @param data
     * @throws IOException
     */
    public static void write(HttpServletResponse response,byte[] data)  throws IOException {
        //其实response的相关流，在请求结束的时候它会自动关闭的，但自己打开的必须要自己手动关闭，例如这里的bout
        //因为是api接口(返回数据就完了)，因此不会在前端，或者filter中调用response的相关接口，这里手动关闭
        //如果有前端页面或者aop，filter之类的，这里手动关闭了，在前端或者aop的after将获取不到response流的相关信息
        @Cleanup OutputStream out = response.getOutputStream();
        @Cleanup BufferedOutputStream bout = new BufferedOutputStream(out);
        bout.write(data);
        bout.flush();
    }

    /**
     * 返回字符串数据
     * @param response
     * @param data
     * @throws IOException
     */
    public static void write(HttpServletResponse response,String data)  throws IOException {
        //其实response的相关流，在请求结束的时候它会自动关闭的，但自己打开的必须要自己手动关闭
        //因为是api接口(返回数据就完了)，因此不会在前端，或者filter中调用response的相关接口，这里手动关闭
        //如果有前端页面或者aop，filter之类的，这里手动关闭了，在前端或者aop的after将获取不到response流的相关信息
        @Cleanup PrintWriter writer = response.getWriter();
        writer.write(data);//返回jsonp格式数据
        writer.flush();
    }
}
