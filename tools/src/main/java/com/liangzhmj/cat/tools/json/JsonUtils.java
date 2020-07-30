package com.liangzhmj.cat.tools.json;

import com.liangzhmj.cat.tools.string.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

import java.util.Collection;

/**
 * json工具类
 * @author liangzhmj
 *
 */
public class JsonUtils {

	private static JsonConfig jsonConfig;
	
	static{
		jsonConfig = initJonsConfig();
	}
	
	/**
	 * 初始化jsonConfig
	 * @return
	 */
	private static JsonConfig initJonsConfig(){
		jsonConfig = new JsonConfig();
	    jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
	            public boolean apply(Object object, String fieldName, Object fieldValue) {
	            	return null == fieldValue;
	            }
	    	}
        );
	    return jsonConfig;
	}
	/**
	 * 获取jsonConfig
	 * @return
	 */
	public static JsonConfig getJonConfig(){
		if(jsonConfig == null){
			jsonConfig = initJonsConfig();
		}
		return jsonConfig;
	}
	
	/**
	 * 把obj转成json字符串，忽略空属性
	 * @param obj
	 * @return
	 */
	public static String getJsonWithoutEmpty(Object obj){
		JSONObject json = JSONObject.fromObject(obj, JsonUtils.getJonConfig());
		if(json == null){
			return null;
		}
		return json.toString();
	}
	
	/**
	 * 给json设置属性
	 * @param json
	 * @param key
	 * @param value
	 * @return
	 */
	public static JSONObject setValue(JSONObject json,String key,Object value){
		if(StringUtils.isEmpty(key) || value == null){
			return json;
		}
		//如果是数组
		if((value instanceof Object[]) || (value instanceof Collection)){
			JSONArray ja = JsonUtils.setJSONArray(value);
			json.put(key, ja);
			return json;
		}
		//不是数组
		Object temp = JsonUtils.changeValue(value);
		if(temp != null){
			json.put(key, temp);
		}
		return json;
	}
	
	/**
	 * 数组转化为json格式
	 * @param value
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static JSONArray setJSONArray(Object value){
		JSONArray ja = new JSONArray();
		if(value == null){
			return ja;
		}
		if(value instanceof Object[]){
			Object[] objs = (Object[])value;
			for (Object obj : objs) {
				Object temp = JsonUtils.changeValue(obj);
				if(temp != null){
					ja.add(temp);
				}
			}
			return ja;
		}
		if(value instanceof Collection){
			Collection objs = (Collection)value;
			for (Object obj : objs) {
				Object temp = JsonUtils.changeValue(obj);
				if(temp != null){
					ja.add(temp);
				}
			}
			return ja;
		}
		return ja;
	}
	
	/**
	 * 转化成json对象
	 * @param value
	 * @return
	 */
	public static Object changeValue(Object value){
		if(value == null){
			return null;
		}
		if((value instanceof Object[]) || (value instanceof Collection)){
			JSONArray ja = JsonUtils.setJSONArray(value);
			return ja;
		}
		//继承jsonbase
		if(value instanceof JSONBase){
			JSONBase jvalue = (JSONBase)value;
			return jvalue.toJSON();
		}
		//没有继承jsonbase，直接返回原样
		return value;
	}
}
