package com.liangzhmj.cat.ext.wechat.thirdplatform.offia;

import com.liangzhmj.cat.ext.wechat.thirdplatform.ThirdAPIUtils;
import com.liangzhmj.cat.ext.wechat.thirdplatform.vo.ReleWapp;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 公众号关联小程序 - 微信文档[第三方平台->代公众号实现业务->小程序管理权限集]
 * @author liangzhmj
 */
@Log4j2
public class MiniAppAuthAPI {

    /**
     * 获取公众号关联的小程序
     * @param thirdAppid
     * @param appid
     * @return
     */
    public static List<ReleWapp> getWechatWapps(String thirdAppid, String appid){
        try {
            JSONObject params = new JSONObject();
            JSONObject rjson = ThirdAPIUtils.callAPI(thirdAppid, appid, "https://api.weixin.qq.com/cgi-bin/wxopen/wxamplinkget?access_token=", params);
            JSONArray ja = rjson.optJSONObject("wxopens").getJSONArray("items");
            if(ja != null && !ja.isEmpty()){
                List<ReleWapp> rws = new ArrayList<ReleWapp>();
                for (int i = 0; i < ja.size(); i++) {
                    JSONObject json = ja.getJSONObject(i);
                    rws.add(new ReleWapp(json));
                }
                return rws;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 关联绑定公众号-小程序
     * @param thirdAppid
     * @param appid 公众号的appid
     * @param wappid 小程序的appid
     * @return
     */
    public static JSONObject bindingWechat(String thirdAppid,String appid,String wappid){
        try {
            JSONObject params = new JSONObject();
            params.put("appid", wappid);
            params.put("notify_users", "0");
            params.put("show_profile", "0");
            JSONObject rjson = ThirdAPIUtils.callAPI(thirdAppid, appid, "https://api.weixin.qq.com/cgi-bin/wxopen/wxamplink?access_token=", params);
            return rjson;
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    /**
     * 解绑已绑定的小程序
     * @param thirdAppid
     * @param appid
     * @param wappid
     * @return
     */
    public static JSONObject unbindingWechat(String thirdAppid,String appid,String wappid){
        try {
            JSONObject params = new JSONObject();
            params.put("appid", wappid);
            JSONObject rjson = ThirdAPIUtils.callAPI(thirdAppid, appid, "https://api.weixin.qq.com/cgi-bin/wxopen/wxampunlink?access_token=", params);
            return rjson;
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }
}
