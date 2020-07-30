package com.liangzhmj.cat.ext.wechat.thirdplatform.minip;

import com.liangzhmj.cat.ext.wechat.exception.WechatException;
import com.liangzhmj.cat.ext.wechat.thirdplatform.ThirdAPIUtils;
import com.liangzhmj.cat.ext.wechat.thirdplatform.vo.Draft;
import com.liangzhmj.cat.ext.wechat.thirdplatform.vo.Template;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 代理小程序管理模板库 - 微信文档[第三方平台->代小程序实现业务->代码模板设置]
 * @author liangzhmj
 *
 */
public class TemplateAPI {

	private static Logger log = Logger.getLogger(TemplateAPI.class);
	
	/**
	 * 获取平台草稿列表
	 * @param thirdAppid
	 * @return
	 */
	public static List<Draft> getDrafts(String thirdAppid){
		try {
			JSONObject params = new JSONObject();
			JSONObject rjson = ThirdAPIUtils.callComponentAPI(thirdAppid, "https://api.weixin.qq.com/wxa/gettemplatedraftlist?access_token=", params);
			JSONArray ja = rjson.getJSONArray("draft_list");	
			if(ja == null || ja.isEmpty()){
				return null;
			}
			List<Draft> dfs = new ArrayList<Draft>();
			for (int i = 0; i < ja.size(); i++) {
				Draft df = new Draft(ja.getJSONObject(i));
				df.setThirdAppId(thirdAppid);
				dfs.add(df);
			}
			return dfs;
		} catch (Exception e) {
			log.error(e);
		}
		return null;
	}

	/**
	 * 获取平台模板列表
	 * @param thirdAppid
	 * @return
	 */
	public static List<Template> getTemplates(String thirdAppid){
		try {
			JSONObject params = new JSONObject();
			JSONObject rjson = ThirdAPIUtils.callComponentAPI(thirdAppid, "https://api.weixin.qq.com/wxa/gettemplatelist?access_token=", params);
			JSONArray ja = rjson.getJSONArray("template_list");	
			if(ja == null || ja.isEmpty()){
				return null;
			}
			List<Template> tms = new ArrayList<Template>();
			for (int i = 0; i < ja.size(); i++) {
				Template df = new Template(ja.getJSONObject(i));
				df.setThirdAppId(thirdAppid);
				tms.add(df);
			}
			return tms;
		} catch (Exception e) {
			log.error(e);
		}
		return null;
	}

	/**
	 * 把草稿添加模板
	 * @param thirdAppid
	 * @param draftid
	 */
	public static void draft2Template(String thirdAppid,int draftid){
		try {
			JSONObject params = new JSONObject();
			params.put("draft_id", draftid);
			JSONObject rjson = ThirdAPIUtils.callComponentAPI(thirdAppid, "https://api.weixin.qq.com/wxa/addtotemplate?access_token=", params);
			if(rjson.getInt("errcode") != 0){
				throw new WechatException("添加模板失败:"+rjson);
			}
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	/**
	 * 删除模板
	 * @param thirdAppid
	 * @param templateid
	 */
	public static void delTemplate(String thirdAppid,int templateid){
		try {
			JSONObject params = new JSONObject();
			params.put("template_id", templateid);
			JSONObject rjson = ThirdAPIUtils.callComponentAPI(thirdAppid, "https://api.weixin.qq.com/wxa/deletetemplate?access_token=", params);
			if(rjson.getInt("errcode") != 0){
				throw new WechatException("删除模板失败:"+rjson);
			}
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	
}
