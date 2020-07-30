package com.liangzhmj.cat.tools.fel;

import com.greenpineyu.fel.FelEngine;
import com.greenpineyu.fel.context.FelContext;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;

import java.util.Set;

/**
 * 数据包装工具类
 * @author liangzhmj
 *
 */
@Log4j2
public class DataWrapUtils {

	/** 不包装的数组模块结果用的key，因为serviceMoudle的doService方法返回的是一个JSONObject ，
	 * 如果客户端要求不要包装，直接返回一个JSONArray的话，这可以先用这个key包装，然后在dataWrap中去掉 
	 * 该返回格式的数据模块不可拼装（拼装的话，就不是一个不包装的JSONArray了）
	 * **/
	public static final String NO_WRAP_ARRAY_KEY = "NO_WARP";

	/**
	 * 数据格式转换包装
	 * @param src 源数据
	 * @param rule 转换规则
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Object dataWrap(JSONObject src,JSONObject rule){
		if(src == null || src.isNullObject() || rule == null || rule.isNullObject()){
			return src;//返回源数据
		}
		if(src.has(NO_WRAP_ARRAY_KEY)){
			//包含不包装的信息,直接返回不包装的值
			log.info("结果集包含不包装的信息,直接返回不包装的值");
			return src.get(NO_WRAP_ARRAY_KEY);
		}
		
		if(rule.has(NO_WRAP_ARRAY_KEY)){
			log.info("规则包含不包装的信息,直接返回不包装的固定值");
			return rule.get(NO_WRAP_ARRAY_KEY);
		}
		//遍历rule的key
		Set<String> keys = rule.keySet();
		if(CollectionUtils.isEmpty(keys)){return src;}
		

		FelEngine fel = FelEngineUtils.newFelEngine();//不用单例，线程不安全
		//设置表达式引擎上下文参数
		FelContext ctx = fel.getContext();
		ctx.set("data", src);
		
		JSONObject dest = new JSONObject();
		for (String key : keys) {
			String value = rule.getString(key);
			//为空
			if(StringUtils.isEmpty(value)){
				//赋值到目标对象
				dest.put(key, value);
				continue;
			}
			//value为表达式
			value = value.trim();
			//通过表达式计算获取值
			Object res = fel.eval(value);
			//赋值到目标对象
			dest.put(key, res);
		}
		return dest;
	}
}
