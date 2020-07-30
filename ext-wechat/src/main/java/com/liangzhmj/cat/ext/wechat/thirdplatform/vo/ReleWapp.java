package com.liangzhmj.cat.ext.wechat.thirdplatform.vo;

import com.liangzhmj.cat.tools.json.JSONBase;
import com.liangzhmj.cat.tools.json.JSONField;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sf.json.JSONObject;

/**
 * 关联小程序
 * @author liangzhmj
 *
 */
@NoArgsConstructor
@Setter
@Getter
public class ReleWapp extends JSONBase {

	@JSONField(clazz = JSONBase.INTEGER)
	private int status;
	@JSONField
	private String nickname;
	@JSONField
	private String username;
	@JSONField
	private String source;
	@JSONField(name = "headimg_url")
	private String logo;
	

	public ReleWapp(JSONObject json){
		this.fromJSON(json);
	}
	
	
}
