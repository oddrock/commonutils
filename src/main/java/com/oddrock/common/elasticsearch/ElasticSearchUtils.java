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
	
	/**
	 * 生成搜索的URL
	 * @param esBasicUrl
	 * @param esIndex
	 * @return
	 */
	public static String generateSearchUrl(String esBasicUrl, String esIndex, String esType) {
		StringBuffer sb = new StringBuffer();
		sb.append(esBasicUrl).append("/").append(esIndex).append("/").append(esType).append("/_search");
		return sb.toString();
	}
	
	/**
	 * 生成删除Index的URL
	 * @param esBasicUrl
	 * @param esIndex
	 * @return
	 */
	public static String generateDeleteUrl(String esBasicUrl, String esIndex) {
		StringBuffer sb = new StringBuffer();
		sb.append(esBasicUrl).append("/").append(esIndex);
		return sb.toString();
	}
	
	/**
	 * 生成创建Index的URL
	 * @param esBasicUrl
	 * @param esIndex
	 * @return
	 */
	public static String generateCreateUrl(String esBasicUrl, String esIndex) {
		StringBuffer sb = new StringBuffer();
		sb.append(esBasicUrl).append("/").append(esIndex);
		return sb.toString();
	}
	
	/**
	 * 生成插入数据的URL
	 * @param esBasicUrl
	 * @param esIndex
	 * @param esType
	 * @return
	 */
	public static String generateInsertUrl(String esBasicUrl, String esIndex, String esType) {
		StringBuffer sb = new StringBuffer();
		sb.append(esBasicUrl).append("/").append(esIndex).append("/").append(esType);
		return sb.toString();
	}
}
