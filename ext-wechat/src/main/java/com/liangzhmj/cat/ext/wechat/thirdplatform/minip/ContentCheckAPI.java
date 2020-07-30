package com.liangzhmj.cat.ext.wechat.thirdplatform.minip;

import com.liangzhmj.cat.ext.wechat.thirdplatform.ThirdAPIUtils;
import net.sf.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 内容安全检查 - 微信文档[小程序->服务端->内容安全]
 * @author liangzhmj
 */
public class ContentCheckAPI {

    /**
     * 校验一张图片是否含有违法违规内容。
     * 应用场景举例：
     * 1.图片智能鉴黄：涉及拍照的工具类应用(如美拍，识图类应用)用户拍照上传检测；电商类商品上架图片检测；媒体类用户文章里的图片检测等；
     * 2.敏感人脸识别：用户头像；媒体类用户文章里的图片检测；社交类用户上传的图片检测等。 频率限制：单个 appId 调用上限为 2000 次/分钟，200,000 次/天*（图片大小限制：1M）
     * @param thirdAppid
     * @param appid
     * @param data
     * @return errcode:0 正常
     */
    public static JSONObject imgSecCheck(String thirdAppid,String appid,File data){
        String url = "https://api.weixin.qq.com/wxa/img_sec_check?access_token=";
        Map<String,String> header = new HashMap<>();
        header.put("Connection", "keep-alive");
        header.put("Accept", "*/*");
        header.put("Content-Type", "multipart/form-data");
        header.put("User-Agent",
                "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
        return ThirdAPIUtils.callAPI(thirdAppid,appid,url,header,data);
    }

    /**
     * 异步校验图片/音频是否含有违法违规内容。
     * 应用场景举例：
     * 1.语音风险识别：社交类用户发表的语音内容检测；
     * 2.图片智能鉴黄：涉及拍照的工具类应用(如美拍，识图类应用)用户拍照上传检测；电商类商品上架图片检测；媒体类用户文章里的图片检测等；
     * 3.敏感人脸识别：用户头像；媒体类用户文章里的图片检测；社交类用户上传的图片检测等。 频率限制：单个 appId 调用上限为 2000 次/分钟，200,000 次/天；文件大小限制：单个文件大小不超过10M
     * @param thirdAppid
     * @param appid
     * @param mediaUrl
     * @param type 1:音频;2:图片
     * @return errcode:0 正常
     */
    public static JSONObject mediaCheckAsync(String thirdAppid,String appid,String mediaUrl,int type){
        String url = "https://api.weixin.qq.com/wxa/media_check_async?access_token=";
        JSONObject params = new JSONObject();
        params.put("media_url",mediaUrl);
        params.put("media_type",type);
        return ThirdAPIUtils.callAPI(thirdAppid,appid,url,params);
    }
    /**
     * 检查一段文本是否含有违法违规内容。
     * 应用场景举例：
     * 1.用户个人资料违规文字检测；
     * 2.媒体新闻类用户发表文章，评论内容检测；
     * 3.游戏类用户编辑上传的素材(如答题类小游戏用户上传的问题及答案)检测等。 频率限制：单个 appId 调用上限为 4000 次/分钟，2,000,000 次/天*
     * @param thirdAppid
     * @param appid
     * @param content
     * @return errcode:0 正常
     */
    public static JSONObject msgSecCheck(String thirdAppid,String appid,String content){
        String url = "https://api.weixin.qq.com/wxa/msg_sec_check?access_token=";
        JSONObject params = new JSONObject();
        params.put("content",content);
        return ThirdAPIUtils.callAPI(thirdAppid,appid,url,params);
    }

    
}
