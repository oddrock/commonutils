package com.oddrock.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
	private static final SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static String getFormatTime1(Date date) {
		return format1.format(date);
	}

	public static String getFormatTime1() {
		return format1.format(new Date());
	}

	public static String getFormatTime(Date date) {
		return format.format(date);
	}

	public static String getFormatTime() {
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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			smallDate = sdf.parse(sdf.format(smallDate));
			bigDate = sdf.parse(sdf.format(bigDate));
			Calendar cal = Calendar.getInstance();
			cal.setTime(smallDate);
			long time1 = cal.getTimeInMillis();
			cal.setTime(bigDate);
			long time2 = cal.getTimeInMillis();
			long between_days = (time2 - time1) / (1000 * 3600 * 24);
			return Integer.parseInt(String.valueOf(between_days));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
	
	public static void main(String[] args) {
		System.out.println(yearsAfter(-10, null));
	}
}