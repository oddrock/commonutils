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
}
