package com.liangzhmj.cat.ext.wechat.tools.common.protocol;

import com.liangzhmj.cat.tools.json.JSONBase;
import com.liangzhmj.cat.tools.json.JSONField;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.Getter;
import lombok.Setter;
import net.sf.json.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * 消息事件
 * @author liangzhmj
 */
@Setter
@Getter
public class MessageEventProtocol extends JSONBase {

    @JSONField(name = "ToUserName")
    private String toUser;
    @JSONField(name = "FromUserName")
    private String fromUser;
    @JSONField(name = "CreateTime",clazz = JSONBase.LONG)
    private Long createTime;
    //消息类型：event,text,image,voice,miniprogrampage...
    @JSONField(name = "MsgType")
    private String msgType;
    //事件:subscribe,unsubscribe,SCAN,LOCATION,weapp_audit_success,weapp_audit_fail...
    @JSONField(name = "Event")
    private String event;
    @JSONField(name = "MsgId")
    private String msgId;


    //===================== event->user_enter_tempsession(小程序用户进入客服事件)
    @JSONField(name = "SessionFrom")
    private String sessionFrom;
    //=====================

    //===================== event->SCAN(扫描关注或者进入会话)
    @JSONField(name = "Ticket")
    private String ticket;
    @JSONField(name = "EventKey")
    private String eventKey;
    //=====================

    //===================== event->weapp_audit_fail(失败原因)
    @JSONField(name = "Reason")
    private String reason;
    //=====================

    //===================== text(文本消息)
    @JSONField(name = "Content")
    private String content;
    //=====================

    //===================== image,voice(图片，语音,视频消息)
    @JSONField(name = "PicUrl")
    private String picUrl;//图片
    @JSONField(name = "Format")
    private String format;//语音
    @JSONField(name = "MediaId")
    private String mediaId;//图片语音视频共用
    //=====================

    //===================== miniprogrampage(小程序卡片)
    @JSONField(name = "Title")
    private String title;
    @JSONField(name = "AppId")
    private String appid;
    @JSONField(name = "PagePath")
    private String path;
    @JSONField(name = "ThumbUrl")
    private String thumbUrl;
    @JSONField(name = "ThumbMediaId")
    private String thumbMediaId;//视频封面共用
    //=====================


    /**
     * json赋值
     * @param jsonStr
     */
    public void initJSON(String jsonStr){
        JSONObject json = JSONObject.fromObject(jsonStr);
        this.fromJSON(json);
    }

    /**
     * xml赋值
     * @param xmlStr
     */
    public void initXML(String xmlStr) throws Exception{
        Document document = DocumentHelper.parseText(xmlStr);
        Element root = document.getRootElement();
        this.fromUser = root.elementTextTrim("FromUserName");
        this.toUser = root.elementTextTrim("ToUserName");
        this.msgType = root.elementTextTrim("MsgType");
        this.event = root.elementTextTrim("Event");
        this.msgId = root.elementTextTrim("MsgId");
        this.appid = root.elementTextTrim("AppId");
        this.content = root.elementTextTrim("Content");
        this.createTime = StringUtils.getCleanLong(root.elementTextTrim("CreateTime"));
        this.format = root.elementTextTrim("Format");
        this.sessionFrom = root.elementTextTrim("SessionFrom");
        this.ticket = root.elementTextTrim("Ticket");
        this.reason = root.elementTextTrim("Reason");
        this.picUrl = root.elementTextTrim("PicUrl");
        this.mediaId = root.elementTextTrim("MediaId");
        this.title = root.elementTextTrim("Title");
        this.path = root.elementTextTrim("PagePath");
        this.thumbUrl = root.elementTextTrim("ThumbUrl");
        this.thumbMediaId = root.elementTextTrim("ThumbMediaId");
        this.eventKey = root.elementTextTrim("EventKey");
    }
}
