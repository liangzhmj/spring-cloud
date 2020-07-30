package com.liangzhmj.cat.dao.mysql.utils;

public class DBUtils {

	public static String mysql_varchar_escape(String str){
        str = (str==null)?"":str;
        str = str.replaceAll("'","''");
		str = str.replaceAll("\\\\","\\\\\\\\");
		return str;  
	}
	public static String mysql_like_escape(String str){
		str = str.replaceAll("'","''");
		str = str.replaceAll("\\\\","\\\\\\\\");
		str = str.replaceAll("%","\\%");
		str = str.replaceAll("_","\\_");
		return str;
	}
}
