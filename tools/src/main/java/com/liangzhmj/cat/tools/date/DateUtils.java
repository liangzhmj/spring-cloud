package com.liangzhmj.cat.tools.date;

import com.liangzhmj.cat.tools.exception.ToolsException;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



/**
 * 对日期的一些处理
 * @author liangzhmj
 *
 */
/**
 * @author Administrator
 *
 */
@Log4j2
public class DateUtils {
	

	/**
	 * 判断lastTime是否是n分钟之前的时间
	 * @param lastTime
	 * @param minute
	 * @return 如果是n分钟之前的返回true 否则返回false
	 */
	public static boolean isTimeOut(Date lastTime,int minute) {
		return isTimeOut(lastTime,Calendar.MINUTE,minute);
	}
	/**
	 * 判断lastTime是否是n(单位时间)之前的时间
	 * @param n
	 * @param unit 例如Calendar.MINUTE之类
	 * @param lastTime
	 * @return 如果是n(单位时间)之前的返回true 否则返回false
	 */
	public static boolean isTimeOut(Date lastTime,int unit,int n) {
		Calendar now = Calendar.getInstance();
		Calendar last = Calendar.getInstance();
		last.setTime(lastTime);
		last.add(unit, n);
		if (last.before(now))
			return true;
		return false;
	}

	/**
	 * 获取当天最开始的时间
	 * @param time
	 * @return
	 */
	public static Date getCurDayFirst(Date time){
		Calendar c = Calendar.getInstance();
		c.setTime(time);
		c.set(Calendar.HOUR_OF_DAY, c.getMinimum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMinimum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMinimum(Calendar.SECOND));
		return c.getTime();
	}
	/**
	 * 获取当天最开始的时间
	 * @return
	 */
	public static Date getCurDayFirst(){
		return getCurDayFirst(new Date());
	}
	/**
	 * 获取当天最开始的时间
	 * @param time
	 * @param format
	 * @return
	 */
	public static String getCurDayFirst(Date time,String format){
		return dateToString(format,getCurDayFirst(time));
	}

	/**
	 * time为type（年月日时分秒）添加amount个数值
	 * @param time
	 * @param type
	 * @param amount
	 * @return
	 */
	public static Date addTime(Date time,int type,int amount){
		if(time == null)time = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(time);
		c.add(type, amount);
		return c.getTime();
	}

	/**
	 * 获取count(时间单位)之后的时间
	 * @param type 时间类型如果Calendar.YEAR（年月日时分秒）
	 * @param count
	 * @param format
	 * @return
	 */
	public static String getFutureTime(int type,int count,String format){
		Date time = addTime(new Date(),type,count);
		format = format == null ? "yyyy-MM-dd HH:mm:ss" : format;
		return DateUtils.dateToString(format, time);
	}

	/**
	 * 获取count天之后的时间
	 * @param count
	 * @param format
	 * @return
	 */
	public static String getFutureDay(int count,String format){
		return getFutureTime(Calendar.DAY_OF_YEAR,count,format);
	}
	/**
	 * 获取count月之后的时间
	 * @param count
	 * @param format
	 * @return
	 */
	public static String getFutureMonth(int count,String format){
		return getFutureTime(Calendar.MONTH,count,format);
	}
	/**
	 * 获取count年之后的时间
	 * @param count
	 * @param format
	 * @return
	 */
	public static String getFutureYear(int count,String format){
		return getFutureTime(Calendar.YEAR,count,format);
	}

	/**
	 * 获取当天最后的时间
	 * @param time
	 * @return
	 */
	public static Date getCurDayLast(Date time){
		Calendar c = Calendar.getInstance();
		c.setTime(time);
		c.set(Calendar.HOUR_OF_DAY, c.getMaximum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMaximum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMaximum(Calendar.SECOND));
		return c.getTime();
	}
	/**
	 * 获取当天最后的时间
	 * @return
	 */
	public static Date getCurDayLast(){
		return getCurDayLast(new Date());
	}
	/**
	 * 获取当天最后的时间
	 * @param time
	 * @param format
	 * @return
	 */
	public static String getCurDayLast(Date time,String format){
		return dateToString(format,getCurDayLast(time));
	}
	/**
	 * 获取当月最开始的时间
	 * @param time
	 * @return
	 */
	public static Date getCurMonFirst(Date time){
		Calendar c = Calendar.getInstance();
		c.setTime(time);
		c.set(Calendar.DAY_OF_MONTH, c.getMinimum(Calendar.DAY_OF_MONTH));
		c.set(Calendar.HOUR_OF_DAY, c.getMinimum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMinimum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMinimum(Calendar.SECOND));
		return c.getTime();
	}

	/**
	 * 获取当月最开始的时间
	 * @param time
	 * @param format
	 * @return
	 */
	public static String getCurMonFirst(Date time,String format){
		return dateToString(format,getCurMonFirst(time));
	}
	/**
	 * 获取当月最后的时间
	 * @param time
	 * @return
	 */
	public static Date getCurMonLast(Date time){
		Calendar c = Calendar.getInstance();
		c.setTime(time);
		c.set(Calendar.DAY_OF_MONTH, c.getMaximum(Calendar.DAY_OF_MONTH));
		c.set(Calendar.HOUR_OF_DAY, c.getMaximum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMaximum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMaximum(Calendar.SECOND));
		return c.getTime();
	}

	/**
	 * 获取当月最后的时间
	 * @param time
	 * @param format
	 * @return
	 */
	public static String getCurMonLast(Date time,String format){
		return dateToString(format,getCurMonLast(time));
	}

	/**
	 * 获取当周最开始的时间
	 * @param time
	 * @return
	 */
	public static Date getCurWeekFirst(Date time){
		Calendar c = Calendar.getInstance();
		c.setTime(time);
		c.set(Calendar.DAY_OF_WEEK, c.getMinimum(Calendar.DAY_OF_WEEK)+1);//默认是周日是第一天，这里设置周一是第一天
		c.set(Calendar.HOUR_OF_DAY, c.getMinimum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMinimum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMinimum(Calendar.SECOND));
		return c.getTime();
	}

	/**
	 * 获取当周最开始的时间
	 * @param time
	 * @param format
	 * @return
	 */
	public static String getCurWeekFirst(Date time,String format){
		return dateToString(format,getCurWeekFirst(time));
	}
	/**
	 * 获取当周最后的时间
	 * @param time
	 * @return
	 */
	public static Date getCurWeekLast(Date time){
		Calendar c = Calendar.getInstance();
		c.setTime(time);
		//默认是周日是第一天，这里设置周一是第一天，也就是说周日是下一周的第一天
		c.add(Calendar.WEEK_OF_YEAR,1);//加一周
		c.set(Calendar.DAY_OF_WEEK, c.getMinimum(Calendar.DAY_OF_WEEK));//下周的第一天也就是星期天
		c.set(Calendar.HOUR_OF_DAY, c.getMaximum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMaximum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMaximum(Calendar.SECOND));
		return c.getTime();
	}

	/**
	 * 获取当周最后的时间
	 * @param time
	 * @param format
	 * @return
	 */
	public static String getCurWeekLast(Date time,String format){
		return dateToString(format,getCurWeekLast(time));
	}

	/**
	 * 获取当周的时间范围
	 * @param time
	 * @param format
	 * @return
	 */
	public static String getCurWeekRange(Date time,String format){
		Calendar c = Calendar.getInstance();
		c.setTime(time);
		c.set(Calendar.DAY_OF_WEEK, c.getMinimum(Calendar.DAY_OF_WEEK)+1);//默认是周日是第一天，这里设置周一是第一天
		c.set(Calendar.HOUR_OF_DAY, c.getMinimum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMinimum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMinimum(Calendar.SECOND));
		StringBuilder res = new StringBuilder();
		res.append(dateToString(format,c.getTime())).append(" - ");
		c.setTime(time);
		//默认是周日是第一天，这里设置周一是第一天，也就是说周日是下一周的第一天
		c.add(Calendar.WEEK_OF_YEAR,1);//加一周
		c.set(Calendar.DAY_OF_WEEK, c.getMinimum(Calendar.DAY_OF_WEEK));//下周的第一天也就是星期天
		c.set(Calendar.HOUR_OF_DAY, c.getMaximum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMaximum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMaximum(Calendar.SECOND));
		res.append(dateToString(format,c.getTime()));
		return res.toString();
	}

	/**
	 * 获取当前时间字符串
	 * @param format
	 * @return
	 */
	public static String getCurrentStr(String format){
		String dateStr = null;
		dateStr = new SimpleDateFormat(format).format(new Date());
		return dateStr;
	}

	/**
	 * 判断time是否是今天之内的时间
	 * @param time
	 * @return
	 */
	public static boolean isToday(Date time) {
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		Calendar last = Calendar.getInstance();
		last.setTime(time);
		//start为今天的开始,把时分秒置零
		start.set(Calendar.HOUR_OF_DAY, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		//end为第二天的开始，把时分秒置零
		end.set(Calendar.HOUR_OF_DAY, 0);
		end.set(Calendar.MINUTE, 0);
		end.set(Calendar.SECOND, 0);
		//天数增加1
		end.add(Calendar.DAY_OF_MONTH, 1);
		if(last.after(start) && last.before(end)){
			return true;
		}
		return false;
	}
	/**
	 * 把date按format格式转化为字符串
	 * @param format
	 * @param date
	 * @return
	 */
	public static String dateToString(String format,Date date){
		String dateStr = null;
		dateStr = new SimpleDateFormat(format).format(date);
		return dateStr;
	}


	/**
	 * 把dateStr按照format格式转化为日期格式
	 * @param format
	 * @param dateStr
	 * @return
	 */
	public static Date stringToDate(String format,String dateStr){
		Date date = null;
		try {
			date = new SimpleDateFormat(format).parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}






	/**
	 * 把srcfmt格式的日期字符串转化成destfmt格式的日期字符串
	 * @param src
	 * @param srcfmt
	 * @param desfmt
	 * @return
	 */
	public static String dateStr2DateStr(String src,String srcfmt,String desfmt){
		if(StringUtils.isEmpty(src)){
			return src;
		}
		Date date = stringToDate(srcfmt, src);
		String dest = dateToString(desfmt, date);
		return dest;
	}
	
	/**
	 * 获取昨天的日期字符串 
	 * @param format 返回的格式
	 * @return
	 */
	public static String getYesterday(String format){
		return getFutureDay(-1,format);
	}

	/**
	 * 获取明天的日期字符串
	 * @param format
	 * @return
	 */
	public static String getTomorrow(String format){
		return getFutureDay(1,format);
	}

	/**
	 * 检查时间段的合法性
	 * @param begin
	 * @param end
	 * @param type 0：默认取当前，1：默认取当月
	 * @param srcFormat
	 * @param destFormat
	 * @param limit 最大的相差时间，0表示不检测，type=0天，type=1月
	 * @return 0:开始时间，1:结束时间
	 * @throws Exception
	 */
	public static String[] checkTime(String begin,String end,int type,String srcFormat,String destFormat,int limit)throws Exception{
		Calendar b = Calendar.getInstance();
		Calendar e = Calendar.getInstance();
		String beginTime = null;
		String endTime = null;
		Date curtime = new Date();
		try {
			Date bt = null; 
			Date et = null;
			//如果beign为空
			if(StringUtils.isEmpty(begin)){
				if(StringUtils.isEmpty(end)){
					//如果end也为空
					//默认取当天
					if(type == 0){
						bt = DateUtils.getCurDayFirst(curtime);
					}
					//默认取当月
					if(type == 1){
						bt = DateUtils.getCurMonFirst(curtime);
					}
					et = DateUtils.getCurDayLast(curtime);
				}else{
					
					et = DateUtils.stringToDate(srcFormat, end);
					//取那天最末时间
					et = DateUtils.getCurDayLast(et);
					//对应天的开始时间
					if(type == 0){
						bt = DateUtils.getCurDayFirst(et);
					}
					//对应月的开始时间
					if(type == 1){
						bt = DateUtils.getCurMonFirst(et);
					}
				}
				return new String[]{DateUtils.dateToString(destFormat, bt),DateUtils.dateToString(destFormat, et)};
			}
			//开始时间不为空
			bt = DateUtils.stringToDate(srcFormat, begin);
			bt = DateUtils.getCurDayFirst(bt);
			//end为null
			if(StringUtils.isEmpty(end)){
				if(type == 0){
					et = DateUtils.getCurDayLast(bt);
				}
				if(type == 1){
					et = DateUtils.getCurMonLast(bt);
				}
				return new String[]{DateUtils.dateToString(destFormat, bt),DateUtils.dateToString(destFormat, et)};
			}
			
			//都不为空	
			et = DateUtils.stringToDate(srcFormat, end);
			et = DateUtils.getCurDayLast(et);
			beginTime = DateUtils.dateToString(destFormat, bt);
			endTime = DateUtils.dateToString(destFormat, et);
			b.setTime(bt);
			e.setTime(et);
			if(StringUtils.isEmpty(beginTime) || StringUtils.isEmpty(endTime)){
				throw new ToolsException("日期处理失败");
			}
		} catch (Exception e1) {
			log.error("时间或参数输入不合法:"+e1.getMessage());
			if(type == 0){
				beginTime = DateUtils.dateToString(destFormat,DateUtils.getCurDayFirst(curtime));
				endTime = DateUtils.dateToString(destFormat,DateUtils.getCurDayFirst(curtime));
			}else{
				beginTime = DateUtils.dateToString(destFormat,DateUtils.getCurMonFirst(curtime));
				endTime = DateUtils.dateToString(destFormat,DateUtils.getCurMonFirst(curtime));
			}
			return new String[]{beginTime,endTime};
		}
		if(b.after(e)){
			throw new ToolsException("时间输入不合法，开始时间不能比结束时间大");
		}
		if(limit > 0){
			if(type == 0){
				b.add(Calendar.DAY_OF_MONTH, limit);
			}
			if(type == 1){
				b.add(Calendar.MONTH, limit);
			}
			if(b.before(e)){
				throw new ToolsException("选定的时间间隔太长");
			}
		}
		return new String[]{beginTime,endTime};
	}
	
	/**
	 * 比较两个日期
	 * @param d1
	 * @param d2
	 * @return d1>d2 返回true，否则返回false
	 */
	public static boolean compareDate(Date d1,Date d2){
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(d1);
		c2.setTime(d2);
		if(c1.after(c2)){
			return true;
		}
		return false;
	}
}
