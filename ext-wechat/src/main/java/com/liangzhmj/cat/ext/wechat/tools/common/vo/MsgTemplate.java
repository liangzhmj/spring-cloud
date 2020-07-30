package com.liangzhmj.cat.ext.wechat.tools.common.vo;

import com.liangzhmj.cat.tools.collection.CollectionUtils;
import com.liangzhmj.cat.tools.json.JSONBase;
import com.liangzhmj.cat.tools.json.JSONField;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * 消息模板
 * @author liangzhmj
 *
 */
@Setter
@Getter
@NoArgsConstructor
public class MsgTemplate extends JSONBase {

	//模板ID
	@JSONField(name = "template_id")
	private String id;
	//接收者openid
	@JSONField(name = "touser")
	private String openid;
	@JSONField
	private JSONObject data;

	//------------------------ 公众号专用 -------------------------start
	//模板跳转链接（海外帐号没有跳转能力）
	@JSONField
	private String url;
	//如果转跳小程序，这需要这个信息{"appid":"xiaochengxuappid12345","pagepath":"index?foo=bar"}
	@JSONField
	private JSONObject miniprogram;

	//------------------------ 公众号专用 -------------------------end

	//------------------------ 小程序专用 -------------------------start
	//表单提交场景下，为 submit 事件带上的 formId；支付场景下，为本次支付的 prepay_id，可以在用户和服务器交互的时候适当埋点获取formid
	@JSONField(name = "form_id")
	private String formid;
	//点击模板卡片后的跳转页面，仅限本小程序内的页面。支持带参数,（示例index?foo=bar）。该字段不填则模板无跳转。
	@JSONField
	private String page;
	//模板需要放大的关键词,例如:keyword1.DATA，不填则默认无放大
	@JSONField(name = "emphasis_keyword")
	private String emphasisKeyword;
	//------------------------ 小程序专用 -------------------------end



	public MsgTemplate(String id, String openid, String url, List<String[]> kws){
		this.id = id;
		this.openid = openid;
		this.url = url;
		this.data = this.initData(kws);
	}
	public MsgTemplate(String id, String openid, JSONObject miniprogram, List<String[]> kws){
		this.id = id;
		this.openid = openid;
		this.miniprogram = miniprogram;
		this.data = this.initData(kws);
	}


	public MsgTemplate(String id, String openid, String formid,String page,String ek , List<String> kws){
		this.id = id;
		this.openid = openid;
		this.formid = formid;
		this.page = page;
		this.emphasisKeyword = ek;
		this.data = this.initAppData(kws);
	}



	/**
	 * 初始化关键字信息
	 * @param kws 0:关键字（value必填），1:颜色（color选填）
	 * @return
	 */
	public JSONObject initData(List<String[]> kws){
		if(CollectionUtils.isEmpty(kws)){
			return null;
		}
		JSONObject data = new JSONObject();
		for (int i = 0; i < kws.size(); i++) {
			JSONObject kw = new JSONObject();
			String[] strs = kws.get(i);
			String value = StringUtils.getCleanString(strs[0]);
			kw.put("value", value);
			if(strs.length > 1){
				String color = StringUtils.getCleanString(strs[1]);
				kw.put("color", color);
			}
			if(i == 0){
				data.put("first", kw);
			}else{
				data.put("keyword"+i, kw);
			}
			
		}
		return data;
	}
	/**
	 * 初始化关键字信息
	 * @param kws 关键字（value必填）
	 * @return
	 */
	public JSONObject initAppData(List<String> kws){
		if(CollectionUtils.isEmpty(kws)){
			return null;
		}
		JSONObject data = new JSONObject();
		for (int i = 0; i < kws.size(); i++) {
			JSONObject kw = new JSONObject();
			String value = StringUtils.getCleanString(kws.get(i));
			kw.put("value", value);
			data.put("keyword"+i, kw);
		}
		return data;
	}


}
