package com.oddrock.common.cache;

public class CacheUtils {
	/**
	 * 创建cache的键
	 * @param params
	 * @return
	 */
	public static String createCacheKey(Object... params) {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for(Object o : params) {
			if(first) {
				sb.append(o.toString());
				first = false;
			}else {
				sb.append("|").append(o.toString());
			}
		}
		return sb.toString();
	}
}
