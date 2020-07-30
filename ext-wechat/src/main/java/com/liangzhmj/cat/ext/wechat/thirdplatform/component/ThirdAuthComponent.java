package com.liangzhmj.cat.ext.wechat.thirdplatform.component;

import com.liangzhmj.cat.dao.dbconfig.DBProperties;
import com.liangzhmj.cat.dao.mysql.APIDao;
import com.liangzhmj.cat.dao.mysql.utils.DBUtils;
import com.liangzhmj.cat.dao.redis.RedisDao;
import com.liangzhmj.cat.dao.utils.DaoBeanUtils;
import com.liangzhmj.cat.ext.wechat.config.ConfigContext;
import com.liangzhmj.cat.ext.wechat.config.DBConfigUtils;
import com.liangzhmj.cat.ext.wechat.config.vo.RedisKey;
import com.liangzhmj.cat.ext.wechat.exception.WechatException;
import com.liangzhmj.cat.ext.wechat.thirdplatform.ThirdAPIUtils;
import com.liangzhmj.cat.sync.lock.single.MySyncLockUtils;
import com.liangzhmj.cat.sync.lock.single.model.MyLock;
import com.liangzhmj.cat.tools.date.DateUtils;
import com.liangzhmj.cat.tools.http.HttpUtils;
import com.liangzhmj.cat.tools.image.ThumbnailsUtils;
import com.liangzhmj.cat.tools.security.MD5Util;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONObject;

import java.io.File;

/**
 * 第三方平台授权组件
 *
 * @author liangzhmj
 */
@Log4j2
public class ThirdAuthComponent {




    private static RedisKey keys = ConfigContext.getWechatConfig().getRedisKey();
    private static RedisDao redisDao = DaoBeanUtils.getRedisDao();
    private static APIDao serviceDao = DaoBeanUtils.getServiceDao();


    /**
     * 保存ticket
     *
     * @param appid
     * @param ticket
     * @return
     */
    public static String saveVerifyTicket(String appid, String ticket) {
        String key = keys.verifyTicket + appid;
        redisDao.add(key, ticket, 7200);//两个小时
        log.info("授权公众号["+key+"]缓存ticket信息:"+ticket);
        return key;
    }

    /**
     * 获取ticket
     *
     * @param appid
     * @return
     */
    public static String getVerifyTicket(String appid) {
        String key = keys.verifyTicket + appid;
        String ticket = redisDao.getT(key);//两个小时
        return ticket;
    }

    /**
     * 获取第三方组件token
     *
     * @param thirdAppid 第三方平台appid
     * @param srcToken   原token用于对比是否已经刷新过,在refresh=true是生效，主要用于防止多次刷新，第一次发现和原来的不一样就ok了
     * @param refresh    true:强刷
     * @return
     */
    public static String getComponentToken(String thirdAppid, String srcToken, boolean refresh) {
        String key = keys.componentToken + thirdAppid;
        String token;
        if (!refresh) {//不是刷新
            token = redisDao.getT(key);
            if (!StringUtils.isEmpty(token)) {//获取到，返回token
                return token;
            }
        }
        String lockKey = "/mylock/ctoken/" + thirdAppid;
        MyLock lock = MySyncLockUtils.getSimpleLock(lockKey);//一个平台才一个锁对象，不用回收，先不用分布式锁
        synchronized (lock) {
            //再次获取
            token = redisDao.getT(key);
            if(!StringUtils.isEmpty(token) && !token.equals(srcToken)){//之前已经有获取的,防止重复刷新
                log.info("第三方["+thirdAppid+"]token已经被刷新原srcCode["+srcToken+"],被刷新token["+token+"]");
                return token;
            }
            try {
                String ticket = getVerifyTicket(thirdAppid);
                if (StringUtils.isEmpty(ticket)) {
                    throw new WechatException("key[" + thirdAppid + "]获取不到verify_ticket");
                }
                //获取第三方配置信息,0:appid,1:secretKey,2:msgToken,3:msgAseKey
                String[] configs = DBConfigUtils.getThridPConfig(thirdAppid);
                JSONObject data = new JSONObject();
                data.put("component_appid", thirdAppid);
                data.put("component_appsecret", configs[1]);
                data.put("component_verify_ticket", ticket);
                String res = HttpUtils.post("https://api.weixin.qq.com/cgi-bin/component/api_component_token", null, data.toString().getBytes("UTF-8"), "UTF-8", 10000);
                JSONObject json = JSONObject.fromObject(res);
                token = json.optString("component_access_token");
                if (StringUtils.isEmpty(token)) {
                    throw new WechatException("获取不到component_access_token:" + res);
                }
                int expiresIn = json.optInt("expires_in");
                //提前一分钟失效
                int temp = expiresIn - 60;
                expiresIn = temp > 60 ? temp : 60;
                redisDao.add(key, token, expiresIn);
                log.info("执行ctoken刷新srcToken[" + srcToken + "],刷新token[" + token + "]");
                return token;
            } catch (Exception e) {
                log.error("获取组件token失败:" + e.getMessage());
            }
            return null;
        }
    }

    /**
     * 获取授权token
     * @param thirdAppid 第三方平台appId
     * @param srcToken
     * @param appid
     * @param refresh
     * @return
     */
    public static String getAuthToken(String thirdAppid,String appid,String srcToken,boolean refresh){
        String key = keys.authToken+appid;//不用加授权标识，无论在哪个平台只要token生效，同样可以调用公众号的api，同下
        String token = null;
        if(!refresh){//不是刷新
            token = redisDao.getT(key);
            if(!StringUtils.isEmpty(token)){
                return token;
            }
        }
        String lockKey = "/mylock/atoken/"+thirdAppid+"/"+appid;
        MyLock lock = MySyncLockUtils.getSimpleLock(lockKey);
        synchronized (lock) {
            //再次获取
            token = redisDao.getT(key);
            if(!StringUtils.isEmpty(token) && !token.equals(srcToken)){//之前已经有获取的,防止重复刷新
                log.info("atoken已经被刷新原srcCode["+srcToken+"],被刷新token["+token+"]");
                return token;
            }
            try {
                String refKey = keys.authRefreshToken+appid;
                String componentToken = getComponentToken(thirdAppid,null,false);//不强刷
                if(StringUtils.isEmpty(componentToken)){//获取不到
                    throw new WechatException("获取不到component_access_token");
                }
                String refreshToken = redisDao.getT(refKey);
                if(StringUtils.isEmpty(refreshToken)){//redis没有查询数据库
                    refreshToken = StringUtils.getCleanString(serviceDao.getObject("SELECT refreshToken FROM t_wechat_auth WHERE thirdAppid='"+ DBUtils.mysql_varchar_escape(thirdAppid)+"' AND appid='"+DBUtils.mysql_varchar_escape(appid)+"'"));
                    if(StringUtils.isEmpty(refreshToken)){
                        throw new WechatException("公众号["+appid+"]获取不到刷新令牌，请重新授权");
                    }
                }
                JSONObject data = new JSONObject();
                data.put("component_appid", thirdAppid);
                data.put("authorizer_appid", appid);
                data.put("authorizer_refresh_token", refreshToken);
                String res = HttpUtils.post("https://api.weixin.qq.com/cgi-bin/component/api_authorizer_token?component_access_token="+componentToken, null, data.toString().getBytes("UTF-8"), "UTF-8", 10000);
                JSONObject json = JSONObject.fromObject(res);
                token = json.optString("authorizer_access_token");
                if(StringUtils.isEmpty(token)){//获取不到token
                    if("40001".equals(StringUtils.getCleanString(json.optInt("errcode")))
                            ||"42001".equals(StringUtils.getCleanString(json.optInt("errcode")))){//component_token过期，强刷
                        componentToken = getComponentToken(thirdAppid,componentToken,true);//强刷
                        res = HttpUtils.post("https://api.weixin.qq.com/cgi-bin/component/api_authorizer_token?component_access_token="+componentToken, null, data.toString().getBytes("UTF-8"), "UTF-8", 10000);
                        json = JSONObject.fromObject(res);
                        token = json.optString("authorizer_access_token");
                        refreshToken = json.optString("authorizer_refresh_token");
                        if(StringUtils.isEmpty(token) || StringUtils.isEmpty(refreshToken)){
                            throw new WechatException("强刷组件token后刷新授权token失败:"+res);
                        }
                    }else{
                        throw new WechatException("获取不到授权token:"+res);
                    }
                }
                int expiresIn = json.optInt("expires_in");
                //提前一分钟失效
                int temp = expiresIn-60;
                expiresIn = temp>60?temp:60;
                //保存token和强刷token
                redisDao.add(key, token,expiresIn);
                redisDao.add(refKey, refreshToken);
                //保存数据库
                serviceDao.updateSQL("UPDATE t_wechat_auth SET refreshToken='"+ DBUtils.mysql_varchar_escape(refreshToken)+"' WHERE thirdAppid='"+DBUtils.mysql_varchar_escape(thirdAppid)+"' AND appid='"+DBUtils.mysql_varchar_escape(appid)+"'");
                log.info("执行刷新授权token成功:["+thirdAppid+"]-["+appid+"]-["+token+"]-["+refreshToken+"]");
                return token;
            }  catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }

    /**
     * 获取第三预授权码
     *
     * @param thirdAppid 第三方平台appid
     * @param srcCode    原来的code,主要用于防止多次刷新，第一次发现和原来的不一样就ok了
     * @param refresh    是否强刷
     * @return
     */
    public static String getPreAuthCode(String thirdAppid, String srcCode, boolean refresh) {
        String key = keys.thirdPreCode + thirdAppid;//虽然用得不多，但是有必要缓存，因为每次访问微信，后面的会把前面的覆盖，导致前面的用不了
        String code = null;
        if (!refresh) {//不是刷新
            code = redisDao.getT(key);
            if (!StringUtils.isEmpty(code)) {//获取到，返回token
                return code;
            }
        }
        String lockKey = "/mylock/pacode/" + thirdAppid;
        MyLock lock = MySyncLockUtils.getSimpleLock(lockKey);//一个平台才一个，不用回收,这里先不用分布式锁
        synchronized (lock) {
            try {
                //再次获取
                code = redisDao.getT(key);
                if(!StringUtils.isEmpty(code) && !code.equals(srcCode)){//之前已经有获取的,防止重复刷新
                    log.info("ctoken已经被刷新原token[" + srcCode + "],被刷新token[" + code + "]");
                    return code;
                }
                //正常:{"pre_auth_code":"preauthcode@@@U7EQPjD8dYiElwlPF6ksSZU_WL_09CNUDgbuWBlW9ItYegY9uAzl00mEradfvljd","expires_in":1800}
                //超时:{"errcode":40001,"errmsg":"invalid credential, access_token is invalid or not latest hint: [nXGA0391vr31]"}
                String componentToken = getComponentToken(thirdAppid, null, false);//不强刷
                if (StringUtils.isEmpty(componentToken)) {//获取不到
                    throw new WechatException("[" + thirdAppid + "]获取不到component_access_token");
                }
                JSONObject data = new JSONObject();
                data.put("component_appid", thirdAppid);
                String res = HttpUtils.post("https://api.weixin.qq.com/cgi-bin/component/api_create_preauthcode?component_access_token=" + componentToken, null, data.toString().getBytes("UTF-8"), "UTF-8", 10000);
                JSONObject json = JSONObject.fromObject(res);
                code = json.optString("pre_auth_code");
                if (StringUtils.isEmpty(code)) {//获取不到
                    if ("40001".equals(StringUtils.getCleanString(json.optInt("errcode")))
                            || "42001".equals(StringUtils.getCleanString(json.optInt("errcode")))) {//component_token过期，强刷
                        componentToken = getComponentToken(thirdAppid, componentToken, true);//强刷
                        res = HttpUtils.post("https://api.weixin.qq.com/cgi-bin/component/api_create_preauthcode?component_access_token=" + componentToken, null, data.toString().getBytes("UTF-8"), "UTF-8", 10000);
                        json = JSONObject.fromObject(res);
                        code = json.optString("pre_auth_code");
                        if (StringUtils.isEmpty(code)) {
                            throw new WechatException("强刷组件token后获取预授权码失败:" + res);
                        }
                    } else {
                        throw new WechatException("未知错误:" + res);
                    }
                }
                //添加缓存
                int expiresIn = json.optInt("expires_in");
                //提前一分钟失效
                int temp = expiresIn - 60;
                expiresIn = temp > 60 ? temp : 60;
                redisDao.add(key, code, expiresIn);
                log.info("执行pacode,刷新code[" + code + "]");
                return code;
            } catch (Exception e) {
                log.error("获取预授权码失败:" + e.getMessage());
            }
        }
        return null;
    }

    /**
     * 保存授权token
     *
     * @param thirdAppid 第三方平台appId
     * @param authCode   授权码
     * @return 0:appid,1:refreshToken
     * @throws Exception
     */
    public static String[] saveAuthToken(String thirdAppid, String authCode) throws Exception {
        String componentToken = getComponentToken(thirdAppid, null, false);//不强刷
        if (StringUtils.isEmpty(componentToken)) {//获取不到
            throw new WechatException("获取不到component_access_token");
        }
        JSONObject data = new JSONObject();
        data.put("component_appid", thirdAppid);
        data.put("authorization_code", authCode);
        String res = HttpUtils.post("https://api.weixin.qq.com/cgi-bin/component/api_query_auth?component_access_token=" + componentToken, null, data.toString().getBytes("UTF-8"), "UTF-8", 10000);
        JSONObject json = JSONObject.fromObject(res);
        JSONObject info = json.optJSONObject("authorization_info");
        if (info == null) {
            if ("40001".equals(StringUtils.getCleanString(json.optInt("errcode")))
                    || "42001".equals(StringUtils.getCleanString(json.optInt("errcode")))) {//component_token过期，强刷
                componentToken = getComponentToken(thirdAppid, componentToken, true);//强刷
                res = HttpUtils.post("https://api.weixin.qq.com/cgi-bin/component/api_query_auth?component_access_token=" + componentToken, null, data.toString().getBytes("UTF-8"), "UTF-8", 10000);
                json = JSONObject.fromObject(res);
                info = json.optJSONObject("authorization_info");
                if (info == null) {
                    throw new WechatException("强刷组件token后获取授权token失败:" + res);
                }
            } else {
                throw new WechatException("获取不到授权token:" + res);
            }
        }
        String appid = info.optString("authorizer_appid");
        String token = info.optString("authorizer_access_token");
        int expiresIn = info.optInt("expires_in");
        String refreshToken = info.optString("authorizer_refresh_token");
        if (StringUtils.isEmpty(appid) || StringUtils.isEmpty(token) || StringUtils.isEmpty(refreshToken)) {
            throw new WechatException("获取不到授权token:" + res);
        }
        //提前一分钟失效
        int temp = expiresIn - 60;
        expiresIn = temp > 60 ? temp : 60;
        //保存token和强刷token
        redisDao.add(keys.authToken + appid, token, expiresIn);
        redisDao.add(keys.authRefreshToken + appid, refreshToken);
        //保存数据库
        serviceDao.updateSQL("INSERT INTO t_wechat_auth(thirdAppid,appid,refreshToken,authStatus) VALUES('"
                + DBUtils.mysql_varchar_escape(thirdAppid)+"','"+DBUtils.mysql_varchar_escape(appid)+"','"
                +DBUtils.mysql_varchar_escape(refreshToken)+"',1) ON DUPLICATE KEY UPDATE refreshToken='"
                +DBUtils.mysql_varchar_escape(refreshToken)+"',authStatus=1");
        return new String[]{appid, refreshToken};
    }

    /**
     * 获取授权素材主体信息
     *
     * @param thirdAppid
     * @param appid
     * @return
     * @throws Exception
     */
    public static JSONObject authSubjectInfo(String thirdAppid, String appid) throws Exception {
        JSONObject data = new JSONObject();
        data.put("component_appid", thirdAppid);
        data.put("authorizer_appid", appid);
        String urlPrefix = "https://api.weixin.qq.com/cgi-bin/component/api_get_authorizer_info?component_access_token=";
        JSONObject json = ThirdAPIUtils.callComponentAPI(thirdAppid,urlPrefix,data);
        /*
        {"authorizer_info":{"nick_name":"范丝圈","head_img":"http:\/\/wx.qlogo.cn\/mmopen\/zMYW11icgoEy5WicL4Tw3g7sIJ6xGRXSChcx1Rtbg4osmRRO25TSvtWM8lA5uZSmUwYKmflUkZKFe3z1saPwklBR9Xpeib7Y9Ic\/0","service_type_info":{"id":0},"verify_type_info":{"id":0},"user_name":"gh_fc3dd41180cf","alias":"","qrcode_url":"http:\/\/mmbiz.qpic.cn\/mmbiz_jpg\/kSDYYbaBTDQacpMkjsDnjlTtqjG0ulyCvCw0qssAtLKpZaICkZEYmW7jicCZ55gGP6UY1Pib2hqz49SEBrMmdGng\/0","business_info":{"open_pay":1,"open_shake":0,"open_scan":0,"open_card":0,"open_store":0},"idc":1,"principal_name":"易简广告传媒集团股份有限公司","signature":"一款简单的交流工具","MiniProgramInfo":{"network":{"RequestDomain":["https:\/\/mallapi.ejamad.com","https:\/\/social.g5378.com"],"WsRequestDomain":["wss:\/\/mallapi.ejamad.com","wss:\/\/social.g5378.com","wss:\/\/ws.im.jiguang.cn"],"UploadDomain":["https:\/\/mallapi.ejamad.com","https:\/\/sdk.im.jiguang.cn","https:\/\/social.g5378.com"],"DownloadDomain":["https:\/\/dl.im.jiguang.cn","https:\/\/dlop.im.jiguang.cn","https:\/\/mallapi.ejamad.com","https:\/\/social.g5378.com"],"BizDomain":[],"UDPDomain":[]},"categories":[{"first":"文娱","second":"资讯"},{"first":"文娱","second":"语音"},{"first":"商家自营","second":"服装\/鞋\/箱包"}],"visit_status":0}},"authorization_info":{"authorizer_appid":"wx02697196a28855ee","authorizer_refresh_token":"refreshtoken@@@nukl-v9HHIwRmyjNDT4Hm3FzbWpaHQmpgcK2AWlMrdg","func_info":[{"funcscope_category":{"id":17}},{"funcscope_category":{"id":18},"confirm_info":{"need_confirm":0,"already_confirm":0,"can_confirm":0}},{"funcscope_category":{"id":19}},{"funcscope_category":{"id":25},"confirm_info":{"need_confirm":0,"already_confirm":0,"can_confirm":0}},{"funcscope_category":{"id":30},"confirm_info":{"need_confirm":0,"already_confirm":0,"can_confirm":0}},{"funcscope_category":{"id":31},"confirm_info":{"need_confirm":0,"already_confirm":0,"can_confirm":0}},{"funcscope_category":{"id":36}},{"funcscope_category":{"id":37}},{"funcscope_category":{"id":40}},{"funcscope_category":{"id":41},"confirm_info":{"need_confirm":0,"already_confirm":0,"can_confirm":0}},{"funcscope_category":{"id":45}},{"funcscope_category":{"id":48}},{"funcscope_category":{"id":49},"confirm_info":{"need_confirm":0,"already_confirm":0,"can_confirm":0}}]}}
         */
        return json;
    }
    /**
     * 获取授权素材主体信息
     *
     * @param thirdAppid
     * @param appid
     * @param rid
     * @return
     * @throws Exception
     */
    public static JSONObject saveSubjectInfo(String thirdAppid, String appid,int rid) throws Exception {
        JSONObject res = authSubjectInfo(thirdAppid,appid);
        if(res != null && res.containsKey("authorizer_info")){
            JSONObject info = res.getJSONObject("authorizer_info");
            String nickname = DBUtils.mysql_varchar_escape(info.optString("nick_name"));
            String headImg = DBUtils.mysql_varchar_escape(info.optString("head_img"));
            String username = DBUtils.mysql_varchar_escape(info.optString("user_name"));
            String principalName = DBUtils.mysql_varchar_escape(info.optString("principal_name"));
            String alias = DBUtils.mysql_varchar_escape(info.optString("alias"));
            String qrcode = DBUtils.mysql_varchar_escape(info.optString("qrcode_url"));
            String name = null;
            //微信二维码有防盗链，下载二维码
            if(!StringUtils.isEmpty(qrcode)){
                try {
                    log.info("qrcode："+qrcode);
                    byte[] qrdata = HttpUtils.getByteArray(qrcode, "image", 10000);
                    name = "wechats/"+ MD5Util.calc(appid);
                    String path = DBProperties.getProperty("api.resources.upload")+name;
                    File dir = new File(path);
                    if(!dir.exists()){
                        dir.mkdirs();
                    }
                    name += "/qrcode.png";
                    String savePath = path + "/qrcode.png";
                    //转换大小并保存
                    ThumbnailsUtils.channgeSize(qrdata, savePath, 256, 256, true, 1);
                    log.info("保存的路径："+savePath);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    name = qrcode;
                }
            }
            int serviceType = 0;
            try {
                serviceType = info.getJSONObject("service_type_info").getInt("id");
            } catch (Exception e) {
            }
            int verifyType = 0;
            try {
                verifyType = info.getJSONObject("verify_type_info").getInt("id");
            } catch (Exception e) {
            }
            int type = 0;
            if(info.has("MiniProgramInfo") || info.has("miniProgramInfo")){//小程序
                type = 2;
            }else{//公众号
                type = 1;
            }
            serviceDao.updateSQL("UPDATE t_wechat_auth SET roleId="+rid+",nickname='"+nickname+"',headImg='"
                    +headImg+"',qrcode='"+name+"',serviceType="+serviceType+",verifyType="
                    +verifyType+",username='"+username+"',principalName='"+principalName+"',alias='"+alias+"',authTime='"+ DateUtils.getCurrentStr("yyyy-MM-dd HH:mm:ss")+"',atype="+type+" "
                    + "WHERE appId='"+DBUtils.mysql_varchar_escape(appid)+"'");
        }
        return res;
    }


//    /**
//     * 从后台接口获取token
//     * @param thirdAppid
//     * @param appid
//     * @param srcToken
//     * @param refresh
//     * @return
//     */
//    public static String getAuthToken(String thirdAppid,String appid,String srcToken,boolean refresh){
//        String url = "http://api.ejamccmp.com/realSync151?method=m-token";
//        String token = HttpUtils.get(url+"&thirdAppid="+thirdAppid+"&appid="+appid+"&srcToken="+ StringUtils.getCleanString(srcToken)+"&refresh="+refresh, 5000);
//        if(!StringUtils.isEmpty(token) && !"error".equals(token)){
//            return token;
//        }
//        token = HttpUtils.get(url+"&thirdAppId="+thirdAppid+"&appId="+appid+"&srcToken="+srcToken+"&refresh=true", 5000);
//        if(!StringUtils.isEmpty(token) && !"error".equals(token)){
//            return token;
//        }
//        return null;
//    }
//
//    /**
//     * 从后台接口获取第三方平台token
//     * @param thirdAppid
//     * @param srcToken
//     * @param refresh
//     * @return
//     */
//    public static String getComponentToken(String thirdAppid,String srcToken,boolean refresh){
//        String url = "http://api.ejamccmp.com/realSync151?method=c-token";
//        String token = HttpUtils.get(url+"&thirdAppid="+thirdAppid+"&srcToken="+StringUtils.getCleanString(srcToken)+"&refresh="+refresh, 5000);
//        if(!StringUtils.isEmpty(token) && !"error".equals(token)){
//            return token;
//        }
//        token = HttpUtils.get(url+"&thirdAppId="+thirdAppid+"&srcToken="+srcToken+"&refresh=true", 5000);
//        if(!StringUtils.isEmpty(token) && !"error".equals(token)){
//            return token;
//        }
//        return null;
//    }


}
