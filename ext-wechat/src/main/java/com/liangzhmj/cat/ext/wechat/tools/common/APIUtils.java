package com.liangzhmj.cat.ext.wechat.tools.common;

import com.liangzhmj.cat.tools.http.HttpUtils;
import com.liangzhmj.cat.tools.random.RandomUtils;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONObject;
import org.apache.http.entity.ContentType;

import java.io.File;
import java.util.Map;

/**
 * API工具类
 * @author liangzhmj
 *
 */
@Log4j2
public class APIUtils {


    /**
     * 单个素材访问微信api
     * @param url
     * @param appid
     * @param params
     * @return
     */
    public static JSONObject callAPI(String url,String appid, JSONObject params){
        try {
            String token = TokenAPI.getToken(appid,null,false);
            String resp = HttpUtils.post(url+token,null, params.toString().getBytes("UTF-8"), null, 10000);
            JSONObject rjson = JSONObject.fromObject(resp);
            if(rjson != null && ("40001".equals(String.valueOf(rjson.optInt("errcode")))
                    || "42001".equals(StringUtils.getCleanString(rjson.optInt("errcode"))))){//token过期
                token = TokenAPI.getToken(appid,token,true);;//强刷
                resp = HttpUtils.post(url+token,null, params.toString().getBytes("UTF-8"), null, 10000);
                rjson = JSONObject.fromObject(resp);
            }
            checkError(appid,url,rjson);
            return rjson;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private static void checkError(String appid,String url,JSONObject rjson){
        if(rjson == null || !rjson.containsKey("errcode") || rjson.getInt("errcode") != 0){
            if(rjson != null && rjson.has("openid")){//有些没有errcode的例如用户信息接口
                return;
            }
            log.info("素材主体["+appid+"]访问微信api["+url+"]处理失败:"+StringUtils.substring(1000,rjson.toString()));
        }
    }
    /**
     * 单个素材访问微信api
     * @param url
     * @param appid
     * @param headers
     * @param params
     * @return
     */
    public static JSONObject callAPI(String url, String appid, Map<String,String> headers, JSONObject params){
        try {
            String token = TokenAPI.getToken(appid,null,false);
            String resp = HttpUtils.post(url+token,headers, params.toString().getBytes("UTF-8"), null, 10000);
            JSONObject rjson = JSONObject.fromObject(resp);
            if(rjson != null && ("40001".equals(String.valueOf(rjson.optInt("errcode")))
                    || "42001".equals(StringUtils.getCleanString(rjson.optInt("errcode"))))){//token过期
                token = TokenAPI.getToken(appid,token,true);;//强刷
                resp = HttpUtils.post(url+token,headers, params.toString().getBytes("UTF-8"), null, 10000);
                rjson = JSONObject.fromObject(resp);
            }
            checkError(appid,url,rjson);
            return rjson;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
    /**
     * 单个素材访问微信api
     * @param url
     * @param appid
     * @param headers
     * @param data
     * @return
     */
    public static JSONObject callAPI(String url, String appid, Map<String,String> headers, File data){
        try {
            String token = TokenAPI.getToken(appid,null,false);
            String resp = HttpUtils.post(url+token, headers, data, 15000);
            JSONObject rjson = JSONObject.fromObject(resp);
            if(rjson != null && ("40001".equals(String.valueOf(rjson.optInt("errcode")))
                    || "42001".equals(StringUtils.getCleanString(rjson.optInt("errcode"))))){//token过期
                token = TokenAPI.getToken(appid,token,true);;//强刷
                resp = HttpUtils.post(url+token, headers, data, 15000);
                rjson = JSONObject.fromObject(resp);
            }
            checkError(appid,url,rjson);
            return rjson;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 上传素材
     * @param url
     * @param appid
     * @param headers
     * @param contentType
     * @param data
     * @param ext
     * @return
     */
    public static JSONObject uploadMedia(String url, String appid, Map<String,String> headers, ContentType contentType, byte[] data, String ext){
        try {
            String token = TokenAPI.getToken(appid,null,false);
            String filename = RandomUtils.getUUID().replaceAll("-","")+"."+ext;
            String resp = HttpUtils.postMedia(url+token,headers, data,contentType, filename, 15000);
            JSONObject rjson = JSONObject.fromObject(resp);
            if(rjson != null && ("40001".equals(String.valueOf(rjson.optInt("errcode")))
                    || "42001".equals(StringUtils.getCleanString(rjson.optInt("errcode"))))){//token过期
                token = TokenAPI.getToken(appid, token, true);//强刷
                resp = HttpUtils.postMedia(url+token,headers, data,contentType, filename, 15000);
                rjson = JSONObject.fromObject(resp);
            }
            checkError(appid,url,rjson);
            return rjson;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 单个素材访问微信api
     * @param url
     * @param appid
     * @return
     */
    public static JSONObject callAPIGet(String url,String appid){
        try {
            String token = TokenAPI.getToken(appid,null,false);
            String resp = HttpUtils.get(url+token,10000);
            JSONObject rjson = JSONObject.fromObject(resp);
            if(rjson != null && ("40001".equals(String.valueOf(rjson.optInt("errcode")))
                    || "42001".equals(StringUtils.getCleanString(rjson.optInt("errcode"))))){//token过期
                token = TokenAPI.getToken(appid,token,true);;//强刷
                resp = HttpUtils.get(url+token,10000);
                rjson = JSONObject.fromObject(resp);
            }
            checkError(appid,url,rjson);
            return rjson;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
    /**
     * 单个素材访问微信api
     * @param url
     * @param appid
     * @param headers
     * @return
     */
    public static JSONObject callAPIGet(String url,String appid,Map<String,String> headers){
        try {
            String token = TokenAPI.getToken(appid,null,false);
            String resp = HttpUtils.get(url+token,headers,null,10000);
            JSONObject rjson = JSONObject.fromObject(resp);
            if(rjson != null && ("40001".equals(String.valueOf(rjson.optInt("errcode")))
                    || "42001".equals(StringUtils.getCleanString(rjson.optInt("errcode"))))){//token过期
                token = TokenAPI.getToken(appid,token,true);;//强刷
                resp = HttpUtils.get(url+token,headers,null,10000);
                rjson = JSONObject.fromObject(resp);
            }
            checkError(appid,url,rjson);
            return rjson;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

}
