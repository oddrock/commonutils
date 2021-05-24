package com.oddrock.common.math;

import java.util.regex.Pattern;

public class MathUtils {
	private static final Pattern pattern = Pattern.compile("[0-9]*"); 
	
	/**
	 * 检查字符串是否是数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str){ 
	    return pattern.matcher(str).matches();    
	}
	
	public static Double round(Double input, int reservedDigit) {
		int x = 1;
		for(int i=0;i<reservedDigit;i++) {
			x = x * 10;
		}
		return (double) Math.round(input * x) / x;		
	}
	
	public static void main(String[] args) {
		System.out.println(round(3049.8757,2));
		System.out.println(round(2940.1916,2));
	}
}
