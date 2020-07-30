package com.liangzhmj.cat.ext.wechat.offiaccount.protocol;

import net.sf.json.JSONObject;

/**
 * 账号管理协议
 * @author liangzhmj
 */
public class AccountProtocol {

    /**
     * 永久二维码
     * @param param 参数
     * @return
     */
    public static JSONObject perpetualQrcode(String param){
        /*
        {"action_name": "QR_LIMIT_STR_SCENE", "action_info": {"scene": {"scene_str": "test"}}}
         */
        JSONObject params = new JSONObject();
        JSONObject scene = new JSONObject();
        JSONObject actionInfo = new JSONObject();
        scene.put("scene_str", param);
        actionInfo.put("scene", scene);
        params.put("action_info", actionInfo);
        params.put("action_name", "QR_LIMIT_STR_SCENE");
        return params;
    }
    /**
     * 临时二维码
     * @param param 参数
     * @param expireTime 有效时间(s)
     * @return
     */
    public static JSONObject tempQrcode(String param,int expireTime){
        /*
        {"expire_seconds": 604800, "action_name": "QR_STR_SCENE", "action_info": {"scene": {"scene_str": "test"}}}
         */
        JSONObject params = new JSONObject();
        JSONObject scene = new JSONObject();
        JSONObject actionInfo = new JSONObject();
        scene.put("scene_str", param);
        actionInfo.put("scene", scene);
        params.put("action_info", actionInfo);
        params.put("action_name", "QR_STR_SCENE");
        params.put("expire_seconds", expireTime);
        return params;
    }

    /**
     * 长连接转短链接
     * @param longUrl
     * @return
     */
    public static JSONObject long2short(String longUrl){
        JSONObject params = new JSONObject();
        params.put("action","long2short");
        params.put("long_url",longUrl);
        return params;
    }
}
