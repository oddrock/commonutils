package com.oddrock.common;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	private static final SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss"); 
	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	
	public static String getFormatTime1(Date date){
		return format1.format(date);
	}
	
	public static String getFormatTime1(){
		return format1.format(new Date());
	}
	
	public static String getFormatTime(Date date){
		return format.format(date);
	}
	
	public static String getFormatTime(){
		return format.format(new Date());
	}
	
	/**
	 * 返回当前时间字符串，格式为SimpleDateFormat
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
	 * @return
	 */
	public static String timeStrWithoutPunctuation() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(new Date());
	}
	
	/**
	 * 返回当前时间(含毫秒)的不带符号的字符串，格式为SimpleDateFormat
	 * @return
	 */
	public static String timeStrWithMillisWithoutPunctuation() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return sdf.format(new Date());
	}

	public static void main(String[] args) {
		System.out.println(getFormatTime1());
	}

}