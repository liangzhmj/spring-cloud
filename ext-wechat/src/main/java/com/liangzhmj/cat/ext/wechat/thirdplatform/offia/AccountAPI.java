package com.liangzhmj.cat.ext.wechat.thirdplatform.offia;

import com.liangzhmj.cat.ext.wechat.offiaccount.protocol.AccountProtocol;
import com.liangzhmj.cat.ext.wechat.thirdplatform.ThirdAPIUtils;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONObject;

/**
 * 账号管理api - 微信文档[公众号->账号管理]
 * @author liangzhmj
 */
@Log4j2
public class AccountAPI {

    /**
     * 生成永久渠道二维码(对应的扫描推送事件：SCAN)
     * https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=TICKET
     * 提醒：TICKET记得进行UrlEncode
     * @param thirdAppid
     * @param appid
     * @param param
     * @return
     */
    public static String perpetualQr(String thirdAppid,String appid,String param){
        try {
            JSONObject params = AccountProtocol.perpetualQrcode(param);
            JSONObject resp = ThirdAPIUtils.callAPI(thirdAppid,appid,"https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=", params);
            log.info("公众号["+appid+"]生成永久渠道二维码参数:"+params+"---"+resp);
            //{"ticket":"gQH47joAAAAAAAAAASxodHRwOi8vd2VpeGluLnFxLmNvbS9xL2taZ2Z3TVRtNzJXV1Brb3ZhYmJJAAIEZ23sUwMEmm3sUw==","expire_seconds":60,"url":"http://weixin.qq.com/q/kZgfwMTm72WWPkovabbI"}
            return resp.getString("ticket");
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    /**
     * 生成临时渠道二维码(对应的扫描推送事件：SCAN)
     * https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=TICKET
     * 提醒：TICKET记得进行UrlEncode
     * @param thirdAppid
     * @param appid
     * @param param
     * @param expireTime
     * @return
     */
    public static String tempQr(String thirdAppid,String appid,String param,int expireTime){
        try {
            JSONObject params = AccountProtocol.tempQrcode(param,expireTime);
            JSONObject resp = ThirdAPIUtils.callAPI(thirdAppid,appid,"https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=", params);
            log.info("公众号["+appid+"]生成临时渠道二维码参数:"+params+"---"+resp);
            //{"ticket":"gQH47joAAAAAAAAAASxodHRwOi8vd2VpeGluLnFxLmNvbS9xL2taZ2Z3TVRtNzJXV1Brb3ZhYmJJAAIEZ23sUwMEmm3sUw==","expire_seconds":60,"url":"http://weixin.qq.com/q/kZgfwMTm72WWPkovabbI"}
            return resp.getString("ticket");
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }


    /**
     * 长连接转短链接
     * @param thirdAppid
     * @param appid
     * @param longUrl
     * @return
     */
    public static String shortUrl(String thirdAppid,String appid,String longUrl){
        try {
            JSONObject params = AccountProtocol.long2short(longUrl);
            JSONObject resp = ThirdAPIUtils.callAPI(thirdAppid,appid,"https://api.weixin.qq.com/cgi-bin/shorturl?access_token=", params);
            log.info("公众号["+appid+"]生成临时渠道二维码参数:"+params+"---"+resp);
            //{"errcode":0,"errmsg":"ok","short_url":"http:\/\/w.url.cn\/s\/AvCo6Ih"}
            return resp.getString("short_url");
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

}
