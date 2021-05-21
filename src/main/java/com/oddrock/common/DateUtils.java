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
	public static int daysBetween(Date smallDate, Date bigDate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		smallDate = sdf.parse(sdf.format(smallDate));
		bigDate = sdf.parse(sdf.format(bigDate));
		Calendar cal = Calendar.getInstance();
		cal.setTime(smallDate);
		long time1 = cal.getTimeInMillis();
		cal.setTime(bigDate);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);
		return Integer.parseInt(String.valueOf(between_days));
	}
	
	public static Date yestoday() {
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
        return calendar.getTime();
	}

	public static void main(String[] args) {
		System.out.println(yestoday());
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

}