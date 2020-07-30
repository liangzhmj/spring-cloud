package com.liangzhmj.cat.ext.wechat.thirdplatform.vo;

import com.liangzhmj.cat.tools.json.JSONBase;
import com.liangzhmj.cat.tools.json.JSONField;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sf.json.JSONObject;

@NoArgsConstructor
@Setter
@Getter
public class PlugIn extends JSONBase {

	@JSONField(name = "appid")
	private String appId;
	@JSONField
	private String headimgurl;
	@JSONField
	private String nickname;
	//1：申请中，2：申请通过，3：被拒绝；4：已超时
	@JSONField
	private int status;
	private String thirdAppId;
	

	public PlugIn(String thirdAppId,JSONObject json){
		this.thirdAppId = thirdAppId;
		this.fromJSON(json);
	}
	

	
}
