package com.oddrock.common;

public class ExceptionUtils {
	public static String getStackMsg(Exception e) {   
		StringBuffer sb = new StringBuffer();  
		StackTraceElement[] stackArray = e.getStackTrace();  
		for (int i = 0; i < stackArray.length; i++) {  
			StackTraceElement element = stackArray[i];  
			sb.append(element.toString() + "\n");  
		}  
		return sb.toString();  
	} 
}
