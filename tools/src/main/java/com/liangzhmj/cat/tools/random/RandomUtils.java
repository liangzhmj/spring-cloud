package com.liangzhmj.cat.tools.random;

import com.liangzhmj.cat.tools.date.DateUtils;

import java.util.Date;
import java.util.Random;
import java.util.UUID;


public class RandomUtils {
	
	/** 生成订单号，以时间加5位随机数 **/
	public static String createOrderId(){
		String dateStr = DateUtils.dateToString("yyyyMMddHHmmss", new Date());
		String randNum = RandomUtils.getRandomNum(4);
		String serverId = "5";
		return (dateStr+randNum+serverId);
	}

	/** 生成订单号，以时间加5位随机数 **/
	public static String createOrderId(String suffix){
		String dateStr = DateUtils.dateToString("yyyyMMddHHmmss", new Date());
		String randNum = RandomUtils.getRandomNum(4);
		return (dateStr+randNum+suffix);
	}

	/**
	 * 随机生成strLen为数字字符串
	 * @param strLen
	 * @return
	 */
	public static String getRandomNum(int strLen){
		Random random=new Random();
		random.setSeed(System.currentTimeMillis() + random.toString().hashCode());
		String ss="0123456789";
		String s="";
		for(int i=0;i<strLen; i++){
			int n=random.nextInt(ss.length());
			char r=ss.charAt(n);
			s=s+r;
		}
		return s;
	}
	
	public static String getUUID(){
		String uuid = UUID.randomUUID().toString(); //获取UUID并转化为String对象  
        uuid = uuid.replace("-", "");  
        return uuid;
	}
	
	/**
	 * 随机返回一个0-(n-1)的整数
	 * @param n
	 * @return
	 */
	public static int nextInt(int n){
		Random random=new Random();
		return random.nextInt(n);
	}
}
