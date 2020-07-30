package com.liangzhmj.cat.ext.wechat.thirdplatform.vo;

import com.liangzhmj.cat.tools.date.DateUtils;
import com.liangzhmj.cat.tools.json.JSONBase;
import com.liangzhmj.cat.tools.json.JSONField;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sf.json.JSONObject;

import java.util.Date;

/**
 * 代码草稿
 * @author liangzhmj
 *
 */
@NoArgsConstructor
@Setter
@Getter
public class Draft extends JSONBase {

	@JSONField(name = "draft_id" , clazz=JSONBase.INTEGER)
	private int id;
	@JSONField(name = "user_version")
	private String version;
	@JSONField(name = "user_desc")
	private String desc;
	@JSONField(name = "create_time", clazz=JSONBase.LONG)
	private long timestamp;
	private String thirdAppId;
	

	public Draft(JSONObject json){
		this.fromJSON(json);
	}


	public String getTime(){
		Date date = new Date(timestamp*1000);
		return DateUtils.dateToString("yyyy-MM-dd", date);
	}
}
