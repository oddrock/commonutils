package com.oddrock.common.windows;

import org.apache.commons.lang.StringUtils;

public class SensitiveStringUtils {
	public static String replaceSensitiveString(String srcStr){
		if(StringUtils.isBlank(srcStr)) return srcStr;
		srcStr = srcStr.replace("\\", " ");
		srcStr = srcStr.replace("/", " ");
		srcStr = srcStr.replace(":", "：");
		srcStr = srcStr.replace("*", " ");
		srcStr = srcStr.replace("?", " ");
		srcStr = srcStr.replace("\"", " ");
		srcStr = srcStr.replace("<", " ");
		srcStr = srcStr.replace(">", " ");
		srcStr = srcStr.replace("|", " ");
		srcStr = srcStr.replace(";", "；");
		return srcStr;
	}
}
