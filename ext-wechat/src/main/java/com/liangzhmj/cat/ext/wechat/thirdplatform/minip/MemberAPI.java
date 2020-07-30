package com.liangzhmj.cat.ext.wechat.thirdplatform.minip;

import com.liangzhmj.cat.ext.wechat.exception.WechatException;
import com.liangzhmj.cat.ext.wechat.thirdplatform.ThirdAPIUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 代理小程序管理成员 - 微信文档[第三方平台->代小程序实现业务->成员管理]
 * @author liangzhmj
 *
 */
public class MemberAPI {

	
	/**
	 * 绑定体验者
	 * @param thirdAppid
	 * @param appid
	 * @param wechatId
	 */
	public static int binding(String thirdAppid,String appid,String wechatId){
		JSONObject params = new JSONObject();
		params.put("wechatid", wechatId);
		JSONObject rjson = ThirdAPIUtils.callAPI(thirdAppid, appid, "https://api.weixin.qq.com/wxa/bind_tester?access_token=", params);
		if(rjson.getInt("errcode") != 0){
			throw new WechatException("为小程序["+appid+"]添加成员["+wechatId+"]失败:"+rjson);
		}
		return 0;
	}

	/**
	 * 体验者列表
	 * @param thirdAppId
	 * @param appId
	 * @return
	 */
	public static List<String> list(String thirdAppId,String appId){
		JSONObject params = new JSONObject();
		params.put("action", "get_experiencer");
		JSONObject rjson = ThirdAPIUtils.callAPI(thirdAppId, appId, "https://api.weixin.qq.com/wxa/memberauth?access_token=", params);
		if(rjson.getInt("errcode") != 0){
			throw new WechatException("查询小程序["+appId+"]成员失败:"+rjson);
		}
		JSONArray ja = rjson.getJSONArray("members");
		List<String> users = new ArrayList<>();
		for (int i = 0; i < ja.size(); i++) {
			if(ja.getJSONObject(i).has("userstr")){
				users.add(ja.getJSONObject(i).getString("userstr"));
			}
		}
		return users;
	}
	
}
