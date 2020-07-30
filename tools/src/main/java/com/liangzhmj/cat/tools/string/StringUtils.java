package com.liangzhmj.cat.tools.string;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 对String的一些处理
 * @author liangzhmj
 *
 */
@Log4j2
public class StringUtils {


	/**
	 * 对字符串元素进行升序排序
	 * @param src
	 * @return
	 */
	public static String sortString(String src){
		if(isEmpty(src) || src.length() < 2){
			return src;
		}
		char[] chars = src.toCharArray();
		Arrays.sort(chars);
		StringBuilder dest = new StringBuilder();
		for (char sub : chars) {
			dest.append(sub);
		}
		return dest.toString();
	}

	/**
	 * 判断str是否为null或者空字符串
	 * @param str
	 * @return null或空串的话返回true，否则返回false
	 */
	public static boolean isEmpty(String str){
		if(str != null && !"".equals(str.trim())){
			return false;
		}
		return true;
	}
	
	/**
	 * 处理excel double类型数值
	 * @param d
	 * @param df
	 * @return
	 */
	public static String checkExcelDouble(Double d,DecimalFormat df){
		if(d == null || d.equals(0) || d == 0){
			return "/";
		}
		return df.format(d);
	}
	
	/**
	 * 字符串编码
	 * @param srcStr 待编码的字符串
	 * @param from 带编码字符串的编码集
	 * @param to 要转换成的编码集
	 * @return 转换后的字符串
	 */
	public static String encoding(String srcStr,String from,String to){
		
		try {
			srcStr =  new String(srcStr.getBytes(from),to);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return srcStr;
	}
	
	/**
	 * 把一个整形列表构造一个String
	 * 该String的形式：1,2,3,4
	 * @param idList
	 * @return
	 */
	public static String constructIds(Collection<Integer> idList){
		
		StringBuilder ids = new StringBuilder();
		for (Iterator<Integer> iterator = idList.iterator(); iterator.hasNext();) {
			Integer id = iterator.next();
			ids.append(id +",");
		}
		//如果有累加
		if(!StringUtils.isEmpty(ids.toString())){
			return ids.substring(0,ids.length()-1);
		}
		//如果没有累加返回null
		return null;
	}

	/**
	 * 把一个整形列表构造一个String
	 * 该String的形式：1,2,3,4
	 * @param blank 分隔符
	 * @return
	 */
	public static String constructIds(Integer[] idList,String blank){
		if(idList == null || idList.length == 0){
			return null;
		}
		StringBuilder ids = new StringBuilder();
		for (Integer id : idList) {
			ids.append(id +blank);
		}
		//如果有累加
		if(!StringUtils.isEmpty(ids.toString())){
			return ids.substring(0,ids.length()-blank.length());
		}
		//如果没有累加返回null
		return null;
	}

	public static String constructIds(List<String> ids,String prefix,String suffix,String blank){
		if(CollectionUtils.isEmpty(ids)){
			return null;
		}
		StringBuilder str = new StringBuilder();
		for (Object id : ids) {
			if(!StringUtils.isEmpty(prefix)){
				str.append(prefix);
			}
			str.append(id);
			if(!StringUtils.isEmpty(suffix)){
				str.append(suffix);
			}
			str.append(blank);
		}
		//如果有累加
		if(!StringUtils.isEmpty(str.toString())){
			return str.substring(0,str.length()-blank.length());
		}
		//如果没有累加返回null
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	public static String constructIds(List ids,String blank){
		if(CollectionUtils.isEmpty(ids)){
			return null;
		}
		StringBuilder str = new StringBuilder();
		for (Object id : ids) {
			str.append(id).append(blank);
		}
		//如果有累加
		if(!StringUtils.isEmpty(str.toString())){
			return str.substring(0,str.length()-blank.length());
		}
		//如果没有累加返回null
		return null;
	}
	
	/**
	 * 去掉content中以‘<’开头以‘>’结束的内容(去掉html标签)
	 * @param content
	 * @return
	 */
	public static String delHtmlTag(String content){
		if(StringUtils.isEmpty(content)){
			return null;
		}
        Pattern p_html;
        Matcher m_html;
        try {
             String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
             p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
             m_html = p_html.matcher(content);
             content = m_html.replaceAll(""); // 过滤html标签
        } catch (Exception e) {
             e.printStackTrace();
         }
        return content;// 返回文本字符串
	}
	
	/**
	 * 截取str的count个字符
	 * @param count
	 * @param str
	 * @return str为null 返回null，str的长度小于等于count 返回原str，str长度大于count 返回前count个字符
	 */
	public static String substring(int count,String str) {
		if(str == null || count < 1){
			return null;
		}
		if(str.length() <= count){
			return str;
		}
		return str.substring(0, count);
	}
	
	/**
	 * 后台通过struts的配置文件动态传参时把编码先转换成ISO-8859-1
	 * 因为配置文件会自动把ISO-8859-1的编码转换成utf8
	 * @param srcStr 要转换的字符串
	 * @return 已经转换的字符串
	 */
	public static String utfTOIso(String srcStr){
		
		try {
			srcStr =  new String(srcStr.getBytes("UTF-8"),"ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return srcStr;
	}
	
	public static String substringBefore(String srcStr,String subStr){
		if(srcStr == null || subStr == null 
		|| srcStr.length() < subStr.length()){
			return srcStr;
		}
		int index = srcStr.indexOf(subStr);
		if(index == -1){
			return srcStr;
		}
		return srcStr.substring(0, index);
	}
	
	public static String substringAfter(String srcStr,String subStr){
		if(srcStr == null || subStr == null 
			|| srcStr.length() < subStr.length()){
				return srcStr;
		}
		int index = srcStr.indexOf(subStr);
		if(index == -1){
			return srcStr;
		}
		return srcStr.substring(index+subStr.length());
	}
	
	/**
	 * 返回未来第i月的format格式的字符串
	 * @param i
	 * @param format
	 * @return
	 */
	public static String getFutureMonth(int i,String format){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH,i);
		if (format == null)
			format = "yyyyMM";
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String dateString = formatter.format(cal.getTime());
		return dateString;
	}
	
	/**
	 * 检查字符串是否为null或者为"null"<br>
	 * 为null或者为"null",返回""，否则返回字符串 <br>
	 * 用于处理数据库查询数据
	 */
	public static String getCleanString(Object obj){
		if( obj == null ){
			return "";
		}else if(String.valueOf(obj).equals("null")){
			return "";
		}else{
			return String.valueOf(obj).trim();
		}
	}

	/**
	 *字符串为null或不符合格式返回0
	 */
	public static int getCleanInteger(Object obj){
		if(obj == null){
			return 0;
		}
		int res = 0;
		try {
			res = Integer.parseInt(obj.toString());
		} catch (NumberFormatException e) {
			res = 0;
		}
		return res;
	}

	/**
	 *字符串为null或不符合格式返回0
	 */
	public static long getCleanLong(Object obj){
		if(obj == null){
			return 0;
		}
		long res = 0;
		try {
			res = Long.parseLong(obj.toString());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			res = 0;
		}
		return res;
	}

	/**
	 *字符串为null或不符合格式返回0
	 */
	public static double getCleanDouble(Object obj){
		if(obj == null){
			return 0;
		}
		double res = 0;
		try {
			res = Double.parseDouble(obj.toString());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			res = 0;
		}
		return res;
	}

	/**
	 * 去除字符串标点符号空格换行...部分
	 * @param str
	 * @return
	 */
	public static String pureText(String str){
		if(StringUtils.isEmpty(str)){
			return str;
		}
		//去除标点符号
		String pure = str.trim().replaceAll("[\\pP\\p{Punct}]","")
				.replaceAll(" ", "").replaceAll("\r", "")
				.replaceAll("\b", "").replaceAll("\n", "");
		return pure;
	}
}
