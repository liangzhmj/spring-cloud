package com.liangzhmj.cat.ext.wechat.thirdplatform;

import com.liangzhmj.cat.ext.wechat.exception.WechatException;
import com.liangzhmj.cat.ext.wechat.thirdplatform.component.ThirdAuthComponent;
import com.liangzhmj.cat.tools.http.HttpUtils;
import com.liangzhmj.cat.tools.random.RandomUtils;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONObject;
import org.apache.http.entity.ContentType;

import java.io.File;
import java.util.Map;

/**
 * 第三方平台API工具类
 * @author liangzhmj
 *
 */
@Log4j2
public class ThirdAPIUtils {
	
	/**
	 * 调用component接口(平台级别)
	 * @param thirdAppid
	 * @param url
	 * @param params
	 * @return
	 */
	public static JSONObject callComponentAPI(String thirdAppid,String url,JSONObject params){
		try {
			String token = ThirdAuthComponent.getComponentToken(thirdAppid, null, false);
			if(StringUtils.isEmpty(token)){
				throw new WechatException("平台["+thirdAppid+"]获取token错误");
			}
			String resp = HttpUtils.post(url+token,null, params.toString().getBytes("UTF-8"), null, 10000);
			JSONObject rjson = JSONObject.fromObject(resp);
			if(rjson != null && ("40001".equals(String.valueOf(rjson.optInt("errcode"))) 
					|| "42001".equals(StringUtils.getCleanString(rjson.optInt("errcode"))))){//token过期
				token = ThirdAuthComponent.getComponentToken(thirdAppid,token, true);//强刷
				resp = HttpUtils.post(url+token,null, params.toString().getBytes("UTF-8"), null, 10000);
				rjson = JSONObject.fromObject(resp);
			}
			checkError(thirdAppid,url,rjson);
			return rjson;
		} catch (Exception e) {
			log.error(e);
		}
		return null;
	}

	/**
	 * 调用component接口(平台级别)
	 * @param thirdAppid
	 * @param url
	 * @return
	 */
	public static JSONObject callComponentAPIGet(String thirdAppid,String url){
		try {
			String token = ThirdAuthComponent.getComponentToken(thirdAppid, null, false);
			if(StringUtils.isEmpty(token)){
				throw new WechatException("平台["+thirdAppid+"]获取token错误");
			}
			String resp = HttpUtils.get(url+token,10000);
			JSONObject rjson = JSONObject.fromObject(resp);
			if(rjson != null && ("40001".equals(String.valueOf(rjson.optInt("errcode"))) 
					|| "42001".equals(StringUtils.getCleanString(rjson.optInt("errcode"))))){//token过期
				token = ThirdAuthComponent.getComponentToken(thirdAppid,token, true);//强刷
				resp = HttpUtils.get(url+token,10000);
				rjson = JSONObject.fromObject(resp);
			}
			checkError(thirdAppid,url,rjson);
			return rjson;
		} catch (Exception e) {
			log.error(e);
		}
		return null;
	}

	/**
	 * 调用api接口(号级别)
	 * @param thirdAppid
	 * @param appid
	 * @param url
	 * @param params
	 * @return
	 */
	public static JSONObject callAPI(String thirdAppid,String appid,String url,JSONObject params){
		try {
			String token = ThirdAuthComponent.getAuthToken(thirdAppid, appid, null, false);
			if(StringUtils.isEmpty(token) || token.indexOf("非法") != -1){
				throw new WechatException("公众号["+thirdAppid+"]-["+appid+"]获取token错误:"+token);
			}
			String resp = HttpUtils.post(url+token,null, params.toString().getBytes("UTF-8"), null, 10000);
			JSONObject rjson = JSONObject.fromObject(resp);
			if(rjson != null && ("40001".equals(String.valueOf(rjson.optInt("errcode"))) 
					|| "42001".equals(StringUtils.getCleanString(rjson.optInt("errcode"))))){//token过期
				token = ThirdAuthComponent.getAuthToken(thirdAppid, appid, token, true);//强刷
				resp = HttpUtils.post(url+token,null, params.toString().getBytes("UTF-8"), null, 10000);
				rjson = JSONObject.fromObject(resp);
			}
			checkError(thirdAppid,appid,url,rjson);
			return rjson;
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return null;
	}

	/**
	 * 调用api接口(号级别)
	 * @param thirdAppid
	 * @param appid
	 * @param url
	 * @param headers
	 * @param data
	 * @return
	 */
	public static JSONObject callAPI(String thirdAppid, String appid, String url, Map<String,String> headers, File data){
		try {
			String token = ThirdAuthComponent.getAuthToken(thirdAppid, appid, null, false);
			if(StringUtils.isEmpty(token) || token.indexOf("非法") != -1){
				throw new WechatException("公众号["+thirdAppid+"]-["+appid+"]获取token错误:"+token);
			}
			String resp = HttpUtils.post(url+token,headers, data, 15000);
			JSONObject rjson = JSONObject.fromObject(resp);
			if(rjson != null && ("40001".equals(String.valueOf(rjson.optInt("errcode")))
					|| "42001".equals(StringUtils.getCleanString(rjson.optInt("errcode"))))){//token过期
				token = ThirdAuthComponent.getAuthToken(thirdAppid, appid, token, true);//强刷
				resp = HttpUtils.post(url+token,headers, data, 15000);
				rjson = JSONObject.fromObject(resp);
			}
			checkError(thirdAppid,appid,url,rjson);
			return rjson;
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return null;
	}

	/**
	 * 上传素材
	 * @param thirdAppid
	 * @param appid
	 * @param url
	 * @param headers
	 * @param contentType
	 * @param data
	 * @param ext
	 * @return
	 */
	public static JSONObject uploadMedia(String thirdAppid, String appid, String url, Map<String,String> headers, ContentType contentType, byte[] data,String ext){
		try {
			String token = ThirdAuthComponent.getAuthToken(thirdAppid, appid, null, false);
			if(StringUtils.isEmpty(token) || token.indexOf("非法") != -1){
				throw new WechatException("公众号["+thirdAppid+"]-["+appid+"]获取token错误:"+token);
			}
			String filename = RandomUtils.getUUID().replaceAll("-","")+"."+ext;
			String resp = HttpUtils.postMedia(url+token,headers, data,contentType, filename, 15000);
			JSONObject rjson = JSONObject.fromObject(resp);
			if(rjson != null && ("40001".equals(String.valueOf(rjson.optInt("errcode")))
					|| "42001".equals(StringUtils.getCleanString(rjson.optInt("errcode"))))){//token过期
				token = ThirdAuthComponent.getAuthToken(thirdAppid, appid, token, true);//强刷
				resp = HttpUtils.postMedia(url+token,headers, data,contentType, filename, 15000);
				rjson = JSONObject.fromObject(resp);
			}
			checkError(thirdAppid,appid,url,rjson);
			return rjson;
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return null;
	}

	/**
	 * 调用api接口(号级别)
	 * @param thirdAppid
	 * @param appid
	 * @param token
	 * @param url
	 * @param params
	 * @return
	 */
	public static JSONObject callAPI(String thirdAppid,String appid,String url,String token,JSONObject params){
		try {
			if(StringUtils.isEmpty(token)){
				token = ThirdAuthComponent.getAuthToken(thirdAppid, appid, null, false);
			}
			String resp = HttpUtils.post(url+token,null, params.toString().getBytes("UTF-8"), null, 10000);
			JSONObject rjson = JSONObject.fromObject(resp);
			if(rjson != null && ("40001".equals(String.valueOf(rjson.optInt("errcode"))) 
					|| "42001".equals(StringUtils.getCleanString(rjson.optInt("errcode"))))){//token过期
				token = ThirdAuthComponent.getAuthToken(thirdAppid, appid, token, true);//强刷
				resp = HttpUtils.post(url+token,null, params.toString().getBytes("UTF-8"), null, 10000);
				rjson = JSONObject.fromObject(resp);
			}
			checkError(thirdAppid,appid,url,rjson);
			return rjson;
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return null;
	}

	/**
	 * 调用api接口(号级别)
	 * @param thirdAppid
	 * @param appid
	 * @param url
	 * @return
	 */
	public static JSONObject callAPIGet(String thirdAppid,String appid,String url){
		try {
			String token = ThirdAuthComponent.getAuthToken(thirdAppid, appid, null, false);
			if(StringUtils.isEmpty(token)){
				throw new WechatException("公众号["+thirdAppid+"]-["+appid+"]获取token错误");
			}
			String resp = HttpUtils.get(url+token, 10000);
			JSONObject rjson = JSONObject.fromObject(resp);
			if(rjson != null && ("40001".equals(String.valueOf(rjson.optInt("errcode"))) 
					|| "42001".equals(StringUtils.getCleanString(rjson.optInt("errcode"))))){//token过期
				token = ThirdAuthComponent.getAuthToken(thirdAppid, appid, token, true);//强刷
				resp = HttpUtils.get(url+token, 10000);
				rjson = JSONObject.fromObject(resp);
			}
			checkError(thirdAppid,appid,url,rjson);
			return rjson;
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return null;
	}

	private static void checkError(String thirdAppid,String appid,String url,JSONObject rjson){
		if(rjson == null || !rjson.containsKey("errcode") || rjson.getInt("errcode") != 0){
			if(rjson != null && rjson.has("openid")){//有些没有errcode的例如用户信息接口
				return;
			}
			log.info("第三方平台["+thirdAppid+"]主体["+appid+"]访问微信api["+url+"]处理失败:"+StringUtils.substring(1000,rjson.toString()));
		}
	}
	private static void checkError(String thirdAppid,String url,JSONObject rjson){
		if(rjson == null || !rjson.containsKey("errcode") || rjson.getInt("errcode") != 0){
			if(rjson != null && rjson.has("openid")){//有些没有errcode的例如用户信息接口
				return;
			}
			log.info("第三方平台["+thirdAppid+"]访问微信api["+url+"]处理失败:"+StringUtils.substring(1000,rjson.toString()));
		}
	}
}
