package com.oddrock.common.elasticsearch;

public class ElasticSearchUtils {
	/**
	 * 生成搜索的URL
	 * @param esBasicUrl
	 * @param esIndex
	 * @return
	 */
	public static String generateSearchUrl(String esBasicUrl, String esIndex) {
		StringBuffer sb = new StringBuffer();
		sb.append(esBasicUrl).append(esIndex).append("/_search");
		return sb.toString();
	}
}
