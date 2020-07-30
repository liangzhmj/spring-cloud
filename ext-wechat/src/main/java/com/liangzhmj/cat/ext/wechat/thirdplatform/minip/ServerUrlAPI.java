package com.liangzhmj.cat.ext.wechat.thirdplatform.minip;

import com.liangzhmj.cat.ext.wechat.thirdplatform.ThirdAPIUtils;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * 小程序域名设置 - 微信文档[第三方平台->代小程序实现业务->基础信息设置->服务器域名|业务域名]
 * @author liangzhmj
 */
@Log4j2
public class ServerUrlAPI {

    /**
     * 设置小程序服务器域名
     * @param thirdAppid
     * @param appid
     * @param rqUrls request 合法域名
     * @param wsUrls socket 合法域名
     * @param upUrls uploadFile 合法域名
     * @param dwUrls downloadFile 合法域名
     * @return
     */
    public static JSONObject setServerUrl(String thirdAppid, String appid, List<String> rqUrls, List<String> wsUrls, List<String> upUrls, List<String> dwUrls){
        try {
            JSONObject params = new JSONObject();
            params.put("action", "set");
            params.put("requestdomain", rqUrls);
            params.put("wsrequestdomain", wsUrls);
            params.put("uploaddomain", upUrls);
            params.put("downloaddomain", dwUrls);
            JSONObject rjson = ThirdAPIUtils.callAPI(thirdAppid, appid, "https://api.weixin.qq.com/wxa/modify_domain?access_token=", params);
            return rjson;
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    /**
     * 获取小程序配置域名
     * @param thirdAppid
     * @param appid
     * @return
     */
    public static JSONObject getServerUrl(String thirdAppid,String appid){
        try {
            JSONObject params = new JSONObject();
            params.put("action", "get");
            JSONObject rjson = ThirdAPIUtils.callAPI(thirdAppid, appid, "https://api.weixin.qq.com/wxa/modify_domain?access_token=", params);
            return rjson;
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    /**
     * 设置小程序业务域名
     * @param thirdAppid
     * @param appid
     * @param wvUrls
     * @return
     */
    public static JSONObject setWebviewDomain(String thirdAppid,String appid,List<String> wvUrls){
        try {
            JSONObject params = new JSONObject();
            params.put("action", "set");
            params.put("webviewdomain", wvUrls);
            JSONObject rjson = ThirdAPIUtils.callAPI(thirdAppid, appid, "https://api.weixin.qq.com/wxa/setwebviewdomain?access_token=", params);
            return rjson;
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    /**
     * 获取小程序业务域名
     * @param thirdAppid
     * @param appid
     * @return
     */
    public static JSONObject getWebViewDomain(String thirdAppid,String appid){
        try {
            JSONObject params = new JSONObject();
            params.put("action", "get");
            JSONObject rjson = ThirdAPIUtils.callAPI(thirdAppid, appid, "https://api.weixin.qq.com/wxa/setwebviewdomain?access_token=", params);
            return rjson;
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }
}
