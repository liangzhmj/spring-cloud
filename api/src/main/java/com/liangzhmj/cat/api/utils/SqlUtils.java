package com.liangzhmj.cat.api.utils;

import com.liangzhmj.cat.dao.vo.Pager;
import com.liangzhmj.cat.tools.collection.CollectionUtils;
import com.liangzhmj.cat.tools.string.StringUtils;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * sql工具类
 * @author liangzhmj
 *
 */
public class SqlUtils {


	private static Integer PAGER_DEF_PAGE = 1;
	private static Integer PAGER_DEF_PAGESIZE = 30;
	private static Integer PAGER_DEF_MAXSIZE = 300;

	/**
	 * @param params {page:1,size:10}
	 * @param defaultPage
	 * @param defaultPageSize
	 * @param maxSize
	 * @return
	 */
	public static Pager getPager(JSONObject params, int defaultPage, int defaultPageSize, int maxSize){
		if(params == null){
			return Pager.newInstance(defaultPageSize, defaultPage);
		}
		int page = defaultPage;
		int size = defaultPageSize;
		try {//可能没有页数
			page = params.getInt("page");
		} catch (Exception e1) {
		}
		try {
			size = params.getInt("size");
			if(size > maxSize){
				size = maxSize;
			}
		} catch (Exception e) {
		}
		Pager pager = Pager.newInstance(size, page);
		return pager;
	}

	/**
	 * 获取页码的limit sql
	 * @param params
	 * @return
	 */
	public static String getPagerSql(JSONObject params){
		return getPagerSql(params, PAGER_DEF_PAGE, PAGER_DEF_PAGESIZE, PAGER_DEF_MAXSIZE);
	}
	/**
	 * 获取页码的limit sql
	 * @param params
	 * @param defaultPage
	 * @param defaultPageSize
	 * @param maxSize
	 * @return
	 */
	public static String getPagerSql(JSONObject params,int defaultPage,int defaultPageSize,int maxSize){
		Pager pager = getPager(params, defaultPage, defaultPageSize, maxSize);
		return " LIMIT "+pager.getOffset()+","+pager.getPageSize();
	}
	
	/**
	 * 返回集合sql条件
	 * @param conds
	 * @return
	 */
	public static String colCondSql(List<Integer> conds){
		if(CollectionUtils.isEmpty(conds)){
			return "";
		}
		if(conds.isEmpty()){
			return "="+conds.get(0);
		}
		return "IN("+ StringUtils.constructIds(conds)+")";
	}

	/**
	 * 返回结合sql条件
	 * @param conds
	 * @return
	 */
	public static String colCondSql(Integer[] conds){
		if(CollectionUtils.isEmpty(conds)){
			return "";
		}
		if(conds.length == 1){
			return "="+conds[1];
		}
		return "IN("+ StringUtils.constructIds(conds, ",")+")";
	}

	/**
	 * 返回集合sql条件
	 * @param conds
	 * @return
	 */
	public static String colCondStrSql(List<String> conds){
		if(CollectionUtils.isEmpty(conds)){
			return "";
		}
		if(conds.isEmpty()){
			return "='"+conds.get(0)+"'";
		}
		return "IN("+ StringUtils.constructIds(conds, "'", "'", ",")+")";
	}
}
