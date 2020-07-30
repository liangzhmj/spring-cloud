package com.liangzhmj.cat.ext.wechat.miniprogram.openability;

import com.liangzhmj.cat.ext.wechat.config.DBConfigUtils;
import com.liangzhmj.cat.ext.wechat.exception.WechatException;
import com.liangzhmj.cat.tools.http.HttpUtils;
import com.liangzhmj.cat.tools.security.AESUtil;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 小程序开放能力 - 微信文档[小程序->开放能力->用户信息]
 *  -用户开放能力
 * @author liangzhmj
 * @Date 2019-09-26 16:45:12
 */
@Log4j2
public class UserAbility {

    private static Pattern appidPattern = Pattern.compile("https://servicewechat.com/(\\w+)/.+");

    /**
     * 小程序登陆
     *
     * @param appid
     * @param code
     * @return 正常返回:{errcode:0,openid:"o4iAw5VpePcdMeM2r8AobaRKC7iw",session_key:"xxxxx",unionid:"如果有"}
     */
    public static JSONObject login(String appid, String code) {
        //0:appid,1:app密钥,2:商户号,3:商户密钥,4:支付通知路径
        String[] config = DBConfigUtils.getSubjectConfig(appid);
        //向微信前端发起请求
        String res = HttpUtils.get("https://api.weixin.qq.com/sns/jscode2session?appid=" + appid + "&secret=" + config[1] + "&js_code=" + code + "&grant_type=authorization_code", 5000);
        log.info("登陆结果:" + res);
        JSONObject json = JSONObject.fromObject(res);
        if (json == null || !json.has("session_key")) {//登陆失败
            throw new WechatException("登陆失败：" + res);
        }
        return json;
    }


    /**
     * 解密小程序提交过来的
     * 用户信息
     * 手机号码
     * 等需要解密信息
     * @param uinfo
     * @param sessionKey
     * @param iv
     * @return
     *  用户信息返回:
     *  {"openId":"o4iAw5VpePcdMeM2r8AobaRKC7iw","nickName":"__","gender":1,"language":"zh_CN","city":"Guangzhou","province":"Guangdong","country":"China","avatarUrl":"https://wx.qlogo.cn/mmopen/vi_32/EE7eHqEG7f7jFiasWcwAO1sx4hJrCPy4lxLhLm97knwAdHVuAlPeXeC5Rv291dSAd6A910WADJOdiba3A3nvpsibg/132","watermark":{"timestamp":1575621122,"appid":"wx6146811ba4560c00"}}
     *  <br/>
     *  手机号码返回:
     *  "phoneNumber":"13800138000","purePhoneNumber":"13800138000","countryCode":"86","watermark":{"appid":"APPID","timestamp":TIMESTAMP}}
     *
     * @throws Exception
     */
    public static JSONObject decryptUserInfo(String uinfo, String sessionKey, String iv) throws Exception {
        byte[] cont = AESUtil.decrypt(Base64.decodeBase64(uinfo), Base64.decodeBase64(sessionKey), Base64.decodeBase64(iv));
        if (cont == null) {
            log.info("解密用户信息失败:uinfo:[" + uinfo + "]-sessionKey:[" + sessionKey + "]-iv[" + iv + "]");
            throw new WechatException("解密用户信息失败");
        }
        String res = new String(cont, "UTF-8");
        log.info("解密结果：" + res);
        return JSONObject.fromObject(res);
    }

    /**
     * 根据referer获取appId
     *
     * @param req
     * @return
     */
    public static String getAppIdByReferer(HttpServletRequest req) {
        String url = req.getHeader("Referer");
        if (StringUtils.isEmpty(url)) {
            url = req.getHeader("referer");
        }
        if (StringUtils.isEmpty(url)) {
            log.info("通过referer为空获取不到appid");
            return null;
        }
        Matcher m = appidPattern.matcher(url);
        if (m.matches()) {
            String appid = m.group(1);
            log.info("通过referer获取到appid为:"+appid);
            return appid;
        }
        log.info("通过referer["+url+"]不匹配获取不到appid");
        return null;
    }
}