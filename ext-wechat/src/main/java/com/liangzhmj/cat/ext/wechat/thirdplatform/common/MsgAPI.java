package com.liangzhmj.cat.ext.wechat.thirdplatform.common;


import com.liangzhmj.cat.ext.wechat.exception.WechatException;
import com.liangzhmj.cat.ext.wechat.thirdplatform.ThirdAPIUtils;
import com.liangzhmj.cat.ext.wechat.tools.common.protocol.MessageProtocol;
import com.liangzhmj.cat.ext.wechat.tools.common.vo.MsgTemplate;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 消息api - 微信文档[公众号->消息管理->客服消息&模板消息|小程序->客服消息]
 * @author liangzhmj
 *
 */
@Log4j2
public class MsgAPI {

    private static String MSG_URL = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=";
    private static String TEMPLATE_MSG_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=";

    /**
     * 发送文本客服消息
     * @param thirdAppid
     * @param appid
     * @param openid
     * @param content
     * @return
     */
    public static JSONObject sendTextMsg(String thirdAppid,String appid,String openid,String content){
        JSONObject resp = null;
        try {
            JSONObject params = MessageProtocol.textMsg(openid,content);
            resp = ThirdAPIUtils.callAPI(thirdAppid,appid,MSG_URL,params);
        } catch (Exception e) {
            log.error(e);
        }
        return resp;
    }

    /**
     * 发送图片信息
     * @param thirdAppid
     * @param appid
     * @param openid
     * @param mediaId
     * @return
     */
    public static JSONObject sendPicMsg(String thirdAppid, String appid, String openid, String mediaId){
        JSONObject resp = null;
        try {
            JSONObject params = MessageProtocol.picMsg(openid,mediaId);
            resp = ThirdAPIUtils.callAPI(thirdAppid,appid,MSG_URL,params);
        } catch (Exception e) {
            log.error(e);
        }
        return resp;
    }

    /**
     * 发送小程序卡片信息
     * @param thirdAppid
     * @param appid
     * @param openid
     * @param title
     * @param miniAppid
     * @param mediaId
     * @param url
     * @return
     */
    public static JSONObject sendAppCardMsg(String thirdAppid,String appid,String openid,String title,String miniAppid,String mediaId,String url){
        JSONObject resp = null;
        try {
            JSONObject params = MessageProtocol.appCardMsg(openid,title,miniAppid,mediaId,url);
            resp = ThirdAPIUtils.callAPI(thirdAppid,appid,MSG_URL,params);
        } catch (Exception e) {
            log.error(e);
        }
        return resp;
    }
    /**
     * 发送小程序卡片信息(小程序不用miniAppid)
     * @param thirdAppid
     * @param appid
     * @param openid
     * @param title
     * @param mediaId
     * @param url
     * @return
     */
    public static JSONObject sendAppCardMsg(String thirdAppid,String appid,String openid,String title,String mediaId,String url){
        return sendAppCardMsg(thirdAppid,appid,openid,title,null,mediaId,url);
    }

    /**
     * 发送图文信息(小程序专用)
     * @param thirdAppid
     * @param appid
     * @param openid
     * @param title
     * @param descr
     * @param thumbUrl
     * @param url
     * @return
     */
    public static JSONObject sendPicLinkMsgMini(String thirdAppid,String appid,String openid,String title,String descr,String thumbUrl,String url){
        JSONObject resp = null;
        try {
            JSONObject params = MessageProtocol.picLinkMsgMini(openid,title,descr,thumbUrl,url);
            resp = ThirdAPIUtils.callAPI(thirdAppid,appid,MSG_URL,params);
        } catch (Exception e) {
            log.error(e);
        }
        return resp;
    }


    /**
     * 发送图文链接(单条图文,公众号专用)
     * @param thirdAppid
     * @param appid
     * @param openid
     * @param title
     * @param desc
     * @param picUrl
     * @param url
     * @return
     */
    public static JSONObject sendPicLinkMsgOffic(String thirdAppid,String appid,String openid,String title,String desc,String picUrl,String url){
        JSONObject resp = null;
        try {
            JSONArray articles = MessageProtocol.picLinkMsgOffic(openid,title,desc,picUrl,url);
            resp = sendPicTextMsg(thirdAppid, appid, openid, articles);
        } catch (Exception e) {
            log.error(e);
        }
        return resp;
    }

    /**
     * 发送图文客服消息(公众号)
     * @param thirdAppid
     * @param appid
     * @param openid
     * @param articles
     * @return
     */
    public static JSONObject sendPicTextMsg(String thirdAppid, String appid, String openid, JSONArray articles){
        JSONObject resp = null;
        try {
            JSONObject params = MessageProtocol.picTextMsg(openid,articles);
            resp = ThirdAPIUtils.callAPI(thirdAppid,appid,MSG_URL,params);
        } catch (Exception e) {
            log.error(e);
        }
        return resp;
    }

    /**
     * 发送消息模板(公众号)
     * @param thirdAppid
     * @param appid
     * @param template
     */
    public static JSONObject sendMsgTemplate(String thirdAppid, String appid, MsgTemplate template){
        JSONObject resp = null;
        try {
            if(template == null){
                throw new WechatException("template为空");
            }
            resp = ThirdAPIUtils.callAPI(thirdAppid,appid,TEMPLATE_MSG_URL,template.toJSON());
        } catch (Exception e) {
            log.error(e);
        }
        return resp;
    }

    /**
     * 发送消息模板(公众号)
     * @param thirdAppid
     * @param appid
     * @param params
     */
    public static JSONObject sendMsgTemplate(String thirdAppid,String appid,JSONObject params){
        JSONObject resp = null;
        try {
            if(params == null){
                throw new WechatException("params为空");
            }
            resp = ThirdAPIUtils.callAPI(thirdAppid,appid,TEMPLATE_MSG_URL,params);
        } catch (Exception e) {
            log.error(e);
        }
        return resp;
    }
}
