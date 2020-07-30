package com.liangzhmj.cat.ext.wechat.thirdplatform.offia;


import com.liangzhmj.cat.ext.wechat.exception.WechatException;
import com.liangzhmj.cat.ext.wechat.offiaccount.vo.User;
import com.liangzhmj.cat.ext.wechat.thirdplatform.ThirdAPIUtils;
import com.liangzhmj.cat.tools.http.HttpUtils;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONObject;

import java.net.URLEncoder;

/**
 * 网页授权用户api - 微信文档[第三方平台->代公众号实现业务->代公众号发起网页授权]
 * @author liangzhmj
 *
 */
@Log4j2
public class WebUserAPI {


    /**
     * 微信客户端调用访问该url回重定向到微信那边，然后弹框授权登录
     * -ps:若提示“该链接无法访问”，请检查参数是否填写错误，是否拥有 scope 参数对应的授权作用域权限
     * @param thirdAppid
     * @param appid 授权公众号的appid
     * @param redirectUrl 点击授权后，微信重定向到redirectUrl，允许带上参数,如果用户禁止授权，则微信不会返回code
     * @return
     * @throws Exception
     */
    public static String loginUrl(String thirdAppid,String appid,String redirectUrl) throws Exception{
        return "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appid+"&redirect_uri="+ URLEncoder.encode(redirectUrl,"UTF-8")+"&response_type=code&scope=snsapi_userinfo&component_appid="+thirdAppid+"#wechat_redirect";
    }

    /**
     * 获取用户网页授权的token，外部决定要不要持久化
     * -ps:由于安全方面的考虑，对访问该链接的客户端有 IP 白名单的要求。
     * @param thirdAppid
     * @param appid
     * @return
     */
    public static JSONObject userToken(String thirdAppid,String appid){
        String url = "https://api.weixin.qq.com/sns/oauth2/component/access_token?appid="+appid+"&code=CODE&grant_type=authorization_code&component_appid="+thirdAppid+"&component_access_token=";
        JSONObject res = ThirdAPIUtils.callComponentAPIGet(thirdAppid,url);
        /*
        {
          "access_token": "ACCESS_TOKEN",
          "expires_in": 7200,
          "refresh_token": "REFRESH_TOKEN",
          "openid": "OPENID",
          "scope": "SCOPE"
        }
         */
        return res;
    }

    /**
     * 刷新用户网页访问token（如果有需要的话，例如原token失效），外部决定要不要持久化
     * -ps:由于安全方面的考虑，对访问该链接的客户端有 IP 白名单的要求。
     * @param thirdAppid
     * @param appid
     * @param refreshToken 获取用户token或者刷新token都会返回refreshToken
     * @return
     */
    public static JSONObject refreshUserToken(String thirdAppid,String appid,String refreshToken){
        String url = "https://api.weixin.qq.com/sns/oauth2/component/refresh_token?appid="+appid+"&grant_type=refresh_token&component_appid="+thirdAppid+"&refresh_token="+refreshToken+"&component_access_token=";
        JSONObject res = ThirdAPIUtils.callComponentAPIGet(thirdAppid,url);
        /*
            {
              "access_token": "ACCESS_TOKEN",
              "expires_in": 7200,
              "refresh_token": "REFRESH_TOKEN",
              "openid": "OPENID",
              "scope": "SCOPE"
            }
         */
        return res;
    }

    /**
     * 第三方平台获取扫描进来的用户信息（用户扫描，点击登录，微信回调告诉服务器响应的appid和code）
     * @param thirdAppid
     * @param appid 用于授权的公众号的appid
     * @param token 用户网页访问的token
     * @param openid 用户的openid
     * @return
     */
    public static User getUserInfo(String thirdAppid, String appid, String token, String openid){
        String res = HttpUtils.get("https://api.weixin.qq.com/sns/userinfo?access_token="+token+"&openid="+openid+"&lang=zh_CN", 10000);
        /*
            {
              "openid": " OPENID",
              "nickname": "NICKNAME",
              "sex": "1",
              "province": "PROVINCE",
              "city": "CITY",
              "country": "COUNTRY",
              "headimgurl": "http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/46",
              "privilege": ["PRIVILEGE1", "PRIVILEGE2"],
              "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
            }
         */
        JSONObject json = JSONObject.fromObject(res);
        String loadOpenid = json.optString("openid");
        if(StringUtils.isEmpty(loadOpenid)){
            throw new WechatException("获取不到用户信息:"+res);
        }
        return new User(json);
    }
}
