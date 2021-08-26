package com.oddrock.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class DateUtils {


	public static String getFormatTime1(Date date) {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
		return format1.format(date);
	}

	public static String getFormatTime1() {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
		return format1.format(new Date());
	}

	public static String getFormatTime(Date date) {
		if(date!=null) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return format.format(date);
		}else {
			return null;
		}
		
	}

	public static String getFormatTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date());
	}

	/**
	 * 返回当前时间字符串，格式为SimpleDateFormat
	 * 
	 * @return
	 */
	public static String timeStr() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}

	public static String timeStrWithMillis() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return sdf.format(new Date());
	}

	/**
	 * 返回当前时间的不带符号的字符串，格式为SimpleDateFormat
	 * 
	 * @return
	 */
	public static String timeStrWithoutPunctuation() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(new Date());
	}

	public static String timeStrInChinese() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
		return sdf.format(new Date());
	}

	/**
	 * 返回当前时间(含毫秒)的不带符号的字符串，格式为SimpleDateFormat
	 * 
	 * @return
	 */
	public static String timeStrWithMillisWithoutPunctuation() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return sdf.format(new Date());
	}

	/**
	 * 计算两个日期之间相差的天数
	 * 
	 * @param smallDate 较小的时间
	 * @param bigDate	较大的时间
	 * @return 			相差天数
	 * @throws 			ParseException
	 */
	public static Integer daysBetween(Date smallDate, Date bigDate) {
		if(smallDate!=null && bigDate!=null) {
			long time1 = smallDate.getTime();
			long time2 = bigDate.getTime();
			long between_days = (time2 - time1) / (1000 * 3600 * 24);
			return Integer.parseInt(String.valueOf(between_days));
		} else {
			return null;
		}
	}
	
	public static Integer secondsBetween(Date smallDate, Date bigDate) {
		if(smallDate!=null && bigDate!=null) {
			long time1 = smallDate.getTime();
			long time2 = bigDate.getTime();
			long between_days = (time2 - time1) / 1000;
			return Integer.parseInt(String.valueOf(between_days));
		}else {
			return null;
		}
		
	}
	
	public static Date yestoday() {
		return yestoday(new Date());
	}
	
	public static Date yestoday(Date date) {
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
        return calendar.getTime();
	}
	
	public static Date tommorrow(Date date) {
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
        return calendar.getTime();
	}
	
	public static Date tommorrow() {
		return tommorrow(new Date());
	}

	
	
	/**
	 * 获取多少天之前的日期
	 * @param d
	 * @param day
	 * @return
	 */
	public static  Date getPastDate(Date d, int day) {
		Calendar now = Calendar.getInstance();  
		now.setTime(d);  
		now.set(Calendar.DATE,now.get(Calendar.DATE)-day);  
		return now.getTime();  
	 }
	
	/**
	 * 获取多少天之后的日期
	 * @param d
	 * @param day
	 * @return
	 */
	public static  Date getFutureDate(Date d, int day) {
		Calendar now = Calendar.getInstance();  
		now.setTime(d);  
		now.set(Calendar.DATE,now.get(Calendar.DATE)+day);  
		return now.getTime();  
	 }
	
	public static Date millis2Date(Long millis) {
		Date date = new Date();
		date.setTime(millis);
		return date;
	}
	
	public static Date yearsAfter(int years, Date date) {
		return daysAfter(365*years, date);
	}

	public static Date daysAfter(int days, Date date) {
		if(date==null) {
			date = new Date();
		}
		Calendar canlendar = Calendar.getInstance(); 
		canlendar.setTime(date); 
		canlendar.add(Calendar.DATE, days); 
		Date result = canlendar.getTime();
		return result;
	}
	
	public static Integer getYear(Date date) {
		Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);	
	    return calendar.get(Calendar.YEAR);
	}
	
	public static Integer getMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);	
	    return calendar.get(Calendar.MONTH)+1;
	}
	
	/**
	 * 输入日期的本月的第一天
	 * @param date
	 * @return
	 */
	public static Date firstDayOfMonth(Date date) {
		if(date!=null) {
			int year = getYear(date);
			int month = getMonth(date);
			String dateStr = year+"-"+StringUtils.leftPad(String.valueOf(month), 2) + "-01";
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
			try {
				return format.parse(dateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 输入日期的本月的最后一天
	 * @param date
	 * @return
	 */
	public static Date lastDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}
	
	/**
	 * 输入日期的上个月的最后一题那
	 * @param date
	 * @return
	 */
	public static Date lastDayOflastMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		cal.add(Calendar.MONTH, -1);
		return cal.getTime();
	}
	
	public static int getWeekDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int weekday = c.get(Calendar.DAY_OF_WEEK);
		return weekday;
	}
	
	public static int getWeekDayNormally(Date date) {
		int weekday = getWeekDay(date);
		if(weekday>=2) {		// 周日返回1，周一返回2，依次类推。改为周一返回1，直到周日返回7.
			weekday = weekday - 1;
		}else {
			weekday = 7;
		}
		return weekday;
	}
	
	public static int getMonthDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int monthday = c.get(Calendar.DATE);
		return monthday;
	}
	
	public static void main(String[] args) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d1 = format.parse("2020-02-22 21:16:12");
		Date d2 = format.parse("2021-06-22 21:18:01");
		Date d3 = format.parse("2021-06-25 09:18:01");
		Date d4 = format.parse("2021-07-01 09:18:01");
		Date d5 = format.parse("2021-07-01 09:18:01");
		Date d6 = format.parse("2021-08-01 09:18:01");
		System.out.println(secondsBetween(d1, d2));
		System.out.println(daysBetween(d1, d3));
		System.out.println(lastDayOfMonth(d1));
		System.out.println(lastDayOflastMonth(d2));
		System.out.println(getWeekDayNormally(d4));
		System.out.println(getMonthDay(d4));
		System.out.println(getMonthDay(d3));
		System.out.println(lastDayOflastMonth(d5));
		System.out.println(getWeekDayNormally(d6));
		System.out.println(getMonthDay(d6));
	}

	

	
}