package com.liangzhmj.cat.tools.collection;

import com.liangzhmj.cat.tools.string.StringUtils;

import java.util.Collection;
import java.util.Map;

public class CollectionUtils {

	/**
	 * 判断一个数组是否为空
	 * @param objs
	 * @return 空：true 非空：false
	 */
	public static boolean isEmpty(Object[] objs){
		if(objs == null || objs.length < 1){
			return true;
		}
		return false;
	}
	/**判断一个集合是否为空
	 * @param collection
	 * @return
	 */
	public static boolean isEmpty(Collection<?> collection){
		if(collection == null || collection.isEmpty()){
			return true;
		}
		return false;
	}
	/**
	 * 判断一个map是否为空
	 * @param map
	 * @return
	 */
	public static boolean isEmpty(Map<?,?> map){
		if(map == null || map.isEmpty()){
			return true;
		}
		return false;
	}
	
	/**
	 * 检查collection中是否有eqaulIgnoreCase(key)的元素
	 * @param collection
	 * @param key
	 * @return
	 */
	public static boolean containIgnoreCase(Collection<String> collection,String key){
		if(CollectionUtils.isEmpty(collection)){
			return false;
		}
		if(StringUtils.isEmpty(key)){//如果key为空,返回自带的规则结果
			return collection.contains(key);
		}
		for (String elem : collection) {
			if(key.equalsIgnoreCase(elem)){//找到不分大小写equal的元素
				return true;
			}
		}
		return false;
	}
	
	public static String ArrayToString(Object[] objs){
		if(objs == null){
			return null;
		}
		StringBuilder res = new StringBuilder();
		for (Object obj : objs) {
			res.append(obj.toString()).append(",");
		}
		return res.toString();
	}
}
