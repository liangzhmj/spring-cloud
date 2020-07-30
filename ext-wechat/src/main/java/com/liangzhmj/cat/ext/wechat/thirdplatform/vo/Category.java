package com.liangzhmj.cat.ext.wechat.thirdplatform.vo;

import com.liangzhmj.cat.tools.json.JSONBase;
import com.liangzhmj.cat.tools.json.JSONField;
import net.sf.json.JSONObject;

/**
 * 类目
 * @author liangzhmj
 *
 */
public class Category extends JSONBase {

	@JSONField(name = "first_class")
	private String firstClass;
	@JSONField(name = "second_class")
	private String secondClass;
	@JSONField(name = "third_class")
	private String thirdClass;
	@JSONField(name = "first_id",clazz = JSONBase.INTEGER)
	private Integer firstId;
	@JSONField(name = "second_id",clazz = JSONBase.INTEGER)
	private Integer secondId;
	@JSONField(name = "third_id",clazz = JSONBase.INTEGER)
	private Integer thirdId;
	
	public Category(){}

	public Category(JSONObject json){
		this.fromJSON(json);
	}
	
	public String getFirstClass() {
		return firstClass;
	}

	public void setFirstClass(String firstClass) {
		this.firstClass = firstClass;
	}



	public String getSecondClass() {
		return secondClass;
	}



	public void setSecondClass(String secondClass) {
		this.secondClass = secondClass;
	}



	public String getThirdClass() {
		return thirdClass;
	}



	public void setThirdClass(String thirdClass) {
		this.thirdClass = thirdClass;
	}



	public Integer getFirstId() {
		return firstId;
	}



	public void setFirstId(Integer firstId) {
		this.firstId = firstId;
	}



	public Integer getSecondId() {
		return secondId;
	}



	public void setSecondId(Integer secondId) {
		this.secondId = secondId;
	}



	public Integer getThirdId() {
		return thirdId;
	}



	public void setThirdId(Integer thirdId) {
		this.thirdId = thirdId;
	}

	public String getValue(){
		if(thirdId != null){//三级类目
			return  firstId + ":" + firstClass + "#" + secondId + ":" + secondClass + "#" + thirdId + ":" + thirdClass ;
		}
		if(secondId != null){
			return firstId + ":" + firstClass + "#" + secondId + ":" + secondClass;
		}
		return String.valueOf(firstId + ":" + firstClass);
	}

	public String getLabel(){
		if(thirdClass != null){//三级类目
			return  firstClass + "#" + secondClass + "#" + thirdClass;
		}
		if(secondClass != null){
			return firstClass + "#" + secondClass;
		}
		return String.valueOf(firstClass);
	}
	
}
