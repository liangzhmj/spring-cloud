package com.liangzhmj.cat.ext.wechat.thirdplatform.minip;

import com.liangzhmj.cat.ext.wechat.exception.WechatException;
import com.liangzhmj.cat.ext.wechat.thirdplatform.ThirdAPIUtils;
import com.liangzhmj.cat.ext.wechat.thirdplatform.vo.PlugIn;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 代理小程序管理插件 - 微信文档[第三方平台->代小程序实现业务->插件管理]
 * @author liangzhmj
 *
 */
public class PlugInAPI {

	
	
	/**
	 * 为小程序添加插件
	 * @param thirdAppid
	 * @param appid
	 * @param plugInAppid (插件appid)
	 */
	public static int apply(String thirdAppid,String appid,String plugInAppid){
		JSONObject params = new JSONObject();
		params.put("action", "apply");
		params.put("plugin_appid", plugInAppid);
		JSONObject rjson = ThirdAPIUtils.callAPI(thirdAppid, appid, "https://api.weixin.qq.com/wxa/plugin?access_token=", params);
		if(rjson.getInt("errcode") != 0){
			throw new WechatException("为小程序["+appid+"]添加插件["+plugInAppid+"]失败:"+rjson);
		}
		return 0;
	}

	/**
	 * 列出小程序的插件
	 * @param thirdAppid
	 * @param appid
	 * @return
	 */
	public static List<PlugIn> list(String thirdAppid, String appid){
		JSONObject params = new JSONObject();
		params.put("action", "list");
		JSONObject rjson = ThirdAPIUtils.callAPI(thirdAppid, appid, "https://api.weixin.qq.com/wxa/plugin?access_token=", params);
		if(rjson.getInt("errcode") != 0){
			throw new WechatException("查询小程序["+appid+"]插件失败:"+rjson);
		}
		JSONArray ja = rjson.getJSONArray("plugin_list");
		List<PlugIn> plugIns = new ArrayList<PlugIn>();
		for (int i = 0; i < ja.size(); i++) {
			plugIns.add(new PlugIn(thirdAppid, ja.getJSONObject(i)));
		}
		return plugIns;
	}
	
	/**
	 * 删除小程序插件
	 * @param thirdAppid
	 * @param appid
	 * @param plugInAppid
	 * @return
	 */
	public static int del(String thirdAppid,String appid,String plugInAppid){
		JSONObject params = new JSONObject();
		params.put("action", "unbind");
		params.put("plugin_appid", plugInAppid);
		JSONObject rjson = ThirdAPIUtils.callAPI(thirdAppid, appid, "https://api.weixin.qq.com/wxa/plugin?access_token=", params);
		if(rjson.getInt("errcode") != 0){
			throw new WechatException("为小程序["+appid+"]删除插件["+plugInAppid+"]失败:"+rjson);
		}
		return 0;
	}
	
}
