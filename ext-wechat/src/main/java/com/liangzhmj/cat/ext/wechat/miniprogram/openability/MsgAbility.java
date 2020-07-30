package com.liangzhmj.cat.ext.wechat.miniprogram.openability;

import com.liangzhmj.cat.ext.wechat.exception.WechatException;
import com.liangzhmj.cat.ext.wechat.tools.common.APIUtils;
import com.liangzhmj.cat.ext.wechat.tools.common.protocol.MessageProtocol;
import com.liangzhmj.cat.ext.wechat.tools.common.vo.MsgTemplate;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * 小程序开放能力 - 微信文档[小程序->开放能力->客服&模板消息|小程序->服务端->客服&模板消息]
 *  -用户消息能力
 * @author liangzhmj
 * @Date 2019-09-26 16:45:12
 */
@Log4j2
public class MsgAbility {

    private static String MSG_URL = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=";
    private static String TEMPLATE_MSG_URL = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=";

    /**
     * 发送文本客服消息
     * @param appid
     * @param openid
     * @param content
     * @return
     */
    public static JSONObject sendTextMsg(String appid,String openid,String content){
        JSONObject resp = null;
        try {
            JSONObject params = MessageProtocol.textMsg(openid,content);
            resp = APIUtils.callAPI(MSG_URL,appid,params);
        } catch (Exception e) {
            log.error(e);
        }
        return resp;
    }

    /**
     * 发送图片信息
     * @param appid
     * @param openid
     * @param mediaId
     * @return
     */
    public static JSONObject sendPicMsg(String appid, String openid, String mediaId){
        JSONObject resp = null;
        try {
            JSONObject params = MessageProtocol.picMsg(openid,mediaId);
            resp = APIUtils.callAPI(MSG_URL,appid,params);
        } catch (Exception e) {
            log.error(e);
        }
        return resp;
    }

    /**
     * 发送小程序卡片信息
     * @param appid
     * @param openid
     * @param title
     * @param mediaId
     * @param url
     * @return
     */
    public static JSONObject sendAppCardMsg(String appid,String openid,String title,String mediaId,String url){
        JSONObject resp = null;
        try {
            JSONObject params = MessageProtocol.appCardMsg(openid,title,null,mediaId,url);
            resp = APIUtils.callAPI(MSG_URL,appid,params);
        } catch (Exception e) {
            log.error(e);
        }
        return resp;
    }

    /**
     * 发送图文信息
     * @param appid
     * @param openid
     * @param title
     * @param descr
     * @param thumbUrl
     * @param url
     * @return
     */
    public static JSONObject sendPicLinkMsg(String appid,String openid,String title,String descr,String thumbUrl,String url){
        JSONObject resp = null;
        try {
            JSONObject params = MessageProtocol.picLinkMsgMini(openid,title,descr,thumbUrl,url);
            resp = APIUtils.callAPI(MSG_URL,appid,params);
        } catch (Exception e) {
            log.error(e);
        }
        return resp;
    }


    /**
     * 发送消息模板
     * @param appid
     * @param template
     */
    public static JSONObject sendMsgTemplate(String appid, MsgTemplate template){
        JSONObject resp = null;
        try {
            if(template == null){
                throw new WechatException("template为空");
            }
            resp = APIUtils.callAPI(TEMPLATE_MSG_URL,appid,template.toJSON());
        } catch (Exception e) {
            log.error(e);
        }
        return resp;
    }

    /**
     * 发送消息模板
     * @param appid
     * @param params
     */
    public static JSONObject sendMsgTemplate(String appid,JSONObject params){
        JSONObject resp = null;
        try {
            if(params == null){
                throw new WechatException("params为空");
            }
            resp = APIUtils.callAPI(TEMPLATE_MSG_URL,appid,params);
        } catch (Exception e) {
            log.error(e);
        }
        return resp;
    }

    /**
     * 上传临时素材
     * @param appid
     * @param data
     * @return
     */
    public static String uploadTempMaterial(String appid, File data){
        try {
            Map<String,String> header = new HashMap<String,String>();
            header.put("Connection", "keep-alive");
            header.put("Accept", "*/*");
            header.put("Content-Type", "multipart/form-data");
            header.put("User-Agent",
                    "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
            JSONObject res = APIUtils.callAPI("https://api.weixin.qq.com/cgi-bin/media/upload?type=image&access_token=",appid,header,data);
            log.info("上传临时素材结果:"+res);
            return res.getString("media_id");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }


}
