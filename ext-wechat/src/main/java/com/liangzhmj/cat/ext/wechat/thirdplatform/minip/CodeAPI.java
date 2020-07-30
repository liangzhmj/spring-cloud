package com.liangzhmj.cat.ext.wechat.thirdplatform.minip;

import com.liangzhmj.cat.ext.wechat.exception.WechatException;
import com.liangzhmj.cat.ext.wechat.thirdplatform.ThirdAPIUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 代理小程序管理代码库 - 微信文档[第三方平台->代小程序实现业务->代码管理]
 * @author liangzhmj
 *
 */
public class CodeAPI {

	private static Logger log = Logger.getLogger(CodeAPI.class);
	
	
	/**
	 * 上传代码(提交代码)
	 * @param thirdAppid
	 * @param appid
	 * @param templateId
	 * @param extJson
	 * @param version
	 * @param desc
	 */
	public static void commit(String thirdAppid,String appid,int templateId,String extJson,String version,String desc){
		JSONObject params = new JSONObject();
		params.put("template_id", templateId);
		params.put("ext_json", JSONObject.fromObject(extJson.trim())+"###SB_AUTOCONVERT###");
		params.put("user_version", version);
		params.put("user_desc", desc);
		params = JSONObject.fromObject(params.toString().replace("###SB_AUTOCONVERT###", ""));
		JSONObject rjson = ThirdAPIUtils.callAPI(thirdAppid, appid, "https://api.weixin.qq.com/wxa/commit?access_token=", params);
		if(rjson.getInt("errcode") != 0){
			throw new WechatException("为小程序["+appid+"]提交代码["+templateId+"]失败:"+rjson);
		}
	}
	
	/**
	 * 获取已上传的代码的页面列表（用于审核）
	 * @param thirdAppId
	 * @param appId
	 * @return
	 */
	public static List<String> getPaths(String thirdAppId,String appId){
		JSONObject rjson = ThirdAPIUtils.callAPIGet(thirdAppId, appId, "https://api.weixin.qq.com/wxa/get_page?access_token=");
		if(rjson.getInt("errcode") != 0){
			throw new WechatException("获取小程序["+thirdAppId+"]-["+appId+"]路径列表失败:"+rjson);
		}
		JSONArray ja = rjson.getJSONArray("page_list");
		List<String> paths = new ArrayList<String>();
		for (int i = 0; i < ja.size(); i++) {
			paths.add(ja.getString(i));
		}
		return paths;
	}

	/**
	 * 提交审核
	 * @param thirdAppid
	 * @param appid
	 * @param params
	 * @return 审核编号
	 */
	public static long audit(String thirdAppid,String appid,JSONObject params){
		JSONObject rjson = ThirdAPIUtils.callAPI(thirdAppid, appid, "https://api.weixin.qq.com/wxa/submit_audit?access_token=",params);
		log.info("param:"+params+"---------:"+rjson);
		if(rjson.getInt("errcode") != 0){
			throw new WechatException("小程序["+thirdAppid+"]-["+appid+"]提交审核失败:"+rjson);
		}
		return rjson.getLong("auditid");
	}
	
	/**
	 * 获取审核结果
	 * @param thirdAppid
	 * @param appid
	 * @param auditId 审核id
	 * @return 0审核状态(0为审核成功，1为审核失败，2为审核中),1原因
	 */
	public static Object[] getAuditRes(String thirdAppid,String appid,long auditId){
		JSONObject params = new JSONObject();
		params.put("auditid", auditId);
		JSONObject rjson = ThirdAPIUtils.callAPI(thirdAppid, appid, "https://api.weixin.qq.com/wxa/get_auditstatus?access_token=",params);
		if(rjson.getInt("errcode") != 0){
			throw new WechatException("获取小程序["+thirdAppid+"]-["+appid+"]-审核编号["+auditId+"]审核结果失败:"+rjson);
		}
		return new Object[]{rjson.getInt("status"),rjson.optString("reason")};
	}

	/**
	 * 发布已通过审核的小程序
	 * @param thirdAppid
	 * @param appid
	 * @return
	 */
	public static int issue(String thirdAppid,String appid){
		JSONObject params = new JSONObject();
		JSONObject rjson = ThirdAPIUtils.callAPI(thirdAppid, appid, "https://api.weixin.qq.com/wxa/release?access_token=",params);
		if(rjson.getInt("errcode") != 0){
			throw new WechatException("获取小程序["+thirdAppid+"]-["+appid+"]-发布失败:"+rjson);
		}
		return 0;
	}
	
}
