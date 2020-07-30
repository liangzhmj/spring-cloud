package com.liangzhmj.cat.ext.wechat.tools.common.protocol;

import com.liangzhmj.cat.ext.wechat.tools.common.vo.MsgTemplate;
import com.liangzhmj.cat.tools.string.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * 消息协议
 * @author liangzhmj
 */
public class MessageProtocol {
    /**
     * 文本客服消息(共用)
     * @param openid
     * @param content
     * @return
     */
    public static JSONObject textMsg(String openid, String content){
		/*
		{
		  "touser":"OPENID",
		  "msgtype":"text",
		  "text":
		  {
			"content":"Hello World"
		  }
		}
		 */
        JSONObject params = new JSONObject();
        params.put("touser", StringUtils.getCleanString(openid));
        params.put("msgtype", "text");
        JSONObject text = new JSONObject();
        text.put("content", content);
        params.put("text", text);
        return params;
    }

    /**
     * 图片信息(共用)
     * @param openid
     * @param mediaId
     * @return
     */
    public static JSONObject picMsg(String openid,String mediaId){
        /**
         {
         "touser":"OPENID",
         "msgtype":"image",
         "image":
         {
         "media_id":"MEDIA_ID"
         }
         }
         */
        JSONObject params = new JSONObject();
        params.put("touser", StringUtils.getCleanString(openid));
        params.put("msgtype", "image");
        JSONObject image = new JSONObject();
        image.put("media_id", mediaId);
        params.put("image", image);
        return params;
    }

    /**
     * 小程序卡片信息(共用)
     * @param openid
     * @param title
     * @param miniAppid
     * @param mediaId
     * @param url
     * @return
     */
    public static JSONObject appCardMsg(String openid,String title,String miniAppid,String mediaId,String url){
        /**
         {
         "touser":"OPENID",
         "msgtype":"miniprogrampage",
         "miniprogrampage":{
         "title":"title",
         "appid":"appid",//公众号的时候需要
         "pagepath":"pagepath",
         "thumb_media_id":"thumb_media_id"
         }
         }
         */
        JSONObject params = new JSONObject();
        params.put("touser", StringUtils.getCleanString(openid));
        params.put("msgtype", "miniprogrampage");
        JSONObject miniprogrampage = new JSONObject();
        miniprogrampage.put("title", title);
        if(!StringUtils.isEmpty(miniAppid)){
            miniprogrampage.put("appid", miniAppid);
        }
        miniprogrampage.put("pagepath", url);
        miniprogrampage.put("thumb_media_id", mediaId);
        params.put("miniprogrampage", miniprogrampage);
        return params;
    }

    /**
     * 图文信息(小程序专用)
     * @param openid
     * @param title
     * @param descr
     * @param thumbUrl
     * @param url
     * @return
     */
    public static JSONObject picLinkMsgMini(String openid,String title,String descr,String thumbUrl,String url){
        /**
         {
         "touser": "OPENID",
         "msgtype": "link",
         "link": {
         "title": "Happy Day",
         "description": "Is Really A Happy Day",
         "url": "URL",
         "thumb_url": "THUMB_URL"
         }
         }
         */
        JSONObject params = new JSONObject();
        params.put("touser", StringUtils.getCleanString(openid));
        params.put("msgtype", "link");
        JSONObject link = new JSONObject();
        link.put("title", title);
        link.put("description", descr);
        link.put("url", url);
        link.put("thumb_url", thumbUrl);
        params.put("link", link);
        return params;
    }


    /**
     * 图文链接(单条图文,公众号专用)
     * @param openid
     * @param title
     * @param desc
     * @param picUrl
     * @param url
     * @return
     */
    public static JSONArray picLinkMsgOffic(String openid,String title,String desc,String picUrl,String url){
        /**
         {
         "touser":"OPENID",
         "msgtype":"news",
         "news":{
         "articles": [
         {
         "title":"Happy Day",
         "description":"Is Really A Happy Day",
         "url":"URL",
         "picurl":"PIC_URL"
         }
         ]
         }
         }
         */
        JSONArray articles = new JSONArray();
        JSONObject sub = new JSONObject();
        sub.put("title", title);
        sub.put("description", desc);
        sub.put("url", url);
        sub.put("picurl", picUrl);
        articles.add(sub);
        return articles;
    }

    /**
     * 图文客服消息(公众号)
     * @param openid
     * @param articles
     * @return
     */
    public static JSONObject picTextMsg(String openid,JSONArray articles){
        /**
         {
         "touser":"OPENID",
         "msgtype":"news",
         "news":{
         "articles": [
         {
         "title":"Happy Day",
         "description":"Is Really A Happy Day",
         "url":"URL",
         "picurl":"PIC_URL"
         },
         {
         "title":"Happy Day",
         "description":"Is Really A Happy Day",
         "url":"URL",
         "picurl":"PIC_URL"
         }
         ]
         }
         }
         */
        JSONObject params = new JSONObject();
        params.put("touser", StringUtils.getCleanString(openid));
        params.put("msgtype", "news");
        JSONObject news = new JSONObject();
        news.put("articles", articles);
        params.put("news", news);
        return params;
    }

    /**
     * 消息模板(公众号)
     * @param id 模板id
     * @param openid 收信人openid
     * @param url 跳往的链接
     * @param kws 关键字:0:关键字（value必填），1:颜色（color选填）
     */
    public static MsgTemplate msgTemplate(String id, String openid, String url, List<String[]> kws){
        return new MsgTemplate(id,openid,url,kws);
    }
    /**
     * 消息模板(公众号)
     * @param id 模板id
     * @param openid 收信人openid
     * @param miniAppid 跳往小程序的appid
     * @param pagepath 跳往小程序的path
     * @param kws 关键字:0:关键字（value必填），1:颜色（color选填）
     */
    public static MsgTemplate msgTemplate(String id, String openid, String miniAppid, String pagepath, List<String[]> kws){
        JSONObject mini = new JSONObject();
        mini.put("appid",miniAppid);
        mini.put("pagepath",pagepath);
        return new MsgTemplate(id,openid,mini,kws);
    }

    /**
     * 消息模板(小程序)
     * @param id 模板id
     * @param openid 收信人openid
     * @param formid 可以在小程序提交表单的时候获得
     * @param page 跳往小程序的path
     * @param ek 加粗字段
     * @param kws 关键字:0:关键字（value必填），1:颜色（color选填）
     */
    public static MsgTemplate msgTemplate(String id, String openid, String formid,String page,String ek, List<String> kws){
        return new MsgTemplate(id,openid,formid,page,ek,kws);
    }
}
